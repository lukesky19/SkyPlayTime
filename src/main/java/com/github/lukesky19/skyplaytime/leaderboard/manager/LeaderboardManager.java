/*
    SkyPlayTime tracks play time with options to not track play time for inactive (AFK) players.
    Copyright (C) 2025 lukeskywlker19

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.github.lukesky19.skyplaytime.leaderboard.manager;

import com.github.lukesky19.skylib.api.time.TimeUtil;
import com.github.lukesky19.skyplaytime.leaderboard.data.LeaderboardSnapshot;
import com.github.lukesky19.skyplaytime.leaderboard.data.Position;
import com.github.lukesky19.skyplaytime.leaderboard.data.TopTen;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import com.github.lukesky19.skyplaytime.database.DatabaseManager;
import com.github.lukesky19.skyplaytime.database.table.PlayTimeTable;
import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import com.github.lukesky19.skyplaytime.util.TimeCategory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * This class manages obtaining data to display leaderboards and marking whether players are excluded from the leaderboard or not.
 */
public class LeaderboardManager {
    private final @NotNull LeaderboardSnapshotManager leaderboardSnapshotManager;
    private final @NotNull PlayerDataManager playerDataManager;
    private final @NotNull DatabaseManager databaseManager;
    // Cached top ten from the database.
    private final @NotNull Map<TimeCategory, TopTen> databaseTopTen = new HashMap<>();
    private final @NotNull Map<TimeCategory, TopTen> calculatedTopTen = new HashMap<>();

    /**
     * Constructor
     * @param leaderboardSnapshotManager A {@link LeaderboardSnapshotManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param databaseManager A {@link DatabaseManager} instance.
     */
    public LeaderboardManager(
            @NotNull LeaderboardSnapshotManager leaderboardSnapshotManager,
            @NotNull PlayerDataManager playerDataManager,
            @NotNull DatabaseManager databaseManager) {
        this.leaderboardSnapshotManager = leaderboardSnapshotManager;
        this.playerDataManager = playerDataManager;
        this.databaseManager = databaseManager;
    }

    /**
     * Mark the player as exempt from leaderboard reporting.
     * @param uuid The {@link UUID} of the player.
     */
    public void markPlayerExempt(@NotNull UUID uuid) {
        @Nullable PlayerData playerData = playerDataManager.getPlayerData(uuid);
        if(playerData == null) {
            throw new RuntimeException("No player data found for UUID " + uuid);
        }

        playerData.setExempt(true);
    }

    /**
     * Mark the player as not exempt from leaderboard reporting.
     * @param uuid The {@link UUID} of the player.
     */
    public void markPlayerNotExempt(@NotNull UUID uuid) {
        @Nullable PlayerData playerData = playerDataManager.getPlayerData(uuid);
        if(playerData == null) {
            throw new RuntimeException("No player data found for UUID " + uuid);
        }

        playerData.setExempt(false);
    }

    /**
     * Update the cached top ten from the database.
     * @return A {@link CompletableFuture} of type {@link Void} when complete.
     */
    public @NotNull CompletableFuture<Void> updateDatabaseTopTen() {
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        PlayTimeTable playTimeTable = databaseManager.getPlayTimeTable();

        for(TimeCategory timeCategory : TimeCategory.values()) {
            futureList.add(playTimeTable.getTopTenByCategoryNotExempt(timeCategory).thenAccept(topTen -> databaseTopTen.put(timeCategory, topTen)));
        }

        return CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
    }

    /**
     * Update the calculated top ten for all time categories except ALL.
     */
    public void updateTopTenAllCategories() {
        for(TimeCategory timeCategory : TimeCategory.values()) {
            @Nullable TopTen databaseTopTen = this.databaseTopTen.get(timeCategory);
            @NotNull TopTen resultTopTen = new TopTen();
            if(databaseTopTen == null) return;
            // Get a list of all non-null database positions.
            @NotNull List<@NotNull Position> databasePositions = databaseTopTen.getPositions();

            // Get loaded player data
            @NotNull Map<UUID, PlayerData> onlinePlayerDataMap = playerDataManager.getPlayerDataMap();
            // Calculate the top ten positions from the online player data.
            @NotNull List<Position> onlineTopTenPositions = onlinePlayerDataMap.entrySet().stream()
                    .filter(entry -> !entry.getValue().isExempt())
                    .map(entry -> {
                        PlayerData playerData = entry.getValue();
                        long playTime = playerData.getPlayTime(timeCategory);
                        return new Position(entry.getKey(), playerData.getName(), playTime);
                    })
                    .sorted(Comparator.comparingLong(Position::seconds).reversed())
                    .limit(10)
                    .toList();

            // Combine database and online positions
            List<Position> combinedPositions = new ArrayList<>();
            Set<UUID> seenPlayers = new HashSet<>();
            // Add online positions if their position wasn't added already
            onlineTopTenPositions.forEach(position -> {
                if(seenPlayers.add(position.uuid())) {
                    combinedPositions.add(position);
                }
            });
            // Add database positions if their position wasn't added already
            databasePositions.forEach(position -> {
                if(seenPlayers.add(position.uuid())) {
                    combinedPositions.add(position);
                }
            });

            // Sort list to get final positions
            List<Position> finalPositions = combinedPositions.stream()
                    .sorted(Comparator.comparingLong(Position::seconds).reversed())
                    .limit(10)
                    .toList();

            // Set the positions in the resulting top ten
            resultTopTen.setPositions(finalPositions);

            calculatedTopTen.put(timeCategory, resultTopTen);
        }
    }

    /**
     * Get the {@link TopTen} for the {@link TimeCategory}.
     * @param timeCategory The {@link TimeCategory} to get the top play time player data for.
     * @return The {@link TopTen} for the {@link TimeCategory}. May be null. {@link TimeCategory#ALL} will always return null.
     */
    public @Nullable TopTen getTopTenByTimeCategoryNotExempt(@NotNull TimeCategory timeCategory) {
        return calculatedTopTen.get(timeCategory);
    }

    /**
     * From the {@link TopTen} for the {@link TimeCategory} provided, get the {@link Position} at the positon N.
     * NOTE: Anything less than or equal to 0 or greater than 10 will always return null.
     * @param timeCategory The {@link TimeCategory} to get the {@link Position} for.
     * @param positionNumber The position number to get.
     * @return A {@link Position}. May be null.
     */
    public @Nullable Position getPositionForCategoryAtPositionNumber(@NotNull TimeCategory timeCategory, int positionNumber) {
        TopTen topTen = getTopTenByTimeCategoryNotExempt(timeCategory);
        if(topTen == null) {
            return null;
        }

        return topTen.getPosition(positionNumber);
    }

    /**
     * Save an archive the current top 10 players to a file for the provided {@link TimeCategory}.
     * @param timeCategory A {@link TimeCategory} to save the current top 10 leaderboard for.
     * @return A true if successful, or false if not.
     */
    public boolean saveLeaderboardSnapshot(@NotNull TimeCategory timeCategory) {
        TopTen topTen = getTopTenByTimeCategoryNotExempt(timeCategory);
        if(topTen == null) return false;

        String fileName = "leaderboard_" + timeCategory.toString().toLowerCase() + "_" + TimeUtil.millisToTimeStamp(System.currentTimeMillis(), ZoneId.of("America/New_York"), "MM-dd-yyyy_HH-mm-ss");

        return leaderboardSnapshotManager.saveHistoricalLeaderboard(fileName, new LeaderboardSnapshot("1.0.0.0", timeCategory, topTen.getPositions()));
    }

    /**
     * Save a leaderboard snapshot for the play time categories using the boolean options.
     * @param session Should a snapshot of the session play time leaderboard be saved?
     * @param daily Should a snapshot of the daily play time leaderboard be saved?
     * @param weekly Should a snapshot of the weekly play time leaderboard be saved?
     * @param monthly Should a snapshot of the monthly play time leaderboard be saved?
     * @param yearly Should a snapshot of the yearly play time leaderboard be saved?
     * @param total Should a snapshot of the total play time leaderboard be saved?
     * @return true if all were successful, otherwise false.
     */
    public boolean saveLeaderboardSnapshots(
            boolean session,
            boolean daily,
            boolean weekly,
            boolean monthly,
            boolean yearly,
            boolean total) {
        List<Boolean> results = new ArrayList<>(6);

        if(session) results.add(saveLeaderboardSnapshot(TimeCategory.SESSION));
        if(daily) results.add(saveLeaderboardSnapshot(TimeCategory.DAILY));
        if(weekly) results.add(saveLeaderboardSnapshot(TimeCategory.WEEKLY));
        if(monthly) results.add(saveLeaderboardSnapshot(TimeCategory.MONTHLY));
        if(yearly) results.add(saveLeaderboardSnapshot(TimeCategory.YEARLY));
        if(total) results.add(saveLeaderboardSnapshot(TimeCategory.TOTAL));

        return results.stream().allMatch(Boolean::booleanValue);
    }
}
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
package com.github.lukesky19.skyplaytime.database.table;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.api.database.parameter.Parameter;
import com.github.lukesky19.skylib.common.api.database.parameter.impl.IntegerParameter;
import com.github.lukesky19.skylib.common.api.database.parameter.impl.LongParameter;
import com.github.lukesky19.skylib.common.api.database.parameter.impl.StringParameter;
import com.github.lukesky19.skylib.common.api.database.parameter.impl.UUIDParameter;
import com.github.lukesky19.skyplaytime.database.queue.QueueManager;
import com.github.lukesky19.skyplaytime.leaderboard.data.Position;
import com.github.lukesky19.skyplaytime.leaderboard.data.TopTen;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import com.github.lukesky19.skyplaytime.util.TimeCategory;
import com.github.lukesky19.skyplaytime.util.parameter.CaseSensitiveStringParameter;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * This class handles the players table that stores player data.
 */
public class PlayTimeTable {
    private final @NonNull ComponentLogger logger;
    private final @NonNull QueueManager queueManager;
    private final @NonNull VersionsTable versionsTable;
    private final @NonNull String tableName = "players";

    /**
     * Constructor
     * @param logger A {@link ComponentLogger}.
     * @param queueManager A {@link QueueManager} instance.
     * @param versionsTable A {@link VersionsTable} instance.
     */
    public PlayTimeTable(
            @NonNull ComponentLogger logger,
            @NonNull QueueManager queueManager,
            @NonNull VersionsTable versionsTable) {
        this.logger = logger;
        this.queueManager = queueManager;
        this.versionsTable = versionsTable;
    }

    /**
     * Creates a table to store all {@link Player}'s {@link UUID}s as a string.
     * Queues the table creation and index creation sql.
     * @return A {@link CompletableFuture} of type {@link Void} when complete.
     */
    public @NonNull CompletableFuture<Void> createTable() {
        String tableCreationSql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "uuid TEXT PRIMARY KEY NOT NULL UNIQUE, " +
                "name TEXT NOT NULL, " +
                "daily LONG NOT NULL DEFAULT 0, " +
                "weekly LONG NOT NULL DEFAULT 0, " +
                "monthly LONG NOT NULL DEFAULT 0, " +
                "yearly LONG NOT NULL DEFAULT 0, " +
                "total LONG NOT NULL DEFAULT 0, " +
                "exempt INTEGER NOT NULL DEFAULT 0, " +
                "last_updated LONG NOT NULL DEFAULT 0)";
        String indexCreationSql = "CREATE INDEX IF NOT EXISTS idx_player_uuids ON " + tableName + "(uuid);";

        return queueManager.queueBulkWriteTransaction(List.of(tableCreationSql, indexCreationSql))
                .thenCompose(_ -> versionsTable.updateVersion(tableName, 1));
    }

    /**
     * Loads the player's play time and exemption status from the database.
     * @param uuid The {@link UUID} to load data for.
     * @param playerData The {@link PlayerData} to put data into.
     * @return A {@link CompletableFuture} with {@link Void} when complete. The {@link PlayerData} passed to the method will be updated.
     */
    public @NonNull CompletableFuture<Void> loadPlayerData(@NonNull UUID uuid, @NonNull PlayerData playerData) {
        String selectSql = "SELECT daily, weekly, monthly, yearly, total, exempt FROM " + tableName + " WHERE uuid = ?";
        UUIDParameter uuidParameter = new UUIDParameter(uuid);

        return queueManager.queueReadTransaction(selectSql, List.of(uuidParameter), resultSet -> {
            try {
                if(resultSet.next()) {
                    playerData.setDailyPlayTime(playerData.getDailyPlayTimeSeconds() + resultSet.getLong("daily"));
                    playerData.setWeeklyPlayTime(playerData.getWeeklyPlayTimeSeconds() + resultSet.getLong("weekly"));
                    playerData.setMonthlyPlayTime(playerData.getMonthlyPlayTimeSeconds() + resultSet.getLong("monthly"));
                    playerData.setYearlyPlayTime(playerData.getYearlyPlayTimeSeconds() + resultSet.getLong("yearly"));
                    playerData.setTotalPlayTime(playerData.getTotalPlayTimeSeconds() + resultSet.getLong("total"));
                    playerData.setExempt(resultSet.getBoolean("exempt"));
                }

                return null;
            } catch (SQLException e) {
                logger.warn(AdventureUtility.plain("Failed to load player data for player " + playerData.getName()));
                return null;
            }
        });
    }

    /**
     * Saves the player data for a single player.
     * @param uuid The {@link UUID} of the player.
     * @param playerData The {@link PlayerData} for the player.
     * @return A {@link CompletableFuture} of type {@link Void} when complete.
     */
    public @NonNull CompletableFuture<Void> savePlayerData(@NonNull UUID uuid, @NonNull PlayerData playerData) {
        String updateSql = "INSERT INTO " + tableName + " (" +
                "uuid, " +
                "name, " +
                "daily, " +
                "weekly, " +
                "monthly, " +
                "yearly, " +
                "total, " +
                "exempt, " +
                "last_updated) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (uuid) " +
                "DO UPDATE SET " +
                "name = ?, " +
                "daily = ?, " +
                "weekly = ?, " +
                "monthly = ?, " +
                "yearly = ?, " +
                "total = ?, " +
                "exempt = ?, " +
                "last_updated = ? " +
                "WHERE last_updated <= ?";

        UUIDParameter uuidParameter = new UUIDParameter(uuid);
        StringParameter nameParameter = new StringParameter(playerData.getName());
        LongParameter dailyTimeParameter = new LongParameter(playerData.getDailyPlayTimeSeconds());
        LongParameter weeklyTimeParameter = new LongParameter(playerData.getWeeklyPlayTimeSeconds());
        LongParameter monthlyTimeParameter = new LongParameter(playerData.getMonthlyPlayTimeSeconds());
        LongParameter yearlyTimeParameter = new LongParameter(playerData.getYearlyPlayTimeSeconds());
        LongParameter totalTimeParameter = new LongParameter(playerData.getTotalPlayTimeSeconds());
        IntegerParameter exemptParameter = new IntegerParameter(playerData.isExempt() ? 1 : 0);
        LongParameter timestampParameter = new LongParameter(System.currentTimeMillis());

        List<Parameter<?>> parameters = List.of(
                uuidParameter,
                nameParameter,
                dailyTimeParameter,
                weeklyTimeParameter,
                monthlyTimeParameter,
                yearlyTimeParameter,
                totalTimeParameter,
                exemptParameter,
                timestampParameter,
                nameParameter,
                dailyTimeParameter,
                weeklyTimeParameter,
                monthlyTimeParameter,
                yearlyTimeParameter,
                totalTimeParameter,
                exemptParameter,
                timestampParameter,
                timestampParameter);

        return queueManager.queueWriteTransaction(updateSql, parameters).thenRun(() -> {});
    }

    /**
     * Saves all player data to the database.
     * @param playerDataMap A {@link Map} mapping {@link UUID}s to {@link PlayerData}.
     * @return A {@link CompletableFuture} of type {@link List} containing {@link Boolean}s when complete. true if successful, and false if not.
     */
    public @NonNull CompletableFuture<@NonNull List<@NonNull Boolean>> savePlayerData(@NonNull Map<@NonNull UUID, @NonNull PlayerData> playerDataMap) {
        List<List<Parameter<?>>> listOfParametersList = new ArrayList<>();
        String updateSql = "INSERT INTO " + tableName + " (" +
                "uuid, " +
                "name, " +
                "daily, " +
                "weekly, " +
                "monthly, " +
                "yearly, " +
                "total, " +
                "exempt, " +
                "last_updated) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (uuid) " +
                "DO UPDATE SET " +
                "name = ?, " +
                "daily = ?, " +
                "weekly = ?, " +
                "monthly = ?, " +
                "yearly = ?, " +
                "total = ?, " +
                "exempt = ?, " +
                "last_updated = ? " +
                "WHERE last_updated < ?";

        playerDataMap.forEach((playerId, playerData) -> {
            UUIDParameter uuidParameter = new UUIDParameter(playerId);
            CaseSensitiveStringParameter playerNameParameter = new CaseSensitiveStringParameter(playerData.getName());
            LongParameter dailyTimeParameter = new LongParameter(playerData.getDailyPlayTimeSeconds());
            LongParameter weeklyTimeParameter = new LongParameter(playerData.getWeeklyPlayTimeSeconds());
            LongParameter monthlyTimeParameter = new LongParameter(playerData.getMonthlyPlayTimeSeconds());
            LongParameter yearlyTimeParameter = new LongParameter(playerData.getYearlyPlayTimeSeconds());
            LongParameter totalTimeParameter = new LongParameter(playerData.getTotalPlayTimeSeconds());
            IntegerParameter exemptParameter = new IntegerParameter(playerData.isExempt() ? 1 : 0);
            LongParameter timestampParameter = new LongParameter(System.currentTimeMillis());

            List<Parameter<?>> parameters = List.of(
                    uuidParameter,
                    playerNameParameter,
                    dailyTimeParameter,
                    weeklyTimeParameter,
                    monthlyTimeParameter,
                    yearlyTimeParameter,
                    totalTimeParameter,
                    exemptParameter,
                    timestampParameter,
                    playerNameParameter,
                    dailyTimeParameter,
                    weeklyTimeParameter,
                    monthlyTimeParameter,
                    yearlyTimeParameter,
                    totalTimeParameter,
                    exemptParameter,
                    timestampParameter,
                    timestampParameter);

            listOfParametersList.add(parameters);
        });

        return queueManager.queueBulkWriteTransaction(updateSql, listOfParametersList)
                .thenApply(list -> list.stream().map(rowsUpdated -> rowsUpdated > 0).toList());
    }

    /**
     * Resets play time using the boolean options provided.
     * @param daily Should all daily play time be reset?
     * @param weekly Should all weekly play time be reset?
     * @param monthly Should all monthly play time be reset?
     * @param yearly Should all yearly play time be reset?
     * @param total Should all total play time be reset?
     * @return A {@link CompletableFuture} containing a {@link Boolean}. true if the reset succeeded, false if not.
     */
    public @NonNull CompletableFuture<@NonNull Boolean> resetPlayTime(boolean daily, boolean weekly, boolean monthly, boolean yearly, boolean total) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName + " SET ");
        if(daily) sqlBuilder.append("daily = 0, ");
        if(weekly) sqlBuilder.append("weekly = 0, ");
        if(monthly) sqlBuilder.append("monthly = 0, ");
        if(yearly) sqlBuilder.append("yearly = 0, ");
        if(total) sqlBuilder.append("total = 0, ");
        sqlBuilder.append("last_updated = ? WHERE last_updated < ?");

        String updateSql = sqlBuilder.toString();
        LongParameter timestampParameter = new LongParameter(System.currentTimeMillis());

        return queueManager.queueWriteTransaction(updateSql, List.of(timestampParameter, timestampParameter)).thenApply(rowsUpdated -> rowsUpdated > 0);
    }

    /**
     * Retrieves the {@link TopTen} for the {@link TimeCategory} provided that are not exempt.
     * {@link TimeCategory#SESSION} will return a {@link TopTen} with all null values.
     * @param timeCategory The {@link TimeCategory} to sort the query to get player data for.
     * @return A {@link CompletableFuture} containing the {@link TopTen} for the {@link TimeCategory} provided.
     */
    public @NonNull CompletableFuture<@NonNull TopTen> getTopTenByCategoryNotExempt(@NonNull TimeCategory timeCategory) {
        if(timeCategory == TimeCategory.SESSION) return CompletableFuture.completedFuture(new TopTen());
        if(timeCategory == TimeCategory.ALL) timeCategory = TimeCategory.TOTAL;
        String timeCategoryName = timeCategory.toString().toLowerCase();

        String sql = "SELECT uuid, name, " + timeCategoryName + " FROM players WHERE exempt = 0 ORDER BY " + timeCategoryName + " DESC LIMIT 10";
        return queueManager.queueReadTransaction(sql, resultSet -> {
            List<Position> positionList = new LinkedList<>();

            try {
                while(resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    String name = resultSet.getString("name");
                    int seconds = resultSet.getInt(timeCategoryName);

                    positionList.add(new Position(uuid, name, seconds));
                }

                return new TopTen(positionList);
            } catch (SQLException e) {
                logger.warn(AdventureUtility.plain("Failed to get top ten by category " + timeCategoryName + " due to an error. Error: " + e.getMessage()));
                return new TopTen();
            }
        });
    }
}
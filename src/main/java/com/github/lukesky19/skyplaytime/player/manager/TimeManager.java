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
package com.github.lukesky19.skyplaytime.player.manager;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.settings.Settings;
import com.github.lukesky19.skyplaytime.settings.SettingsManager;
import com.github.lukesky19.skyplaytime.database.DatabaseManager;
import com.github.lukesky19.skyplaytime.leaderboard.manager.LeaderboardManager;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import com.github.lukesky19.skyplaytime.util.enums.TimeCategory;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * This class manages the update and retrieval of play time for online players.
 */
public class TimeManager {
    private final @NonNull ComponentLogger logger;
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull DatabaseManager databaseManager;
    private final @NonNull PlayerDataManager playerDataManager;
    private final @NonNull LeaderboardManager leaderboardManager;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param databaseManager A {@link DatabaseManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param leaderboardManager A {@link LeaderboardManager} instance.
     */
    public TimeManager(
            @NonNull SkyPlayTime skyPlayTime,
            @NonNull SettingsManager settingsManager,
            @NonNull DatabaseManager databaseManager,
            @NonNull PlayerDataManager playerDataManager,
            @NonNull LeaderboardManager leaderboardManager) {
        this.logger = skyPlayTime.getComponentLogger();
        this.settingsManager = settingsManager;
        this.databaseManager = databaseManager;
        this.playerDataManager = playerDataManager;
        this.leaderboardManager = leaderboardManager;
    }

    /**
     * Gets the player's play time in seconds for the provided {@link TimeCategory}.
     * @param player The {@link Player}.
     * @param timeCategory The {@link TimeCategory} to get play time for.
     * @return The player's play time in seconds for the provided {@link TimeCategory}.
     */
    public long getPlayTimeSeconds(@NonNull Player player, @NonNull TimeCategory timeCategory) {
        return playerDataManager.getPlayerData(player)
                .map(playerData -> playerData.getPlayTime(timeCategory))
                .orElseGet(() -> {
                    logger.warn(AdventureUtility.plain("Unable to get play time for player " + player.getName() + " due to no player data loaded."));
                    return 0L;
                });
    }

    /**
     * Adds the play time in seconds provided to the player's play time for the provided {@link TimeCategory}.
     * @param player The {@link Player}.
     * @param timeCategory The {@link TimeCategory} to add play time to.
     * @param playTimeSeconds The play time in seconds to remove.
     * @return true if successful, false if not.
     */
    public boolean addPlayTimeSeconds(
            @NonNull Player player,
            @NonNull TimeCategory timeCategory,
            long playTimeSeconds) {
        return playerDataManager.getPlayerData(player)
                .map(playerData -> playerData.addPlayTime(timeCategory, playTimeSeconds))
                .orElseGet(() -> {
                    logger.warn(AdventureUtility.plain("Unable to add play time to player " + player.getName() + " due to no player data loaded."));
                    return false;
                });
    }

    /**
     * Removes the play time in seconds provided to the player's play time for the provided {@link TimeCategory}.
     * @param player The {@link Player}.
     * @param timeCategory The {@link TimeCategory} to remove play time from.
     * @param playTimeSeconds The play time in seconds to remove.
     * @return true if successful, false if not.
     */
    public boolean removePlayTimeSeconds(
            @NonNull Player player,
            @NonNull TimeCategory timeCategory,
            long playTimeSeconds) {
        return playerDataManager.getPlayerData(player)
                .map(playerData -> playerData.removePlayTime(timeCategory, playTimeSeconds))
                .orElseGet(() -> {
                    logger.warn(AdventureUtility.plain("Unable to remove play time from player " + player.getName() + " due to no player data loaded."));
                    return false;
                });
    }

    /**
     * Sets the player's play time for the provided {@link TimeCategory} to the play time in seconds provided.
     * @param player The {@link Player}.
     * @param timeCategory The {@link TimeCategory} to set play time for.
     * @param playTimeSeconds The play time in seconds to remove.
     * @return true if successful, false if not.
     */
    public boolean setPlayTimeSeconds(
            @NonNull Player player,
            @NonNull TimeCategory timeCategory,
            long playTimeSeconds) {
        return playerDataManager.getPlayerData(player)
                .map(playerData -> playerData.setPlayTime(timeCategory, playTimeSeconds))
                .orElseGet(() -> {
                    logger.warn(AdventureUtility.plain("Unable to set play time for player " + player.getName() + " due to no player data loaded."));
                    return false;
                });
    }

    /**
     * Reset the player's play time for the provided {@link UUID} according to the provided boolean options.
     * @param player The {@link Player}.
     * @param session Should session play time be reset?
     * @param daily Should daily play time be reset?
     * @param weekly Should weekly play time be reset?
     * @param monthly Should monthly play time be reset?
     * @param yearly Should yearly play time be reset?
     * @param total Should total play time be reset?
     * @return true if succeeds, false if not.
     */
    public boolean resetPlayTime(
            @NonNull Player player,
            boolean session,
            boolean daily,
            boolean weekly,
            boolean monthly,
            boolean yearly,
            boolean total) {
        if(!session && !daily && !weekly && !monthly && !yearly && !total) {
            return false;
        }

        // If the plugin's settings are invalid, abort the reset.
        Settings settings = settingsManager.getSettings();
        if(settings == null) {
            logger.warn(AdventureUtility.plain("Unable to reset play time due to an invalid settings.yml!"));
            return false;
        }

        // Get the player's data and abort the reset if no player data was found.
        return playerDataManager.getPlayerData(player)
                .map(playerData -> resetPlayTime(player.getUniqueId(), playerData, session, daily, weekly, monthly, yearly, total))
                .orElseGet(() -> {
                    logger.warn(AdventureUtility.plain("Unable to reset play time for player " + player.getName() + " due to no player data found."));
                    return false;
                });
    }

    /**
     * Reset the provided {@link PlayerData}'s play time according to the provided boolean options.
     * @param playerId The {@link UUID} of the player.
     * @param playerData The player's {@link PlayerData}.
     * @param session Should session play time be reset?
     * @param daily Should daily play time be reset?
     * @param weekly Should weekly play time be reset?
     * @param monthly Should monthly play time be reset?
     * @param yearly Should yearly play time be reset?
     * @param total Should total play time be reset?
     * @return true if succeeds, false if not.
     */
    protected boolean resetPlayTime(
            @NonNull UUID playerId,
            @NonNull PlayerData playerData,
            boolean session,
            boolean daily,
            boolean weekly,
            boolean monthly,
            boolean yearly,
            boolean total) {
        if(session) playerData.setPlayTime(TimeCategory.SESSION, 0);
        if(daily) playerData.setPlayTime(TimeCategory.DAILY, 0);
        if(weekly) playerData.setPlayTime(TimeCategory.WEEKLY, 0);
        if(monthly) playerData.setPlayTime(TimeCategory.MONTHLY, 0);
        if(yearly) playerData.setPlayTime(TimeCategory.YEARLY, 0);
        if(total) playerData.setPlayTime(TimeCategory.TOTAL, 0);

        playerDataManager.savePlayerData(playerId);

        return true;
    }

    /**
     * Reset all player's play time for according to the provided boolean options.
     * @param session Should session play time be reset?
     * @param daily Should daily play time be reset?
     * @param weekly Should weekly play time be reset?
     * @param monthly Should monthly play time be reset?
     * @param yearly Should yearly play time be reset?
     * @param total Should total play time be reset?
     * @return A {@link CompletableFuture} of type {@link Boolean}. true if successful, false if not.
     */
    public @NonNull CompletableFuture<@NonNull Boolean> resetPlayTime(
            boolean session,
            boolean daily,
            boolean weekly,
            boolean monthly,
            boolean yearly,
            boolean total) {
        // If all booleans for which time categories to reset are false, abort the reset.
        if(!session && !daily && !weekly && !monthly && !yearly && !total) {
            return CompletableFuture.completedFuture(false);
        }

        // If the plugin's settings are invalid, abort the reset.
        Settings settings = settingsManager.getSettings();
        if(settings == null) {
            logger.warn(AdventureUtility.plain("Unable to reset play time due to an invalid settings.yml!"));
            return CompletableFuture.completedFuture(false);
        }

        // Save Player Data
        return playerDataManager.savePlayerData()
                .thenCompose(list -> {
                    // If an error occurred while saving player data, abort the reset.
                    if(list.contains(false)) {
                        logger.warn(AdventureUtility.plain("Unable to reset play time due to an error while saving player data."));
                        return CompletableFuture.completedFuture(false);
                    }

                    // If a leaderboard snapshot is configured to be saved, do so.
                    return settings.leaderboardSnapshotOnReset()
                            ? createLeaderboardSnapshot(settings, session, daily, weekly, monthly, yearly, total)
                            : resetPlayTime(settings, session, daily, weekly, monthly, yearly, total);
        });
    }

    /**
     * Create the leaderboard snapshots according to the time category booleans.
     * Then runs {@link #resetPlayTime(Settings, boolean, boolean, boolean, boolean, boolean, boolean)}.
     * @param settings The plugin's {@link Settings}.
     * @param session Should a leaderboard snapshot be created for session play time?
     * @param daily Should a leaderboard snapshot be created for daily play time?
     * @param weekly Should a leaderboard snapshot be created for weekly play time?
     * @param monthly Should a leaderboard snapshot be created for monthly play time?
     * @param yearly Should a leaderboard snapshot be created for yearly play time?
     * @param total Should a leaderboard snapshot be created for total play time?
     * @return A {@link CompletableFuture} of type {@link Boolean}. true if successful, otherwise false.
     */
    protected @NonNull CompletableFuture<Boolean> createLeaderboardSnapshot(
            @NonNull Settings settings,
            boolean session,
            boolean daily,
            boolean weekly,
            boolean monthly,
            boolean yearly,
            boolean total) {
        boolean leaderboardResult = leaderboardManager.saveLeaderboardSnapshots(session, daily, weekly, monthly, yearly, total);

        if(!leaderboardResult) {
            logger.warn(AdventureUtility.plain("Unable to reset play time due to an error while saving leaderboard snapshots."));
            return CompletableFuture.completedFuture(false);
        }

        return resetPlayTime(settings, session, daily, weekly, monthly, yearly, total);
    }

    /**
     * Perform a database backup if configured to do so then reset play time for online players and the database according to the time category boolean options.
     * @param settings The plugin's {@link Settings}.
     * @param session Should a leaderboard snapshot be created for session play time?
     * @param daily Should a leaderboard snapshot be created for daily play time?
     * @param weekly Should a leaderboard snapshot be created for weekly play time?
     * @param monthly Should a leaderboard snapshot be created for monthly play time?
     * @param yearly Should a leaderboard snapshot be created for yearly play time?
     * @param total Should a leaderboard snapshot be created for total play time?
     * @return A {@link CompletableFuture} of type {@link Boolean}. true if successful, otherwise false.
     */
    protected @NonNull CompletableFuture<@NonNull Boolean> resetPlayTime(
            @NonNull Settings settings,
            boolean session,
            boolean daily,
            boolean weekly,
            boolean monthly,
            boolean yearly,
            boolean total) {
        // Perform backup if configured
        if(settings.backupOnReset()) {
            return databaseManager.backupDatabase()
                    .thenCompose(backupResult -> {
                        if(!backupResult) {
                            logger.warn(AdventureUtility.plain("Unable to reset play time due to an error during backup."));
                            return CompletableFuture.completedFuture(false);
                        }

                        resetOnlinePlayTime(session, daily, weekly, monthly, yearly, total);

                        return resetDatabasePlayTime(daily, weekly, monthly, yearly, total);
                    });
        } else {
            resetOnlinePlayTime(session, daily, weekly, monthly, yearly, total);

            return resetDatabasePlayTime(daily, weekly, monthly, yearly, total);
        }
    }

    /**
     * Set all online player's play time to 0 according to the time category booleans.
     * @param session Should session play time be reset?
     * @param daily Should daily play time be reset?
     * @param weekly Should weekly play time be reset?
     * @param monthly Should monthly play time be reset?
     * @param yearly Should yearly play time be reset?
     * @param total Should total play time be reset?
     */
    protected void resetOnlinePlayTime(
            boolean session,
            boolean daily,
            boolean weekly,
            boolean monthly,
            boolean yearly,
            boolean total) {
        Map<@NonNull UUID, @NonNull PlayerData> playerDataMap = playerDataManager.getPlayerDataMap();

        if (session) playerDataMap.values().forEach(data -> data.setPlayTime(TimeCategory.SESSION, 0));
        if (daily) playerDataMap.values().forEach(data -> data.setPlayTime(TimeCategory.DAILY, 0));
        if (weekly) playerDataMap.values().forEach(data -> data.setPlayTime(TimeCategory.WEEKLY, 0));
        if (monthly) playerDataMap.values().forEach(data -> data.setPlayTime(TimeCategory.MONTHLY, 0));
        if (yearly) playerDataMap.values().forEach(data -> data.setPlayTime(TimeCategory.YEARLY, 0));
        if (total) playerDataMap.values().forEach(data -> data.setPlayTime(TimeCategory.TOTAL, 0));
    }

    /**
     * Set all play time in the database to 0 according to the time category booleans.
     * @param daily Should daily play time be reset?
     * @param weekly Should weekly play time be reset?
     * @param monthly Should monthly play time be reset?
     * @param yearly Should yearly play time be reset?
     * @param total Should total play time be reset?
     * @return A {@link CompletableFuture} of type {@link Boolean}. true if successful, otherwise false.
     */
    protected @NonNull CompletableFuture<@NonNull Boolean> resetDatabasePlayTime(
            boolean daily,
            boolean weekly,
            boolean monthly,
            boolean yearly,
            boolean total) {
        return databaseManager.getPlayTimeTable().resetPlayTime(daily, weekly, monthly, yearly, total);
    }
}
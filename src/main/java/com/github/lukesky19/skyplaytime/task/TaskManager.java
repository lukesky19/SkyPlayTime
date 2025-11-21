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
package com.github.lukesky19.skyplaytime.task;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.config.data.settings.Settings;
import com.github.lukesky19.skyplaytime.leaderboard.manager.LeaderboardManager;
import com.github.lukesky19.skyplaytime.player.manager.AFKManager;
import com.github.lukesky19.skyplaytime.config.manager.settings.SettingsManager;
import com.github.lukesky19.skyplaytime.player.manager.TimeManager;
import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import com.github.lukesky19.skyplaytime.task.tasks.*;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * This class manages
 */
public class TaskManager {
    private final @NotNull SkyPlayTime skyPlayTime;
    private final @NotNull ComponentLogger logger;
    private final @NotNull SettingsManager settingsManager;
    private final @NotNull PlayerDataManager playerDataManager;
    private final @NotNull TimeManager timeManager;
    private final @NotNull AFKManager afkManager;
    private final @NotNull LeaderboardManager leaderboardManager;

    // Tasks
    private @Nullable BukkitTask activityTask;
    private @Nullable BukkitTask cacheTopTenTask;
    private @Nullable BukkitTask calculateTopTenTask;
    private @Nullable BukkitTask cleanupTask;
    private @Nullable BukkitTask playTimeTask;
    private @Nullable BukkitTask resetTask;
    private @Nullable BukkitTask saveTask;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param timeManager A {@link TimeManager} instance.
     * @param afkManager An {@link AFKManager} instance.
     * @param leaderboardManager A {@link LeaderboardManager} instance.
     */
    public TaskManager(
            @NotNull SkyPlayTime skyPlayTime,
            @NotNull SettingsManager settingsManager,
            @NotNull PlayerDataManager playerDataManager,
            @NotNull TimeManager timeManager,
            @NotNull AFKManager afkManager,
            @NotNull LeaderboardManager leaderboardManager) {
        this.skyPlayTime = skyPlayTime;
        this.logger = skyPlayTime.getComponentLogger();
        this.settingsManager = settingsManager;
        this.playerDataManager = playerDataManager;
        this.timeManager = timeManager;
        this.afkManager = afkManager;
        this.leaderboardManager = leaderboardManager;
    }

    /**
     * Restarts any tasks that are currently executing.
     */
    public void restartTasks() {
        stopTasks();
        startTasks();
    }

    /**
     * Creates and starts new tasks. Existing tasks will be stopped.
     */
    public void startTasks() {
        stopTasks();

        startActivityTask();
        startCacheTopTenTask();
        startCalculateTopTenTask();
        startCleanupTask();
        startPlayTimeTask();
        startResetTask();
        startSaveTask();
    }

    /**
     * Stops any tasks that are running.
     */
    public void stopTasks() {
        stopActivityTask();
        stopCacheTopTenTask();
        stopCalculateTopTenTask();
        stopCleanupTask();
        stopPlayTimeTask();
        stopResetTask();
        stopSaveTask();
    }

    /**
     * Start the {@link ActivityTask}.
     */
    private void startActivityTask() {
        activityTask = new ActivityTask(skyPlayTime, settingsManager, playerDataManager, afkManager).runTaskTimer(skyPlayTime, 20L, 20L);
    }

    /**
     * Stop the {@link ActivityTask}.
     */
    private void stopActivityTask() {
        if(activityTask != null) {
            if(!activityTask.isCancelled()) {
                activityTask.cancel();
            }

            activityTask = null;
        }
    }

    /**
     * Start the {@link CacheTopTenTask}.
     */
    private void startCacheTopTenTask() {
        long ticks = 60 * 15 * 20L;
        cacheTopTenTask = new CacheTopTenTask(leaderboardManager).runTaskTimer(skyPlayTime, ticks, ticks);
    }

    /**
     * Stop the {@link CacheTopTenTask}.
     */
    private void stopCacheTopTenTask() {
        if(cacheTopTenTask != null) {
            if(!cacheTopTenTask.isCancelled()) {
                cacheTopTenTask.cancel();
            }

            cacheTopTenTask = null;
        }
    }

    /**
     * Start the {@link CalculateTopTenTask}.
     */
    private void startCalculateTopTenTask() {
        long ticks = 60 * 15 * 20L;
        calculateTopTenTask = new CalculateTopTenTask(leaderboardManager).runTaskTimer(skyPlayTime, ticks, ticks);
    }

    /**
     * Stop the {@link CalculateTopTenTask}.
     */
    private void stopCalculateTopTenTask() {
        if(calculateTopTenTask != null) {
            if(!calculateTopTenTask.isCancelled()) {
                calculateTopTenTask.cancel();
            }

            calculateTopTenTask = null;
        }
    }

    /**
     * Start the {@link CleanupTask}.
     */
    private void startCleanupTask() {
        long ticks = 60 * 60 * 20L;
        cleanupTask = new CleanupTask(skyPlayTime, settingsManager).runTaskTimer(skyPlayTime, 10 * 20L, ticks);
    }

    /**
     * Stop the {@link ActivityTask}.
     */
    private void stopCleanupTask() {
        if(cleanupTask != null) {
            if(!cleanupTask.isCancelled()) {
                cleanupTask.cancel();
            }

            cleanupTask = null;
        }
    }

    /**
     * Start the {@link PlayTimeTask}.
     */
    private void startPlayTimeTask() {
        playTimeTask = new PlayTimeTask(skyPlayTime, playerDataManager).runTaskTimer(skyPlayTime, 20L, 20L);
    }

    /**
     * Stop the {@link PlayTimeTask}.
     */
    private void stopPlayTimeTask() {
        if(playTimeTask != null) {
            if(!playTimeTask.isCancelled()) {
                playTimeTask.cancel();
            }

            playTimeTask = null;
        }
    }

    /**
     * Start the {@link ResetTask}.
     */
    private void startResetTask() {
        long ticksUntilNextHour = ticksUntilNextHour();
        long ticksInHour = 60 * 60 * 20L;

        resetTask = new ResetTask(skyPlayTime, settingsManager, timeManager).runTaskTimer(skyPlayTime, ticksUntilNextHour, ticksInHour);
    }

    /**
     * Stop the {@link ResetTask}.
     */
    private void stopResetTask() {
        if(resetTask != null) {
            if(!resetTask.isCancelled()) {
                resetTask.cancel();
            }

            resetTask = null;
        }
    }

    /**
     * Start the {@link SaveTask}.
     */
    private void startSaveTask() {
        Settings settings = settingsManager.getSettings();
        if(settings == null || settings.saveIntervalSeconds() <= 0) {
            logger.error(AdventureUtil.deserialize("Unable to start the save task due to invalid plugin settings."));
            return;
        }

        long ticks = settings.saveIntervalSeconds() * 20L;
        saveTask = new SaveTask(playerDataManager).runTaskTimer(skyPlayTime, ticks, ticks);
    }

    /**
     * Stop the {@link SaveTask}.
     */
    private void stopSaveTask() {
        if(saveTask != null) {
            if(!saveTask.isCancelled()) {
                saveTask.cancel();
            }

            saveTask = null;
        }
    }

    /**
     * Gets the number of game ticks until the next hour.
     * @return The number of game ticks until the next hour.
     */
    private long ticksUntilNextHour() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextHour = now.plusHours(1).truncatedTo(ChronoUnit.HOURS);

        return ChronoUnit.SECONDS.between(now, nextHour) * 20L;
    }
}

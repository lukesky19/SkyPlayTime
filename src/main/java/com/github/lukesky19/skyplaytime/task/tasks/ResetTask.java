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
package com.github.lukesky19.skyplaytime.task.tasks;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.config.data.settings.Settings;
import com.github.lukesky19.skyplaytime.config.manager.settings.SettingsManager;
import com.github.lukesky19.skyplaytime.player.manager.TimeManager;
import com.github.lukesky19.skyplaytime.util.TimeCategory;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * This task resets play time categories as necessary.
 */
public class ResetTask extends BukkitRunnable {
    private final @NotNull ComponentLogger logger;
    private final @NotNull SettingsManager settingsManager;
    private final @NotNull TimeManager timeManager;

    /**
     * Constructor
     * @param skyPlayTime A {@link SkyPlayTime} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param timeManager A {@link TimeManager} instance.
     */
    public ResetTask(
            @NotNull SkyPlayTime skyPlayTime,
            @NotNull SettingsManager settingsManager,
            @NotNull TimeManager timeManager) {
        this.logger = skyPlayTime.getComponentLogger();
        this.settingsManager = settingsManager;
        this.timeManager = timeManager;
    }

    /**
     * Reset player's play time categories as necessary.
     */
    @Override
    public void run() {
        @Nullable Settings settings = settingsManager.getSettings();
        if(settings == null) return;

        Settings.ResetSettings resetSettings = settings.resetSettings();
        Settings.LastResetTimes resetTimes = settings.lastResetTimes();
        long currentTime = System.currentTimeMillis();

        ZoneId zoneId = ZoneId.of(resetSettings.zoneId());
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(resetSettings.dayOfWeek());

        long dailyResetTime = calculateEpochMillisecondsForNextReset(TimeCategory.DAILY, dayOfWeek, zoneId, resetSettings.resetHour(), resetTimes);
        long weeklyResetTime = calculateEpochMillisecondsForNextReset(TimeCategory.WEEKLY, dayOfWeek, zoneId, resetSettings.resetHour(), resetTimes);
        long monthlyResetTime = calculateEpochMillisecondsForNextReset(TimeCategory.MONTHLY, dayOfWeek, zoneId, resetSettings.resetHour(), resetTimes);
        long yearlyResetTime = calculateEpochMillisecondsForNextReset(TimeCategory.YEARLY, dayOfWeek, zoneId, resetSettings.resetHour(), resetTimes);

        boolean resetDailyTime = currentTime >= dailyResetTime;
        boolean resetWeeklyTime = currentTime >= weeklyResetTime;
        boolean resetMonthlyTime = currentTime >= monthlyResetTime;
        boolean resetYearlyTime = currentTime >= yearlyResetTime;

        // Return if there is no play time to reset.
        if(!resetDailyTime && !resetWeeklyTime && !resetMonthlyTime && !resetYearlyTime) return;

        timeManager.resetPlayTime(false, resetDailyTime, resetWeeklyTime, resetMonthlyTime, resetYearlyTime, false).thenAccept(result -> {
            if(!result) {
                logger.error(AdventureUtil.deserialize("Unable save last reset timestamps due to an error while resetting play time."));
                return;
            }

            long dailyResetTimestamp = resetTimes.daily();
            long weeklyResetTimestamp = resetTimes.weekly();
            long monthlyResetTimestamp = resetTimes.monthly();
            long yearlyResetTimestamp = resetTimes.yearly();

            if(resetDailyTime) dailyResetTimestamp = System.currentTimeMillis();
            if(resetWeeklyTime) weeklyResetTimestamp = System.currentTimeMillis();
            if(resetMonthlyTime) monthlyResetTimestamp = System.currentTimeMillis();
            if(resetYearlyTime) yearlyResetTimestamp = System.currentTimeMillis();

            Settings.LastResetTimes lastResetTimesRecord = new Settings.LastResetTimes(
                    dailyResetTimestamp,
                    weeklyResetTimestamp,
                    monthlyResetTimestamp,
                    yearlyResetTimestamp);
            Settings updatedSettings = new Settings(
                    settings.configVersion(),
                    settings.locale(),
                    settings.saveIntervalSeconds(),
                    settings.backupOnReset(),
                    settings.leaderboardSnapshotOnReset(),
                    settings.backupsRemoveOlderThan(),
                    settings.leaderboardRemoveOlderThan(),
                    settings.afkSettings(),
                    settings.resetSettings(),
                    lastResetTimesRecord);
            settingsManager.saveSettings(updatedSettings);
        });
    }

    /**
     * Calculates the time when the next reset should occur for a given {@link TimeCategory}.
     * @param resetType The {@link TimeCategory} to get the next reset time for.
     * @param dayOfWeek The {@link DayOfWeek} when weekly play time should be reset on.
     * @param zoneId The {@link ZoneId} (the time zone) to use when calculating reset times.
     * @param hour What hour of the day should resets occur at. Should be a value between and including 1 to 24.
     * @param resetTimes The {@link Settings.LastResetTimes} containing when each {@link TimeCategory} was last reset.
     * @return The milliseconds since the epoch when the next reset should occur.
     */
    private long calculateEpochMillisecondsForNextReset(
            @NotNull TimeCategory resetType,
            @NotNull DayOfWeek dayOfWeek,
            @NotNull ZoneId zoneId,
            int hour,
            @NotNull Settings.LastResetTimes resetTimes) {
        LocalDateTime now = LocalDateTime.now(zoneId);
        LocalDateTime nextReset;

        long lastResetMillis = switch (resetType) {
            case DAILY -> resetTimes.daily();
            case WEEKLY -> resetTimes.weekly();
            case MONTHLY -> resetTimes.monthly();
            case YEARLY -> resetTimes.yearly();
            default -> throw new IllegalStateException("Unexpected TimeCategory provided: " + resetType);
        };

        LocalDateTime lastReset = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastResetMillis), zoneId);

        switch(resetType) {
            case DAILY -> nextReset = lastReset.plusDays(1).withHour(hour).withMinute(0).withSecond(0).withNano(0);
            case WEEKLY -> nextReset = lastReset.with(dayOfWeek).plusWeeks(1).withHour(hour).withMinute(0).withSecond(0).withNano(0);
            case MONTHLY -> nextReset = lastReset.withDayOfMonth(1).plusMonths(1).withHour(hour).withMinute(0).withSecond(0).withNano(0);
            case YEARLY -> nextReset = lastReset.withMonth(1).withDayOfMonth(1).plusYears(1).withHour(hour).withMinute(0).withSecond(0).withNano(0);
            default -> throw new RuntimeException("Unknown ResetType provided.");
        }

        if(nextReset.isBefore(now)) {
            switch (resetType) {
                case DAILY -> nextReset = nextReset.plusDays(1);
                case WEEKLY -> nextReset = nextReset.plusWeeks(1);
                case MONTHLY -> nextReset = nextReset.plusMonths(1);
                case YEARLY -> nextReset = nextReset.plusYears(1);
            }
        }

        return nextReset.atZone(zoneId).toInstant().toEpochMilli();
    }
}

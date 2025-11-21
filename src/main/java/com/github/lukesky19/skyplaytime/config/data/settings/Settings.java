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
package com.github.lukesky19.skyplaytime.config.data.settings;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The plugin's settings configuration.
 * @param configVersion The config version of the file.
 * @param locale The locale file to use.
 * @param saveIntervalSeconds How often to save play time to the database.
 * @param backupOnReset Should the database be backed up when any play time category is reset?
 * @param leaderboardSnapshotOnReset Should the current top 10 players on the leaderboard be saved to a file when any play time category is reset?
 * @param backupsRemoveOlderThan The cut-off where older backups should be deleted for.
 * @param leaderboardRemoveOlderThan The cut-off where older leaderboard snapshots should be deleted for.
 * @param afkSettings The settings that apply to marking players as AFK.
 * @param resetSettings The settings for automatically resetting play time.
 * @param lastResetTimes These settings store the last time each play time category was last reset.
 */
@ConfigSerializable
public record Settings(
        @Nullable String configVersion,
        @Nullable String locale,
        int saveIntervalSeconds,
        boolean backupOnReset,
        boolean leaderboardSnapshotOnReset,
        @Nullable String backupsRemoveOlderThan,
        @Nullable String leaderboardRemoveOlderThan,
        @NotNull AfkSettings afkSettings,
        @NotNull ResetSettings resetSettings,
        @NotNull LastResetTimes lastResetTimes) {
    /**
     * The settings related to marking players as AFK.
     * @param autoAfkSeconds How many seconds should pass before a player is marked as AFK.
     * @param movementTimeSeconds How many seconds should pass along with their action time being below the value below before being marked AFK.
     * @param actionTimeSeconds How many seconds a player's action time should be below along with meeting or exceeding the movement time above before being marked AFK.
     * @param playerSettings Settings related to the player to be applied when they are marked as AFK.
     */
    @ConfigSerializable
    public record AfkSettings(
            int autoAfkSeconds,
            int movementTimeSeconds,
            int actionTimeSeconds,
            @NotNull PlayerSettings playerSettings) {}

    /**
     * The settings related directly to the player that can be applied when AFK.
     * @param afkItemPickup Should the player be able to pick up items while AFK?
     * @param afkInvulnerable Should the player be invulnerable while AFK?
     * @param afkSleeping Should the player be marked as sleeping while AFK?
     */
    @ConfigSerializable
    public record PlayerSettings(
            boolean afkItemPickup,
            boolean afkInvulnerable,
            boolean afkSleeping) {}

    /**
     * These settings are used to determine when play time categories should be reset.
     * @param zoneId The desired time zone.
     * @param dayOfWeek The day of week to reset weekly play time on.
     * @param resetHour What hour should play time categories be reset at.
     */
    @ConfigSerializable
    public record ResetSettings(
            String zoneId,
            String dayOfWeek,
            int resetHour) {}

    /**
     * This record stores the last time in milliseconds since the epoch that each play time category was reset at.
     * @param daily When was the daily play time category reset in milliseconds since the epoch.
     * @param weekly When was the weekly play time category reset in milliseconds since the epoch.
     * @param monthly When was the monthly play time category reset in milliseconds since the epoch.
     * @param yearly When was the yearly play time category reset in milliseconds since the epoch.
     */
    @ConfigSerializable
    public record LastResetTimes(
            long daily,
            long weekly,
            long monthly,
            long yearly) {}
}

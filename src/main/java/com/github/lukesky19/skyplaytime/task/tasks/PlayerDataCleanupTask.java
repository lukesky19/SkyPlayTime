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

import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import com.github.lukesky19.skyplaytime.settings.Settings;
import com.github.lukesky19.skyplaytime.settings.SettingsManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NonNull;

/**
 * This task removes old location snapshots at or older than the configured cutoff.
 */
public class PlayerDataCleanupTask extends BukkitRunnable {
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull PlayerDataManager playerDataManager;

    /**
     * Constructor
     * @param settingsManager A {@link SettingsManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     */
    public PlayerDataCleanupTask(
            @NonNull SettingsManager settingsManager,
            @NonNull PlayerDataManager playerDataManager) {
        this.settingsManager = settingsManager;
        this.playerDataManager = playerDataManager;
    }

    /**
     * For each player data, remove location snapshots at or older than the configured cutoff.
     */
    @Override
    public void run() {
        Settings settings = settingsManager.getSettings();
        if(settings == null || settings.maxLocationAgeSeconds() <= 0) return;
        long now = System.currentTimeMillis();
        long cutoff = (long) (settings.maxLocationAgeSeconds() * 1000);

        playerDataManager.getPlayerDataMap().forEach((_, playerData) ->
                playerData.getLocationList().removeIf(locationSnapshot ->
                        (now - locationSnapshot.timestamp()) >= cutoff));
    }
}
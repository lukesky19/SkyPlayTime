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

import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.config.data.settings.Settings;
import com.github.lukesky19.skyplaytime.config.manager.settings.SettingsManager;
import com.github.lukesky19.skyplaytime.player.manager.AFKManager;
import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NonNull;

/**
 * This task checks if a player should be marked afk or not.
 */
public class ActivityTask extends BukkitRunnable {
    private final @NonNull Server server;
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull PlayerDataManager playerDataManager;
    private final @NonNull AFKManager afkManager;

    /**
     * Constructor
     * @param skyPlayTime A {@link SkyPlayTime} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param afkManager An {@link AFKManager} instance.
     */
    public ActivityTask(
            @NonNull SkyPlayTime skyPlayTime,
            @NonNull SettingsManager settingsManager,
            @NonNull PlayerDataManager playerDataManager,
            @NonNull AFKManager afkManager) {
        this.server = skyPlayTime.getServer();
        this.settingsManager = settingsManager;
        this.playerDataManager = playerDataManager;
        this.afkManager = afkManager;
    }

    /**
     * For all active players, check if they should be marked as afk or not.
     */
    @Override
    public void run() {
        Settings settings = settingsManager.getSettings();
        if(settings == null) return;

        Settings.AfkSettings afkSettings = settings.afkSettings();
        boolean checkAutoAFK = afkSettings.autoAfkSeconds() >= 0;
        boolean checkAutomatedActions = afkSettings.movementTimeSeconds() >= 0 && afkSettings.actionTimeSeconds() >= 0;

        playerDataManager.getActivePlayerData().forEach((uuid, playerData) -> {
            Player player = server.getPlayer(uuid);
            if(player != null && player.isOnline() && player.isConnected()) {
                long moveTimeSeconds = (System.currentTimeMillis() - playerData.getLastMoveTime()) / 1000;
                long actionTimeSeconds = (System.currentTimeMillis() - playerData.getLastActionTime()) / 1000;

                if(checkAutoAFK) {
                    if (moveTimeSeconds >= settings.afkSettings().autoAfkSeconds() && actionTimeSeconds >= settings.afkSettings().autoAfkSeconds()) {
                        afkManager.togglePlayerAFK(player, uuid, true, true);
                    }
                }

                if(checkAutomatedActions) {
                    if (moveTimeSeconds >= settings.afkSettings().movementTimeSeconds() && actionTimeSeconds <= settings.afkSettings().actionTimeSeconds()) {
                        afkManager.togglePlayerAFK(player, uuid, true, true);
                    }
                }
            }
        });
    }
}
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
package com.github.lukesky19.skyplaytime.listener.connection;

import com.github.lukesky19.skyplaytime.algorithm.AlgorithmConfig;
import com.github.lukesky19.skyplaytime.algorithm.AlgorithmConfigManager;
import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jspecify.annotations.NonNull;

/**
 * This class listens to when a player logs in to initialize and update any data.
 */
public class LoginListener implements Listener {
    private final @NonNull AlgorithmConfigManager algorithmConfigManager;
    private final @NonNull PlayerDataManager playerDataManager;

    /**
     * Constructor
     * @param algorithmConfigManager An {@link AlgorithmConfigManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     */
    public LoginListener(
            @NonNull AlgorithmConfigManager algorithmConfigManager,
            @NonNull PlayerDataManager playerDataManager) {
        this.algorithmConfigManager = algorithmConfigManager;
        this.playerDataManager = playerDataManager;
    }

    /**
     * Initialize player data on join.
     * @param playerJoinEvent A {@link PlayerJoinEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent) {
        playerDataManager.loadPlayerData(playerJoinEvent.getPlayer()).thenAccept(playerData -> {
            if(playerData != null) {
                AlgorithmConfig algorithmConfig = algorithmConfigManager.getConfiguration();
                if(algorithmConfig != null) {
                    playerData.setGracePeriod((long) (System.currentTimeMillis() + (algorithmConfig.gracePeriodSeconds() * 1000)));
                }
            }
        });
    }
}
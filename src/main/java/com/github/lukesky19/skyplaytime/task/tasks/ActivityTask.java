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

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.algorithm.AlgorithmManager;
import com.github.lukesky19.skyplaytime.player.manager.AFKManager;
import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NonNull;

/**
 * This task checks if a player should be marked afk or not.
 */
public class ActivityTask extends BukkitRunnable {
    private final @NonNull ComponentLogger logger;
    private final @NonNull Server server;
    private final @NonNull PlayerDataManager playerDataManager;
    private final @NonNull AFKManager afkManager;
    private final @NonNull AlgorithmManager algorithmManager;

    /**
     * Constructor
     * @param skyPlayTime A {@link SkyPlayTime} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param afkManager An {@link AFKManager} instance.
     * @param algorithmManager An {@link AlgorithmManager} instance.
     */
    public ActivityTask(
            @NonNull SkyPlayTime skyPlayTime,
            @NonNull PlayerDataManager playerDataManager,
            @NonNull AFKManager afkManager,
            @NonNull AlgorithmManager algorithmManager) {
        this.logger = skyPlayTime.getComponentLogger();
        this.server = skyPlayTime.getServer();
        this.playerDataManager = playerDataManager;
        this.afkManager = afkManager;
        this.algorithmManager = algorithmManager;
    }

    /**
     * Check if active players should be marked AFK and if AFK players should be marked not AFK.
     */
    @Override
    public void run() {
        playerDataManager.getPlayerDataMap().forEach((uuid, playerData) -> {
            Player player = server.getPlayer(uuid);
            if(player != null && player.isOnline() && player.isConnected()) {
                if(playerData.isAFK()) {
                    if(!playerData.isPlayerInitiated()) {
                        boolean isActive = algorithmManager.getAlgorithmList().stream()
                                .noneMatch(algorithm -> algorithm.isPlayerInactive(player, playerData));

                        if(isActive) {
                            logger.info(AdventureUtility.deserialize("Toggling afk for player " + player.getName() + " as they are considered active by all algorithms."));

                            afkManager.togglePlayerAFK(player, true, true, false);
                        }
                    }
                } else {
                    if(!playerData.isGracePeriodActive()) {
                        boolean isInactive = algorithmManager.getAlgorithmList().stream()
                                .anyMatch(algorithm -> {
                                    boolean result = algorithm.isPlayerInactive(player, playerData);

                                    if (result) {
                                        logger.info(AdventureUtility.deserialize("Toggling afk for player " + player.getName() + " due to the " + algorithm.getName() + " algorithm."));
                                    }

                                    return result;
                                });

                        if(isInactive) {
                            afkManager.togglePlayerAFK(player, true, true, false);
                        }
                    }
                }
            }
        });
    }
}
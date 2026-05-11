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
import com.github.lukesky19.skyplaytime.event.PlayTimeGainedEvent;
import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NonNull;

/**
 * This task adds 1 second of play time to all active players.
 */
public class PlayTimeTask extends BukkitRunnable {
    private final @NonNull ComponentLogger logger;
    private final @NonNull Server server;
    private final @NonNull PluginManager pluginManager;
    private final @NonNull PlayerDataManager playerDataManager;

    /**
     * Constructor
     * @param skyPlayTime A {@link SkyPlayTime} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     */
    public PlayTimeTask(@NonNull SkyPlayTime skyPlayTime, @NonNull PlayerDataManager playerDataManager) {
        this.logger = skyPlayTime.getComponentLogger();
        this.server = skyPlayTime.getServer();
        this.pluginManager = server.getPluginManager();
        this.playerDataManager = playerDataManager;
    }

    /**
     * Add 1 second of play time to all active players.
     */
    @Override
    public void run() {
        playerDataManager.getActivePlayerData()
                .forEach((uuid, playerData) -> {
                    Player player = server.getPlayer(uuid);
                    if(player != null && player.isOnline() && player.isConnected()) {
                        playerData.addPlayTime(1);

                        PlayTimeGainedEvent playTimeGainedEvent = new PlayTimeGainedEvent(player);
                        pluginManager.callEvent(playTimeGainedEvent);
                    } else {
                        logger.warn(AdventureUtility.plain("There is player data loaded for a player that isn't online."));
                        logger.info(AdventureUtility.plain("The data will be unloaded to prevent play time from incrementing."));
                        playerDataManager.unloadPlayerData(uuid);
                    }
                });
    }
}
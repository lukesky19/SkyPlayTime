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
import com.github.lukesky19.skyplaytime.event.PlayTimeGainedEvent;
import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This task adds 1 second of play time to all active players.
 */
public class PlayTimeTask extends BukkitRunnable {
    private final @NotNull Server server;
    private final @NotNull PluginManager pluginManager;
    private final @NotNull PlayerDataManager playerDataManager;

    /**
     * Constructor
     * @param skyPlayTime A {@link SkyPlayTime} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     */
    public PlayTimeTask(@NotNull SkyPlayTime skyPlayTime, @NotNull PlayerDataManager playerDataManager) {
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
                    playerData.addPlayTime(1);

                    @Nullable Player player = server.getPlayer(uuid);
                    if(player != null && player.isOnline() && player.isConnected()) {
                        PlayTimeGainedEvent playTimeGainedEvent = new PlayTimeGainedEvent(player);
                        pluginManager.callEvent(playTimeGainedEvent);
                    }
                });
    }
}

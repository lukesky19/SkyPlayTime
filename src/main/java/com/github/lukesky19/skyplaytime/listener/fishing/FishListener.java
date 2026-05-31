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
package com.github.lukesky19.skyplaytime.listener.fishing;

import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.jspecify.annotations.NonNull;

/**
 * This class manages listening to fishing.
 */
public class FishListener implements Listener {
    private final @NonNull PlayerDataManager playerDataManager;

    /**
     * Constructor
     * @param playerDataManager A {@link PlayerDataManager} instance.
     */
    public FishListener(@NonNull PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    /**
     * Listens to when a player fishes and stores the timestamp of when they completed the action.
     * This method only considers an action completed for the following states: REEL_IN, FISHING, CAUGHT_FISH, and CAUGHT_ENTITY.
     * @param playerFishEvent A {@link PlayerFishEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent playerFishEvent) {
        playerDataManager.getPlayerData(playerFishEvent.getPlayer())
                .ifPresent(playerData -> {
                    switch(playerFishEvent.getState()) {
                        case FISHING -> playerData.setLastRodCast(System.currentTimeMillis());

                        case REEL_IN -> playerData.setLastRodReel(System.currentTimeMillis());

                        case CAUGHT_FISH, CAUGHT_ENTITY -> playerData.setLastRodCatch(System.currentTimeMillis());
                    }
                });
    }
}
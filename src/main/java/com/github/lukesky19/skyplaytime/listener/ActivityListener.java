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
package com.github.lukesky19.skyplaytime.listener;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.config.manager.settings.SettingsManager;
import com.github.lukesky19.skyplaytime.config.data.settings.Settings;
import com.github.lukesky19.skyplaytime.player.manager.AFKManager;
import com.github.lukesky19.skyplaytime.player.manager.ActivityManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * This class tracks player activity.
 */
public class ActivityListener implements Listener {
    private final @NotNull ComponentLogger logger;
    private final @NotNull SettingsManager settingsManager;
    private final @NotNull AFKManager afkManager;
    private final @NotNull ActivityManager activityManager;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param afkManager An {@link AFKManager} instance.
     * @param activityManager An {@link ActivityManager} instance.
     */
    public ActivityListener(
            @NotNull SkyPlayTime skyPlayTime,
            @NotNull SettingsManager settingsManager,
            @NotNull AFKManager afkManager,
            @NotNull ActivityManager activityManager) {
        this.logger = skyPlayTime.getComponentLogger();
        this.settingsManager = settingsManager;
        this.afkManager = afkManager;
        this.activityManager = activityManager;
    }

    /**
     * Listens to when a player moves and if they moved at least one block, store the timestamp of when they moved and removes them from being afk (if they are).
     * Also marks the player as no longer AFK if necessary.
     * @param playerMoveEvent A {@link PlayerMoveEvent}.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        Settings settings = settingsManager.getSettings();
        if(settings == null) {
            logger.warn(AdventureUtil.deserialize("Unable to process a PlayerMoveEvent due to invalid plugin settings."));
            return;
        }

        Player player = playerMoveEvent.getPlayer();
        UUID uuid = player.getUniqueId();
        Location from = playerMoveEvent.getFrom();
        Location to = playerMoveEvent.getTo();

        int fromX = from.getBlockX();
        int fromY = from.getBlockY();
        int fromZ = from.getBlockZ();

        int toX = to.getBlockX();
        int toY = to.getBlockY();
        int toZ = to.getBlockZ();

        // Only update player move time and add to recent locations if they moved a full block.
        if(fromX != toX || fromY != toY || fromZ != toZ) {
            activityManager.updateMoveTimeStamp(uuid);

            if(afkManager.isPlayerAFK(uuid)) {
                afkManager.togglePlayerAFK(player, uuid, true, true);
            }
        }
    }
    
    /**
     * Listens to when a player interacts in general and stores the timestamp of when they completed the action.
     * @param playerInteractEvent A {@link PlayerInteractEvent}.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent playerInteractEvent) {
        activityManager.updateActionTimeStamp(playerInteractEvent.getPlayer().getUniqueId());
    }

    /**
     * Listens to when a player interacts with an entity and stores the timestamp of when they completed the action.
     * @param playerInteractEntityEvent A {@link PlayerInteractEvent}.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent playerInteractEntityEvent) {
        activityManager.updateActionTimeStamp(playerInteractEntityEvent.getPlayer().getUniqueId());
    }

    /**
     * Listens to when a player fishes and stores the timestamp of when they completed the action.
     * This method only considers an action completed for the following states: REEL_IN, FISHING, CAUGHT_FISH, and CAUGHT_ENTITY.
     * @param playerFishEvent A {@link PlayerFishEvent}.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent playerFishEvent) {
        Player player = playerFishEvent.getPlayer();
        UUID uuid = player.getUniqueId();

        switch(playerFishEvent.getState()) {
            case REEL_IN, FISHING, CAUGHT_FISH, CAUGHT_ENTITY -> activityManager.updateActionTimeStamp(uuid);
        }
    }
}

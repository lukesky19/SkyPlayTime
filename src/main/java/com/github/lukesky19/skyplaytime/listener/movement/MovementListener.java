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
package com.github.lukesky19.skyplaytime.listener.movement;

import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import com.github.lukesky19.skyplaytime.player.manager.AFKManager;
import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import com.github.lukesky19.skyplaytime.settings.Settings;
import com.github.lukesky19.skyplaytime.settings.SettingsManager;
import com.github.lukesky19.skyplaytime.util.location.ImmutableLocation;
import com.github.lukesky19.skyplaytime.util.location.LocationSnapshot;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.jspecify.annotations.NonNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * This class manages listening to player movement.
 */
public class MovementListener implements Listener {
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull PlayerDataManager playerDataManager;
    private final @NonNull AFKManager afkManager;

    /**
     * Stores locations already processed as a result of a piston extending.
     * PlayerMoveEvent fires inconsistently for being moved by a piston,
     * so in the event a PlayerMoveEvent actually fires, we can ignore the event by checking this cache.
     * The Long is just the timestamp and is largely unused.
     */
    private final @NonNull Cache<ImmutableLocation, Long> ignoredLocations = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.SECONDS).build();

    /**
     * Constructor
     * @param settingsManager A {@link SettingsManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param afkManager An {@link AFKManager} instance.
     */
    public MovementListener(
            @NonNull SettingsManager settingsManager,
            @NonNull PlayerDataManager playerDataManager,
            @NonNull AFKManager afkManager) {
        this.settingsManager = settingsManager;
        this.playerDataManager = playerDataManager;
        this.afkManager = afkManager;
    }

    /**
     * Listens to when a player moves and stores data related to the event.
     * @param playerMoveEvent A {@link PlayerMoveEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent playerMoveEvent) {
        Settings settings = settingsManager.getSettings();
        if(settings == null) return;

        Player player = playerMoveEvent.getPlayer();
        UUID playerId = player.getUniqueId();
        PlayerData playerData = playerDataManager.getPlayerData(playerId).orElse(null);
        if(playerData == null) return;

        Location from = playerMoveEvent.getFrom();
        World fromWorld = from.getWorld();
        if(fromWorld == null) return;
        int fromX = from.getBlockX();
        int fromY = from.getBlockY();
        int fromZ = from.getBlockZ();

        Location to = playerMoveEvent.getTo();
        World toWorld = to.getWorld();
        if(toWorld == null) return;
        int toX = to.getBlockX();
        int toY = to.getBlockY();
        int toZ = to.getBlockZ();

        if(!fromWorld.getName().equals(toWorld.getName())) return;
        if(fromX == toX && fromY == toY && fromZ == toZ) return;

        ImmutableLocation immutableLocation = ImmutableLocation.fromBukkitLocation(to);
        // This section of code is required because being moved by a piston doesn't always fire the PlayerMoveEvent.
        // In the event it does, and the event is canceled, it is ideal to remove the most recent LocationSnapshot that matches the location.
        if(playerMoveEvent.isCancelled()) {
            if(ignoredLocations.getIfPresent(immutableLocation) != null) {
                ignoredLocations.invalidate(immutableLocation);

                playerData.removeMostRecentLocationSnapshot();
            }

            return;
        } else {
            // If this event is for a location already processed, we don't need to do anything.
            if(ignoredLocations.getIfPresent(immutableLocation) != null) {
                ignoredLocations.invalidate(immutableLocation);
                return;
            }
        }

        boolean inVehicle = player.isInsideVehicle();

        // Add the location snapshot
        playerData.addLocationSnapshot(
                new LocationSnapshot(toWorld, toX, toY, toZ, inVehicle, false),
                settings.maxLocationHistoryCount());

        // Update player move time
        playerData.setLastMoveTime(System.currentTimeMillis());

        // Toggle AFK is manually AFK
        if(playerData.isAFK() && playerData.isPlayerInitiated()) {
            afkManager.togglePlayerAFK(player, true, true, true);
        }
    }

    /**
     * Listens to a {@link BlockPistonExtendEvent} and marks any players affected by the piston.
     * @param blockPistonExtendEvent A {@link BlockPistonExtendEvent}.
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPistonExtend(BlockPistonExtendEvent blockPistonExtendEvent) {
        Settings settings = settingsManager.getSettings();
        if(settings == null) return;

        // Piston
        Block piston = blockPistonExtendEvent.getBlock();
        // Direction
        BlockFace face = blockPistonExtendEvent.getDirection();
        // Count of blocks moved by piston
        int count = blockPistonExtendEvent.getBlocks().size();

        // Starting Location (Piston)
        Location start = piston.getLocation();

        // XYZ modifiers
        int dx = face.getModX() != 0 ? (face.getModX() * count) + (face.getModX() > 0 ? 1 : -1) : 0;
        int dy = face.getModY() != 0 ? (face.getModY() * count) + (face.getModY() > 0 ? 1 : -1) : 0;
        int dz = face.getModZ() != 0 ? (face.getModZ() * count) + (face.getModZ() > 0 ? 1 : -1) : 0;

        // End Location
        Location end = start.clone().add(dx, dy, dz);

        // Get min and max XYZ coordinates
        int minX = Math.min(start.getBlockX(), end.getBlockX());
        int minY = Math.min(start.getBlockY(), end.getBlockY());
        int minZ = Math.min(start.getBlockZ(), end.getBlockZ());
        int maxX = Math.max(start.getBlockX(), end.getBlockX());
        int maxY = Math.max(start.getBlockY(), end.getBlockY());
        int maxZ = Math.max(start.getBlockZ(), end.getBlockZ());

        // Loop through players within the same world as the piston and mark the player if they are within the piston's area of effect.
        World pistonWorld = piston.getWorld();
        for(Player player : pistonWorld.getPlayers()) {
            UUID playerId = player.getUniqueId();
            PlayerData playerData = playerDataManager.getPlayerData(playerId).orElse(null);
            if(playerData == null) continue;
            Location playerLocation = player.getLocation();

            // Mark player as pushed by a piston if within the area affected by the piston.
            if(playerLocation.getBlockX() >= minX && playerLocation.getBlockX() <= maxX
                    && playerLocation.getBlockY() >= minY && playerLocation.getBlockY() <= maxY
                    && playerLocation.getBlockZ() >= minZ && playerLocation.getBlockZ() <= maxZ) {
                // Calculate the presumed location the player would be at.
                Location toLocation = playerLocation.clone().add(face.getDirection());

                // Add it to the list of ignored locations
                ignoredLocations.put(ImmutableLocation.fromBukkitLocation(toLocation), System.currentTimeMillis());

                // Add the location snapshot
                playerData.addLocationSnapshot(
                        new LocationSnapshot(
                                toLocation.getWorld(),
                                toLocation.getBlockX(),
                                toLocation.getBlockY(),
                                toLocation.getBlockZ(),
                                player.isInsideVehicle(),
                                true),
                        settings.maxLocationHistoryCount());
            }
        }
    }
}
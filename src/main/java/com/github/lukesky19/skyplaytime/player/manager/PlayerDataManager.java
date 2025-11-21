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
package com.github.lukesky19.skyplaytime.player.manager;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.database.DatabaseManager;
import com.github.lukesky19.skyplaytime.database.table.PlayTimeTable;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * This class manages all player data.
 */
public class PlayerDataManager {
    private final @NotNull ComponentLogger logger;
    private final @NotNull DatabaseManager databaseManager;
    private final @NotNull Map<@NotNull UUID, @NotNull PlayerData> playerDataMap = new HashMap<>();

    /**
     * Constructor
     * @param skyPlayTime A {@link SkyPlayTime} instance.
     * @param databaseManager A {@link DatabaseManager} instance.
     */
    public PlayerDataManager(
            @NotNull SkyPlayTime skyPlayTime,
            @NotNull DatabaseManager databaseManager) {
        this.logger = skyPlayTime.getComponentLogger();
        this.databaseManager = databaseManager;
    }

    /**
     * Get a {@link Map} mapping {@link UUID}s to {@link PlayerData} for all players.
     * @return A {@link Map} mapping {@link UUID}s to {@link PlayerData}.
     */
    public @NotNull Map<@NotNull UUID, @NotNull PlayerData> getPlayerDataMap() {
        return playerDataMap;
    }

    /**
     * Get the a {@link Map} mapping {@link UUID}s to {@link PlayerData} for all active players.
     * @return A {@link Map} mapping {@link UUID}s to {@link PlayerData}.
     */
    public @NotNull Map<UUID, PlayerData> getActivePlayerData() {
        return playerDataMap.entrySet()
                .stream()
                .filter(entry -> !entry.getValue().isAFK())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Get the {@link PlayerData} for the given {@link UUID}.
     * @param uuid The {@link UUID} of the player.
     * @return The {@link PlayerData} or null.
     */
    public @Nullable PlayerData getPlayerData(@NotNull UUID uuid) {
        return playerDataMap.get(uuid);
    }

    /**
     * Loads player data from the database.
     * @param player The {@link Player} to load data for.
     * @param uuid The {@link UUID} of the player to load data for.
     * @return A {@link CompletableFuture} of type {@link Void} when complete.
     */
    public @NotNull CompletableFuture<Void> loadPlayerData(@NotNull Player player, @NotNull UUID uuid) {
        PlayTimeTable playTimeTable = databaseManager.getPlayTimeTable();
        PlayerData playerData = playerDataMap.getOrDefault(uuid, new PlayerData(player.getName()));

        return playTimeTable.loadPlayerData(uuid, playerData)
                .thenAccept(updatedPlayerData -> {
                    // Store the player data
                    playerDataMap.put(uuid, updatedPlayerData);

                    // Save player data as the player name may have been updated.
                    savePlayerData(uuid, updatedPlayerData);
                })
                .exceptionally(ex -> {
                    logger.error(AdventureUtil.deserialize("Failed to load player data from the database."));
                    return null;
                });
    }

    /**
     * Saves the {@link PlayerData} for the player with the provided {@link UUID} to the database and then unloads it from memory.
     * @param uuid The {@link UUID} of the player.
     */
    public void unloadPlayerData(@NotNull UUID uuid) {
        @Nullable PlayerData playerData = getPlayerData(uuid);
        if(playerData == null) {
            logger.warn(AdventureUtil.deserialize("No player data to save and unload."));
            return;
        }

        databaseManager.getPlayTimeTable().savePlayerData(uuid, playerData)
                .thenAccept(v -> playerDataMap.remove(uuid))
                .exceptionally(ex -> {
                    playerDataMap.remove(uuid);
                    logger.error(AdventureUtil.deserialize("Failed to save player data to the database."));
                    return null;
                });
    }

    /**
     * Saves the {@link PlayerData} for the player with the provided {@link UUID} to the database.
     * @param uuid The {@link UUID} of the player.
     */
    public void savePlayerData(@NotNull UUID uuid) {
        @Nullable PlayerData playerData = getPlayerData(uuid);
        if(playerData == null) {
            logger.warn(AdventureUtil.deserialize("No player data to save."));
            CompletableFuture.completedFuture(null);
            return;
        }

        savePlayerData(uuid, playerData);
    }

    /**
     * Saves the {@link PlayerData} for the {@link UUID} provided.
     * @param uuid The {@link UUID} of the player.
     * @param playerData The {@link PlayerData} to save.
     */
    public void savePlayerData(@NotNull UUID uuid, @NotNull PlayerData playerData) {
        databaseManager.getPlayTimeTable().savePlayerData(uuid, playerData)
                .thenRun(() -> {
                })
                .exceptionally(t -> {
                    logger.error(AdventureUtil.deserialize("Failed to save player data to the database."));
                    return null;
                });
    }

    /**
     * Saves all loaded player data to the database.
     * @return A {@link CompletableFuture} containing a {@link List} of type {@link Boolean}.
     * If any player data fails to save, the list will contain a false result, otherwise true.
     */
    public @NotNull CompletableFuture<@NotNull List<@NotNull Boolean>> savePlayerData() {
        PlayTimeTable playTimeTable = databaseManager.getPlayTimeTable();
        return playTimeTable.savePlayerData(playerDataMap);
    }
}
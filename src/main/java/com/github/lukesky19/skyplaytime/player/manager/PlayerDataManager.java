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

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.database.DatabaseManager;
import com.github.lukesky19.skyplaytime.database.table.PlayTimeTable;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * This class manages all player data.
 */
public class PlayerDataManager {
    private final @NonNull ComponentLogger logger;
    private final @NonNull DatabaseManager databaseManager;
    private final @NonNull Map<UUID, PlayerData> playerDataMap = new HashMap<>();

    /**
     * Constructor
     * @param skyPlayTime A {@link SkyPlayTime} instance.
     * @param databaseManager A {@link DatabaseManager} instance.
     */
    public PlayerDataManager(
            @NonNull SkyPlayTime skyPlayTime,
            @NonNull DatabaseManager databaseManager) {
        this.logger = skyPlayTime.getComponentLogger();
        this.databaseManager = databaseManager;
    }

    /**
     * Get a {@link Map} mapping {@link UUID}s to {@link PlayerData} for all players.
     * @return A {@link Map} mapping {@link UUID}s to {@link PlayerData}.
     */
    public @NonNull Map<@NonNull UUID, @NonNull PlayerData> getPlayerDataMap() {
        return playerDataMap;
    }

    /**
     * Get the a {@link Map} mapping {@link UUID}s to {@link PlayerData} for all active players.
     * @return A {@link Map} mapping {@link UUID}s to {@link PlayerData}.
     */
    public @NonNull Map<UUID, PlayerData> getActivePlayerData() {
        return playerDataMap.entrySet()
                .stream()
                .filter(entry -> !entry.getValue().isAFK())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Get the {@link PlayerData} for the given {@link UUID} stored in memory.
     * @param player The {@link Player}.
     * @return The {@link Optional} {@link PlayerData}.
     */
    public @NonNull Optional<PlayerData> getPlayerData(@NonNull Player player) {
        return Optional.ofNullable(playerDataMap.get(player.getUniqueId()));
    }

    /**
     * Get the {@link PlayerData} for the given {@link UUID} stored in memory.
     * @param playerId The {@link UUID} of the player.
     * @return The {@link Optional} {@link PlayerData}.
     */
    public @NonNull Optional<PlayerData> getPlayerData(@NonNull UUID playerId) {
        return Optional.ofNullable(playerDataMap.get(playerId));
    }

    /**
     * Loads player data from the database.
     * @param player The {@link Player} to load data for.
     * @return A {@link CompletableFuture} of type {@link Optional} {@link PlayerData} when complete.
     */
    public @NonNull CompletableFuture<@Nullable PlayerData> loadPlayerData(@NonNull Player player) {
        UUID playerId = player.getUniqueId();
        PlayTimeTable playTimeTable = databaseManager.getPlayTimeTable();
        PlayerData playerData = playerDataMap.computeIfAbsent(playerId, _ -> new PlayerData(player.getName()));

        return playTimeTable.loadPlayerData(playerId, playerData).thenApply(_ -> {
            // Save player data as the player name may have been updated.
            savePlayerData(playerId, playerData);

            return playerData;
        }).exceptionally(_ -> {
            playerDataMap.remove(playerId);

            logger.warn(AdventureUtility.plain("Failed to load player data for player " + player.getName()));

            return null;
        });
    }

    /**
     * Saves the {@link PlayerData} for the player with the provided {@link UUID} to the database and then unloads it from memory.
     * @param uuid The {@link UUID} of the player.
     */
    public void unloadPlayerData(@NonNull UUID uuid) {
        PlayerData playerData = playerDataMap.get(uuid);
        if(playerData == null) {
            logger.warn(AdventureUtility.plain("No player data loaded to save and unload player id " + uuid + "."));
            return;
        }

        databaseManager.getPlayTimeTable().savePlayerData(uuid, playerData)
                .thenAccept(_ -> {
                    playerDataMap.remove(uuid);

                    logger.info(AdventureUtility.plain("Saved and unloaded player data for player " + playerData.getName()));
                })
                .exceptionally(_ -> {
                    playerDataMap.remove(uuid);
                    logger.warn(AdventureUtility.plain("Failed to save player data for player " + playerData.getName() + " to the database."));
                    return null;
                });
    }

    /**
     * Saves the {@link PlayerData} for the player with the provided {@link UUID} to the database.
     * @param uuid The {@link UUID} of the player.
     */
    public void savePlayerData(@NonNull UUID uuid) {
        PlayerData playerData = playerDataMap.get(uuid);
        if(playerData == null) {
            logger.warn(AdventureUtility.plain("No player data to save."));
            return;
        }

        savePlayerData(uuid, playerData);
    }

    /**
     * Saves the {@link PlayerData} for the {@link UUID} provided.
     * @param uuid The {@link UUID} of the player.
     * @param playerData The {@link PlayerData} to save.
     */
    public void savePlayerData(@NonNull UUID uuid, @NonNull PlayerData playerData) {
        databaseManager.getPlayTimeTable().savePlayerData(uuid, playerData)
                .exceptionally(_ -> {
                    logger.warn(AdventureUtility.plain("Failed to save player data to the database."));
                    return null;
                });
    }

    /**
     * Saves all loaded player data to the database.
     * @return A {@link CompletableFuture} containing a {@link List} of type {@link Boolean}.
     * If any player data fails to save, the list will contain a false result, otherwise true.
     */
    public @NonNull CompletableFuture<@NonNull List<@NonNull Boolean>> savePlayerData() {
        PlayTimeTable playTimeTable = databaseManager.getPlayTimeTable();
        return playTimeTable.savePlayerData(playerDataMap);
    }

    /**
     * Stores the {@link PlayerData} for the {@link UUID} provided.
     * @apiNote Only stores the data if the {@link UUID} doesn't have any data.
     * @apiNote For debugging purposes only.
     * @param playerId The player's {@link UUID}.
     * @param playerData The {@link PlayerData}.
     */
    protected void setPlayerData(@NonNull UUID playerId, @NonNull PlayerData playerData) {
        playerDataMap.putIfAbsent(playerId, playerData);
    }
}
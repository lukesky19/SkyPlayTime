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

import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.UUID;

/**
 * This class manages the retrieval and updating of data related to player activity.
 */
public class ActivityManager {
    private final @NonNull PlayerDataManager playerDataManager;

    /**
     * Constructor
     * @param playerDataManager A {@link PlayerDataManager} instance.
     */
    public ActivityManager(@NonNull PlayerDataManager playerDataManager) {
        this.playerDataManager = playerDataManager;
    }

    /**
     * Gets when a player last moved.
     * @param uuid The {@link UUID} of the player.
     * @return The last time they moved in milliseconds.
     */
    public long getLastMoveTime(@NonNull UUID uuid) {
        PlayerData playerData = playerDataManager.getPlayerData(uuid);
        if(playerData == null) throw new RuntimeException("No player data found for UUID " + uuid);

        return playerData.getLastMoveTime();
    }

    /**
     * Gets when a player last completed an action.
     * @param uuid The {@link UUID} of the player.
     * @return The last time they completed an action in milliseconds.
     */
    public long getLastActionTime(@NonNull UUID uuid) {
        PlayerData playerData = playerDataManager.getPlayerData(uuid);
        if(playerData == null) throw new RuntimeException("No player data found for UUID " + uuid);

        return playerData.getLastActionTime();
    }

    /**
     * Stores the current system time in milliseconds to the player's last move time.
     * @param player The {@link Player}.
     * @param uuid The {@link UUID} of the player.
     */
    public void updateMoveTimeStamp(@NonNull Player player, @NonNull UUID uuid) {
        PlayerData playerData = playerDataManager.getPlayerData(uuid);
        if(playerData == null) {
            playerDataManager.loadPlayerData(player, uuid)
                    .thenAccept(loadedPlayerData -> {
                        if(loadedPlayerData.isErrored()) return;

                        loadedPlayerData.setLastMoveTime(System.currentTimeMillis());
                    });
        } else {
            if(playerData.isErrored()) return;

            playerData.setLastMoveTime(System.currentTimeMillis());
        }
    }

    /**
     * Stores the current system time in milliseconds to the player's last action time.
     * @param player The {@link Player}.
     * @param uuid The {@link UUID} of the player.
     */
    public void updateActionTimeStamp(@NonNull Player player, @NonNull UUID uuid) {
        PlayerData playerData = playerDataManager.getPlayerData(uuid);
        if(playerData == null) {
            playerDataManager.loadPlayerData(player, uuid)
                    .thenAccept(loadedPlayerData -> {
                        if(loadedPlayerData.isErrored()) return;

                        loadedPlayerData.setLastActionTime(System.currentTimeMillis());
                    });
        } else {
            if(playerData.isErrored()) return;

            playerData.setLastActionTime(System.currentTimeMillis());
        }
    }
}
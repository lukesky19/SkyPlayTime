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
package com.github.lukesky19.skyplaytime.api.algorithm;

import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * This interface defines the contract to check if a player is active.
 */
public interface Algorithm {
    /**
     * Get the identifier of the algorithm.
     * @return The identifier.
     */
    @NonNull String getIdentifier();

    /**
     * Get the name of the algorithm.
     * @return The name.
     */
    @NonNull String getName();

    /**
     * Is the player considered inactive?
     * @param player The {@link Player}.
     * @param playerData The {@link PlayerData}.
     * @return true if inactive, false if not.
     */
    boolean isPlayerInactive(@NonNull Player player, @NonNull PlayerData playerData);
}
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
package com.github.lukesky19.skyplaytime.util;

/**
 * This enum contains the results of when a player's AFK status is toggled.
 */
public enum AFKToggleResult {
    /**
     * When the Player is successfully marked as AFK.
     */
    SUCCESS_AFK,
    /**
     * When the Player is successfully marked as no longer AFK
     */
    SUCCESS_NO_LONGER_AFK,
    /**
     * When the AFKStatusChangeEvent was cancelled.
     */
    CANCELLED,
    /**
     * When the plugin failed to toggle the player's AFK status due to a config error.
     */
    CONFIG_ERROR,
    /**
     *  When the plugin failed to toggle the player's AFK status due to any other error.
     */
    ERROR
}

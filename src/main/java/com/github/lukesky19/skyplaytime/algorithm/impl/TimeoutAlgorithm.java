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
package com.github.lukesky19.skyplaytime.algorithm.impl;

import com.github.lukesky19.skyplaytime.algorithm.AlgorithmConfig;
import com.github.lukesky19.skyplaytime.algorithm.AlgorithmConfigManager;
import com.github.lukesky19.skyplaytime.api.algorithm.Algorithm;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * This algorithm is for the classic afk timeout after X seconds. Time is configurable in settings though.
 */
public class TimeoutAlgorithm implements Algorithm {
    private final @NonNull AlgorithmConfigManager algorithmConfigManager;

    /**
     * Constructor
     * @param algorithmConfigManager An {@link AlgorithmConfigManager} instance.
     */
    public TimeoutAlgorithm(@NonNull AlgorithmConfigManager algorithmConfigManager) {
        this.algorithmConfigManager = algorithmConfigManager;
    }

    @Override
    public @NonNull String getIdentifier() {
        return "timeout";
    }

    @Override
    public @NonNull String getName() {
        return "Auto-AFK";
    }

    /**
     * Has the player not moved and not completed an action in the configured auto afk seconds?
     * Invalid settings will always return false.
     * @param player The {@link Player}.
     * @param playerData The {@link PlayerData}.
     * @return true if considered inactive, otherwise false.
     */
    @Override
    public boolean isPlayerInactive(@NonNull Player player, @NonNull PlayerData playerData) {
        // Configuration
        AlgorithmConfig algorithmConfig = algorithmConfigManager.getConfiguration();
        if(algorithmConfig == null) return false;
        AlgorithmConfig.TimeoutOptions timeoutOptions = algorithmConfig.timeoutOptions();
        if(timeoutOptions.disable()) return false;

        long now = System.currentTimeMillis();

        return (now - playerData.getLastMove()) >= (timeoutOptions.autoAfkSeconds() * 1000)
                && (now - playerData.getLastBlockBreak()) >= (timeoutOptions.autoAfkSeconds() * 1000)
                && (now - playerData.getLastBlockPlace()) >= (timeoutOptions.autoAfkSeconds() * 1000)
                && (now - playerData.getLastRodCast()) >= (timeoutOptions.autoAfkSeconds() * 1000)
                && (now - playerData.getLastRodCatch()) >= (timeoutOptions.autoAfkSeconds() * 1000)
                && (now - playerData.getLastRodReel()) >= (timeoutOptions.autoAfkSeconds() * 1000);
    }
}
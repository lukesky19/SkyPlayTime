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
 * This algorithm is for detecting players afk mining at a cobblestone generator.
 */
public class GeneratorAlgorithm implements Algorithm {
    private final @NonNull AlgorithmConfigManager algorithmConfigManager;

    /**
     * Constructor
     * @param algorithmConfigManager An {@link AlgorithmConfigManager} instance.
     */
    public GeneratorAlgorithm(@NonNull AlgorithmConfigManager algorithmConfigManager) {
        this.algorithmConfigManager = algorithmConfigManager;
    }

    @Override
    public @NonNull String getIdentifier() {
        return "cobblestone-generator";
    }

    @Override
    public @NonNull String getName() {
        return "Cobblestone Generator";
    }

    @Override
    public boolean isPlayerInactive(@NonNull Player player, @NonNull PlayerData playerData) {
        // Configuration
        AlgorithmConfig algorithmConfig = algorithmConfigManager.getConfiguration();
        if(algorithmConfig == null) return false;
        AlgorithmConfig.GeneratorOptions generatorOptions = algorithmConfig.generatorOptions();
        if(generatorOptions.disable()) return false;

        // Current time in milliseconds
        long now = System.currentTimeMillis();

        return (now - playerData.getLastMove()) >= (generatorOptions.movementTimeSeconds() * 1000)
                && (now - playerData.getLastBlockBreak()) <= (generatorOptions.actionTimeSeconds() * 1000);
    }
}
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
import com.github.lukesky19.skyplaytime.util.location.LocationSnapshot;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This algorithm is used to detect a player in an afk pool to bypass movement-based afk checks.
 */
public class AfkPoolAlgorithm implements Algorithm {
    private final @NonNull AlgorithmConfigManager algorithmConfigManager;

    /**
     * Constructor
     * @param algorithmConfigManager An {@link AlgorithmConfigManager} instance.
     */
    public AfkPoolAlgorithm(@NonNull AlgorithmConfigManager algorithmConfigManager) {
        this.algorithmConfigManager = algorithmConfigManager;
    }

    @Override
    public @NonNull String getIdentifier() {
        return "afk-pool";
    }

    @Override
    public @NonNull String getName() {
        return "AFK Pool";
    }

    @Override
    public boolean isPlayerInactive(@NonNull Player player, @NonNull PlayerData playerData) {
        // Configuration
        AlgorithmConfig algorithmConfig = algorithmConfigManager.getConfiguration();
        if(algorithmConfig == null) return false;
        AlgorithmConfig.LocationSimilarityOptions afkPoolOptions = algorithmConfig.afkPoolOptions();
        if(afkPoolOptions.disable()) return false;

        // Current time in milliseconds
        long now = System.currentTimeMillis();

        // Get locations from the last X seconds
        List<LocationSnapshot> locationList = playerData.getLocationList().stream()
                .filter(loc -> (now - loc.timestamp()) < (afkPoolOptions.locationHistorySeconds() * 1000))
                .filter(LocationSnapshot::isInWater)
                .toList();
        if(locationList.isEmpty()) return false;
        if(locationList.size() < afkPoolOptions.minLocationCount()) return false;

        // Group similar locations near water
        Map<LocationSnapshot, Integer> counts = new HashMap<>();
        for(LocationSnapshot loc : locationList) {
            LocationSnapshot match = null;
            for(LocationSnapshot key : counts.keySet()) {
                if(key.isSimilarByCoordinates(loc)) {
                    match = key;
                    break;
                }
            }

            if(match != null) {
                counts.merge(match, 1, Integer::sum);
            } else {
                counts.put(loc, 1);
            }
        }

        // Calculate total duplicates and similarity score
        long totalDuplicates = counts.values().stream().mapToLong(c -> c - 1).sum();
        double similarity = (double) totalDuplicates / locationList.size();

        return similarity >= afkPoolOptions.threshold();
    }
}
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
 * This algorithm is used to detect a player in a bubble column to bypass movement-based afk checks.
 */
public class BubbleColumnAlgorithm implements Algorithm {
    private final @NonNull AlgorithmConfigManager algorithmConfigManager;

    /**
     * Constructor
     * @param algorithmConfigManager An {@link AlgorithmConfigManager} instance.
     */
    public BubbleColumnAlgorithm(@NonNull AlgorithmConfigManager algorithmConfigManager) {
        this.algorithmConfigManager = algorithmConfigManager;
    }

    @Override
    public @NonNull String getIdentifier() {
        return "bubble-column";
    }

    @Override
    public @NonNull String getName() {
        return "Bubble Column";
    }

    @Override
    public boolean isPlayerInactive(@NonNull Player player, @NonNull PlayerData playerData) {
        // Configuration
        AlgorithmConfig algorithmConfig = algorithmConfigManager.getConfiguration();
        if(algorithmConfig == null) return false;
        AlgorithmConfig.LocationSimilarityOptions bubbleColumnOptions = algorithmConfig.bubbleColumnOptions();
        if(bubbleColumnOptions.disable()) return false;

        // Current time in milliseconds
        long now = System.currentTimeMillis();

        // Get locations from the last X seconds
        List<LocationSnapshot> locationList = playerData.getLocationList().stream()
                .filter(loc -> (now - loc.timestamp()) < (bubbleColumnOptions.locationHistorySeconds() * 1000))
                .filter(LocationSnapshot::isNearBubbleColumn)
                .toList();
        if(locationList.isEmpty()) return false;
        if(locationList.size() < bubbleColumnOptions.minLocationCount()) return false;

        // Group similar locations near bubble columns
        Map<Long, Integer> counts = new HashMap<>();
        for(LocationSnapshot loc : locationList) {
            counts.merge(loc.getCoordinatesAsLong(), 1, Integer::sum);
        }

        // Calculate total duplicates and similarity score
        long totalDuplicates = counts.values().stream().mapToLong(c -> c - 1).sum();
        double similarity = (double) totalDuplicates / locationList.size();

        return similarity >= bubbleColumnOptions.threshold();
    }
}
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
package com.github.lukesky19.skyplaytime.algorithm;

import com.github.lukesky19.skyplaytime.api.algorithm.Algorithm;
import org.jspecify.annotations.NonNull;

import java.util.*;

/**
 * This class manages registered algorithms to determine whether the player is afk or not.
 */
public class AlgorithmManager {
    /**
     * The map of algorithms.
     */
    private final @NonNull Map<String, Algorithm> algorithmMap = new HashMap<>();
    /**
     * The list of algorithms.
     */
    private final @NonNull List<Algorithm> algorithmList = new ArrayList<>();

    /**
     * Constructor
     */
    public AlgorithmManager() {}

    /**
     * Add an algorithm.
     * @param algorithm The algorithm.
     * @return true if successful, false if not.
     */
    public boolean addAlgorithm(@NonNull Algorithm algorithm) {
        if(algorithm.getIdentifier().isEmpty()) return false;
        if(algorithmMap.containsKey(algorithm.getIdentifier())) return false;

        algorithmMap.put(algorithm.getIdentifier(), algorithm);
        updateList();

        return true;
    }

    /**
     * Remove an algorithm.
     * @param algorithm The algorithm.
     * @return true if successful, false if not.
     */
    public boolean removeAlgorithm(@NonNull Algorithm algorithm) {
        if(algorithm.getIdentifier().isEmpty()) return false;
        if(!algorithmMap.containsKey(algorithm.getIdentifier())) return false;

        algorithmMap.remove(algorithm.getIdentifier());
        updateList();

        return true;
    }

    /**
     * Get the list of algorithms.
     * @return The list of algorithms.
     */
    public @NonNull List<Algorithm> getAlgorithmList() {
        return algorithmList;
    }

    /**
     * Update the list of algorithms.
     */
    private void updateList() {
        algorithmList.clear();
        algorithmList.addAll(algorithmMap.values().stream().toList());
    }
}
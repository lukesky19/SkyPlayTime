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

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import org.jspecify.annotations.NonNull;

/**
 * This record contains the configuration for the algorithms to determine if a player is AFK or not.
 * @param version The config version.
 * @param gracePeriodSeconds The grace period applied when a player is no longer AFK.
 * @param afkPoolOptions The options for the afk pool algorithm.
 * @param bubbleColumnOptions The options for the bubble column algorithm.
 * @param fishingOptions The options for the fishing algorithm.
 * @param generatorOptions The options for the generator algorithm.
 * @param pistonOptions The options for the piston algorithm.
 * @param timeoutOptions The options for the timeout algorithm.
 */
@ConfigSerializable
public record AlgorithmConfig(
        int version,
        double gracePeriodSeconds,
        @NonNull LocationSimilarityOptions afkPoolOptions,
        @NonNull LocationSimilarityOptions bubbleColumnOptions,
        @NonNull FishingOptions fishingOptions,
        @NonNull GeneratorOptions generatorOptions,
        @NonNull LocationSimilarityOptions pistonOptions,
        @NonNull TimeoutOptions timeoutOptions) {
    /**
     * This record contains the configuration used for algorithms based on location similarity.
     * @param disable Is the algorithm disabled?
     * @param locationHistorySeconds The seconds used to get the location snapshots that occurred up to this number of seconds ago.
     * @param minLocationCount The minimum location account required to calculate whether the player has low movement.
     * @param threshold The threshold to consider the player AFK at and above. This value should be a value between or exactly 0 and 1.
     */
    @ConfigSerializable
    public record LocationSimilarityOptions(
            boolean disable,
            double locationHistorySeconds,
            int minLocationCount,
            double threshold) {}

    /**
     * This record contains the configuration used for the fishing algorithm.
     * @param disable Is the algorithm disabled?
     * @param locationHistorySeconds The seconds used to get the location snapshots that occurred up to this number of seconds ago.
     * @param minLocationCount The minimum location account required to calculate whether the player has low movement.
     * @param threshold The threshold to consider the player AFK at and above. This value should be a value between or exactly 0 and 1.
     */
    @ConfigSerializable
    public record FishingOptions(
            boolean disable,
            double locationHistorySeconds,
            int minLocationCount,
            double threshold) {}

    /**
     * This record contains the configuration used for the cobblestone generator algorithm.
     * @param disable Is the algorithm disabled?
     * @param movementTimeSeconds The movement time threshold to consider the player AFK at.
     * @param actionTimeSeconds The action time threshold to consider the player AFK at.
     */
    @ConfigSerializable
    public record GeneratorOptions(
            boolean disable,
            double movementTimeSeconds,
            double actionTimeSeconds) {}

    /**
     * This record contains the configuration for when a player has done nothing and should be marked AFK after this amount of time.
     * @param disable Is the algorithm disabled?
     * @param autoAfkSeconds The time in seconds to mark a player AFK of no action(s).
     */
    @ConfigSerializable
    public record TimeoutOptions(
            boolean disable,
            double autoAfkSeconds) {}
}
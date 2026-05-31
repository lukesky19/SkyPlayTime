package com.github.lukesky19.skyplaytime.algorithm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * This class tests {@link AlgorithmConfig}.
 */
@ExtendWith(MockitoExtension.class)
public class AlgorithmConfigTest {
    /**
     * Test the creation of {@link AlgorithmConfig}.
     */
    @Test
    public void testCreateAlgorithmConfig() {
        AlgorithmConfig algorithmConfig = new AlgorithmConfig(
                1,
                120,
                new AlgorithmConfig.LocationSimilarityOptions(
                        false,
                        180,
                        8,
                        0.6),
                new AlgorithmConfig.LocationSimilarityOptions(
                        false,
                        3.5,
                        8,
                        0.75),
                new AlgorithmConfig.FishingOptions(
                        false,
                        10,
                        8,
                        0.75),
                new AlgorithmConfig.GeneratorOptions(
                        false,
                        60,
                        30),
                new AlgorithmConfig.LocationSimilarityOptions(
                        false,
                        15,
                        8,
                        0.75),
                new AlgorithmConfig.TimeoutOptions(
                        false,
                        300));

        assertNotNull(algorithmConfig);
    }
}
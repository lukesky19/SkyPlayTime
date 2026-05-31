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
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * This class tests {@link GeneratorAlgorithm}.
 */
@ExtendWith(MockitoExtension.class)
public class GeneratorAlgorithmTest {
    @Mock
    private AlgorithmConfigManager algorithmConfigManager;
    @Mock
    private AlgorithmConfig algorithmConfig;
    @Mock
    private AlgorithmConfig.GeneratorOptions generatorOptions;

    @Mock
    private Player player;
    @Mock
    private PlayerData playerData;

    private GeneratorAlgorithm generatorAlgorithm;

    /**
     * Setup data for each test.
     */
    @BeforeEach
    public void setup() {
        generatorAlgorithm = new GeneratorAlgorithm(algorithmConfigManager);
    }

    /**
     * Test {@link GeneratorAlgorithm#getIdentifier()}.
     */
    @Test
    public void testGetIdentifier() {
        assertEquals("cobblestone-generator", generatorAlgorithm.getIdentifier());
    }

    /**
     * Test {@link GeneratorAlgorithm#getName()}.
     */
    @Test
    public void testGetName() {
        assertEquals("Cobblestone Generator", generatorAlgorithm.getName());
    }

    /**
     * Test {@link GeneratorAlgorithm#isPlayerInactive(Player, PlayerData)} where the player is active by move time.
     */
    @Test
    public void testIsPlayerInactiveActiveByMoveTime() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(algorithmConfig);
        when(algorithmConfig.generatorOptions()).thenReturn(generatorOptions);
        when(generatorOptions.disable()).thenReturn(false);

        when(generatorOptions.movementTimeSeconds()).thenReturn(60.0);

        when(playerData.getLastMove()).thenReturn(System.currentTimeMillis());

        assertFalse(generatorAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Test {@link GeneratorAlgorithm#isPlayerInactive(Player, PlayerData)} where the player is active by action time.
     */
    @Test
    public void testIsPlayerInactiveActiveByActionTime() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(algorithmConfig);
        when(algorithmConfig.generatorOptions()).thenReturn(generatorOptions);
        when(generatorOptions.disable()).thenReturn(false);

        when(generatorOptions.movementTimeSeconds()).thenReturn(60.0);
        when(generatorOptions.actionTimeSeconds()).thenReturn(30.0);

        when(playerData.getLastMove()).thenReturn(System.currentTimeMillis() - (1000 * 65));
        when(playerData.getLastBlockBreak()).thenReturn(System.currentTimeMillis() - (1000 * 35));

        assertFalse(generatorAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Test {@link GeneratorAlgorithm#isPlayerInactive(Player, PlayerData)} where the player is inactive.
     */
    @Test
    public void testIsPlayerInactiveInactive() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(algorithmConfig);
        when(algorithmConfig.generatorOptions()).thenReturn(generatorOptions);
        when(generatorOptions.disable()).thenReturn(false);

        when(generatorOptions.movementTimeSeconds()).thenReturn(60.0);
        when(generatorOptions.actionTimeSeconds()).thenReturn(30.0);

        when(playerData.getLastMove()).thenReturn(System.currentTimeMillis() - (1000 * 65));
        when(playerData.getLastBlockBreak()).thenReturn(System.currentTimeMillis());

        assertTrue(generatorAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Test {@link GeneratorAlgorithm#isPlayerInactive(Player, PlayerData)}, but the configuration is invalid.
     */
    @Test
    public void testIsPlayerInactiveInvalidConfiguration() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(null);

        assertFalse(generatorAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Test {@link GeneratorAlgorithm#isPlayerInactive(Player, PlayerData)}, but the algorithm is disabled.
     */
    @Test
    public void testIsPlayerInactiveAlgorithmDisabled() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(algorithmConfig);
        when(algorithmConfig.generatorOptions()).thenReturn(generatorOptions);
        when(generatorOptions.disable()).thenReturn(true);

        assertFalse(generatorAlgorithm.isPlayerInactive(player, playerData));
    }
}
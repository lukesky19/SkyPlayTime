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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

/**
 * This class tests {@link TimeoutAlgorithm}.
 */
@ExtendWith(MockitoExtension.class)
public class TimeoutAlgorithmTest {
    @Mock
    private AlgorithmConfigManager algorithmConfigManager;
    @Mock
    private AlgorithmConfig algorithmConfig;
    @Mock
    private AlgorithmConfig.TimeoutOptions timeoutOptions;

    @Mock
    private Player player;
    @Mock
    private PlayerData playerData;

    private TimeoutAlgorithm timeoutAlgorithm;

    /**
     * Setup data for each test.
     */
    @BeforeEach
    public void setup() {
        timeoutAlgorithm = new TimeoutAlgorithm(algorithmConfigManager);
    }

    /**
     * Test {@link TimeoutAlgorithm#getIdentifier()}.
     */
    @Test
    public void testGetIdentifier() {
        assertEquals("timeout", timeoutAlgorithm.getIdentifier());
    }

    /**
     * Test {@link TimeoutAlgorithm#getName()}.
     */
    @Test
    public void testGetName() {
        assertEquals("Auto-AFK", timeoutAlgorithm.getName());
    }

    /**
     * Test {@link TimeoutAlgorithm#isPlayerInactive(Player, PlayerData)} where the player is active by move time.
     */
    @Test
    public void testIsPlayerInactiveActiveByMoveTime() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(algorithmConfig);
        when(algorithmConfig.timeoutOptions()).thenReturn(timeoutOptions);
        when(timeoutOptions.disable()).thenReturn(false);

        when(timeoutOptions.autoAfkSeconds()).thenReturn(300.0);

        when(playerData.getLastMove()).thenReturn(System.currentTimeMillis());

        assertFalse(timeoutAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Test {@link TimeoutAlgorithm#isPlayerInactive(Player, PlayerData)} where the player is active by block break time.
     */
    @Test
    public void testIsPlayerInactiveActiveByBlockBreakTime() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(algorithmConfig);
        when(algorithmConfig.timeoutOptions()).thenReturn(timeoutOptions);
        when(timeoutOptions.disable()).thenReturn(false);

        when(timeoutOptions.autoAfkSeconds()).thenReturn(300.0);

        when(playerData.getLastMove()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastBlockBreak()).thenReturn(System.currentTimeMillis());

        assertFalse(timeoutAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Test {@link TimeoutAlgorithm#isPlayerInactive(Player, PlayerData)} where the player is active by block place time.
     */
    @Test
    public void testIsPlayerInactiveActiveByBlockPlaceTime() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(algorithmConfig);
        when(algorithmConfig.timeoutOptions()).thenReturn(timeoutOptions);
        when(timeoutOptions.disable()).thenReturn(false);

        when(timeoutOptions.autoAfkSeconds()).thenReturn(300.0);

        when(playerData.getLastMove()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastBlockBreak()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastBlockPlace()).thenReturn(System.currentTimeMillis());

        assertFalse(timeoutAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Test {@link TimeoutAlgorithm#isPlayerInactive(Player, PlayerData)} where the player is active by rod cast time.
     */
    @Test
    public void testIsPlayerInactiveActiveByRodCastTime() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(algorithmConfig);
        when(algorithmConfig.timeoutOptions()).thenReturn(timeoutOptions);
        when(timeoutOptions.disable()).thenReturn(false);

        when(timeoutOptions.autoAfkSeconds()).thenReturn(300.0);

        when(playerData.getLastMove()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastBlockBreak()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastBlockPlace()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastRodCast()).thenReturn(System.currentTimeMillis());

        assertFalse(timeoutAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Test {@link TimeoutAlgorithm#isPlayerInactive(Player, PlayerData)} where the player is active by rod catch time.
     */
    @Test
    public void testIsPlayerInactiveActiveByRodCatchTime() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(algorithmConfig);
        when(algorithmConfig.timeoutOptions()).thenReturn(timeoutOptions);
        when(timeoutOptions.disable()).thenReturn(false);

        when(timeoutOptions.autoAfkSeconds()).thenReturn(300.0);

        when(playerData.getLastMove()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastBlockBreak()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastBlockPlace()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastRodCast()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastRodCatch()).thenReturn(System.currentTimeMillis());

        assertFalse(timeoutAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Test {@link TimeoutAlgorithm#isPlayerInactive(Player, PlayerData)} where the player is active by rod reel time.
     */
    @Test
    public void testIsPlayerInactiveActiveByRodReelTime() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(algorithmConfig);
        when(algorithmConfig.timeoutOptions()).thenReturn(timeoutOptions);
        when(timeoutOptions.disable()).thenReturn(false);

        when(timeoutOptions.autoAfkSeconds()).thenReturn(300.0);

        when(playerData.getLastMove()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastBlockBreak()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastBlockPlace()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastRodCast()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastRodCatch()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastRodReel()).thenReturn(System.currentTimeMillis());

        assertFalse(timeoutAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Test {@link TimeoutAlgorithm#isPlayerInactive(Player, PlayerData)} where the player is inactive.
     */
    @Test
    public void testIsPlayerInactiveInactive() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(algorithmConfig);
        when(algorithmConfig.timeoutOptions()).thenReturn(timeoutOptions);
        when(timeoutOptions.disable()).thenReturn(false);

        when(timeoutOptions.autoAfkSeconds()).thenReturn(300.0);

        when(playerData.getLastMove()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastBlockBreak()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastBlockPlace()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastRodCast()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastRodCatch()).thenReturn(System.currentTimeMillis() - (1000 * 305));
        when(playerData.getLastRodReel()).thenReturn(System.currentTimeMillis() - (1000 * 305));

        assertTrue(timeoutAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Test {@link TimeoutAlgorithm#isPlayerInactive(Player, PlayerData)}, but the configuration is invalid.
     */
    @Test
    public void testIsPlayerInactiveInvalidConfiguration() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(null);

        assertFalse(timeoutAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Test {@link TimeoutAlgorithm#isPlayerInactive(Player, PlayerData)}, but the algorithm is disabled.
     */
    @Test
    public void testIsPlayerInactiveAlgorithmDisabled() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(algorithmConfig);
        when(algorithmConfig.timeoutOptions()).thenReturn(timeoutOptions);
        when(timeoutOptions.disable()).thenReturn(true);

        assertFalse(timeoutAlgorithm.isPlayerInactive(player, playerData));
    }
}
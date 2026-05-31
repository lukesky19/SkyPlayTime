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
import com.github.lukesky19.skyplaytime.util.location.LocationSnapshot;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * This class tests {@link BubbleColumnAlgorithm}.
 */
@ExtendWith(MockitoExtension.class)
public class BubbleColumnAlgorithmTest {
    @Mock
    private AlgorithmConfigManager algorithmConfigManager;
    @Mock
    private AlgorithmConfig algorithmConfig;
    @Mock
    private AlgorithmConfig.LocationSimilarityOptions bubbleColumnOptions;

    @Mock
    private Player player;
    @Mock
    private PlayerData playerData;

    @Mock
    private LocationSnapshot snapshot1;
    @Mock
    private LocationSnapshot snapshot2;
    @Mock
    private LocationSnapshot snapshot3;
    @Mock
    private LocationSnapshot snapshot4;
    @Mock
    private LocationSnapshot snapshot5;
    @Mock
    private LocationSnapshot snapshot6;
    @Mock
    private LocationSnapshot snapshot7;
    @Mock
    private LocationSnapshot snapshot8;
    @Mock
    private LocationSnapshot snapshot9;
    @Mock
    private LocationSnapshot snapshot10;

    private BubbleColumnAlgorithm bubbleColumnAlgorithm;

    /**
     * Setup data for each test.
     */
    @BeforeEach
    public void setup() {
        bubbleColumnAlgorithm = new BubbleColumnAlgorithm(algorithmConfigManager);
    }

    /**
     * Test {@link BubbleColumnAlgorithm#getIdentifier()}.
     */
    @Test
    public void testGetIdentifier() {
        assertEquals("bubble-column", bubbleColumnAlgorithm.getIdentifier());
    }

    /**
     * Test {@link BubbleColumnAlgorithm#getName()}.
     */
    @Test
    public void testGetName() {
        assertEquals("Bubble Column", bubbleColumnAlgorithm.getName());
    }

    /**
     * Test {@link BubbleColumnAlgorithm#isPlayerInactive(Player, PlayerData)} where the player is active.
     */
    @Test
    public void testIsPlayerInactiveActive() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(algorithmConfig);
        when(algorithmConfig.bubbleColumnOptions()).thenReturn(bubbleColumnOptions);
        when(bubbleColumnOptions.disable()).thenReturn(false);
        when(bubbleColumnOptions.locationHistorySeconds()).thenReturn(3.5);
        when(bubbleColumnOptions.minLocationCount()).thenReturn(8);
        when(bubbleColumnOptions.threshold()).thenReturn(0.75);

        when(snapshot1.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot2.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot3.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot4.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot5.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot6.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot7.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot8.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot9.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot10.timestamp()).thenReturn(System.currentTimeMillis());

        when(snapshot1.isNearBubbleColumn()).thenReturn(true);
        when(snapshot2.isNearBubbleColumn()).thenReturn(true);
        when(snapshot3.isNearBubbleColumn()).thenReturn(true);
        when(snapshot4.isNearBubbleColumn()).thenReturn(true);
        when(snapshot5.isNearBubbleColumn()).thenReturn(true);
        when(snapshot6.isNearBubbleColumn()).thenReturn(true);
        when(snapshot7.isNearBubbleColumn()).thenReturn(true);
        when(snapshot8.isNearBubbleColumn()).thenReturn(true);
        when(snapshot9.isNearBubbleColumn()).thenReturn(true);
        when(snapshot10.isNearBubbleColumn()).thenReturn(true);

        when(snapshot1.getCoordinatesAsLong()).thenReturn(10L);
        when(snapshot2.getCoordinatesAsLong()).thenReturn(20L);
        when(snapshot3.getCoordinatesAsLong()).thenReturn(30L);
        when(snapshot4.getCoordinatesAsLong()).thenReturn(40L);
        when(snapshot5.getCoordinatesAsLong()).thenReturn(50L);
        when(snapshot6.getCoordinatesAsLong()).thenReturn(60L);
        when(snapshot7.getCoordinatesAsLong()).thenReturn(70L);
        when(snapshot8.getCoordinatesAsLong()).thenReturn(80L);
        when(snapshot9.getCoordinatesAsLong()).thenReturn(90L);
        when(snapshot10.getCoordinatesAsLong()).thenReturn(100L);

        List<LocationSnapshot> list = createLocationSnapshotList();
        assertEquals(10, list.size());
        when(playerData.getLocationList()).thenReturn(list);

        assertFalse(bubbleColumnAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Test {@link BubbleColumnAlgorithm#isPlayerInactive(Player, PlayerData)} where the player is inactive.
     */
    @Test
    public void testIsPlayerInactiveInactive() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(algorithmConfig);
        when(algorithmConfig.bubbleColumnOptions()).thenReturn(bubbleColumnOptions);
        when(bubbleColumnOptions.disable()).thenReturn(false);
        when(bubbleColumnOptions.locationHistorySeconds()).thenReturn(3.5);
        when(bubbleColumnOptions.minLocationCount()).thenReturn(8);
        when(bubbleColumnOptions.threshold()).thenReturn(0.75);

        when(snapshot1.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot2.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot3.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot4.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot5.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot6.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot7.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot8.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot9.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot10.timestamp()).thenReturn(System.currentTimeMillis());

        when(snapshot1.isNearBubbleColumn()).thenReturn(true);
        when(snapshot2.isNearBubbleColumn()).thenReturn(true);
        when(snapshot3.isNearBubbleColumn()).thenReturn(true);
        when(snapshot4.isNearBubbleColumn()).thenReturn(true);
        when(snapshot5.isNearBubbleColumn()).thenReturn(true);
        when(snapshot6.isNearBubbleColumn()).thenReturn(true);
        when(snapshot7.isNearBubbleColumn()).thenReturn(true);
        when(snapshot8.isNearBubbleColumn()).thenReturn(true);
        when(snapshot9.isNearBubbleColumn()).thenReturn(true);
        when(snapshot10.isNearBubbleColumn()).thenReturn(true);

        when(snapshot1.getCoordinatesAsLong()).thenReturn(10L);
        when(snapshot2.getCoordinatesAsLong()).thenReturn(10L);
        when(snapshot3.getCoordinatesAsLong()).thenReturn(10L);
        when(snapshot4.getCoordinatesAsLong()).thenReturn(10L);
        when(snapshot5.getCoordinatesAsLong()).thenReturn(10L);
        when(snapshot6.getCoordinatesAsLong()).thenReturn(10L);
        when(snapshot7.getCoordinatesAsLong()).thenReturn(10L);
        when(snapshot8.getCoordinatesAsLong()).thenReturn(10L);
        when(snapshot9.getCoordinatesAsLong()).thenReturn(10L);
        when(snapshot10.getCoordinatesAsLong()).thenReturn(10L);

        List<LocationSnapshot> list = createLocationSnapshotList();
        assertEquals(10, list.size());
        when(playerData.getLocationList()).thenReturn(list);

        assertTrue(bubbleColumnAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Test {@link BubbleColumnAlgorithm#isPlayerInactive(Player, PlayerData)}, but the configuration is invalid.
     */
    @Test
    public void testIsPlayerInactiveInvalidConfiguration() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(null);

        assertFalse(bubbleColumnAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Test {@link BubbleColumnAlgorithm#isPlayerInactive(Player, PlayerData)}, but the algorithm is disabled.
     */
    @Test
    public void testIsPlayerInactiveAlgorithmDisabled() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(algorithmConfig);
        when(algorithmConfig.bubbleColumnOptions()).thenReturn(bubbleColumnOptions);
        when(bubbleColumnOptions.disable()).thenReturn(true);

        assertFalse(bubbleColumnAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Test {@link BubbleColumnAlgorithm#isPlayerInactive(Player, PlayerData)}, but the location list is empty.
     */
    @Test
    public void testIsPlayerInactiveEmptyLocationList() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(algorithmConfig);
        when(algorithmConfig.bubbleColumnOptions()).thenReturn(bubbleColumnOptions);
        when(bubbleColumnOptions.disable()).thenReturn(false);

        when(playerData.getLocationList()).thenReturn(new ArrayList<>());

        assertFalse(bubbleColumnAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Test {@link BubbleColumnAlgorithm#isPlayerInactive(Player, PlayerData)}, but the location list size is below the minimum count.
     */
    @Test
    public void testIsPlayerInactiveBelowMinLocationCount() {
        when(algorithmConfigManager.getConfiguration()).thenReturn(algorithmConfig);
        when(algorithmConfig.bubbleColumnOptions()).thenReturn(bubbleColumnOptions);

        when(bubbleColumnOptions.disable()).thenReturn(false);
        when(bubbleColumnOptions.locationHistorySeconds()).thenReturn(3.5);
        when(bubbleColumnOptions.minLocationCount()).thenReturn(8);

        List<LocationSnapshot> list = createLocationSnapshotList();
        assertEquals(10, list.size());

        when(snapshot1.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot1.isNearBubbleColumn()).thenReturn(true);
        when(snapshot2.timestamp()).thenReturn(System.currentTimeMillis() - (30 * 1000));
        when(snapshot3.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot3.isNearBubbleColumn()).thenReturn(true);
        when(snapshot4.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot4.isNearBubbleColumn()).thenReturn(true);
        when(snapshot5.timestamp()).thenReturn(System.currentTimeMillis() - (60 * 1000));
        when(snapshot6.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot6.isNearBubbleColumn()).thenReturn(true);
        when(snapshot7.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot7.isNearBubbleColumn()).thenReturn(false);
        when(snapshot8.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot8.isNearBubbleColumn()).thenReturn(true);
        when(snapshot9.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot9.isNearBubbleColumn()).thenReturn(true);
        when(snapshot10.timestamp()).thenReturn(System.currentTimeMillis());
        when(snapshot10.isNearBubbleColumn()).thenReturn(false);

        when(playerData.getLocationList()).thenReturn(list);

        assertFalse(bubbleColumnAlgorithm.isPlayerInactive(player, playerData));
    }

    /**
     * Create the {@link List} of {@link LocationSnapshot} for testing purposes.
     * @return The {@link List} of {@link LocationSnapshot}.
     */
    private @NonNull List<LocationSnapshot> createLocationSnapshotList() {
        List<LocationSnapshot> locationSnapshotList = new ArrayList<>();

        locationSnapshotList.add(snapshot1);
        locationSnapshotList.add(snapshot2);
        locationSnapshotList.add(snapshot3);
        locationSnapshotList.add(snapshot4);
        locationSnapshotList.add(snapshot5);
        locationSnapshotList.add(snapshot6);
        locationSnapshotList.add(snapshot7);
        locationSnapshotList.add(snapshot8);
        locationSnapshotList.add(snapshot9);
        locationSnapshotList.add(snapshot10);

        return locationSnapshotList;
    }
}
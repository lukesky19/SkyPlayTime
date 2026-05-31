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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * This class tests {@link AlgorithmManager}.
 */
@ExtendWith(MockitoExtension.class)
public class AlgorithmManagerTest {
    @Mock
    private Algorithm algorithm;

    /**
     * Test {@link AlgorithmManager#addAlgorithm(Algorithm)}.
     */
    @Test
    public void testAddAlgorithm() {
        AlgorithmManager algorithmManager = new AlgorithmManager();

        when(algorithm.getIdentifier()).thenReturn("test");

        assertTrue(algorithmManager.addAlgorithm(algorithm));
    }

    /**
     * Test {@link AlgorithmManager#addAlgorithm(Algorithm)}, but the identifier is invalid.
     */
    @Test
    public void testAddAlgorithmInvalidIdentifier() {
        AlgorithmManager algorithmManager = new AlgorithmManager();

        when(algorithm.getIdentifier()).thenReturn("");

        assertFalse(algorithmManager.addAlgorithm(algorithm));
    }

    /**
     * Test {@link AlgorithmManager#addAlgorithm(Algorithm)}, but an identifier is already registered with that id.
     */
    @Test
    public void testAddAlgorithmDuplicateIdentifier() {
        AlgorithmManager algorithmManager = new AlgorithmManager();

        when(algorithm.getIdentifier()).thenReturn("test");

        assertTrue(algorithmManager.addAlgorithm(algorithm));
        assertFalse(algorithmManager.addAlgorithm(algorithm));
    }

    /**
     * Test {@link AlgorithmManager#addAlgorithm(Algorithm)}.
     */
    @Test
    public void testRemoveAlgorithm() {
        AlgorithmManager algorithmManager = new AlgorithmManager();

        when(algorithm.getIdentifier()).thenReturn("test");

        assertTrue(algorithmManager.addAlgorithm(algorithm));
        assertTrue(algorithmManager.removeAlgorithm(algorithm));
    }

    /**
     * Test {@link AlgorithmManager#addAlgorithm(Algorithm)}, but the identifier is invalid.
     */
    @Test
    public void testRemoveAlgorithmInvalidIdentifier() {
        AlgorithmManager algorithmManager = new AlgorithmManager();

        when(algorithm.getIdentifier()).thenReturn("");

        assertFalse(algorithmManager.removeAlgorithm(algorithm));
    }

    /**
     * Test {@link AlgorithmManager#addAlgorithm(Algorithm)}, but an identifier is already registered with that id.
     */
    @Test
    public void testRemoveAlgorithmNotRegistered() {
        AlgorithmManager algorithmManager = new AlgorithmManager();

        when(algorithm.getIdentifier()).thenReturn("test");

        assertFalse(algorithmManager.removeAlgorithm(algorithm));
    }

    /**
     * Test {@link AlgorithmManager#getAlgorithmList()}.
     */
    @Test
    public void testGetAlgorithmList() {
        AlgorithmManager algorithmManager = new AlgorithmManager();

        when(algorithm.getIdentifier()).thenReturn("test");

        assertTrue(algorithmManager.addAlgorithm(algorithm));

        List<Algorithm> algorithmList = algorithmManager.getAlgorithmList();
        assertEquals(1, algorithmList.size());
    }
}
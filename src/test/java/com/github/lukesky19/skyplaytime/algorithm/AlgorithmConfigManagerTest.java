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

import com.github.lukesky19.skylib.common.api.plugin.ISkyPlugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * This class tests {@link AlgorithmConfigManager}.
 */
@ExtendWith(MockitoExtension.class)
public class AlgorithmConfigManagerTest {
    @Mock
    private ISkyPlugin plugin;
    @Mock
    private Path configurationPath;
    @Mock
    private File configurationFile;
    @Mock
    private AlgorithmConfig algorithmConfig;

    /**
     * Test {@link AlgorithmConfigManager#saveDefaultConfiguration()}.
     */
    @Test
    public void testSaveDefaultConfiguration() {
        when(plugin.getDirectoryFile()).thenReturn(new File("test_dir"));
        AlgorithmConfigManager algorithmConfigManager = new AlgorithmConfigManager(plugin);

        algorithmConfigManager.saveDefaultConfiguration();

        verify(plugin).saveResource("algorithms.yml", false);
    }

    /**
     * Test {@link AlgorithmConfigManager#saveDefaultConfiguration()}, but the configuration path is invalid.
     */
    @Test
    public void testSaveDefaultConfigurationInvalidPath() {
        when(plugin.getDirectoryFile()).thenReturn(new File("test_dir"));
        AlgorithmConfigManager algorithmConfigManager = new AlgorithmConfigManager(plugin);

        algorithmConfigManager.setConfigurationPath(null);

        algorithmConfigManager.saveDefaultConfiguration();

        verify(plugin, never()).saveResource("algorithms.yml", false);
    }

    /**
     * Test {@link AlgorithmConfigManager#saveDefaultConfiguration()}, but the configuration already exists on the disk.
     */
    @Test
    public void testSaveDefaultConfigurationFileExists() {
        when(plugin.getDirectoryFile()).thenReturn(new File("test_dir"));
        AlgorithmConfigManager algorithmConfigManager = new AlgorithmConfigManager(plugin);

        algorithmConfigManager.setConfigurationPath(configurationPath);
        when(configurationPath.toFile()).thenReturn(configurationFile);
        when(configurationFile.exists()).thenReturn(true);

        algorithmConfigManager.saveDefaultConfiguration();

        verify(plugin, never()).saveResource("algorithms.yml", false);
    }

    /**
     * Test {@link AlgorithmConfigManager#migrateConfiguration(AlgorithmConfig)}.
     */
    @Test
    public void testMigrateConfiguration() {
        when(plugin.getDirectoryFile()).thenReturn(new File("test_dir"));
        AlgorithmConfigManager algorithmConfigManager = new AlgorithmConfigManager(plugin);

        assertEquals(algorithmConfig, algorithmConfigManager.migrateConfiguration(algorithmConfig));
    }

    /**
     * Test {@link AlgorithmConfigManager#validateConfiguration(AlgorithmConfig)}.
     */
    @Test
    public void testValidateConfiguration() {
        when(plugin.getDirectoryFile()).thenReturn(new File("test_dir"));
        AlgorithmConfigManager algorithmConfigManager = new AlgorithmConfigManager(plugin);

        assertTrue(algorithmConfigManager.validateConfiguration(algorithmConfig));
    }

    /**
     * Test {@link AlgorithmConfigManager#validateConfiguration(AlgorithmConfig)}.
     */
    @Test
    public void testValidateConfigurationInvalidConfiguration() {
        when(plugin.getDirectoryFile()).thenReturn(new File("test_dir"));
        AlgorithmConfigManager algorithmConfigManager = new AlgorithmConfigManager(plugin);

        assertFalse(algorithmConfigManager.validateConfiguration(null));
    }
}
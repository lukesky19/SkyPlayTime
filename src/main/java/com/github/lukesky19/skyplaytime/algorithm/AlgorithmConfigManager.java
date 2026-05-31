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

import com.github.lukesky19.skylib.common.api.configuration.abstracts.SimpleConfigManager;
import com.github.lukesky19.skylib.common.api.plugin.ISkyPlugin;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;

/**
 * This class manages the algorithm configuration.
 */
public class AlgorithmConfigManager extends SimpleConfigManager<AlgorithmConfig> {
    /**
     * Constructor
     * @param plugin An {@link ISkyPlugin} instance.
     */
    public AlgorithmConfigManager(@NonNull ISkyPlugin plugin) {
        super(plugin, Path.of(plugin.getDirectoryFile() + File.separator + "algorithms.yml"), AlgorithmConfig.class);
    }

    @Override
    public void setConfigurationPath(@Nullable Path configurationPath) {
        this.configurationPath = configurationPath;
    }

    @Override
    public void saveDefaultConfiguration() {
        if(configurationPath == null) return;

        if(!configurationPath.toFile().exists()) {
            plugin.saveResource("algorithms.yml", false);
        }
    }

    @Override
    public @NonNull AlgorithmConfig migrateConfiguration(@NonNull AlgorithmConfig algorithmConfig) {
        return algorithmConfig;
    }

    @Override
    public boolean validateConfiguration(@Nullable AlgorithmConfig algorithmConfig) {
        return algorithmConfig != null;
    }
}
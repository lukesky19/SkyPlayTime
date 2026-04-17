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
package com.github.lukesky19.skyplaytime.config.manager.settings;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.platform.PlatformUtils;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.config.data.settings.Settings;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import com.github.lukesky19.skylib.libs.configurate.CommentedConfigurationNode;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.NodeStyle;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.ZoneId;

/**
 * This class manages the plugin's settings.
 */
public class SettingsManager {
    private final @NonNull SkyPlayTime skyPlayTime;
    private Settings settings;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     */
    public SettingsManager(@NonNull SkyPlayTime skyPlayTime) {
        this.skyPlayTime = skyPlayTime;
    }

    /**
     * Get the plugin's settings. May be null if it failed to load.
     * @return A {@link Settings} record or null.
     */
    public @Nullable Settings getSettings() {
        return settings;
    }

    /**
     * (Re-)loads the plugin's settings
     */
    public void loadSettings() {
        ComponentLogger logger = skyPlayTime.getComponentLogger();
        settings = null;

        saveDefaultSettings();

        Path path = Path.of(skyPlayTime.getDataFolder() + File.separator + "settings.yml");
        YamlConfigurationLoader loader = createLoader(path);
        try {
            settings = loader.load().get(Settings.class);

            validateConfig();
        } catch (ConfigurateException e) {
            logger.warn(AdventureUtility.plain("Failed to load plugin settings."));
        }
    }

    /**
     * Saves the plugin's settings.
     * @param settings The {@link Settings} record to save.
     */
    public void saveSettings(@NonNull Settings settings) {
        ComponentLogger logger = skyPlayTime.getComponentLogger();
        Path path = Path.of(skyPlayTime.getDataFolder() + File.separator + "settings.yml");

        YamlConfigurationLoader loader = createLoader(path);
        try {
            CommentedConfigurationNode node = loader.createNode();
            node.set(Settings.class, settings);
            loader.save(node);

            this.settings = settings;
        } catch (ConfigurateException e) {
            logger.warn(AdventureUtility.plain("Failed to save plugin settings."));
        }
    }

    /**
     * Saves the plugin's default settings.yml bundled with the plugin if the file doesn't exist.
     */
    public void saveDefaultSettings() {
        Path path = Path.of(skyPlayTime.getDataFolder() + File.separator + "settings.yml");
        if(!path.toFile().exists()) {
            skyPlayTime.saveResource("settings.yml", false);
        }
    }

    /**
     * Validate plugin settings.
     */
    public void validateConfig() {
        if(settings == null) return;

        ComponentLogger logger = skyPlayTime.getComponentLogger();

        if(settings.locale() == null) {
            settings = null;
            logger.warn(AdventureUtility.plain("Invalid locale name provided in settings.yml."));
            return;
        }

        if(settings.resetSettings().zoneId() == null) {
            settings = null;
            logger.warn(AdventureUtility.plain("Invalid zone id name provided in settings.yml."));
            return;
        }

        try {
            //noinspection ResultOfMethodCallIgnored
            ZoneId.of(settings.resetSettings().zoneId());
        } catch (DateTimeException e) {
            settings = null;
            logger.warn(AdventureUtility.plain("Invalid zone id provided in settings.yml. " + e));
            return;
        }

        try {
            DayOfWeek.valueOf(settings.resetSettings().dayOfWeek());
        } catch (IllegalArgumentException e) {
            settings = null;
            logger.warn(AdventureUtility.plain("Invalid day of week provided in settings.yml. " + e));
        }
    }

    /**
     * Create the {@link YamlConfigurationLoader} for the path provided.
     * @apiNote {@link PlatformUtils#getSerializers()} are included by default.
     * @param path The {@link Path}.
     * @return The {@link YamlConfigurationLoader}.
     */
    protected @NonNull YamlConfigurationLoader createLoader(@NonNull Path path) {
        return YamlConfigurationLoader.builder()
                .path(path)
                .nodeStyle(NodeStyle.BLOCK)
                .indent(4)
                .defaultOptions(configurationOptions ->
                        configurationOptions.serializers(builder ->
                                builder.registerAll(PlatformUtils.getSerializers())))
                .build();
    }
}
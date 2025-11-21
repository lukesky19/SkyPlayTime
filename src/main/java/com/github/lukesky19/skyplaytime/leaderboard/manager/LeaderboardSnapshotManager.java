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
package com.github.lukesky19.skyplaytime.leaderboard.manager;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.configurate.ConfigurationUtility;
import com.github.lukesky19.skylib.libs.configurate.CommentedConfigurationNode;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.leaderboard.data.LeaderboardSnapshot;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * This class manages the loading and saving of leaderboard snapshots.
 */
public class LeaderboardSnapshotManager {
    private final @NotNull SkyPlayTime skyPlayTime;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     */
    public LeaderboardSnapshotManager(@NotNull SkyPlayTime skyPlayTime) {
        this.skyPlayTime = skyPlayTime;
    }

    /**
     * Loads a leaderboard snapshot file
     * @param fileName The file name to load.
     * @return A {@link LeaderboardSnapshot} or null.
     * @throws RuntimeException on any {@link ConfigurateException}.
     */
    public @Nullable LeaderboardSnapshot loadLeaderboardSnapshot(@NotNull String fileName) {
        ComponentLogger logger = skyPlayTime.getComponentLogger();
        Path path = Path.of(skyPlayTime.getDataFolder() + File.separator + "leaderboards" + File.separator + fileName);

        @NotNull YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
        try {
            return loader.load().get(LeaderboardSnapshot.class);
        } catch (ConfigurateException e) {
            logger.error(AdventureUtil.deserialize("Failed to load historical leaderboard for file: " + fileName + ". " + e.getMessage()));
            return null;
        }
    }

    /**
     * Saves a {@link LeaderboardSnapshot} to the provided file name in the SkyPlayTime/leaderboards folder.
     * @param fileName The file name to use.
     * @param leaderboardSnapshot The {@link LeaderboardSnapshot} to save.
     * @return true if successful, otherwise false.
     */
    public boolean saveHistoricalLeaderboard(@NotNull String fileName, @NotNull LeaderboardSnapshot leaderboardSnapshot) {
        ComponentLogger logger = skyPlayTime.getComponentLogger();
        Path path = Path.of(skyPlayTime.getDataFolder() + File.separator + "leaderboards" + File.separator + fileName + ".yml");

        @NotNull YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
        try {
            CommentedConfigurationNode node = loader.createNode();
            node.set(LeaderboardSnapshot.class, leaderboardSnapshot);
            loader.save(node);
            return true;
        } catch (ConfigurateException e) {
            logger.error(AdventureUtil.deserialize("Failed to save the leaderboard snapshot. " + e.getMessage()));
            return false;
        }
    }

    /**
     * Get a list of file names in the SkyPlayTime/leaderboards directory.
     * @return A {@link List} of {@link String}s for file names.
     * @throws RuntimeException on any {@link IOException}.
     */
    public @NotNull List<String> getLeaderboardSnapshotFileNames() {
        ComponentLogger logger = skyPlayTime.getComponentLogger();
        List<String> fileNames = new ArrayList<>();

        Path path = Path.of(skyPlayTime.getDataFolder() + File.separator + "leaderboards");
        try(Stream<Path> stream = Files.walk(path)) {
            stream.filter(Files::isRegularFile).forEach(file -> fileNames.add(String.valueOf(file.getFileName())));
        } catch (IOException e) {
            logger.error(AdventureUtil.deserialize("Failed to load historical leaderboard file names. " + e.getMessage()));
            return new ArrayList<>();
        }

        return fileNames;
    }
}

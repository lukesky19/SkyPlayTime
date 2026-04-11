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
package com.github.lukesky19.skyplaytime.task.tasks;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.time.TimeUtil;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.config.data.settings.Settings;
import com.github.lukesky19.skyplaytime.config.manager.settings.SettingsManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.scheduler.BukkitRunnable;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

/**
 * This task removes old database backups and leaderboard snapshots.
 */
public class CleanupTask extends BukkitRunnable {
    private final @NonNull ComponentLogger logger;
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull Path databaseBackupDirectory;
    private final @NonNull Path leaderboardDirectory;

    /**
     * Constructor
     * @param skyPlayTime A {@link SkyPlayTime} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     */
    public CleanupTask(@NonNull SkyPlayTime skyPlayTime, @NonNull SettingsManager settingsManager) {
        this.logger = skyPlayTime.getComponentLogger();
        this.settingsManager = settingsManager;

        databaseBackupDirectory = Path.of(skyPlayTime.getDataFolder() + File.separator + "database_backups");
        leaderboardDirectory = Path.of(skyPlayTime.getDataFolder() + File.separator + "leaderboards");
    }

    /**
     * Removes old database backups and leaderboard snapshots.
     */
    @Override
    public void run() {
        Settings settings = settingsManager.getSettings();
        if(settings == null) {
            logger.warn(AdventureUtil.deserialize("Unable to delete old database backups and leaderboard snapshots due to invalid plugin settings."));
            return;
        }

        if(settings.backupsRemoveOlderThan() != null) {
            long olderThanMillis = TimeUtil.stringToMillis(settings.backupsRemoveOlderThan());
            if(olderThanMillis > 0) {
                long cutoffMillis = System.currentTimeMillis() - olderThanMillis;

                deleteOlderThan(databaseBackupDirectory, cutoffMillis);
            }
        }

        if(settings.leaderboardRemoveOlderThan() != null) {
            long olderThanMillis = TimeUtil.stringToMillis(settings.leaderboardRemoveOlderThan());
            if(olderThanMillis > 0) {
                long cutoffMillis = System.currentTimeMillis() - olderThanMillis;

                deleteOlderThan(leaderboardDirectory, cutoffMillis);
            }
        }
    }

    /**
     * Loop through the directory's files and delete any files older than the cutoff milliseconds provided.
     * @param directory The {@link Path} to loop through.
     * @param cutoffMillis The cutoff time in milliseconds.
     */
    private void deleteOlderThan(@NonNull Path directory, long cutoffMillis) {
        if(Files.isDirectory(directory)) {
            try(Stream<Path> paths = Files.walk(directory)) {
                paths.forEach(path -> {
                    File file = path.toFile();
                    if(file.isFile()) {
                        try {
                            BasicFileAttributes fileAttributes = Files.readAttributes(Paths.get(file.getAbsolutePath()), BasicFileAttributes.class);
                            long creationTime = fileAttributes.creationTime().toMillis();

                            if(creationTime < cutoffMillis) {
                                file.delete();
                            }
                        } catch (IOException e) {
                            logger.warn(AdventureUtil.deserialize("Failed to delete an old database or leaderboard file. Error: " + e.getMessage()));
                        }
                    }
                });
            } catch (IOException e) {
                logger.warn(AdventureUtil.deserialize("Failed to delete old database backups and leaderboard files. Error: " + e.getMessage()));
            }
        }
    }
}

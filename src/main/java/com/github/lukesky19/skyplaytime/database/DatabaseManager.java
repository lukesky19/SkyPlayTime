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
package com.github.lukesky19.skyplaytime.database;

import com.github.lukesky19.skylib.api.database.AbstractDatabaseManager;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.database.connection.ConnectionManager;
import com.github.lukesky19.skyplaytime.database.queue.QueueManager;
import com.github.lukesky19.skyplaytime.database.table.PlayTimeTable;
import com.github.lukesky19.skyplaytime.database.table.VersionsTable;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

/**
 * This class manages access to database tables, in this case just {@link PlayTimeTable}, and backing up the database.
 */
public class DatabaseManager extends AbstractDatabaseManager {
    private final SkyPlayTime skyPlayTime;
    private final PlayTimeTable playTimeTable;

    /**
     * Get the {@link PlayTimeTable} table.
     * @return A {@link PlayTimeTable}
     */
    public @NonNull PlayTimeTable getPlayTimeTable() {
        return playTimeTable;
    }

    /**
     * Constructor
     * Initializes the {@link ConnectionManager}, {@link QueueManager}, and all tables.
     * @param skyPlayTime The main plugin's instance.
     * @param connectionManager A {@link ConnectionManager} instance.
     * @param queueManager A {@link QueueManager} instance.
     */
    public DatabaseManager(@NonNull SkyPlayTime skyPlayTime, @NonNull ConnectionManager connectionManager, @NonNull QueueManager queueManager) {
        super(connectionManager, queueManager);

        this.skyPlayTime = skyPlayTime;
        ComponentLogger logger = skyPlayTime.getComponentLogger();

        VersionsTable versionsTable = new VersionsTable(logger, queueManager);
        versionsTable.createTable();

        playTimeTable = new PlayTimeTable(logger, queueManager, versionsTable);
        playTimeTable.createTable();
    }

    /**
     * Attempts to back up the database.
     * @return A {@link CompletableFuture} containing a {@link Boolean} containing true if it succeeds, and false if not.
     */
    public @NonNull CompletableFuture<Boolean> backupDatabase() {
        CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();

        // Define the source file path
        Path sourcePath = Paths.get(skyPlayTime.getDataFolder().toString(), "database.db");

        super.backupDatabase(sourcePath)
                .thenAccept(v -> resultFuture.complete(true))
                .exceptionally(ex -> {
                    resultFuture.completeExceptionally(ex);
                    return null;
                });

        return resultFuture;
    }
}

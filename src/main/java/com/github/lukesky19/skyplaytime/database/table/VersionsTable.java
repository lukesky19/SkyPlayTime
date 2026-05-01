package com.github.lukesky19.skyplaytime.database.table;

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.api.database.parameter.impl.IntegerParameter;
import com.github.lukesky19.skylib.common.api.database.parameter.impl.StringParameter;
import com.github.lukesky19.skyplaytime.database.queue.QueueManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jspecify.annotations.NonNull;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class is used to create and interface with the versions table in the database.
 */
public class VersionsTable {
    private final @NonNull ComponentLogger logger;
    private final @NonNull QueueManager queueManager;
    private final @NonNull String tableName = "skyplaytime_versions";

    /**
     * Default Constructor.
     * You should use {@link #VersionsTable(ComponentLogger, QueueManager)} instead.
     * @deprecated You should use {@link #VersionsTable(ComponentLogger, QueueManager)} instead.
     */
    @Deprecated
    public VersionsTable() {
        throw new RuntimeException("The use of the default constructor is not allowed.");
    }

    /**
     * Constructor
     * @param logger A {@link ComponentLogger} instance.
     * @param queueManager A {@link QueueManager} instance.
     */
    public VersionsTable(@NonNull ComponentLogger logger, @NonNull QueueManager queueManager) {
        this.logger = logger;
        this.queueManager = queueManager;
    }

    /**
     * Creates the table in the database if it doesn't exist.
     * @return A {@link CompletableFuture} of type {@link Void} when complete.
     */
    public @NonNull CompletableFuture<Void> createTable() {
        String tableCreationSql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INTEGER PRIMARY KEY, " +
                "table_id TEXT NOT NULL UNIQUE, " +
                "version INTEGER NOT NULL)";

        return queueManager.queueWriteTransaction(tableCreationSql).thenRun(() -> {});
    }

    /**
     * Update the version number for a table id.
     * @param tableId The table id.
     * @param version The version to set.
     * @return A {@link CompletableFuture} of type {@link Void} when complete.
     */
    public @NonNull CompletableFuture<Void> updateVersion(@NonNull String tableId, int version) {
        String updateSql = "INSERT INTO " + tableName + " (table_id, version) VALUES (?, ?) ON CONFLICT (table_id) DO UPDATE SET version = ?";

        StringParameter tableIdParameter = new StringParameter(tableId);
        IntegerParameter versionParameter = new IntegerParameter(version);

        return queueManager.queueWriteTransaction(updateSql, List.of(tableIdParameter, versionParameter, versionParameter)).thenRun(() -> {});
    }

    /**
     * Get the version for the table id provided.
     * @param tableId The table id.
     * @return A {@link CompletableFuture} of type {@link Integer} containing the version number. -1 is returned for no version stored.
     */
    public @NonNull CompletableFuture<Integer> getVersion(@NonNull String tableId) {
        String readSql = "SELECT version FROM " + tableName + " WHERE table_id = ?";

        StringParameter tableIdParameter = new StringParameter(tableId);

        return queueManager.queueReadTransaction(readSql, List.of(tableIdParameter), resultSet -> {
            try {
                if(resultSet.next()) {
                    return resultSet.getInt("version");
                } else {
                    return 0;
                }
            } catch (SQLException e) {
                logger.warn(AdventureUtility.plain("Failed to get the table version for table id " + tableId + ". Error: " + e.getMessage()));
                return -1;
            }
        });
    }
}
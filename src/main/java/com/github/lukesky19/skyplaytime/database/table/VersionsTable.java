package com.github.lukesky19.skyplaytime.database.table;

import com.github.lukesky19.skylib.api.database.parameter.impl.IntegerParameter;
import com.github.lukesky19.skylib.api.database.parameter.impl.StringParameter;
import com.github.lukesky19.skylib.api.database.queue.QueueManager;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This class is used to create and interface with the versions table in the database.
 */
public class VersionsTable {
    private final @NotNull QueueManager queueManager;
    private final @NotNull String tableName = "skyplaytime_versions";

    /**
     * Default Constructor.
     * You should use {@link #VersionsTable(QueueManager)} instead.
     * @deprecated You should use {@link #VersionsTable(QueueManager)} instead.
     */
    @Deprecated
    public VersionsTable() {
        throw new RuntimeException("The use of the default constructor is not allowed.");
    }

    /**
     * Constructor
     * @param queueManager A {@link QueueManager} instance.
     */
    public VersionsTable(@NotNull QueueManager queueManager) {
        this.queueManager = queueManager;
    }

    /**
     * Creates the table in the database if it doesn't exist.
     */
    public void createTable() {
        String tableCreationSql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "id INTEGER PRIMARY KEY, " +
                "table_id TEXT NOT NULL UNIQUE, " +
                "version INTEGER NOT NULL)";

        queueManager.queueWriteTransaction(tableCreationSql);
    }

    /**
     * Update the version number for a table id.
     * @param tableId The table id.
     * @param version The version to set.
     */
    public void updateVersion(@NotNull String tableId, int version) {
        String updateSql = "INSERT INTO " + tableName + " (table_id, version) VALUES (?, ?) ON CONFLICT (table_id) DO UPDATE SET version = ?";

        StringParameter tableIdParameter = new StringParameter(tableId);
        IntegerParameter versionParameter = new IntegerParameter(version);

        queueManager.queueWriteTransaction(updateSql, List.of(tableIdParameter, versionParameter, versionParameter));
    }

    /**
     * Get the version for the table id provided.
     * @param tableId The table id.
     * @return A {@link CompletableFuture} of type {@link Integer} containing the version number. -1 is returned for no version stored.
     */
    public @NotNull CompletableFuture<Integer> getTableVersion(@NotNull String tableId) {
        String readSql = "SELECT version FROM " + tableName + " WHERE table_id = ?";

        StringParameter tableIdParameter = new StringParameter(tableId);

        return queueManager.queueReadTransaction(readSql, List.of(tableIdParameter), resultSet -> {
            try {
                if(resultSet.next()) {
                    return resultSet.getInt("version");
                } else {
                    return -1; // No known version
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
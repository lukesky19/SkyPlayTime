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
package com.github.lukesky19.skyplaytime.database.table;

import com.github.lukesky19.skylib.api.database.parameter.Parameter;
import com.github.lukesky19.skylib.api.database.parameter.impl.*;
import com.github.lukesky19.skyplaytime.database.queue.QueueManager;
import com.github.lukesky19.skyplaytime.leaderboard.data.Position;
import com.github.lukesky19.skyplaytime.leaderboard.data.TopTen;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import com.github.lukesky19.skyplaytime.util.TimeCategory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * This class handles the players table that stores player data.
 */
public class PlayTimeTable {
    private final @NotNull QueueManager queueManager;
    private final @NotNull VersionsTable versionsTable;
    private final @NotNull String tableName = "players";

    /**
     * Constructor
     * @param queueManager A {@link QueueManager} instance.
     * @param versionsTable A {@link VersionsTable} instance.
     */
    public PlayTimeTable(
            @NotNull QueueManager queueManager,
            @NotNull VersionsTable versionsTable) {
        this.queueManager = queueManager;
        this.versionsTable = versionsTable;
    }

    /**
     * Creates a table to store all {@link Player}'s {@link UUID}s as a string.
     * Queues the table creation and index creation sql.
     */
    public void createTable() {
        String tableCreationSql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "uuid TEXT PRIMARY KEY NOT NULL UNIQUE, " +
                "name TEXT NOT NULL, " +
                "daily LONG NOT NULL DEFAULT 0, " +
                "weekly LONG NOT NULL DEFAULT 0, " +
                "monthly LONG NOT NULL DEFAULT 0, " +
                "yearly LONG NOT NULL DEFAULT 0, " +
                "total LONG NOT NULL DEFAULT 0, " +
                "exempt INTEGER NOT NULL DEFAULT 0, " +
                "last_updated LONG NOT NULL DEFAULT 0)";
        String indexCreationSql = "CREATE INDEX IF NOT EXISTS idx_player_uuids ON " + tableName + "(uuid);";

        queueManager.queueBulkWriteTransaction(List.of(tableCreationSql, indexCreationSql));

        versionsTable.updateVersion(tableName, 1);
    }

    /**
     * Loads the player's play time and exemption status from the database.
     * @param uuid The {@link UUID} to load data for.
     * @param playerData The {@link PlayerData} to put data into.
     * @return A {@link CompletableFuture} with {@link PlayerData} when complete. The {@link PlayerData} passed to the method will be updated as well.
     */
    public @NotNull CompletableFuture<@NotNull PlayerData> loadPlayerData(@NotNull UUID uuid, @NotNull PlayerData playerData) {
        String selectSql = "SELECT daily, weekly, monthly, yearly, total, exempt FROM " + tableName + " WHERE uuid = ?";
        UUIDParameter uuidParameter = new UUIDParameter(uuid);

        return queueManager.queueReadTransaction(selectSql, List.of(uuidParameter), resultSet -> {
            try {
                if(resultSet.next()) {
                    playerData.setDailyPlayTime(playerData.getDailyPlayTimeSeconds() + resultSet.getLong("daily"));
                    playerData.setWeeklyPlayTime(playerData.getWeeklyPlayTimeSeconds() + resultSet.getLong("weekly"));
                    playerData.setMonthlyPlayTime(playerData.getMonthlyPlayTimeSeconds() + resultSet.getLong("monthly"));
                    playerData.setYearlyPlayTime(playerData.getYearlyPlayTimeSeconds() + resultSet.getLong("yearly"));
                    playerData.setTotalPlayTime(playerData.getTotalPlayTimeSeconds() + resultSet.getLong("total"));
                    playerData.setExempt(resultSet.getBoolean("exempt"));
                }

                return playerData;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Saves the player data for a single player.
     * @param uuid The {@link UUID} of the player.
     * @param playerData The {@link PlayerData} for the player.
     * @return A {@link CompletableFuture} of type {@link Void} when complete.
     */
    public @NotNull CompletableFuture<Void> savePlayerData(@NotNull UUID uuid, @NotNull PlayerData playerData) {
        String updateSql = "INSERT INTO " + tableName + " (" +
                "uuid, " +
                "name, " +
                "daily, " +
                "weekly, " +
                "monthly, " +
                "yearly, " +
                "total, " +
                "exempt, " +
                "last_updated) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (uuid) " +
                "DO UPDATE SET " +
                "name = ?, " +
                "daily = ?, " +
                "weekly = ?, " +
                "monthly = ?, " +
                "yearly = ?, " +
                "total = ?, " +
                "exempt = ?, " +
                "last_updated = ? " +
                "WHERE last_updated <= ?";

        UUIDParameter uuidParameter = new UUIDParameter(uuid);
        StringParameter nameParameter = new StringParameter(playerData.getName());
        LongParameter dailyTimeParameter = new LongParameter(playerData.getDailyPlayTimeSeconds());
        LongParameter weeklyTimeParameter = new LongParameter(playerData.getWeeklyPlayTimeSeconds());
        LongParameter monthlyTimeParameter = new LongParameter(playerData.getMonthlyPlayTimeSeconds());
        LongParameter yearlyTimeParameter = new LongParameter(playerData.getYearlyPlayTimeSeconds());
        LongParameter totalTimeParameter = new LongParameter(playerData.getTotalPlayTimeSeconds());
        IntegerParameter exemptParameter = new IntegerParameter(playerData.isExempt() ? 1 : 0);
        LongParameter timestampParameter = new LongParameter(System.currentTimeMillis());

        List<Parameter<?>> parameters = List.of(
                uuidParameter,
                nameParameter,
                dailyTimeParameter,
                weeklyTimeParameter,
                monthlyTimeParameter,
                yearlyTimeParameter,
                totalTimeParameter,
                exemptParameter,
                timestampParameter,
                nameParameter,
                dailyTimeParameter,
                weeklyTimeParameter,
                monthlyTimeParameter,
                yearlyTimeParameter,
                totalTimeParameter,
                exemptParameter,
                timestampParameter,
                timestampParameter);

        return queueManager.queueWriteTransaction(updateSql, parameters).thenRun(() -> {});
    }

    /**
     * Saves all player data to the database.
     * @param playerDataMap A {@link Map} mapping {@link UUID}s to {@link PlayerData}.
     * @return A {@link CompletableFuture} of type {@link List} containing {@link Boolean}s when complete. true if successful, and false if not.
     */
    public @NotNull CompletableFuture<@NotNull List<@NotNull Boolean>> savePlayerData(@NotNull Map<@NotNull UUID, @NotNull PlayerData> playerDataMap) {
        List<List<Parameter<?>>> listOfParametersList = new ArrayList<>();
        String updateSql = "UPDATE " + tableName + " SET daily = ?, weekly = ?, monthly = ?, yearly = ?, total = ?, exempt = ?, last_updated = ? WHERE uuid = ? AND last_updated < ?";

        playerDataMap.forEach((uuid, playerData) -> {
            LongParameter dailyTimeParameter = new LongParameter(playerData.getDailyPlayTimeSeconds());
            LongParameter weeklyTimeParameter = new LongParameter(playerData.getWeeklyPlayTimeSeconds());
            LongParameter monthlyTimeParameter = new LongParameter(playerData.getMonthlyPlayTimeSeconds());
            LongParameter yearlyTimeParameter = new LongParameter(playerData.getYearlyPlayTimeSeconds());
            LongParameter totalTimeParameter = new LongParameter(playerData.getTotalPlayTimeSeconds());
            IntegerParameter exemptParameter = new IntegerParameter(playerData.isExempt() ? 1 : 0);
            LongParameter timestampParameter = new LongParameter(System.currentTimeMillis());
            UUIDParameter uuidParameter = new UUIDParameter(uuid);
            List<Parameter<?>> parameters = List.of(dailyTimeParameter, weeklyTimeParameter, monthlyTimeParameter, yearlyTimeParameter, totalTimeParameter, exemptParameter, timestampParameter, uuidParameter, timestampParameter);

            listOfParametersList.add(parameters);
        });

        return queueManager.queueBulkWriteTransaction(updateSql, listOfParametersList).thenApply(list -> {
                List<Boolean> results = new ArrayList<>();

                list.forEach(rowsUpdated -> {
                    if(rowsUpdated > 0) {
                        results.add(true);
                    } else  {
                        results.add(false);
                    }
                });

                return results;
            }
        );
    }

    /**
     * Resets play time using the boolean options provided.
     * @param daily Should all daily play time be reset?
     * @param weekly Should all weekly play time be reset?
     * @param monthly Should all monthly play time be reset?
     * @param yearly Should all yearly play time be reset?
     * @param total Should all total play time be reset?
     * @return A {@link CompletableFuture} containing a {@link Boolean}. true if the reset succeeded, false if not.
     */
    public @NotNull CompletableFuture<@NotNull Boolean> resetPlayTime(boolean daily, boolean weekly, boolean monthly, boolean yearly, boolean total) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE " + tableName + " SET ");
        if(daily) sqlBuilder.append("daily = 0, ");
        if(weekly) sqlBuilder.append("weekly = 0, ");
        if(monthly) sqlBuilder.append("monthly = 0, ");
        if(yearly) sqlBuilder.append("yearly = 0, ");
        if(total) sqlBuilder.append("total = 0, ");
        sqlBuilder.append("last_updated = ? WHERE last_updated < ?");

        String updateSql = sqlBuilder.toString();
        LongParameter timestampParameter = new LongParameter(System.currentTimeMillis());

        return queueManager.queueWriteTransaction(updateSql, List.of(timestampParameter, timestampParameter)).thenApply(rowsUpdated -> rowsUpdated > 0);
    }

    /**
     * Retrieves the {@link TopTen} for the {@link TimeCategory} provided that are not exempt.
     * {@link TimeCategory#SESSION} will return a {@link TopTen} with all null values.
     * @param timeCategory The {@link TimeCategory} to sort the query to get player data for.
     * @return A {@link CompletableFuture} containing the {@link TopTen} for the {@link TimeCategory} provided.
     */
    public @NotNull CompletableFuture<@NotNull TopTen> getTopTenByCategoryNotExempt(@NotNull TimeCategory timeCategory) {
        if(timeCategory == TimeCategory.SESSION) return CompletableFuture.completedFuture(new TopTen());
        if(timeCategory == TimeCategory.ALL) timeCategory = TimeCategory.TOTAL;
        String timeCategoryName = timeCategory.toString().toLowerCase();

        String sql = "SELECT uuid, name, " + timeCategoryName + " FROM players WHERE exempt = 0 ORDER BY " + timeCategoryName + " DESC LIMIT 10";
        return queueManager.queueReadTransaction(sql, resultSet -> {
            List<Position> positionList = new LinkedList<>();

            try {
                while(resultSet.next()) {
                    UUID uuid = UUID.fromString(resultSet.getString("uuid"));
                    String name = resultSet.getString("name");
                    int seconds = resultSet.getInt(timeCategoryName);

                    positionList.add(new Position(uuid, name, seconds));
                }

                return new TopTen(positionList);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

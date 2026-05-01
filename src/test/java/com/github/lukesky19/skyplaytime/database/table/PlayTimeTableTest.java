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

import com.github.lukesky19.skyplaytime.database.table.abstracts.AbstractTableTest;
import com.github.lukesky19.skyplaytime.leaderboard.data.TopTen;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import com.github.lukesky19.skyplaytime.util.TimeCategory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * This class tests the {@link PlayTimeTableTest}.
 * Most code is tested against a live database except for errors.
 */
public class PlayTimeTableTest extends AbstractTableTest {
    @Mock
    private ComponentLogger logger;

    private UUID playerId;

    // Classes being tested
    private PlayTimeTable livePlayTimeTable;
    private PlayTimeTable playTimeTableWithMockedQueueManager;

    /**
     * Set up the required data for the tests.
     * @param testInfo The {@link TestInfo}.
     */
    @BeforeEach
    public void setup(@NonNull TestInfo testInfo) {
        super.setup(testInfo);

        playerId = UUID.randomUUID();

        // Setup supporting table classes
        VersionsTable versionsTable = new VersionsTable(logger, liveQueueManager);
        // Create table in database
        versionsTable.createTable().join();

        // Setup classes for tests
        livePlayTimeTable = new PlayTimeTable(logger, liveQueueManager, versionsTable);
        playTimeTableWithMockedQueueManager = new PlayTimeTable(logger, mockedQueueManager, versionsTable);
    }

    /**
     * Tests the creation of the table in the database.
     */
    @Test
    public void testCreateTable() {
        // Check that the table was created successfully and didn't error
        livePlayTimeTable.createTable()
                .thenAccept(Assertions::assertNull)
                .exceptionally(ex -> {
                    fail("Future completed exceptionally: " + ex.getMessage());
                    return null;
                })
                .join();
    }

    /**
     * Tests the creation of the table in the database, but an error occurs.
     */
    @Test
    public void testCreateTableError() {
        // When a bulk write transaction is queued, return a failed future
        when(mockedQueueManager.queueBulkWriteTransaction(anyList()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Test Error")));

        // Check that the table creation errored
        CompletableFuture<Void> future = playTimeTableWithMockedQueueManager.createTable();
        assertTrue(future.isCompletedExceptionally());
    }

    /**
     * Test the saving and loading of player data.
     */
    @Test
    public void testSaveLoadPlayerData() {
        livePlayTimeTable.createTable()
                .thenAccept(Assertions::assertNull)
                .exceptionally(ex -> {
                    fail("Failed to create players table: " + ex.getMessage());
                    return null;
                })
                .join();

        PlayerData playerData = new PlayerData(
                "lukeskywlker19",
                10,
                20,
                30,
                40,
                50,
                60,
                false,
                false);
        CompletableFuture<Void> saveFuture = livePlayTimeTable.savePlayerData(playerId, playerData);
        saveFuture.join();
        assertFalse(saveFuture.isCompletedExceptionally());

        PlayerData testPlayerData = new PlayerData("lukeskywlker19");
        CompletableFuture<Void> loadFuture = livePlayTimeTable.loadPlayerData(playerId, testPlayerData);
        loadFuture.join();
        assertFalse(loadFuture.isCompletedExceptionally());

        assertEquals(0, testPlayerData.getSessionPlayTimeSeconds());
        assertEquals(20, testPlayerData.getDailyPlayTimeSeconds());
        assertEquals(30, testPlayerData.getWeeklyPlayTimeSeconds());
        assertEquals(40, testPlayerData.getMonthlyPlayTimeSeconds());
        assertEquals(50, testPlayerData.getYearlyPlayTimeSeconds());
        assertEquals(60, testPlayerData.getTotalPlayTimeSeconds());

        verify(logger, never()).info(any(Component.class));
        verify(logger, never()).warn(any(Component.class));
        verify(logger, never()).error(any(Component.class));
    }

    /**
     * Test the loading of player data, but the player has no data in the database.
     */
    @Test
    public void testLoadPlayerDataNoData() {
        livePlayTimeTable.createTable()
                .thenAccept(Assertions::assertNull)
                .exceptionally(ex -> {
                    fail("Failed to create players table: " + ex.getMessage());
                    return null;
                })
                .join();

        PlayerData testPlayerData = new PlayerData("lukeskywlker19");
        CompletableFuture<Void> loadFuture = livePlayTimeTable.loadPlayerData(playerId, testPlayerData);
        loadFuture.join();
        assertFalse(loadFuture.isCompletedExceptionally());

        assertEquals(0, testPlayerData.getSessionPlayTimeSeconds());
        assertEquals(0, testPlayerData.getDailyPlayTimeSeconds());
        assertEquals(0, testPlayerData.getWeeklyPlayTimeSeconds());
        assertEquals(0, testPlayerData.getMonthlyPlayTimeSeconds());
        assertEquals(0, testPlayerData.getYearlyPlayTimeSeconds());
        assertEquals(0, testPlayerData.getTotalPlayTimeSeconds());

        verify(logger, never()).info(any(Component.class));
        verify(logger, never()).warn(any(Component.class));
        verify(logger, never()).error(any(Component.class));
    }

    /**
     * Test the loading of player data, but an error occurs.
     */
    @Test
    @SuppressWarnings("resource") // The ResultSet here is a mock, so a try-with-resources block is unnecessary.
    public void testLoadPlayerDataError() {
        // Created a mocked ResultSet
        ResultSet resultSetMock = Mockito.mock(ResultSet.class);

        // When the ResultSet is used, throw an SQLException for the test
        try {
            when(resultSetMock.next()).thenThrow(new SQLException("Test Error"));
        } catch (SQLException e) { // Required to make the IDE happy
            throw new RuntimeException(e);
        }

        // When a read transaction is queued, intercept the invocation to replace the existing ResultSet with the mocked one.
        when(mockedQueueManager.queueReadTransaction(anyString(), anyList(), Mockito.<Function<ResultSet, PlayerData>>any()))
                .thenAnswer(invocation -> {
                    // Get the function
                    Function<ResultSet, PlayerData> function = invocation.getArgument(2);
                    // Call the function with the mocked ResultSet instead.
                    return CompletableFuture.completedFuture(function.apply(resultSetMock));
                });

        PlayerData testPlayerData = new PlayerData("lukeskywlker19");

        playTimeTableWithMockedQueueManager.loadPlayerData(playerId, testPlayerData).join();

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link PlayTimeTable#savePlayerData(UUID, PlayerData)} using player data where the player is exempt from leaderboard reporting.
     */
    @Test
    public void testSavePlayerDataIsExempt() {
        livePlayTimeTable.createTable()
                .thenAccept(Assertions::assertNull)
                .exceptionally(ex -> {
                    fail("Failed to create players table: " + ex.getMessage());
                    return null;
                })
                .join();

        PlayerData playerData = new PlayerData(
                "lukeskywlker19",
                10,
                20,
                30,
                40,
                50,
                60,
                true,
                false);
        CompletableFuture<Void> saveFuture = livePlayTimeTable.savePlayerData(playerId, playerData);
        saveFuture.join();
        assertFalse(saveFuture.isCompletedExceptionally());

        PlayerData testPlayerData = new PlayerData("lukeskywlker19");
        CompletableFuture<Void> loadFuture = livePlayTimeTable.loadPlayerData(playerId, testPlayerData);
        loadFuture.join();
        assertFalse(loadFuture.isCompletedExceptionally());

        assertEquals(0, testPlayerData.getSessionPlayTimeSeconds());
        assertEquals(20, testPlayerData.getDailyPlayTimeSeconds());
        assertEquals(30, testPlayerData.getWeeklyPlayTimeSeconds());
        assertEquals(40, testPlayerData.getMonthlyPlayTimeSeconds());
        assertEquals(50, testPlayerData.getYearlyPlayTimeSeconds());
        assertEquals(60, testPlayerData.getTotalPlayTimeSeconds());
        assertTrue(testPlayerData.isExempt());

        verify(logger, never()).info(any(Component.class));
        verify(logger, never()).warn(any(Component.class));
        verify(logger, never()).error(any(Component.class));
    }

    /**
     * Test the bulk saving of player data using {@link PlayTimeTable#savePlayerData(Map)}.
     */
    @Test
    public void testSaveAllPlayerData() {
        livePlayTimeTable.createTable()
                .thenAccept(Assertions::assertNull)
                .exceptionally(ex -> {
                    fail("Failed to create players table: " + ex.getMessage());
                    return null;
                })
                .join();

        Map<UUID, PlayerData> playerDataMap = new HashMap<>();
        playerDataMap.put(UUID.randomUUID(), new PlayerData("player1",
                10, 20, 30,
                40, 50, 60, false));
        playerDataMap.put(UUID.randomUUID(), new PlayerData("player2",
                10, 20, 30,
                40, 50, 60, false));
        playerDataMap.put(UUID.randomUUID(), new PlayerData("player3",
                10, 20, 30,
                40, 50, 60, false));

        CompletableFuture<List<Boolean>> future = livePlayTimeTable.savePlayerData(playerDataMap);
        future.join();
        assertFalse(future.isCompletedExceptionally());

        future.thenAccept(results -> {
            assertEquals(3, results.size());
            assertFalse(results.contains(false));
        });
    }

    /**
     * Test the bulk saving of player data using {@link PlayTimeTable#savePlayerData(Map)}, but no data is saved/rows are updated.
     */
    @Test
    public void testSaveAllPlayerDataNoDataUpdated() {
        Map<UUID, PlayerData> playerDataMap = new HashMap<>();
        playerDataMap.put(UUID.randomUUID(), new PlayerData("player1",
                10, 20, 30,
                40, 50, 60, false));
        playerDataMap.put(UUID.randomUUID(), new PlayerData("player2",
                10, 20, 30,
                40, 50, 60, false));
        playerDataMap.put(UUID.randomUUID(), new PlayerData("player3",
                10, 20, 30,
                40, 50, 60, false));

        when(mockedQueueManager.queueBulkWriteTransaction(anyString(), anyList()))
                .thenReturn(CompletableFuture.completedFuture(List.of(1, 0, 1)));

        CompletableFuture<List<Boolean>> future = playTimeTableWithMockedQueueManager.savePlayerData(playerDataMap);
        future.join();
        assertFalse(future.isCompletedExceptionally());

        future.thenAccept(results -> {
            assertEquals(3, results.size());
            assertTrue(results.contains(false));
        });
    }

    /**
     * Test resetting daily play time using {@link PlayTimeTable#resetPlayTime(boolean, boolean, boolean, boolean, boolean)}.
     */
    @Test
    public void testResetDailyPlayTime() {
        livePlayTimeTable.createTable()
                .thenAccept(Assertions::assertNull)
                .exceptionally(ex -> {
                    fail("Failed to create players table: " + ex.getMessage());
                    return null;
                })
                .join();

        UUID playerId1 = UUID.randomUUID();
        UUID playerId2 = UUID.randomUUID();
        UUID playerId3 = UUID.randomUUID();

        Map<UUID, PlayerData> playerDataMap = new HashMap<>();
        playerDataMap.put(playerId1, new PlayerData("player1",
                10, 20, 30,
                40, 50, 60, false));
        playerDataMap.put(playerId2, new PlayerData("player2",
                10, 20, 30,
                40, 50, 60, false));
        playerDataMap.put(playerId3, new PlayerData("player3",
                10, 20, 30,
                40, 50, 60, false));

        CompletableFuture<List<Boolean>> saveFuture = livePlayTimeTable.savePlayerData(playerDataMap);
        saveFuture.join();
        assertFalse(saveFuture.isCompletedExceptionally());

        saveFuture.thenAccept(results -> {
            assertEquals(3, results.size());
            assertFalse(results.contains(false));
        });

        CompletableFuture<Boolean> resetFuture = livePlayTimeTable.resetPlayTime(true, false, false, false, false);
        resetFuture.join();
        assertFalse(resetFuture.isCompletedExceptionally());

        try {
            assertTrue(resetFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        PlayerData testPlayerData1 = new PlayerData("player1");
        PlayerData testPlayerData2 = new PlayerData("player2");
        PlayerData testPlayerData3 = new PlayerData("player3");
        livePlayTimeTable.loadPlayerData(playerId1, testPlayerData1).join();
        livePlayTimeTable.loadPlayerData(playerId2, testPlayerData2).join();
        livePlayTimeTable.loadPlayerData(playerId3, testPlayerData3).join();

        assertEquals(0, testPlayerData1.getDailyPlayTimeSeconds());
        assertEquals(0, testPlayerData2.getDailyPlayTimeSeconds());
        assertEquals(0, testPlayerData3.getDailyPlayTimeSeconds());
    }

    /**
     * Test resetting weekly play time using {@link PlayTimeTable#resetPlayTime(boolean, boolean, boolean, boolean, boolean)}.
     */
    @Test
    public void testResetWeeklyPlayTime() {
        livePlayTimeTable.createTable()
                .thenAccept(Assertions::assertNull)
                .exceptionally(ex -> {
                    fail("Failed to create players table: " + ex.getMessage());
                    return null;
                })
                .join();

        UUID playerId1 = UUID.randomUUID();
        UUID playerId2 = UUID.randomUUID();
        UUID playerId3 = UUID.randomUUID();

        Map<UUID, PlayerData> playerDataMap = new HashMap<>();
        playerDataMap.put(playerId1, new PlayerData("player1",
                10, 20, 30,
                40, 50, 60, false));
        playerDataMap.put(playerId2, new PlayerData("player2",
                10, 20, 30,
                40, 50, 60, false));
        playerDataMap.put(playerId3, new PlayerData("player3",
                10, 20, 30,
                40, 50, 60, false));

        CompletableFuture<List<Boolean>> saveFuture = livePlayTimeTable.savePlayerData(playerDataMap);
        saveFuture.join();
        assertFalse(saveFuture.isCompletedExceptionally());

        saveFuture.thenAccept(results -> {
            assertEquals(3, results.size());
            assertFalse(results.contains(false));
        });

        CompletableFuture<Boolean> resetFuture = livePlayTimeTable.resetPlayTime(false, true, false, false, false);
        resetFuture.join();
        assertFalse(resetFuture.isCompletedExceptionally());

        try {
            assertTrue(resetFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        PlayerData testPlayerData1 = new PlayerData("player1");
        PlayerData testPlayerData2 = new PlayerData("player2");
        PlayerData testPlayerData3 = new PlayerData("player3");
        livePlayTimeTable.loadPlayerData(playerId1, testPlayerData1).join();
        livePlayTimeTable.loadPlayerData(playerId2, testPlayerData2).join();
        livePlayTimeTable.loadPlayerData(playerId3, testPlayerData3).join();

        assertEquals(0, testPlayerData1.getWeeklyPlayTimeSeconds());
        assertEquals(0, testPlayerData2.getWeeklyPlayTimeSeconds());
        assertEquals(0, testPlayerData3.getWeeklyPlayTimeSeconds());
    }

    /**
     * Test resetting monthly play time using {@link PlayTimeTable#resetPlayTime(boolean, boolean, boolean, boolean, boolean)}.
     */
    @Test
    public void testResetMonthlyPlayTime() {
        livePlayTimeTable.createTable()
                .thenAccept(Assertions::assertNull)
                .exceptionally(ex -> {
                    fail("Failed to create players table: " + ex.getMessage());
                    return null;
                })
                .join();

        UUID playerId1 = UUID.randomUUID();
        UUID playerId2 = UUID.randomUUID();
        UUID playerId3 = UUID.randomUUID();

        Map<UUID, PlayerData> playerDataMap = new HashMap<>();
        playerDataMap.put(playerId1, new PlayerData("player1",
                10, 20, 30,
                40, 50, 60, false));
        playerDataMap.put(playerId2, new PlayerData("player2",
                10, 20, 30,
                40, 50, 60, false));
        playerDataMap.put(playerId3, new PlayerData("player3",
                10, 20, 30,
                40, 50, 60, false));

        CompletableFuture<List<Boolean>> saveFuture = livePlayTimeTable.savePlayerData(playerDataMap);
        saveFuture.join();
        assertFalse(saveFuture.isCompletedExceptionally());

        saveFuture.thenAccept(results -> {
            assertEquals(3, results.size());
            assertFalse(results.contains(false));
        });

        CompletableFuture<Boolean> resetFuture = livePlayTimeTable.resetPlayTime(false, false, true, false, false);
        resetFuture.join();
        assertFalse(resetFuture.isCompletedExceptionally());

        try {
            assertTrue(resetFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        PlayerData testPlayerData1 = new PlayerData("player1");
        PlayerData testPlayerData2 = new PlayerData("player2");
        PlayerData testPlayerData3 = new PlayerData("player3");
        livePlayTimeTable.loadPlayerData(playerId1, testPlayerData1).join();
        livePlayTimeTable.loadPlayerData(playerId2, testPlayerData2).join();
        livePlayTimeTable.loadPlayerData(playerId3, testPlayerData3).join();

        assertEquals(0, testPlayerData1.getMonthlyPlayTimeSeconds());
        assertEquals(0, testPlayerData2.getMonthlyPlayTimeSeconds());
        assertEquals(0, testPlayerData3.getMonthlyPlayTimeSeconds());
    }

    /**
     * Test resetting yearly play time using {@link PlayTimeTable#resetPlayTime(boolean, boolean, boolean, boolean, boolean)}.
     */
    @Test
    public void testResetYearlyPlayTime() {
        livePlayTimeTable.createTable()
                .thenAccept(Assertions::assertNull)
                .exceptionally(ex -> {
                    fail("Failed to create players table: " + ex.getMessage());
                    return null;
                })
                .join();

        UUID playerId1 = UUID.randomUUID();
        UUID playerId2 = UUID.randomUUID();
        UUID playerId3 = UUID.randomUUID();

        Map<UUID, PlayerData> playerDataMap = new HashMap<>();
        playerDataMap.put(playerId1, new PlayerData("player1",
                10, 20, 30,
                40, 50, 60, false));
        playerDataMap.put(playerId2, new PlayerData("player2",
                10, 20, 30,
                40, 50, 60, false));
        playerDataMap.put(playerId3, new PlayerData("player3",
                10, 20, 30,
                40, 50, 60, false));

        CompletableFuture<List<Boolean>> saveFuture = livePlayTimeTable.savePlayerData(playerDataMap);
        saveFuture.join();
        assertFalse(saveFuture.isCompletedExceptionally());

        saveFuture.thenAccept(results -> {
            assertEquals(3, results.size());
            assertFalse(results.contains(false));
        });

        CompletableFuture<Boolean> resetFuture = livePlayTimeTable.resetPlayTime(false, false, false, true, false);
        resetFuture.join();
        assertFalse(resetFuture.isCompletedExceptionally());

        try {
            assertTrue(resetFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        PlayerData testPlayerData1 = new PlayerData("player1");
        PlayerData testPlayerData2 = new PlayerData("player2");
        PlayerData testPlayerData3 = new PlayerData("player3");
        livePlayTimeTable.loadPlayerData(playerId1, testPlayerData1).join();
        livePlayTimeTable.loadPlayerData(playerId2, testPlayerData2).join();
        livePlayTimeTable.loadPlayerData(playerId3, testPlayerData3).join();

        assertEquals(0, testPlayerData1.getYearlyPlayTimeSeconds());
        assertEquals(0, testPlayerData2.getYearlyPlayTimeSeconds());
        assertEquals(0, testPlayerData3.getYearlyPlayTimeSeconds());
    }

    /**
     * Test resetting total play time using {@link PlayTimeTable#resetPlayTime(boolean, boolean, boolean, boolean, boolean)}.
     */
    @Test
    public void testResetTotalPlayTime() {
        livePlayTimeTable.createTable()
                .thenAccept(Assertions::assertNull)
                .exceptionally(ex -> {
                    fail("Failed to create players table: " + ex.getMessage());
                    return null;
                })
                .join();

        UUID playerId1 = UUID.randomUUID();
        UUID playerId2 = UUID.randomUUID();
        UUID playerId3 = UUID.randomUUID();

        Map<UUID, PlayerData> playerDataMap = new HashMap<>();
        playerDataMap.put(playerId1, new PlayerData("player1",
                10, 20, 30,
                40, 50, 60, false));
        playerDataMap.put(playerId2, new PlayerData("player2",
                10, 20, 30,
                40, 50, 60, false));
        playerDataMap.put(playerId3, new PlayerData("player3",
                10, 20, 30,
                40, 50, 60, false));

        CompletableFuture<List<Boolean>> saveFuture = livePlayTimeTable.savePlayerData(playerDataMap);
        saveFuture.join();
        assertFalse(saveFuture.isCompletedExceptionally());

        saveFuture.thenAccept(results -> {
            assertEquals(3, results.size());
            assertFalse(results.contains(false));
        });

        CompletableFuture<Boolean> resetFuture = livePlayTimeTable.resetPlayTime(false, false, false, false, true);
        resetFuture.join();
        assertFalse(resetFuture.isCompletedExceptionally());

        try {
            assertTrue(resetFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        PlayerData testPlayerData1 = new PlayerData("player1");
        PlayerData testPlayerData2 = new PlayerData("player2");
        PlayerData testPlayerData3 = new PlayerData("player3");
        livePlayTimeTable.loadPlayerData(playerId1, testPlayerData1).join();
        livePlayTimeTable.loadPlayerData(playerId2, testPlayerData2).join();
        livePlayTimeTable.loadPlayerData(playerId3, testPlayerData3).join();

        assertEquals(0, testPlayerData1.getTotalPlayTimeSeconds());
        assertEquals(0, testPlayerData2.getTotalPlayTimeSeconds());
        assertEquals(0, testPlayerData3.getTotalPlayTimeSeconds());
    }

    /**
     * Test the resetting of player data using {@link PlayTimeTable#resetPlayTime(boolean, boolean, boolean, boolean, boolean)}, but no data is updated.
     */
    @Test
    public void testResetPlayTimeNoDataUpdated() {
        when(mockedQueueManager.queueWriteTransaction(anyString(), anyList()))
                .thenReturn(CompletableFuture.completedFuture(0));

        CompletableFuture<Boolean> future = playTimeTableWithMockedQueueManager.resetPlayTime(true, true, true, true, true);
        future.join();
        assertFalse(future.isCompletedExceptionally());

        future.thenAccept(Assertions::assertFalse);
    }

    /**
     * Test {@link PlayTimeTable#getTopTenByCategoryNotExempt(TimeCategory)} using {@link TimeCategory#SESSION}.
     */
    @Test
    public void testGetTopTenSessionCategory() {
        livePlayTimeTable.createTable()
                .thenAccept(Assertions::assertNull)
                .exceptionally(ex -> {
                    fail("Failed to create players table: " + ex.getMessage());
                    return null;
                })
                .join();

        Map<UUID, PlayerData> playerDataMap = new HashMap<>();
        playerDataMap.put(UUID.randomUUID(), new PlayerData("player1",
                10, 163, 30,
                40, 50, 540, true));
        playerDataMap.put(UUID.randomUUID(), new PlayerData("player2",
                10, 300, 30,
                40, 50, 154, false));
        playerDataMap.put(UUID.randomUUID(), new PlayerData("player3",
                10, 89, 30,
                40, 50, 1382, false));

        CompletableFuture<List<Boolean>> saveFuture = livePlayTimeTable.savePlayerData(playerDataMap);
        saveFuture.join();
        assertFalse(saveFuture.isCompletedExceptionally());

        saveFuture.thenAccept(results -> {
            assertEquals(3, results.size());
            assertFalse(results.contains(false));
        });

        CompletableFuture<TopTen> topTenFuture = livePlayTimeTable.getTopTenByCategoryNotExempt(TimeCategory.SESSION);
        topTenFuture.join();
        assertFalse(topTenFuture.isCompletedExceptionally());

        topTenFuture.thenAccept(topTen -> assertTrue(topTen.getPositions().isEmpty()));

        verify(logger, never()).info(any(Component.class));
        verify(logger, never()).warn(any(Component.class));
        verify(logger, never()).error(any(Component.class));
    }

    /**
     * Test {@link PlayTimeTable#getTopTenByCategoryNotExempt(TimeCategory)} using {@link TimeCategory#DAILY}.
     */
    @Test
    public void testGetTopTenDailyCategory() {
        livePlayTimeTable.createTable()
                .thenAccept(Assertions::assertNull)
                .exceptionally(ex -> {
                    fail("Failed to create players table: " + ex.getMessage());
                    return null;
                })
                .join();

        Map<UUID, PlayerData> playerDataMap = new HashMap<>();
        playerDataMap.put(UUID.randomUUID(), new PlayerData("player1",
                10, 163, 30,
                40, 50, 540, true));
        playerDataMap.put(UUID.randomUUID(), new PlayerData("player2",
                10, 300, 30,
                40, 50, 154, false));
        playerDataMap.put(UUID.randomUUID(), new PlayerData("player3",
                10, 89, 30,
                40, 50, 1382, false));

        CompletableFuture<List<Boolean>> saveFuture = livePlayTimeTable.savePlayerData(playerDataMap);
        saveFuture.join();
        assertFalse(saveFuture.isCompletedExceptionally());

        saveFuture.thenAccept(results -> {
            assertEquals(3, results.size());
            assertFalse(results.contains(false));
        });

        CompletableFuture<TopTen> topTenFuture = livePlayTimeTable.getTopTenByCategoryNotExempt(TimeCategory.DAILY);
        topTenFuture.join();
        assertFalse(topTenFuture.isCompletedExceptionally());

        topTenFuture.thenAccept(topTen -> assertEquals(2, topTen.getPositions().size()));

        verify(logger, never()).info(any(Component.class));
        verify(logger, never()).warn(any(Component.class));
        verify(logger, never()).error(any(Component.class));
    }

    /**
     * Test {@link PlayTimeTable#getTopTenByCategoryNotExempt(TimeCategory)} using {@link TimeCategory#ALL}.
     */
    @Test
    public void testGetTopTenAllCategory() {
        livePlayTimeTable.createTable()
                .thenAccept(Assertions::assertNull)
                .exceptionally(ex -> {
                    fail("Failed to create players table: " + ex.getMessage());
                    return null;
                })
                .join();

        Map<UUID, PlayerData> playerDataMap = new HashMap<>();
        playerDataMap.put(UUID.randomUUID(), new PlayerData("player1",
                10, 163, 30,
                40, 50, 540, true));
        playerDataMap.put(UUID.randomUUID(), new PlayerData("player2",
                10, 300, 30,
                40, 50, 154, false));
        playerDataMap.put(UUID.randomUUID(), new PlayerData("player3",
                10, 89, 30,
                40, 50, 1382, false));

        CompletableFuture<List<Boolean>> saveFuture = livePlayTimeTable.savePlayerData(playerDataMap);
        saveFuture.join();
        assertFalse(saveFuture.isCompletedExceptionally());

        saveFuture.thenAccept(results -> {
            assertEquals(3, results.size());
            assertFalse(results.contains(false));
        });

        CompletableFuture<TopTen> topTenFuture = livePlayTimeTable.getTopTenByCategoryNotExempt(TimeCategory.ALL);
        topTenFuture.join();
        assertFalse(topTenFuture.isCompletedExceptionally());

        topTenFuture.thenAccept(topTen -> assertEquals(2, topTen.getPositions().size()));

        verify(logger, never()).info(any(Component.class));
        verify(logger, never()).warn(any(Component.class));
        verify(logger, never()).error(any(Component.class));
    }

    /**
     * Test {@link PlayTimeTable#getTopTenByCategoryNotExempt(TimeCategory)}, but an error occurs.
     */
    @Test
    @SuppressWarnings("resource") // The ResultSet here is a mock, so a try-with-resources block is unnecessary.
    public void testGetTopTenError() {
        // Created a mocked ResultSet
        ResultSet resultSetMock = Mockito.mock(ResultSet.class);

        // When the ResultSet is used, throw an SQLException for the test
        try {
            when(resultSetMock.next()).thenThrow(new SQLException("Test Error"));
        } catch (SQLException e) { // Required to make the IDE happy
            throw new RuntimeException(e);
        }

        // When a read transaction is queued, intercept the invocation to replace the existing ResultSet with the mocked one.
        when(mockedQueueManager.queueReadTransaction(anyString(), Mockito.<Function<ResultSet, TopTen>>any()))
                .thenAnswer(invocation -> {
                    // Get the function
                    Function<ResultSet, TopTen> function = invocation.getArgument(1);
                    // Call the function with the mocked ResultSet instead.
                    return CompletableFuture.completedFuture(function.apply(resultSetMock));
                });

        CompletableFuture<TopTen> future = playTimeTableWithMockedQueueManager.getTopTenByCategoryNotExempt(TimeCategory.MONTHLY);
        assertFalse(future.isCompletedExceptionally());

        future.thenAccept(topTen -> assertTrue(topTen.getPositions().isEmpty()));

        verify(logger).warn(any(Component.class));
    }
}
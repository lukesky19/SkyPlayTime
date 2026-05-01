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
package com.github.lukesky19.skyplaytime.player.manager;

import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.common.MockBukkitExtension;
import com.github.lukesky19.skyplaytime.database.DatabaseManager;
import com.github.lukesky19.skyplaytime.database.table.PlayTimeTable;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * This class tests {@link PlayerDataManager}.
 */
@ExtendWith({MockBukkitExtension.class, MockitoExtension.class})
public class PlayerDataManagerTest {
    // Plugin
    @Mock
    private SkyPlayTime skyPlayTime;
    // Logger
    @Mock
    private ComponentLogger logger;

    // Database
    @Mock
    private DatabaseManager databaseManager;
    @Mock
    private PlayTimeTable playerDataTable;

    // Test Class
    private PlayerDataManager playerDataManager;

    // Player
    @Mock
    private Player player;
    private final UUID playerId = UUID.randomUUID();
    private final String playerName = "lukeskywlker19";

    /**
     * Sets up data for each individual test.
     */
    @BeforeEach
    public void setup() {
        when(skyPlayTime.getComponentLogger()).thenReturn(logger);

        playerDataManager = new PlayerDataManager(skyPlayTime, databaseManager);
    }

    /**
     * Test getting all active (non-afk) player data using {@link PlayerDataManager#getActivePlayerData()}.
     */
    @Test
    public void testGetActivePlayerData() {
        playerDataManager.setPlayerData(
                UUID.randomUUID(),
                new PlayerData(
                        "player1",
                        60,
                        60,
                        60,
                        60,
                        60,
                        60,
                        false,
                        false));
        playerDataManager.setPlayerData(
                UUID.randomUUID(),
                new PlayerData(
                        "player2",
                        60,
                        60,
                        60,
                        60,
                        60,
                        60,
                        false,
                        true));
        playerDataManager.setPlayerData(
                UUID.randomUUID(),
                new PlayerData(
                        "player3",
                        60,
                        60,
                        60,
                        60,
                        60,
                        60,
                        false,
                        true));
        playerDataManager.setPlayerData(
                UUID.randomUUID(),
                new PlayerData(
                        "player4",
                        60,
                        60,
                        60,
                        60,
                        60,
                        60,
                        false,
                        false));
        playerDataManager.setPlayerData(
                UUID.randomUUID(),
                new PlayerData(
                        "player5",
                        60,
                        60,
                        60,
                        60,
                        60,
                        60,
                        false,
                        false));

        assertEquals(5, playerDataManager.getPlayerDataMap().size());

        assertEquals(3, playerDataManager.getActivePlayerData().size());
    }

    /**
     * Test getting player data using {@link PlayerDataManager#getPlayerData(Player)}.
     */
    @Test
    public void testGetPlayerDataByPlayer() {
        when(player.getUniqueId()).thenReturn(playerId);
        PlayerData playerData = new PlayerData(
                playerName,
                60,
                60,
                60,
                60,
                60,
                60,
                false,
                false);
        playerDataManager.setPlayerData(playerId, playerData);

        PlayerData testPlayerData = playerDataManager.getPlayerData(player);
        assertNotNull(testPlayerData);
        assertEquals(playerData, testPlayerData);
    }

    /**
     * Test getting player data using {@link PlayerDataManager#getPlayerData(UUID)}.
     */
    @Test
    public void testGetPlayerDataByUUID() {
        PlayerData playerData = new PlayerData(
                playerName,
                60,
                60,
                60,
                60,
                60,
                60,
                false,
                false);
        playerDataManager.setPlayerData(playerId, playerData);

        PlayerData testPlayerData = playerDataManager.getPlayerData(playerId);
        assertNotNull(testPlayerData);
        assertEquals(playerData, testPlayerData);
    }

    /**
     * Test loading player data.
     */
    @Test
    public void testLoadPlayerData() {
        when(player.getUniqueId()).thenReturn(playerId);
        when(player.getName()).thenReturn(playerName);
        when(databaseManager.getPlayTimeTable()).thenReturn(playerDataTable);

        when(playerDataTable.loadPlayerData(eq(playerId), any(PlayerData.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        CompletableFuture<PlayerData> future = playerDataManager.loadPlayerData(player);
        future.join();
        assertTrue(future.isDone());
        try {
            assertNotNull(future.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        verify(databaseManager, atMost(2)).getPlayTimeTable();
        verify(playerDataTable).loadPlayerData(eq(playerId), any(PlayerData.class));

        assertEquals(1, playerDataManager.getPlayerDataMap().size());
        verify(logger, never()).warn(any(Component.class));
    }

    /**
     * Test loading player data, but an error occurs.
     */
    @Test
    public void testLoadPlayerDataError() {
        when(player.getUniqueId()).thenReturn(playerId);
        when(player.getName()).thenReturn(playerName);
        when(databaseManager.getPlayTimeTable()).thenReturn(playerDataTable);

        when(playerDataTable.loadPlayerData(eq(playerId), any(PlayerData.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Test error")));

        CompletableFuture<PlayerData> future = playerDataManager.loadPlayerData(player);
        future.join();
        assertTrue(future.isDone());
        try {
            assertNull(future.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        verify(databaseManager).getPlayTimeTable();
        verify(playerDataTable).loadPlayerData(eq(playerId), any(PlayerData.class));

        assertEquals(0, playerDataManager.getPlayerDataMap().size());
        verify(logger).warn(any(Component.class));
    }

    @Test
    public void testUnloadPlayerData() {
        when(databaseManager.getPlayTimeTable()).thenReturn(playerDataTable);

        PlayerData playerData = new PlayerData(
                playerName,
                60,
                60,
                60,
                60,
                60,
                60,
                false);
        playerDataManager.setPlayerData(playerId, playerData);
        assertEquals(1, playerDataManager.getPlayerDataMap().size());

        playerDataManager.unloadPlayerData(playerId);

        verify(databaseManager).getPlayTimeTable();
        verify(playerDataTable).savePlayerData(eq(playerId), any(PlayerData.class));

        assertEquals(0, playerDataManager.getPlayerDataMap().size());

        verify(logger, never()).warn(any(Component.class));
    }

    /**
     * Test unloading player data, but no data exists.
     */
    @Test
    public void testUnloadPlayerDataNoPlayerData() {
        assertEquals(0, playerDataManager.getPlayerDataMap().size());

        playerDataManager.unloadPlayerData(playerId);

        verify(logger).warn(any(Component.class));
        verify(databaseManager, never()).getPlayTimeTable();
        verify(playerDataTable, never()).savePlayerData(eq(playerId), any(PlayerData.class));
    }

    /**
     * Test unloading player data, but an error occurs.
     */
    @Test
    public void testUnloadPlayerDataError() {
        when(databaseManager.getPlayTimeTable()).thenReturn(playerDataTable);

        PlayerData playerData = new PlayerData(
                playerName,
                60,
                60,
                60,
                60,
                60,
                60,
                false);
        playerDataManager.setPlayerData(playerId, playerData);
        assertEquals(1, playerDataManager.getPlayerDataMap().size());

        when(playerDataTable.savePlayerData(eq(playerId), any(PlayerData.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Test Error")));

        playerDataManager.unloadPlayerData(playerId);

        verify(databaseManager).getPlayTimeTable();
        verify(playerDataTable).savePlayerData(eq(playerId), any(PlayerData.class));

        assertEquals(0, playerDataManager.getPlayerDataMap().size());

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test saving player data using {@link PlayerDataManager#savePlayerData(UUID)}.
     */
    @Test
    public void testSavePlayerDataByUUID() {
        when(databaseManager.getPlayTimeTable()).thenReturn(playerDataTable);

        PlayerData playerData = new PlayerData(
                playerName,
                60,
                60,
                60,
                60,
                60,
                60,
                false);
        playerDataManager.setPlayerData(playerId, playerData);

        playerDataManager.savePlayerData(playerId);

        verify(logger, never()).warn(any(Component.class));
        verify(databaseManager).getPlayTimeTable();
        verify(playerDataTable).savePlayerData(eq(playerId), any(PlayerData.class));
    }

    /**
     * Test saving player data using {@link PlayerDataManager#savePlayerData(UUID)}, but no player data exists to save.
     */
    @Test
    public void testSavePlayerDataByUUIDNoPlayerData() {
        playerDataManager.savePlayerData(playerId);

        verify(logger).warn(any(Component.class));
        verify(databaseManager, never()).getPlayTimeTable();
        verify(playerDataTable, never()).savePlayerData(eq(playerId), any(PlayerData.class));
    }

    /**
     * Test saving player data using {@link PlayerDataManager#savePlayerData(UUID, PlayerData)}.
     */
    @Test
    public void testSavePlayerData() {
        when(databaseManager.getPlayTimeTable()).thenReturn(playerDataTable);

        PlayerData playerData = new PlayerData(
                playerName,
                60,
                60,
                60,
                60,
                60,
                60,
                false);

        playerDataManager.savePlayerData(playerId, playerData);

        verify(logger, never()).warn(any(Component.class));
        verify(databaseManager).getPlayTimeTable();
        verify(playerDataTable).savePlayerData(eq(playerId), any(PlayerData.class));
    }

    /**
     * Test saving player data using {@link PlayerDataManager#savePlayerData(UUID, PlayerData)}, but an error occurs.
     */
    @Test
    public void testSavePlayerDataError() {
        when(databaseManager.getPlayTimeTable()).thenReturn(playerDataTable);

        PlayerData playerData = new PlayerData(
                playerName,
                60,
                60,
                60,
                60,
                60,
                60,
                false);

        when(playerDataTable.savePlayerData(eq(playerId), any(PlayerData.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Test Error")));

        playerDataManager.savePlayerData(playerId, playerData);

        verify(logger).warn(any(Component.class));
        verify(databaseManager).getPlayTimeTable();
        verify(playerDataTable).savePlayerData(eq(playerId), any(PlayerData.class));
    }

    /**
     * Test saving all player data using {@link PlayerDataManager#savePlayerData()}.
     */
    @Test
    public void testSaveAllPlayerData() {
        when(databaseManager.getPlayTimeTable()).thenReturn(playerDataTable);

        PlayerData playerData = new PlayerData(
                playerName,
                60,
                60,
                60,
                60,
                60,
                60,
                false);

        playerDataManager.setPlayerData(playerId, playerData);

        playerDataManager.savePlayerData();

        verify(databaseManager).getPlayTimeTable();
        verify(playerDataTable).savePlayerData(anyMap());
    }
}
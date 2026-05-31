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
import com.github.lukesky19.skyplaytime.settings.Settings;
import com.github.lukesky19.skyplaytime.settings.SettingsManager;
import com.github.lukesky19.skyplaytime.database.DatabaseManager;
import com.github.lukesky19.skyplaytime.database.table.PlayTimeTable;
import com.github.lukesky19.skyplaytime.leaderboard.manager.LeaderboardManager;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import com.github.lukesky19.skyplaytime.util.enums.TimeCategory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * This class tests {@link TimeManager}.
 */
@ExtendWith({MockBukkitExtension.class, MockitoExtension.class})
public class TimeManagerTest {
    // Plugin
    @Mock
    private SkyPlayTime skyPlayTime;
    // Logger
    @Mock
    private ComponentLogger logger;

    // Settings
    @Mock
    private SettingsManager settingsManager;

    // Database
    @Mock
    private DatabaseManager databaseManager;
    @Mock
    private PlayTimeTable playTimeTable;

    // PlayerDataManager
    @Mock
    private PlayerDataManager playerDataManager;

    // LeaderboardManager
    @Mock
    private LeaderboardManager leaderboardManager;

    // Test Class
    private TimeManager timeManager;

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

        timeManager = new TimeManager(skyPlayTime, settingsManager, databaseManager, playerDataManager, leaderboardManager);
    }

    /**
     * Test {@link TimeManager#getPlayTimeSeconds(Player, TimeCategory)}.
     */
    @Test
    public void testGetPlayTimeSeconds() {
        PlayerData playerData = new PlayerData(
                playerName,
                60,
                60,
                60,
                60,
                60,
                60,
                false,
                true);
        when(playerDataManager.getPlayerData(player)).thenReturn(Optional.of(playerData));

        long playTime = timeManager.getPlayTimeSeconds(player, TimeCategory.MONTHLY);

        assertEquals(60, playTime);
    }

    /**
     * Test {@link TimeManager#getPlayTimeSeconds(Player, TimeCategory)}, but the player has no player data.
     */
    @Test
    public void testGetPlayTimeSecondsNoPlayerData() {
        long playTime = timeManager.getPlayTimeSeconds(player, TimeCategory.MONTHLY);

        assertEquals(0, playTime);

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link TimeManager#addPlayTimeSeconds(Player, TimeCategory, long)}.
     */
    @Test
    public void testAddPlayTimeSeconds() {
        PlayerData playerData = new PlayerData(
                playerName,
                60,
                60,
                60,
                60,
                60,
                60,
                false,
                true);
        when(playerDataManager.getPlayerData(player)).thenReturn(Optional.of(playerData));

        assertTrue(timeManager.addPlayTimeSeconds(player, TimeCategory.MONTHLY, 30));

        assertEquals(90, timeManager.getPlayTimeSeconds(player, TimeCategory.MONTHLY));
    }

    /**
     * Test {@link TimeManager#addPlayTimeSeconds(Player, TimeCategory, long)}, but the player has no player data.
     */
    @Test
    public void testAddPlayTimeSecondsNoPlayerData() {
        assertFalse(timeManager.addPlayTimeSeconds(player, TimeCategory.MONTHLY, 30));

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link TimeManager#removePlayTimeSeconds(Player, TimeCategory, long)}.
     */
    @Test
    public void testRemovePlayTimeSeconds() {
        PlayerData playerData = new PlayerData(
                playerName,
                60,
                60,
                60,
                60,
                60,
                60,
                false,
                true);
        when(playerDataManager.getPlayerData(player)).thenReturn(Optional.of(playerData));

        assertTrue(timeManager.removePlayTimeSeconds(player, TimeCategory.MONTHLY, 30));

        assertEquals(30, timeManager.getPlayTimeSeconds(player, TimeCategory.MONTHLY));
    }

    /**
     * Test {@link TimeManager#removePlayTimeSeconds(Player, TimeCategory, long)}, but the player has no player data.
     */
    @Test
    public void testRemovePlayTimeSecondsNoPlayerData() {
        assertFalse(timeManager.removePlayTimeSeconds(player, TimeCategory.MONTHLY, 30));

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link TimeManager#setPlayTimeSeconds(Player, TimeCategory, long)}.
     */
    @Test
    public void testSetPlayTimeSeconds() {
        PlayerData playerData = new PlayerData(
                playerName,
                60,
                60,
                60,
                60,
                60,
                60,
                false,
                true);
        when(playerDataManager.getPlayerData(player)).thenReturn(Optional.of(playerData));

        assertTrue(timeManager.setPlayTimeSeconds(player, TimeCategory.MONTHLY, 45));

        assertEquals(45, timeManager.getPlayTimeSeconds(player, TimeCategory.MONTHLY));
    }

    /**
     * Test {@link TimeManager#setPlayTimeSeconds(Player, TimeCategory, long)}, but the player has no player data.
     */
    @Test
    public void testSetPlayTimeSecondsNoPlayerData() {
        assertFalse(timeManager.setPlayTimeSeconds(player, TimeCategory.MONTHLY, 45));

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link TimeManager#resetPlayTime(Player, boolean, boolean, boolean, boolean, boolean, boolean)} for all the player's play time.
     */
    @Test
    public void testResetPlayerPlayTime() {
        when(player.getUniqueId()).thenReturn(playerId);

        setupSettings();

        PlayerData playerData = setupPlayerData();

        assertTrue(timeManager.resetPlayTime(player, true, true, true, true, true, true));

        verify(logger, never()).warn(any(Component.class));

        verify(playerDataManager).savePlayerData(playerId);

        assertEquals(0, playerData.getPlayTime(TimeCategory.SESSION));
        assertEquals(0, playerData.getPlayTime(TimeCategory.DAILY));
        assertEquals(0, playerData.getPlayTime(TimeCategory.WEEKLY));
        assertEquals(0, playerData.getPlayTime(TimeCategory.MONTHLY));
        assertEquals(0, playerData.getPlayTime(TimeCategory.YEARLY));
        assertEquals(0, playerData.getPlayTime(TimeCategory.TOTAL));
    }

    /**
     * Test {@link TimeManager#resetPlayTime(Player, boolean, boolean, boolean, boolean, boolean, boolean)} for the player's session play time only.
     */
    @Test
    public void testResetPlayerSessionPlayTime() {
        when(player.getUniqueId()).thenReturn(playerId);

        setupSettings();

        PlayerData playerData = setupPlayerData();

        assertTrue(timeManager.resetPlayTime(player, true, false, false, false, false, false));

        verify(logger, never()).warn(any(Component.class));

        verify(playerDataManager).savePlayerData(playerId);

        assertEquals(0, playerData.getPlayTime(TimeCategory.SESSION));
        assertEquals(60, playerData.getPlayTime(TimeCategory.DAILY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.WEEKLY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.MONTHLY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.YEARLY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.TOTAL));
    }

    /**
     * Test {@link TimeManager#resetPlayTime(Player, boolean, boolean, boolean, boolean, boolean, boolean)} for the player's daily play time only.
     */
    @Test
    public void testResetPlayerDailyPlayTime() {
        when(player.getUniqueId()).thenReturn(playerId);

        setupSettings();

        PlayerData playerData = setupPlayerData();

        assertTrue(timeManager.resetPlayTime(player, false, true, false, false, false, false));

        verify(logger, never()).warn(any(Component.class));

        verify(playerDataManager).savePlayerData(playerId);

        assertEquals(60, playerData.getPlayTime(TimeCategory.SESSION));
        assertEquals(0, playerData.getPlayTime(TimeCategory.DAILY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.WEEKLY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.MONTHLY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.YEARLY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.TOTAL));
    }

    /**
     * Test {@link TimeManager#resetPlayTime(Player, boolean, boolean, boolean, boolean, boolean, boolean)} for the player's weekly play time only.
     */
    @Test
    public void testResetPlayerWeeklyPlayTime() {
        when(player.getUniqueId()).thenReturn(playerId);

        setupSettings();

        PlayerData playerData = setupPlayerData();

        assertTrue(timeManager.resetPlayTime(player, false, false, true, false, false, false));

        verify(logger, never()).warn(any(Component.class));

        verify(playerDataManager).savePlayerData(playerId);

        assertEquals(60, playerData.getPlayTime(TimeCategory.SESSION));
        assertEquals(60, playerData.getPlayTime(TimeCategory.DAILY));
        assertEquals(0, playerData.getPlayTime(TimeCategory.WEEKLY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.MONTHLY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.YEARLY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.TOTAL));
    }

    /**
     * Test {@link TimeManager#resetPlayTime(Player, boolean, boolean, boolean, boolean, boolean, boolean)} for the player's monthly play time only.
     */
    @Test
    public void testResetPlayerMonthlyPlayTime() {
        when(player.getUniqueId()).thenReturn(playerId);

        setupSettings();

        PlayerData playerData = setupPlayerData();

        assertTrue(timeManager.resetPlayTime(player, false, false, false, true, false, false));

        verify(logger, never()).warn(any(Component.class));

        verify(playerDataManager).savePlayerData(playerId);

        assertEquals(60, playerData.getPlayTime(TimeCategory.SESSION));
        assertEquals(60, playerData.getPlayTime(TimeCategory.DAILY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.WEEKLY));
        assertEquals(0, playerData.getPlayTime(TimeCategory.MONTHLY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.YEARLY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.TOTAL));
    }

    /**
     * Test {@link TimeManager#resetPlayTime(Player, boolean, boolean, boolean, boolean, boolean, boolean)} for the player's yearly play time only.
     */
    @Test
    public void testResetPlayerYearlyPlayTime() {
        when(player.getUniqueId()).thenReturn(playerId);

        setupSettings();

        PlayerData playerData = setupPlayerData();

        assertTrue(timeManager.resetPlayTime(player, false, false, false, false, true, false));

        verify(logger, never()).warn(any(Component.class));

        verify(playerDataManager).savePlayerData(playerId);

        assertEquals(60, playerData.getPlayTime(TimeCategory.SESSION));
        assertEquals(60, playerData.getPlayTime(TimeCategory.DAILY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.WEEKLY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.MONTHLY));
        assertEquals(0, playerData.getPlayTime(TimeCategory.YEARLY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.TOTAL));
    }

    /**
     * Test {@link TimeManager#resetPlayTime(Player, boolean, boolean, boolean, boolean, boolean, boolean)} for the player's total play time only.
     */
    @Test
    public void testResetPlayerTotalPlayTime() {
        when(player.getUniqueId()).thenReturn(playerId);

        setupSettings();

        PlayerData playerData = setupPlayerData();

        assertTrue(timeManager.resetPlayTime(player, false, false, false, false, false, true));

        verify(logger, never()).warn(any(Component.class));

        verify(playerDataManager).savePlayerData(playerId);

        assertEquals(60, playerData.getPlayTime(TimeCategory.SESSION));
        assertEquals(60, playerData.getPlayTime(TimeCategory.DAILY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.WEEKLY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.MONTHLY));
        assertEquals(60, playerData.getPlayTime(TimeCategory.YEARLY));
        assertEquals(0, playerData.getPlayTime(TimeCategory.TOTAL));
    }

    /**
     * Test {@link TimeManager#resetPlayTime(Player, boolean, boolean, boolean, boolean, boolean, boolean)}, but all booleans are false.
     */
    @Test
    public void testResetPlayerPlayTimeAllOptionsFalse() {
        assertFalse(timeManager.resetPlayTime(player, false, false, false, false, false, false));

        verify(logger, never()).warn(any(Component.class));
    }

    /**
     * Test {@link TimeManager#resetPlayTime(Player, boolean, boolean, boolean, boolean, boolean, boolean)}, but the plugin's settings are invalid.
     */
    @Test
    public void testResetPlayerPlayTimeInvalidSettings() {
        assertFalse(timeManager.resetPlayTime(player, true, true, true, true, true, true));

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link TimeManager#resetPlayTime(Player, boolean, boolean, boolean, boolean, boolean, boolean)}, but the player has no player data.
     */
    @Test
    public void testResetPlayerPlayTimeNoPlayerData() {
        Settings settings = new Settings(
                1,
                "en_US",
                900,
                512,
                300,
                true,
                true,
                "30d",
                "90d",
                new Settings.PlayerSettings(false, true, true),
                new Settings.ResetSettings("America/New_York", "SUNDAY", 10),
                new Settings.LastResetTimes(0, 0, 0, 0));
        when(settingsManager.getSettings()).thenReturn(settings);

        assertFalse(timeManager.resetPlayTime(player, true, true, true, true, true, true));

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link TimeManager#resetPlayTime(boolean, boolean, boolean, boolean, boolean, boolean)}.
     * Also tests leaderboard snapshot creation and database backup.
     */
    @Test
    public void testResetAllPlayTime() {
        when(databaseManager.getPlayTimeTable()).thenReturn(playTimeTable);
        when(databaseManager.backupDatabase()).thenReturn(CompletableFuture.completedFuture(true));

        when(playTimeTable.resetPlayTime(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(CompletableFuture.completedFuture(true));

        when(playerDataManager.savePlayerData()).thenReturn(CompletableFuture.completedFuture(List.of(true, true, true, true)));

        when(leaderboardManager.saveLeaderboardSnapshots(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(true);

        setupSettings();
        Map<UUID, PlayerData> playerDataMap = setupPlayerDataMap();

        CompletableFuture<Boolean> future = timeManager.resetPlayTime(true, true, true, true, true, true);

        future.thenAccept(Assertions::assertTrue).join();

        verify(logger, never()).warn(any(Component.class));

        playerDataMap.forEach((_, playerData) -> {
            assertEquals(0, playerData.getPlayTime(TimeCategory.SESSION));
            assertEquals(0, playerData.getPlayTime(TimeCategory.DAILY));
            assertEquals(0, playerData.getPlayTime(TimeCategory.WEEKLY));
            assertEquals(0, playerData.getPlayTime(TimeCategory.MONTHLY));
            assertEquals(0, playerData.getPlayTime(TimeCategory.YEARLY));
            assertEquals(0, playerData.getPlayTime(TimeCategory.TOTAL));
        });
    }

    /**
     * Test {@link TimeManager#resetPlayTime(boolean, boolean, boolean, boolean, boolean, boolean)} for session play time only.
     * Also tests leaderboard snapshot creation and database backup.
     */
    @Test
    public void testResetAllSessionPlayTime() {
        when(databaseManager.getPlayTimeTable()).thenReturn(playTimeTable);
        when(databaseManager.backupDatabase()).thenReturn(CompletableFuture.completedFuture(true));

        when(playTimeTable.resetPlayTime(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(CompletableFuture.completedFuture(true));

        when(playerDataManager.savePlayerData()).thenReturn(CompletableFuture.completedFuture(List.of(true, true, true, true)));

        when(leaderboardManager.saveLeaderboardSnapshots(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(true);

        setupSettings();
        Map<UUID, PlayerData> playerDataMap = setupPlayerDataMap();

        CompletableFuture<Boolean> future = timeManager.resetPlayTime(true, false, false, false, false, false);

        future.thenAccept(Assertions::assertTrue).join();

        verify(logger, never()).warn(any(Component.class));

        playerDataMap.forEach((_, playerData) -> {
            assertEquals(0, playerData.getPlayTime(TimeCategory.SESSION));
            assertEquals(60, playerData.getPlayTime(TimeCategory.DAILY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.WEEKLY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.MONTHLY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.YEARLY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.TOTAL));
        });
    }

    /**
     * Test {@link TimeManager#resetPlayTime(boolean, boolean, boolean, boolean, boolean, boolean)} for daily play time only.
     * Also tests leaderboard snapshot creation and database backup.
     */
    @Test
    public void testResetAllDailyPlayTime() {
        when(databaseManager.getPlayTimeTable()).thenReturn(playTimeTable);
        when(databaseManager.backupDatabase()).thenReturn(CompletableFuture.completedFuture(true));

        when(playTimeTable.resetPlayTime(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(CompletableFuture.completedFuture(true));

        when(playerDataManager.savePlayerData()).thenReturn(CompletableFuture.completedFuture(List.of(true, true, true, true)));

        when(leaderboardManager.saveLeaderboardSnapshots(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(true);

        setupSettings();
        Map<UUID, PlayerData> playerDataMap = setupPlayerDataMap();

        CompletableFuture<Boolean> future = timeManager.resetPlayTime(false, true, false, false, false, false);

        future.thenAccept(Assertions::assertTrue).join();

        verify(logger, never()).warn(any(Component.class));

        playerDataMap.forEach((_, playerData) -> {
            assertEquals(60, playerData.getPlayTime(TimeCategory.SESSION));
            assertEquals(0, playerData.getPlayTime(TimeCategory.DAILY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.WEEKLY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.MONTHLY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.YEARLY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.TOTAL));
        });
    }

    /**
     * Test {@link TimeManager#resetPlayTime(boolean, boolean, boolean, boolean, boolean, boolean)} for weekly play time only.
     * Also tests leaderboard snapshot creation and database backup.
     */
    @Test
    public void testResetAllWeeklyPlayTime() {
        when(databaseManager.getPlayTimeTable()).thenReturn(playTimeTable);
        when(databaseManager.backupDatabase()).thenReturn(CompletableFuture.completedFuture(true));

        when(playTimeTable.resetPlayTime(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(CompletableFuture.completedFuture(true));

        when(playerDataManager.savePlayerData()).thenReturn(CompletableFuture.completedFuture(List.of(true, true, true, true)));

        when(leaderboardManager.saveLeaderboardSnapshots(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(true);

        setupSettings();
        Map<UUID, PlayerData> playerDataMap = setupPlayerDataMap();

        CompletableFuture<Boolean> future = timeManager.resetPlayTime(false, false, true, false, false, false);

        future.thenAccept(Assertions::assertTrue).join();

        verify(logger, never()).warn(any(Component.class));

        playerDataMap.forEach((_, playerData) -> {
            assertEquals(60, playerData.getPlayTime(TimeCategory.SESSION));
            assertEquals(60, playerData.getPlayTime(TimeCategory.DAILY));
            assertEquals(0, playerData.getPlayTime(TimeCategory.WEEKLY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.MONTHLY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.YEARLY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.TOTAL));
        });
    }

    /**
     * Test {@link TimeManager#resetPlayTime(boolean, boolean, boolean, boolean, boolean, boolean)} for monthly play time only.
     * Also tests leaderboard snapshot creation and database backup.
     */
    @Test
    public void testResetAllMonthlyPlayTime() {
        when(databaseManager.getPlayTimeTable()).thenReturn(playTimeTable);
        when(databaseManager.backupDatabase()).thenReturn(CompletableFuture.completedFuture(true));

        when(playTimeTable.resetPlayTime(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(CompletableFuture.completedFuture(true));

        when(playerDataManager.savePlayerData()).thenReturn(CompletableFuture.completedFuture(List.of(true, true, true, true)));

        when(leaderboardManager.saveLeaderboardSnapshots(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(true);

        setupSettings();
        Map<UUID, PlayerData> playerDataMap = setupPlayerDataMap();

        CompletableFuture<Boolean> future = timeManager.resetPlayTime(false, false, false, true, false, false);

        future.thenAccept(Assertions::assertTrue).join();

        verify(logger, never()).warn(any(Component.class));

        playerDataMap.forEach((_, playerData) -> {
            assertEquals(60, playerData.getPlayTime(TimeCategory.SESSION));
            assertEquals(60, playerData.getPlayTime(TimeCategory.DAILY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.WEEKLY));
            assertEquals(0, playerData.getPlayTime(TimeCategory.MONTHLY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.YEARLY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.TOTAL));
        });
    }

    /**
     * Test {@link TimeManager#resetPlayTime(boolean, boolean, boolean, boolean, boolean, boolean)} for yearly play time only.
     * Also tests leaderboard snapshot creation and database backup.
     */
    @Test
    public void testResetAllYearlyPlayTime() {
        when(databaseManager.getPlayTimeTable()).thenReturn(playTimeTable);
        when(databaseManager.backupDatabase()).thenReturn(CompletableFuture.completedFuture(true));

        when(playTimeTable.resetPlayTime(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(CompletableFuture.completedFuture(true));

        when(playerDataManager.savePlayerData()).thenReturn(CompletableFuture.completedFuture(List.of(true, true, true, true)));

        when(leaderboardManager.saveLeaderboardSnapshots(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(true);

        setupSettings();
        Map<UUID, PlayerData> playerDataMap = setupPlayerDataMap();

        CompletableFuture<Boolean> future = timeManager.resetPlayTime(false, false, false, false, true, false);

        future.thenAccept(Assertions::assertTrue).join();

        verify(logger, never()).warn(any(Component.class));

        playerDataMap.forEach((_, playerData) -> {
            assertEquals(60, playerData.getPlayTime(TimeCategory.SESSION));
            assertEquals(60, playerData.getPlayTime(TimeCategory.DAILY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.WEEKLY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.MONTHLY));
            assertEquals(0, playerData.getPlayTime(TimeCategory.YEARLY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.TOTAL));
        });
    }

    /**
     * Test {@link TimeManager#resetPlayTime(boolean, boolean, boolean, boolean, boolean, boolean)} for total play time only.
     * Also tests leaderboard snapshot creation and database backup.
     */
    @Test
    public void testResetAllTotalPlayTime() {
        when(databaseManager.getPlayTimeTable()).thenReturn(playTimeTable);
        when(databaseManager.backupDatabase()).thenReturn(CompletableFuture.completedFuture(true));

        when(playTimeTable.resetPlayTime(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(CompletableFuture.completedFuture(true));

        when(playerDataManager.savePlayerData()).thenReturn(CompletableFuture.completedFuture(List.of(true, true, true, true)));

        when(leaderboardManager.saveLeaderboardSnapshots(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(true);

        setupSettings();
        Map<UUID, PlayerData> playerDataMap = setupPlayerDataMap();

        CompletableFuture<Boolean> future = timeManager.resetPlayTime(false, false, false, false, false, true);

        future.thenAccept(Assertions::assertTrue).join();

        verify(logger, never()).warn(any(Component.class));

        playerDataMap.forEach((_, playerData) -> {
            assertEquals(60, playerData.getPlayTime(TimeCategory.SESSION));
            assertEquals(60, playerData.getPlayTime(TimeCategory.DAILY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.WEEKLY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.MONTHLY));
            assertEquals(60, playerData.getPlayTime(TimeCategory.YEARLY));
            assertEquals(0, playerData.getPlayTime(TimeCategory.TOTAL));
        });
    }

    /**
     * Test {@link TimeManager#resetPlayTime(boolean, boolean, boolean, boolean, boolean, boolean)}, but all booleans are false.
     */
    @Test
    public void testResetAllPlayTimeAllOptionsFalse() {
        CompletableFuture<Boolean> future = timeManager.resetPlayTime(false, false, false, false, false, false);

        future.thenAccept(Assertions::assertFalse).join();

        verify(logger, never()).warn(any(Component.class));
    }

    /**
     * Test {@link TimeManager#resetPlayTime(boolean, boolean, boolean, boolean, boolean, boolean)}, but an error occurred while saving player data.
     */
    @Test
    public void testResetAllPlayTimeSavePlayerDataError() {
        setupSettings();

        when(playerDataManager.savePlayerData()).thenReturn(CompletableFuture.completedFuture(List.of(true, true, false, true)));

        CompletableFuture<Boolean> future = timeManager.resetPlayTime(true, true, true, true, true, true);

        future.thenAccept(Assertions::assertFalse).join();

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link TimeManager#resetPlayTime(boolean, boolean, boolean, boolean, boolean, boolean)}, but the database fails to backup.
     */
    @Test
    public void testResetAllPlayTimeWithDatabaseBackupError() {
        when(databaseManager.backupDatabase()).thenReturn(CompletableFuture.completedFuture(false));

        when(playerDataManager.savePlayerData()).thenReturn(CompletableFuture.completedFuture(List.of(true, true, true, true)));

        Settings settings = new Settings(
                1,
                "en_US",
                900,
                512,
                300,
                true,
                false,
                "30d",
                "90d",
                new Settings.PlayerSettings(false, true, true),
                new Settings.ResetSettings("America/New_York", "SUNDAY", 10),
                new Settings.LastResetTimes(0, 0, 0, 0));
        when(settingsManager.getSettings()).thenReturn(settings);

        CompletableFuture<Boolean> future = timeManager.resetPlayTime(true, true, true, true, true, true);

        future.thenAccept(Assertions::assertFalse).join();

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link TimeManager#resetPlayTime(boolean, boolean, boolean, boolean, boolean, boolean)}.
     */
    @Test
    public void testResetAllPlayersPlayTimeNoBackupOrLeaderboardSnapshot() {
        when(databaseManager.getPlayTimeTable()).thenReturn(playTimeTable);
        when(playTimeTable.resetPlayTime(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(CompletableFuture.completedFuture(true));

        Map<UUID, PlayerData> playerDataMap = new HashMap<>();
        playerDataMap.put(
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
        playerDataMap.put(
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
        playerDataMap.put(
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
        playerDataMap.put(
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
        playerDataMap.put(
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

        when(playerDataManager.getPlayerDataMap()).thenReturn(playerDataMap);

        when(playerDataManager.savePlayerData()).thenReturn(CompletableFuture.completedFuture(List.of(true, true, true, true)));

        Settings settings = new Settings(
                1,
                "en_US",
                900,
                512,
                300,
                false,
                false,
                "30d",
                "90d",
                new Settings.PlayerSettings(false, true, true),
                new Settings.ResetSettings("America/New_York", "SUNDAY", 10),
                new Settings.LastResetTimes(0, 0, 0, 0));
        when(settingsManager.getSettings()).thenReturn(settings);

        CompletableFuture<Boolean> future = timeManager.resetPlayTime(true, true, true, true, true, true);

        verify(logger, never()).warn(any(Component.class));

        future.join();

        assertTrue(future.isDone());

        future.thenAccept(Assertions::assertTrue).join();

        playerDataMap.forEach((_, playerData) -> {
            assertEquals(0, playerData.getPlayTime(TimeCategory.SESSION));
            assertEquals(0, playerData.getPlayTime(TimeCategory.DAILY));
            assertEquals(0, playerData.getPlayTime(TimeCategory.WEEKLY));
            assertEquals(0, playerData.getPlayTime(TimeCategory.MONTHLY));
            assertEquals(0, playerData.getPlayTime(TimeCategory.YEARLY));
            assertEquals(0, playerData.getPlayTime(TimeCategory.TOTAL));
        });
    }

    /**
     * Test {@link TimeManager#resetPlayTime(boolean, boolean, boolean, boolean, boolean, boolean)}, but the plugin's settings are invalid.
     */
    @Test
    public void testResetAllPlayTimeInvalidSettings() {
        CompletableFuture<Boolean> future = timeManager.resetPlayTime(true, true, true, true, true, true);

        future.thenAccept(Assertions::assertFalse).join();

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link TimeManager#createLeaderboardSnapshot(Settings, boolean, boolean, boolean, boolean, boolean, boolean)}, but an error occurred while creating and saving the snapshot.
     */
    @Test
    public void testCreateLeaderboardSnapshotError() {
        when(leaderboardManager.saveLeaderboardSnapshots(anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean())).thenReturn(false);

        Settings settings = new Settings(
                1,
                "en_US",
                900,
                512,
                300,
                true,
                true,
                "30d",
                "90d",
                new Settings.PlayerSettings(false, true, true),
                new Settings.ResetSettings("America/New_York", "SUNDAY", 10),
                new Settings.LastResetTimes(0, 0, 0, 0));

        CompletableFuture<Boolean> future = timeManager.createLeaderboardSnapshot(settings, true, true, true, true, true, true);

        future.thenAccept(Assertions::assertFalse).join();

        verify(logger).warn(any(Component.class));
    }

    /**
     * Sets up {@link Settings} for testing purposes.
     */
    private void setupSettings() {
        Settings settings = new Settings(
                1,
                "en_US",
                900,
                512,
                300,
                true,
                true,
                "30d",
                "90d",
                new Settings.PlayerSettings(false, true, true),
                new Settings.ResetSettings("America/New_York", "SUNDAY", 10),
                new Settings.LastResetTimes(0, 0, 0, 0));
        when(settingsManager.getSettings()).thenReturn(settings);

    }

    /**
     * Sets up {@link PlayerData} for testing purposes.
     * @return The created {@link PlayerData}.
     */
    private @NonNull PlayerData setupPlayerData() {
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
        when(playerDataManager.getPlayerData(player)).thenReturn(Optional.of(playerData));

        return playerData;
    }

    /**
     * Sets up a {@link Map} mapping {@link UUID}s to {@link PlayerData}.
     * @return A {@link Map} mapping {@link UUID}s to {@link PlayerData}.
     */
    private @NonNull Map<UUID, PlayerData> setupPlayerDataMap() {
        Map<UUID, PlayerData> playerDataMap = new HashMap<>();
        playerDataMap.put(
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
        playerDataMap.put(
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
        playerDataMap.put(
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
        playerDataMap.put(
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
        playerDataMap.put(
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

        when(playerDataManager.getPlayerDataMap()).thenReturn(playerDataMap);

        return playerDataMap;
    }
}
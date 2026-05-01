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

import com.github.lukesky19.newPlayerPerks.NewPlayerPerksAPI;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.common.MockBukkitExtension;
import com.github.lukesky19.skyplaytime.config.data.locale.Locale;
import com.github.lukesky19.skyplaytime.config.data.settings.Settings;
import com.github.lukesky19.skyplaytime.config.manager.locale.LocaleManager;
import com.github.lukesky19.skyplaytime.config.manager.settings.SettingsManager;
import com.github.lukesky19.skyplaytime.event.AFKStatusChangeEvent;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import com.github.lukesky19.skyplaytime.util.AFKToggleResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * This class tests {@link AFKManager}.
 */
@ExtendWith({MockBukkitExtension.class, MockitoExtension.class})
public class AFKManagerTest {
    // Plugin
    @Mock
    private SkyPlayTime skyPlayTime;
    // Logger
    @Mock
    private ComponentLogger logger;
    // Server
    @Mock
    private Server server;
    // PluginManager
    @Mock
    private PluginManager pluginManager;

    // Settings
    @Mock
    private SettingsManager settingsManager;
    @Mock
    private LocaleManager localeManager;

    // NewPlayerPerksAPI
    @Mock
    private NewPlayerPerksAPI newPlayerPerksAPI;

    // PlayerDataManager
    @Mock
    private PlayerDataManager playerDataManager;

    // Test Class
    private AFKManager afkManager;

    // Players
    @Mock
    private Player player1;
    private final UUID player1Id = UUID.randomUUID();
    private final String player1Name = "lukeskywlker19";

    @Mock
    private Player player2;
    @Mock
    private Player player3;
    @Mock
    private Player player4;
    @Mock
    private Player player5;

    /**
     * Sets up data for each individual test.
     */
    @BeforeEach
    public void setup() {
        when(skyPlayTime.getComponentLogger()).thenReturn(logger);

        afkManager = new AFKManager(skyPlayTime, settingsManager, localeManager, playerDataManager, newPlayerPerksAPI);
    }

    /**
     * Test {@link AFKManager#isPlayerAFK(Player)}.
     */
    @Test
    public void testIsPlayerAFK() {
        PlayerData playerData = new PlayerData(
                player1Name,
                60,
                60,
                60,
                60,
                60,
                60,
                false,
                true);
        when(playerDataManager.getPlayerData(player1)).thenReturn(playerData);

        assertTrue(afkManager.isPlayerAFK(player1));

        verify(logger, never()).warn(any(Component.class));
    }

    /**
     * Test {@link AFKManager#isPlayerAFK(Player)}, but no player1 data exists.
     */
    @Test
    public void testIsPlayerAFKNoPlayerData() {
        when(playerDataManager.getPlayerData(player1)).thenReturn(null);

        assertFalse(afkManager.isPlayerAFK(player1));

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link AFKManager#isPlayerAFK(UUID)}.
     */
    @Test
    public void testIsPlayerAFKByUUID() {
        PlayerData playerData = new PlayerData(
                player1Name,
                60,
                60,
                60,
                60,
                60,
                60,
                false,
                true);
        when(playerDataManager.getPlayerData(player1Id)).thenReturn(playerData);

        assertTrue(afkManager.isPlayerAFK(player1Id));

        verify(logger, never()).warn(any(Component.class));
    }

    /**
     * Test {@link AFKManager#isPlayerAFK(UUID)}, but no player1 data exists.
     */
    @Test
    public void testIsPlayerAFKByUUIDNoPlayerData() {
        when(playerDataManager.getPlayerData(player1Id)).thenReturn(null);

        assertFalse(afkManager.isPlayerAFK(player1Id));

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link AFKManager#getAFKPlayers()}.
     */
    @Test
    public void testGetAFKPlayers() {
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

        assertEquals(2, afkManager.getAFKPlayers().size());
    }

    /**
     * Test {@link AFKManager#togglePlayerAFK(Player, boolean, boolean)} where the player1 is marked AFK.
     */
    @Test
    public void testMarkPlayerAFK() {
        when(skyPlayTime.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(pluginManager);

        when(server.getOnlinePlayers()).thenAnswer(_ -> List.of(player1, player2, player3, player4, player5));

        Settings settings = createSettings();
        when(settingsManager.getSettings()).thenReturn(settings);

        Locale locale = createLocale();
        when(localeManager.getLocale()).thenReturn(locale);

        when(player1.getUniqueId()).thenReturn(player1Id);
        when(player1.getName()).thenReturn(player1Name);

        PlayerData playerData = new PlayerData(
                player1Name,
                60,
                60,
                60,
                60,
                60,
                60,
                false);
        when(playerDataManager.getPlayerData(player1)).thenReturn(playerData);
        when(playerDataManager.getPlayerData(player1Id)).thenReturn(playerData);
        assertFalse(playerData.isAFK());

        AFKToggleResult result = afkManager.togglePlayerAFK(player1, true, true);
        assertEquals(AFKToggleResult.SUCCESS_AFK, result);

        assertTrue(afkManager.isPlayerAFK(player1));

        verify(player1).sendMessage(any(Component.class));
        verify(player2).sendMessage(any(Component.class));
        verify(player3).sendMessage(any(Component.class));
        verify(player4).sendMessage(any(Component.class));
        verify(player5).sendMessage(any(Component.class));

        verify(logger, never()).warn(any(Component.class));
    }

    /**
     * Test {@link AFKManager#togglePlayerAFK(Player, boolean, boolean)} where the player1 is marked not AFK.
     */
    @Test
    public void testMarkPlayerNotAFK() {
        when(skyPlayTime.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(pluginManager);

        when(server.getOnlinePlayers()).thenAnswer(_ -> List.of(player1, player2, player3, player4, player5));

        Settings settings = createSettings();
        when(settingsManager.getSettings()).thenReturn(settings);

        Locale locale = createLocale();
        when(localeManager.getLocale()).thenReturn(locale);

        when(player1.getUniqueId()).thenReturn(player1Id);
        when(player1.getName()).thenReturn(player1Name);

        PlayerData playerData = new PlayerData(
                player1Name,
                60,
                60,
                60,
                60,
                60,
                60,
                false,
                true);
        when(playerDataManager.getPlayerData(player1)).thenReturn(playerData);
        when(playerDataManager.getPlayerData(player1Id)).thenReturn(playerData);
        assertTrue(playerData.isAFK());

        AFKToggleResult result = afkManager.togglePlayerAFK(player1, true, true);
        assertEquals(AFKToggleResult.SUCCESS_NO_LONGER_AFK, result);

        assertFalse(afkManager.isPlayerAFK(player1));

        verify(player1).sendMessage(any(Component.class));
        verify(player2).sendMessage(any(Component.class));
        verify(player3).sendMessage(any(Component.class));
        verify(player4).sendMessage(any(Component.class));
        verify(player5).sendMessage(any(Component.class));

        verify(logger, never()).warn(any(Component.class));
    }

    /**
     * Test {@link AFKManager#togglePlayerAFK(Player, boolean, boolean)}, but the plugin settings are invalid.
     */
    @Test
    public void testTogglePlayerAFKInvalidSettings() {
        AFKToggleResult result = afkManager.togglePlayerAFK(player1, true, true);
        assertEquals(AFKToggleResult.CONFIG_ERROR, result);

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link AFKManager#togglePlayerAFK(Player, boolean, boolean)}, but the player1 has no player1 data.
     */
    @Test
    public void testTogglePlayerAFKNoPlayerData() {
        Settings settings = createSettings();
        when(settingsManager.getSettings()).thenReturn(settings);

        Locale locale = createLocale();
        when(localeManager.getLocale()).thenReturn(locale);

        when(player1.getUniqueId()).thenReturn(player1Id);

        when(playerDataManager.getPlayerData(player1Id)).thenReturn(null);

        AFKToggleResult result = afkManager.togglePlayerAFK(player1, true, true);
        assertEquals(AFKToggleResult.ERROR, result);

        verify(logger).warn(any(Component.class));
    }

    /**
     * Test {@link AFKManager#togglePlayerAFK(Player, boolean, boolean)}, but the {@link AFKStatusChangeEvent} is canceled.
     */
    @Test
    public void testTogglePlayerAFKCancelled() {
        when(skyPlayTime.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(pluginManager);

        Settings settings = createSettings();
        when(settingsManager.getSettings()).thenReturn(settings);

        Locale locale = createLocale();
        when(localeManager.getLocale()).thenReturn(locale);

        when(player1.getUniqueId()).thenReturn(player1Id);

        PlayerData playerData = new PlayerData(
                player1Name,
                60,
                60,
                60,
                60,
                60,
                60,
                false);
        when(playerDataManager.getPlayerData(player1)).thenReturn(playerData);
        when(playerDataManager.getPlayerData(player1Id)).thenReturn(playerData);
        assertFalse(playerData.isAFK());

        // Cancel the event
        doAnswer(invocation -> {
            AFKStatusChangeEvent event = invocation.getArgument(0);
            event.setCancelled(true);
            return null;
        }).when(pluginManager).callEvent(any(AFKStatusChangeEvent.class));

        AFKToggleResult result = afkManager.togglePlayerAFK(player1, true, true);
        assertEquals(AFKToggleResult.CANCELLED, result);

        assertFalse(afkManager.isPlayerAFK(player1));

        verify(player1, never()).sendMessage(any(Component.class));
        verify(player2, never()).sendMessage(any(Component.class));
        verify(player3, never()).sendMessage(any(Component.class));
        verify(player4, never()).sendMessage(any(Component.class));
        verify(player5, never()).sendMessage(any(Component.class));

        verify(logger, never()).warn(any(Component.class));
    }

    /**
     * Test {@link AFKManager#togglePlayerAFK(Player, boolean, boolean)} where the player1 is marked AFK and the player and server are not notified.
     */
    @Test
    public void testMarkPlayerAFKDoNotNotify() {
        when(skyPlayTime.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(pluginManager);

        Settings settings = createSettings();
        when(settingsManager.getSettings()).thenReturn(settings);

        Locale locale = createLocale();
        when(localeManager.getLocale()).thenReturn(locale);

        when(player1.getUniqueId()).thenReturn(player1Id);

        PlayerData playerData = new PlayerData(
                player1Name,
                60,
                60,
                60,
                60,
                60,
                60,
                false);
        when(playerDataManager.getPlayerData(player1)).thenReturn(playerData);
        when(playerDataManager.getPlayerData(player1Id)).thenReturn(playerData);
        assertFalse(playerData.isAFK());

        AFKToggleResult result = afkManager.togglePlayerAFK(player1, false, false);
        assertEquals(AFKToggleResult.SUCCESS_AFK, result);

        assertTrue(afkManager.isPlayerAFK(player1));

        verify(player1, never()).sendMessage(any(Component.class));
        verify(player2, never()).sendMessage(any(Component.class));
        verify(player3, never()).sendMessage(any(Component.class));
        verify(player4, never()).sendMessage(any(Component.class));
        verify(player5, never()).sendMessage(any(Component.class));

        verify(logger, never()).warn(any(Component.class));
    }

    /**
     * Test {@link AFKManager#togglePlayerAFK(Player, boolean, boolean)} where the player is marked not AFK and the player and server are not notified.
     */
    @Test
    public void testMarkPlayerNotAFKDoNotNotify() {
        when(skyPlayTime.getServer()).thenReturn(server);
        when(server.getPluginManager()).thenReturn(pluginManager);

        Settings settings = createSettings();
        when(settingsManager.getSettings()).thenReturn(settings);

        Locale locale = createLocale();
        when(localeManager.getLocale()).thenReturn(locale);

        when(player1.getUniqueId()).thenReturn(player1Id);

        PlayerData playerData = new PlayerData(
                player1Name,
                60,
                60,
                60,
                60,
                60,
                60,
                false,
                true);
        when(playerDataManager.getPlayerData(player1)).thenReturn(playerData);
        when(playerDataManager.getPlayerData(player1Id)).thenReturn(playerData);
        assertTrue(playerData.isAFK());

        AFKToggleResult result = afkManager.togglePlayerAFK(player1, false, false);
        assertEquals(AFKToggleResult.SUCCESS_NO_LONGER_AFK, result);

        assertFalse(afkManager.isPlayerAFK(player1));

        verify(player1, never()).sendMessage(any(Component.class));
        verify(player2, never()).sendMessage(any(Component.class));
        verify(player3, never()).sendMessage(any(Component.class));
        verify(player4, never()).sendMessage(any(Component.class));
        verify(player5, never()).sendMessage(any(Component.class));

        verify(logger, never()).warn(any(Component.class));
    }

    /**
     * Test {@link AFKManager#setAFKPlayerSettings(Settings, Player)}.
     */
    @Test
    public void testSetAFKSettings() {
        Settings settings = createSettings();

        afkManager.setAFKPlayerSettings(settings, player1);

        verify(player1).setCanPickupItems(false);

        verify(player1).setInvulnerable(true);

        verify(player1).setSleepingIgnored(true);
    }

    /**
     * Test {@link AFKManager#setAFKPlayerSettings(Settings, Player)} where all afk settings are disabled.
     */
    @Test
    public void testSetAFKSettingsDisabled() {
        Settings settings = new Settings(
                1,
                "en_US",
                900,
                true,
                true,
                "30d",
                "90d",
                new Settings.AfkSettings(
                        300,
                        60,
                        30,
                        new Settings.PlayerSettings(true, false, false)),
                new Settings.ResetSettings("America/New_York", "SUNDAY", 10),
                new Settings.LastResetTimes(0, 0, 0, 0));

        afkManager.setAFKPlayerSettings(settings, player1);

        verify(player1, never()).setCanPickupItems(false);

        verify(player1, never()).setInvulnerable(true);

        verify(player1, never()).setSleepingIgnored(true);
    }

    /**
     * Test {@link AFKManager#resetAFKPlayerSettings(Settings, Player)}.
     */
    @Test
    public void testResetAFKSettings() {
        when(player1.getUniqueId()).thenReturn(player1Id);

        Settings settings = createSettings();

        afkManager.resetAFKPlayerSettings(settings, player1);

        verify(player1).setCanPickupItems(true);

        verify(player1).setInvulnerable(false);

        verify(player1).setSleepingIgnored(false);
    }

    /**
     * Test {@link AFKManager#resetAFKPlayerSettings(Settings, Player)} where all afk settings are disabled.
     */
    @Test
    public void testResetAFKSettingsDisabled() {
        when(player1.getUniqueId()).thenReturn(player1Id);

        Settings settings = new Settings(
                1,
                "en_US",
                900,
                true,
                true,
                "30d",
                "90d",
                new Settings.AfkSettings(
                        300,
                        60,
                        30,
                        new Settings.PlayerSettings(true, false, false)),
                new Settings.ResetSettings("America/New_York", "SUNDAY", 10),
                new Settings.LastResetTimes(0, 0, 0, 0));

        afkManager.resetAFKPlayerSettings(settings, player1);

        verify(player1, never()).setCanPickupItems(true);

        verify(player1, never()).setInvulnerable(false);

        verify(player1, never()).setSleepingIgnored(false);
    }

    /**
     * Test {@link AFKManager#resetAFKPlayerSettings(Settings, Player)}, but the player1 has perks from the NewPlayerPerks plugin.
     */
    @Test
    public void testResetAFKSettingsHasPerks() {
        when(player1.getUniqueId()).thenReturn(player1Id);

        when(newPlayerPerksAPI.hasPerks(player1Id)).thenReturn(true);
        when(newPlayerPerksAPI.isInvulnerablePerkEnabled()).thenReturn(true);

        Settings settings = createSettings();

        afkManager.resetAFKPlayerSettings(settings, player1);

        verify(player1).setCanPickupItems(true);

        verify(player1, never()).setInvulnerable(false);

        verify(player1).setSleepingIgnored(false);
    }

    /**
     * Test {@link AFKManager#resetAFKPlayerSettings(Settings, Player)}, but the invulnerable perk from NewPlayerPerks is not enabled.
     */
    @Test
    public void testResetAFKSettingsInvulnerabilityPerkNotEnabled() {
        when(player1.getUniqueId()).thenReturn(player1Id);

        when(newPlayerPerksAPI.hasPerks(player1Id)).thenReturn(true);
        when(newPlayerPerksAPI.isInvulnerablePerkEnabled()).thenReturn(false);

        Settings settings = createSettings();

        afkManager.resetAFKPlayerSettings(settings, player1);

        verify(player1).setCanPickupItems(true);

        verify(player1).setInvulnerable(false);

        verify(player1).setSleepingIgnored(false);
    }

    /**
     * Test {@link AFKManager#resetAFKPlayerSettings(Settings, Player)}, but NewPlayerPerks isn't hooked into.
     */
    @Test
    public void testResetAFKSettingsNoNewPlayerPerks() {
        when(player1.getUniqueId()).thenReturn(player1Id);

        AFKManager afkManagerNoNewPlayerPerks = new AFKManager(skyPlayTime, settingsManager, localeManager, playerDataManager, null);

        Settings settings = createSettings();

        afkManagerNoNewPlayerPerks.resetAFKPlayerSettings(settings, player1);

        verify(player1).setCanPickupItems(true);

        verify(player1).setInvulnerable(false);

        verify(player1).setSleepingIgnored(false);
    }

    /**
     * Create {@link Settings} configuration for testing purposes.
     * @return A {@link Settings} configuration.
     */
    private @NonNull Settings createSettings() {
        return new Settings(
                1,
                "en_US",
                900,
                true,
                true,
                "30d",
                "90d",
                new Settings.AfkSettings(
                        300,
                        60,
                        30,
                        new Settings.PlayerSettings(false, true, true)),
                new Settings.ResetSettings("America/New_York", "SUNDAY", 10),
                new Settings.LastResetTimes(0, 0, 0, 0));
    }

    /**
     * Create {@link Locale} configuration for testing purposes.
     * @return A {@link Locale} configuration.
     */
    private @NonNull Locale createLocale() {
        Locale.TimeFormat TIME_FORMAT = new Locale.TimeFormat(
                "",
                "<green><years></green> year(s)",
                "<green><months></green> month(s)",
                "<green><weeks></green> week(s)",
                "<green><days></green> day(s)",
                "<green><hours></green> hour(s)",
                "<green><minutes></green> minute(s)",
                "<green><seconds></green> second(s)",
                "");

        return new Locale(
                1,
                "<aqua><bold>SkyPlayTime</bold></aqua><gray> ▪ </gray>",
                List.of("<aqua>SkyPlayTime is developed by <white><bold>lukeskywlker19</bold></white>.</aqua>",
                        "<aqua>Source code is released on GitHub: <click:OPEN_URL:https://github.com/lukesky19><yellow><underlined><bold>https://github.com/lukesky19</bold></underlined></yellow></click></aqua>",
                        " ",
                        "<aqua><bold>List of Commands:</bold></aqua>",
                        "<white>/</white><aqua>skyplaytime</aqua> <yellow>reload</yellow>",
                        "<white>/</white><aqua>skyplaytime</aqua> <yellow>help</yellow>",
                        "<white>/</white><aqua>skyplaytime</aqua> <yellow>time <player1 name> <session | daily | weely | monthly | yearly | total></yellow>",
                        "<white>/</white><aqua>afk</aqua> <yellow>afk [player1 name]</yellow>",
                        "<white>/</white><aqua>list</aqua>",
                        "<white>/</white><aqua>skyplaytime</aqua> <yellow>add <session | daily | weely | monthly | yearly | total> <player1 name> <time></yellow>",
                        "<white>/</white><aqua>skyplaytime</aqua> <yellow>remove <session | daily | weely | monthly | yearly | total> <player1 name> <time></yellow>",
                        "<white>/</white><aqua>skyplaytime</aqua> <yellow>set <session | daily | weely | monthly | yearly | total> <player1 name> <time></yellow>",
                        "<white>/</white><aqua>skyplaytime</aqua> <yellow>reset <session | daily | weely | monthly | yearly | total> [player1 name]</yellow>",
                        "<white>/</white><aqua>skyplaytime</aqua> <yellow>backup</yellow>",
                        "<white>/</white><aqua>skyplaytime</aqua> <yellow>exempt <player1 name></yellow>",
                        "<white>/</white><aqua>skyplaytime</aqua> <yellow>unexempt <player1 name></yellow>",
                        "<white>/</white><aqua>skyplaytime</aqua> <yellow>debug status</yellow>",
                        "<white>/</white><aqua>skyplaytime</aqua> <yellow>debug last-move</yellow>",
                        "<white>/</white><aqua>skyplaytime</aqua> <yellow>debug last-action</yellow>",
                        "<white>/</white><aqua>skyplaytime</aqua> <yellow>debug list</yellow>"),
                "<aqua>The plugin has been reloaded.</aqua>",
                "<gray>You are now afk.</gray>",
                "<gray>You are no longer afk.</gray>",
                "<gray>Player <aqua><player1></aqua> is now afk.</gray>",
                "<gray>Player <aqua><player1></aqua> is no longer afk.</gray>",
                "<red>This command can only be ran by a player1.</red>",
                "<aqua>Forcefully marked player1 <yellow><player1></yellow> as afk.</aqua>",
                "<aqua>Forcefully marked player1 <yellow><player1></yellow> as no longer afk.</aqua>",
                "<red>Failed to forcefully toggle <yellow><player_name></yellow>'s AFK status.",
                "<aqua>Online Players</aqua> <gray>-</gray> <yellow><player_count></yellow>",
                "<gray><player_name></gray>",
                " <gray>[</gray><white>AFK<white><gray>]</gray>",
                "<gray>, </gray>",
                "<aqua><bold>Top 10 Players by Session Play Time</bold></aqua>",
                "<gray>[</gray><aqua><position></aqua><gray>]</gray> <yellow><player1></yellow> <time>",
                "<gray>[</gray><aqua><position></aqua><gray>] ----------</gray>",
                TIME_FORMAT,
                "<aqua><bold>Top 10 Players by Daily Play Time</bold></aqua>",
                "<gray>[</gray><aqua><position></aqua><gray>]</gray> <yellow><player1></yellow> <time>",
                "<gray>[</gray><aqua><position></aqua><gray>] ----------</gray>",
                TIME_FORMAT,
                "<aqua><bold>Top 10 Players by Weekly Play Time</bold></aqua>",
                "<gray>[</gray><aqua><position></aqua><gray>]</gray> <yellow><player1></yellow> <time>",
                "<gray>[</gray><aqua><position></aqua><gray>] ----------</gray>",
                TIME_FORMAT,
                "<aqua><bold>Top 10 Players by Monthly Play Time</bold></aqua>",
                "<gray>[</gray><aqua><position></aqua><gray>]</gray> <yellow><player1></yellow> <time>",
                "<gray>[</gray><aqua><position></aqua><gray>] ----------</gray>",
                TIME_FORMAT,
                "<aqua><bold>Top 10 Players by Yearly Play Time</bold></aqua>",
                "<gray>[</gray><aqua><position></aqua><gray>]</gray> <yellow><player1></yellow> <time>",
                "<gray>[</gray><aqua><position></aqua><gray>] ----------</gray>",
                TIME_FORMAT,
                "<aqua><bold>Top 10 Players by Total Play Time</bold></aqua>",
                "<gray>[</gray><aqua><position></aqua><gray>]</gray> <yellow><player1></yellow> <time>",
                "<gray>[</gray><aqua><position></aqua><gray>] ----------</gray>",
                TIME_FORMAT,
                "<aqua>Historical Leaderboard from File:</aqua> <yellow><file_name></yellow>",
                "<gray>[</gray><aqua><position></aqua><gray>]</gray> <yellow><player1></yellow> <time>",
                "<gray>[</gray><aqua><position></aqua><gray>] ----------</gray>",
                TIME_FORMAT,
                "<red>Failed to load the historical leaderboard from file: <yellow><file_name></yellow></red>",
                "<aqua>Your session play time is: <yellow><time></yellow>.</aqua>",
                "<aqua>Player <yellow><player1></yellow>'s session play time is: <yellow><time></yellow>.</aqua>",
                TIME_FORMAT,
                "<aqua>Your daily play time is: <yellow><time></yellow>.</aqua>",
                "<aqua>Player <yellow><player1></yellow>'s daily play time is: <yellow><time></yellow>.</aqua>",
                TIME_FORMAT,
                "<aqua>Your weekly play time is: <yellow><time></yellow>.</aqua>",
                "<aqua>Player <yellow><player1></yellow>'s weekly play time is: <yellow><time></yellow>.</aqua>",
                TIME_FORMAT,
                "<aqua>Your monthly play time is: <yellow><time></yellow>.</aqua>",
                "<aqua>Player <yellow><player1></yellow>'s monthly play time is: <yellow><time></yellow>.</aqua>",
                TIME_FORMAT,
                "<aqua>Your yearly play time is: <yellow><time></yellow>.</aqua>",
                "<aqua>Player <yellow><player1></yellow>'s yearly play time is: <yellow><time></yellow>.</aqua>",
                TIME_FORMAT,
                "<aqua>Your total play time is: <yellow><time></yellow>.</aqua>",
                "<aqua>Player <yellow><player1></yellow>'s total play time is: <yellow><time></yellow>.</aqua>",
                TIME_FORMAT,
                "<aqua>Your session play time has been updated. Your session play time is now <yellow><time></yellow>.</aqua>",
                "<aqua>Player <yellow><player1></yellow>'s session play time is now <yellow><time></yellow>.</aqua>",
                TIME_FORMAT,
                "<aqua>Your daily play time has been updated. Your daily play time is now <yellow><time></yellow>.</aqua>",
                "<aqua>Player <yellow><player1></yellow>'s daily play time is now <yellow><time></yellow>.</aqua>",
                TIME_FORMAT,
                "<aqua>Your weekly play time has been updated. Your weekly play time is now <yellow><time></yellow>.</aqua>",
                "<aqua>Player <yellow><player1></yellow>'s weekly play time is now <yellow><time></yellow>.</aqua>",
                TIME_FORMAT,
                "<aqua>Your monthly play time has been updated. Your monthly play time is now <yellow><time></yellow>.</aqua>",
                "<aqua>Player <yellow><player1></yellow>'s monthly time is now <yellow><time></yellow>.</aqua>",
                TIME_FORMAT,
                "<aqua>Your yearly play time has been updated. Your yearly play time is now <yellow><time></yellow>.</aqua>",
                "<aqua>Player <yellow><player1></yellow>'s yearly play time is now <yellow><time></yellow>.</aqua>",
                TIME_FORMAT,
                "<aqua>Your total play time has been updated. Your total play time is now <yellow><time></yellow>.</aqua>",
                "<aqua>Player <yellow><player1></yellow>'s total time is now <yellow><time></yellow>.</aqua>",
                TIME_FORMAT,
                "<aqua>Your session play time has been reset.</aqua>",
                "<aqua>Successfully reset player1 <yellow><player1></yellow>'s session play time.</aqua>",
                "<red>Failed to reset player1 <yellow><player1></yellow>'s session play time.</red>",
                "<aqua>Successfully reset all player1's session play time.</aqua>",
                "<red>Failed to reset all player1's session play time.</red>",
                "<aqua>Your daily play time has been reset.</aqua>",
                "<aqua>Successfully reset player1 <yellow><player1></yellow>'s daily play time.</aqua>",
                "<red>Failed to reset player1 <yellow><player1></yellow>'s daily play time.</red>",
                "<aqua>Successfully reset all player1's daily play time.</aqua>",
                "<red>Failed to reset all player1's daily play time.</red>",
                "<aqua>Your weekly play time has been reset.</aqua>",
                "<aqua>Successfully reset player1 <yellow><player1></yellow>'s weekly play time.</aqua>",
                "<red>Failed to reset player1 <yellow><player1></yellow>'s weekly play time.</red>",
                "<aqua>Successfully reset all player1's weekly play time.</aqua>",
                "<red>Failed to reset all player1's weekly play time.</red>",
                "<aqua>Your monthly play time has been reset.</aqua>",
                "<aqua>Successfully reset player1 <yellow><player1></yellow>'s monthly play time.</aqua>",
                "<red>Failed to reset player1 <yellow><player1></yellow>'s monthly play time.</red>",
                "<aqua>Successfully reset all player1's monthly play time.</aqua>",
                "<red>Failed to reset all player1's monthly play time.</red>",
                "<aqua>Your yearly play time has been reset.</aqua>",
                "<aqua>Successfully reset player1 <yellow><player1></yellow>'s yearly play time.</aqua>",
                "<red>Failed to reset player1 <yellow><player1></yellow>'s yearly play time.</red>",
                "<aqua>Successfully reset all player1's yearly play time.</aqua>",
                "<red>Failed to reset all player1's yearly play time.</red>",
                "<aqua>Your total play time has been reset.</aqua>",
                "<aqua>Successfully reset player1 <yellow><player1></yellow>'s total play time.</aqua>",
                "<red>Failed to reset player1 <yellow><player1></yellow>'s total play time.</red>",
                "<aqua>Successfully reset all player1's total play time.</aqua>",
                "<red>Failed to reset all player1's total play time.</red>",
                "<aqua>All of your play time has been reset.</aqua>",
                "<aqua>Successfully reset all play time for player1 <yellow><player1></yellow>.</aqua>",
                "<red>Failed to reset all play time for player1 <yellow><player1></yellow>.</red>",
                "<aqua>Successfully reset all players' play time.</aqua>",
                "<red>Failed to reset all players' play time.</red>",
                "<red>The plugin failed to read or write to the database.</red>",
                "<aqua>The database has been successfully backed up!</aqua>",
                "<red>The database failed to be backed up!</red>",
                "<aqua>Successfully saved in-memory play-time to the database.</aqua>",
                "<red>Failed to save in-memory play-time to the database.</red>",
                "<aqua>Player <yellow><player1></yellow> is now exempt from top playtime placeholders.<aqua>",
                "<aqua>Player <yellow><player1></yellow> is now unexempt from top playtime placeholders.<aqua>"
        );
    }
}
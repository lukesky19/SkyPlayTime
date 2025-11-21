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
package com.github.lukesky19.skyplaytime;

import com.github.lukesky19.newPlayerPerks.NewPlayerPerksAPI;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skyplaytime.player.manager.AFKManager;
import com.github.lukesky19.skyplaytime.player.manager.ActivityManager;
import com.github.lukesky19.skyplaytime.command.SkyPlayTimeCommand;
import com.github.lukesky19.skyplaytime.command.arguments.AFKCommand;
import com.github.lukesky19.skyplaytime.command.arguments.ListCommand;
import com.github.lukesky19.skyplaytime.leaderboard.manager.LeaderboardManager;
import com.github.lukesky19.skyplaytime.leaderboard.manager.LeaderboardSnapshotManager;
import com.github.lukesky19.skyplaytime.config.manager.locale.LocaleManager;
import com.github.lukesky19.skyplaytime.config.manager.settings.SettingsManager;
import com.github.lukesky19.skyplaytime.database.connection.ConnectionManager;
import com.github.lukesky19.skyplaytime.database.DatabaseManager;
import com.github.lukesky19.skyplaytime.database.queue.QueueManager;
import com.github.lukesky19.skyplaytime.listener.*;
import com.github.lukesky19.skyplaytime.placeholderapi.SkyPlayTimeExpansion;
import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import com.github.lukesky19.skyplaytime.player.manager.TimeManager;
import com.github.lukesky19.skyplaytime.task.TaskManager;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This is the entry point to the SkyPlayTime plugin.
 */
public final class SkyPlayTime extends JavaPlugin {
    private SettingsManager settingsManager;
    private LocaleManager localeManager;
    private DatabaseManager databaseManager;
    private PlayerDataManager playerDataManager;
    private LeaderboardManager leaderboardManager;
    private AFKManager afkManager;
    private TaskManager taskManager;
    private SkyPlayTimeExpansion skyPlayTimeExpansion;

    /**
     * Constructor
     */
    public SkyPlayTime() {}

    /**
     * This method initializes the plugin's data when enabled.
     */
    @Override
    public void onEnable() {
        if(!checkSkyLibVersion()) return;
        @Nullable NewPlayerPerksAPI newPlayerPerksAPI = getNewPlayerPerksAPI();

        // Initialize Classes
        // Config Classes
        settingsManager = new SettingsManager(this);
        localeManager = new LocaleManager(this, settingsManager);
        LeaderboardSnapshotManager leaderboardSnapshotManager = new LeaderboardSnapshotManager(this);

        // Database Classes
        ConnectionManager connectionManager = new ConnectionManager(this);
        QueueManager queueManager = new QueueManager(connectionManager);
        databaseManager = new DatabaseManager(this, connectionManager, queueManager);

        // Manager classes
        playerDataManager = new PlayerDataManager(this, databaseManager);
        leaderboardManager = new LeaderboardManager(leaderboardSnapshotManager, playerDataManager, databaseManager);
        TimeManager timeManager = new TimeManager(this, settingsManager, databaseManager, playerDataManager, leaderboardManager);
        afkManager = new AFKManager(this, settingsManager, localeManager, playerDataManager, newPlayerPerksAPI);
        ActivityManager activityManager = new ActivityManager(playerDataManager);
        taskManager = new TaskManager(this, settingsManager, playerDataManager, timeManager, afkManager, leaderboardManager);

        // Register Listeners
        this.getServer().getPluginManager().registerEvents(new LoginListener(playerDataManager), this);
        this.getServer().getPluginManager().registerEvents(new LogoutListener(playerDataManager), this);
        this.getServer().getPluginManager().registerEvents(new ActivityListener(this, settingsManager, afkManager, activityManager), this);

        // Create and register the API
        SkyPlayTimeAPI skyPlayTimeAPI = new SkyPlayTimeAPI(timeManager, afkManager, leaderboardManager);
        this.getServer().getServicesManager().register(SkyPlayTimeAPI.class, skyPlayTimeAPI, this, ServicePriority.Lowest);

        // Register Commands
        SkyPlayTimeCommand skyPlayTimeCommand = new SkyPlayTimeCommand(this, localeManager, leaderboardSnapshotManager, databaseManager, playerDataManager, leaderboardManager, timeManager, afkManager, activityManager);
        AFKCommand afkCommand = new AFKCommand(this, localeManager, afkManager);
        ListCommand listCommand = new ListCommand(this, localeManager, playerDataManager);
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
            Commands registrar = commands.registrar();
            registrar.register(skyPlayTimeCommand.createCommand(), "Command to manage the SkyPlayTime plugin.", List.of("playtime", "spt"));
            registrar.register(afkCommand.createCommand(), "Command to toggle AFK status for the SkyPlayTime plugin.");
            registrar.register(listCommand.createCommand(), "Command to view the list of online players and their AFK status provided by the SkyPlayTime plugin.");
        });

        // Reload plugin data
        reload(true);

        // Initialize player data for any online players that joined before the plugin was fully enabled.
        // This is mostly for plugman edge cases, but 99% of the time is not necessary.
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        this.getServer().getOnlinePlayers().forEach(player ->
                futureList.add(playerDataManager.loadPlayerData(player, player.getUniqueId())));

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
        allFutures.thenAccept(v1 ->
                leaderboardManager.updateDatabaseTopTen().thenAccept(v2 ->
                        leaderboardManager.updateTopTenAllCategories()));
    }

    /**
     * Reloads any plugin data as necessary.
     * @param onEnable Is the reload occurring on plugin enable?
     */
    public void reload(boolean onEnable) {
        registerExpansion();

        settingsManager.loadSettings();
        localeManager.loadLocale();
        taskManager.restartTasks();

        if(!onEnable) {
            leaderboardManager.updateDatabaseTopTen().thenAccept(v2 ->
                    leaderboardManager.updateTopTenAllCategories());
        }
    }

    /**
     * This method cleans up any plugin data when the plugin is disabled.
     */
    @Override
    public void onDisable() {
        unregisterExpansion();

        if(taskManager != null) {
            taskManager.stopTasks();
        }

        if(playerDataManager != null) {
            playerDataManager.savePlayerData().thenAccept(results -> {
                boolean finalResult = !results.contains(false);

                if (finalResult) {
                    databaseManager.handlePluginDisable();
                } else {
                    this.getComponentLogger().warn(AdventureUtil.deserialize("Failed to save player data on plugin disable. Data loss will occur."));
                    databaseManager.handlePluginDisable();
                }
            }).exceptionally(ex -> {
                this.getComponentLogger().warn(AdventureUtil.deserialize("Failed to save player data on plugin disable. Data loss will occur."));
                databaseManager.handlePluginDisable();
                return null;
            });
        }
    }

    /**
     * This method registers the PlaceholderAPI expansion if PlaceholderAPI is enabled.
     */
    private void registerExpansion() {
        if(this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            if(skyPlayTimeExpansion == null) {
                skyPlayTimeExpansion = new SkyPlayTimeExpansion(localeManager, leaderboardManager, playerDataManager, afkManager);
                skyPlayTimeExpansion.register();
            }
        }
    }

    /**
     * This method unregisters the PlaceholderAPI expansion if PlaceholderAPI is enabled.
     */
    private void unregisterExpansion() {
        if(this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            if(skyPlayTimeExpansion != null) {
                skyPlayTimeExpansion.unregister();
            }
        }
    }

    /**
     * Attempts to retrieve the {@link NewPlayerPerksAPI}.
     */
    private @Nullable NewPlayerPerksAPI getNewPlayerPerksAPI() {
        RegisteredServiceProvider<NewPlayerPerksAPI> provider = Bukkit.getServicesManager().getRegistration(NewPlayerPerksAPI.class);
        if(provider != null) {
            return provider.getProvider();
        }

        return null;
    }

    /**
     * Checks if the Server has the proper SkyLib version.
     * @return true if it does, false if not.
     */
    private boolean checkSkyLibVersion() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        Plugin skyLib = pluginManager.getPlugin("SkyLib");
        if(skyLib != null) {
            String version = skyLib.getPluginMeta().getVersion();
            String[] splitVersion = version.split("\\.");
            int second = Integer.parseInt(splitVersion[1]);

            if(second >= 4) {
                return true;
            }
        }

        this.getComponentLogger().error(AdventureUtil.deserialize("SkyLib Version 1.4.0.0 or newer is required to run this plugin."));
        this.getServer().getPluginManager().disablePlugin(this);
        return false;
    }
}

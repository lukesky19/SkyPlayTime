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

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.paper.api.plugin.SkyPlugin;
import com.github.lukesky19.skyplaytime.algorithm.AlgorithmConfig;
import com.github.lukesky19.skyplaytime.api.SkyPlayTimeAPI;
import com.github.lukesky19.skyplaytime.api.algorithm.Algorithm;
import com.github.lukesky19.skyplaytime.algorithm.AlgorithmConfigManager;
import com.github.lukesky19.skyplaytime.algorithm.AlgorithmManager;
import com.github.lukesky19.skyplaytime.algorithm.impl.*;
import com.github.lukesky19.skyplaytime.integration.HookManager;
import com.github.lukesky19.skyplaytime.listener.block.BlockListener;
import com.github.lukesky19.skyplaytime.listener.connection.LoginListener;
import com.github.lukesky19.skyplaytime.listener.connection.LogoutListener;
import com.github.lukesky19.skyplaytime.listener.fishing.FishListener;
import com.github.lukesky19.skyplaytime.listener.movement.MovementListener;
import com.github.lukesky19.skyplaytime.listener.player.PlayerInteractListener;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import com.github.lukesky19.skyplaytime.player.manager.AFKManager;
import com.github.lukesky19.skyplaytime.command.SkyPlayTimeCommand;
import com.github.lukesky19.skyplaytime.command.arguments.AFKCommand;
import com.github.lukesky19.skyplaytime.command.arguments.ListCommand;
import com.github.lukesky19.skyplaytime.leaderboard.manager.LeaderboardManager;
import com.github.lukesky19.skyplaytime.leaderboard.manager.LeaderboardSnapshotManager;
import com.github.lukesky19.skyplaytime.locale.LocaleManager;
import com.github.lukesky19.skyplaytime.settings.SettingsManager;
import com.github.lukesky19.skyplaytime.database.connection.ConnectionManager;
import com.github.lukesky19.skyplaytime.database.DatabaseManager;
import com.github.lukesky19.skyplaytime.database.queue.QueueManager;
import com.github.lukesky19.skyplaytime.placeholderapi.SkyPlayTimeExpansion;
import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import com.github.lukesky19.skyplaytime.player.manager.TimeManager;
import com.github.lukesky19.skyplaytime.task.TaskManager;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * This is the entry point to the SkyPlayTime plugin.
 */
public final class SkyPlayTime extends SkyPlugin {
    private SettingsManager settingsManager;
    private LocaleManager localeManager;
    private AlgorithmConfigManager algorithmConfigManager;
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

        // Initialize Classes
        // Config Classes
        settingsManager = new SettingsManager(this);
        localeManager = new LocaleManager(this, settingsManager);
        algorithmConfigManager = new AlgorithmConfigManager(this);
        LeaderboardSnapshotManager leaderboardSnapshotManager = new LeaderboardSnapshotManager(this);

        // Database Classes
        ConnectionManager connectionManager = new ConnectionManager(this);
        QueueManager queueManager = new QueueManager(connectionManager);
        databaseManager = new DatabaseManager(this, connectionManager, queueManager);

        // Integration/Hooks
        HookManager hookManager = new HookManager(this);

        // Player Data and Related
        playerDataManager = new PlayerDataManager(this, databaseManager);
        leaderboardManager = new LeaderboardManager(this, leaderboardSnapshotManager, playerDataManager, databaseManager);
        TimeManager timeManager = new TimeManager(this, settingsManager, databaseManager, playerDataManager, leaderboardManager);
        afkManager = new AFKManager(this, settingsManager, localeManager, algorithmConfigManager, playerDataManager, hookManager);

        // Algorithms
        AlgorithmManager algorithmManager = initAlgorithmManager();

        // Tasks
        taskManager = new TaskManager(this, settingsManager, playerDataManager, timeManager, afkManager, leaderboardManager, algorithmManager);

        // Register Listeners
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new LoginListener(algorithmConfigManager, playerDataManager), this);
        pluginManager.registerEvents(new LogoutListener(playerDataManager), this);
        pluginManager.registerEvents(new MovementListener(settingsManager, playerDataManager, afkManager), this);
        pluginManager.registerEvents(new FishListener(playerDataManager), this);
        pluginManager.registerEvents(new BlockListener(playerDataManager), this);
        pluginManager.registerEvents(new PlayerInteractListener(playerDataManager), this);

        // Create and register the API
        SkyPlayTimeAPI skyPlayTimeAPI = new SkyPlayTimeAPI(playerDataManager, timeManager, afkManager, leaderboardManager, algorithmManager);
        this.getServer().getServicesManager().register(SkyPlayTimeAPI.class, skyPlayTimeAPI, this, ServicePriority.Lowest);

        // Register Commands
        SkyPlayTimeCommand skyPlayTimeCommand = new SkyPlayTimeCommand(this, localeManager, leaderboardSnapshotManager, databaseManager, playerDataManager, leaderboardManager, timeManager, afkManager, algorithmManager);
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
        List<CompletableFuture<PlayerData>> futureList = new ArrayList<>();
        this.getServer().getOnlinePlayers().forEach(player ->
                futureList.add(playerDataManager.loadPlayerData(player)));

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0]));
        allFutures.thenAccept(_ -> {
            leaderboardManager.updateDatabaseTopTen().thenAccept(_ ->
                    leaderboardManager.updateTopTenAllCategories());

            AlgorithmConfig algorithmConfig = algorithmConfigManager.getConfiguration();
            if(algorithmConfig != null) {
                long gracePeriod = (long) (System.currentTimeMillis() + (algorithmConfig.gracePeriodSeconds() * 1000));
                playerDataManager.getPlayerDataMap().forEach((_, playerData) -> playerData.setGracePeriod(gracePeriod));
            }

            // Start tasks after player data has been loaded and grace periods applied
            // This is done outside of reload in onEnable to prevent players being marked AFK before grace periods are applied.
            taskManager.restartTasks();
        });
    }

    /**
     * Initialize the {@link AlgorithmManager} and bundled algorithms.
     * @return The {@link AlgorithmManager}.
     */
    private @NonNull AlgorithmManager initAlgorithmManager() {
        AlgorithmManager algorithmManager = new AlgorithmManager();

        Algorithm afkPoolAlgorithm = new AfkPoolAlgorithm(algorithmConfigManager);
        Algorithm bubbleColumnAlgorithm = new BubbleColumnAlgorithm(algorithmConfigManager);
        Algorithm fishingAlgorithm = new FishingAlgorithm(algorithmConfigManager);
        Algorithm generatorAlgorithm = new GeneratorAlgorithm(algorithmConfigManager);
        Algorithm pistonAlgorithm = new PistonAlgorithm(algorithmConfigManager);
        Algorithm timeoutAlgorithm = new TimeoutAlgorithm(algorithmConfigManager);

        algorithmManager.addAlgorithm(afkPoolAlgorithm);
        algorithmManager.addAlgorithm(bubbleColumnAlgorithm);
        algorithmManager.addAlgorithm(fishingAlgorithm);
        algorithmManager.addAlgorithm(generatorAlgorithm);
        algorithmManager.addAlgorithm(pistonAlgorithm);
        algorithmManager.addAlgorithm(timeoutAlgorithm);

        return algorithmManager;
    }

    @Override
    public void reload() {
        reload(false);
    }

    /**
     * Reloads any plugin data as necessary.
     * @param onEnable Is the reload occurring on plugin enable?
     */
    public void reload(boolean onEnable) {
        registerExpansion();

        settingsManager.loadSettings();
        localeManager.loadLocale();
        algorithmConfigManager.loadConfiguration();

        if(!onEnable) {
            taskManager.restartTasks();

            leaderboardManager.updateDatabaseTopTen().thenAccept(_ ->
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

        if(afkManager != null) {
            afkManager.resetOnlinePlayerAFKSettings();
        }

        if(playerDataManager != null) {
            playerDataManager.savePlayerData().thenAccept(results -> {
                boolean finalResult = !results.contains(false);

                if (finalResult) {
                    databaseManager.handlePluginDisable();
                } else {
                    this.getComponentLogger().warn(AdventureUtility.plain("Failed to save player data on plugin disable. Data loss will occur."));
                    databaseManager.handlePluginDisable();
                }
            }).exceptionally(_ -> {
                this.getComponentLogger().warn(AdventureUtility.plain("Failed to save player data on plugin disable. Data loss will occur."));
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
                skyPlayTimeExpansion = new SkyPlayTimeExpansion(leaderboardManager, playerDataManager, afkManager);
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
     * Checks if the Server has the proper SkyLib version.
     * @return true if it does, false if not.
     */
    private boolean checkSkyLibVersion() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        Plugin skyLib = pluginManager.getPlugin("SkyLib");
        if(skyLib != null) {
            String version = skyLib.getPluginMeta().getVersion();
            String[] splitVersion = version.split("\\.");
            int first = Integer.parseInt(splitVersion[0]);

            if(first >= 2) {
                return true;
            }
        }

        this.getComponentLogger().error(AdventureUtility.plain("SkyLib Version 2.0.0.0 or newer is required to run this plugin."));
        this.getServer().getPluginManager().disablePlugin(this);
        return false;
    }
}
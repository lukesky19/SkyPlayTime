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

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.algorithm.AlgorithmConfig;
import com.github.lukesky19.skyplaytime.algorithm.AlgorithmConfigManager;
import com.github.lukesky19.skyplaytime.integration.HookManager;
import com.github.lukesky19.skyplaytime.integration.hook.NewPlayerPerksHook;
import com.github.lukesky19.skyplaytime.locale.Locale;
import com.github.lukesky19.skyplaytime.settings.Settings;
import com.github.lukesky19.skyplaytime.locale.LocaleManager;
import com.github.lukesky19.skyplaytime.settings.SettingsManager;
import com.github.lukesky19.skyplaytime.api.event.AFKStatusChangeEvent;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import com.github.lukesky19.skyplaytime.util.enums.AFKToggleResult;
import com.github.lukesky19.skyplaytime.util.PluginUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class the update and retrieval of player's AFK statuses.
 */
public class AFKManager {
    private final @NonNull SkyPlayTime skyPlayTime;
    private final @NonNull ComponentLogger logger;
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull LocaleManager localeManager;
    private final @NonNull AlgorithmConfigManager algorithmConfigManager;
    private final @NonNull PlayerDataManager playerDataManager;
    private final @NonNull HookManager hookManager;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param algorithmConfigManager An {@link AlgorithmConfigManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param hookManager A {@link HookManager} instance.
     */
    public AFKManager(
            @NonNull SkyPlayTime skyPlayTime,
            @NonNull SettingsManager settingsManager,
            @NonNull LocaleManager localeManager,
            @NonNull AlgorithmConfigManager algorithmConfigManager,
            @NonNull PlayerDataManager playerDataManager,
            @NonNull HookManager hookManager) {
        this.skyPlayTime = skyPlayTime;
        this.logger = skyPlayTime.getComponentLogger();
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.algorithmConfigManager = algorithmConfigManager;
        this.playerDataManager = playerDataManager;
        this.hookManager = hookManager;
    }

    /**
     * Is the player AFK?
     * @param player The {@link Player}.
     * @return true if afk, false if not.
     */
    public boolean isPlayerAFK(@NonNull Player player) {
        Optional<PlayerData> optionalPlayerData = playerDataManager.getPlayerData(player);
        if(optionalPlayerData.isPresent()) {
            return optionalPlayerData.get().isAFK();
        } else {
            logger.warn(AdventureUtility.plain("Unable to check player AFK status due to no player data found for player " + player.getName()));
            return false;
        }
    }

    /**
     * Is the player AFK?
     * @param playerId The {@link UUID} of the player.
     * @return true if afk, false if not.
     */
    public boolean isPlayerAFK(@NonNull UUID playerId) {
        Optional<PlayerData> optionalPlayerData = playerDataManager.getPlayerData(playerId);
        if(optionalPlayerData.isPresent()) {
            return optionalPlayerData.get().isAFK();
        } else {
            logger.warn(AdventureUtility.plain("Unable to check player AFK status due to no player data found for player id " + playerId));
            return false;
        }
    }

    /**
     * Get the a {@link Map} mapping {@link UUID}s to {@link PlayerData} for all AFK players.
     * @return A {@link Map} mapping {@link UUID}s to {@link PlayerData}.
     */
    public @NonNull Map<UUID, PlayerData> getAFKPlayers() {
        return playerDataManager.getPlayerDataMap().entrySet().stream()
                .filter(entry -> entry.getValue().isAFK())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Toggles whether the player is AFK or not. If the target player is vanished, their AFK status change will not be broadcasted to the server regardless of the option provided.
     * @param player The {@link Player}.
     * @param notifyPlayer Should the player be notified of their AFK status change?
     * @param notifyServer Should the server be notified of this player's AFK status change?
     * @param playerInitiated Did the player initiate the AFK Toggle?
     * @return The enum {@link AFKToggleResult} containing the result.
     */
    public @NonNull AFKToggleResult togglePlayerAFK(@NonNull Player player, boolean notifyPlayer, boolean notifyServer, boolean playerInitiated) {
        Settings settings = settingsManager.getSettings();
        Locale locale = localeManager.getLocale();
        AlgorithmConfig algorithmConfig = algorithmConfigManager.getConfiguration();

        // Log an error if plugin settings are invalid and return AFKToggleResult.CONFIG_ERROR
        if(settings == null) {
            logger.warn(AdventureUtility.plain("Failed to toggle AFK Status for player " + player.getName() + " due to invalid plugin settings."));
            return AFKToggleResult.CONFIG_ERROR;
        }

        if(algorithmConfig == null) {
            logger.warn(AdventureUtility.plain("Failed to toggle AFK Status for player " + player.getName() + " due to invalid algorithm configuration settings."));
            return AFKToggleResult.CONFIG_ERROR;
        }

        UUID playerId = player.getUniqueId();
        PlayerData playerData = playerDataManager.getPlayerData(playerId).orElse(null);
        // Log an error if no player data was found and return AFKToggleResult.ERROR
        if(playerData == null) {
            logger.warn(AdventureUtility.plain("Failed to toggle AFK status as no player data was found for player: " + player.getName()));
            return AFKToggleResult.ERROR;
        }

        // Get Player Data
        boolean vanished = PluginUtils.isPlayerVanished(player);
        boolean currentAFKStatus = playerData.isAFK();
        if(vanished) notifyServer = false;

        // Create a AFKStatusChangeEvent and call the event
        AFKStatusChangeEvent afkStatusChangeEvent = new AFKStatusChangeEvent(player, !currentAFKStatus, vanished);
        skyPlayTime.getServer().getPluginManager().callEvent(afkStatusChangeEvent);

        // if the event was cancelled, return AFKToggleResult.CANCELLED
        if(afkStatusChangeEvent.isCancelled()) return AFKToggleResult.CANCELLED;

        // Toggle the target player's AFK status
        if(currentAFKStatus) {
            // Set AFK status to false
            playerData.setAFK(false);
            playerData.setPlayerInitiatedAFK(false);

            // If the target player should be notified that they are no longer AFK, do so here
            if(notifyPlayer) player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.noLongerAfkMessage()));
            // If the server should be notified that the player is no longer AFK, do so here
            if(notifyServer) {
                // Create the placeholders list
                List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("player_name", player.getName()));
                // Get a list of all online players minus the target player.
                List<Player> onlinePlayersExceptTarget = new ArrayList<>(skyPlayTime.getServer().getOnlinePlayers());
                onlinePlayersExceptTarget.remove(player);

                // Create the message to send to all online players
                Component serverMessage = AdventureUtility.deserialize(locale.prefix() + locale.playerNoLongerAfkMessage(), placeholders);
                // Send the message that the target player is no longer AFK
                onlinePlayersExceptTarget.forEach(onlinePlayer -> onlinePlayer.sendMessage(serverMessage));
            }

            // Apply grace period
            playerData.setGracePeriod((long) (System.currentTimeMillis() + (algorithmConfig.gracePeriodSeconds() * 1000)));

            // Reset AFK settings
            resetAFKPlayerSettings(settings, player);

            return AFKToggleResult.SUCCESS_NO_LONGER_AFK;
        } else {
            // Set AFK status to true
            playerData.setAFK(true);
            // Set if player initiated AFK
            if(playerInitiated) playerData.setPlayerInitiatedAFK(true);

            // If the target player should be notified that they are now AFK, do so here
            if(notifyPlayer) player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.afkMessage()));
            // If the server should be notified that the player is now AFK, do so here
            if(notifyServer) {
                // Create the placeholders list
                List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("player_name", player.getName()));
                // Get a list of all online players minus the target player.
                List<Player> onlinePlayersExceptTarget = new ArrayList<>(skyPlayTime.getServer().getOnlinePlayers());
                onlinePlayersExceptTarget.remove(player);

                // Create the message to send to all online players
                Component serverMessage = AdventureUtility.deserialize(locale.prefix() + locale.playerAfkMessage(), placeholders);
                // Send the message that the target player is now AFK
                onlinePlayersExceptTarget.forEach(onlinePlayer -> onlinePlayer.sendMessage(serverMessage));
            }

            // Apply AFK settings
            setAFKPlayerSettings(settings, player);

            return AFKToggleResult.SUCCESS_AFK;
        }
    }

    /**
     * Applies any settings configured to apply to afk players.
     * @param settings The plugin's {@link Settings}.
     * @param player The {@link Player}.
     */
    protected void setAFKPlayerSettings(@NonNull Settings settings, @NonNull Player player) {
        Settings.PlayerSettings playerSettings = settings.playerSettings();

        // Set if the player can pickup items while afk.
        if(!playerSettings.afkItemPickup()) {
            player.setCanPickupItems(false);
        }

        // Set if the player is invulnerable while afk.
        if(playerSettings.afkInvulnerable()) {
            player.setInvulnerable(true);
        }

        // Mark the player as ignored for sleeping through the night while afk.
        if(playerSettings.afkSleeping()) {
            player.setSleepingIgnored(true);
        }
    }

    /**
     * Undoes any settings that were applied to any online players that are AFK.
     */
    public void resetOnlinePlayerAFKSettings() {
        Settings settings = settingsManager.getSettings();
        if(settings == null) return;
        Settings.PlayerSettings playerSettings = settings.playerSettings();
        NewPlayerPerksHook newPlayerPerksHook = hookManager.getHook(NewPlayerPerksHook.class);

        skyPlayTime.getServer().getOnlinePlayers().stream()
                .filter(this::isPlayerAFK)
                .forEach(player ->
                        resetAFKPlayerSettings(playerSettings, newPlayerPerksHook, player));
    }

    /**
     * Undoes any settings that were applied to the AFK player.
     * @param settings The plugin's {@link Settings}.
     * @param player The {@link Player}.
     */
    protected void resetAFKPlayerSettings(@NonNull Settings settings, @NonNull Player player) {
        resetAFKPlayerSettings(settings.playerSettings(), hookManager.getHook(NewPlayerPerksHook.class), player);
    }

    /**
     * Undoes any settings that were applied to the AFK player.
     * @param playerSettings The plugin's {@link Settings.PlayerSettings}.
     * @param newPlayerPerksHook A {@link NewPlayerPerksHook} instance.
     * @param player The {@link Player}.
     */
    protected void resetAFKPlayerSettings(
            Settings.@NonNull PlayerSettings playerSettings,
            @NonNull NewPlayerPerksHook newPlayerPerksHook,
            @NonNull Player player) {
        // Get the player's UUID
        UUID playerId = player.getUniqueId();

        // Reset if the player can pickup items while afk.
        if(!playerSettings.afkItemPickup()) {
            player.setCanPickupItems(true);
        }

        // Reset if the player is invulnerable while afk.
        if(playerSettings.afkInvulnerable()) {
            // If the NewPlayerPerksAPI is not null, check the player's perks
            if(newPlayerPerksHook.isHooked()) {
                // If the player doesn't have perks or the invulnerable perk isn't used, remove invulnerability
                if(!newPlayerPerksHook.hasPerks(playerId) || !newPlayerPerksHook.isInvulnerablePerkEnabled()) {
                    player.setInvulnerable(false);
                }
            } else {
                // Otherwise disable invulnerability
                player.setInvulnerable(false);
            }
        }

        // Reset the player's sleeping ignored status.
        if(playerSettings.afkSleeping()) {
            player.setSleepingIgnored(false);
        }
    }
}
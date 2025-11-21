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
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.config.manager.settings.SettingsManager;
import com.github.lukesky19.skyplaytime.config.manager.locale.LocaleManager;
import com.github.lukesky19.skyplaytime.config.data.locale.Locale;
import com.github.lukesky19.skyplaytime.config.data.settings.Settings;
import com.github.lukesky19.skyplaytime.event.AFKStatusChangeEvent;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import com.github.lukesky19.skyplaytime.util.AFKToggleResult;
import com.github.lukesky19.skyplaytime.util.PluginUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class the update and retrieval of player's AFK statuses.
 */
public class AFKManager {
    private final @NotNull SkyPlayTime skyPlayTime;
    private final @NotNull ComponentLogger logger;
    private final @NotNull SettingsManager settingsManager;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull PlayerDataManager playerDataManager;
    private final @Nullable NewPlayerPerksAPI newPlayerPerksAPI;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param newPlayerPerksAPI A {@link NewPlayerPerksAPI} instance. May be null.
     */
    public AFKManager(
            @NotNull SkyPlayTime skyPlayTime,
            @NotNull SettingsManager settingsManager,
            @NotNull LocaleManager localeManager,
            @NotNull PlayerDataManager playerDataManager,
            @Nullable NewPlayerPerksAPI newPlayerPerksAPI) {
        this.skyPlayTime = skyPlayTime;
        this.logger = skyPlayTime.getComponentLogger();
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
        this.playerDataManager = playerDataManager;
        this.newPlayerPerksAPI = newPlayerPerksAPI;
    }

    /**
     * Is the player AFK?
     * @param uuid The {@link UUID} of the player.
     * @return true if afk, false if not.
     * @throws RuntimeException if there is no player data loaded for the player.
     */
    public boolean isPlayerAFK(@NotNull UUID uuid) {
        @Nullable PlayerData playerData = playerDataManager.getPlayerData(uuid);
        if(playerData == null) {
            throw new RuntimeException("No player data found for UUID " + uuid);
        }

        return playerData.isAFK();
    }

    /**
     * Get the a {@link Map} mapping {@link UUID}s to {@link PlayerData} for all AFK players.
     * @return A {@link Map} mapping {@link UUID}s to {@link PlayerData}.
     */
    public @NotNull Map<UUID, PlayerData> getAFKPlayers() {
        return playerDataManager.getPlayerDataMap().entrySet().stream()
                .filter(entry -> !entry.getValue().isAFK())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Toggles whether the player is AFK or not. If the target player is vanished, their AFK status change will not be broadcasted to the server regardless of the option provided.
     * @param targetPlayer The player to toggle their AFK status for.
     * @param uuid The {@link UUID} of the player.
     * @param notifyPlayer Should the player be notified of their AFK status change?
     * @param notifyServer Should the server be notified of this player's AFK status change?
     * @return The enum {@link AFKToggleResult} containing the result.
     * @throws RuntimeException if there is no player data loaded for the player.
     */
    public @NotNull AFKToggleResult togglePlayerAFK(@NotNull Player targetPlayer, @NotNull UUID uuid, boolean notifyPlayer, boolean notifyServer) {
        @Nullable Settings settings = settingsManager.getSettings();
        Locale locale = localeManager.getLocale();
        @Nullable PlayerData playerData = playerDataManager.getPlayerData(uuid);

        // Log an error if plugin settings are invalid and return AFKToggleResult.CONFIG_ERROR
        if(settings == null) {
            logger.warn(AdventureUtil.deserialize("Failed to toggle AFK Status for player " + targetPlayer.getName() + " due to invalid plugin settings."));
            return AFKToggleResult.CONFIG_ERROR;
        }

        // Log an error if no player data was found and return AFKToggleResult.ERROR
        if(playerData == null) {
            logger.warn(AdventureUtil.deserialize("Failed to toggle AFK status as no player data was found for player: " + targetPlayer.getName()));
            return AFKToggleResult.ERROR;
        }

        // Get Player Data
        boolean currentAFKStatus = playerData.isAFK();
        if(PluginUtils.isPlayerVanished(targetPlayer)) notifyServer = false;

        // Create a AFKStatusChangeEvent and call the event
        AFKStatusChangeEvent afkStatusChangeEvent = new AFKStatusChangeEvent(targetPlayer, !currentAFKStatus);
        skyPlayTime.getServer().getPluginManager().callEvent(afkStatusChangeEvent);

        // if the event was cancelled, return AFKToggleResult.CANCELLED
        if(afkStatusChangeEvent.isCancelled()) return AFKToggleResult.CANCELLED;

        // Toggle the target player's AFK status
        if(currentAFKStatus) {
            // Set AFK status to false
            playerData.setAFK(false);

            // If the target player should be notified that they are no longer AFK, do so here
            if(notifyPlayer) targetPlayer.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.noLongerAfkMessage()));
            // If the server should be notified that the player is no longer AFK, do so here
            if(notifyServer) {
                // Create the placeholders list
                List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("player_name", targetPlayer.getName()));
                // Get a list of all online players minus the target player.
                List<Player> onlinePlayersExceptTarget = new ArrayList<>(skyPlayTime.getServer().getOnlinePlayers());
                onlinePlayersExceptTarget.remove(targetPlayer);

                // Create the message to send to all online players
                Component serverMessage = AdventureUtil.deserialize(locale.prefix() + locale.playerNoLongerAfkMessage(), placeholders);
                // Send the message that the target player is no longer AFK
                onlinePlayersExceptTarget.forEach(player -> player.sendMessage(serverMessage));
            }

            // Reset movement and action time counters to avoid being marked as AFK right away.
            playerData.setLastMoveTime(System.currentTimeMillis());
            playerData.setLastActionTime(System.currentTimeMillis());

            // Reset AFK settings
            resetAFKPlayerSettings(settings, targetPlayer);

            return AFKToggleResult.SUCCESS_NO_LONGER_AFK;
        } else {
            // Set AFK status to true
            playerData.setAFK(true);

            // If the target player should be notified that they are now AFK, do so here
            if(notifyPlayer) targetPlayer.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.afkMessage()));
            // If the server should be notified that the player is now AFK, do so here
            if(notifyServer) {
                // Create the placeholders list
                List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("player_name", targetPlayer.getName()));
                // Get a list of all online players minus the target player.
                List<Player> onlinePlayersExceptTarget = new ArrayList<>(skyPlayTime.getServer().getOnlinePlayers());
                onlinePlayersExceptTarget.remove(targetPlayer);

                // Create the message to send to all online players
                Component serverMessage = AdventureUtil.deserialize(locale.prefix() + locale.playerAfkMessage(), placeholders);
                // Send the message that the target player is now AFK
                onlinePlayersExceptTarget.forEach(player -> player.sendMessage(serverMessage));
            }

            // Apply AFK settings
            setAFKPlayerSettings(settings, targetPlayer);

            return AFKToggleResult.SUCCESS_AFK;
        }
    }

    /**
     * Applies any settings configured to apply to afk players.
     * @param settings The plugin's {@link Settings}.
     * @param player The {@link Player}.
     */
    private void setAFKPlayerSettings(@NotNull Settings settings, @NotNull Player player) {
        Settings.PlayerSettings playerSettings = settings.afkSettings().playerSettings();

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
     * Undoes any settings that were applied to the AFK player.
     * @param settings The plugin's {@link Settings}.
     * @param player The {@link Player}.
     */
    private void resetAFKPlayerSettings(@NotNull Settings settings, @NotNull Player player) {
        // Get the player's UUID
        UUID uuid = player.getUniqueId();

        Settings.PlayerSettings playerSettings = settings.afkSettings().playerSettings();
        // Reset if the player can pickup items while afk.
        if(!playerSettings.afkItemPickup()) {
            player.setCanPickupItems(true);
        }

        // Reset if the player is invulnerable while afk.
        if(playerSettings.afkInvulnerable()) {
            // If the NewPlayerPerksAPI is not null, check the player's perks
            if(newPlayerPerksAPI != null) {
                // If the player doesn't have perks or the invulnerable perk isn't used, remove invulnerability
                if(!newPlayerPerksAPI.hasPerks(uuid) || !newPlayerPerksAPI.isInvulnerablePerkEnabled()) {
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

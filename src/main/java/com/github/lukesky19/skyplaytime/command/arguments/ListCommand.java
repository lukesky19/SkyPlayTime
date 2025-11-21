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
package com.github.lukesky19.skyplaytime.command.arguments;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.config.manager.locale.LocaleManager;
import com.github.lukesky19.skyplaytime.config.data.locale.Locale;
import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import com.github.lukesky19.skyplaytime.util.PluginUtils;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class is used to create the list command used to list online players and their AFK status.
 */
public class ListCommand {
    private final @NotNull SkyPlayTime skyPlayTime;
    private final @NotNull ComponentLogger logger;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull PlayerDataManager playerDataManager;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     */
    public ListCommand(@NotNull SkyPlayTime skyPlayTime, @NotNull LocaleManager localeManager, @NotNull PlayerDataManager playerDataManager) {
        this.skyPlayTime = skyPlayTime;
        this.logger = skyPlayTime.getComponentLogger();
        this.localeManager = localeManager;
        this.playerDataManager = playerDataManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the list command and command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack}.
     */
    public LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("list")
                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.list"))
                .executes(ctx -> {
                    Locale locale = localeManager.getLocale();
                    CommandSender sender = ctx.getSource().getSender();

                    // Get a list of all names and their AFK status indicators, excluding vanished players
                    List<String> playerNamesAndStatuses = getPlayerNamesAndStatuses(locale);

                    // Get the server's player count excluding vanished players
                    long playerCount = skyPlayTime.getServer().getOnlinePlayers().stream().filter(PluginUtils::isPlayerVanished).count();
                    // Create the placeholders list
                    List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("player_count", String.valueOf(playerCount)));

                    // Create the final message to display to players contain all player names and statuses, excluding vanished players
                    String finalMessage = String.join(locale.delimiter(), playerNamesAndStatuses);

                    // Send the list title and message containing player names and statuses, excluding vanished players
                    sender.sendMessage(AdventureUtil.deserialize(locale.listTitle(), placeholders));
                    sender.sendMessage(AdventureUtil.deserialize(finalMessage));

                    return 1;
                }).build();
    }

    /**
     * Get a {@link List} of {@link String}s for player names and their AFK status. Vanished players are not included.
     * @param locale The plugin's {@link Locale}.
     * @return A {@link List} of {@link String}s for player names and their AFK status. Vanished players are not included.
     */
    private @NotNull List<String> getPlayerNamesAndStatuses(Locale locale) {
        @NotNull Map<UUID, PlayerData> playerDataMap = playerDataManager.getPlayerDataMap();
        List<String> playerNamesAndStatuses = new ArrayList<>();

        playerDataMap.forEach((uuid, playerData) -> {
            Player player = skyPlayTime.getServer().getPlayer(uuid);
            if(player != null && player.isOnline() && player.isConnected()) {
                if(!PluginUtils.isPlayerVanished(player)) {
                    String playerName = AdventureUtil.serialize(AdventureUtil.deserialize(locale.playerName(), List.of(Placeholder.parsed("player_name", playerData.getName()))));

                    if(playerData.isAFK()) {
                        playerNamesAndStatuses.add(playerName + locale.afkIndicator());
                    } else {
                        playerNamesAndStatuses.add(playerName);
                    }
                }
            } else {
                logger.warn(AdventureUtil.deserialize("There is player data stored for a player that is invalid (null), offline, and or not connected!"));
            }
        });

        return playerNamesAndStatuses;
    }
}

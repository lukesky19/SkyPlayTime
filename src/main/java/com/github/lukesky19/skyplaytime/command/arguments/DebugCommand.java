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

import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.paper.api.adventure.PaperAdventureUtility;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.algorithm.AlgorithmManager;
import com.github.lukesky19.skyplaytime.locale.LocaleManager;
import com.github.lukesky19.skyplaytime.locale.Locale;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import com.github.lukesky19.skyplaytime.player.manager.AFKManager;
import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.UUID;

/**
 * This class is used to create the debug command which is used to debug issues.
 */
public class DebugCommand {
    private final @NonNull SkyPlayTime skyPlayTime;
    private final @NonNull ComponentLogger logger;
    private final @NonNull LocaleManager localeManager;
    private final @NonNull PlayerDataManager playerDataManager;
    private final @NonNull AFKManager afkManager;
    private final @NonNull AlgorithmManager algorithmManager;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param afkManager An {@link AFKManager} instance.
     * @param algorithmManager An {@link AlgorithmManager} instance.
     */
    public DebugCommand(
            @NonNull SkyPlayTime skyPlayTime,
            @NonNull LocaleManager localeManager,
            @NonNull PlayerDataManager playerDataManager,
            @NonNull AFKManager afkManager,
            @NonNull AlgorithmManager algorithmManager) {
        this.skyPlayTime = skyPlayTime;
        this.logger = skyPlayTime.getComponentLogger();
        this.localeManager = localeManager;
        this.playerDataManager = playerDataManager;
        this.afkManager = afkManager;
        this.algorithmManager = algorithmManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the debug command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack}.
     */
    public LiteralCommandNode<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("debug");
        builder.requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.debug"));

        builder.then(Commands.literal("status")
                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.debug.status"))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.debug.status.others"))
                        .executes(ctx -> {
                            Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                            UUID targetUUID = target.getUniqueId();
                            Locale locale = localeManager.getLocale();

                            boolean status = afkManager.isPlayerAFK(targetUUID);
                            String afkText = (status ? "AFK" : "Not AFK");
                            String statusMessage = "<aqua>Player <yellow>" + target.getName() + "</yellow>'s AFK status is: <yellow>" + afkText + "</yellow>.</aqua>";

                            if(ctx.getSource().getSender() instanceof Player player) {
                                player.sendMessage(PaperAdventureUtility.deserialize(target, locale.prefix() + statusMessage));
                            } else {
                                logger.info(PaperAdventureUtility.deserialize(target, statusMessage));
                            }

                            return 1;
                        })
                )

                .executes(ctx -> {
                    Locale locale = localeManager.getLocale();

                    if(ctx.getSource().getSender() instanceof Player player) {
                        boolean status = afkManager.isPlayerAFK(player.getUniqueId());
                        String afkText = (status ? "AFK" : "Not AFK");
                        String statusMessage = "<aqua>Your AFK status is: <yellow>" + afkText + "</yellow>.</aqua>";

                        player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + statusMessage));

                        return 1;
                    } else {
                        logger.info(PaperAdventureUtility.deserialize(locale.commandPlayerOnly()));

                        return 0;
                    }
                })
        );

        builder.then(Commands.literal("algorithms")
            .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.debug.algorithms"))
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes(ctx -> {
                    CommandSender sender = ctx.getSource().getSender();
                    Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                    UUID targetUUID = target.getUniqueId();

                    return playerDataManager.getPlayerData(targetUUID)
                            .map(playerData -> {
                                sender.sendMessage(AdventureUtility.deserialize("Algorithm Results for Player " + target.getName()));
                                algorithmManager.getAlgorithmList().forEach(algorithm ->
                                        sender.sendMessage(AdventureUtility.deserialize("Algorithm: " + algorithm.getName() + " | Result: " + algorithm.isPlayerInactive(target, playerData))));

                                return 1;
                            })
                            .orElse(0);
                })
        ));

        builder.then(Commands.literal("list")
                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.debug.list"))
                .then(Commands.literal("active")
                        .executes(ctx -> {
                            Map<UUID, PlayerData> activePlayersData = playerDataManager.getActivePlayerData();

                            if(ctx.getSource().getSender() instanceof Player senderPlayer) {
                                senderPlayer.sendMessage(PaperAdventureUtility.deserialize("<green>Green</green> <white>- Online and Play Time Tracked"));
                                senderPlayer.sendMessage(PaperAdventureUtility.deserialize("<yellow>Yellow</yellow> <white>- Offline and Play Time Tracked"));
                                senderPlayer.sendMessage(PaperAdventureUtility.deserialize("<red>Red</red> <white>- Unknown and Play Time Tracked"));
                                senderPlayer.sendMessage(PaperAdventureUtility.deserialize(" "));
                                senderPlayer.sendMessage(PaperAdventureUtility.deserialize("<aqua>A list of all players with active play time being tracked:"));

                                activePlayersData.keySet()
                                        .forEach(uuid -> {
                                            Player targetPlayer = skyPlayTime.getServer().getPlayer(uuid);
                                            if(targetPlayer != null) {
                                                if(targetPlayer.isOnline() && targetPlayer.isConnected()) {
                                                    senderPlayer.sendMessage(PaperAdventureUtility.deserialize("<green>" + targetPlayer.getName()));
                                                } else {
                                                    senderPlayer.sendMessage(PaperAdventureUtility.deserialize("<yellow>" + targetPlayer.getName()));
                                                }
                                            } else {
                                                senderPlayer.sendMessage(PaperAdventureUtility.deserialize("<red>Unknown Player: " + uuid));
                                            }
                                        });
                            } else {
                                logger.info(PaperAdventureUtility.deserialize("<green>Green</green> <white>- Online and Play Time Tracked"));
                                logger.info(PaperAdventureUtility.deserialize("<yellow>Yellow</yellow> <white>- Offline and Play Time Tracked"));
                                logger.info(PaperAdventureUtility.deserialize("<red>Red</red> <white>- Unknown and Play Time Tracked"));
                                logger.info(PaperAdventureUtility.deserialize(" "));
                                logger.info(PaperAdventureUtility.deserialize("<aqua>A list of all players with active play time being tracked:"));

                                activePlayersData.keySet()
                                        .forEach(uuid -> {
                                            Player targetPlayer = skyPlayTime.getServer().getPlayer(uuid);
                                            if(targetPlayer != null) {
                                                if(targetPlayer.isOnline() && targetPlayer.isConnected()) {
                                                    logger.info(PaperAdventureUtility.deserialize("<green>" + targetPlayer.getName()));
                                                } else {
                                                    logger.info(PaperAdventureUtility.deserialize("<yellow>" + targetPlayer.getName()));
                                                }
                                            } else {
                                                logger.info(PaperAdventureUtility.deserialize("<red>Unknown Player: " + uuid));
                                            }
                                        });
                            }

                            return 1;
                        })
                )

                .then(Commands.literal("afk")
                        .executes(ctx -> {
                            Map<UUID, PlayerData> afkPlayersData = afkManager.getAFKPlayers();

                            if(ctx.getSource().getSender() instanceof Player senderPlayer) {
                                senderPlayer.sendMessage(PaperAdventureUtility.deserialize("<green>Green</green> <white>- Online and AFK"));
                                senderPlayer.sendMessage(PaperAdventureUtility.deserialize("<yellow>Yellow</yellow> <white>- Offline and AFK"));
                                senderPlayer.sendMessage(PaperAdventureUtility.deserialize("<red>Red</red> <white>- Unknown and AFK"));
                                senderPlayer.sendMessage(PaperAdventureUtility.deserialize(" "));
                                senderPlayer.sendMessage(PaperAdventureUtility.deserialize("<aqua>A list of all players that are afk and time is not tracked:"));

                                afkPlayersData.keySet()
                                        .forEach(uuid -> {
                                            Player targetPlayer = skyPlayTime.getServer().getPlayer(uuid);
                                            if(targetPlayer != null) {
                                                if(targetPlayer.isOnline() && targetPlayer.isConnected()) {
                                                    senderPlayer.sendMessage(PaperAdventureUtility.deserialize("<green>" + targetPlayer.getName()));
                                                } else {
                                                    senderPlayer.sendMessage(PaperAdventureUtility.deserialize("<yellow>" + targetPlayer.getName()));
                                                }
                                            } else {
                                                senderPlayer.sendMessage(PaperAdventureUtility.deserialize("<red>Unknown Player: " + uuid));
                                            }
                                        });
                            } else {
                                logger.info(PaperAdventureUtility.deserialize("<green>Green</green> <white>- Online and AFK"));
                                logger.info(PaperAdventureUtility.deserialize("<yellow>Yellow</yellow> <white>- Offline and AFK"));
                                logger.info(PaperAdventureUtility.deserialize("<red>Red</red> <white>- Unknown and AFK"));
                                logger.info(PaperAdventureUtility.deserialize(" "));
                                logger.info(PaperAdventureUtility.deserialize("<aqua>A list of all players that are afk and time is not tracked:"));

                                afkPlayersData.keySet()
                                        .forEach(uuid -> {
                                            Player targetPlayer = skyPlayTime.getServer().getPlayer(uuid);
                                            if(targetPlayer != null) {
                                                if(targetPlayer.isOnline() && targetPlayer.isConnected()) {
                                                    logger.info(PaperAdventureUtility.deserialize("<green>" + targetPlayer.getName()));
                                                } else {
                                                    logger.info(PaperAdventureUtility.deserialize("<yellow>" + targetPlayer.getName()));
                                                }
                                            } else {
                                                logger.info(PaperAdventureUtility.deserialize("<red>Unknown Player: " + uuid));
                                            }
                                        });
                            }

                            return 1;
                        })
                )
        );

        return builder.build();
    }
}

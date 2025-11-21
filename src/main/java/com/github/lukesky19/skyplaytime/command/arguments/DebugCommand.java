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
import com.github.lukesky19.skylib.api.time.TimeUtil;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.config.manager.locale.LocaleManager;
import com.github.lukesky19.skyplaytime.config.data.locale.Locale;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import com.github.lukesky19.skyplaytime.player.manager.AFKManager;
import com.github.lukesky19.skyplaytime.player.manager.ActivityManager;
import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;

/**
 * This class is used to create the debug command which is used to debug issues.
 */
public class DebugCommand {
    private final @NotNull SkyPlayTime skyPlayTime;
    private final @NotNull ComponentLogger logger;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull PlayerDataManager playerDataManager;
    private final @NotNull AFKManager afkManager;
    private final @NotNull ActivityManager activityManager;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param afkManager An {@link AFKManager} instance.
     * @param activityManager An {@link ActivityManager} instance.
     */
    public DebugCommand(
            @NotNull SkyPlayTime skyPlayTime,
            @NotNull LocaleManager localeManager,
            @NotNull PlayerDataManager playerDataManager,
            @NotNull AFKManager afkManager,
            @NotNull ActivityManager activityManager) {
        this.skyPlayTime = skyPlayTime;
        this.logger = skyPlayTime.getComponentLogger();
        this.localeManager = localeManager;
        this.playerDataManager = playerDataManager;
        this.afkManager = afkManager;
        this.activityManager = activityManager;
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
                                player.sendMessage(AdventureUtil.deserialize(target, locale.prefix() + statusMessage));
                            } else {
                                logger.info(AdventureUtil.deserialize(target, statusMessage));
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

                        player.sendMessage(AdventureUtil.deserialize(player, locale.prefix() + statusMessage));

                        return 1;
                    } else {
                        logger.info(AdventureUtil.deserialize(locale.commandPlayerOnly()));

                        return 0;
                    }
                })
        );

        builder.then(Commands.literal("last-move")
                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.last-move"))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .executes(ctx -> {
                            Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                            UUID targetUUID = target.getUniqueId();
                            Locale locale = localeManager.getLocale();

                            String timeStampFormat = "MM-dd-yyyy HH:mm:ss";
                            String timeMessage = TimeUtil.millisToTimeStamp(activityManager.getLastMoveTime(targetUUID), ZoneId.of("America/New_York"), timeStampFormat);
                            String lastMoveMessage = "<aqua>Player <yellow>" + target.getName() + "</yellow> last moved at <yellow>" + timeMessage + "</yellow>.</aqua>";

                            if(ctx.getSource().getSender() instanceof Player player) {
                                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + lastMoveMessage));
                            } else {
                                logger.info(AdventureUtil.deserialize(lastMoveMessage));
                            }

                            return 1;
                        })
                )
        );

        builder.then(Commands.literal("last-interact")
                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.last-interact"))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .executes(ctx -> {
                            Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                            UUID targetUUID = target.getUniqueId();
                            Locale locale = localeManager.getLocale();

                            String timeStampFormat = "MM-dd-yyyy HH:mm:ss";
                            String timeMessage = TimeUtil.millisToTimeStamp(activityManager.getLastActionTime(targetUUID), ZoneId.of("America/New_York"), timeStampFormat);
                            String lastMoveMessage = "<aqua>Player <yellow>" + target.getName() + "</yellow> last interacted at <yellow>" + timeMessage + "</yellow>.</aqua>";

                            if(ctx.getSource().getSender() instanceof Player player) {
                                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + lastMoveMessage));
                            } else {
                                logger.info(AdventureUtil.deserialize(lastMoveMessage));
                            }

                            return 1;
                        })
                )
        );

        builder.then(Commands.literal("list")
                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.debug.list"))
                .then(Commands.literal("active")
                        .executes(ctx -> {
                            @NotNull Map<UUID, PlayerData> activePlayersData = playerDataManager.getActivePlayerData();

                            if(ctx.getSource().getSender() instanceof Player senderPlayer) {
                                senderPlayer.sendMessage(AdventureUtil.deserialize("<green>Green</green> <white>- Online and Play Time Tracked"));
                                senderPlayer.sendMessage(AdventureUtil.deserialize("<yellow>Yellow</yellow> <white>- Offline and Play Time Tracked"));
                                senderPlayer.sendMessage(AdventureUtil.deserialize("<red>Red</red> <white>- Unknown and Play Time Tracked"));
                                senderPlayer.sendMessage(AdventureUtil.deserialize(" "));
                                senderPlayer.sendMessage(AdventureUtil.deserialize("<aqua>A list of all players with active play time being tracked:"));

                                activePlayersData.keySet()
                                        .forEach(uuid -> {
                                            Player targetPlayer = skyPlayTime.getServer().getPlayer(uuid);
                                            if(targetPlayer != null) {
                                                if(targetPlayer.isOnline() && targetPlayer.isConnected()) {
                                                    senderPlayer.sendMessage(AdventureUtil.deserialize("<green>" + targetPlayer.getName()));
                                                } else {
                                                    senderPlayer.sendMessage(AdventureUtil.deserialize("<yellow>" + targetPlayer.getName()));
                                                }
                                            } else {
                                                senderPlayer.sendMessage(AdventureUtil.deserialize("<red>Unknown Player: " + uuid));
                                            }
                                        });
                            } else {
                                logger.info(AdventureUtil.deserialize("<green>Green</green> <white>- Online and Play Time Tracked"));
                                logger.info(AdventureUtil.deserialize("<yellow>Yellow</yellow> <white>- Offline and Play Time Tracked"));
                                logger.info(AdventureUtil.deserialize("<red>Red</red> <white>- Unknown and Play Time Tracked"));
                                logger.info(AdventureUtil.deserialize(" "));
                                logger.info(AdventureUtil.deserialize("<aqua>A list of all players with active play time being tracked:"));

                                activePlayersData.keySet()
                                        .forEach(uuid -> {
                                            Player targetPlayer = skyPlayTime.getServer().getPlayer(uuid);
                                            if(targetPlayer != null) {
                                                if(targetPlayer.isOnline() && targetPlayer.isConnected()) {
                                                    logger.info(AdventureUtil.deserialize("<green>" + targetPlayer.getName()));
                                                } else {
                                                    logger.info(AdventureUtil.deserialize("<yellow>" + targetPlayer.getName()));
                                                }
                                            } else {
                                                logger.info(AdventureUtil.deserialize("<red>Unknown Player: " + uuid));
                                            }
                                        });
                            }

                            return 1;
                        })
                )

                .then(Commands.literal("afk")
                        .executes(ctx -> {
                            @NotNull Map<UUID, PlayerData> afkPlayersData = afkManager.getAFKPlayers();

                            if(ctx.getSource().getSender() instanceof Player senderPlayer) {
                                senderPlayer.sendMessage(AdventureUtil.deserialize("<green>Green</green> <white>- Online and AFK"));
                                senderPlayer.sendMessage(AdventureUtil.deserialize("<yellow>Yellow</yellow> <white>- Offline and AFK"));
                                senderPlayer.sendMessage(AdventureUtil.deserialize("<red>Red</red> <white>- Unknown and AFK"));
                                senderPlayer.sendMessage(AdventureUtil.deserialize(" "));
                                senderPlayer.sendMessage(AdventureUtil.deserialize("<aqua>A list of all players that are afk and time is not tracked:"));

                                afkPlayersData.keySet()
                                        .forEach(uuid -> {
                                            Player targetPlayer = skyPlayTime.getServer().getPlayer(uuid);
                                            if(targetPlayer != null) {
                                                if(targetPlayer.isOnline() && targetPlayer.isConnected()) {
                                                    senderPlayer.sendMessage(AdventureUtil.deserialize("<green>" + targetPlayer.getName()));
                                                } else {
                                                    senderPlayer.sendMessage(AdventureUtil.deserialize("<yellow>" + targetPlayer.getName()));
                                                }
                                            } else {
                                                senderPlayer.sendMessage(AdventureUtil.deserialize("<red>Unknown Player: " + uuid));
                                            }
                                        });
                            } else {
                                logger.info(AdventureUtil.deserialize("<green>Green</green> <white>- Online and AFK"));
                                logger.info(AdventureUtil.deserialize("<yellow>Yellow</yellow> <white>- Offline and AFK"));
                                logger.info(AdventureUtil.deserialize("<red>Red</red> <white>- Unknown and AFK"));
                                logger.info(AdventureUtil.deserialize(" "));
                                logger.info(AdventureUtil.deserialize("<aqua>A list of all players that are afk and time is not tracked:"));

                                afkPlayersData.keySet()
                                        .forEach(uuid -> {
                                            Player targetPlayer = skyPlayTime.getServer().getPlayer(uuid);
                                            if(targetPlayer != null) {
                                                if(targetPlayer.isOnline() && targetPlayer.isConnected()) {
                                                    logger.info(AdventureUtil.deserialize("<green>" + targetPlayer.getName()));
                                                } else {
                                                    logger.info(AdventureUtil.deserialize("<yellow>" + targetPlayer.getName()));
                                                }
                                            } else {
                                                logger.info(AdventureUtil.deserialize("<red>Unknown Player: " + uuid));
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

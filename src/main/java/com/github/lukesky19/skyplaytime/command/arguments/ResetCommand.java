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
import com.github.lukesky19.skyplaytime.player.manager.TimeManager;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This class is used to create the reset command used to reset play time for a player or all players.
 */
public class ResetCommand {
    private final @NotNull SkyPlayTime skyPlayTime;
    private final @NotNull ComponentLogger logger;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull TimeManager timeManager;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param timeManager A {@link TimeManager} instance.
     */
    public ResetCommand(
            @NotNull SkyPlayTime skyPlayTime,
            @NotNull LocaleManager localeManager,
            @NotNull TimeManager timeManager) {
        this.skyPlayTime = skyPlayTime;
        this.logger = skyPlayTime.getComponentLogger();
        this.localeManager = localeManager;
        this.timeManager = timeManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the reset command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack}.
     */
    public LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("reset")
                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.reset"))
                .then(Commands.literal("session")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .executes(ctx -> {
                                    Locale locale = localeManager.getLocale();
                                    Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                    UUID targetUUID = target.getUniqueId();

                                    // Reset the desired play time
                                    timeManager.resetPlayTime(targetUUID, true, false, false, false, false, false);

                                    // Create the placeholders list
                                    List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("player_name", target.getName()));

                                    // Tell the target player that their play time was reset
                                    target.sendMessage(AdventureUtil.deserialize(target,locale.prefix() + locale.sessionPlayTimeReset(), placeholders));

                                    // Tell the sender that the target had their play time reset
                                    if(ctx.getSource().getSender() instanceof Player player) {
                                        player.sendMessage(AdventureUtil.deserialize(target, locale.prefix() + locale.playerSessionPlayTimeReset(), placeholders));
                                    } else {
                                        logger.info(AdventureUtil.deserialize(target, locale.playerSessionPlayTimeReset(), placeholders));
                                    }

                                    return 1;
                                })
                        )

                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();

                            // Reset the desired play time
                            timeManager.resetPlayTime(true, false, false, false, false, false);

                            // Tell all online players that their play time was reset
                            for(Player player : skyPlayTime.getServer().getOnlinePlayers()) {
                                player.sendMessage(AdventureUtil.deserialize(player, locale.prefix() + locale.sessionPlayTimeReset()));
                            }

                            // Tell the sender that the all players had their play time reset
                            if(ctx.getSource().getSender() instanceof Player player) {
                                player.sendMessage(AdventureUtil.deserialize(player, locale.prefix() + locale.resetSessionPlayTime()));
                            } else {
                                logger.info(AdventureUtil.deserialize(locale.resetSessionPlayTime()));
                            }

                            return 1;
                        })
                )

                .then(Commands.literal("daily")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .executes(ctx -> {
                                    Locale locale = localeManager.getLocale();
                                    Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                    UUID targetUUID = target.getUniqueId();
                                    CommandSender sender = ctx.getSource().getSender();
                                    boolean isSenderPlayer = sender instanceof Player;

                                    // Create the placeholders list
                                    List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("player_name", target.getName()));

                                    // Reset the desired play time
                                    boolean result = timeManager.resetPlayTime(targetUUID, false,true, false, false, false, false);

                                    if(result) {
                                        // Tell the target player that their daily play time was reset
                                        target.sendMessage(AdventureUtil.deserialize(target,locale.prefix() + locale.dailyPlayTimeReset(), placeholders));

                                        // Tell the command sender (if a player) or log to console that the target had their daily play time reset
                                        if(isSenderPlayer) {
                                            sender.sendMessage(AdventureUtil.deserialize(target, locale.prefix() + locale.playerDailyPlayTimeReset(), placeholders));
                                        } else {
                                            logger.info(AdventureUtil.deserialize(target, locale.playerDailyPlayTimeReset(), placeholders));
                                        }

                                        return 1;
                                    } else {
                                        // Tell the command sender (if a player) or log to console that the target failed to have their daily play time reset
                                        if(isSenderPlayer) {
                                            sender.sendMessage(AdventureUtil.deserialize(target, locale.prefix() + locale.playerDailyPlayTimeResetError(), placeholders));
                                        } else {
                                            logger.warn(AdventureUtil.deserialize(target, locale.playerDailyPlayTimeResetError(), placeholders));
                                        }

                                        return 0;
                                    }
                                })
                        )

                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();
                            CommandSender sender = ctx.getSource().getSender();
                            boolean isSenderPlayer = sender instanceof Player;

                            // Reset the desired play time
                            CompletableFuture<Boolean> resultFuture = timeManager.resetPlayTime(false,true, false, false, false, false);
                            resultFuture.thenAccept(result -> {
                                if(result) {
                                    // Tell all online players that their daily play time was reset
                                    for(Player player : skyPlayTime.getServer().getOnlinePlayers()) {
                                        player.sendMessage(AdventureUtil.deserialize(player, locale.prefix() + locale.dailyPlayTimeReset()));
                                    }

                                    // Notify the sender that all daily play time was reset
                                    if(isSenderPlayer) {
                                        sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.resetDailyPlayTime()));
                                    } else {
                                        logger.info(AdventureUtil.deserialize(locale.resetDailyPlayTime()));
                                    }
                                } else {
                                    // Notify the sender that all daily play time failed to be reset
                                    if(isSenderPlayer) {
                                        sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.resetDailyPlayTimeError()));
                                    } else {
                                        logger.info(AdventureUtil.deserialize(locale.resetDailyPlayTimeError()));
                                    }
                                }
                            }).exceptionally(ex -> {
                                // Notify the sender that all daily play time failed to be reset
                                if(isSenderPlayer) {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.resetDailyPlayTimeError()));
                                } else {
                                    logger.info(AdventureUtil.deserialize(locale.resetDailyPlayTimeError()));
                                }

                                return null;
                            });

                            return 1;
                        })
                )

                .then(Commands.literal("weekly")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .executes(ctx -> {
                                    Locale locale = localeManager.getLocale();
                                    Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                    UUID targetUUID = target.getUniqueId();
                                    CommandSender sender = ctx.getSource().getSender();
                                    boolean isSenderPlayer = sender instanceof Player;

                                    // Create the placeholders list
                                    List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("player_name", target.getName()));

                                    // Reset the desired play time
                                    boolean result = timeManager.resetPlayTime(targetUUID, false,false, true, false, false, false);

                                    if(result) {
                                        // Tell the target player that their weekly play time was reset
                                        target.sendMessage(AdventureUtil.deserialize(target,locale.prefix() + locale.weeklyPlayTimeReset(), placeholders));

                                        // Tell the command sender (if a player) or log to console that the target had their weekly play time reset
                                        if(isSenderPlayer) {
                                            sender.sendMessage(AdventureUtil.deserialize(target, locale.prefix() + locale.playerWeeklyPlayTimeReset(), placeholders));
                                        } else {
                                            logger.info(AdventureUtil.deserialize(target, locale.playerWeeklyPlayTimeReset(), placeholders));
                                        }

                                        return 1;
                                    } else {
                                        // Tell the command sender (if a player) or log to console that the target failed to have their weekly play time reset
                                        if(isSenderPlayer) {
                                            sender.sendMessage(AdventureUtil.deserialize(target, locale.prefix() + locale.playerWeeklyPlayTimeResetError(), placeholders));
                                        } else {
                                            logger.warn(AdventureUtil.deserialize(target, locale.playerWeeklyPlayTimeResetError(), placeholders));
                                        }

                                        return 0;
                                    }
                                })
                        )

                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();
                            CommandSender sender = ctx.getSource().getSender();
                            boolean isSenderPlayer = sender instanceof Player;

                            // Reset the desired play time
                            CompletableFuture<Boolean> resultFuture = timeManager.resetPlayTime(false,false, true, false, false, false);
                            resultFuture.thenAccept(result -> {
                                if(result) {
                                    // Tell all online players that their weekly play time was reset
                                    for(Player player : skyPlayTime.getServer().getOnlinePlayers()) {
                                        player.sendMessage(AdventureUtil.deserialize(player, locale.prefix() + locale.weeklyPlayTimeReset()));
                                    }

                                    // Notify the sender that all weekly play time was reset
                                    if(isSenderPlayer) {
                                        sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.resetWeeklyPlayTime()));
                                    } else {
                                        logger.info(AdventureUtil.deserialize(locale.resetWeeklyPlayTime()));
                                    }
                                } else {
                                    // Notify the sender that all weekly play time failed to be reset
                                    if(isSenderPlayer) {
                                        sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.resetWeeklyPlayTimeError()));
                                    } else {
                                        logger.info(AdventureUtil.deserialize(locale.resetWeeklyPlayTimeError()));
                                    }
                                }
                            }).exceptionally(ex -> {
                                // Notify the sender that all weekly play time failed to be reset
                                if(isSenderPlayer) {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.resetWeeklyPlayTimeError()));
                                } else {
                                    logger.info(AdventureUtil.deserialize(locale.resetWeeklyPlayTimeError()));
                                }

                                return null;
                            });

                            return 1;
                        })
                )

                .then(Commands.literal("monthly")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .executes(ctx -> {
                                    Locale locale = localeManager.getLocale();
                                    Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                    UUID targetUUID = target.getUniqueId();
                                    CommandSender sender = ctx.getSource().getSender();
                                    boolean isSenderPlayer = sender instanceof Player;

                                    // Create the placeholders list
                                    List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("player_name", target.getName()));

                                    // Reset the desired play time
                                    boolean result = timeManager.resetPlayTime(targetUUID, false,false, false, true, false, false);

                                    if(result) {
                                        // Tell the target player that their monthly play time was reset
                                        target.sendMessage(AdventureUtil.deserialize(target,locale.prefix() + locale.monthlyPlayTimeReset(), placeholders));

                                        // Tell the command sender (if a player) or log to console that the target had their monthly play time reset
                                        if(isSenderPlayer) {
                                            sender.sendMessage(AdventureUtil.deserialize(target, locale.prefix() + locale.playerMonthlyPlayTimeReset(), placeholders));
                                        } else {
                                            logger.info(AdventureUtil.deserialize(target, locale.playerMonthlyPlayTimeReset(), placeholders));
                                        }

                                        return 1;
                                    } else {
                                        // Tell the command sender (if a player) or log to console that the target failed to have their monthly play time reset
                                        if(isSenderPlayer) {
                                            sender.sendMessage(AdventureUtil.deserialize(target, locale.prefix() + locale.playerMonthlyPlayTimeResetError(), placeholders));
                                        } else {
                                            logger.warn(AdventureUtil.deserialize(target, locale.playerMonthlyPlayTimeResetError(), placeholders));
                                        }

                                        return 0;
                                    }
                                })
                        )

                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();
                            CommandSender sender = ctx.getSource().getSender();
                            boolean isSenderPlayer = sender instanceof Player;

                            // Reset the desired play time
                            CompletableFuture<Boolean> resultFuture = timeManager.resetPlayTime(false,false, false, true, false, false);
                            resultFuture.thenAccept(result -> {
                                if(result) {
                                    // Tell all online players that their monthly play time was reset
                                    for(Player player : skyPlayTime.getServer().getOnlinePlayers()) {
                                        player.sendMessage(AdventureUtil.deserialize(player, locale.prefix() + locale.monthlyPlayTimeReset()));
                                    }

                                    // Notify the sender that all monthly play time was reset
                                    if(isSenderPlayer) {
                                        sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.resetMonthlyPlayTime()));
                                    } else {
                                        logger.info(AdventureUtil.deserialize(locale.resetMonthlyPlayTime()));
                                    }
                                } else {
                                    // Notify the sender that all monthly play time failed to be reset
                                    if(isSenderPlayer) {
                                        sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.resetMonthlyPlayTimeError()));
                                    } else {
                                        logger.info(AdventureUtil.deserialize(locale.resetMonthlyPlayTimeError()));
                                    }
                                }
                            }).exceptionally(ex -> {
                                // Notify the sender that all monthly play time failed to be reset
                                if(isSenderPlayer) {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.resetMonthlyPlayTimeError()));
                                } else {
                                    logger.info(AdventureUtil.deserialize(locale.resetMonthlyPlayTimeError()));
                                }

                                return null;
                            });

                            return 1;
                        })
                )

                .then(Commands.literal("yearly")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .executes(ctx -> {
                                    Locale locale = localeManager.getLocale();
                                    Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                    UUID targetUUID = target.getUniqueId();
                                    CommandSender sender = ctx.getSource().getSender();
                                    boolean isSenderPlayer = sender instanceof Player;

                                    // Create the placeholders list
                                    List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("player_name", target.getName()));

                                    // Reset the desired play time
                                    boolean result = timeManager.resetPlayTime(targetUUID, false,false, false, false, true, false);

                                    if(result) {
                                        // Tell the target player that their yearly play time was reset
                                        target.sendMessage(AdventureUtil.deserialize(target,locale.prefix() + locale.yearlyPlayTimeReset(), placeholders));

                                        // Tell the command sender (if a player) or log to console that the target had their yearly play time reset
                                        if(isSenderPlayer) {
                                            sender.sendMessage(AdventureUtil.deserialize(target, locale.prefix() + locale.playerYearlyPlayTimeReset(), placeholders));
                                        } else {
                                            logger.info(AdventureUtil.deserialize(target, locale.playerYearlyPlayTimeReset(), placeholders));
                                        }

                                        return 1;
                                    } else {
                                        // Tell the command sender (if a player) or log to console that the target failed to have their yearly play time reset
                                        if(isSenderPlayer) {
                                            sender.sendMessage(AdventureUtil.deserialize(target, locale.prefix() + locale.playerYearlyPlayTimeResetError(), placeholders));
                                        } else {
                                            logger.warn(AdventureUtil.deserialize(target, locale.playerYearlyPlayTimeResetError(), placeholders));
                                        }

                                        return 0;
                                    }
                                })
                        )

                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();
                            CommandSender sender = ctx.getSource().getSender();
                            boolean isSenderPlayer = sender instanceof Player;

                            // Reset the desired play time
                            CompletableFuture<Boolean> resultFuture = timeManager.resetPlayTime(false,false, false, false, true, false);
                            resultFuture.thenAccept(result -> {
                                if(result) {
                                    // Tell all online players that their yearly play time was reset
                                    for(Player player : skyPlayTime.getServer().getOnlinePlayers()) {
                                        player.sendMessage(AdventureUtil.deserialize(player, locale.prefix() + locale.yearlyPlayTimeReset()));
                                    }

                                    // Notify the sender that all yearly play time was reset
                                    if(isSenderPlayer) {
                                        sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.resetYearlyPlayTime()));
                                    } else {
                                        logger.info(AdventureUtil.deserialize(locale.resetYearlyPlayTime()));
                                    }
                                } else {
                                    // Notify the sender that all yearly play time failed to be reset
                                    if(isSenderPlayer) {
                                        sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.resetYearlyPlayTimeError()));
                                    } else {
                                        logger.info(AdventureUtil.deserialize(locale.resetYearlyPlayTimeError()));
                                    }
                                }
                            }).exceptionally(ex -> {
                                // Notify the sender that all yearly play time failed to be reset
                                if(isSenderPlayer) {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.resetYearlyPlayTimeError()));
                                } else {
                                    logger.info(AdventureUtil.deserialize(locale.resetYearlyPlayTimeError()));
                                }

                                return null;
                            });

                            return 1;
                        })
                )

                .then(Commands.literal("total")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .executes(ctx -> {
                                    Locale locale = localeManager.getLocale();
                                    Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                    UUID targetUUID = target.getUniqueId();
                                    CommandSender sender = ctx.getSource().getSender();
                                    boolean isSenderPlayer = sender instanceof Player;

                                    // Create the placeholders list
                                    List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("player_name", target.getName()));

                                    // Reset the desired play time
                                    boolean result = timeManager.resetPlayTime(targetUUID, false,false, false, false, false, true);

                                    if(result) {
                                        // Tell the target player that their total play time was reset
                                        target.sendMessage(AdventureUtil.deserialize(target,locale.prefix() + locale.totalPlayTimeReset(), placeholders));

                                        // Tell the command sender (if a player) or log to console that the target had their total play time reset
                                        if(isSenderPlayer) {
                                            sender.sendMessage(AdventureUtil.deserialize(target, locale.prefix() + locale.playerTotalPlayTimeReset(), placeholders));
                                        } else {
                                            logger.info(AdventureUtil.deserialize(target, locale.playerTotalPlayTimeReset(), placeholders));
                                        }

                                        return 1;
                                    } else {
                                        // Tell the command sender (if a player) or log to console that the target failed to have their total play time reset
                                        if(isSenderPlayer) {
                                            sender.sendMessage(AdventureUtil.deserialize(target, locale.prefix() + locale.playerTotalPlayTimeResetError(), placeholders));
                                        } else {
                                            logger.warn(AdventureUtil.deserialize(target, locale.playerTotalPlayTimeResetError(), placeholders));
                                        }

                                        return 0;
                                    }
                                })
                        )

                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();
                            CommandSender sender = ctx.getSource().getSender();
                            boolean isSenderPlayer = sender instanceof Player;

                            // Reset the desired play time
                            CompletableFuture<Boolean> resultFuture = timeManager.resetPlayTime(false,false, false, false, false, true);
                            resultFuture.thenAccept(result -> {
                                if(result) {
                                    // Tell all online players that their total play time was reset
                                    for(Player player : skyPlayTime.getServer().getOnlinePlayers()) {
                                        player.sendMessage(AdventureUtil.deserialize(player, locale.prefix() + locale.totalPlayTimeReset()));
                                    }

                                    // Notify the sender that all total play time was reset
                                    if(isSenderPlayer) {
                                        sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.resetTotalPlayTime()));
                                    } else {
                                        logger.info(AdventureUtil.deserialize(locale.resetTotalPlayTime()));
                                    }
                                } else {
                                    // Notify the sender that all total play time failed to be reset
                                    if(isSenderPlayer) {
                                        sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.resetTotalPlayTimeError()));
                                    } else {
                                        logger.info(AdventureUtil.deserialize(locale.resetTotalPlayTimeError()));
                                    }
                                }
                            }).exceptionally(ex -> {
                                // Notify the sender that all total play time failed to be reset
                                if(isSenderPlayer) {
                                    sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.resetTotalPlayTimeError()));
                                } else {
                                    logger.info(AdventureUtil.deserialize(locale.resetTotalPlayTimeError()));
                                }

                                return null;
                            });

                            return 1;
                        })
                ).build();
    }
}

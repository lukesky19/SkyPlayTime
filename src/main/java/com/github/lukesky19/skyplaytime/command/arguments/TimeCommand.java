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

import com.github.lukesky19.skylib.paper.api.adventure.PaperAdventureUtility;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.config.manager.locale.LocaleManager;
import com.github.lukesky19.skyplaytime.config.data.locale.Locale;
import com.github.lukesky19.skyplaytime.player.manager.TimeManager;
import com.github.lukesky19.skyplaytime.util.TimeCategory;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import static com.github.lukesky19.skyplaytime.util.PluginUtils.formatPlayTimeChat;

/**
 * This class is used to create the time command used to view play time counters.
 */
public class TimeCommand {
    private final @NonNull ComponentLogger logger;
    private final @NonNull LocaleManager localeManager;
    private final @NonNull TimeManager timeManager;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance
     * @param localeManager A {@link LocaleManager} instance.
     * @param timeManager A {@link TimeManager} instance.
     */
    public TimeCommand(
            @NonNull SkyPlayTime skyPlayTime,
            @NonNull LocaleManager localeManager,
            @NonNull TimeManager timeManager) {
        this.logger = skyPlayTime.getComponentLogger();
        this.localeManager = localeManager;
        this.timeManager = timeManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the time command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack}.
     */
    public @NonNull LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("time")
                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.time"))
                .then(Commands.literal("session")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.time.others"))
                                .executes(ctx -> {
                                    Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                    Locale locale = localeManager.getLocale();
                                    Locale.TimeFormat timeFormat = locale.sessionPlayTimeTimePlaceholder();

                                    long currentSessionPlayTime = timeManager.getPlayTimeSeconds(target, TimeCategory.SESSION);

                                    List<TagResolver.Single> placeholders = new ArrayList<>();
                                    placeholders.add(Placeholder.parsed("player_name", target.getName()));
                                    placeholders.add(Placeholder.parsed("time", formatPlayTimeChat(timeFormat, currentSessionPlayTime)));

                                    if(ctx.getSource().getSender() instanceof Player player) {
                                        player.sendMessage(PaperAdventureUtility.deserialize(target, locale.prefix() + locale.playerSessionPlayTime(), placeholders));
                                    } else {
                                        logger.info(PaperAdventureUtility.deserialize(target, locale.playerSessionPlayTime(), placeholders));
                                    }

                                    return 1;
                                })
                        )

                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();
                            Locale.TimeFormat timeFormat = locale.sessionPlayTimeTimePlaceholder();

                            if(ctx.getSource().getSender() instanceof Player player) {
                                long currentSessionPlayTime = timeManager.getPlayTimeSeconds(player, TimeCategory.SESSION);

                                List<TagResolver.Single> placeholders = new ArrayList<>();
                                placeholders.add(Placeholder.parsed("time", formatPlayTimeChat(timeFormat, currentSessionPlayTime)));

                                player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.sessionPlayTime(), placeholders));

                                return 1;
                            } else {
                                logger.info(PaperAdventureUtility.deserialize(locale.commandPlayerOnly()));

                                return 0;
                            }
                        })
                )

                .then(Commands.literal("daily")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.time.others"))
                                .executes(ctx -> {
                                    Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                    Locale locale = localeManager.getLocale();
                                    Locale.TimeFormat timeFormat = locale.dailyPlayTimeTimePlaceholder();

                                    long currentDailyPlayTime = timeManager.getPlayTimeSeconds(target, TimeCategory.DAILY);

                                    List<TagResolver.Single> placeholders = new ArrayList<>();
                                    placeholders.add(Placeholder.parsed("player_name", target.getName()));
                                    placeholders.add(Placeholder.parsed("time", formatPlayTimeChat(timeFormat, currentDailyPlayTime)));

                                    if(ctx.getSource().getSender() instanceof Player player) {
                                        player.sendMessage(PaperAdventureUtility.deserialize(target, locale.prefix() + locale.playerDailyPlayTime(), placeholders));
                                    } else {
                                        logger.info(PaperAdventureUtility.deserialize(target, locale.playerDailyPlayTime(), placeholders));
                                    }

                                    return 1;
                                })
                        )

                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();
                            Locale.TimeFormat timeFormat = locale.dailyPlayTimeTimePlaceholder();

                            if(ctx.getSource().getSender() instanceof Player player) {
                                long currentDailyPlayTime = timeManager.getPlayTimeSeconds(player, TimeCategory.DAILY);

                                List<TagResolver.Single> placeholders = new ArrayList<>();
                                placeholders.add(Placeholder.parsed("time", formatPlayTimeChat(timeFormat, currentDailyPlayTime)));

                                player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.dailyPlayTime(), placeholders));

                                return 1;
                            } else {
                                logger.info(PaperAdventureUtility.deserialize(locale.commandPlayerOnly()));

                                return 0;
                            }
                        })
                )

                .then(Commands.literal("weekly")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.time.others"))
                                .executes(ctx -> {
                                    Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                    Locale locale = localeManager.getLocale();
                                    Locale.TimeFormat timeFormat = locale.weeklyPlayTimeTimePlaceholder();

                                    long currentWeeklyPlayTime = timeManager.getPlayTimeSeconds(target, TimeCategory.WEEKLY);

                                    List<TagResolver.Single> placeholders = new ArrayList<>();
                                    placeholders.add(Placeholder.parsed("player_name", target.getName()));
                                    placeholders.add(Placeholder.parsed("time", formatPlayTimeChat(timeFormat, currentWeeklyPlayTime)));

                                    if(ctx.getSource().getSender() instanceof Player player) {
                                        player.sendMessage(PaperAdventureUtility.deserialize(target, locale.prefix() + locale.playerWeeklyPlayTime(), placeholders));
                                    } else {
                                        logger.info(PaperAdventureUtility.deserialize(target, locale.playerWeeklyPlayTime(), placeholders));
                                    }

                                    return 1;
                                })
                        )

                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();
                            Locale.TimeFormat timeFormat = locale.weeklyPlayTimeTimePlaceholder();

                            if(ctx.getSource().getSender() instanceof Player player) {
                                long currentWeeklyPlayTime = timeManager.getPlayTimeSeconds(player, TimeCategory.WEEKLY);

                                List<TagResolver.Single> placeholders = new ArrayList<>();
                                placeholders.add(Placeholder.parsed("time", formatPlayTimeChat(timeFormat, currentWeeklyPlayTime)));

                                player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.weeklyPlayTime(), placeholders));

                                return 1;
                            } else {
                                logger.info(PaperAdventureUtility.deserialize(locale.commandPlayerOnly()));

                                return 0;
                            }
                        })
                )

                .then(Commands.literal("monthly")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.time.others"))
                                .executes(ctx -> {
                                    Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                    Locale locale = localeManager.getLocale();
                                    Locale.TimeFormat timeFormat = locale.monthlyPlayTimeTimePlaceholder();

                                    long currentMonthlyPlayTime = timeManager.getPlayTimeSeconds(target, TimeCategory.MONTHLY);

                                    List<TagResolver.Single> placeholders = new ArrayList<>();
                                    placeholders.add(Placeholder.parsed("player_name", target.getName()));
                                    placeholders.add(Placeholder.parsed("time", formatPlayTimeChat(timeFormat, currentMonthlyPlayTime)));

                                    if(ctx.getSource().getSender() instanceof Player player) {
                                        player.sendMessage(PaperAdventureUtility.deserialize(target, locale.prefix() + locale.playerMonthlyPlayTime(), placeholders));
                                    } else {
                                        logger.info(PaperAdventureUtility.deserialize(target, locale.playerMonthlyPlayTime(), placeholders));
                                    }

                                    return 1;
                                })
                        )

                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();
                            Locale.TimeFormat timeFormat = locale.monthlyPlayTimeTimePlaceholder();

                            if(ctx.getSource().getSender() instanceof Player player) {
                                long currentMonthlyPlayTime = timeManager.getPlayTimeSeconds(player, TimeCategory.MONTHLY);

                                List<TagResolver.Single> placeholders = new ArrayList<>();
                                placeholders.add(Placeholder.parsed("time", formatPlayTimeChat(timeFormat, currentMonthlyPlayTime)));

                                player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.monthlyPlayTime(), placeholders));

                                return 1;
                            } else {
                                logger.info(PaperAdventureUtility.deserialize(locale.commandPlayerOnly()));

                                return 0;
                            }
                        })
                )

                .then(Commands.literal("yearly")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.time.others"))
                                .executes(ctx -> {
                                    Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                    Locale locale = localeManager.getLocale();
                                    Locale.TimeFormat timeFormat = locale.yearlyPlayTimeTimePlaceholder();

                                    long currentYearlyPlayTime = timeManager.getPlayTimeSeconds(target, TimeCategory.YEARLY);

                                    List<TagResolver.Single> placeholders = new ArrayList<>();
                                    placeholders.add(Placeholder.parsed("player_name", target.getName()));
                                    placeholders.add(Placeholder.parsed("time", formatPlayTimeChat(timeFormat, currentYearlyPlayTime)));

                                    if(ctx.getSource().getSender() instanceof Player player) {
                                        player.sendMessage(PaperAdventureUtility.deserialize(target, locale.prefix() + locale.playerYearlyPlayTime(), placeholders));
                                    } else {
                                        logger.info(PaperAdventureUtility.deserialize(target, locale.playerYearlyPlayTime(), placeholders));
                                    }

                                    return 1;
                                })
                        )

                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();
                            Locale.TimeFormat timeFormat = locale.yearlyPlayTimeTimePlaceholder();

                            if(ctx.getSource().getSender() instanceof Player player) {
                                long currentYearlyPlayTime = timeManager.getPlayTimeSeconds(player, TimeCategory.YEARLY);

                                List<TagResolver.Single> placeholders = new ArrayList<>();
                                placeholders.add(Placeholder.parsed("time", formatPlayTimeChat(timeFormat, currentYearlyPlayTime)));

                                player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.yearlyPlayTime(), placeholders));

                                return 1;
                            } else {
                                logger.info(PaperAdventureUtility.deserialize(locale.commandPlayerOnly()));

                                return 0;
                            }
                        })
                )

                .then(Commands.literal("total")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.time.others"))
                                .executes(ctx -> {
                                    Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                    Locale locale = localeManager.getLocale();
                                    Locale.TimeFormat timeFormat = locale.totalPlayTimeTimePlaceholder();

                                    long currentTotalPlayTime = timeManager.getPlayTimeSeconds(target, TimeCategory.TOTAL);

                                    List<TagResolver.Single> placeholders = new ArrayList<>();
                                    placeholders.add(Placeholder.parsed("player_name", target.getName()));
                                    placeholders.add(Placeholder.parsed("time", formatPlayTimeChat(timeFormat, currentTotalPlayTime)));

                                    if(ctx.getSource().getSender() instanceof Player player) {
                                        player.sendMessage(PaperAdventureUtility.deserialize(target, locale.prefix() + locale.playerTotalPlayTime(), placeholders));
                                    } else {
                                        logger.info(PaperAdventureUtility.deserialize(target, locale.playerTotalPlayTime(), placeholders));
                                    }

                                    return 1;
                                })
                        )

                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();
                            Locale.TimeFormat timeFormat = locale.monthlyPlayTimeTimePlaceholder();

                            if(ctx.getSource().getSender() instanceof Player player) {
                                long currentTotalPlayTime = timeManager.getPlayTimeSeconds(player, TimeCategory.TOTAL);

                                List<TagResolver.Single> placeholders = new ArrayList<>();
                                placeholders.add(Placeholder.parsed("time", formatPlayTimeChat(timeFormat, currentTotalPlayTime)));

                                player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.totalPlayTime(), placeholders));

                                return 1;
                            } else {
                                logger.info(PaperAdventureUtility.deserialize(locale.commandPlayerOnly()));

                                return 0;
                            }
                        })
                ).build();
    }
}
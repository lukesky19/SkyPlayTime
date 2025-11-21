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
import com.github.lukesky19.skyplaytime.player.manager.TimeManager;
import com.github.lukesky19.skyplaytime.util.TimeCategory;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static com.github.lukesky19.skyplaytime.util.PluginUtils.formatPlayTimeChat;

/**
 * This class is used to create the remove command used to remove play time from a player's play time.
 */
public class RemoveCommand {
    private final @NotNull ComponentLogger logger;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull TimeManager timeManager;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param timeManager A {@link TimeManager} instance.
     */
    public RemoveCommand(
            @NotNull SkyPlayTime skyPlayTime,
            @NotNull LocaleManager localeManager,
            @NotNull TimeManager timeManager) {
        this.logger = skyPlayTime.getComponentLogger();
        this.localeManager = localeManager;
        this.timeManager = timeManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the remove command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack}.
     */
    public LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("remove")
                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.remove"))
                .then(Commands.literal("session")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .then(Commands.argument("time", StringArgumentType.word())
                                        .executes(ctx -> {
                                            Locale locale = localeManager.getLocale();
                                            Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                            UUID targetUUID = target.getUniqueId();

                                            long timeToRemove = TimeUtil.stringToMillis(ctx.getArgument("time", String.class)) / 1000;

                                            // Remove the play time and then get the updated play time
                                            timeManager.removePlayTimeSeconds(targetUUID, TimeCategory.SESSION, timeToRemove);
                                            long playTimeSeconds = timeManager.getPlayTimeSeconds(targetUUID, TimeCategory.SESSION);

                                            // Create the placeholders list
                                            List<TagResolver.Single> placeholders = List.of(
                                                    Placeholder.parsed("player_name", target.getName()),
                                                    Placeholder.parsed("time", formatPlayTimeChat(locale.sessionPlayTimeUpdatedTimePlaceholder(), playTimeSeconds)));

                                            // Tell the target player that their play time was updated
                                            target.sendMessage(AdventureUtil.deserialize(target,locale.prefix() + locale.sessionPlayTimeUpdated(), placeholders));

                                            // Tell the sender that the target had their play time updated
                                            if(ctx.getSource().getSender() instanceof Player player) {
                                                player.sendMessage(AdventureUtil.deserialize(player, locale.prefix() + locale.playerSessionPlayTimeUpdated(), placeholders));
                                            } else {
                                                logger.info(AdventureUtil.deserialize(target, locale.playerSessionPlayTimeUpdated(), placeholders));
                                            }

                                            return 1;
                                        })
                                )
                        )
                )

                .then(Commands.literal("daily")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .then(Commands.argument("time", StringArgumentType.word())
                                        .executes(ctx -> {
                                            Locale locale = localeManager.getLocale();
                                            Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                            UUID targetUUID = target.getUniqueId();

                                            long timeToRemove = TimeUtil.stringToMillis(ctx.getArgument("time", String.class)) / 1000;

                                            // Remove the play time and then get the updated play time
                                            timeManager.removePlayTimeSeconds(targetUUID, TimeCategory.DAILY, timeToRemove);
                                            long playTimeSeconds = timeManager.getPlayTimeSeconds(targetUUID, TimeCategory.DAILY);

                                            // Create the placeholders list
                                            List<TagResolver.Single> placeholders = List.of(
                                                    Placeholder.parsed("player_name", target.getName()),
                                                    Placeholder.parsed("time", formatPlayTimeChat(locale.dailyPlayTimeUpdatedTimePlaceholder(), playTimeSeconds)));

                                            // Tell the target player that their play time was updated
                                            target.sendMessage(AdventureUtil.deserialize(target,locale.prefix() + locale.dailyPlayTimeUpdated(), placeholders));

                                            // Tell the sender that the target had their play time updated
                                            if(ctx.getSource().getSender() instanceof Player player) {
                                                player.sendMessage(AdventureUtil.deserialize(player, locale.prefix() + locale.playerDailyPlayTimeUpdated(), placeholders));
                                            } else {
                                                logger.info(AdventureUtil.deserialize(target, locale.playerDailyPlayTimeUpdated(), placeholders));
                                            }

                                            return 1;
                                        })
                                )
                        )
                )

                .then(Commands.literal("weekly")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .then(Commands.argument("time", StringArgumentType.word())
                                        .executes(ctx -> {
                                            Locale locale = localeManager.getLocale();
                                            Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                            UUID targetUUID = target.getUniqueId();

                                            long timeToRemove = TimeUtil.stringToMillis(ctx.getArgument("time", String.class)) / 1000;

                                            // Remove the play time and then get the updated play time
                                            timeManager.removePlayTimeSeconds(targetUUID, TimeCategory.WEEKLY, timeToRemove);
                                            long playTimeSeconds = timeManager.getPlayTimeSeconds(targetUUID, TimeCategory.WEEKLY);

                                            // Create the placeholders list
                                            List<TagResolver.Single> placeholders = List.of(
                                                    Placeholder.parsed("player_name", target.getName()),
                                                    Placeholder.parsed("time", formatPlayTimeChat(locale.weeklyPlayTimeUpdatedTimePlaceholder(), playTimeSeconds)));

                                            // Tell the target player that their play time was updated
                                            target.sendMessage(AdventureUtil.deserialize(target,locale.prefix() + locale.weeklyPlayTimeUpdated(), placeholders));

                                            // Tell the sender that the target had their play time updated
                                            if(ctx.getSource().getSender() instanceof Player player) {
                                                player.sendMessage(AdventureUtil.deserialize(player, locale.prefix() + locale.playerWeeklyPlayTimeUpdated(), placeholders));
                                            } else {
                                                logger.info(AdventureUtil.deserialize(target, locale.playerWeeklyPlayTimeUpdated(), placeholders));
                                            }

                                            return 1;
                                        })
                                )
                        )
                )

                .then(Commands.literal("monthly")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .then(Commands.argument("time", StringArgumentType.word())
                                        .executes(ctx -> {
                                            Locale locale = localeManager.getLocale();
                                            Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                            UUID targetUUID = target.getUniqueId();

                                            long timeToRemove = TimeUtil.stringToMillis(ctx.getArgument("time", String.class)) / 1000;

                                            // Remove the play time and then get the updated play time
                                            timeManager.removePlayTimeSeconds(targetUUID, TimeCategory.MONTHLY, timeToRemove);
                                            long playTimeSeconds = timeManager.getPlayTimeSeconds(targetUUID, TimeCategory.MONTHLY);

                                            // Create the placeholders list
                                            List<TagResolver.Single> placeholders = List.of(
                                                    Placeholder.parsed("player_name", target.getName()),
                                                    Placeholder.parsed("time", formatPlayTimeChat(locale.monthlyPlayTimeUpdatedTimePlaceholder(), playTimeSeconds)));

                                            // Tell the target player that their play time was updated
                                            target.sendMessage(AdventureUtil.deserialize(target,locale.prefix() + locale.monthlyPlayTimeUpdated(), placeholders));

                                            // Tell the sender that the target had their play time updated
                                            if(ctx.getSource().getSender() instanceof Player player) {
                                                player.sendMessage(AdventureUtil.deserialize(player, locale.prefix() + locale.playerMonthlyPlayTimeUpdated(), placeholders));
                                            } else {
                                                logger.info(AdventureUtil.deserialize(target, locale.playerMonthlyPlayTimeUpdated(), placeholders));
                                            }

                                            return 1;
                                        })
                                )
                        )
                )

                .then(Commands.literal("yearly")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .then(Commands.argument("time", StringArgumentType.word())
                                        .executes(ctx -> {
                                            Locale locale = localeManager.getLocale();
                                            Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                            UUID targetUUID = target.getUniqueId();

                                            long timeToRemove = TimeUtil.stringToMillis(ctx.getArgument("time", String.class)) / 1000;

                                            // Remove the play time and then get the updated play time
                                            timeManager.removePlayTimeSeconds(targetUUID, TimeCategory.YEARLY, timeToRemove);
                                            long playTimeSeconds = timeManager.getPlayTimeSeconds(targetUUID, TimeCategory.YEARLY);

                                            // Create the placeholders list
                                            List<TagResolver.Single> placeholders = List.of(
                                                    Placeholder.parsed("player_name", target.getName()),
                                                    Placeholder.parsed("time", formatPlayTimeChat(locale.yearlyPlayTimeUpdatedTimePlaceholder(), playTimeSeconds)));

                                            // Tell the target player that their play time was updated
                                            target.sendMessage(AdventureUtil.deserialize(target,locale.prefix() + locale.yearlyPlayTimeUpdated(), placeholders));

                                            // Tell the sender that the target had their play time updated
                                            if(ctx.getSource().getSender() instanceof Player player) {
                                                player.sendMessage(AdventureUtil.deserialize(player, locale.prefix() + locale.playerYearlyPlayTimeUpdated(), placeholders));
                                            } else {
                                                logger.info(AdventureUtil.deserialize(target, locale.playerYearlyPlayTimeUpdated(), placeholders));
                                            }

                                            return 1;
                                        })
                                )
                        )
                )

                .then(Commands.literal("total")
                        .then(Commands.argument("player", ArgumentTypes.player())
                                .then(Commands.argument("time", StringArgumentType.word())
                                        .executes(ctx -> {
                                            Locale locale = localeManager.getLocale();
                                            Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                                            UUID targetUUID = target.getUniqueId();

                                            long timeToRemove = TimeUtil.stringToMillis(ctx.getArgument("time", String.class)) / 1000;

                                            // Remove the play time and then get the updated play time
                                            timeManager.removePlayTimeSeconds(targetUUID, TimeCategory.TOTAL, timeToRemove);
                                            long playTimeSeconds = timeManager.getPlayTimeSeconds(targetUUID, TimeCategory.TOTAL);

                                            // Create the placeholders list
                                            List<TagResolver.Single> placeholders = List.of(
                                                    Placeholder.parsed("player_name", target.getName()),
                                                    Placeholder.parsed("time", formatPlayTimeChat(locale.totalPlayTimeUpdatedTimePlaceholder(), playTimeSeconds)));

                                            // Tell the target player that their play time was updated
                                            target.sendMessage(AdventureUtil.deserialize(target,locale.prefix() + locale.totalPlayTimeUpdated(), placeholders));

                                            // Tell the sender that the target had their play time updated
                                            if(ctx.getSource().getSender() instanceof Player player) {
                                                player.sendMessage(AdventureUtil.deserialize(player, locale.prefix() + locale.playerTotalPlayTimeUpdated(), placeholders));
                                            } else {
                                                logger.info(AdventureUtil.deserialize(target, locale.playerTotalPlayTimeUpdated(), placeholders));
                                            }

                                            return 1;
                                        })
                                )
                        )
                ).build();
    }
}

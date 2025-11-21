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
import com.github.lukesky19.skyplaytime.leaderboard.manager.LeaderboardSnapshotManager;
import com.github.lukesky19.skyplaytime.config.manager.locale.LocaleManager;
import com.github.lukesky19.skyplaytime.leaderboard.data.LeaderboardSnapshot;
import com.github.lukesky19.skyplaytime.config.data.locale.Locale;
import com.github.lukesky19.skyplaytime.leaderboard.manager.LeaderboardManager;
import com.github.lukesky19.skyplaytime.leaderboard.data.Position;
import com.github.lukesky19.skyplaytime.leaderboard.data.TopTen;
import com.github.lukesky19.skyplaytime.util.PluginUtils;
import com.github.lukesky19.skyplaytime.util.TimeCategory;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.github.lukesky19.skyplaytime.util.PluginUtils.formatPlayTimeChat;

/**
 * This class is used to create the leaderboard command used to view leaderboards for play time.
 */
public class LeaderboardCommand {
    private final @NotNull ComponentLogger logger;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull LeaderboardSnapshotManager leaderboardSnapshotManager;
    private final @NotNull LeaderboardManager leaderboardManager;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param leaderboardManager A {@link LeaderboardManager} instance.
     * @param leaderboardSnapshotManager A {@link LeaderboardSnapshotManager} instance.
     */
    public LeaderboardCommand(
            @NotNull SkyPlayTime skyPlayTime,
            @NotNull LocaleManager localeManager,
            @NotNull LeaderboardManager leaderboardManager,
            @NotNull LeaderboardSnapshotManager leaderboardSnapshotManager) {
        this.logger = skyPlayTime.getComponentLogger();
        this.localeManager = localeManager;
        this.leaderboardSnapshotManager = leaderboardSnapshotManager;
        this.leaderboardManager = leaderboardManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the leaderboard command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack}.
     */
    public LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("leaderboard")
                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.leaderboard"))
                .then(Commands.literal("session")
                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();
                            CommandSender sender = ctx.getSource().getSender();
                            @Nullable TopTen topTen = leaderboardManager.getTopTenByTimeCategoryNotExempt(TimeCategory.SESSION);
                            if(topTen == null) topTen = new TopTen();

                            sender.sendMessage(AdventureUtil.deserialize(locale.sessionLeaderboardTitle()));

                            int positionNumber = 1;
                            for(Position position : topTen.getPositions()) {
                                List<TagResolver.Single> placeholders = List.of(
                                        Placeholder.parsed("position", String.valueOf(positionNumber)),
                                        Placeholder.parsed("player_name", position.name()),
                                        Placeholder.parsed("time", formatPlayTimeChat(locale.sessionLeaderboardTimePlaceholder(), position.seconds())));

                                sender.sendMessage(AdventureUtil.deserialize(locale.sessionLeaderboardPosition(), placeholders));

                                positionNumber++;
                            }

                            while(positionNumber <= 10) {
                                List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("position", String.valueOf(positionNumber)));

                                sender.sendMessage(AdventureUtil.deserialize(locale.sessionLeaderboardPositionEmpty(), placeholders));

                                positionNumber++;
                            }

                            return 1;
                        })
                )

                .then(Commands.literal("daily")
                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();
                            CommandSender sender = ctx.getSource().getSender();
                            @Nullable TopTen topTen = leaderboardManager.getTopTenByTimeCategoryNotExempt(TimeCategory.DAILY);
                            if(topTen == null) topTen = new TopTen();

                            sender.sendMessage(AdventureUtil.deserialize(locale.dailyLeaderboardTitle()));

                            int positionNumber = 1;
                            for(Position position : topTen.getPositions()) {
                                List<TagResolver.Single> placeholders = List.of(
                                        Placeholder.parsed("position", String.valueOf(positionNumber)),
                                        Placeholder.parsed("player_name", position.name()),
                                        Placeholder.parsed("time", formatPlayTimeChat(locale.dailyLeaderboardTimePlaceholder(), position.seconds())));

                                sender.sendMessage(AdventureUtil.deserialize(locale.dailyLeaderboardPosition(), placeholders));

                                positionNumber++;
                            }

                            while(positionNumber <= 10) {
                                List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("position", String.valueOf(positionNumber)));

                                sender.sendMessage(AdventureUtil.deserialize(locale.dailyLeaderboardPositionEmpty(), placeholders));

                                positionNumber++;
                            }

                            return 1;
                        })
                )

                .then(Commands.literal("weekly")
                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();
                            CommandSender sender = ctx.getSource().getSender();
                            @Nullable TopTen topTen = leaderboardManager.getTopTenByTimeCategoryNotExempt(TimeCategory.WEEKLY);
                            if(topTen == null) topTen = new TopTen();

                            sender.sendMessage(AdventureUtil.deserialize(locale.weeklyLeaderboardTitle()));

                            int positionNumber = 1;
                            for(Position position : topTen.getPositions()) {
                                List<TagResolver.Single> placeholders = List.of(
                                        Placeholder.parsed("position", String.valueOf(positionNumber)),
                                        Placeholder.parsed("player_name", position.name()),
                                        Placeholder.parsed("time", formatPlayTimeChat(locale.weeklyLeaderboardTimePlaceholder(), position.seconds())));

                                sender.sendMessage(AdventureUtil.deserialize(locale.weeklyLeaderboardPosition(), placeholders));

                                positionNumber++;
                            }

                            while(positionNumber <= 10) {
                                List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("position", String.valueOf(positionNumber)));

                                sender.sendMessage(AdventureUtil.deserialize(locale.weeklyLeaderboardPositionEmpty(), placeholders));

                                positionNumber++;
                            }

                            return 1;
                        })
                )

                .then(Commands.literal("monthly")
                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();
                            CommandSender sender = ctx.getSource().getSender();
                            @Nullable TopTen topTen = leaderboardManager.getTopTenByTimeCategoryNotExempt(TimeCategory.MONTHLY);
                            if(topTen == null) topTen = new TopTen();

                            sender.sendMessage(AdventureUtil.deserialize(locale.monthlyLeaderboardTitle()));

                            int positionNumber = 1;
                            for(Position position : topTen.getPositions()) {
                                List<TagResolver.Single> placeholders = List.of(
                                        Placeholder.parsed("position", String.valueOf(positionNumber)),
                                        Placeholder.parsed("player_name", position.name()),
                                        Placeholder.parsed("time", formatPlayTimeChat(locale.monthlyLeaderboardTimePlaceholder(), position.seconds())));

                                sender.sendMessage(AdventureUtil.deserialize(locale.monthlyLeaderboardPosition(), placeholders));

                                positionNumber++;
                            }

                            while(positionNumber <= 10) {
                                List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("position", String.valueOf(positionNumber)));

                                sender.sendMessage(AdventureUtil.deserialize(locale.monthlyLeaderboardPositionEmpty(), placeholders));

                                positionNumber++;
                            }

                            return 1;
                        })
                )

                .then(Commands.literal("yearly")
                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();
                            CommandSender sender = ctx.getSource().getSender();
                            @Nullable TopTen topTen = leaderboardManager.getTopTenByTimeCategoryNotExempt(TimeCategory.YEARLY);
                            if(topTen == null) topTen = new TopTen();

                            sender.sendMessage(AdventureUtil.deserialize(locale.yearlyLeaderboardTitle()));

                            int positionNumber = 1;
                            for(Position position : topTen.getPositions()) {
                                List<TagResolver.Single> placeholders = List.of(
                                        Placeholder.parsed("position", String.valueOf(positionNumber)),
                                        Placeholder.parsed("player_name", position.name()),
                                        Placeholder.parsed("time", formatPlayTimeChat(locale.yearlyLeaderboardTimePlaceholder(), position.seconds())));

                                sender.sendMessage(AdventureUtil.deserialize(locale.yearlyLeaderboardPosition(), placeholders));

                                positionNumber++;
                            }

                            while(positionNumber <= 10) {
                                List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("position", String.valueOf(positionNumber)));

                                sender.sendMessage(AdventureUtil.deserialize(locale.yearlyLeaderboardPositionEmpty(), placeholders));

                                positionNumber++;
                            }

                            return 1;
                        })
                )

                .then(Commands.literal("total")
                        .executes(ctx -> {
                            Locale locale = localeManager.getLocale();
                            CommandSender sender = ctx.getSource().getSender();
                            @Nullable TopTen topTen = leaderboardManager.getTopTenByTimeCategoryNotExempt(TimeCategory.TOTAL);
                            if(topTen == null) topTen = new TopTen();

                            sender.sendMessage(AdventureUtil.deserialize(locale.totalLeaderboardTitle()));

                            int positionNumber = 1;
                            for(Position position : topTen.getPositions()) {
                                List<TagResolver.Single> placeholders = List.of(
                                        Placeholder.parsed("position", String.valueOf(positionNumber)),
                                        Placeholder.parsed("player_name", position.name()),
                                        Placeholder.parsed("time", formatPlayTimeChat(locale.totalLeaderboardTimePlaceholder(), position.seconds())));

                                sender.sendMessage(AdventureUtil.deserialize(locale.totalLeaderboardPosition(), placeholders));

                                positionNumber++;
                            }

                            while(positionNumber <= 10) {
                                List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("position", String.valueOf(positionNumber)));

                                sender.sendMessage(AdventureUtil.deserialize(locale.totalLeaderboardPositionEmpty(), placeholders));

                                positionNumber++;
                            }

                            return 1;
                        })
                )

                .then(Commands.literal("history")
                        .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.commands.skyplaytime.leaderboard.historical"))
                        .then(Commands.argument("file_name", StringArgumentType.word())
                                .suggests((ctx, suggestionsBuilder) -> {
                                    List<String> fileNames = leaderboardSnapshotManager.getLeaderboardSnapshotFileNames();
                                    fileNames.forEach(suggestionsBuilder::suggest);

                                    return suggestionsBuilder.buildFuture();
                                })

                                .executes(ctx -> {
                                    CommandSender sender = ctx.getSource().getSender();
                                    boolean isPlayer = sender instanceof Player;
                                    String fileName = ctx.getArgument("file_name", String.class);
                                    Locale locale = localeManager.getLocale();
                                    List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("file_name", fileName));
                                    LeaderboardSnapshot historicalLeaderboard = leaderboardSnapshotManager.loadLeaderboardSnapshot(fileName);
                                    if(historicalLeaderboard == null) {
                                        if(isPlayer) {
                                            sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.historicalLeaderboardLoadError(), placeholders));
                                        } else {
                                            logger.error(locale.historicalLeaderboardLoadError(), placeholders);
                                        }

                                        return 0;
                                    }

                                    sender.sendMessage(AdventureUtil.deserialize(locale.historicalLeaderboardTitle(), placeholders));

                                    int positionNumber = 1;
                                    for(Position position : historicalLeaderboard.positions()) {
                                        List<TagResolver.Single> positionPlaceholders = List.of(
                                                Placeholder.parsed("position", String.valueOf(positionNumber)),
                                                Placeholder.parsed("player_name", position.name()),
                                                Placeholder.parsed("time", PluginUtils.formatPlayTimeChat(locale.historicalLeaderboardTimePlaceholder(), position.seconds())));

                                        sender.sendMessage(AdventureUtil.deserialize(locale.historicalLeaderboardPosition(), positionPlaceholders));

                                        positionNumber++;
                                    }

                                    while(positionNumber <= 10) {
                                        List<TagResolver.Single> positionPlaceholders = List.of(Placeholder.parsed("position", String.valueOf(positionNumber)));

                                        sender.sendMessage(AdventureUtil.deserialize(locale.historicalLeaderboardPositionEmpty(), positionPlaceholders));

                                        positionNumber++;
                                    }

                                    return 1;
                                })
                        )
                ).build();
    }
}

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
import com.github.lukesky19.skyplaytime.api.SkyPlayTimeAPI;
import com.github.lukesky19.skyplaytime.locale.LocaleManager;
import com.github.lukesky19.skyplaytime.locale.Locale;
import com.github.lukesky19.skyplaytime.leaderboard.manager.LeaderboardManager;
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

import java.util.List;

/**
 * This class is used to create the unexempt command used to mark players not exempt from leaderboard reporting.
 */
public class UnExemptCommand {
    private final @NonNull ComponentLogger logger;
    private final @NonNull LocaleManager localeManager;
    private final @NonNull LeaderboardManager leaderboardManager;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param leaderboardManager A {@link SkyPlayTimeAPI} instance.
     */
    public UnExemptCommand(@NonNull SkyPlayTime skyPlayTime, @NonNull LocaleManager localeManager, @NonNull LeaderboardManager leaderboardManager) {
        this.logger = skyPlayTime.getComponentLogger();
        this.localeManager = localeManager;
        this.leaderboardManager = leaderboardManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the unexempt command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack}.
     */
    public LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("unexempt")
                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.unexempt"))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .executes(ctx -> {
                            Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                            Locale locale = localeManager.getLocale();

                            List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("player_name", target.getName()));

                            leaderboardManager.markPlayerNotExempt(target);

                            if(ctx.getSource().getSender() instanceof Player player) {
                                player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.playerUnexempt(), placeholders));
                            } else {
                                logger.info(PaperAdventureUtility.deserialize(target, locale.playerUnexempt(), placeholders));
                            }

                            return 1;
                        })
                ).build();
    }
}

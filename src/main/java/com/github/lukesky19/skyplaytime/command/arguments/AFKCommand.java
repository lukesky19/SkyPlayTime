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
*//*
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
import com.github.lukesky19.skyplaytime.player.manager.AFKManager;
import com.github.lukesky19.skyplaytime.util.AFKToggleResult;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class is used to create the AFK command.
 */
public class AFKCommand {
    private final @NotNull ComponentLogger logger;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull AFKManager afkManager;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance
     * @param localeManager A {@link LocaleManager} instance.
     * @param afkManager An {@link AFKManager} instance.
     */
    public AFKCommand(
            @NotNull SkyPlayTime skyPlayTime,
            @NotNull LocaleManager localeManager,
            @NotNull AFKManager afkManager) {
        this.logger = skyPlayTime.getComponentLogger();
        this.localeManager = localeManager;
        this.afkManager = afkManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the afk command or afk command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack}.
     */
    public @NotNull LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("afk")
                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.afk"))
                .then(Commands.argument("player", ArgumentTypes.player())
                        .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.afk.others"))
                        .executes(ctx -> {
                            CommandSender sender = ctx.getSource().getSender();
                            boolean isSenderPlayer = sender instanceof Player;
                            Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).getFirst();
                            UUID targetUUID = target.getUniqueId();
                            Locale locale = localeManager.getLocale();

                            List<TagResolver.Single> placeholders = new ArrayList<>();
                            placeholders.add(Placeholder.parsed("player_name", target.getName()));

                            AFKToggleResult result = afkManager.togglePlayerAFK(target, targetUUID, true, true);

                            switch(result) {
                                case SUCCESS_AFK -> {
                                    if(isSenderPlayer) {
                                        sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.forcedPlayerAfkMessage(), placeholders));
                                    } else {
                                        logger.info(AdventureUtil.deserialize(locale.forcedPlayerAfkMessage(), placeholders));
                                    }

                                    return 1;
                                }

                                case SUCCESS_NO_LONGER_AFK -> {
                                    if(isSenderPlayer) {
                                        sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.forcedPlayerNoLongerAfkMessage(), placeholders));
                                    } else {
                                        logger.info(AdventureUtil.deserialize(locale.forcedPlayerNoLongerAfkMessage(), placeholders));
                                    }

                                    return 1;
                                }

                                default -> {
                                    if(isSenderPlayer) {
                                        sender.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.forcedAfkToggleFailed(), placeholders));
                                    } else {
                                        logger.info(AdventureUtil.deserialize(locale.forcedAfkToggleFailed(), placeholders));
                                    }

                                    return 0;
                                }
                            }
                        })
                )

                .executes(ctx -> {
                    Locale locale = localeManager.getLocale();

                    if(ctx.getSource().getSender() instanceof Player player) {
                        UUID uuid = player.getUniqueId();

                        afkManager.togglePlayerAFK(player, uuid, true, true);

                        return 1;
                    } else {
                        logger.info(AdventureUtil.deserialize(locale.commandPlayerOnly()));

                        return 0;
                    }
                }
        ).build();
    }
}

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
import com.github.lukesky19.skyplaytime.config.manager.locale.LocaleManager;
import com.github.lukesky19.skyplaytime.config.data.locale.Locale;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * This class is used to create the help command.
 */
public class HelpCommand {
    private final @NotNull LocaleManager localeManager;

    /**
     * Constructor
     * @param localeManager A {@link LocaleManager} instance.
     */
    public HelpCommand(@NotNull LocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the help command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack}.
     */
    public @NotNull LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("help")
                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.help"))
                .executes(ctx -> {
                    Locale locale = localeManager.getLocale();
                    CommandSender sender = ctx.getSource().getSender();

                    for (String msg : locale.help()) {
                        sender.sendMessage(AdventureUtil.deserialize(msg));
                    }

                    return 1;
                }).build();
    }
}

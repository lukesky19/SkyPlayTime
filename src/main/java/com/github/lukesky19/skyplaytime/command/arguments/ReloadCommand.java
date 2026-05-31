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
import com.github.lukesky19.skyplaytime.locale.LocaleManager;
import com.github.lukesky19.skyplaytime.locale.Locale;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * This class is used to create the reload command used to reload the plugin.
 */
public class ReloadCommand {
    private final @NonNull SkyPlayTime skyPlayTime;
    private final @NonNull ComponentLogger logger;
    private final @NonNull LocaleManager localeManager;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     * @param localeManager A {@link LocaleManager} instance.
     */
    public ReloadCommand(@NonNull SkyPlayTime skyPlayTime, @NonNull LocaleManager localeManager) {
        this.skyPlayTime = skyPlayTime;
        this.logger = skyPlayTime.getComponentLogger();
        this.localeManager = localeManager;
    }
    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the reload command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack}.
     */
    public LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("reload")
                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.reload"))
                .executes(ctx -> {
                    skyPlayTime.reload(false);

                    Locale locale = localeManager.getLocale();

                    if(ctx.getSource().getSender() instanceof Player player) {
                        player.sendMessage(PaperAdventureUtility.deserialize(player, locale.prefix() + locale.reload()));
                    } else {
                        logger.info(AdventureUtility.deserialize(locale.reload()));
                    }

                    return 1;
                }).build();
    }
}

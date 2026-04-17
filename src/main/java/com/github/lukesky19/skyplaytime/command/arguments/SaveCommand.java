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
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.config.manager.locale.LocaleManager;
import com.github.lukesky19.skyplaytime.config.data.locale.Locale;
import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

/**
 * This class is used to create the save command used to save player data.
 */
public class SaveCommand {
    private final @NonNull SkyPlayTime skyPlayTime;
    private final @NonNull ComponentLogger logger;
    private final @NonNull LocaleManager localeManager;
    private final @NonNull PlayerDataManager playerDataManager;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance
     */
    public SaveCommand(@NonNull SkyPlayTime skyPlayTime, @NonNull LocaleManager localeManager, @NonNull PlayerDataManager playerDataManager) {
        this.skyPlayTime = skyPlayTime;
        this.logger = skyPlayTime.getComponentLogger();
        this.localeManager = localeManager;
        this.playerDataManager = playerDataManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the save command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack}.
     */
    public LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("save")
                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.save"))
                .executes(ctx -> {
                    Locale locale = localeManager.getLocale();

                    playerDataManager.savePlayerData().thenAccept(results -> {
                        skyPlayTime.getServer().getScheduler().runTask(skyPlayTime, () -> {
                            if(ctx.getSource().getSender() instanceof Player player) {
                                if(!results.contains(false)) {
                                    player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.playTimeSaveSuccess()));
                                } else {
                                    player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.playTimeSaveError()));
                                }
                            } else {
                                if(!results.contains(false)) {
                                    logger.warn(AdventureUtility.deserialize(locale.playTimeSaveSuccess()));
                                } else {
                                    logger.warn(AdventureUtility.deserialize(locale.playTimeSaveError()));
                                }
                            }
                        });
                    }).exceptionally(ex -> {
                        skyPlayTime.getServer().getScheduler().runTask(skyPlayTime, () -> {
                            if(ctx.getSource().getSender() instanceof Player player) {
                                player.sendMessage(AdventureUtility.deserialize(locale.prefix() + locale.playTimeSaveError()));
                                player.sendMessage(AdventureUtility.deserialize(locale.prefix() + ex.getMessage()));
                            } else {
                                logger.warn(AdventureUtility.deserialize(locale.playTimeSaveError()));
                                logger.warn(AdventureUtility.deserialize(ex.getMessage()));
                            }
                        });

                        return null;
                    });

                    return 1;
                }).build();
    }
}
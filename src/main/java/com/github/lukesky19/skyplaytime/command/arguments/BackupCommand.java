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
import com.github.lukesky19.skyplaytime.database.DatabaseManager;
import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class is used to create the backup command used to backup the database.
 */
public class BackupCommand {
    private final @NotNull SkyPlayTime skyPlayTime;
    private final @NotNull ComponentLogger logger;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull PlayerDataManager playerDataManager;
    private final @NotNull DatabaseManager databaseManager;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance
     * @param databaseManager A {@link DatabaseManager} instance.
     */
    public BackupCommand(@NotNull SkyPlayTime skyPlayTime, @NotNull LocaleManager localeManager, @NotNull PlayerDataManager playerDataManager, @NotNull DatabaseManager databaseManager) {
        this.skyPlayTime = skyPlayTime;
        this.logger = skyPlayTime.getComponentLogger();
        this.localeManager = localeManager;
        this.playerDataManager = playerDataManager;
        this.databaseManager = databaseManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} for the backup command argument.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack}.
     */
    public LiteralCommandNode<CommandSourceStack> createCommand() {
        return Commands.literal("backup")
                .requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime.backup"))
                .executes(ctx -> {
                    Locale locale = localeManager.getLocale();

                    playerDataManager.savePlayerData().thenAccept(results -> {
                        if(results.contains(false)) {
                            skyPlayTime.getServer().getScheduler().runTask(skyPlayTime, () -> {
                                if(ctx.getSource().getSender() instanceof Player player) {
                                    player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.playTimeSaveError()));
                                    player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.databaseBackupError()));
                                } else {
                                    logger.error(AdventureUtil.deserialize(locale.playTimeSaveError()));
                                    logger.error(AdventureUtil.deserialize(locale.databaseBackupError()));
                                }
                            });

                            return;
                        }

                        databaseManager.backupDatabase().thenAccept(result ->
                                skyPlayTime.getServer().getScheduler().runTask(skyPlayTime, () -> {
                                    if(ctx.getSource().getSender() instanceof Player player) {
                                        if(result) {
                                            player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.databaseBackupSuccess()));
                                        } else {
                                            player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.databaseBackupError()));
                                        }
                                    } else {
                                        if(result) {
                                            logger.info(AdventureUtil.deserialize(locale.databaseBackupSuccess()));
                                        } else {
                                            logger.info(AdventureUtil.deserialize(locale.databaseBackupError()));
                                        }
                                    }
                        })).exceptionally(ex -> {
                            logger.error(AdventureUtil.deserialize("Failed to backup database: " + ex.getMessage()));

                            skyPlayTime.getServer().getScheduler().runTask(skyPlayTime, () -> {
                                if(ctx.getSource().getSender() instanceof Player player) {
                                    player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.databaseBackupError()));
                                } else {
                                    logger.error(AdventureUtil.deserialize(locale.databaseBackupError()));
                                }
                            });

                            return null;
                        });
                    }).exceptionally(ex -> {
                        skyPlayTime.getServer().getScheduler().runTask(skyPlayTime, () -> {
                            if(ctx.getSource().getSender() instanceof Player player) {
                                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.playTimeSaveError()));
                                player.sendMessage(AdventureUtil.deserialize(locale.prefix() + locale.databaseBackupError()));
                            } else {
                                logger.error(AdventureUtil.deserialize(locale.playTimeSaveError()));
                                logger.error(AdventureUtil.deserialize(locale.databaseBackupError()));
                            }
                        });

                        return null;
                    });

                    return 1;
                }).build();
    }
}

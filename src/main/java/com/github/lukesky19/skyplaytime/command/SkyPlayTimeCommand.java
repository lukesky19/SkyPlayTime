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
package com.github.lukesky19.skyplaytime.command;

import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.player.manager.AFKManager;
import com.github.lukesky19.skyplaytime.player.manager.ActivityManager;
import com.github.lukesky19.skyplaytime.command.arguments.*;
import com.github.lukesky19.skyplaytime.leaderboard.manager.LeaderboardManager;
import com.github.lukesky19.skyplaytime.leaderboard.manager.LeaderboardSnapshotManager;
import com.github.lukesky19.skyplaytime.database.DatabaseManager;
import com.github.lukesky19.skyplaytime.config.manager.locale.LocaleManager;
import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import com.github.lukesky19.skyplaytime.player.manager.TimeManager;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

/**
 * This class creates the main /skyplaytime command to register.
 */
public class SkyPlayTimeCommand {
    private final @NotNull SkyPlayTime skyPlayTime;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull LeaderboardSnapshotManager leaderboardSnapshotManager;
    private final @NotNull DatabaseManager databaseManager;
    private final @NotNull PlayerDataManager playerDataManager;
    private final @NotNull LeaderboardManager leaderboardManager;
    private final @NotNull TimeManager timeManager;
    private final @NotNull AFKManager afkManager;
    private final @NotNull ActivityManager activityManager;

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param leaderboardSnapshotManager A {@link LeaderboardSnapshotManager} instance.
     * @param databaseManager A {@link DatabaseManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param timeManager A {@link TimeManager} instance.
     * @param leaderboardManager A {@link LeaderboardManager} instance.
     * @param afkManager An {@link AFKManager} instance.
     * @param activityManager An {@link ActivityManager} instance.
     */
    public SkyPlayTimeCommand(
            @NotNull SkyPlayTime skyPlayTime,
            @NotNull LocaleManager localeManager,
            @NotNull LeaderboardSnapshotManager leaderboardSnapshotManager,
            @NotNull DatabaseManager databaseManager,
            @NotNull PlayerDataManager playerDataManager,
            @NotNull LeaderboardManager leaderboardManager,
            @NotNull TimeManager timeManager,
            @NotNull AFKManager afkManager,
            @NotNull ActivityManager activityManager) {
        this.skyPlayTime = skyPlayTime;
        this.localeManager = localeManager;
        this.leaderboardSnapshotManager = leaderboardSnapshotManager;
        this.databaseManager = databaseManager;
        this.playerDataManager = playerDataManager;
        this.leaderboardManager = leaderboardManager;
        this.timeManager = timeManager;
        this.afkManager = afkManager;
        this.activityManager = activityManager;
    }

    /**
     * Creates the {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API.
     * @return A {@link LiteralCommandNode} of type {@link CommandSourceStack} to register using the Lifecycle API.
     */
    public @NotNull LiteralCommandNode<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("skyplaytime");
        builder.requires(ctx -> ctx.getSender().hasPermission("skyplaytime.command.skyplaytime"));

        AddCommand addCommand = new AddCommand(skyPlayTime, localeManager, timeManager);
        AFKCommand afkCommand = new AFKCommand(skyPlayTime, localeManager, afkManager);
        BackupCommand backupCommand = new BackupCommand(skyPlayTime, localeManager, playerDataManager, databaseManager);
        DebugCommand debugCommand = new DebugCommand(skyPlayTime, localeManager, playerDataManager, afkManager, activityManager);
        ExemptCommand exemptCommand = new ExemptCommand(skyPlayTime, localeManager, leaderboardManager);
        HelpCommand helpCommand = new HelpCommand(localeManager);
        LeaderboardCommand leaderboardCommand = new LeaderboardCommand(skyPlayTime, localeManager, leaderboardManager, leaderboardSnapshotManager);
        ListCommand listCommand = new ListCommand(skyPlayTime, localeManager, playerDataManager);
        ReloadCommand reloadCommand = new ReloadCommand(skyPlayTime, localeManager);
        RemoveCommand removeCommand = new RemoveCommand(skyPlayTime, localeManager, timeManager);
        ResetCommand resetCommand = new ResetCommand(skyPlayTime, localeManager, timeManager);
        SaveCommand saveCommand = new SaveCommand(skyPlayTime, localeManager, playerDataManager);
        SetCommand setCommand = new SetCommand(skyPlayTime, localeManager, timeManager);
        TimeCommand timeCommand = new TimeCommand(skyPlayTime, localeManager, timeManager);
        UnExemptCommand unExemptCommand = new UnExemptCommand(skyPlayTime, localeManager, leaderboardManager);

        builder.then(addCommand.createCommand());
        builder.then(afkCommand.createCommand());
        builder.then(backupCommand.createCommand());
        builder.then(debugCommand.createCommand());
        builder.then(exemptCommand.createCommand());
        builder.then(helpCommand.createCommand());
        builder.then(leaderboardCommand.createCommand());
        builder.then(listCommand.createCommand());
        builder.then(reloadCommand.createCommand());
        builder.then(removeCommand.createCommand());
        builder.then(resetCommand.createCommand());
        builder.then(saveCommand.createCommand());
        builder.then(setCommand.createCommand());
        builder.then(timeCommand.createCommand());
        builder.then(unExemptCommand.createCommand());

        return builder.build();
    }
}

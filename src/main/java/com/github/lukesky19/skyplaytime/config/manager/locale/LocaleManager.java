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
package com.github.lukesky19.skyplaytime.config.manager.locale;

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.configurate.ConfigurationUtility;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.config.data.locale.Locale;
import com.github.lukesky19.skyplaytime.config.data.settings.Settings;
import com.github.lukesky19.skyplaytime.config.manager.settings.SettingsManager;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * This class manages the plugin's locale.
 */
public class LocaleManager {
    private final SkyPlayTime skyPlayTime;
    private final SettingsManager settingsManager;
    private Locale locale;
    private final @NotNull Locale.TimeFormat TIME_FORMAT = new Locale.TimeFormat(
            "",
            "<green><years></green> year(s)",
            "<green><months></green> month(s)",
            "<green><weeks></green> week(s)",
            "<green><days></green> day(s)",
            "<green><hours></green> hour(s)",
            "<green><minutes></green> minute(s)",
            "<green><seconds></green> second(s)",
            "");
    private final @NotNull Locale DEFAULT_LOCALE = new Locale(
            "1.0.0.0",
            "<aqua><bold>SkyPlayTime</bold></aqua><gray> ▪ </gray>",
            List.of("<aqua>SkyPlayTime is developed by <white><bold>lukeskywlker19</bold></white>.</aqua>",
                    "<aqua>Source code is released on GitHub: <click:OPEN_URL:https://github.com/lukesky19><yellow><underlined><bold>https://github.com/lukesky19</bold></underlined></yellow></click></aqua>",
                    " ",
                    "<aqua><bold>List of Commands:</bold></aqua>",
                    "<white>/</white><aqua>skyplaytime</aqua> <yellow>reload</yellow>",
                    "<white>/</white><aqua>skyplaytime</aqua> <yellow>help</yellow>",
                    "<white>/</white><aqua>skyplaytime</aqua> <yellow>time <player name> <session | daily | weely | monthly | yearly | total></yellow>",
                    "<white>/</white><aqua>afk</aqua> <yellow>afk [player name]</yellow>",
                    "<white>/</white><aqua>list</aqua>",
                    "<white>/</white><aqua>skyplaytime</aqua> <yellow>add <session | daily | weely | monthly | yearly | total> <player name> <time></yellow>",
                    "<white>/</white><aqua>skyplaytime</aqua> <yellow>remove <session | daily | weely | monthly | yearly | total> <player name> <time></yellow>",
                    "<white>/</white><aqua>skyplaytime</aqua> <yellow>set <session | daily | weely | monthly | yearly | total> <player name> <time></yellow>",
                    "<white>/</white><aqua>skyplaytime</aqua> <yellow>reset <session | daily | weely | monthly | yearly | total> [player name]</yellow>",
                    "<white>/</white><aqua>skyplaytime</aqua> <yellow>backup</yellow>",
                    "<white>/</white><aqua>skyplaytime</aqua> <yellow>exempt <player name></yellow>",
                    "<white>/</white><aqua>skyplaytime</aqua> <yellow>unexempt <player name></yellow>",
                    "<white>/</white><aqua>skyplaytime</aqua> <yellow>debug status</yellow>",
                    "<white>/</white><aqua>skyplaytime</aqua> <yellow>debug last-move</yellow>",
                    "<white>/</white><aqua>skyplaytime</aqua> <yellow>debug last-action</yellow>",
                    "<white>/</white><aqua>skyplaytime</aqua> <yellow>debug list</yellow>"),
            "<aqua>The plugin has been reloaded.</aqua>",
            "<gray>You are now afk.</gray>",
            "<gray>You are no longer afk.</gray>",
            "<gray>Player <aqua><player></aqua> is now afk.</gray>",
            "<gray>Player <aqua><player></aqua> is no longer afk.</gray>",
            "<red>This command can only be ran by a player.</red>",
            "<aqua>Forcefully marked player <yellow><player></yellow> as afk.</aqua>",
            "<aqua>Forcefully marked player <yellow><player></yellow> as no longer afk.</aqua>",
            "<red>Failed to forcefully toggle <yellow><player_name></yellow>'s AFK status.",
            "<aqua>Online Players</aqua> <gray>-</gray> <yellow><player_count></yellow>",
            "<gray><player_name></gray>",
            " <gray>[</gray><white>AFK<white><gray>]</gray>",
            "<gray>, </gray>",
            "<aqua><bold>Top 10 Players by Session Play Time</bold></aqua>",
            "<gray>[</gray><aqua><position></aqua><gray>]</gray> <yellow><player></yellow> <time>",
            "<gray>[</gray><aqua><position></aqua><gray>] ----------</gray>",
            TIME_FORMAT,
            "<aqua><bold>Top 10 Players by Daily Play Time</bold></aqua>",
            "<gray>[</gray><aqua><position></aqua><gray>]</gray> <yellow><player></yellow> <time>",
            "<gray>[</gray><aqua><position></aqua><gray>] ----------</gray>",
            TIME_FORMAT,
            "<aqua><bold>Top 10 Players by Weekly Play Time</bold></aqua>",
            "<gray>[</gray><aqua><position></aqua><gray>]</gray> <yellow><player></yellow> <time>",
            "<gray>[</gray><aqua><position></aqua><gray>] ----------</gray>",
            TIME_FORMAT,
            "<aqua><bold>Top 10 Players by Monthly Play Time</bold></aqua>",
            "<gray>[</gray><aqua><position></aqua><gray>]</gray> <yellow><player></yellow> <time>",
            "<gray>[</gray><aqua><position></aqua><gray>] ----------</gray>",
            TIME_FORMAT,
            "<aqua><bold>Top 10 Players by Yearly Play Time</bold></aqua>",
            "<gray>[</gray><aqua><position></aqua><gray>]</gray> <yellow><player></yellow> <time>",
            "<gray>[</gray><aqua><position></aqua><gray>] ----------</gray>",
            TIME_FORMAT,
            "<aqua><bold>Top 10 Players by Total Play Time</bold></aqua>",
            "<gray>[</gray><aqua><position></aqua><gray>]</gray> <yellow><player></yellow> <time>",
            "<gray>[</gray><aqua><position></aqua><gray>] ----------</gray>",
            TIME_FORMAT,
            "<aqua>Historical Leaderboard from File:</aqua> <yellow><file_name></yellow>",
            "<gray>[</gray><aqua><position></aqua><gray>]</gray> <yellow><player></yellow> <time>",
            "<gray>[</gray><aqua><position></aqua><gray>] ----------</gray>",
            TIME_FORMAT,
            "<red>Failed to load the historical leaderboard from file: <yellow><file_name></yellow></red>",
            "<aqua>Your session play time is: <yellow><time></yellow>.</aqua>",
            "<aqua>Player <yellow><player></yellow>'s session play time is: <yellow><time></yellow>.</aqua>",
            TIME_FORMAT,
            "<aqua>Your daily play time is: <yellow><time></yellow>.</aqua>",
            "<aqua>Player <yellow><player></yellow>'s daily play time is: <yellow><time></yellow>.</aqua>",
            TIME_FORMAT,
            "<aqua>Your weekly play time is: <yellow><time></yellow>.</aqua>",
            "<aqua>Player <yellow><player></yellow>'s weekly play time is: <yellow><time></yellow>.</aqua>",
            TIME_FORMAT,
            "<aqua>Your monthly play time is: <yellow><time></yellow>.</aqua>",
            "<aqua>Player <yellow><player></yellow>'s monthly play time is: <yellow><time></yellow>.</aqua>",
            TIME_FORMAT,
            "<aqua>Your yearly play time is: <yellow><time></yellow>.</aqua>",
            "<aqua>Player <yellow><player></yellow>'s yearly play time is: <yellow><time></yellow>.</aqua>",
            TIME_FORMAT,
            "<aqua>Your total play time is: <yellow><time></yellow>.</aqua>",
            "<aqua>Player <yellow><player></yellow>'s total play time is: <yellow><time></yellow>.</aqua>",
            TIME_FORMAT,
            "<aqua>Your session play time has been updated. Your session play time is now <yellow><time></yellow>.</aqua>",
            "<aqua>Player <yellow><player></yellow>'s session play time is now <yellow><time></yellow>.</aqua>",
            TIME_FORMAT,
            "<aqua>Your daily play time has been updated. Your daily play time is now <yellow><time></yellow>.</aqua>",
            "<aqua>Player <yellow><player></yellow>'s daily play time is now <yellow><time></yellow>.</aqua>",
            TIME_FORMAT,
            "<aqua>Your weekly play time has been updated. Your weekly play time is now <yellow><time></yellow>.</aqua>",
            "<aqua>Player <yellow><player></yellow>'s weekly play time is now <yellow><time></yellow>.</aqua>",
            TIME_FORMAT,
            "<aqua>Your monthly play time has been updated. Your monthly play time is now <yellow><time></yellow>.</aqua>",
            "<aqua>Player <yellow><player></yellow>'s monthly time is now <yellow><time></yellow>.</aqua>",
            TIME_FORMAT,
            "<aqua>Your yearly play time has been updated. Your yearly play time is now <yellow><time></yellow>.</aqua>",
            "<aqua>Player <yellow><player></yellow>'s yearly play time is now <yellow><time></yellow>.</aqua>",
            TIME_FORMAT,
            "<aqua>Your total play time has been updated. Your total play time is now <yellow><time></yellow>.</aqua>",
            "<aqua>Player <yellow><player></yellow>'s total time is now <yellow><time></yellow>.</aqua>",
            TIME_FORMAT,
            "<aqua>Your session play time has been reset.</aqua>",
            "<aqua>Successfully reset player <yellow><player></yellow>'s session play time.</aqua>",
            "<red>Failed to reset player <yellow><player></yellow>'s session play time.</red>",
            "<aqua>Successfully reset all player's session play time.</aqua>",
            "<red>Failed to reset all player's session play time.</red>",
            "<aqua>Your daily play time has been reset.</aqua>",
            "<aqua>Successfully reset player <yellow><player></yellow>'s daily play time.</aqua>",
            "<red>Failed to reset player <yellow><player></yellow>'s daily play time.</red>",
            "<aqua>Successfully reset all player's daily play time.</aqua>",
            "<red>Failed to reset all player's daily play time.</red>",
            "<aqua>Your weekly play time has been reset.</aqua>",
            "<aqua>Successfully reset player <yellow><player></yellow>'s weekly play time.</aqua>",
            "<red>Failed to reset player <yellow><player></yellow>'s weekly play time.</red>",
            "<aqua>Successfully reset all player's weekly play time.</aqua>",
            "<red>Failed to reset all player's weekly play time.</red>",
            "<aqua>Your monthly play time has been reset.</aqua>",
            "<aqua>Successfully reset player <yellow><player></yellow>'s monthly play time.</aqua>",
            "<red>Failed to reset player <yellow><player></yellow>'s monthly play time.</red>",
            "<aqua>Successfully reset all player's monthly play time.</aqua>",
            "<red>Failed to reset all player's monthly play time.</red>",
            "<aqua>Your yearly play time has been reset.</aqua>",
            "<aqua>Successfully reset player <yellow><player></yellow>'s yearly play time.</aqua>",
            "<red>Failed to reset player <yellow><player></yellow>'s yearly play time.</red>",
            "<aqua>Successfully reset all player's yearly play time.</aqua>",
            "<red>Failed to reset all player's yearly play time.</red>",
            "<aqua>Your total play time has been reset.</aqua>",
            "<aqua>Successfully reset player <yellow><player></yellow>'s total play time.</aqua>",
            "<red>Failed to reset player <yellow><player></yellow>'s total play time.</red>",
            "<aqua>Successfully reset all player's total play time.</aqua>",
            "<red>Failed to reset all player's total play time.</red>",
            "<aqua>All of your play time has been reset.</aqua>",
            "<aqua>Successfully reset all play time for player <yellow><player></yellow>.</aqua>",
            "<red>Failed to reset all play time for player <yellow><player></yellow>.</red>",
            "<aqua>Successfully reset all players' play time.</aqua>",
            "<red>Failed to reset all players' play time.</red>",
            "<red>The plugin failed to read or write to the database.</red>",
            "<aqua>The database has been successfully backed up!</aqua>",
            "<red>The database failed to be backed up!</red>",
            "<aqua>Successfully saved in-memory play-time to the database.</aqua>",
            "<red>Failed to save in-memory play-time to the database.</red>",
            "<aqua>Player <yellow><player></yellow> is now exempt from top playtime placeholders.<aqua>",
            "<aqua>Player <yellow><player></yellow> is now unexempt from top playtime placeholders.<aqua>"
    );

    /**
     * Constructor
     * @param skyPlayTime The plugin's main instance.
     * @param settingsManager A {@link SettingsManager instance.}
     */
    public LocaleManager(SkyPlayTime skyPlayTime, SettingsManager settingsManager) {
        this.skyPlayTime = skyPlayTime;
        this.settingsManager = settingsManager;
    }

    /**
     * Gets the plugin's {@link Locale} or the {@link #DEFAULT_LOCALE} if the locale config failed to load.
     * @return A {@link Locale} record.
     */
    public @NotNull Locale getLocale() {
        if (locale != null) return locale;

        return DEFAULT_LOCALE;
    }

    /**
     * (Re-)loads the plugin's locale.
     */
    public void loadLocale() {
        ComponentLogger logger = skyPlayTime.getComponentLogger();
        locale = null;

        saveDefaultLocales();

        Settings settings = settingsManager.getSettings();
        if(settings == null) {
            logger.warn("Failed to load locale configuration due to invalid plugin settings.");
            return;
        }

        Path path = Path.of(skyPlayTime.getDataFolder() + File.separator + "locale" + File.separator + (settings.locale() + ".yml"));

        @NotNull YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
        try {
            locale = loader.load().get(Locale.class);

            validateConfig();
        } catch (ConfigurateException e) {
            logger.error(AdventureUtil.deserialize("Failed to load locale configuration. " + e.getMessage()));
        }
    }

    /**
     * Copies any default locale files bundled with the plugin to the locale folder if they don't exist.
     */
    public void saveDefaultLocales() {
        Path path = Path.of(skyPlayTime.getDataFolder() + File.separator + "locale" + File.separator + "en_US.yml");
        if (!path.toFile().exists()) {
            skyPlayTime.saveResource("locale/en_US.yml", false);
        }
    }

    /**
     * Checks if any locale messages are invalid (null) and sets {@link #locale} to null, resulting in {@link #DEFAULT_LOCALE} being used.
     */
    public void validateConfig() {
        if (locale == null) return;

        if (locale.configVersion() == null
                || locale.prefix() == null
                || locale.reload() == null
                || locale.afkMessage() == null
                || locale.noLongerAfkMessage() == null
                || locale.playerAfkMessage() == null
                || locale.playerNoLongerAfkMessage() == null
                || locale.commandPlayerOnly() == null
                || locale.forcedPlayerAfkMessage() == null
                || locale.forcedPlayerNoLongerAfkMessage() == null
                || locale.forcedAfkToggleFailed() == null
                || locale.listTitle() == null
                || locale.playerName() == null
                || locale.afkIndicator() == null
                || locale.delimiter() == null
                || locale.sessionLeaderboardTitle() == null
                || locale.sessionLeaderboardPosition() == null
                || locale.sessionLeaderboardPositionEmpty() == null
                || isTimeFormatInvalid(locale.sessionLeaderboardTimePlaceholder())
                || locale.dailyLeaderboardTitle() == null
                || locale.dailyLeaderboardPosition() == null
                || locale.dailyLeaderboardPositionEmpty() == null
                || isTimeFormatInvalid(locale.dailyLeaderboardTimePlaceholder())
                || locale.weeklyLeaderboardTitle() == null
                || locale.weeklyLeaderboardPosition() == null
                || locale.weeklyLeaderboardPositionEmpty() == null
                || isTimeFormatInvalid(locale.weeklyLeaderboardTimePlaceholder())
                || locale.monthlyLeaderboardTitle() == null
                || locale.monthlyLeaderboardPosition() == null
                || locale.monthlyLeaderboardPositionEmpty() == null
                || isTimeFormatInvalid(locale.monthlyLeaderboardTimePlaceholder())
                || locale.yearlyLeaderboardTitle() == null
                || locale.yearlyLeaderboardPosition() == null
                || locale.yearlyLeaderboardPositionEmpty() == null
                || isTimeFormatInvalid(locale.yearlyLeaderboardTimePlaceholder())
                || locale.totalLeaderboardTitle() == null
                || locale.totalLeaderboardPosition() == null
                || locale.totalLeaderboardPositionEmpty() == null
                || isTimeFormatInvalid(locale.totalLeaderboardTimePlaceholder())
                || locale.historicalLeaderboardTitle() == null
                || locale.historicalLeaderboardPosition() == null
                || locale.historicalLeaderboardPositionEmpty() == null
                || isTimeFormatInvalid(locale.historicalLeaderboardTimePlaceholder())
                || locale.historicalLeaderboardLoadError() == null
                || locale.sessionPlayTime() == null
                || locale.playerSessionPlayTime() == null
                || isTimeFormatInvalid(locale.sessionPlayTimeTimePlaceholder())
                || locale.dailyPlayTime() == null
                || locale.playerDailyPlayTime() == null
                || isTimeFormatInvalid(locale.dailyPlayTimeTimePlaceholder())
                || locale.weeklyPlayTime() == null
                || locale.playerWeeklyPlayTime() == null
                || isTimeFormatInvalid(locale.weeklyPlayTimeTimePlaceholder())
                || locale.monthlyPlayTime() == null
                || locale.playerMonthlyPlayTime() == null
                || isTimeFormatInvalid(locale.monthlyPlayTimeTimePlaceholder())
                || locale.yearlyPlayTime() == null
                || locale.playerYearlyPlayTime() == null
                || isTimeFormatInvalid(locale.yearlyPlayTimeTimePlaceholder())
                || locale.totalPlayTime() == null
                || locale.playerTotalPlayTime() == null
                || isTimeFormatInvalid(locale.totalPlayTimeTimePlaceholder())
                || locale.sessionPlayTimeUpdated() == null
                || locale.playerSessionPlayTimeUpdated() == null
                || isTimeFormatInvalid(locale.sessionPlayTimeUpdatedTimePlaceholder())
                || locale.dailyPlayTimeUpdated() == null
                || locale.playerDailyPlayTimeUpdated() == null
                || isTimeFormatInvalid(locale.dailyPlayTimeUpdatedTimePlaceholder())
                || locale.weeklyPlayTimeUpdated() == null
                || locale.playerWeeklyPlayTimeUpdated() == null
                || isTimeFormatInvalid(locale.weeklyPlayTimeUpdatedTimePlaceholder())
                || locale.monthlyPlayTimeUpdated() == null
                || locale.playerMonthlyPlayTimeUpdated() == null
                || isTimeFormatInvalid(locale.monthlyPlayTimeUpdatedTimePlaceholder())
                || locale.yearlyPlayTimeUpdated() == null
                || locale.playerYearlyPlayTimeUpdated() == null
                || isTimeFormatInvalid(locale.yearlyPlayTimeUpdatedTimePlaceholder())
                || locale.totalPlayTimeUpdated() == null
                || locale.playerTotalPlayTimeUpdated() == null
                || isTimeFormatInvalid(locale.totalPlayTimeUpdatedTimePlaceholder())
                || locale.sessionPlayTimeReset() == null
                || locale.playerSessionPlayTimeReset() == null
                || locale.playerSessionPlayTimeResetError() == null
                || locale.resetSessionPlayTime() == null
                || locale.resetSessionPlayTimeError() == null
                || locale.dailyPlayTimeReset() == null
                || locale.playerDailyPlayTimeReset() == null
                || locale.playerDailyPlayTimeResetError() == null
                || locale.resetDailyPlayTime() == null
                || locale.resetDailyPlayTimeError() == null
                || locale.weeklyPlayTimeReset() == null
                || locale.playerWeeklyPlayTimeReset() == null
                || locale.playerWeeklyPlayTimeResetError() == null
                || locale.resetWeeklyPlayTime() == null
                || locale.resetWeeklyPlayTimeError() == null
                || locale.monthlyPlayTimeReset() == null
                || locale.playerMonthlyPlayTimeReset() == null
                || locale.playerMonthlyPlayTimeResetError() == null
                || locale.resetMonthlyPlayTime() == null
                || locale.resetMonthlyPlayTimeError() == null
                || locale.yearlyPlayTimeReset() == null
                || locale.playerYearlyPlayTimeReset() == null
                || locale.playerYearlyPlayTimeResetError() == null
                || locale.resetYearlyPlayTime() == null
                || locale.resetYearlyPlayTimeError() == null
                || locale.totalPlayTimeReset() == null
                || locale.playerTotalPlayTimeReset() == null
                || locale.playerTotalPlayTimeResetError() == null
                || locale.resetTotalPlayTime() == null
                || locale.resetTotalPlayTimeError() == null
                || locale.allPlayTimeReset() == null
                || locale.playerAllPlayTimeReset() == null
                || locale.playerAllPlayTimeResetError() == null
                || locale.resetAllPlayTime() == null
                || locale.resetAllPlayTimeError() == null
                || locale.playerExempt() == null
                || locale.playerUnexempt() == null
                || locale.databaseError() == null
                || locale.databaseBackupSuccess() == null
                || locale.databaseBackupError() == null
                || locale.playTimeSaveSuccess() == null
                || locale.playTimeSaveError() == null) {

            locale = null;
        }
    }

    /**
     * Checks if all {@link String}s in a {@link Locale.TimeFormat} are null.
     * @param timeFormat The {@link Locale.TimeFormat} to check.
     * @return true if invalid, false if not.
     */
    private boolean isTimeFormatInvalid(@NotNull Locale.TimeFormat timeFormat) {
        return timeFormat.prefix() == null
                || timeFormat.years() == null
                || timeFormat.months() == null
                || timeFormat.weeks() == null
                || timeFormat.days() == null
                || timeFormat.hours() == null
                || timeFormat.minutes() == null
                || timeFormat.seconds() == null
                || timeFormat.suffix() == null;
    }
}

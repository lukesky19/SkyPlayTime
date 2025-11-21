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
package com.github.lukesky19.skyplaytime.config.data.locale;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This record contains the locale configuration for the plugin's messages.
 * @param configVersion The config version.
 * @param prefix The plugin's prefix.
 * @param help The plugin's help messages.
 * @param reload The message sent when the plugin is reloaded.
 * @param commandPlayerOnly The message sent when a command is player only.
 * @param listTitle The title to send for the /list command.
 * @param playerName The player name format to use in the /list command.
 * @param afkIndicator The AFK indicator format to use in the /list command.
 * @param delimiter The delimiter to use for separating players in the /list command.
 * @param sessionLeaderboardTitle The session leaderboard title.
 * @param sessionLeaderboardPosition The session leaderboard positon text.
 * @param sessionLeaderboardPositionEmpty The session leaderboard position empty text.
 * @param sessionLeaderboardTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param dailyLeaderboardTitle The daily leaderboard title.
 * @param dailyLeaderboardPosition The daily leaderboard positon text.
 * @param dailyLeaderboardPositionEmpty The daily leaderboard position empty text.
 * @param dailyLeaderboardTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param weeklyLeaderboardTitle The weekly leaderboard title.
 * @param weeklyLeaderboardPosition The weekly leaderboard positon text.
 * @param weeklyLeaderboardPositionEmpty The weekly leaderboard position empty text.
 * @param weeklyLeaderboardTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param monthlyLeaderboardTitle The monthly leaderboard title.
 * @param monthlyLeaderboardPosition The monthly leaderboard positon text.
 * @param monthlyLeaderboardPositionEmpty The monthly leaderboard position empty text.
 * @param monthlyLeaderboardTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param yearlyLeaderboardTitle The yearly leaderboard title.
 * @param yearlyLeaderboardPosition The yearly leaderboard positon text.
 * @param yearlyLeaderboardPositionEmpty The yearly leaderboard position empty text.
 * @param yearlyLeaderboardTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param totalLeaderboardTitle The total leaderboard title.
 * @param totalLeaderboardPosition The total leaderboard positon text.
 * @param totalLeaderboardPositionEmpty The total leaderboard position empty text.
 * @param totalLeaderboardTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param historicalLeaderboardTitle The historical leaderboard title.
 * @param historicalLeaderboardPosition The historical leaderboard positon text.
 * @param historicalLeaderboardPositionEmpty The historical leaderboard position empty text.
 * @param historicalLeaderboardTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param historicalLeaderboardLoadError The message sent when the historical leaderboard fails to load.
 * @param afkMessage The message sent to a player that is marked AFK.
 * @param noLongerAfkMessage The message sent to a player that is marked no longer AFK.
 * @param playerAfkMessage The message sent to all other players when another player goes AFK.
 * @param playerNoLongerAfkMessage The message sent to all other players when another player is no longer AFK.
 * @param forcedPlayerAfkMessage The message sent to the player or console that forces a player AFK.
 * @param forcedPlayerNoLongerAfkMessage The message sent to the player or console that forces a player as no longer AFK.
 * @param forcedAfkToggleFailed The message sent to the player or console when a error occurs while toggling a player's AFK status.
 * @param databaseError The message sent when an error with the database occurs.
 * @param databaseBackupSuccess The message sent when the database backs up successfully.
 * @param databaseBackupError The message sent when the database fails to be backed up.
 * @param playTimeSaveSuccess The message sent when play time saves successfully.
 * @param playTimeSaveError The message sent when play time fails to be saved.
 * @param sessionPlayTime The message sent to a player viewing their own session play time.
 * @param playerSessionPlayTime The message sent to a player viewing another player's session play time.
 * @param sessionPlayTimeTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param dailyPlayTime The message sent to a player viewing their own daily play time.
 * @param playerDailyPlayTime The message sent to a player viewing another player's daily play time.
 * @param dailyPlayTimeTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param weeklyPlayTime The message sent to a player viewing their own weekly play time.
 * @param playerWeeklyPlayTime The message sent to a player viewing another player's weekly play time.
 * @param weeklyPlayTimeTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param monthlyPlayTime The message sent to a player viewing their own monthly play time.
 * @param playerMonthlyPlayTime The message sent to a player viewing another player's monthly play time.
 * @param monthlyPlayTimeTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param yearlyPlayTime The message sent to a player viewing their own yearly play time.
 * @param playerYearlyPlayTime The message sent to a player viewing another player's yearly play time.
 * @param yearlyPlayTimeTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param totalPlayTime The message sent to a player viewing their own total play time.
 * @param playerTotalPlayTime  The message sent to a player viewing another player's total play time.
 * @param totalPlayTimeTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param sessionPlayTimeUpdated The message sent to a player when their session play time was updated.
 * @param playerSessionPlayTimeUpdated The message sent to the player who modified another player's session play time.
 * @param sessionPlayTimeUpdatedTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param dailyPlayTimeUpdated The message sent to a player when their daily play time was updated.
 * @param playerDailyPlayTimeUpdated The message sent to the player who modified another player's daily play time.
 * @param dailyPlayTimeUpdatedTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param weeklyPlayTimeUpdated The message sent to a player when their weekly play time was updated.
 * @param playerWeeklyPlayTimeUpdated The message sent to the player who modified another player's weekly play time.
 * @param weeklyPlayTimeUpdatedTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param monthlyPlayTimeUpdated The message sent to a player when their monthly play time was updated.
 * @param playerMonthlyPlayTimeUpdated The message sent to the player who modified another player's monthly play time.
 * @param monthlyPlayTimeUpdatedTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param yearlyPlayTimeUpdated The message sent to a player when their yearly play time was updated.
 * @param playerYearlyPlayTimeUpdated The message sent to the player who modified another player's yearly play time.
 * @param yearlyPlayTimeUpdatedTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param totalPlayTimeUpdated The message sent to a player when their total play time was updated.
 * @param playerTotalPlayTimeUpdated The message sent to the player who modified another player's total play time.
 * @param totalPlayTimeUpdatedTimePlaceholder The {@link TimeFormat} configuration to create the text that replaces a {@literal <time>} placeholder.
 * @param sessionPlayTimeReset The message sent to the player who had their session play time reset.
 * @param playerSessionPlayTimeReset The message sent to the player who reset another player's session play time.
 * @param playerSessionPlayTimeResetError The message sent to the player who attempted to reset another player's session play time and an error occurred.
 * @param resetSessionPlayTime The message sent to the player who reset all player's session play time.
 * @param resetSessionPlayTimeError The message sent to the player who attempted to reset all player's session play time and an error occurred.
 * @param dailyPlayTimeReset The message sent to the player who had their daily play time reset.
 * @param playerDailyPlayTimeReset The message sent to the player who reset another player's daily play time.
 * @param playerDailyPlayTimeResetError The message sent to the player who attempted to reset another player's daily play time and an error occurred.
 * @param resetDailyPlayTime The message sent to the player who reset all player's daily play time.
 * @param resetDailyPlayTimeError The message sent to the player who attempted to reset all player's daily play time and an error occurred.
 * @param weeklyPlayTimeReset The message sent to the player who had their weekly play time reset.
 * @param playerWeeklyPlayTimeReset The message sent to the player who reset another player's weekly play time.
 * @param playerWeeklyPlayTimeResetError The message sent to the player who attempted to reset another player's weekly play time and an error occurred.
 * @param resetWeeklyPlayTime The message sent to the player who reset all player's weekly play time.
 * @param resetWeeklyPlayTimeError The message sent to the player who attempted to reset all player's weekly play time and an error occurred.
 * @param monthlyPlayTimeReset The message sent to the player who had their monthly play time reset.
 * @param playerMonthlyPlayTimeReset The message sent to the player who reset another player's monthly play time.
 * @param playerMonthlyPlayTimeResetError The message sent to the player who attempted to reset another player's monthly play time and an error occurred.
 * @param resetMonthlyPlayTime The message sent to the player who reset all player's monthly play time.
 * @param resetMonthlyPlayTimeError The message sent to the player who attempted to reset all player's monthly play time and an error occurred.
 * @param yearlyPlayTimeReset The message sent to the player who had their yearly play time reset.
 * @param playerYearlyPlayTimeReset The message sent to the player who reset another player's yearly play time.
 * @param playerYearlyPlayTimeResetError The message sent to the player who attempted to reset another player's yearly play time and an error occurred.
 * @param resetYearlyPlayTime The message sent to the player who reset all player's yearly play time.
 * @param resetYearlyPlayTimeError The message sent to the player who attempted to reset all player's yearly play time and an error occurred.
 * @param totalPlayTimeReset The message sent to the player who had their total play time reset.
 * @param playerTotalPlayTimeReset The message sent to the player who reset another player's total play time.
 * @param playerTotalPlayTimeResetError The message sent to the player who attempted to reset another player's total play time and an error occurred.
 * @param resetTotalPlayTime The message sent to the player who reset all player's total play time.
 * @param resetTotalPlayTimeError The message sent to the player who attempted to reset all player's total play time and an error occurred.
 * @param allPlayTimeReset The message sent to the player who had all their play time reset.
 * @param playerAllPlayTimeReset The message sent to the player who reset all of another player's play time.
 * @param playerAllPlayTimeResetError The message sent to the player who attempted to reset all of another player's play time and an error occurred.
 * @param resetAllPlayTime The message sent to the player who reset all of another player's play time.
 * @param resetAllPlayTimeError The message sent to the player who attempted to reset all of another player's play time and an error occurred.
 * @param playerExempt The message sent to the player or console who marked another player as exempt from leaderboard reporting.
 * @param playerUnexempt The message sent to the player or console who marked another player as no longer exempt from leaderboard reporting.
 */
@ConfigSerializable
public record Locale(
        String configVersion,
        String prefix,
        @NotNull List<String> help,
        String afkMessage,
        String noLongerAfkMessage,
        String playerAfkMessage,
        String playerNoLongerAfkMessage,
        String reload,
        String commandPlayerOnly,
        String forcedPlayerAfkMessage,
        String forcedPlayerNoLongerAfkMessage,
        String forcedAfkToggleFailed,
        String listTitle,
        String playerName,
        String afkIndicator,
        String delimiter,
        String sessionLeaderboardTitle,
        String sessionLeaderboardPosition,
        String sessionLeaderboardPositionEmpty,
        TimeFormat sessionLeaderboardTimePlaceholder,
        String dailyLeaderboardTitle,
        String dailyLeaderboardPosition,
        String dailyLeaderboardPositionEmpty,
        TimeFormat dailyLeaderboardTimePlaceholder,
        String weeklyLeaderboardTitle,
        String weeklyLeaderboardPosition,
        String weeklyLeaderboardPositionEmpty,
        TimeFormat weeklyLeaderboardTimePlaceholder,
        String monthlyLeaderboardTitle,
        String monthlyLeaderboardPosition,
        String monthlyLeaderboardPositionEmpty,
        TimeFormat monthlyLeaderboardTimePlaceholder,
        String yearlyLeaderboardTitle,
        String yearlyLeaderboardPosition,
        String yearlyLeaderboardPositionEmpty,
        TimeFormat yearlyLeaderboardTimePlaceholder,
        String totalLeaderboardTitle,
        String totalLeaderboardPosition,
        String totalLeaderboardPositionEmpty,
        TimeFormat totalLeaderboardTimePlaceholder,
        String historicalLeaderboardTitle,
        String historicalLeaderboardPosition,
        String historicalLeaderboardPositionEmpty,
        TimeFormat historicalLeaderboardTimePlaceholder,
        String historicalLeaderboardLoadError,
        String sessionPlayTime,
        String playerSessionPlayTime,
        TimeFormat sessionPlayTimeTimePlaceholder,
        String dailyPlayTime,
        String playerDailyPlayTime,
        TimeFormat dailyPlayTimeTimePlaceholder,
        String weeklyPlayTime,
        String playerWeeklyPlayTime,
        TimeFormat weeklyPlayTimeTimePlaceholder,
        String monthlyPlayTime,
        String playerMonthlyPlayTime,
        TimeFormat monthlyPlayTimeTimePlaceholder,
        String yearlyPlayTime,
        String playerYearlyPlayTime,
        TimeFormat yearlyPlayTimeTimePlaceholder,
        String totalPlayTime,
        String playerTotalPlayTime,
        TimeFormat totalPlayTimeTimePlaceholder,
        String sessionPlayTimeUpdated,
        String playerSessionPlayTimeUpdated,
        TimeFormat sessionPlayTimeUpdatedTimePlaceholder,
        String dailyPlayTimeUpdated,
        String playerDailyPlayTimeUpdated,
        TimeFormat dailyPlayTimeUpdatedTimePlaceholder,
        String weeklyPlayTimeUpdated,
        String playerWeeklyPlayTimeUpdated,
        TimeFormat weeklyPlayTimeUpdatedTimePlaceholder,
        String monthlyPlayTimeUpdated,
        String playerMonthlyPlayTimeUpdated,
        TimeFormat monthlyPlayTimeUpdatedTimePlaceholder,
        String yearlyPlayTimeUpdated,
        String playerYearlyPlayTimeUpdated,
        TimeFormat yearlyPlayTimeUpdatedTimePlaceholder,
        String totalPlayTimeUpdated,
        String playerTotalPlayTimeUpdated,
        TimeFormat totalPlayTimeUpdatedTimePlaceholder,
        String sessionPlayTimeReset,
        String playerSessionPlayTimeReset,
        String playerSessionPlayTimeResetError,
        String resetSessionPlayTime,
        String resetSessionPlayTimeError,
        String dailyPlayTimeReset,
        String playerDailyPlayTimeReset,
        String playerDailyPlayTimeResetError,
        String resetDailyPlayTime,
        String resetDailyPlayTimeError,
        String weeklyPlayTimeReset,
        String playerWeeklyPlayTimeReset,
        String playerWeeklyPlayTimeResetError,
        String resetWeeklyPlayTime,
        String resetWeeklyPlayTimeError,
        String monthlyPlayTimeReset,
        String playerMonthlyPlayTimeReset,
        String playerMonthlyPlayTimeResetError,
        String resetMonthlyPlayTime,
        String resetMonthlyPlayTimeError,
        String yearlyPlayTimeReset,
        String playerYearlyPlayTimeReset,
        String playerYearlyPlayTimeResetError,
        String resetYearlyPlayTime,
        String resetYearlyPlayTimeError,
        String totalPlayTimeReset,
        String playerTotalPlayTimeReset,
        String playerTotalPlayTimeResetError,
        String resetTotalPlayTime,
        String resetTotalPlayTimeError,
        String allPlayTimeReset,
        String playerAllPlayTimeReset,
        String playerAllPlayTimeResetError,
        String resetAllPlayTime,
        String resetAllPlayTimeError,
        String databaseError,
        String databaseBackupSuccess,
        String databaseBackupError,
        String playTimeSaveSuccess,
        String playTimeSaveError,
        String playerExempt,
        String playerUnexempt) {
    /**
     * The record containing the data necessary to format a {@literal <time>} placeholder.
     * @param prefix The text to display before the first time unit.
     * @param years The text to display when the player's time enters years.
     * @param months The text to display when the player's time enters months.
     * @param weeks The text to display when the player's time enters weeks.
     * @param days The text to display when the player's time enters days.
     * @param hours The text to display when the player's time enters hours.
     * @param minutes The text to display when the player's time enters minutes.
     * @param seconds The text to display when the player's time enters seconds.
     * @param suffix The text to display after the last time unit.
     */
    @ConfigSerializable
    public record TimeFormat(
            String prefix,
            String years,
            String months,
            String weeks,
            String days,
            String hours,
            String minutes,
            String seconds,
            String suffix) {}
}

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
package com.github.lukesky19.skyplaytime.placeholderapi;

import com.github.lukesky19.skylib.common.api.time.Time;
import com.github.lukesky19.skylib.common.api.time.TimeUnit;
import com.github.lukesky19.skylib.common.api.time.TimeUtil;
import com.github.lukesky19.skyplaytime.leaderboard.data.Position;
import com.github.lukesky19.skyplaytime.player.manager.AFKManager;
import com.github.lukesky19.skyplaytime.leaderboard.manager.LeaderboardManager;
import com.github.lukesky19.skyplaytime.player.manager.PlayerDataManager;
import com.github.lukesky19.skyplaytime.player.data.PlayerData;
import com.github.lukesky19.skyplaytime.util.TimeCategory;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.*;

/**
 * This class supplies placeholders using PlaceholderAPI.
 */
public class SkyPlayTimeExpansion extends PlaceholderExpansion {
    private final @NonNull LeaderboardManager leaderboardManager;
    private final @NonNull PlayerDataManager playerDataManager;
    private final @NonNull AFKManager afkManager;

    /**
     * Constructor
     * @param leaderboardManager A {@link LeaderboardManager} instance.
     * @param playerDataManager A {@link PlayerDataManager} instance.
     * @param afkManager An {@link AFKManager} instance.
     */
    public SkyPlayTimeExpansion(
            @NonNull LeaderboardManager leaderboardManager,
            @NonNull PlayerDataManager playerDataManager,
            @NonNull AFKManager afkManager) {
        this.leaderboardManager = leaderboardManager;
        this.playerDataManager = playerDataManager;
        this.afkManager = afkManager;
    }

    /**
     * Get the author of the expansion.
     * @return The name of the author.
     */
    @Override
    public @NonNull String getAuthor() {
        return "lukeskywlker19";
    }

    /**
     * Get the identifier of the expansion.
     * @return The idenifier for the expansion.
     */
    @Override
    public @NonNull String getIdentifier() {
        return "SkyPlayTime";
    }

    /**
     * Get the version of the expansion.
     * @return The version of the expansion.
     */
    @Override
    public @NonNull String getVersion() {
        return "1.0.0.0";
    }

    /**
     * Set the persist value to always be true so that PlaceholderAPI will not unregister the expansion on reload.
     * @return Always returns true.
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * When a request is made to the expansion for a placeholder, attempt to parse the placeholder and return the result.
     * @param player The {@link Player} making the request.
     * @param placeholder The placeholder.
     * @return The resolved placeholder text, an empty {@link String}, or null.
     */
    @Override
    public @Nullable String onPlaceholderRequest(@NonNull Player player, @NonNull String placeholder) {
        UUID uuid = player.getUniqueId();
        placeholder = placeholder.toLowerCase();

        switch(placeholder.toLowerCase()) {
            case "session_time" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return formatTime(playerData.getSessionPlayTimeSeconds());
            }

            case "daily_time" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return formatTime(playerData.getDailyPlayTimeSeconds());
            }

            case "weekly_time" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return formatTime(playerData.getWeeklyPlayTimeSeconds());
            }

            case "monthly_time" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return formatTime(playerData.getMonthlyPlayTimeSeconds());
            }

            case "yearly_time" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return formatTime(playerData.getYearlyPlayTimeSeconds());
            }

            case "total_time" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return formatTime(playerData.getTotalPlayTimeSeconds());
            }

            case "session_time_raw" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return String.valueOf(playerData.getSessionPlayTimeSeconds());
            }

            case "daily_time_raw" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return String.valueOf(playerData.getDailyPlayTimeSeconds());
            }

            case "weekly_time_raw" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return String.valueOf(playerData.getWeeklyPlayTimeSeconds());
            }

            case "monthly_time_raw" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return String.valueOf(playerData.getMonthlyPlayTimeSeconds());
            }

            case "yearly_time_raw" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return String.valueOf(playerData.getYearlyPlayTimeSeconds());
            }

            case "total_time_raw" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return String.valueOf(playerData.getTotalPlayTimeSeconds());
            }

            case "afk" -> {
                if(afkManager.isPlayerAFK(player)) {
                    return "AFK";
                } else {
                    return "";
                }
            }

            default -> {
                // 0 - top
                // 1 - <category>
                // 2 - time
                // 3 - <position>
                // 4 onwards = units
                if(placeholder.startsWith("top_session_time")
                        || placeholder.startsWith("top_daily_time")
                        || placeholder.startsWith("top_weekly_time")
                        || placeholder.startsWith("top_monthly_time")
                        || placeholder.startsWith("top_yearly_time")
                        || placeholder.startsWith("top_total_time")) {
                    String[] parts = placeholder.split("_");
                    if(parts.length < 5) return "";

                    // Extract the category
                    String categoryName = parts[1];
                    TimeCategory timeCategory;
                    try {
                        timeCategory = TimeCategory.valueOf(categoryName.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        return "";
                    }

                    // Extract the position
                    int position;
                    try {
                        position = Integer.parseInt(parts[3]);
                    } catch (NumberFormatException e) {
                        return "";
                    }

                    List<String> timeUnits = new ArrayList<>(Arrays.asList(parts).subList(4, parts.length));

                    return formatPlayerTimeAtPosition(timeCategory, position, timeUnits);
                } else if(placeholder.startsWith("top_session_name")
                        || placeholder.startsWith("top_daily_name")
                        || placeholder.startsWith("top_weekly_name")
                        || placeholder.startsWith("top_monthly_name")
                        || placeholder.startsWith("top_yearly_name")
                        || placeholder.startsWith("top_total_name")) {
                    String[] parts = placeholder.split("_");
                    if(parts.length < 4) {
                        return "";
                    }

                    // Extract the category
                    String category = parts[1].toUpperCase();
                    TimeCategory timeCategory;
                    try {
                        timeCategory = TimeCategory.valueOf(category);
                    } catch (IllegalArgumentException e) {
                        return "";
                    }

                    // Extract the position
                    int position;
                    try {
                        position = Integer.parseInt(parts[3]);
                    } catch (NumberFormatException e) {
                        return "";
                    }

                    return getPlayerNameAtPosition(position, timeCategory);
                }

                return null;
            }
        }
    }

    /**
     * When a request is made to the expansion for a placeholder, attempt to parse the placeholder and return the result.
     * @param player The {@link OfflinePlayer} making the request.
     * @param placeholder The placeholder.
     * @return The resolved placeholder text, an empty {@link String}, or null.
     */
    @Override
    public @Nullable String onRequest(@NonNull OfflinePlayer player, @NonNull String placeholder) {
        UUID uuid = player.getUniqueId();
        placeholder = placeholder.toLowerCase();

        switch(placeholder.toLowerCase()) {
            case "session_time" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return formatTime(playerData.getSessionPlayTimeSeconds());
            }

            case "daily_time" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return formatTime(playerData.getDailyPlayTimeSeconds());
            }

            case "weekly_time" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return formatTime(playerData.getWeeklyPlayTimeSeconds());
            }

            case "monthly_time" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return formatTime(playerData.getMonthlyPlayTimeSeconds());
            }

            case "yearly_time" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return formatTime(playerData.getYearlyPlayTimeSeconds());
            }

            case "total_time" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return formatTime(playerData.getTotalPlayTimeSeconds());
            }

            case "session_time_raw" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return String.valueOf(playerData.getSessionPlayTimeSeconds());
            }

            case "daily_time_raw" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return String.valueOf(playerData.getDailyPlayTimeSeconds());
            }

            case "weekly_time_raw" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return String.valueOf(playerData.getWeeklyPlayTimeSeconds());
            }

            case "monthly_time_raw" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return String.valueOf(playerData.getMonthlyPlayTimeSeconds());
            }

            case "yearly_time_raw" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return String.valueOf(playerData.getYearlyPlayTimeSeconds());
            }

            case "total_time_raw" -> {
                PlayerData playerData = playerDataManager.getPlayerData(uuid);
                if(playerData == null) return "";

                return String.valueOf(playerData.getTotalPlayTimeSeconds());
            }

            case "afk" -> {
                if(afkManager.isPlayerAFK(player.getUniqueId())) {
                    return "AFK";
                } else {
                    return "";
                }
            }

            default -> {
                // 0 - top
                // 1 - <category>
                // 2 - time
                // 3 - <position>
                // 4 onwards = units
                if(placeholder.startsWith("top_session_time")
                        || placeholder.startsWith("top_daily_time")
                        || placeholder.startsWith("top_weekly_time")
                        || placeholder.startsWith("top_monthly_time")
                        || placeholder.startsWith("top_yearly_time")
                        || placeholder.startsWith("top_total_time")) {
                    String[] parts = placeholder.split("_");
                    if(parts.length < 5) return "";

                    // Extract the category
                    String categoryName = parts[1];
                    TimeCategory timeCategory;
                    try {
                        timeCategory = TimeCategory.valueOf(categoryName.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        return "";
                    }

                    // Extract the position
                    int position;
                    try {
                        position = Integer.parseInt(parts[3]);
                    } catch (NumberFormatException e) {
                        return "";
                    }

                    List<String> timeUnits = new ArrayList<>(Arrays.asList(parts).subList(4, parts.length));

                    return formatPlayerTimeAtPosition(timeCategory, position, timeUnits);
                } else if(placeholder.startsWith("top_session_name")
                        || placeholder.startsWith("top_daily_name")
                        || placeholder.startsWith("top_weekly_name")
                        || placeholder.startsWith("top_monthly_name")
                        || placeholder.startsWith("top_yearly_name")
                        || placeholder.startsWith("top_total_name")) {
                    String[] parts = placeholder.split("_");
                    if(parts.length < 4) {
                        return "";
                    }

                    // Extract the category
                    String category = parts[1].toUpperCase();
                    TimeCategory timeCategory;
                    try {
                        timeCategory = TimeCategory.valueOf(category);
                    } catch (IllegalArgumentException e) {
                        return "";
                    }

                    // Extract the position
                    int position;
                    try {
                        position = Integer.parseInt(parts[3]);
                    } catch (NumberFormatException e) {
                        return "";
                    }

                    return getPlayerNameAtPosition(position, timeCategory);
                }

                return null;
            }
        }
    }

    /**
     * Get the player's name for the provide {@link TimeCategory} and position.
     * @param positionNumber The position number to get the player name for.
     * @param timeCategory The {@link TimeCategory} to get.
     * @return A {@link String} with the player's name or an empty {@link String}
     */
    private @NonNull String getPlayerNameAtPosition(int positionNumber, @NonNull TimeCategory timeCategory) {
        Position position = leaderboardManager.getPositionForCategoryAtPositionNumber(timeCategory, positionNumber);
        if(position == null) {
            return "";
        }

        return position.name();
    }

    /**
     * Formats the play time for the given category, position, and time units.
     * @param timeCategory The {@link TimeCategory}.
     * @param positionNumber The position number.
     * @param timeUnits The time units to display.
     * @return A {@link String} containing the formatted time or an empty {@link String} for no player at said position.
     */
    private @NonNull String formatPlayerTimeAtPosition(@NonNull TimeCategory timeCategory, int positionNumber, @NonNull List<String> timeUnits) {
        Position position = leaderboardManager.getPositionForCategoryAtPositionNumber(timeCategory, positionNumber);
        if(position == null) return "";

        // Format the time based on the specified time units
        return formatTime(position.seconds(), timeUnits);
    }

    /**
     * Create a String that displays all time units
     * @param totalSeconds The total time in seconds.
     * @return A {@link String} containing the formatted time.
     */
    private @NonNull String formatTime(long totalSeconds) {
        Time timeRecord = TimeUtil.millisToTime(totalSeconds * 1000L);
        StringBuilder messageBuilder = new StringBuilder();
        boolean firstUnit = true;

        if(timeRecord.years() > 0) {
            if(!firstUnit) messageBuilder.append(" ");
            messageBuilder.append(timeRecord.years()).append(" year").append(timeRecord.years() > 1 ? "s" : "");
            firstUnit = false;
        }

        if (timeRecord.months() > 0) {
            if (!firstUnit) messageBuilder.append(" ");
            messageBuilder.append(timeRecord.months()).append(" month").append(timeRecord.months() > 1 ? "s" : "");
            firstUnit = false;
        }

        if (timeRecord.weeks() > 0) {
            if (!firstUnit) messageBuilder.append(" ");
            messageBuilder.append(timeRecord.weeks()).append(" week").append(timeRecord.weeks() > 1 ? "s" : "");
            firstUnit = false;
        }

        if (timeRecord.days() > 0) {
            if (!firstUnit) messageBuilder.append(" ");
            messageBuilder.append(timeRecord.days()).append(" day").append(timeRecord.days() > 1 ? "s" : "");
            firstUnit = false;
        }

        if (timeRecord.hours() > 0) {
            if (!firstUnit) messageBuilder.append(" ");
            messageBuilder.append(timeRecord.hours()).append(" hour").append(timeRecord.hours() > 1 ? "s" : "");
            firstUnit = false;
        }

        if (timeRecord.minutes() > 0) {
            if (!firstUnit) messageBuilder.append(" ");
            messageBuilder.append(timeRecord.minutes()).append(" minute").append(timeRecord.minutes() > 1 ? "s" : "");
            firstUnit = false;
        }

        if (timeRecord.seconds() > 0) {
            if (!firstUnit) messageBuilder.append(" ");
            messageBuilder.append(timeRecord.seconds()).append(" second").append(timeRecord.seconds() > 1 ? "s" : "");
            firstUnit = false;
        }

        // If no units were added, return "0 seconds" or similar
        if (firstUnit) {
            return "0 seconds";
        }

        return messageBuilder.toString();
    }

    /**
     * Create a String that displays the time units provided.
     * @param totalSeconds The total time in seconds.
     * @param timeUnits The time units to display as a {@link List} of {@link String}.
     * @return A {@link String} containing the formatted time.
     */
    private @NonNull String formatTime(long totalSeconds, @NonNull List<String> timeUnits) {
        TimeUnit timeUnit = getHighestTimeUnit(timeUnits);

        if(timeUnit == null) {
            return "";
        }

        Time timeRecord = TimeUtil.millisToTime(totalSeconds * 1000L, timeUnit);
        StringBuilder messageBuilder = new StringBuilder();
        boolean firstUnit = true;

        for(String unit : timeUnits) {
            switch (unit.toLowerCase()) {
                case "years" -> {
                    if (timeRecord.years() > 0) {
                        if (!firstUnit) messageBuilder.append(" ");
                        messageBuilder.append(timeRecord.years()).append(" year").append(timeRecord.years() > 1 ? "s" : "");
                        firstUnit = false;
                    }

                }

                case "yr" -> {
                    if (timeRecord.years() > 0) {
                        if (!firstUnit) messageBuilder.append(" ");
                        messageBuilder.append(timeRecord.years()).append("yr");
                        firstUnit = false;
                    }

                }

                case "y" -> {
                    if (timeRecord.years() > 0) {
                        if (!firstUnit) messageBuilder.append(" ");
                        messageBuilder.append(timeRecord.years()).append("y");
                        firstUnit = false;
                    }

                }

                case "months" -> {
                    if (timeRecord.months() > 0) {
                        if (!firstUnit) messageBuilder.append(" ");
                        messageBuilder.append(timeRecord.months()).append(" month").append(timeRecord.months() > 1 ? "s" : "");
                        firstUnit = false;
                    }

                }

                case "mo" -> {
                    if (timeRecord.months() > 0) {
                        if (!firstUnit) messageBuilder.append(" ");
                        messageBuilder.append(timeRecord.months()).append("M");
                        firstUnit = false;
                    }

                }

                case "weeks" -> {
                    if (timeRecord.weeks() > 0) {
                        if (!firstUnit) messageBuilder.append(" ");
                        messageBuilder.append(timeRecord.weeks()).append(" week").append(timeRecord.weeks() > 1 ? "s" : "");
                        firstUnit = false;
                    }

                }

                case "w" -> {
                    if (timeRecord.weeks() > 0) {
                        if (!firstUnit) messageBuilder.append(" ");
                        messageBuilder.append(timeRecord.weeks()).append("w");
                        firstUnit = false;
                    }

                }

                case "days" -> {
                    if (timeRecord.days() > 0) {
                        if (!firstUnit) messageBuilder.append(" ");
                        messageBuilder.append(timeRecord.days()).append(" day").append(timeRecord.days() > 1 ? "s" : "");
                        firstUnit = false;
                    }

                }

                case "d" -> {
                    if (timeRecord.days() > 0) {
                        if (!firstUnit) messageBuilder.append(" ");
                        messageBuilder.append(timeRecord.days()).append("d");
                        firstUnit = false;
                    }

                }

                case "hours" -> {
                    if (timeRecord.hours() > 0) {
                        if (!firstUnit) messageBuilder.append(" ");
                        messageBuilder.append(timeRecord.hours()).append(" hour").append(timeRecord.hours() > 1 ? "s" : "");
                        firstUnit = false;
                    }

                }

                case "hr" -> {
                    if (timeRecord.hours() > 0) {
                        if (!firstUnit) messageBuilder.append(" ");
                        messageBuilder.append(timeRecord.hours()).append("hr");
                        firstUnit = false;
                    }

                }

                case "h" -> {
                    if (timeRecord.hours() > 0) {
                        if (!firstUnit) messageBuilder.append(" ");
                        messageBuilder.append(timeRecord.hours()).append("h");
                        firstUnit = false;
                    }

                }

                case "minutes" -> {
                    if (timeRecord.minutes() > 0) {
                        if (!firstUnit) messageBuilder.append(" ");
                        messageBuilder.append(timeRecord.minutes()).append(" minute").append(timeRecord.minutes() > 1 ? "s" : "");
                        firstUnit = false;
                    }

                }

                case "min" -> {
                    if (timeRecord.minutes() > 0) {
                        if (!firstUnit) messageBuilder.append(" ");
                        messageBuilder.append(timeRecord.minutes()).append("m");
                        firstUnit = false;
                    }

                }

                case "seconds" -> {
                    if (timeRecord.seconds() > 0) {
                        if (!firstUnit) messageBuilder.append(" ");
                        messageBuilder.append(timeRecord.seconds()).append(" second").append(timeRecord.seconds() > 1 ? "s" : "");
                        firstUnit = false;
                    }

                }

                case "sec" -> {
                    if (timeRecord.seconds() > 0) {
                        if (!firstUnit) messageBuilder.append(" ");
                        messageBuilder.append(timeRecord.seconds()).append(" sec");
                        firstUnit = false;
                    }

                }

                case "s" -> {
                    if (timeRecord.seconds() > 0) {
                        if (!firstUnit) messageBuilder.append(" ");
                        messageBuilder.append(timeRecord.seconds()).append("s");
                        firstUnit = false;
                    }

                }
            }
        }

        // If no units were added, return "0 seconds" or similar
        if (firstUnit) {
            return "0 seconds";
        }

        return messageBuilder.toString();
    }

    /**
     * Get the highest {@link TimeUnit} based on the {@link List} of time units as a {@link String} provided.
     * @param timeUnits The {@link List} of time units as a {@link String}.
     * @return The highest {@link TimeUnit} or null.
     */
    private @Nullable TimeUnit getHighestTimeUnit(@NonNull List<String> timeUnits) {
        TimeUnit timeUnit = null;

        for(String unit : timeUnits) {
            switch(unit.toLowerCase()) {
                case "years", "yr", "y" -> {
                    if(timeUnit != null) {
                        if(timeUnit.getPriority() < TimeUnit.YEARS.getPriority()) {
                            timeUnit = TimeUnit.YEARS;
                        }
                    } else {
                        timeUnit = TimeUnit.YEARS;
                    }

                    return timeUnit;
                }

                case "months", "mo" -> {
                    if(timeUnit != null) {
                        if(timeUnit.getPriority() < TimeUnit.MONTHS.getPriority()) {
                            timeUnit = TimeUnit.MONTHS;
                        }
                    } else {
                        timeUnit = TimeUnit.MONTHS;
                    }

                }

                case "weeks", "w" -> {
                    if(timeUnit != null) {
                        if(timeUnit.getPriority() < TimeUnit.WEEKS.getPriority()) {
                            timeUnit = TimeUnit.WEEKS;
                        }
                    } else {
                        timeUnit = TimeUnit.WEEKS;
                    }

                }

                case "days", "d" -> {
                    if(timeUnit != null) {
                        if(timeUnit.getPriority() < TimeUnit.DAYS.getPriority()) {
                            timeUnit = TimeUnit.DAYS;
                        }
                    } else {
                        timeUnit = TimeUnit.DAYS;
                    }

                }

                case "hours", "hr", "h" -> {
                    if(timeUnit != null) {
                        if(timeUnit.getPriority() < TimeUnit.HOURS.getPriority()) {
                            timeUnit = TimeUnit.HOURS;
                        }
                    } else {
                        timeUnit = TimeUnit.HOURS;
                    }

                }

                case "minutes", "min" -> {
                    if(timeUnit != null) {
                        if(timeUnit.getPriority() < TimeUnit.MINUTES.getPriority()) {
                            timeUnit = TimeUnit.MINUTES;
                        }
                    } else {
                        timeUnit = TimeUnit.MINUTES;
                    }

                }

                case "seconds", "sec", "s" -> {
                    if(timeUnit != null) {
                        if(timeUnit.getPriority() < TimeUnit.SECONDS.getPriority()) {
                            timeUnit = TimeUnit.SECONDS;
                        }
                    } else {
                        timeUnit = TimeUnit.SECONDS;
                    }

                }
            }
        }

        return timeUnit;
    }
}

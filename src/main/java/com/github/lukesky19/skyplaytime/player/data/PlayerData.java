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
package com.github.lukesky19.skyplaytime.player.data;

import com.github.lukesky19.skyplaytime.util.TimeCategory;
import org.jspecify.annotations.NonNull;

/**
 * This class stores play time data for players.
 */
public class PlayerData {
    // Player Data
    private final @NonNull String name;
    // Play Time Data
    private long sessionPlayTimeSeconds = 0;
    private long dailyPlayTimeSeconds = 0;
    private long weeklyPlayTimeSeconds = 0;
    private long monthlyPlayTimeSeconds = 0;
    private long totalPlayTimeSeconds = 0;
    private long yearlyPlayTimeSeconds = 0;
    // Leaderboard Data
    private boolean exempt = false;

    // Activity Data
    private long lastMoveTime = System.currentTimeMillis();
    private long lastActionTime = System.currentTimeMillis();
    // AFK Status
    private boolean isAFK = false;

    private boolean isErrored = false;

    /**
     * Create player data using player name provided.
     * The player will be not exempt from leaderboard reporting and all play time will start at 0.
     * @param name The name of the player.
     */
    public PlayerData(@NonNull String name) {
        this.name = name;
    }

    /**
     * Did the player data fail to load properly?
     * @return Is the player data errored?
     */
    public boolean isErrored() {
        return isErrored;
    }

    /**
     * Set whether the player data is errored or not.
     * Player data will not be saved if errored.
     * @param errored true if errored or false if not.
     */
    public void setErrored(boolean errored) {
        this.isErrored = errored;
    }

    /**
     * Adds the provided play time in seconds to all play time counters.
     * @param playTimeSeconds The play time in seconds to add. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean addPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        sessionPlayTimeSeconds += playTimeSeconds;
        dailyPlayTimeSeconds += playTimeSeconds;
        weeklyPlayTimeSeconds += playTimeSeconds;
        monthlyPlayTimeSeconds += playTimeSeconds;
        yearlyPlayTimeSeconds += playTimeSeconds;
        totalPlayTimeSeconds += playTimeSeconds;

        return true;
    }

    /**
     * Removes the provided play time in seconds from all play time counters.
     * @param playTimeSeconds The play time in seconds to remove. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean removePlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        sessionPlayTimeSeconds += playTimeSeconds;
        dailyPlayTimeSeconds += playTimeSeconds;
        weeklyPlayTimeSeconds += playTimeSeconds;
        monthlyPlayTimeSeconds += playTimeSeconds;
        totalPlayTimeSeconds += playTimeSeconds;

        if(sessionPlayTimeSeconds < 0) sessionPlayTimeSeconds = 0;
        if(dailyPlayTimeSeconds < 0) dailyPlayTimeSeconds = 0;
        if(weeklyPlayTimeSeconds < 0) weeklyPlayTimeSeconds = 0;
        if(monthlyPlayTimeSeconds < 0) monthlyPlayTimeSeconds = 0;
        if(totalPlayTimeSeconds < 0) totalPlayTimeSeconds = 0;

        return true;
    }

    /**
     * Replaces all play time counters with the provided play time in seconds.
     * @param playTimeSeconds The play time in seconds to set. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean setPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        sessionPlayTimeSeconds = playTimeSeconds;
        dailyPlayTimeSeconds = playTimeSeconds;
        weeklyPlayTimeSeconds = playTimeSeconds;
        monthlyPlayTimeSeconds = playTimeSeconds;
        totalPlayTimeSeconds = playTimeSeconds;

        return true;
    }

    /**
     * Adds the provided play time in seconds to the player's session play time counter.
     * @param playTimeSeconds The play time in seconds to add. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean addSessionPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        sessionPlayTimeSeconds += playTimeSeconds;

        return true;
    }

    /**
     * Removes the provided play time in seconds from player's session play time counter.
     * @param playTimeSeconds The play time in seconds to remove. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean removeSessionPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        sessionPlayTimeSeconds -= playTimeSeconds;

        if(sessionPlayTimeSeconds < 0) sessionPlayTimeSeconds = 0;

        return true;
    }

    /**
     * Replaces the player's session play time using the provided play time in seconds.
     * @param playTimeSeconds The play time in seconds to set. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean setSessionPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        sessionPlayTimeSeconds = playTimeSeconds;

        return true;
    }

    /**
     * Get the player's play time in seconds for their current session.
     * @return The player's session play time in seconds.
     */
    public long getSessionPlayTimeSeconds() {
        return sessionPlayTimeSeconds;
    }

    /**
     * Adds the provided play time in seconds to the player's daily play time counter.
     * @param playTimeSeconds The play time in seconds to add. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean addDailyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        dailyPlayTimeSeconds += playTimeSeconds;

        return true;
    }

    /**
     * Removes the provided play time in seconds from player's daily play time counter.
     * @param playTimeSeconds The play time in seconds to remove. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean removeDailyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        dailyPlayTimeSeconds -= playTimeSeconds;

        if(dailyPlayTimeSeconds < 0) dailyPlayTimeSeconds = 0;

        return true;
    }

    /**
     * Replaces the player's daily play time using the provided play time in seconds.
     * @param playTimeSeconds The play time in seconds to set. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean setDailyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        dailyPlayTimeSeconds = playTimeSeconds;

        return true;
    }

    /**
     * Get the player's daily play time in seconds.
     * @return The player's daily play time in seconds.
     */
    public long getDailyPlayTimeSeconds() {
        return dailyPlayTimeSeconds;
    }

    /**
     * Adds the provided play time in seconds to the player's weekly play time counter.
     * @param playTimeSeconds The play time in seconds to add. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean addWeeklyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        weeklyPlayTimeSeconds += playTimeSeconds;

        return true;
    }

    /**
     * Removes the provided play time in seconds from player's weekly play time counter.
     * @param playTimeSeconds The play time in seconds to remove. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean removeWeeklyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        weeklyPlayTimeSeconds -= playTimeSeconds;

        if(weeklyPlayTimeSeconds < 0) weeklyPlayTimeSeconds = 0;

        return true;
    }

    /**
     * Replaces the player's weekly play time using the provided play time in seconds.
     * @param playTimeSeconds The play time in seconds to set. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean setWeeklyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        weeklyPlayTimeSeconds = playTimeSeconds;

        return true;
    }

    /**
     * Get the player's weekly play time in seconds.
     * @return The player's weekly play time in seconds.
     */
    public long getWeeklyPlayTimeSeconds() {
        return weeklyPlayTimeSeconds;
    }

    /**
     * Adds the provided play time in seconds to the player's monthly play time counter.
     * @param playTimeSeconds The play time in seconds to add. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean addMonthlyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        monthlyPlayTimeSeconds += playTimeSeconds;

        return true;
    }

    /**
     * Removes the provided play time in seconds from player's monthly play time counter.
     * @param playTimeSeconds The play time in seconds to remove. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean removeMonthlyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        monthlyPlayTimeSeconds -= playTimeSeconds;

        if(monthlyPlayTimeSeconds < 0) monthlyPlayTimeSeconds = 0;

        return true;
    }

    /**
     * Replaces the player's monthly play time using the provided play time in seconds.
     * @param playTimeSeconds The play time in seconds to set. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean setMonthlyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        monthlyPlayTimeSeconds = playTimeSeconds;

        return true;
    }

    /**
     * Get the player's monthly play time in seconds.
     * @return The player's monthly play time in seconds.
     */
    public long getMonthlyPlayTimeSeconds() {
        return monthlyPlayTimeSeconds;
    }

    /**
     * Adds the provided play time in seconds to the player's yearly play time counter.
     * @param playTimeSeconds The play time in seconds to add. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean addYearlyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        yearlyPlayTimeSeconds += playTimeSeconds;

        return true;
    }

    /**
     * Removes the provided play time in seconds from player's yearly play time counter.
     * @param playTimeSeconds The play time in seconds to remove. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean removeYearlyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        yearlyPlayTimeSeconds -= playTimeSeconds;

        if(yearlyPlayTimeSeconds < 0) yearlyPlayTimeSeconds = 0;

        return true;
    }

    /**
     * Replaces the player's total play time using the provided play time in seconds.
     * @param playTimeSeconds The play time in seconds to set. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean setYearlyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        yearlyPlayTimeSeconds = playTimeSeconds;

        return true;
    }

    /**
     * Get the player's yearly play time in seconds.
     * @return The player's yearly play time in seconds.
     */
    public long getYearlyPlayTimeSeconds() {
        return yearlyPlayTimeSeconds;
    }

    /**
     * Adds the provided play time in seconds to the player's total play time counter.
     * @param playTimeSeconds The play time in seconds to add. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean addTotalPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        totalPlayTimeSeconds += playTimeSeconds;

        return true;
    }

    /**
     * Removes the provided play time in seconds from player's total play time counter.
     * @param playTimeSeconds The play time in seconds to remove. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean removeTotalPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        totalPlayTimeSeconds -= playTimeSeconds;

        if(totalPlayTimeSeconds < 0) totalPlayTimeSeconds = 0;

        return true;
    }

    /**
     * Replaces the player's total play time using the provided play time in seconds.
     * @param playTimeSeconds The play time in seconds to set. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean setTotalPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) return false;

        totalPlayTimeSeconds = playTimeSeconds;

        return true;
    }

    /**
     * Get the player's total play time in seconds.
     * @return The player's total play time in seconds.
     */
    public long getTotalPlayTimeSeconds() {
        return totalPlayTimeSeconds;
    }

    /**
     * Add the play time in seconds for the {@link TimeCategory} provided.
     * @param timeCategory The {@link TimeCategory} to add play time for.
     * @param seconds The time in seconds to add.
     * @return true if successful, false if not.
     */
    public boolean addPlayTime(@NonNull TimeCategory timeCategory, long seconds) {
        if(seconds < 0) return false;

        return switch(timeCategory) {
            case SESSION -> addSessionPlayTime(seconds);
            case DAILY -> addDailyPlayTime(seconds);
            case WEEKLY -> addWeeklyPlayTime(seconds);
            case MONTHLY -> addMonthlyPlayTime(seconds);
            case YEARLY -> addYearlyPlayTime(seconds);
            case TOTAL -> addTotalPlayTime(seconds);
            case ALL -> addPlayTime(seconds);
        };
    }

    /**
     * Remove the play time in seconds for the {@link TimeCategory} provided.
     * @param timeCategory The {@link TimeCategory} to remove play time for.
     * @param seconds The time in seconds to remove.
     * @return true if successful, false if not.
     */
    public boolean removePlayTime(@NonNull TimeCategory timeCategory, long seconds) {
        if(seconds < 0) return false;

        return switch(timeCategory) {
            case SESSION -> removeSessionPlayTime(seconds);
            case DAILY -> removeDailyPlayTime(seconds);
            case WEEKLY -> removeWeeklyPlayTime(seconds);
            case MONTHLY -> removeMonthlyPlayTime(seconds);
            case YEARLY -> removeYearlyPlayTime(seconds);
            case TOTAL -> removeTotalPlayTime(seconds);
            case ALL -> removePlayTime(seconds);
        };
    }

    /**
     * Set the play time in seconds for the {@link TimeCategory} provided.
     * @param timeCategory The {@link TimeCategory} to set play time for.
     * @param seconds The time in seconds to set.
     * @return true if successful, false if not.
     */
    public boolean setPlayTime(@NonNull TimeCategory timeCategory, long seconds) {
        if(seconds < 0) return false;

        return switch(timeCategory) {
            case SESSION -> setSessionPlayTime(seconds);
            case DAILY -> setDailyPlayTime(seconds);
            case WEEKLY -> setWeeklyPlayTime(seconds);
            case MONTHLY -> setMonthlyPlayTime(seconds);
            case YEARLY -> setYearlyPlayTime(seconds);
            case TOTAL -> setTotalPlayTime(seconds);
            case ALL -> setPlayTime(seconds);
        };
    }

    /**
     * Get the play time in seconds for the {@link TimeCategory} provided.
     * {@link TimeCategory#ALL} will return the total play time category.
     * @param timeCategory The {@link TimeCategory} to get play time for.
     * @return The play time in seconds for the {@link TimeCategory} provided.
     */
    public long getPlayTime(@NonNull TimeCategory timeCategory) {
        return switch (timeCategory) {
            case SESSION -> getSessionPlayTimeSeconds();
            case DAILY -> getDailyPlayTimeSeconds();
            case WEEKLY -> getWeeklyPlayTimeSeconds();
            case MONTHLY -> getMonthlyPlayTimeSeconds();
            case YEARLY -> getYearlyPlayTimeSeconds();
            case TOTAL, ALL -> getTotalPlayTimeSeconds();
        };
    }

    /**
     * Sets whether the player is exempt from leaderboard reporting or not.
     * @param exempt Is the player exempt from leaderboard reporting?
     */
    public void setExempt(boolean exempt) {
        this.exempt = exempt;
    }

    /**
     * Is the player exempt from leaderboard reporting?
     * @return true if exempt, false if not.
     */
    public boolean isExempt() {
        return exempt;
    }

    /**
     * Gets the player's last move timestamp.
     * @return The player's last move timestamp.
     */
    public long getLastMoveTime() {
        return lastMoveTime;
    }

    /**
     * Sets the player's last move timestamp.
     * @param lastMoveTime The timestamp of {@link System#currentTimeMillis()} of when the player last moved their character.
     */
    public void setLastMoveTime(long lastMoveTime) {
        this.lastMoveTime = lastMoveTime;
    }

    /**
     * Gets the player's last longeract timestamp.
     * @return The player's last longeract timestamp.
     */
    public long getLastActionTime() {
        return lastActionTime;
    }

    /**
     * Sets the player's last longeract timestamp.
     * @param lastActionTime The timestamp of {@link System#currentTimeMillis()} of when the player last longeracted with something.
     */
    public void setLastActionTime(long lastActionTime) {
        this.lastActionTime = lastActionTime;
    }

    /**
     * Sets whether the player is marked as afk or not.
     * @param status true if afk, false if not.
     */
    public void setAFK(boolean status) {
        isAFK = status;
    }

    /**
     * Is the player currently AFK or not?
     * @return true if AFK, false if not.
     */
    public boolean isAFK() {
        return isAFK;
    }

    /**
     * Get the name of the player this data is associated with.
     * @return A {@link String} containing the player's name.
     */
    public @NonNull String getName() {
        return name;
    }
}

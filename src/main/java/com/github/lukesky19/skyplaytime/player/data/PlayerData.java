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
import org.jetbrains.annotations.NotNull;

/**
 * This class stores play time data for players.
 */
public class PlayerData {
    // Player Data
    private final @NotNull String name;
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

    /**
     * Create player data using player name provided.
     * The player will be not exempt from leaderboard reporting and all play time will start at 0.
     * @param name The name of the player.
     */
    public PlayerData(@NotNull String name) {
        this.name = name;
    }

    /**
     * Adds the provided play time in seconds to all play time counters.
     * @param playTimeSeconds The play time in seconds to add. Must be a positive number.
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void addPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        sessionPlayTimeSeconds += playTimeSeconds;
        dailyPlayTimeSeconds += playTimeSeconds;
        weeklyPlayTimeSeconds += playTimeSeconds;
        monthlyPlayTimeSeconds += playTimeSeconds;
        yearlyPlayTimeSeconds += playTimeSeconds;
        totalPlayTimeSeconds += playTimeSeconds;
    }

    /**
     * Removes the provided play time in seconds from all play time counters.
     * @param playTimeSeconds The play time in seconds to remove. Must be a positive number.
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void removePlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

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
    }

    /**
     * Replaces all play time counters with the provided play time in seconds.
     * @param playTimeSeconds The play time in seconds to set. Must be a positive number.
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void setPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        sessionPlayTimeSeconds = playTimeSeconds;
        dailyPlayTimeSeconds = playTimeSeconds;
        weeklyPlayTimeSeconds = playTimeSeconds;
        monthlyPlayTimeSeconds = playTimeSeconds;
        totalPlayTimeSeconds = playTimeSeconds;
    }

    /**
     * Adds the provided play time in seconds to the player's session play time counter.
     * @param playTimeSeconds The play time in seconds to add. Must be a positive number.
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void addSessionPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        sessionPlayTimeSeconds += playTimeSeconds;
    }

    /**
     * Removes the provided play time in seconds from player's session play time counter.
     * @param playTimeSeconds The play time in seconds to remove. Must be a positive number.
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void removeSessionPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        sessionPlayTimeSeconds -= playTimeSeconds;

        if(sessionPlayTimeSeconds < 0) sessionPlayTimeSeconds = 0;
    }

    /**
     * Replaces the player's session play time using the provided play time in seconds.
     * @param playTimeSeconds The play time in seconds to set. Must be a positive number.
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void setSessionPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        sessionPlayTimeSeconds = playTimeSeconds;
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
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void addDailyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        dailyPlayTimeSeconds += playTimeSeconds;
    }

    /**
     * Removes the provided play time in seconds from player's daily play time counter.
     * @param playTimeSeconds The play time in seconds to remove. Must be a positive number.
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void removeDailyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        dailyPlayTimeSeconds -= playTimeSeconds;

        if(dailyPlayTimeSeconds < 0) dailyPlayTimeSeconds = 0;
    }

    /**
     * Replaces the player's daily play time using the provided play time in seconds.
     * @param playTimeSeconds The play time in seconds to set. Must be a positive number.
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void setDailyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        dailyPlayTimeSeconds = playTimeSeconds;
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
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void addWeeklyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        weeklyPlayTimeSeconds += playTimeSeconds;
    }

    /**
     * Removes the provided play time in seconds from player's weekly play time counter.
     * @param playTimeSeconds The play time in seconds to remove. Must be a positive number.
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void removeWeeklyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        weeklyPlayTimeSeconds -= playTimeSeconds;

        if(weeklyPlayTimeSeconds < 0) weeklyPlayTimeSeconds = 0;
    }

    /**
     * Replaces the player's weekly play time using the provided play time in seconds.
     * @param playTimeSeconds The play time in seconds to set. Must be a positive number.
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void setWeeklyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        weeklyPlayTimeSeconds = playTimeSeconds;
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
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void addMonthlyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        monthlyPlayTimeSeconds += playTimeSeconds;
    }

    /**
     * Removes the provided play time in seconds from player's monthly play time counter.
     * @param playTimeSeconds The play time in seconds to remove. Must be a positive number.
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void removeMonthlyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        monthlyPlayTimeSeconds -= playTimeSeconds;

        if(monthlyPlayTimeSeconds < 0) monthlyPlayTimeSeconds = 0;
    }

    /**
     * Replaces the player's monthly play time using the provided play time in seconds.
     * @param playTimeSeconds The play time in seconds to set. Must be a positive number.
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void setMonthlyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        monthlyPlayTimeSeconds = playTimeSeconds;
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
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void addYearlyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        yearlyPlayTimeSeconds += playTimeSeconds;
    }

    /**
     * Removes the provided play time in seconds from player's yearly play time counter.
     * @param playTimeSeconds The play time in seconds to remove. Must be a positive number.
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void removeYearlyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        yearlyPlayTimeSeconds -= playTimeSeconds;

        if(yearlyPlayTimeSeconds < 0) yearlyPlayTimeSeconds = 0;
    }

    /**
     * Replaces the player's total play time using the provided play time in seconds.
     * @param playTimeSeconds The play time in seconds to set. Must be a positive number.
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void setYearlyPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        yearlyPlayTimeSeconds = playTimeSeconds;
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
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void addTotalPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        totalPlayTimeSeconds += playTimeSeconds;
    }

    /**
     * Removes the provided play time in seconds from player's total play time counter.
     * @param playTimeSeconds The play time in seconds to remove. Must be a positive number.
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void removeTotalPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        totalPlayTimeSeconds -= playTimeSeconds;

        if(totalPlayTimeSeconds < 0) totalPlayTimeSeconds = 0;
    }

    /**
     * Replaces the player's total play time using the provided play time in seconds.
     * @param playTimeSeconds The play time in seconds to set. Must be a positive number.
     * @throws RuntimeException if the play time provided is less than 0.
     */
    public void setTotalPlayTime(long playTimeSeconds) {
        if(playTimeSeconds < 0) throw new RuntimeException("Play time must be a positive number.");

        totalPlayTimeSeconds = playTimeSeconds;
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
     */
    public void addPlayTime(@NotNull TimeCategory timeCategory, long seconds) {
        if(seconds < 0) throw new RuntimeException("Play time must be a positive number.");

        switch(timeCategory) {
            case SESSION -> addSessionPlayTime(seconds);
            case DAILY -> addDailyPlayTime(seconds);
            case WEEKLY -> addWeeklyPlayTime(seconds);
            case MONTHLY -> addMonthlyPlayTime(seconds);
            case YEARLY -> addYearlyPlayTime(seconds);
            case TOTAL -> addTotalPlayTime(seconds);
            case ALL -> addPlayTime(seconds);
        }
    }

    /**
     * Remove the play time in seconds for the {@link TimeCategory} provided.
     * @param timeCategory The {@link TimeCategory} to remove play time for.
     * @param seconds The time in seconds to remove.
     */
    public void removePlayTime(@NotNull TimeCategory timeCategory, long seconds) {
        if(seconds < 0) throw new RuntimeException("Play time must be a positive number.");

        switch(timeCategory) {
            case SESSION -> removeSessionPlayTime(seconds);
            case DAILY -> removeDailyPlayTime(seconds);
            case WEEKLY -> removeWeeklyPlayTime(seconds);
            case MONTHLY -> removeMonthlyPlayTime(seconds);
            case YEARLY -> removeYearlyPlayTime(seconds);
            case TOTAL -> removeTotalPlayTime(seconds);
            case ALL -> removePlayTime(seconds);
        }
    }

    /**
     * Set the play time in seconds for the {@link TimeCategory} provided.
     * @param timeCategory The {@link TimeCategory} to set play time for.
     * @param seconds The time in seconds to set.
     */
    public void setPlayTime(@NotNull TimeCategory timeCategory, long seconds) {
        if(seconds < 0) throw new RuntimeException("Play time must be a positive number.");

        switch(timeCategory) {
            case SESSION -> setSessionPlayTime(seconds);
            case DAILY -> setDailyPlayTime(seconds);
            case WEEKLY -> setWeeklyPlayTime(seconds);
            case MONTHLY -> setMonthlyPlayTime(seconds);
            case YEARLY -> setYearlyPlayTime(seconds);
            case TOTAL -> setTotalPlayTime(seconds);
            case ALL -> setPlayTime(seconds);
        }
    }

    /**
     * Get the play time in seconds for the {@link TimeCategory} provided.
     * {@link TimeCategory#ALL} will return the total play time category.
     * @param timeCategory The {@link TimeCategory} to get play time for.
     * @return The play time in seconds for the {@link TimeCategory} provided.
     */
    public long getPlayTime(@NotNull TimeCategory timeCategory) {
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
    public @NotNull String getName() {
        return name;
    }
}

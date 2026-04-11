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
package com.github.lukesky19.skyplaytime;

import com.github.lukesky19.skyplaytime.player.manager.AFKManager;
import com.github.lukesky19.skyplaytime.leaderboard.manager.LeaderboardManager;
import com.github.lukesky19.skyplaytime.player.manager.TimeManager;
import com.github.lukesky19.skyplaytime.util.TimeCategory;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

/**
 * This class allows other plugins to get the play time of players.
 */
@SuppressWarnings("unused")
public class SkyPlayTimeAPI {
    private final @NonNull TimeManager timeManager;
    private final @NonNull AFKManager afkManager;
    private final @NonNull LeaderboardManager leaderboardManager;

    /**
     * Constructor
     * @param timeManager A {@link TimeManager} instance.
     * @param afkManager A {@link AFKManager} instance.
     * @param leaderboardManager A {@link LeaderboardManager} instance.
     */
    public SkyPlayTimeAPI(@NonNull TimeManager timeManager, @NonNull AFKManager afkManager, @NonNull LeaderboardManager leaderboardManager) {
        this.timeManager = timeManager;
        this.afkManager = afkManager;
        this.leaderboardManager = leaderboardManager;
    }

    /**
     * Checks if the player is afk for the given player.
     * @param player The player of the player.
     * @return true if afk, false if not.
     */
    public boolean isPlayerAfk(@NonNull Player player) {
        return afkManager.isPlayerAFK(player);
    }

    /**
     * Get the player's play time for their current session in seconds.
     * @param player The {@link Player}.
     * @return The player's play time for their current session in seconds.
     */
    public long getSessionPlayTimeSeconds(@NonNull Player player) {
        return timeManager.getPlayTimeSeconds(player, TimeCategory.SESSION);
    }

    /**
     * Adds the provided play time to the player's session play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The session play time to add in seconds.
     * @return true if successful, false if not.
     */
    public boolean addSessionPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        return timeManager.addPlayTimeSeconds(player, TimeCategory.SESSION, playTimeSeconds);
    }

    /**
     * Removes the provided play time from the player's session play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The session play time to remove in seconds.
     * @return true if successful, false if not.
     */
    public boolean removeSessionPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        return timeManager.removePlayTimeSeconds(player, TimeCategory.SESSION, playTimeSeconds);
    }

    /**
     * Sets the player's session play time to the provided play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The session play time to set in seconds.
     * @return true if successful, false if not.
     */
    public boolean setSessionPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        return timeManager.setPlayTimeSeconds(player, TimeCategory.SESSION, playTimeSeconds);
    }

    /**
     * Get the player's daily play time in seconds
     * @param player The {@link Player}.
     * @return The player's daily play time in seconds
     */
    public long getDailyPlayTimeSeconds(@NonNull Player player) {
        return timeManager.getPlayTimeSeconds(player, TimeCategory.DAILY);
    }

    /**
     * Adds the provided play time to the player's daily play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The daily play time to add in seconds.
     */
    public void addDailyPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        timeManager.addPlayTimeSeconds(player, TimeCategory.DAILY, playTimeSeconds);
    }

    /**
     * Removes the provided play time from the player's daily play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The daily play time to remove in seconds.
     * @return true if successful, false if not.
     */
    public boolean removeDailyPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        return timeManager.removePlayTimeSeconds(player, TimeCategory.DAILY, playTimeSeconds);
    }

    /**
     * Sets the player's daily play time to the provided play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The daily play time to set in seconds.
     * @return true if successful, false if not.
     */
    public boolean setDailyPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        return timeManager.setPlayTimeSeconds(player, TimeCategory.DAILY, playTimeSeconds);
    }

    /**
     * Get the player's weekly play time in seconds
     * @param player The {@link Player}.
     * @return The player's weekly play time in seconds
     */
    public long getWeeklyPlayTimeSeconds(@NonNull Player player) {
        return timeManager.getPlayTimeSeconds(player, TimeCategory.WEEKLY);
    }

    /**
     * Adds the provided play time to the player's weekly play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The weekly play time to add in seconds.
     * @return true if successful, false if not.
     */
    public boolean addWeeklyPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        return timeManager.addPlayTimeSeconds(player, TimeCategory.WEEKLY, playTimeSeconds);
    }

    /**
     * Removes the provided play time from the player's weekly play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The weekly play time to remove in seconds.
     * @return true if successful, false if not.
     */
    public boolean removeWeeklyPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        return timeManager.removePlayTimeSeconds(player, TimeCategory.WEEKLY, playTimeSeconds);
    }

    /**
     * Sets the player's weekly play time to the provided play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The weekly play time to set in seconds.
     * @return true if successful, false if not.
     */
    public boolean setWeeklyPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        return timeManager.setPlayTimeSeconds(player, TimeCategory.WEEKLY, playTimeSeconds);
    }

    /**
     * Get the player's monthly play time in seconds
     * @param player The {@link Player}.
     * @return The player's monthly play time in seconds
     */
    public long getMonthlyPlayTimeSeconds(@NonNull Player player) {
        return timeManager.getPlayTimeSeconds(player, TimeCategory.MONTHLY);
    }

    /**
     * Adds the provided play time to the player's monthly play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The monthly play time to add in seconds.
     * @return true if successful, false if not.
     */
    public boolean addMonthlyPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        return timeManager.addPlayTimeSeconds(player, TimeCategory.MONTHLY, playTimeSeconds);
    }

    /**
     * Removes the provided play time from the player's monthly play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The monthly play time to remove in seconds.
     * @return true if successful, false if not.
     */
    public boolean removeMonthlyPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        return timeManager.removePlayTimeSeconds(player, TimeCategory.MONTHLY, playTimeSeconds);
    }

    /**
     * Sets the player's monthly play time to the provided play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The monthly play time to set in seconds.
     * @return true if successful, false if not.
     */
    public boolean setMonthlyPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        return timeManager.setPlayTimeSeconds(player, TimeCategory.MONTHLY, playTimeSeconds);
    }

    /**
     * Get the player's yearly play time in seconds
     * @param player The {@link Player}.
     * @return The player's yearly play time in seconds
     */
    public long getYearlyPlayTimeSeconds(@NonNull Player player) {
        return timeManager.getPlayTimeSeconds(player, TimeCategory.YEARLY);
    }

    /**
     * Adds the provided play time to the player's yearly play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The yearly play time to add in seconds.
     * @return true if successful, false if not.
     */
    public boolean addYearlyPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        return timeManager.addPlayTimeSeconds(player, TimeCategory.YEARLY, playTimeSeconds);
    }

    /**
     * Removes the provided play time from the player's yearly play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The yearly play time to remove in seconds.
     * @return true if successful, false if not.
     */
    public boolean removeYearlyPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        return timeManager.removePlayTimeSeconds(player, TimeCategory.YEARLY, playTimeSeconds);
    }

    /**
     * Sets the player's yearly play time to the provided play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The yearly play time to set in seconds.
     * @return true if successful, false if not.
     */
    public boolean setYearlyPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        return timeManager.setPlayTimeSeconds(player, TimeCategory.YEARLY, playTimeSeconds);
    }

    /**
     * Get the player's total play time in seconds
     * @param player The {@link Player}.
     * @return The player's total play time in seconds
     */
    public long getTotalPlayTimeSeconds(@NonNull Player player) {
        return timeManager.getPlayTimeSeconds(player, TimeCategory.TOTAL);
    }

    /**
     * Adds the provided play time to the player's total play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The total play time to add in seconds.
     */
    public void addTotalPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        timeManager.addPlayTimeSeconds(player, TimeCategory.TOTAL, playTimeSeconds);
    }

    /**
     * Removes the provided play time from the player's total play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The total play time to remove in seconds.
     * @return true if successful, false if not.
     */
    public boolean removeTotalPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        return timeManager.removePlayTimeSeconds(player, TimeCategory.TOTAL, playTimeSeconds);
    }

    /**
     * Sets the player's total play time to the provided play time.
     * @param player The {@link Player}.
     * @param playTimeSeconds The total play time to set in seconds.
     * @return true if successful, false if not.
     */
    public boolean setTotalPlayTimeSeconds(@NonNull Player player, long playTimeSeconds) {
        return timeManager.setPlayTimeSeconds(player, TimeCategory.TOTAL, playTimeSeconds);
    }

    /**
     * Reset the player's play time according to the provided boolean options.
     * @param player The {@link Player}.
     * @param session Should session play time be reset?
     * @param daily Should daily play time be reset?
     * @param weekly Should weekly play time be reset?
     * @param monthly Should monthly play time be reset?
     * @param yearly Should yearly play time be reset?
     * @param total Should total play time be reset?
     * @return true if succeeds, false if not
     */
    public boolean resetPlayTime(@NonNull Player player, boolean session, boolean daily, boolean weekly, boolean monthly, boolean yearly, boolean total) {
        return timeManager.resetPlayTime(player, session, daily, weekly, monthly, yearly, total);
    }

    /**
     * Reset all player's play time for according to the provided boolean options.
     * @param session Should session play time be reset?
     * @param daily Should daily play time be reset?
     * @param weekly Should weekly play time be reset?
     * @param monthly Should monthly play time be reset?
     * @param yearly Should yearly play time be reset?
     * @param total Should total play time be reset?
     * @return A {@link CompletableFuture} of type {@link Boolean}. true if successful, false if not.
     */
    public @NonNull CompletableFuture<@NonNull Boolean> resetPlayTime(boolean session, boolean daily, boolean weekly, boolean monthly, boolean yearly, boolean total) {
        return timeManager.resetPlayTime(session, daily, weekly, monthly, yearly, total);
    }

    /**
     * Resets all play time for a specific player.
     * Any errors will abort the reset process and errors will be logged to console.
     * @param player The {@link Player}.
     * @return true if successful, false if not.
     */
    public boolean resetAllPlayTime(@NonNull Player player) {
        return timeManager.resetPlayTime(player, true, true, true, true, true, true);
    }

    /**
     * Resets all play time for all players.
     * If configured in SkyPlayTime's settings.yml, a backup taken and leaderboard snapshot will be saved beforehand.
     * Any errors will abort the reset process and errors will be logged to console.
     * @return A {@link CompletableFuture} containing a {@link Boolean}. true if successful, false if not.
     */
    public @NonNull CompletableFuture<@NonNull Boolean> resetAllPlayTime() {
        return timeManager.resetPlayTime(true, true, true, true, true, true);
    }

    /**
     * Tells the plugin to not report this player on any leaderboard reporting. Time is still tracked though.
     * @param player The {@link Player}.
     * @return true if successful, false if not.
     */
    public boolean markPlayerExempt(@NonNull Player player) {
        return leaderboardManager.markPlayerExempt(player);
    }

    /**
     * Tells the plugin to once again report this player on any leaderboard reporting.
     * @param player The {@link Player}.
     * @return true if successful, false if not.
     */
    public boolean markPlayerNotExempt(@NonNull Player player) {
        return leaderboardManager.markPlayerNotExempt(player);
    }
}

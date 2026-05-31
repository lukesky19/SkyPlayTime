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

import com.github.lukesky19.skyplaytime.util.enums.TimeCategory;
import com.github.lukesky19.skyplaytime.util.location.LocationSnapshot;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class stores play time data for players.
 */
public class PlayerData {
    // Player Data
    private final @NonNull String name;

    // Play Time Data
    private final @NonNull Time session = new Time();
    private final @NonNull Time daily = new Time();
    private final @NonNull Time weekly = new Time();
    private final @NonNull Time monthly = new Time();
    private final @NonNull Time yearly = new Time();
    private final @NonNull Time total = new Time();

    // Activity Timestamps
    private long gracePeriod = 0;
    private long lastMove = 0;
    private long lastInteract = 0;
    private long lastBlockBreak = 0;
    private long lastBlockPlace = 0;
    private long lastRodCast = 0;
    private long lastRodCatch = 0;
    private long lastRodReel = 0;

    // Player locations
    private final @NonNull List<LocationSnapshot> locationList = new ArrayList<>();

    // AFK Status
    private boolean isAFK = false;
    private boolean manualAFK = false;

    // Leaderboard Data
    private boolean exempt = false;

    /**
     * Create player data using player name provided.
     * The player will be not exempt from leaderboard reporting and all play time will start at 0.
     * @param name The name of the player.
     */
    public PlayerData(@NonNull String name) {
        this.name = name;
    }

    /**
     * Create player data using player name, play time, and exemption status provided.
     * @param name The name of the player.
     * @param sessionPlayTimeSeconds The player's session playtime in seconds.
     * @param dailyPlayTimeSeconds The player's daily playtime in seconds.
     * @param weeklyPlayTimeSeconds The player's weekly playtime in seconds.
     * @param monthlyPlayTimeSeconds The player's monthly playtime in seconds.
     * @param yearlyPlayTimeSeconds The player's yearly playtime in seconds.
     * @param totalPlayTimeSeconds The player's total playtime in seconds.
     * @param exempt Is the player exempt from leaderboard reporting?
     */
    public PlayerData(
            @NonNull String name,
            long sessionPlayTimeSeconds,
            long dailyPlayTimeSeconds,
            long weeklyPlayTimeSeconds,
            long monthlyPlayTimeSeconds,
            long yearlyPlayTimeSeconds,
            long totalPlayTimeSeconds,
            boolean exempt) {
        this.name = name;

        this.session.add(sessionPlayTimeSeconds);
        this.daily.add(dailyPlayTimeSeconds);
        this.weekly.add(weeklyPlayTimeSeconds);
        this.monthly.add(monthlyPlayTimeSeconds);
        this.yearly.add(yearlyPlayTimeSeconds);
        this.total.add(totalPlayTimeSeconds);

        this.exempt = exempt;
    }

    /**
     * Create player data using player name, play time, and exemption status provided.
     * @param name The name of the player.
     * @param sessionPlayTimeSeconds The player's session playtime in seconds.
     * @param dailyPlayTimeSeconds The player's daily playtime in seconds.
     * @param weeklyPlayTimeSeconds The player's weekly playtime in seconds.
     * @param monthlyPlayTimeSeconds The player's monthly playtime in seconds.
     * @param yearlyPlayTimeSeconds The player's yearly playtime in seconds.
     * @param totalPlayTimeSeconds The player's total playtime in seconds.
     * @param exempt Is the player exempt from leaderboard reporting?
     * @param isAFK Is the player afk or not?
     */
    public PlayerData(
            @NonNull String name,
            long sessionPlayTimeSeconds,
            long dailyPlayTimeSeconds,
            long weeklyPlayTimeSeconds,
            long monthlyPlayTimeSeconds,
            long yearlyPlayTimeSeconds,
            long totalPlayTimeSeconds,
            boolean exempt,
            boolean isAFK) {
        this.name = name;

        this.session.add(sessionPlayTimeSeconds);
        this.daily.add(dailyPlayTimeSeconds);
        this.weekly.add(weeklyPlayTimeSeconds);
        this.monthly.add(monthlyPlayTimeSeconds);
        this.yearly.add(yearlyPlayTimeSeconds);
        this.total.add(totalPlayTimeSeconds);

        this.exempt = exempt;
        this.isAFK = isAFK;
    }

    /**
     * Constructor
     * @param name The name of the player.
     * @param sessionPlayTimeSeconds The player's session playtime in seconds.
     * @param dailyPlayTimeSeconds The player's daily playtime in seconds.
     * @param weeklyPlayTimeSeconds The player's weekly playtime in seconds.
     * @param monthlyPlayTimeSeconds The player's monthly playtime in seconds.
     * @param yearlyPlayTimeSeconds The player's yearly playtime in seconds.
     * @param totalPlayTimeSeconds The player's total playtime in seconds.
     * @param lastMove The timestamp of when the player last moved.
     * @param lastBlockBreak The timestamp of when the player last broke a block.
     * @param lastBlockPlace The timestamp of when the player last placed a block.
     * @param lastRodCast  The timestamp of when the player last cast a fishing rod.
     * @param lastRodCatch The timestamp of when the player last caught something with a fishing rod.
     * @param lastRodReel The timestamp of when the player last reeled a fishing rod.
     * @param isAFK Is the player afk or not?isAFK
     * @param exempt Is the player exempt from leaderboard reporting?
     */
    public PlayerData(
            @NonNull String name,
            long sessionPlayTimeSeconds,
            long dailyPlayTimeSeconds,
            long weeklyPlayTimeSeconds,
            long monthlyPlayTimeSeconds,
            long yearlyPlayTimeSeconds,
            long totalPlayTimeSeconds,
            long lastMove,
            long lastBlockBreak,
            long lastBlockPlace,
            long lastRodCast,
            long lastRodCatch,
            long lastRodReel,
            boolean isAFK,
            boolean exempt) {
        this.name = name;

        this.session.add(sessionPlayTimeSeconds);
        this.daily.add(dailyPlayTimeSeconds);
        this.weekly.add(weeklyPlayTimeSeconds);
        this.monthly.add(monthlyPlayTimeSeconds);
        this.yearly.add(yearlyPlayTimeSeconds);
        this.total.add(totalPlayTimeSeconds);

        this.lastMove = lastMove;
        this.lastBlockBreak = lastBlockBreak;
        this.lastBlockPlace = lastBlockPlace;
        this.lastRodCast = lastRodCast;
        this.lastRodCatch = lastRodCatch;
        this.lastRodReel = lastRodReel;

        this.isAFK = isAFK;
        this.exempt = exempt;
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
            case SESSION -> session.add(seconds);
            case DAILY -> daily.add(seconds);
            case WEEKLY -> weekly.add(seconds);
            case MONTHLY -> monthly.add(seconds);
            case YEARLY -> yearly.add(seconds);
            case TOTAL -> total.add(seconds);
            case ALL -> this.session.add(seconds)
                    && this.daily.add(seconds)
                    && this.weekly.add(seconds)
                    && this.monthly.add(seconds)
                    && this.yearly.add(seconds)
                    && this.total.add(seconds);
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
            case SESSION -> session.remove(seconds);
            case DAILY -> daily.remove(seconds);
            case WEEKLY -> weekly.remove(seconds);
            case MONTHLY -> monthly.remove(seconds);
            case YEARLY -> yearly.remove(seconds);
            case TOTAL -> total.remove(seconds);
            case ALL -> this.session.remove(seconds)
                    && this.daily.remove(seconds)
                    && this.weekly.remove(seconds)
                    && this.monthly.remove(seconds)
                    && this.yearly.remove(seconds)
                    && this.total.remove(seconds);
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
            case SESSION -> session.set(seconds);
            case DAILY -> daily.set(seconds);
            case WEEKLY -> weekly.set(seconds);
            case MONTHLY -> monthly.set(seconds);
            case YEARLY -> yearly.set(seconds);
            case TOTAL -> total.set(seconds);
            case ALL -> this.session.set(seconds)
                    && this.daily.set(seconds)
                    && this.weekly.set(seconds)
                    && this.monthly.set(seconds)
                    && this.yearly.set(seconds)
                    && this.total.set(seconds);
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
            case SESSION -> session.get();
            case DAILY -> daily.get();
            case WEEKLY -> weekly.get();
            case MONTHLY -> monthly.get();
            case YEARLY -> yearly.get();
            case TOTAL, ALL -> total.get();
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
     * Get the timestamp when the player's grace period expires or expired at.
     * @return The timestamp when player's grace period expires or expired at.
     */
    public long getGracePeriod() {
        return gracePeriod;
    }

    /**
     * Set the timestamp for when the player should next be checked for being marked as AFK.
     * @param gracePeriod The timestamp for when the player should next be checked for being marked as AFK.
     */
    public void setGracePeriod(long gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    /**
     * Is the player's grace period active and should be ignored when processing algorithms.
     * @return true if the player's grace period is active otherwise false.
     */
    public boolean isGracePeriodActive() {
        return gracePeriod >= System.currentTimeMillis();
    }

    /**
     * Gets the player's last move timestamp.
     * @return The player's last move timestamp.
     */
    public long getLastMove() {
        return lastMove;
    }

    /**
     * Sets the player's last move timestamp.
     * @param lastMoveTime The timestamp from {@link System#currentTimeMillis()}.
     */
    public void setLastMoveTime(long lastMoveTime) {
        this.lastMove = lastMoveTime;
    }

    /**
     * Gets the player's last interact timestamp.
     * @return The player's last interact timestamp.
     */
    public long getLastInteract() {
        return lastInteract;
    }

    /**
     * Sets the player's last interact timestamp.
     * @param lastInteract The timestamp from {@link System#currentTimeMillis()}.
     */
    public void setLastInteractTime(long lastInteract) {
        this.lastInteract = lastInteract;
    }

    /**
     * Gets the player's last block break timestamp.
     * @return The player's last block break timestamp.
     */
    public long getLastBlockBreak() {
        return lastBlockBreak;
    }

    /**
     * Sets the player's last block break timestamp.
     * @param lastBlockBreak The timestamp from {@link System#currentTimeMillis()}.
     */
    public void setLastBlockBreak(long lastBlockBreak) {
        this.lastBlockBreak = lastBlockBreak;
    }

    /**
     * Gets the player's last block place timestamp.
     * @return The player's last block place timestamp.
     */
    public long getLastBlockPlace() {
        return lastBlockPlace;
    }

    /**
     * Sets the player's last block place timestamp.
     * @param lastBlockPlace The timestamp from {@link System#currentTimeMillis()}.
     */
    public void setLastBlockPlace(long lastBlockPlace) {
        this.lastBlockPlace = lastBlockPlace;
    }

    /**
     * Gets the player's last fishing rod cast timestamp.
     * @return The player's last fishing rod cast timestamp.
     */
    public long getLastRodCast() {
        return lastRodCast;
    }

    /**
     * Sets the player's last fishing rod cast timestamp.
     * @param lastRodCast The timestamp from {@link System#currentTimeMillis()}.
     */
    public void setLastRodCast(long lastRodCast) {
        this.lastRodCast = lastRodCast;
    }

    /**
     * Gets the player's last fishing rod catch timestamp.
     * @return The player's last fishing rod catch timestamp.
     */
    public long getLastRodCatch() {
        return lastRodCatch;
    }

    /**
     * Sets the player's last fishing rod catch timestamp.
     * @param lastRodCatch The timestamp from {@link System#currentTimeMillis()}.
     */
    public void setLastRodCatch(long lastRodCatch) {
        this.lastRodCatch = lastRodCatch;
    }

    /**
     * Gets the player's last fishing rod reel timestamp.
     * @return The player's last fishing rod reel timestamp.
     */
    public long getLastRodReel() {
        return lastRodReel;
    }

    /**
     * Sets the player's last fishing rod reel timestamp.
     * @param lastRodReel The timestamp from {@link System#currentTimeMillis()}.
     */
    public void setLastRodReel(long lastRodReel) {
        this.lastRodReel = lastRodReel;
    }

    /**
     * Add a {@link LocationSnapshot} to the list.
     * @param locationSnapshot The {@link LocationSnapshot}.
     * @param maxSnapshots The maximum number of snapshots to store.
     */
    public void addLocationSnapshot(
            @NonNull LocationSnapshot locationSnapshot,
            int maxSnapshots) {
        if(!locationList.isEmpty() && maxSnapshots > 0) {
            while(locationList.size() >= maxSnapshots) {
                locationList.removeFirst();
            }
        }

        locationList.add(locationSnapshot);
    }

    /**
     * Remove the {@link LocationSnapshot} from the list.
     * @param locationSnapshot The {@link LocationSnapshot}.
     */
    public void removeLocationSnapshot(@NonNull LocationSnapshot locationSnapshot) {
        locationList.remove(locationSnapshot);
    }

    /**
     * Remove the most recent {@link LocationSnapshot} from the list.
     */
    public void removeMostRecentLocationSnapshot() {
        locationList.removeLast();
    }

    /**
     * Get the {@link List} of {@link LocationSnapshot}s.
     * @return A {@link List} of {@link LocationSnapshot}s.
     */
    public @NonNull List<LocationSnapshot> getLocationList() {
        return locationList;
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
     * Sets whether the player is manually marked as afk (i.e., the /afk command) or not.
     * @param status true if manually afk, false if not.
     */
    public void setPlayerInitiatedAFK(boolean status) {
        this.manualAFK = status;
    }

    /**
     * Is the player currently manually AFK (i.e., the /afk command) or not?
     * @return true if manually AFK, false if not.
     */
    public boolean isPlayerInitiated() {
        return manualAFK;
    }

    /**
     * Get the name of the player this data is associated with.
     * @return A {@link String} containing the player's name.
     */
    public @NonNull String getName() {
        return name;
    }
}
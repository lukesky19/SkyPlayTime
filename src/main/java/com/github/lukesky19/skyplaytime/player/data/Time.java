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

/**
 * This class stores time in seconds.
 */
public class Time {
    private long seconds = 0;

    /**
     * Constructor
     */
    public Time() {}

    /**
     * Constructor
     * @param seconds The initial time in seconds.
     */
    public Time(long seconds) {
        this.seconds = seconds;
    }

    /**
     * Adds the provided seconds to the current seconds.
     * @param seconds The seconds to add. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean add(long seconds) {
        if(seconds < 0) return false;

        this.seconds += seconds;

        return true;
    }

    /**
     * Removes the provided seconds from the current seconds.
     * @param seconds The seconds to remove. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean remove(long seconds) {
        if(seconds < 0) return false;

        this.seconds -= seconds;

        if(this.seconds < 0) this.seconds = 0;

        return true;
    }

    /**
     * Replace the existing seconds with the provided seconds.
     * @param seconds The seconds to set. Must be a positive number.
     * @return true if successful, false if not.
     */
    public boolean set(long seconds) {
        if(seconds < 0) return false;

        this.seconds = seconds;

        return true;
    }

    /**
     * Get the seconds.
     * @return The seconds.
     */
    public long get() {
        return seconds;
    }
}
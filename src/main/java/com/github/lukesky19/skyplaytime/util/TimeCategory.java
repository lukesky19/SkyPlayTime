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
package com.github.lukesky19.skyplaytime.util;

/**
 * This enum is used to identify play time categories.
 */
public enum TimeCategory {
    /**
     * The play time category for session play time (i.e., the time since login).
     */
    SESSION,
    /**
     * The play time category for daily play time.
     */
    DAILY,
    /**
     * The play time category for weekly play time.
     */
    WEEKLY,
    /**
     * The play time category for monthly play time.
     */
    MONTHLY,
    /**
     * The play time category for yearly play time.
     */
    YEARLY,
    /**
     * The play time category for total play time.
     */
    TOTAL,
    /**
     * The play time category to target all other play time categories.
     */
    ALL
}

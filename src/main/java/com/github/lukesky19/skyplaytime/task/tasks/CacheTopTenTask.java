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
package com.github.lukesky19.skyplaytime.task.tasks;

import com.github.lukesky19.skyplaytime.leaderboard.manager.LeaderboardManager;
import com.github.lukesky19.skyplaytime.util.TimeCategory;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * This task refreshes the cached top 10 play time for each {@link TimeCategory} (excluding {@link TimeCategory#ALL}) stored in {@link LeaderboardManager}.
 */
public class CacheTopTenTask extends BukkitRunnable {
    private final @NotNull LeaderboardManager leaderboardManager;

    /**
     * Constructor
     * @param leaderboardManager A {@link LeaderboardManager} instance.
     */
    public CacheTopTenTask(@NotNull LeaderboardManager leaderboardManager) {
        this.leaderboardManager = leaderboardManager;
    }

    /**
     * Update the top 10 players for each {@link TimeCategory} (excluding {@link TimeCategory#ALL}) stored in {@link LeaderboardManager}.
     */
    @Override
    public void run() {
        leaderboardManager.updateDatabaseTopTen();
    }
}

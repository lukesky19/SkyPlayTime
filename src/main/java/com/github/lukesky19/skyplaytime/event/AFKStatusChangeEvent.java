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
package com.github.lukesky19.skyplaytime.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This class is the event fired when a {@link Player}'s AFK status is changed.
 */
public class AFKStatusChangeEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled;
    private final Player player;
    private final boolean status;

    /**
     * The event fired when a {@link Player}'s AFK status changes.
     * @param player The {@link Player} associated with the event.
     * @param status The updated AFK status of the player. true if being marked AFK, false if being marked no longer AFK.
     */
    public AFKStatusChangeEvent(@NotNull Player player, boolean status) {
        this.player = player;
        this.status = status;
        this.isCancelled = false;
    }

    /**
     * The {@link Player} whose AFK status is being changed.
     * @return A {@link Player}
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    /**
     * The updated AFK status of the {@link Player}
     * @return true if being marked AFK, false if being marked no longer AFK.
     */
    public boolean getStatus() {
        return status;
    }

    /**
     * Get the {@link HandlerList} for this event.
     * @return A {@link HandlerList}
     */
    public static @NotNull HandlerList getHandlerList() {
        return HANDLERS;
    }

    /**
     * Get the {@link HandlerList} for this event.
     * @return A {@link HandlerList}
     */
    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Is the event cancelled?
     * @return true if cancelled, false if not.
     */
    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    /**
     * Set if the event is cancelled or not.
     * @param isCancelled {@code true} if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}

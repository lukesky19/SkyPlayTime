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
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This event is fired when a player's gains 1 second of play time.
 * This event does not fire for modifications to a player's play time done by command.
 */
public class PlayTimeGainedEvent extends Event {
    private static final @NotNull HandlerList HANDLERS = new HandlerList();
    private final @NotNull Player player;

    /**
     * The event fired when a {@link Player} has gained 1 second of play time.
     * @param player The {@link Player} associated with the event.
     */
    public PlayTimeGainedEvent(@NotNull Player player) {
        this.player = player;
    }

    /**
     * The {@link Player} whose AFK status is being changed.
     * @return A {@link Player}
     */
    public @NotNull Player getPlayer() {
        return player;
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
}

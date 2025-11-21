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

import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.time.Time;
import com.github.lukesky19.skylib.api.time.TimeUtil;
import com.github.lukesky19.skyplaytime.config.data.locale.Locale;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * This class contains utilities used throughout the plugin.
 */
public class PluginUtils {
    /**
     * Constructor
     */
    public PluginUtils() {}

    /**
     * Is the provided {@link Player} vanished?
     * @param player The {@link Player} to check if they are vanished.
     * @return true if vanished, false if not.
     */
    public static boolean isPlayerVanished(@NotNull Player player) {
        for(MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }

        return false;
    }

    /**
     * Formats the number of seconds to a formatted message to display in a chat message.
     * If any value is 0, it won't be shown.
     * @param timeMessage The {@link Locale.TimeFormat} to use for formatting.
     * @param timeInSeconds The time in milliseconds to format to a message.
     * @return The time in seconds formatted to a {@link String}.
     */
    public static @NotNull String formatPlayTimeChat(@NotNull Locale.TimeFormat timeMessage, long timeInSeconds) {
        boolean firstUnit = true;
        Time timeRecord = TimeUtil.millisToTime(timeInSeconds * 1000L);
        StringBuilder messageBuilder = new StringBuilder();

        if(!timeMessage.prefix().isEmpty()) messageBuilder.append(timeMessage.prefix());

        if (timeRecord.years() > 0) {
            messageBuilder.append(timeMessage.years());
            firstUnit = false;
        }

        if (timeRecord.months() > 0) {
            if (!firstUnit) {
                messageBuilder.append(" ");
            }
            messageBuilder.append(timeMessage.months());
            firstUnit = false;
        }

        if (timeRecord.weeks() > 0) {
            if (!firstUnit) {
                messageBuilder.append(" ");
            }
            messageBuilder.append(timeMessage.weeks());
            firstUnit = false;
        }

        if (timeRecord.days() > 0) {
            if (!firstUnit) {
                messageBuilder.append(" ");
            }
            messageBuilder.append(timeMessage.days());
            firstUnit = false;
        }

        if (timeRecord.hours() > 0) {
            if (!firstUnit) {
                messageBuilder.append(" ");
            }
            messageBuilder.append(timeMessage.hours());
            firstUnit = false;
        }

        if (timeRecord.minutes() > 0) {
            if (!firstUnit) {
                messageBuilder.append(" ");
            }
            messageBuilder.append(timeMessage.minutes());
            firstUnit = false;
        }

        if (timeRecord.seconds() > 0) {
            if (!firstUnit) {
                messageBuilder.append(" ");
            }
            messageBuilder.append(timeMessage.seconds());
            firstUnit = false;
        }

        if(firstUnit) {
            messageBuilder.append("0 ").append(timeMessage.seconds());
        }

        if(!timeMessage.suffix().isEmpty()) messageBuilder.append(timeMessage.suffix());

        List<TagResolver.Single> placeholders = List.of(
                Placeholder.parsed("years", String.valueOf(timeRecord.years())),
                Placeholder.parsed("months", String.valueOf(timeRecord.months())),
                Placeholder.parsed("weeks", String.valueOf(timeRecord.weeks())),
                Placeholder.parsed("days", String.valueOf(timeRecord.days())),
                Placeholder.parsed("hours", String.valueOf(timeRecord.hours())),
                Placeholder.parsed("minutes", String.valueOf(timeRecord.minutes())),
                Placeholder.parsed("seconds", String.valueOf(timeRecord.seconds())));

        return AdventureUtil.serialize(AdventureUtil.deserialize(messageBuilder.toString(), placeholders));
    }
}

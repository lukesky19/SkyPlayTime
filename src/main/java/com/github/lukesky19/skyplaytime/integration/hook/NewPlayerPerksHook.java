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
package com.github.lukesky19.skyplaytime.integration.hook;

import com.github.lukesky19.newPlayerPerks.NewPlayerPerksAPI;
import com.github.lukesky19.skylib.common.api.integration.Hook;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

/**
 * This clss manages interfacing with the NewPlayerPerks plugin.
 */
public class NewPlayerPerksHook implements Hook {
    private final @NonNull SkyPlayTime skyPlayTime;
    private @Nullable NewPlayerPerksAPI newPlayerPerksAPI;

    /**
     * Constructor
     * @param skyPlayTime A {@link SkyPlayTime} instance.
     */
    public NewPlayerPerksHook(@NonNull SkyPlayTime skyPlayTime) {
        this.skyPlayTime = skyPlayTime;
    }

    @Override
    public void initialize() {
        if(skyPlayTime.getServer().getPluginManager().isPluginEnabled("NewPlayerPerks")) {
            RegisteredServiceProvider<NewPlayerPerksAPI> provider = Bukkit.getServicesManager().getRegistration(NewPlayerPerksAPI.class);
            if(provider != null) {
                newPlayerPerksAPI = provider.getProvider();
            }
        }
    }

    @Override
    public boolean isHooked() {
        return newPlayerPerksAPI != null;
    }

    /**
     * Does the player have perks applied?
     * @param playerId The {@link UUID} of the player.
     * @return true if the player has perks applied, otherwise false.
     */
    public boolean hasPerks(@NonNull UUID playerId) {
        if(newPlayerPerksAPI == null) return false;

        return newPlayerPerksAPI.hasPerks(playerId);
    }

    /**
     * Is the invulnerability perk enabled?
     * @return true if enabled, otherwise false.
     */
    public boolean isInvulnerablePerkEnabled() {
        if(newPlayerPerksAPI == null) return false;

        return newPlayerPerksAPI.isInvulnerablePerkEnabled();
    }
}
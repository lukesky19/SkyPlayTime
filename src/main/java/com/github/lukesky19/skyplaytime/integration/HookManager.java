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
package com.github.lukesky19.skyplaytime.integration;

import com.github.lukesky19.skylib.common.api.integration.Hook;
import com.github.lukesky19.skyplaytime.SkyPlayTime;
import com.github.lukesky19.skyplaytime.integration.hook.NewPlayerPerksHook;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * This class manages hooks into different plugins.
 */
public class HookManager {
    private final @NonNull Map<Class<?>, Hook> hooks = new HashMap<>();

    /**
     * Constructor
     * @param skyPlayTime A {@link SkyPlayTime} instance.
     */
    public HookManager(@NonNull SkyPlayTime skyPlayTime) {
        registerHook(NewPlayerPerksHook.class, new NewPlayerPerksHook(skyPlayTime));
    }

    /**
     * Register a hook.
     * @param hookClass The class.
     * @param hook The class instance.
     * @param <T> Parameter for any class that extends {@link Hook}.
     */
    public <T extends Hook> void registerHook(@NonNull Class<T> hookClass, @NonNull Hook hook) {
        hooks.put(hookClass, hook);
        hook.initialize();
    }

    /**
     * Get a hook.
     * @param hookClass The class.
     * @param <T> Parameter for any class that extends {@link Hook}.
     * @return The class instance.
     */
    public @NonNull <T extends Hook> T getHook(@NonNull Class<T> hookClass) {
        return hookClass.cast(hooks.get(hookClass));
    }
}
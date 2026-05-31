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
package com.github.lukesky19.skyplaytime.util.location;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.BlockType;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.BubbleColumn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.lang.ref.WeakReference;

/**
 * This record stores the data for a {@link Location} in an immutable format.
 * @param worldReference A {@link WeakReference} containing a {@link World}.
 * @param x The x coordinate.
 * @param y The y coordinate.
 * @param z The z coordinate.
 * @param packedXYZ The x y and z coordinate packed into a single long.
 * @param inVehicle Was this location stored as a result of movement in a vehicle?
 * @param byPiston Was this location stored as a result of movement by a piston?
 * @param blockState The {@link BlockState} of the block at the location at time of creation. This block state is not actually placed in the world, as it is a snapshot.
 * @param aboveBlockState The {@link BlockState} above the location at the time of creation. This block state is not actually placed in the world, as it is a snapshot.
 * @param belowBlockState The {@link BlockState} below the location at the time of creation. This block state is not actually placed in the world, as it is a snapshot.
 * @param inWater Is this location considered in water?
 * @param aboveInWater Is the location above this one considered in water?
 * @param belowInWater Is the location below this one considered in water?
 * @param inBubbleColumn Is this location considered a bubble column?
 * @param aboveInBubbleColumn Is the location above this one considered a bubble column?
 * @param belowInBubbleColumn Is the location below this one considered a bubble column?
 * @param timestamp The timestamp.
 */
public record LocationSnapshot(
        @NotNull WeakReference<World> worldReference,
        int x,
        int y,
        int z,
        long packedXYZ,
        @NonNull BlockState blockState,
        @NonNull BlockState aboveBlockState,
        @NonNull BlockState belowBlockState,
        boolean inVehicle,
        boolean byPiston,
        boolean inWater,
        boolean aboveInWater,
        boolean belowInWater,
        boolean inBubbleColumn,
        boolean aboveInBubbleColumn,
        boolean belowInBubbleColumn,
        long timestamp) {
    /**
     * Constructor
     * @param world A {@link World}.
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param z The z coordinate.
     * @param inVehicle Is the player in a vehicle?
     * @param byPiston Was this location a result of a piston?
     */
    public LocationSnapshot(
            @NotNull World world,
            int x,
            int y,
            int z,
            boolean inVehicle,
            boolean byPiston) {
        long packedXYZ = (((long) x) << 42) ^ (((long) (y & 0x3FFFF)) << 21) ^ (z & 0x1FFFFF);

        BlockState blockState = world.getBlockState(x, y, z).copy();
        BlockData blockData = blockState.getBlockData();
        BlockType blockType = blockState.getType().asBlockType();
        boolean inWater = blockType != null && blockType.equals(BlockType.WATER);
        boolean inBubbleColumn = blockData instanceof BubbleColumn;

        BlockState aboveBlockState = world.getBlockState(x, y + 1, z).copy();
        BlockData aboveBlockData = aboveBlockState.getBlockData();
        BlockType aboveBlockType = aboveBlockState.getType().asBlockType();
        boolean aboveInWater = aboveBlockType != null && aboveBlockType.equals(BlockType.WATER);
        boolean aboveInBubbleColumn = aboveBlockData instanceof BubbleColumn;

        BlockState belowBlockState = world.getBlockState(x, y - 1, z).copy();
        BlockData belowBlockData = belowBlockState.getBlockData();
        BlockType belowBlockType = belowBlockState.getType().asBlockType();
        boolean belowInWater = belowBlockType != null && belowBlockType.equals(BlockType.WATER);
        boolean belowInBubbleColumn = belowBlockData instanceof BubbleColumn;

        this(new WeakReference<>(world), x, y, z, packedXYZ,
                blockState, belowBlockState, aboveBlockState,
                inVehicle, byPiston,
                inWater, aboveInWater, belowInWater,
                inBubbleColumn, aboveInBubbleColumn, belowInBubbleColumn,
                System.currentTimeMillis());
    }

    /**
     * Get the {@link World} for this location.
     * @return The {@link World} or null.
     * @throws RuntimeException if the world reference is no longer valid.
     */
    public @NotNull World getWorld() {
        @Nullable World world = worldReference.get();
        if (world == null) {
            throw new RuntimeException("World reference is invalid. Likely unloaded.");
        }
        return world;
    }

    /**
     * Get the x coordinate of the location.
     * @return The x coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Get the y coordinate of the location.
     * @return The y coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Get the z coordinate of the location.
     * @return The z coordinate.
     */
    public int getZ() {
        return z;
    }

    /**
     * Is the location, above location, or below location water?
     * @return true or false.
     */
    public boolean isNearWater() {
        return inWater || aboveInWater || belowInWater;
    }

    /**
     * Is the location or above location water?
     * @return true or false.
     */
    public boolean isInWater() {
        return inWater || aboveInWater;
    }

    /**
     * Is the location, above location, or below location a bubble column?
     * @return true or false.
     */
    public boolean isNearBubbleColumn() {
        return inBubbleColumn() || aboveInBubbleColumn() || belowInBubbleColumn();
    }

    /**
     * Is the {@link LocationSnapshot} similar to this one by the world names and coordinates?
     * @param compare The {@link LocationSnapshot} to compare.
     * @return true if similar, otherwise false.
     */
    public boolean isSimilarByCoordinates(@NonNull LocationSnapshot compare) {
        return this.getWorld().getName().equals(compare.getWorld().getName())
                && this.getCoordinatesAsLong() == compare.getCoordinatesAsLong();
    }

    /**
     * Check if the object provided is equal to this LocationSnapshot.
     * @param obj The object to compare.
     * @return true if equal, otherwise false. See other isSimilar methods for more individual equality tests.
     */
    @Override
    public boolean equals(@NotNull Object obj) {
        // Check if object is of a LocationSnapshot
        if(!(obj instanceof LocationSnapshot compareLocation)) return false;

        // Throw error on invalid world
        @Nullable World world = worldReference.get();
        if(world == null) {
            throw new RuntimeException("World reference is invalid. Likely unloaded.");
        }

        // Compare Worlds based on names
        if(!compareLocation.getWorld().getName().equals(world.getName())) return false;

        if(x != compareLocation.getX()
                || y != compareLocation.getY()
                || z != compareLocation.getZ()) return false;

        // Compare Block States
        if(!blockState.equals(compareLocation.blockState)) return false;
        if(!aboveBlockState.equals(compareLocation.aboveBlockState)) return false;
        if(!belowBlockState.equals(compareLocation.belowBlockState)) return false;

        // Compare booleans
        if(inWater != compareLocation.inWater) return false;
        if(aboveInWater != compareLocation.aboveInWater) return false;
        if(belowInWater != compareLocation.belowInWater) return false;
        if(inBubbleColumn != compareLocation.inBubbleColumn) return false;
        if(aboveInBubbleColumn != compareLocation.aboveInBubbleColumn) return false;
        return belowInBubbleColumn == compareLocation.belowInBubbleColumn;
    }

    /**
     * Create a hash code for this LocationSnapshot.
     * @return An int.
     * @throws RuntimeException if the world reference is invalid.
     */
    @Override
    public int hashCode() throws RuntimeException {
        @Nullable World world = worldReference.get();
        if(world == null) {
            throw new RuntimeException("World reference is invalid. Likely unloaded.");
        }

        // Starting Value
        int result = 7;

        // World
        result = 31 * result + world.getName().hashCode();

        // Coordinates
        result = 31 * result + Integer.hashCode(x);
        result = 31 * result + Integer.hashCode(y);
        result = 31 * result + Integer.hashCode(z);

        // Block States
        result = 31 * result + blockState.hashCode();
        result = 31 * result + aboveBlockState.hashCode();
        result = 31 * result + belowBlockState.hashCode();

        // Booleans
        result = 31 * result + Boolean.hashCode(inWater);
        result = 31 * result + Boolean.hashCode(aboveInWater);
        result = 31 * result + Boolean.hashCode(belowInWater);
        result = 31 * result + Boolean.hashCode(inBubbleColumn);
        result = 31 * result + Boolean.hashCode(aboveInBubbleColumn);
        result = 31 * result + Boolean.hashCode(belowInBubbleColumn);

        return result;
    }

    /**
     * Get the x y and z coordinates as a packed long.
     * @return The coordinates as a packed long
     */
    public long getCoordinatesAsLong() {
        return packedXYZ;
    }
}
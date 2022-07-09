package me.modify.supersponge.util;

import me.modify.supersponge.objects.AbsorbShape;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.ArrayList;
import java.util.List;

public class SpongeUtil {

    /**
     * Absorbs all blocks in the given radius in the shape defined by the 'shape' parameter.
     * @param centerBlock central block to absorb from
     * @param radius radius around central block to absorb
     * @param shape shape to absorb
     * @param function absorb function
     */
    public static void absorbRadius(Block centerBlock, int radius, AbsorbShape shape, SpongeAbsorbFunction function) {
        World world = centerBlock.getWorld();

        int bx = centerBlock.getX();
        int by = centerBlock.getY();
        int bz = centerBlock.getZ();

        for(int x = bx - radius; x <= bx + radius; x++) {
            for(int y = by - radius; y <= by + radius; y++) {
                for(int z = bz - radius; z <= bz + radius; z++) {

                    if (shape == AbsorbShape.CUBE) {
                        Block block = world.getBlockAt( x,  y,  z);
                        function.absorbBlock(block);
                    } else {
                        double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y)));

                        if(distance < radius * radius) {
                            Block block = world.getBlockAt(x, y, z);
                            function.absorbBlock(block);
                        }
                    }
                }
            }
        }

    }

/*    public static List<Location> getAllLocationsInRadius(Block centerBlock, int radius, AbsorbShape shape) {
        List<Location> allBlocks = new ArrayList<>();
        World world = centerBlock.getWorld();

        int bx = centerBlock.getX();
        int by = centerBlock.getY();
        int bz = centerBlock.getZ();

        for(int x = bx - radius; x <= bx + radius; x++) {
            for (int y = by - radius; y <= by + radius; y++) {
                for (int z = bz - radius; z <= bz + radius; z++) {

                    if (shape == AbsorbShape.CUBE) {
                        Block block = world.getBlockAt(x, y, z);
                        allBlocks.add(block.getLocation());
                    } else {
                        double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y)));

                        if(distance < radius * radius) {
                            Block block = world.getBlockAt(x, y, z);
                            allBlocks.add(block.getLocation());
                        }
                    }
                }
            }
        }

        return allBlocks;
    }

    *//**
     * Recursive function, uses the sponge absorb function to absorb all blocks
     * connected to the target block.
     * @param block target block to absorb connecting blocks
     * @param allBlocks list of all blocks the method must check
     * @param function absorb function, handles which type of blocks should be absorbed.
     *//*
    public static void absorbConnectingBlocks(Block block, List<Location> allBlocks, SpongeAbsorbFunction function) {
        if (allBlocks.contains(block.getLocation())) {
            function.absorbBlock(block);

            for (BlockFace blockFace : Constants.BLOCK_ADJACENTS) {
                Block adjacent = block.getRelative(blockFace);
                absorbConnectingBlocks(adjacent, allBlocks, function);
            }
        }
    }*/
}

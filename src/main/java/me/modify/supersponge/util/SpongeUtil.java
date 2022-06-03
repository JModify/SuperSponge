package me.modify.supersponge.util;

import org.bukkit.World;
import org.bukkit.block.Block;

public class SpongeUtil {

    /**
     * Absorbs all blocks in the given radius in the shape of a cube.
     * @param centerBlock central block to absorb from
     * @param radius radius around central block to absorb
     * @param function absorb function
     */
    public static void absorbCubeRadius(Block centerBlock, int radius, SpongeAbsorbFunction function) {
        World world = centerBlock.getWorld();

        int bx = centerBlock.getX();
        int by = centerBlock.getY();
        int bz = centerBlock.getZ();

        for (int fx = -radius; fx <= radius; ++fx) {
            for (int fy = -radius; fy <= radius; ++fy) {
                for (int fz = -radius; fz <= radius; ++fz) {
                    Block block = world.getBlockAt(bx + fx, by + fy, bz + fz);
                    function.absorbBlock(block);
                }
            }
        }
    }

    /**
     * Absorbs all blocks in the given radius in the shape of a cube.
     * @param centerBlock central block to absorb from
     * @param radius radius around central block to absorb
     * @param function absorb function
     */
    public static void absorbSphereRadius(Block centerBlock, int radius, SpongeAbsorbFunction function) {
        World world = centerBlock.getWorld();

        int bx = centerBlock.getX();
        int by = centerBlock.getY();
        int bz = centerBlock.getZ();

        for(int x = bx - radius; x <= bx + radius; x++) {
            for(int y = by - radius; y <= by + radius; y++) {
                for(int z = bz - radius; z <= bz + radius; z++) {
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

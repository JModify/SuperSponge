package me.modify.supersponge.util;

import org.bukkit.World;
import org.bukkit.block.Block;

public class SpongeUtil {

    /**
     * Absorbs all blocks in the given radius.
     * @param centerBlock central block to absorb from
     * @param radius radius around central block to absorb
     * @param function absorb function
     */
    public static void absorbRadius(Block centerBlock, int radius, SpongeAbsorbFunction function) {
        World world = centerBlock.getWorld();

        int bx = centerBlock.getX();
        int by = centerBlock.getY();
        int bz = centerBlock.getZ();

        for (int fx = -radius; fx <= radius; ++fx) {
            for (int fy = -radius; fy <= radius; ++fy) {
                for (int fz = -radius; fz <= radius; ++fz) {
                    Block b = world.getBlockAt(bx + fx, by + fy, bz + fz);
                    function.absorbBlock(b);
                }
            }
        }
    }

}

package me.modify.supersponge.util;

import org.bukkit.block.Block;

/**
 * Functional interface used as a function that absorbs a block.
 */
@FunctionalInterface
public interface SpongeAbsorbFunction {

    /**
     * Absorbs this block
     * @param block block to absorb
     */
    void absorbBlock(Block block);

}

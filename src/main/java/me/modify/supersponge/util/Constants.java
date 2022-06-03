package me.modify.supersponge.util;

import org.bukkit.block.BlockFace;

public class Constants {

    /**
     * Meta-data value attached to item stacks which is used to determine
     * whether an item is a super sponge or not.
     */
    public static final int SUPER_SPONGE_META_IDENTIFIER = 1834946194;

    /** All adjacent block faces*/
    public static final BlockFace[] BLOCK_ADJACENTS = { BlockFace.DOWN, BlockFace.UP,
            BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST };

    /** Five minutes in ticks, used for SuperSpongeLocationSaveTimer */
    public static final long FIVE_MINUTES_IN_TICKS = 6000;

    /** The default sponge clearing radius for lava */
    public static final int DEFAULT_SPONGE_RADIUS = 4;
}

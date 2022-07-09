package me.modify.supersponge.listeners;

import com.modify.fundamentum.text.PlugLogger;
import me.modify.supersponge.SuperSponge;
import me.modify.supersponge.data.cache.SuperSpongeLocationCache;
import me.modify.supersponge.managers.SuperSpongeManager;
import me.modify.supersponge.objects.AbsorbShape;
import me.modify.supersponge.objects.SuperSpongeLocation;
import me.modify.supersponge.util.Constants;
import me.modify.supersponge.util.SpongeAbsorbFunction;
import me.modify.supersponge.util.SpongeUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SpongeAbsorbEvent;

public class SpongeListener implements Listener {

    @EventHandler
    public void onSpongeAbsorb(SpongeAbsorbEvent event) {
        SuperSpongeManager manager = SuperSponge.getInstance().getSuperSpongeManager();
        SuperSpongeLocationCache spongeLocations = manager.getSpongeLocations();
        SuperSpongeLocation superSpongeLocation = SuperSpongeLocation.fromBukkitLocation(event.getBlock().getLocation());
        if (spongeLocations.isSuperSpongeLocation(superSpongeLocation)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {

        Block block = event.getBlock();

        if (event.isCancelled()) return;

        SuperSpongeManager manager = SuperSponge.getInstance().getSuperSpongeManager();
        if (manager.isSuperSponge(event.getItemInHand())) {
            manager.handleSuperSpongePlacement(event);
        } else {
            if (block.getType() == Material.SPONGE) {
                handleDefaultSpongePlacement(block);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (event.isCancelled()) return;

        SuperSpongeManager manager = SuperSponge.getInstance().getSuperSpongeManager();
        SuperSpongeLocationCache spongeLocations = manager.getSpongeLocations();
        SuperSpongeLocation superSpongeLocation = SuperSpongeLocation.fromBukkitLocation(block.getLocation());
        if (spongeLocations.isSuperSpongeLocation(superSpongeLocation)) {
            manager.handleSuperSpongeBreak(event, superSpongeLocation);
        }
    }

    private void handleDefaultSpongePlacement(Block block) {

        boolean inLava = false;
        for (BlockFace f : Constants.BLOCK_ADJACENTS) {
            if (block.getRelative(f).getType() == Material.LAVA) {
                inLava = true;
                break;
            }
        }
        if (!inLava) return;

        int radius = Constants.DEFAULT_SPONGE_RADIUS;

        SpongeAbsorbFunction spongeAbsorbFunction = absorbedBlock -> {
            if (absorbedBlock.getType() == Material.LAVA) {
                absorbedBlock.setType(Material.AIR);
            }
        };

        SpongeUtil.absorbRadius(block, radius, AbsorbShape.SPHERE, spongeAbsorbFunction);
    }

}

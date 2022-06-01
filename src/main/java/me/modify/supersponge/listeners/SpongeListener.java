package me.modify.supersponge.listeners;

import me.modify.supersponge.SuperSponge;
import me.modify.supersponge.data.cache.SuperSpongeLocationCache;
import me.modify.supersponge.managers.SuperSpongeManager;
import me.modify.supersponge.objects.SuperSpongeLocation;
import me.modify.supersponge.util.Constants;
import me.modify.supersponge.util.SpongeAbsorbFunction;
import me.modify.supersponge.util.SpongeUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
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

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        Block block = event.getBlock();

        SuperSpongeManager manager = SuperSponge.getInstance().getSuperSpongeManager();
        if (manager.isSuperSponge(event.getItemInHand())) {
            manager.handleSuperSpongePlacement(block);
        } else {
            if (block.getType() == Material.SPONGE) {
                handleDefaultSpongePlacement(block);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        SuperSpongeManager manager = SuperSponge.getInstance().getSuperSpongeManager();
        SuperSpongeLocationCache spongeLocations = manager.getSpongeLocations();
        SuperSpongeLocation superSpongeLocation = SuperSpongeLocation.fromBukkitLocation(event.getBlock().getLocation());
        if (spongeLocations.isSuperSpongeLocation(superSpongeLocation)) {
            event.setCancelled(true);

            block.setType(Material.AIR);
            spongeLocations.removeLocation(superSpongeLocation);

            if (player.getGameMode() == GameMode.CREATIVE) return;
            try {
                block.getWorld().dropItemNaturally(block.getLocation(), manager.getSuperSpongeItem(1));
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }

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

        int radius = Constants.DEFAULT_LAVA_SPONGE_RADIUS;

        SpongeAbsorbFunction spongeAbsorbFunction = absorbedBlock -> {
            if (absorbedBlock.getType() == Material.LAVA) {
                absorbedBlock.setType(Material.AIR);
            }
        };

        SpongeUtil.absorbRadius(block, radius, spongeAbsorbFunction);
    }

}

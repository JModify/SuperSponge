package me.modify.supersponge.managers;

import me.modify.supersponge.data.cache.SuperSpongeLocationCache;
import me.modify.supersponge.data.cache.SuperSpongePropertyCache;
import com.modify.fundamentum.text.ColorUtil;
import lombok.Getter;
import me.modify.supersponge.SuperSponge;
import me.modify.supersponge.hooks.CoreProtectHook;
import me.modify.supersponge.objects.AbsorbShape;
import me.modify.supersponge.objects.BlockedType;
import me.modify.supersponge.objects.SuperSpongeLocation;
import me.modify.supersponge.util.Constants;
import me.modify.supersponge.util.SpongeAbsorbFunction;
import me.modify.supersponge.util.SpongeUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

public class SuperSpongeManager {

    /*** Super sponge properties cache.*/
    @Getter private SuperSpongePropertyCache spongeProperties;

    /** Location cache for placed super sponges */
    @Getter private SuperSpongeLocationCache spongeLocations;

    /** Manages all actions which are delayed */
    @Getter private SuperSpongeDelayManager delayManager;

    /**
     * Constructs a new SuperSpongeManager.
     */
    public SuperSpongeManager() {
        this.spongeProperties = new SuperSpongePropertyCache();
        this.spongeLocations = new SuperSpongeLocationCache();
        this.delayManager = new SuperSpongeDelayManager();
    }

    /**
     * Loads all super sponge data from data file.
     */
    public void load() {
        spongeLocations.load();
        spongeProperties.load();
    }

    /**
     * Saves all super sponge caches back into data file.
     */
    public void save() {
        spongeLocations.save();
    }

    /**
     * Retrieves a super sponge item using the super sponge item configuration in config.yml
     * Also attaches meta-data to returned item, so it can later be identified as a super sponge.
     * @param amount amount of super sponges to retrieve.
     * @return super sponge item(s)
     *
     * @throws InvalidConfigurationException if any required configuration values are not set.
     */
    public ItemStack getSuperSpongeItem(int amount) throws InvalidConfigurationException {
        FileConfiguration f = SuperSponge.getInstance().getDataManager().getConfigFile().getConfig();

        String configSection = "super-sponge.item";

        if (!f.isSet(configSection + ".name")) {
            throw new InvalidConfigurationException("Invalid super sponge configuration. Item 'name' value corrupted.");
        }
        String displayName = ColorUtil.format(f.getString(configSection + ".name"));

        if (!f.isSet(configSection + ".lore")) {
            throw new InvalidConfigurationException("Invalid super sponge configuration. Item 'lore' value corrupted.");
        }
        List<String> lore = ColorUtil.formatList(f.getStringList(configSection + ".lore"));

        if (!f.isSet(configSection + ".enchant-glow")) {
            throw new InvalidConfigurationException("Invalid super sponge configuration. Item 'enchant-glow' value corrupted.");
        }
        boolean enchantGlow = f.getBoolean(configSection + ".enchant-glow");

        ItemStack item = new ItemStack(Material.SPONGE, amount);

        ItemMeta meta = item.getItemMeta();


        meta.setDisplayName(displayName);

        if (enchantGlow) {
            meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        if (!lore.isEmpty()) {
            meta.setLore(lore);
        }

        NamespacedKey namespacedKey = new NamespacedKey(SuperSponge.getInstance(), "is-super-sponge");
        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.INTEGER, Constants.SUPER_SPONGE_META_IDENTIFIER);

        item.setItemMeta(meta);

        return item;
    }

    /**
     * Checks whether an item is a super sponge.
     * This method will return false if:
     * - The checked item is not a sponge
     * - The check item does not contain the super sponge meta identifier.
     *
     * @param item item to check
     * @return true if a super sponge, else false.
     */
    public boolean isSuperSponge(ItemStack item) {
        if (item.getType() != Material.SPONGE) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }

        NamespacedKey namespacedKey = new NamespacedKey(SuperSponge.getInstance(), "is-super-sponge");
        PersistentDataContainer container = meta.getPersistentDataContainer();
        if (!container.has(namespacedKey, PersistentDataType.INTEGER)) {
            return false;
        }

        Integer wrappedValue = container.get(namespacedKey, PersistentDataType.INTEGER);
        int primValue = wrappedValue != null ? wrappedValue : 0;

        return primValue == Constants.SUPER_SPONGE_META_IDENTIFIER;
    }

    /**
     * Handles the block place event when a super sponge is placed.
     * @param event event to handle
     */
    public void handleSuperSpongePlacement(BlockPlaceEvent event) {

        Player player = event.getPlayer();
        Block placedBlock = event.getBlock();

        UUID playerId = player.getUniqueId();

        if (delayManager.shouldDelayPlacements()) {
            if (!player.hasPermission("supersponge.delay.place.bypass")) {
                if (delayManager.isBlocked(playerId, BlockedType.PLACING)) {
                    event.setCancelled(true);
                    int timeRemaining = delayManager.getTimeRemaining(playerId, BlockedType.PLACING);
                    player.sendMessage(ColorUtil.format("&4&l(!) &r&cYou must wait " + timeRemaining + " seconds before placing another super sponge."));
                    return;
                }
                delayManager.blockUser(playerId, BlockedType.PLACING);
            }
        }


        spongeLocations.addLocation(placedBlock.getLocation());

        boolean inLiquid = false;
        for (BlockFace f : Constants.BLOCK_ADJACENTS) {
            if (placedBlock.getRelative(f).getType() == Material.LAVA || placedBlock.getRelative(f).getType() == Material.WATER) {
                inLiquid = true;
                break;
            }
        }
        if (!inLiquid) return;

        SuperSpongePropertyCache spongeProperties = getSpongeProperties();
        int radius = spongeProperties.getClearRadius();
        boolean blockUpdates = SuperSponge.getInstance().getSuperSpongeManager().getSpongeProperties().shouldDoBlockUpdates();

        CoreProtectHook coreProtectHook = SuperSponge.getInstance().getCoreProtectHook();
        SpongeAbsorbFunction spongeAbsorbFunction = blockToAbsorb -> {

            BlockData blockData = blockToAbsorb.getBlockData();
            Material material = blockToAbsorb.getType();
            Location location = blockToAbsorb.getLocation();

            // Checks if the block is a lava block
            if (spongeProperties.shouldClearLava()) {
                if (material == Material.LAVA) {
                    blockToAbsorb.setType(Material.AIR, blockUpdates);
                    coreProtectHook.logBlockRemoval(player.getName(), location, material, blockData);
                    return;
                }
            }

            if (spongeProperties.shouldClearWater()) {
                // Checks if the block is water logged, and removes its water logging, then continues loop
                if (blockToAbsorb.getBlockData() instanceof Waterlogged waterlogged) {
                    if (waterlogged.isWaterlogged()) {
                        waterlogged.setWaterlogged(false);
                        coreProtectHook.logBlockRemoval(player.getName(), location, material, blockData);
                        return;
                    }
                }

                // Checks if block is a pure water block, then continues loop
                if (material == Material.WATER) {
                    blockToAbsorb.setType(Material.AIR, blockUpdates);
                    coreProtectHook.logBlockRemoval(player.getName(), location, material, blockData);
                    return;
                }

                // Checks if block is seagrass or tall-seagrass, breaks them then sets block to air to remove water.
                if (material == Material.SEAGRASS || material == Material.TALL_SEAGRASS) {
                    blockToAbsorb.breakNaturally();
                    blockToAbsorb.setType(Material.AIR, blockUpdates);
                    coreProtectHook.logBlockRemoval(player.getName(), location, material, blockData);
                }
            }
        };

        if (spongeProperties.shouldAbsorbSphere()) {
            SpongeUtil.absorbRadius(placedBlock, radius, AbsorbShape.SPHERE, spongeAbsorbFunction);
            return;
        }

        SpongeUtil.absorbRadius(placedBlock, radius, AbsorbShape.CUBE, spongeAbsorbFunction);
    }

    /**
     * Handles the block place event when a super sponge is broken
     * @param event event to handle
     * @param superSpongeLocation location of the super sponge.
     */
    public void handleSuperSpongeBreak(BlockBreakEvent event, SuperSpongeLocation superSpongeLocation) {

        Player player = event.getPlayer();
        Block block = event.getBlock();

        UUID playerId = player.getUniqueId();
        SuperSpongeDelayManager delayManager = getDelayManager();

        if (delayManager.shouldDelayBreaking()) {
            if (!player.hasPermission("supersponge.delay.break.bypass")) {
                if (delayManager.isBlocked(playerId, BlockedType.BREAKING)) {
                    int timeRemaining = delayManager.getTimeRemaining(playerId, BlockedType.BREAKING);
                    player.sendMessage(ColorUtil.format("&4&l(!) &r&cYou must wait " + timeRemaining + " seconds before breaking another super sponge."));
                    event.setCancelled(true);
                    return;
                }
                delayManager.blockUser(playerId, BlockedType.BREAKING);
            }
        }

        block.setType(Material.AIR);
        spongeLocations.removeLocation(superSpongeLocation);

        if (player.getGameMode() == GameMode.CREATIVE) return;

        Location locationToDrop = block.getLocation();
        locationToDrop.add(0.5, 0.5, 0.5);

        try {
            // Spigot bug with dropItemNaturally when there is a wall, have to use dropItem for this reason.
            //block.getWorld().dropItemNaturally(block.getLocation(), getSuperSpongeItem(1));
            block.getWorld().dropItem(locationToDrop, getSuperSpongeItem(1));
        } catch (InvalidConfigurationException e) {
            SuperSponge.getInstance().getDebugger().sendDebugError("Failed to drop super sponge item at " + superSpongeLocation.toString() + ". Loss of super sponge");
            e.printStackTrace();
        }
    }

}

package me.modify.supersponge.managers;

import me.modify.supersponge.data.cache.SuperSpongeLocationCache;
import me.modify.supersponge.data.cache.SuperSpongePropertyCache;
import com.modify.fundamentum.text.ColorUtil;
import lombok.Getter;
import me.modify.supersponge.SuperSponge;
import me.modify.supersponge.util.Constants;
import me.modify.supersponge.util.SpongeAbsorbFunction;
import me.modify.supersponge.util.SpongeUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

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
     * Caches all super sponge locations from file into the SuperSpongeLocationCache.
     */
    public void loadLocations() {
        spongeLocations.load();
    }

    /**
     * Saves all super sponge locations from SuperSpongeLocationCache into data file.
     */
    public void saveLocations() {
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
     * @param placedBlock block to handle
     */
    public void handleSuperSpongePlacement(Block placedBlock) {
        this.getSpongeLocations().addLocation(placedBlock.getLocation());

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

        SpongeAbsorbFunction spongeAbsorbFunction = blockToAbsorb -> {
            // Checks i
            if (spongeProperties.shouldClearLava()) {
                if (blockToAbsorb.getType() == Material.LAVA) {
                    blockToAbsorb.setType(Material.AIR);
                    return;
                }
            }

            if (spongeProperties.shouldClearWater()) {
                // Checks if the block is water logged, and removes its water logging, then continues loop
                if (blockToAbsorb.getBlockData() instanceof Waterlogged waterlogged) {
                    if (waterlogged.isWaterlogged()) {
                        waterlogged.setWaterlogged(false);
                        return;
                    }
                }

                // Checks if block is a pure water block, then continues loop
                if (blockToAbsorb.getType() == Material.WATER) {
                    blockToAbsorb.setType(Material.AIR);
                    return;
                }

                // Checks if block is seagrass or tall-seagrass, breaks them then sets block to air to remove water.
                if (blockToAbsorb.getType() == Material.SEAGRASS || blockToAbsorb.getType() == Material.TALL_SEAGRASS) {
                    blockToAbsorb.breakNaturally();
                    blockToAbsorb.setType(Material.AIR);
                }
            }
        };

        SpongeUtil.absorbRadius(placedBlock, radius, spongeAbsorbFunction);
    }

}
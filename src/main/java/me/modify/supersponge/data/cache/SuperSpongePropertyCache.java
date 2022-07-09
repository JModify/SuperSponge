package me.modify.supersponge.data.cache;

import me.modify.supersponge.SuperSponge;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class SuperSpongePropertyCache {

    private Map<SuperSpongeProperty, Object> propertyMap;

    public SuperSpongePropertyCache() {
        propertyMap = new HashMap<>();
    }

    public boolean shouldAbsorbSphere() {
        return (boolean) propertyMap.get(SuperSpongeProperty.ABSORB_SPHERE);
    }

    public boolean shouldDoBlockUpdates() {
        return (boolean) propertyMap.get(SuperSpongeProperty.BLOCK_UPDATES);
    }

    public boolean shouldClearWater() {
        return (boolean) propertyMap.get(SuperSpongeProperty.CLEAR_WATER);
    }

    public boolean shouldClearLava() {
        return (boolean) propertyMap.get(SuperSpongeProperty.CLEAR_LAVA);
    }

    public int getClearRadius() {
        return (int) propertyMap.get(SuperSpongeProperty.CLEAR_RADIUS);
    }

    public int getBreakDelay() {
        return (int) propertyMap.get(SuperSpongeProperty.BREAK_DELAY);
    }

    public int getPlacementDelay() {
        return (int) propertyMap.get(SuperSpongeProperty.PLACEMENT_DELAY);
    }

    public void load() {
        FileConfiguration f = SuperSponge.getInstance().getDataManager().getConfigFile().getConfig();
        String configSection = "super-sponge.properties";

        propertyMap.put(SuperSpongeProperty.CLEAR_LAVA, f.getBoolean(configSection + ".clear-lava", false));

        propertyMap.put(SuperSpongeProperty.CLEAR_WATER, f.getBoolean(configSection + ".clear-water", false));

        propertyMap.put(SuperSpongeProperty.ABSORB_SPHERE, f.getBoolean(configSection + ".absorb-sphere", false));

        propertyMap.put(SuperSpongeProperty.BLOCK_UPDATES, f.getBoolean(configSection + ".block-updates", false));

        propertyMap.put(SuperSpongeProperty.PLACEMENT_DELAY, f.getInt(configSection + ".placement-delay", 5));

        propertyMap.put(SuperSpongeProperty.BREAK_DELAY, f.getInt(configSection + ".break-delay", 5));

        propertyMap.put(SuperSpongeProperty.CLEAR_RADIUS, f.getInt(configSection + ".clear-radius", 7));

        SuperSponge.getInstance().getDebugger().sendDebugInfo("Successfully cached super sponge properties from data file.");
    }

    private enum SuperSpongeProperty {

        /** Should lava be cleared */
        CLEAR_LAVA,

        /** Should water be cleared */
        CLEAR_WATER,

        /** Should water/lava be cleared in a spherical shape */
        ABSORB_SPHERE,

        /** Should super sponges update surrounding blocks */
        BLOCK_UPDATES,

        /** Delay between placing super sponges */
        PLACEMENT_DELAY,

        /** Delay between breaking super sponges */
        BREAK_DELAY,

        /** Radius of which super sponges clear */
        CLEAR_RADIUS,

    }


}

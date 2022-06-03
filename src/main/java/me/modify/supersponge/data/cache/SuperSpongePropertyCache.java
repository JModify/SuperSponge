package me.modify.supersponge.data.cache;

import com.modify.fundamentum.text.PlugLogger;
import lombok.Getter;
import me.modify.supersponge.SuperSponge;
import me.modify.supersponge.objects.SuperSpongeProperty;
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

    public void loadFromFile() {
        FileConfiguration f = SuperSponge.getInstance().getDataManager().getConfigFile().getConfig();
        String configSection = "super-sponge.properties";

        if (!f.isSet(configSection + ".clear-lava")) {
            PlugLogger.logError("Failed to retrieve super sponge property 'clear-lava'. Corrupted config file?");
            propertyMap.put(SuperSpongeProperty.CLEAR_LAVA, false);
        }
        propertyMap.put(SuperSpongeProperty.CLEAR_LAVA, f.getBoolean(configSection + ".clear-lava"));

        if (!f.isSet(configSection + ".clear-water")) {
            PlugLogger.logError("Failed to retrieve super sponge property 'clear-water'. Corrupted config file?");
            propertyMap.put(SuperSpongeProperty.CLEAR_WATER, false);
        }
        propertyMap.put(SuperSpongeProperty.CLEAR_WATER, f.getBoolean(configSection + ".clear-water"));

        if (!f.isSet(configSection + ".absorb-sphere")) {
            PlugLogger.logError("Failed to retrieve super sponge property 'absorb-sphere'. Corrupted config file?");
            propertyMap.put(SuperSpongeProperty.ABSORB_SPHERE, false);
        }
        propertyMap.put(SuperSpongeProperty.ABSORB_SPHERE, f.getBoolean(configSection + ".absorb-sphere"));

        if (!f.isSet(configSection + ".placement-delay")) {
            PlugLogger.logError("Failed to retrieve super sponge property 'placement-delay'. Corrupted config file?");
            propertyMap.put(SuperSpongeProperty.PLACEMENT_DELAY, 0);
        }
        propertyMap.put(SuperSpongeProperty.PLACEMENT_DELAY, f.getInt(configSection + ".placement-delay"));

        if (!f.isSet(configSection + ".break-delay")) {
            PlugLogger.logError("Failed to retrieve super sponge property 'break-delay'. Corrupted config file?");
            propertyMap.put(SuperSpongeProperty.BREAK_DELAY, 0);
        }
        propertyMap.put(SuperSpongeProperty.BREAK_DELAY, f.getInt(configSection + ".break-delay"));

        if (!f.isSet(configSection + ".clear-radius")) {
            PlugLogger.logError("Failed to retrieve super sponge property 'clear-radius'. Corrupted config file?");
            propertyMap.put(SuperSpongeProperty.CLEAR_RADIUS, 7);
        }
        propertyMap.put(SuperSpongeProperty.CLEAR_RADIUS, f.getInt(configSection + ".clear-radius"));

        SuperSponge.getInstance().getDebugger().sendDebugInfo("Successfully cached super sponge properties from data file.");
    }
}

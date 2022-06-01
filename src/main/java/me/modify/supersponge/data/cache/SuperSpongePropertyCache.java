package me.modify.supersponge.data.cache;

import com.modify.fundamentum.text.PlugLogger;
import lombok.Getter;
import me.modify.supersponge.SuperSponge;
import org.bukkit.configuration.file.FileConfiguration;

public class SuperSpongePropertyCache {

    @Getter private int placementDelay;

    @Getter private int breakDelay;

    @Getter private int clearRadius;

    private boolean clearLava;

    private boolean clearWater;

    public SuperSpongePropertyCache() {
        loadFromFile();
    }

    public boolean shouldClearLava() {
        return clearLava;
    }

    public boolean shouldClearWater() {
        return clearWater;
    }

    private void loadFromFile() {
        FileConfiguration f = SuperSponge.getInstance().getDataManager().getConfigFile().getConfig();

        String configSection = "super-sponge.properties";

        if (!f.isSet(configSection + ".clear-lava")) {
            PlugLogger.logError("Failed to retrieve super sponge property 'clear-lava'. Corrupted config file?");
            this.clearLava = false;
        }
        this.clearLava = f.getBoolean(configSection + ".clear-lava");

        if (!f.isSet(configSection + ".clear-water")) {
            PlugLogger.logError("Failed to retrieve super sponge property 'clear-water'. Corrupted config file?");
            this.clearWater = false;
        }
        this.clearWater = f.getBoolean(configSection + ".clear-water");

        if (!f.isSet(configSection + ".placement-delay")) {
            PlugLogger.logError("Failed to retrieve super sponge property 'placement-delay'. Corrupted config file?");
            this.placementDelay = 0;
        }
        this.placementDelay = f.getInt(configSection + ".placement-delay");

        if (!f.isSet(configSection + ".break-delay")) {
            PlugLogger.logError("Failed to retrieve super sponge property 'break-delay'. Corrupted config file?");
            this.breakDelay = 0;
        }
        this.breakDelay = f.getInt(configSection + ".break-delay");

        if (!f.isSet(configSection + ".clear-radius")) {
            PlugLogger.logError("Failed to retrieve super sponge property 'clear-radius'. Corrupted config file?");
            this.clearRadius = 5;
        }
        this.clearRadius = f.getInt(configSection + ".clear-radius");
    }
}

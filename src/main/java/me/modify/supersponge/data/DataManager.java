package me.modify.supersponge.data;

import com.modify.fundamentum.config.Config;
import com.modify.fundamentum.text.PlugLogger;
import lombok.Getter;
import me.modify.supersponge.SuperSponge;
import me.modify.supersponge.managers.SuperSpongeManager;

import java.io.InputStream;

/**
 * DataManager class represents the management of all data that is to
 * be read/written internally for the plugin.
 */
public class DataManager {

    /** Config.yml file, stores configurable plugin settings */
    @Getter private Config configFile;

    /** Data.yml file, save and store data used in the plugin */
    @Getter private Config dataFile;

    /**
     * Initializes new Config objects and copies defaults for these
     * objects if a physical file does not already exist. Also clears
     * configuration caches.
     */
    public void initialize() {
        Config.cache.clear();

        configFile = new Config("config");
        copyDefault(configFile);

        dataFile = new Config("data");
        copyDefault(dataFile);
    }

    /**
     * Copy default configuration file from resource to plugin data folder.
     * @param file configuration file to copy
     */
    private void copyDefault(Config file) {
        if (!file.exists()) {
            SuperSponge plugin = SuperSponge.getInstance();
            InputStream stream = plugin.getResource(file.getName() + ".yml");
            Config.copy(stream, file.getFile());

            SuperSponge.getInstance().getDebugger().sendDebugInfo(file.getName() + ".yml file does not exist. Copying defaults.");
        }
    }

    public Config getConfigFile() {
        if (!configFile.exists()) {
            copyDefault(configFile);
        }
        return configFile;
    }

    public Config getDataFile() {
        if (!dataFile.exists()) {
            copyDefault(dataFile);
        }
        return dataFile;
    }

    /**
     * Reloads all configuration files
     */
    public void reloadConfigurations() {
        initialize();

        SuperSpongeManager manager = SuperSponge.getInstance().getSuperSpongeManager();
        manager.save();
        manager.load();
    }

}

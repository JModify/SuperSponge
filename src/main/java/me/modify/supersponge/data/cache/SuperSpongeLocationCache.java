package me.modify.supersponge.data.cache;

import com.modify.fundamentum.config.Config;
import com.modify.fundamentum.text.PlugLogger;
import me.modify.supersponge.SuperSponge;
import me.modify.supersponge.exceptions.SpongeLocationParseException;
import me.modify.supersponge.exceptions.WorldNotFoundException;
import me.modify.supersponge.objects.SuperSpongeLocation;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SuperSpongeLocationCache {

    private Set<SuperSpongeLocation> cache;

    public SuperSpongeLocationCache() {
        this.cache = new HashSet<>();
    }

    public void removeLocation(SuperSpongeLocation location) {
        cache.remove(location);
    }

    public void addLocation(Location location) {
        SuperSpongeLocation superSpongeLocation = SuperSpongeLocation.fromBukkitLocation(location);
        cache.add(superSpongeLocation);
    }

    public boolean isSuperSpongeLocation(SuperSpongeLocation location) {
        return cache.contains(location);
    }

    public void load() {
        Config dataFile = SuperSponge.getInstance().getDataManager().getDataFile();
        List<String> locations = dataFile.getConfig().getStringList("super-sponge-locations");

        for(String loc : locations) {
            try {
                cache.add(SuperSpongeLocation.fromString(loc));
            } catch (SpongeLocationParseException | WorldNotFoundException e) {
                e.printStackTrace();
            }
        }
        PlugLogger.logInfo("Successfully loaded " + locations.size() + " super sponge locations from data.yml file.");
    }

    public void save() {
        Config dataFile = SuperSponge.getInstance().getDataManager().getDataFile();

        List<String> locations = new ArrayList<>();
        for (SuperSpongeLocation loc : cache) {
            locations.add(loc.toString());
        }

        dataFile.getConfig().set("super-sponge-locations", locations);
        dataFile.saveConfig();

        PlugLogger.logInfo("Successfully saved " + locations.size() + " super sponge locations to data.yml file.");
    }

}

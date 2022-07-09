package me.modify.supersponge.hooks;

import com.modify.fundamentum.text.PlugLogger;
import com.modify.fundamentum.util.PlugDebugger;
import me.modify.supersponge.SuperSponge;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.Plugin;

public class CoreProtectHook {

    private boolean hooked = false;

    public void check(){
        if (SuperSponge.getInstance().getServer().getPluginManager().getPlugin("CoreProtect") != null) {
            setHooked(true);
            PlugLogger.logInfo("CoreProtect detected. Plugin successfully hooked.");
        }
    }

    public boolean isHooked() {
        return hooked;
    }

    private void setHooked(boolean hooked) {
        this.hooked = hooked;
    }

    private CoreProtectAPI getCoreProtect() {
        Plugin plugin = SuperSponge.getInstance().getServer().getPluginManager().getPlugin("CoreProtect");

        PlugDebugger debugger = SuperSponge.getInstance().getDebugger();

        // Check that CoreProtect is loaded
        if (!(plugin instanceof CoreProtect)) {
            debugger.sendDebugError("Failed to retrieve CoreProtect API. Incorrect plugin retrieved.");
            return null;
        }

        // Check that the API is enabled
        CoreProtectAPI CoreProtect = ((CoreProtect) plugin).getAPI();
        if (!CoreProtect.isEnabled()) {
            debugger.sendDebugError("Failed to retrieve CoreProtect API. API is not enabled.");
            return null;
        }

        // Check that a compatible version of the API is loaded
        if (CoreProtect.APIVersion() < 9) {
            debugger.sendDebugError("Failed to retrieve CoreProtect API. Invalid API version.");
            return null;
        }

        return CoreProtect;
    }

    public void logBlockRemoval(String user, Location location, Material material, BlockData blockData) {
        PlugDebugger debugger = SuperSponge.getInstance().getDebugger();
        if (hooked) {
            CoreProtectAPI api = getCoreProtect();

            if (api == null) {
                debugger.sendDebugError("Failed to log SuperSponge block update. API is null");
                return;
            }

            if (!api.logRemoval(user, location, material, blockData)) {
                debugger.sendDebugError("Failed to log SuperSponge block update. API might be disabled.");
            }
        }
    }
}

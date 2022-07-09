package me.modify.supersponge;

import com.modify.fundamentum.Fundamentum;
import com.modify.fundamentum.util.PlugDebugger;
import com.modify.fundamentum.util.PlugUtil;
import lombok.Getter;
import lombok.Setter;
import me.modify.supersponge.commands.SuperSpongeCommand;
import me.modify.supersponge.data.DataManager;
import me.modify.supersponge.hooks.CoreProtectHook;
import me.modify.supersponge.listeners.SpongeListener;
import me.modify.supersponge.managers.SuperSpongeManager;
import me.modify.supersponge.timers.SuperSpongeLocationSaveTimer;
import me.modify.supersponge.util.Constants;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SuperSponge extends JavaPlugin {

    @Getter @Setter
    private static SuperSponge instance;

    @Getter
    private DataManager dataManager;

    @Getter
    private SuperSpongeManager superSpongeManager;

    @Getter
    private PlugDebugger debugger;

    @Getter
    private CoreProtectHook coreProtectHook;

    @Override
    public void onEnable() {
        setInstance(this);
        Fundamentum.setPlugin(this);

        initialize();

        registerListeners();
        registerCommands();

        startRepeatingTimers();
    }

    @Override
    public void onDisable() {
        superSpongeManager.save();
    }

    private void initialize() {
        debugger = new PlugDebugger();
        debugger.setDebugMode(true);

        dataManager = new DataManager();
        dataManager.initialize();

        superSpongeManager = new SuperSpongeManager();
        superSpongeManager.load();

        coreProtectHook = new CoreProtectHook();
        coreProtectHook.check();
    }

    private void startRepeatingTimers() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new SuperSpongeLocationSaveTimer(), Constants.FIVE_MINUTES_IN_TICKS, Constants.FIVE_MINUTES_IN_TICKS);
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new SpongeListener(), this);
    }

    private void registerCommands() {
        PlugUtil.registerCommand(new SuperSpongeCommand());
    }


}

package me.modify.supersponge.timers;


import me.modify.supersponge.SuperSponge;

/**
 * Saves all placed super sponge locations from cache to file every 5 minutes.
 * This is used to avoid major loss of data in the event of a server crash.
 */
public class SuperSpongeLocationSaveTimer implements Runnable {

    @Override
    public void run() {
        SuperSponge.getInstance().getSuperSpongeManager().save();
    }


}

package me.modify.supersponge.managers;

import lombok.Getter;
import me.modify.supersponge.SuperSponge;
import me.modify.supersponge.objects.BlockedType;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SuperSpongeDelayManager {

    /**
     * Users who are blocked from breaking super sponges for a certain length of time.
     * Key is the uuid of the user blocked.
     * Time in seconds at which the block began.
     */
    private Map<UUID, Double> blockedPlacement;

    /**
     * Users who are blocked from placing super sponges for a certain length of time.
     * Key is the uuid of the user blocked.
     * Time in seconds at which the block began.
     */
    private Map<UUID, Double> blockedBreaking;

    public SuperSpongeDelayManager() {
        this.blockedPlacement = new HashMap<>();
        this.blockedBreaking = new HashMap<>();
    }

    public void blockUser(UUID user, BlockedType blockedType) {
        switch (blockedType) {
            case BREAKING -> blockedBreaking.put(user, (System.currentTimeMillis() / 1000.0));
            case PLACING -> blockedPlacement.put(user, (System.currentTimeMillis() / 1000.0));
        }
    }

    public boolean shouldDelayPlacements() {
        return SuperSponge.getInstance().getSuperSpongeManager().getSpongeProperties().getPlacementDelay() != -1;
    }

    public boolean shouldDelayBreaking() {
        return SuperSponge.getInstance().getSuperSpongeManager().getSpongeProperties().getBreakDelay() != -1;
    }

    public boolean isBlocked(UUID user, BlockedType blockedType) {
        int breakingDelay = SuperSponge.getInstance().getSuperSpongeManager().getSpongeProperties().getBreakDelay();
        int placementDelay = SuperSponge.getInstance().getSuperSpongeManager().getSpongeProperties().getPlacementDelay();

        switch (blockedType) {

            case BREAKING -> {

                if (!blockedBreaking.containsKey(user)) {
                    return false;
                }
                double blockStart = blockedBreaking.get(user);
                double currentTime = System.currentTimeMillis() / 1000.0;

                if (currentTime - blockStart >= breakingDelay) {
                    blockedBreaking.remove(user);
                    return false;
                }
                return true;
            }
            case PLACING -> {

                if (!blockedPlacement.containsKey(user)) {
                    return false;
                }

                double blockStart = blockedPlacement.get(user);
                double currentTime = System.currentTimeMillis() / 1000.0;

                if (currentTime - blockStart >= placementDelay) {
                    blockedPlacement.remove(user);
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public int getTimeRemaining(UUID user, BlockedType blockedType) {

        int breakingDelay = SuperSponge.getInstance().getSuperSpongeManager().getSpongeProperties().getBreakDelay();
        int placementDelay = SuperSponge.getInstance().getSuperSpongeManager().getSpongeProperties().getPlacementDelay();

        switch(blockedType) {

            case BREAKING -> {
                double blockStart = blockedBreaking.get(user);
                double currentTime = System.currentTimeMillis() / 1000.0;

                return (int) (breakingDelay - (currentTime - blockStart));
            }
            case PLACING -> {
                double blockStart = blockedPlacement.get(user);
                double currentTime = System.currentTimeMillis() / 1000.0;

                return (int) (placementDelay - (currentTime - blockStart));
            }
        }

        return -1;
    }


}

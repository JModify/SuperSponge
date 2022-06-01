package me.modify.supersponge.managers;

import lombok.Getter;
import me.modify.supersponge.objects.BlockedType;

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
}

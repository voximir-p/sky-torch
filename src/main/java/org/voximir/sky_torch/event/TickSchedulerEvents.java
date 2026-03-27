package org.voximir.sky_torch.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.voximir.sky_torch.util.TickScheduler;

public class TickSchedulerEvents {
    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> TickScheduler.tick());
    }
}

package org.voximir.sky_torch.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.voximir.sky_torch.utility.TickScheduler;

public class TickSchedulerEvent {
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> TickScheduler.tick());
    }
}

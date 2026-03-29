package org.voximir.sky_torch.event;

public class ModEvents {
    public static void init() {
        LightningEvent.register();
        GameObjectEvent.register();
        TickSchedulerEvent.register();
    }
}

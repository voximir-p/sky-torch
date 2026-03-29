package org.voximir.sky_torch.event;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import org.voximir.sky_torch.laser.GameObject;

public class GameObjectEvent {
    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            var live = GameObject.getLive();
            for (var gameObject : live) {
                gameObject.update();
            }
            for (var gameObject : live) {
                gameObject.render();
            }
        });
    }
}

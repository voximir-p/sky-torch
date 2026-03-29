package org.voximir.sky_torch.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.voximir.sky_torch.client.networking.S2CPayloadHandler;
import org.voximir.sky_torch.client.render.SkyTorchHudRenderer;
import org.voximir.sky_torch.client.render.SkyTorchRenderState;
import org.voximir.sky_torch.client.render.SkyTorchWorldRenderer;

public class SkyTorchClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        S2CPayloadHandler.init();
        SkyTorchWorldRenderer.register();
        SkyTorchHudRenderer.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level != null) {
                SkyTorchRenderState.tick();
            }
        });
    }
}

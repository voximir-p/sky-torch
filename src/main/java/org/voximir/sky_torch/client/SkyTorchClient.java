package org.voximir.sky_torch.client;

import net.fabricmc.api.ClientModInitializer;
import org.voximir.sky_torch.client.networking.S2CPayloadHandler;

public class SkyTorchClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        S2CPayloadHandler.init();
    }
}

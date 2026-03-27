package org.voximir.sky_torch.client;

import net.fabricmc.api.ClientModInitializer;
import org.voximir.sky_torch.client.networking.ModS2CPayloadHandler;

public class SkyTorchClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModS2CPayloadHandler.init();
    }
}

package org.voximir.sky_torch.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ModNetworking {
    public static void init() {
        PayloadTypeRegistry.playS2C().register(PlayLocalSoundS2CPayload.ID, PlayLocalSoundS2CPayload.CODEC);
    }
}

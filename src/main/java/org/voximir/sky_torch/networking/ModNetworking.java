package org.voximir.sky_torch.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ModNetworking {
    public static void init() {
        PayloadTypeRegistry.playS2C().register(PlayLocalSoundS2CPayload.ID, PlayLocalSoundS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SpawnCloudS2CPayload.ID, SpawnCloudS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SpawnFlyingBlockS2CPayload.ID, SpawnFlyingBlockS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LaserStateS2CPayload.ID, LaserStateS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BlindingEffectS2CPayload.ID, BlindingEffectS2CPayload.CODEC);
    }
}

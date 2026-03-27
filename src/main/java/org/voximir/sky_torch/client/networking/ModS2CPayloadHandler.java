package org.voximir.sky_torch.client.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import org.voximir.sky_torch.networking.PlayLocalSoundS2CPayload;

import java.util.Objects;

public class ModS2CPayloadHandler {
    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(PlayLocalSoundS2CPayload.ID, (payload, context) ->
            context.client().execute(() -> {
                var soundEvent = BuiltInRegistries.SOUND_EVENT.getValue(payload.soundEventId());
                if (soundEvent != null) {
                    Objects.requireNonNull(context.client().level).playPlayerSound(
                            soundEvent,
                            payload.soundSource(),
                            payload.volume(),
                            payload.pitch()
                    );
                }
            })
        );
    }
}

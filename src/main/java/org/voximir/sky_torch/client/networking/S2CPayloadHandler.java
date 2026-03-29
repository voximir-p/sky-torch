package org.voximir.sky_torch.client.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import org.voximir.sky_torch.client.render.ClientBlindingOverlay;
import org.voximir.sky_torch.client.render.ClientCloud;
import org.voximir.sky_torch.client.render.ClientFlyingBlock;
import org.voximir.sky_torch.client.render.SkyTorchRenderState;
import org.voximir.sky_torch.networking.BlindingEffectS2CPayload;
import org.voximir.sky_torch.networking.LaserStateS2CPayload;
import org.voximir.sky_torch.networking.PlayLocalSoundS2CPayload;
import org.voximir.sky_torch.networking.SpawnCloudS2CPayload;
import org.voximir.sky_torch.networking.SpawnFlyingBlockS2CPayload;

import java.util.Objects;

public class S2CPayloadHandler {
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

        ClientPlayNetworking.registerGlobalReceiver(SpawnCloudS2CPayload.ID, (payload, context) ->
            context.client().execute(() -> {
                SkyTorchRenderState.addCloud(new ClientCloud(
                        payload.x(), payload.y(), payload.z(),
                        payload.vx(), payload.vy(), payload.vz(),
                        payload.size(), payload.growth(), payload.maxAge(),
                        payload.pitch(), payload.yaw(),
                        payload.pitchVelocity(), payload.yawVelocity(),
                        payload.blockStateIds()
                ));
            })
        );

        ClientPlayNetworking.registerGlobalReceiver(SpawnFlyingBlockS2CPayload.ID, (payload, context) ->
            context.client().execute(() -> {
                SkyTorchRenderState.addFlyingBlock(new ClientFlyingBlock(
                        payload.x(), payload.y(), payload.z(),
                        payload.vx(), payload.vy(), payload.vz(),
                        payload.blockStateId(),
                        payload.initialPitch(), payload.initialYaw(),
                        payload.rotateAxisX(), payload.rotateAxisY(), payload.rotateAxisZ(),
                        payload.rotateSpeed()
                ));
            })
        );

        ClientPlayNetworking.registerGlobalReceiver(LaserStateS2CPayload.ID, (payload, context) ->
            context.client().execute(() -> {
                SkyTorchRenderState.updateLaser(
                        payload.laserId(),
                        new net.minecraft.world.phys.Vec3(payload.tipX(), payload.tipY(), payload.tipZ()),
                        new net.minecraft.world.phys.Vec3(payload.endX(), payload.endY(), payload.endZ()),
                        payload.beamWidth(), payload.glowWidth(),
                        payload.active()
                );
            })
        );

        ClientPlayNetworking.registerGlobalReceiver(BlindingEffectS2CPayload.ID, (payload, context) ->
            context.client().execute(() -> {
                SkyTorchRenderState.setBlindingOverlay(new ClientBlindingOverlay(
                        payload.fadeInDuration(), payload.duration(), payload.fadeOutDuration()
                ));
            })
        );
    }
}

package org.voximir.sky_torch.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import static org.voximir.sky_torch.SkyTorch.MOD_ID;

public record SpawnFlyingBlockS2CPayload(
        double x, double y, double z,
        double vx, double vy, double vz,
        int blockStateId,
        float initialPitch, float initialYaw,
        float rotateAxisX, float rotateAxisY, float rotateAxisZ,
        double rotateSpeed
) implements CustomPacketPayload {

    public static final Type<SpawnFlyingBlockS2CPayload> ID =
            new Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "spawn_flying_block"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpawnFlyingBlockS2CPayload> CODEC = new StreamCodec<>() {
        @Override
        public @NonNull SpawnFlyingBlockS2CPayload decode(RegistryFriendlyByteBuf buf) {
            return new SpawnFlyingBlockS2CPayload(
                    buf.readDouble(), buf.readDouble(), buf.readDouble(),
                    buf.readDouble(), buf.readDouble(), buf.readDouble(),
                    buf.readVarInt(),
                    buf.readFloat(), buf.readFloat(),
                    buf.readFloat(), buf.readFloat(), buf.readFloat(),
                    buf.readDouble()
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SpawnFlyingBlockS2CPayload p) {
            buf.writeDouble(p.x());
            buf.writeDouble(p.y());
            buf.writeDouble(p.z());
            buf.writeDouble(p.vx());
            buf.writeDouble(p.vy());
            buf.writeDouble(p.vz());
            buf.writeVarInt(p.blockStateId());
            buf.writeFloat(p.initialPitch());
            buf.writeFloat(p.initialYaw());
            buf.writeFloat(p.rotateAxisX());
            buf.writeFloat(p.rotateAxisY());
            buf.writeFloat(p.rotateAxisZ());
            buf.writeDouble(p.rotateSpeed());
        }
    };

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}

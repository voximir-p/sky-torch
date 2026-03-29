package org.voximir.sky_torch.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import static org.voximir.sky_torch.SkyTorch.MOD_ID;

public record LaserStateS2CPayload(
        int laserId,
        double tipX, double tipY, double tipZ,
        double endX, double endY, double endZ,
        float beamWidth, float glowWidth,
        boolean active
) implements CustomPacketPayload {

    public static final Type<LaserStateS2CPayload> ID =
            new Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "laser_state"));

    public static final StreamCodec<RegistryFriendlyByteBuf, LaserStateS2CPayload> CODEC = new StreamCodec<>() {
        @Override
        public @NonNull LaserStateS2CPayload decode(RegistryFriendlyByteBuf buf) {
            return new LaserStateS2CPayload(
                    buf.readVarInt(),
                    buf.readDouble(), buf.readDouble(), buf.readDouble(),
                    buf.readDouble(), buf.readDouble(), buf.readDouble(),
                    buf.readFloat(), buf.readFloat(),
                    buf.readBoolean()
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, LaserStateS2CPayload p) {
            buf.writeVarInt(p.laserId());
            buf.writeDouble(p.tipX());
            buf.writeDouble(p.tipY());
            buf.writeDouble(p.tipZ());
            buf.writeDouble(p.endX());
            buf.writeDouble(p.endY());
            buf.writeDouble(p.endZ());
            buf.writeFloat(p.beamWidth());
            buf.writeFloat(p.glowWidth());
            buf.writeBoolean(p.active());
        }
    };

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}

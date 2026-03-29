package org.voximir.sky_torch.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import static org.voximir.sky_torch.SkyTorch.MOD_ID;

public record BlindingEffectS2CPayload(
        int fadeInDuration,
        int duration,
        int fadeOutDuration
) implements CustomPacketPayload {

    public static final Type<BlindingEffectS2CPayload> ID =
            new Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "blinding_effect"));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlindingEffectS2CPayload> CODEC = new StreamCodec<>() {
        @Override
        public @NonNull BlindingEffectS2CPayload decode(RegistryFriendlyByteBuf buf) {
            return new BlindingEffectS2CPayload(
                    buf.readVarInt(),
                    buf.readVarInt(),
                    buf.readVarInt()
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, BlindingEffectS2CPayload p) {
            buf.writeVarInt(p.fadeInDuration());
            buf.writeVarInt(p.duration());
            buf.writeVarInt(p.fadeOutDuration());
        }
    };

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}

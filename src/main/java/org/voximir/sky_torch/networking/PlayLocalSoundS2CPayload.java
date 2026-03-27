package org.voximir.sky_torch.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import org.jspecify.annotations.NonNull;

import static org.voximir.sky_torch.SkyTorch.MOD_ID;

public record PlayLocalSoundS2CPayload(Identifier soundEventId, SoundSource soundSource, float volume, float pitch) implements CustomPacketPayload {
    public static final Type<PlayLocalSoundS2CPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "play_local_sound"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayLocalSoundS2CPayload> CODEC = new StreamCodec<>() {
        @Override
        public @NonNull PlayLocalSoundS2CPayload decode(RegistryFriendlyByteBuf buf) {
            var soundEventId = buf.readIdentifier();
            var soundSource = buf.readEnum(SoundSource.class);
            var volume = buf.readFloat();
            var pitch = buf.readFloat();
            return new PlayLocalSoundS2CPayload(soundEventId, soundSource, volume, pitch);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, PlayLocalSoundS2CPayload payload) {
            buf.writeIdentifier(payload.soundEventId());
            buf.writeEnum(payload.soundSource());
            buf.writeFloat(payload.volume());
            buf.writeFloat(payload.pitch());
        }
    };

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}

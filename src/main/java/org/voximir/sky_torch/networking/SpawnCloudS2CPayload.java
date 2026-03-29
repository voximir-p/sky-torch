package org.voximir.sky_torch.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import static org.voximir.sky_torch.SkyTorch.MOD_ID;

public record SpawnCloudS2CPayload(
        double x, double y, double z,
        double vx, double vy, double vz,
        double size, double growth,
        int maxAge,
        float pitch, float yaw,
        float pitchVelocity, float yawVelocity,
        List<Integer> blockStateIds
) implements CustomPacketPayload {

    public static final Type<SpawnCloudS2CPayload> ID =
            new Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "spawn_cloud"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpawnCloudS2CPayload> CODEC = new StreamCodec<>() {
        @Override
        public @NonNull SpawnCloudS2CPayload decode(RegistryFriendlyByteBuf buf) {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            double vx = buf.readDouble();
            double vy = buf.readDouble();
            double vz = buf.readDouble();
            double size = buf.readDouble();
            double growth = buf.readDouble();
            int maxAge = buf.readVarInt();
            float pitch = buf.readFloat();
            float yaw = buf.readFloat();
            float pitchVelocity = buf.readFloat();
            float yawVelocity = buf.readFloat();
            int count = buf.readVarInt();
            List<Integer> ids = new ArrayList<>(count);
            for (int i = 0; i < count; i++) {
                ids.add(buf.readVarInt());
            }
            return new SpawnCloudS2CPayload(x, y, z, vx, vy, vz, size, growth, maxAge,
                    pitch, yaw, pitchVelocity, yawVelocity, ids);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, SpawnCloudS2CPayload p) {
            buf.writeDouble(p.x());
            buf.writeDouble(p.y());
            buf.writeDouble(p.z());
            buf.writeDouble(p.vx());
            buf.writeDouble(p.vy());
            buf.writeDouble(p.vz());
            buf.writeDouble(p.size());
            buf.writeDouble(p.growth());
            buf.writeVarInt(p.maxAge());
            buf.writeFloat(p.pitch());
            buf.writeFloat(p.yaw());
            buf.writeFloat(p.pitchVelocity());
            buf.writeFloat(p.yawVelocity());
            buf.writeVarInt(p.blockStateIds().size());
            for (int id : p.blockStateIds()) {
                buf.writeVarInt(id);
            }
        }
    };

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return ID;
    }
}

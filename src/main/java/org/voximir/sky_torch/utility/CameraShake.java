package org.voximir.sky_torch.utility;

import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec3;

import java.util.Set;

public class CameraShake {

    public static class CameraShakeOptions {
        public double magnitude;
        public double decay;
        public double pitchPeriod;
        public double yawPeriod;

        public CameraShakeOptions(double magnitude, double decay, double pitchPeriod, double yawPeriod) {
            this.magnitude = magnitude;
            this.decay = decay;
            this.pitchPeriod = pitchPeriod;
            this.yawPeriod = yawPeriod;
        }
    }

    public CameraShake(ServerPlayer player, CameraShakeOptions options) {
        final int[] time = {0};
        final double[] magnitude = {options.magnitude};
        final double[] prevPitch = {0.0};
        final double[] prevYaw = {0.0};

        // Schedule repeating ticks via a self-rescheduling lambda.
        Runnable[] tickRef = new Runnable[1];
        tickRef[0] = () -> {
            time[0]++;
            magnitude[0] -= options.decay;

            if (magnitude[0] < 0 || player.isRemoved()) {
                return;
            }

            double pitch = Math.sin((double) time[0] / options.pitchPeriod * 2 * Math.PI) * magnitude[0];
            double yaw = Math.cos((double) time[0] / options.yawPeriod * 2 * Math.PI) * magnitude[0];

            double relativePitch = pitch - prevPitch[0];
            double relativeYaw = yaw - prevYaw[0];
            prevPitch[0] = pitch;
            prevYaw[0] = yaw;

            // Use the vanilla position packet with relative pitch/yaw flags to avoid teleporting.
            player.connection.send(new ClientboundPlayerPositionPacket(
                    player.getId(),
                    new PositionMoveRotation(Vec3.ZERO, Vec3.ZERO, (float) relativeYaw, (float) relativePitch),
                    Set.of(
                            Relative.X,
                            Relative.Y,
                            Relative.Z,
                            Relative.X_ROT,
                            Relative.Y_ROT
                    )
            ));

            TickScheduler.schedule(1, tickRef[0]);
        };

        TickScheduler.schedule(1, tickRef[0]);
    }
}

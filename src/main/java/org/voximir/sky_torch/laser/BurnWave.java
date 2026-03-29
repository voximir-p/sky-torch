package org.voximir.sky_torch.laser;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.voximir.sky_torch.utility.GameUtil;
import org.voximir.sky_torch.utility.MathUtil;
import org.voximir.sky_torch.utility.TickScheduler;

import java.util.concurrent.ThreadLocalRandom;

public final class BurnWave {
    private BurnWave() {
    }

    public static class BurnWaveOptions {
        public int count = 20;
        public int delayedCount = 90;
        public int durationMin = (int) (4.0 * 20);
        public int durationMax = (int) (8.0 * 20);
        public long maxSpawnDelay = 35L;
        public int spawnRandomInterval = 5;
        public double growth = 0.25;
        public double size = 0.1;
        public int burnBlocks = 4;
        public double speedMin = 0.5 * 1.5;
        public double speedMax = 0.55 * 1.5;
        public BlockPalette palette = BlockPalette.ORANGE;
        public double scanUpwards = 25.0;
        public double scanDownwards = 0.0;
        public double airDragCoefficient = 0.005;
        public boolean disableHeat = false;
    }

    public static class BurnWavePlacement {
        public final ServerLevel level;
        public final Vec3 origin;
        public final Vec3 render;

        public BurnWavePlacement(ServerLevel level, Vec3 origin, Vec3 render) {
            this.level = level;
            this.origin = origin;
            this.render = render;
        }
    }

    public static void spawnBurnWave(BurnWavePlacement placement, BurnWaveOptions options, Burner burner) {
        for (GameUtil.XYPair p : GameUtil.ring(options.count)) {
            spawnBurnCloud(placement, options, burner, p.x(), p.y());
        }

        for (GameUtil.XYPair p : GameUtil.ring(options.delayedCount)) {
            long delay = ThreadLocalRandom.current().nextLong(
                    options.maxSpawnDelay / options.spawnRandomInterval + 1)
                    * options.spawnRandomInterval;
            TickScheduler.schedule((int) delay, () ->
                    spawnBurnCloud(placement, options, burner, p.x(), p.y()));
        }
    }

    private static Cloud spawnBurnCloud(BurnWavePlacement placement, BurnWaveOptions options,
                                        Burner burner, double x, double y) {
        double speed = ThreadLocalRandom.current().nextDouble(options.speedMin, options.speedMax);
        Vec3 velocity = new Vec3(x, 0.1, y).normalize().scale(speed);

        var burnWaveList = options.palette.burn.burnWave;
        var blocks = burnWaveList.get(ThreadLocalRandom.current().nextInt(burnWaveList.size()));
        int duration = ThreadLocalRandom.current().nextInt(options.durationMin, options.durationMax);

        Cloud cloud = new Cloud(
                placement.level,
                placement.origin,
                placement.render,
                velocity,
                options.size,
                options.growth,
                duration,
                blocks
        );

        cloud.onUpdate = () -> {
            cloud.velocity = cloud.velocity.scale(1 - options.airDragCoefficient);

            double scanUp = cloud.size / 2 + options.scanUpwards;
            double scanDown = cloud.size / 2 + options.scanDownwards;

            // Re-run raycast only every 2 ticks; reuse cached ground on off-ticks.
            if (cloud.cachedGround == null || cloud.groundAge % 2 == 0) {
                Vec3 scanTop = new Vec3(cloud.location.x, cloud.location.y + scanUp, cloud.location.z);
                BlockHitResult groundHit = GameUtil.raycastGround(
                        placement.level, scanTop, new Vec3(0, -1, 0), scanUp + scanDown);
                cloud.cachedGround = (groundHit != null) ? groundHit.getLocation() : cloud.location;
            }
            cloud.groundAge++;
            Vec3 ground = cloud.cachedGround;

            BlockPos cloudPos = GameUtil.toBlockPos(cloud.location);
            if (placement.level.getBlockState(cloudPos).isSolid()) {
                double newY = MathUtil.lerp(cloud.location.y, ground.y + cloud.size / 2.0, 0.03);
                cloud.location = new Vec3(cloud.location.x, newY, cloud.location.z);
            }

            double burnHeight = Math.max(cloud.size * 0.7, 5.0);
            double burnAmount = Math.max(cloud.size, 1.0) * options.burnBlocks;
            for (int i = 0; i < (int) burnAmount; i++) {
                int dx = ThreadLocalRandom.current().nextInt(-3, 3);
                int dy = (int) ThreadLocalRandom.current().nextDouble(-burnHeight, burnHeight);
                int dz = ThreadLocalRandom.current().nextInt(-3, 3);
                BlockPos burnPos = GameUtil.toBlockPos(ground).offset(dx, dy, dz);
                float heat = options.disableHeat ? 0.5f : 1f - ((float) cloud.age / cloud.maxAge);
                burner.burn(placement.level, burnPos, placement.render, heat);
            }
        };

        return cloud;
    }
}

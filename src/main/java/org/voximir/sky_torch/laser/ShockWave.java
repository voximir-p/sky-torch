package org.voximir.sky_torch.laser;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.voximir.sky_torch.utility.CameraShake;
import org.voximir.sky_torch.utility.GameUtil;
import org.voximir.sky_torch.utility.SeriesScheduler;
import org.voximir.sky_torch.utility.SetBlockUndo;
import org.voximir.sky_torch.utility.TickScheduler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public final class ShockWave {
    private ShockWave() {
    }

    public static class ShockWaveOptions {
        public int count = 120;
        public CameraShake.CameraShakeOptions cameraShake = new CameraShake.CameraShakeOptions(1.2, 0.04, 3.7, 3.0);
        public double destructionRadius = 3.8;
        public int destructionCurve = 3;
        public double destructionRange = 20.0;
        public double flyingBlockChance = 0.1;
        public double flyingBlockMinVelocity = 2.7 * 1.2;
        public double flyingBlockMaxVelocity = 3.0 * 1.2;
        public double size = 0.1;
        public double growth = 0.125;
        public int duration = 4 * 20;
        public double speed = 3.2;
        public double scanUpwards = 15.0;
    }

    public static class ShockWavePlacement {
        public final ServerLevel level;
        public final Vec3 origin;
        public final Vec3 render;

        public ShockWavePlacement(ServerLevel level, Vec3 origin, Vec3 render) {
            this.level = level;
            this.origin = origin;
            this.render = render;
        }
    }

    private static final List<BlockState> PALETTE = List.of(
            Blocks.WHITE_STAINED_GLASS.defaultBlockState(),
            Blocks.LIGHT_GRAY_STAINED_GLASS.defaultBlockState()
    );

    public static void spawnShockwave(ShockWavePlacement placement, ShockWaveOptions options, Burner burner) {
        Set<ServerPlayer> visitedPlayers = new HashSet<>();

        for (GameUtil.XYPair pair : GameUtil.ring(options.count)) {
            Vec3 cloudVelocity = new Vec3(pair.x(), 0.02, pair.y()).scale(options.speed);
            BlockState paletteEntry = PALETTE.get(ThreadLocalRandom.current().nextInt(PALETTE.size()));

            Cloud cloud = new Cloud(
                    placement.level,
                    placement.origin,
                    placement.render,
                    cloudVelocity,
                    options.size,
                    options.growth,
                    options.duration,
                    List.of(paletteEntry)
            );

            cloud.onUpdate = () -> {
                Vec3 nextPos = cloud.location.add(cloud.velocity);
                double radiusSquared = nextPos.distanceToSqr(placement.origin);

                // Camera shake — trigger once as shockwave passes each player.
                for (ServerPlayer player : placement.level.players()) {
                    if (visitedPlayers.contains(player)) continue;
                    if (player.distanceToSqr(placement.origin) > radiusSquared) continue;
                    visitedPlayers.add(player);
                    new CameraShake(player, options.cameraShake);
                }

                // Find ground below cloud.
                Vec3 scanTop = new Vec3(cloud.location.x, cloud.location.y + options.scanUpwards, cloud.location.z);
                BlockHitResult groundHit = GameUtil.raycastGround(
                        placement.level, scanTop, new Vec3(0, -1, 0), options.scanUpwards);
                Vec3 ground = (groundHit != null) ? groundHit.getLocation() : cloud.location;

                // Stay on ground if inside occluding block.
                BlockPos cloudPos = GameUtil.toBlockPos(cloud.location);
                if (placement.level.getBlockState(cloudPos).isSolid()) {
                    double newY = org.voximir.sky_torch.utility.MathUtil.lerp(cloud.location.y, ground.y + cloud.size / 2.0, 0.1);
                    cloud.location = new Vec3(cloud.location.x, newY, cloud.location.z);
                }

                // Destruction.
                double radiusFraction = radiusSquared / Math.pow(options.destructionRange, 2);
                if (radiusFraction > 1) return;

                int removeRadius = (int) Math.round(
                        Math.pow(1 - radiusFraction, options.destructionCurve) * options.destructionRadius);
                removeRadius = Math.max(removeRadius, options.destructionRadius == 0 ? 0 : 1);
                if (removeRadius == 0) return;

                BlockPos groundPos = GameUtil.toBlockPos(ground);
                for (org.joml.Vector3i offset : GameUtil.sphereBlockOffsets(removeRadius)) {
                    BlockPos blockPos = groundPos.offset(offset.x, offset.y, offset.z);
                    BlockState blockState = placement.level.getBlockState(blockPos);

                    if (blockState.isAir() || blockState.getFluidState().isSource()) continue;

                    if (ThreadLocalRandom.current().nextFloat() < options.flyingBlockChance) {
                        spawnFlyingBlock(
                                placement.level, blockPos, blockState, cloudVelocity,
                                radiusFraction, placement.render,
                                burner.options.palette.burn, options);
                    }
                    removeBlock(placement.level, blockPos, blockState);
                }
            };
        }
    }

    private static void removeBlock(ServerLevel level, BlockPos pos, BlockState state) {
        if (state.getFluidState().is(net.minecraft.world.level.material.Fluids.WATER)) {
            SetBlockUndo.setBlock(level, pos, Blocks.WATER.defaultBlockState());
        } else {
            SetBlockUndo.setBlock(level, pos, Blocks.AIR.defaultBlockState());
        }
    }

    private static void spawnFlyingBlock(ServerLevel level, BlockPos pos, BlockState state,
                                         Vec3 shockWaveVelocity, double radiusFraction,
                                         Vec3 renderLocation, BurnPalette burnPalette,
                                         ShockWaveOptions options) {
        List<BurnPalette.Pair<Long, BlockState>> palette = burnPalette.burn(level, pos, 1.0f);
        if (palette == null) {
            palette = List.of(new BurnPalette.Pair<>(0L, state));
        }

        double power = 1.0 - radiusFraction;
        double speed = ThreadLocalRandom.current().nextDouble(
                options.flyingBlockMinVelocity, options.flyingBlockMaxVelocity)
                * Math.pow(power, 2);
        Vec3 velocity = new Vec3(
                shockWaveVelocity.x,
                shockWaveVelocity.length() * power,
                shockWaveVelocity.z
        ).normalize().scale(speed);

        List<BurnPalette.Pair<Long, BlockState>> finalPalette = palette;

        long delay = ThreadLocalRandom.current().nextLong(5);
        TickScheduler.schedule((int) delay, () -> {
            FlyingBlock flying = new FlyingBlock(
                    level, renderLocation, Vec3.atCenterOf(pos), velocity, state);
            SeriesScheduler series = new SeriesScheduler();
            for (BurnPalette.Pair<Long, BlockState> entry : finalPalette) {
                series.sleep((int) (long) entry.first());
                series.run(() -> flying.blockState = entry.second());
            }
        });
    }
}

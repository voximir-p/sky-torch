package org.voximir.sky_torch.laser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.voximir.sky_torch.utility.MathUtil;
import org.voximir.sky_torch.utility.SeriesScheduler;
import org.voximir.sky_torch.utility.TickScheduler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Burner {

    public static class BurnOptions {
        public boolean disabled = false;
        public double setFireChance = 1.0 / 80;
        public double spawnSmokeChance = 0.2;
        public BlockPalette palette = BlockPalette.ORANGE;
    }

    public final BurnOptions options;
    public boolean isClosed = false;
    public final Set<BlockPos> visited = new HashSet<>();
    public final Map<BlockPos, BlockState> burning = new HashMap<>();

    public Burner(BurnOptions options) {
        this.options = options;
    }

    public void close() {
        isClosed = true;
    }

    public void burn(ServerLevel level, BlockPos pos, Vec3 renderLocation, float heat) {
        BurnPalette palette = options.palette.burn;

        if (visited.contains(pos)) {
            return;
        }
        if (isOccluded(level, pos)) {
            return;
        }
        visited.add(pos.immutable());

        if (options.disabled) {
            return;
        }

        BlockState state = level.getBlockState(pos);

        if (state.getFluidState().isSource()) {
            return;
        }

        // Air block: maybe place fire.
        if (state.isAir()) {
            BlockPos below = pos.below();
            if (canPlaceFireOn(level, below)) {
                if (ThreadLocalRandom.current().nextDouble() > options.setFireChance) {
                    return;
                }
                BlockState fireState = palette.firePalette.get(
                        ThreadLocalRandom.current().nextInt(palette.firePalette.size()));
                org.voximir.sky_torch.utility.SetBlockUndo.setBlock(level, pos, fireState);
                BlockPos immutablePos = pos.immutable();
                TickScheduler.schedule(20 * 10, () -> {
                    if (isClosed) return;
                    if (!canPlaceFireOn(level, immutablePos.below())) {
                        org.voximir.sky_torch.utility.SetBlockUndo.setBlock(level, immutablePos,
                                Blocks.AIR.defaultBlockState());
                    }
                });
            }
            return;
        }

        // Non-air: perform staged burn.
        List<BurnPalette.Pair<Long, BlockState>> burnPalette = palette.burn(level, pos, heat);
        if (burnPalette == null) {
            return;
        }

        BlockPos immutablePos = pos.immutable();
        burning.put(immutablePos, state);

        SeriesScheduler series = new SeriesScheduler();
        for (BurnPalette.Pair<Long, BlockState> entry : burnPalette) {
            series.sleep((int) (long) entry.first());
            series.run(() -> {
                if (isClosed) return;
                if (level.getBlockState(immutablePos).isAir()) return;
                org.voximir.sky_torch.utility.SetBlockUndo.setBlock(level, immutablePos, entry.second());
            });
        }

        if (ThreadLocalRandom.current().nextDouble() < options.spawnSmokeChance) {
            spawnSmokeCloud(level, BlockPos.containing(pos.getCenter()), renderLocation, palette);
        }
    }

    private boolean isOccluded(ServerLevel level, BlockPos pos) {
        return isOccluding(level, pos.above())
                && isOccluding(level, pos.below())
                && isOccluding(level, pos.east())
                && isOccluding(level, pos.west())
                && isOccluding(level, pos.south())
                && isOccluding(level, pos.north());
    }

    private boolean isOccluding(ServerLevel level, BlockPos pos) {
        BlockState state = burning.getOrDefault(pos, level.getBlockState(pos));
        return state.isSolidRender();
    }

    private static boolean canPlaceFireOn(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isFaceSturdy(level, pos, Direction.UP, SupportType.RIGID);
    }

    public static Cloud spawnSmokeCloud(ServerLevel level, BlockPos pos, Vec3 renderLocation, BurnPalette palette) {
        Vec3 location = Vec3.atCenterOf(pos);
        Vec3 randDir = MathUtil.randomDirection();
        Vec3 velocity = new Vec3(randDir.x, Math.abs(randDir.y), randDir.z).normalize().scale(1.0 / 10);

        Cloud cloud = new Cloud(
                level,
                location,
                renderLocation,
                velocity,
                0.1,
                ThreadLocalRandom.current().nextDouble(0.01, 0.05),
                (int) ThreadLocalRandom.current().nextDouble(1.5 * 20, 2.5 * 20),
                palette.smokePalette()
        );
        cloud.onUpdate = () -> {
            double drag = 0.02;
            cloud.velocity = new Vec3(
                    cloud.velocity.x * (1 - drag),
                    cloud.velocity.y,
                    cloud.velocity.z * (1 - drag)
            );
        };
        return cloud;
    }
}

package org.voximir.sky_torch.laser;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.voximir.sky_torch.utility.GameUtil;
import org.voximir.sky_torch.utility.TickScheduler;

import java.util.concurrent.ThreadLocalRandom;

public final class FlashBurn {
    private FlashBurn() {
    }

    public static class FlashBurnOptions {
        public int horizontalCount = 40;
        public int verticalCount = 10;
        public double verticalMinAngle = -Math.PI / 2 * 0.6;
        public double verticalMaxAngle = Math.PI / 2 * 0.6;
        public double rayDistance = 100.0;
    }

    public static class FlashBurnPlacement {
        public final ServerLevel level;
        public final Vec3 origin;
        public final Vec3 render;

        public FlashBurnPlacement(ServerLevel level, Vec3 origin, Vec3 render) {
            this.level = level;
            this.origin = origin;
            this.render = render;
        }
    }

    public static void flashBurn(FlashBurnPlacement placement, FlashBurnOptions options, Burner burner) {
        double horizontalStep = Math.PI * 2 / options.horizontalCount;
        double verticalStep = (options.verticalMaxAngle - options.verticalMinAngle) / options.verticalCount;

        // Spread each vertical slice over a separate tick so the spike is ~10× smaller.
        for (int v = 0; v < options.verticalCount; v++) {
            final double vAngle = options.verticalMinAngle + v * verticalStep;
            final int tick = v;
            TickScheduler.schedule(tick, () -> {
                for (int h = 0; h < options.horizontalCount; h++) {
                    double hAngle = h * horizontalStep;
                    Vec3 direction = new Vec3(
                            Math.cos(hAngle) * Math.cos(vAngle),
                            Math.sin(vAngle),
                            Math.sin(hAngle) * Math.cos(vAngle)
                    );
                    BlockHitResult hit = GameUtil.raycastGround(
                            placement.level, placement.origin, direction, options.rayDistance);
                    if (hit == null) continue;

                    Vec3 hitLocation = hit.getLocation();
                    int area = 2;
                    for (int x = -area; x <= area; x++) {
                        for (int z = -area; z <= area; z++) {
                            for (int y = -area; y <= area; y++) {
                                if (ThreadLocalRandom.current().nextBoolean()) continue;
                                BlockPos burnPos = BlockPos.containing(
                                        hitLocation.x + x, hitLocation.y + y, hitLocation.z + z);
                                burner.burn(placement.level, burnPos, placement.render, 0.5f);
                            }
                        }
                    }
                }
            });
        }
    }
}

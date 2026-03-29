package org.voximir.sky_torch.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.server.level.ServerLevel;
import org.joml.Vector3i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GameUtil {
    private GameUtil() {
    }

    private static final Map<Integer, List<Vector3i>> SPHERE_CACHE = new HashMap<>();

    public static List<Vector3i> sphereBlockOffsets(int radius) {
        return SPHERE_CACHE.computeIfAbsent(radius, r -> {
            List<Vector3i> out = new ArrayList<>();
            for (int x = -r; x <= r; x++) {
                for (int z = -r; z <= r; z++) {
                    for (int y = -r; y <= r; y++) {
                        if (x * x + y * y + z * z > r * r) {
                            continue;
                        }
                        out.add(new Vector3i(x, y, z));
                    }
                }
            }
            return out;
        });
    }

    public record XYPair(double x, double y) {
    }

    public static List<XYPair> ring(int count) {
        List<XYPair> out = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            double angle = (double) i / count * 2 * Math.PI;
            out.add(new XYPair(Math.sin(angle), Math.cos(angle)));
        }
        return out;
    }

    /**
     * Raycasts from origin in direction, returns the hit result or null if nothing hit.
     */
    public static BlockHitResult raycastGround(ServerLevel level, Vec3 origin, Vec3 direction, double maxDistance) {
        Vec3 end = origin.add(direction.normalize().scale(maxDistance));
        BlockHitResult result = level.clip(new ClipContext(
                origin, end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                CollisionContext.empty()
        ));
        if (result.getType() == HitResult.Type.MISS) {
            return null;
        }
        return result;
    }

    public static BlockPos toBlockPos(Vec3 vec) {
        return BlockPos.containing(vec);
    }
}

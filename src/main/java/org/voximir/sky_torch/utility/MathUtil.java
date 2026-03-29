package org.voximir.sky_torch.utility;

import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ThreadLocalRandom;

public class MathUtil {
    private MathUtil() {
    }

    public static float lerp(float start, float end, float alpha) {
        return start * (1.0f - alpha) + end * alpha;
    }

    public static double lerp(double start, double end, double alpha) {
        return start * (1.0 - alpha) + end * alpha;
    }

    public static Vec3 randomDirection() {
        double x = ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
        double y = ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
        double z = ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
        return new Vec3(x, y, z).normalize();
    }
}

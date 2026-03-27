package org.voximir.sky_torch.util;

public class MathUtil {
    public static float lerp(float start, float end, float alpha) {
        return start * (1.0f - alpha) + end * alpha;
    }
}

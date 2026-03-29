package org.voximir.sky_torch.client.render;

public class ClientBlindingOverlay {
    private final int fadeInDuration;
    private final int duration;
    private final int fadeOutDuration;
    private int age = 0;

    public ClientBlindingOverlay(int fadeInDuration, int duration, int fadeOutDuration) {
        this.fadeInDuration = fadeInDuration;
        this.duration = duration;
        this.fadeOutDuration = fadeOutDuration;
    }

    public void tick() {
        age++;
    }

    public boolean isDead() {
        return age > fadeInDuration + duration + fadeOutDuration;
    }

    public float getAlpha() {
        if (age < fadeInDuration) {
            return (float) age / fadeInDuration;
        } else if (age < fadeInDuration + duration) {
            return 1.0f;
        } else {
            int fadeAge = age - fadeInDuration - duration;
            return Math.max(0f, 1.0f - (float) fadeAge / fadeOutDuration);
        }
    }
}

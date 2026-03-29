package org.voximir.sky_torch.laser;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import org.voximir.sky_torch.networking.BlindingEffectS2CPayload;

import java.util.List;

public final class BlindingEffect {
    private BlindingEffect() {
    }

    public static class BlindingEffectOptions {
        public boolean enabled = false;
        public int fadeInDuration = 3;
        public int duration = 20;
        public int fadeOutDuration = 10;
    }

    public static class BlindEffect extends GameObject {
        private final List<ServerPlayer> players;
        public final BlindingEffectOptions options;
        private int age = 0;

        public BlindEffect(List<ServerPlayer> players, BlindingEffectOptions options) {
            this.players = players;
            this.options = options;

            if (options.enabled) {
                BlindingEffectS2CPayload payload = new BlindingEffectS2CPayload(
                        options.fadeInDuration, options.duration, options.fadeOutDuration
                );
                for (ServerPlayer player : players) {
                    ServerPlayNetworking.send(player, payload);
                }
            }
        }

        @Override
        public void update() {
            age++;
            if (age > options.fadeInDuration + options.duration + options.fadeOutDuration) {
                remove();
            }
        }

        @Override
        public void render() {
            // Rendering handled client-side
        }
    }
}

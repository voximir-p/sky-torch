package org.voximir.sky_torch.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.voximir.sky_torch.laser.LaserPlacement;
import org.voximir.sky_torch.networking.PlayLocalSoundS2CPayload;

public class SoundUtil {
    /// Plays a distance-scaled sound to players locally
    public static void playLocalScaledSoundAtPlayer(LaserPlacement placement, SoundEvent soundEvent, SoundSource soundSource, float volMin, float volMax, float pitch) {
        var soundEventId = BuiltInRegistries.SOUND_EVENT.getKey(soundEvent);
        for (var player : placement.affectedPlayers()) {
            var volume = MathUtil.lerp(
                    volMax,
                    volMin,
                    (float) (player.position().distanceTo(placement.hit()) / placement.soundRadius())
            );
            playLocalSound((ServerPlayer) player, soundEventId, soundSource, volume, pitch);
            player.displayClientMessage(Component.literal("Volume: ").append(String.valueOf(volume)), false);
        }
    }

    ///  Plays a sound to player locally
    public static void playLocalSound(ServerPlayer player, Identifier soundEventId, SoundSource soundSource, float volume, float pitch) {
        var payload = new PlayLocalSoundS2CPayload(soundEventId, soundSource, volume, pitch);
        ServerPlayNetworking.send(player, payload);
    }

    /// Plays a sound from given player
    public static void playSoundFromPlayer(Level level, Player player, SoundEvent soundEvent, float volume, float pitch) {
        level.playSound(null, player, soundEvent, SoundSource.PLAYERS, volume, pitch);
    }
}

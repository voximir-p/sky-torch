package org.voximir.sky_torch.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.ItemStack;
import org.voximir.sky_torch.item.ModItems;
import org.voximir.sky_torch.utility.Translatable;

public class LightningEvent {
    public static void register() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
            if (!(entity instanceof ServerPlayer serverPlayer)) return;
            if (!source.is(DamageTypes.LIGHTNING_BOLT)) return;

            var heldItem = serverPlayer.getMainHandItem();
            if (!heldItem.is(ModItems.BURNT_SHARD)) return;

            serverPlayer.setItemInHand(serverPlayer.getUsedItemHand(), new ItemStack(ModItems.SUPERCHARGED_SHARD));
            serverPlayer.displayClientMessage(Translatable.component("overlay", "shard_supercharge"), true);

            var serverLevel = serverPlayer.level();

            serverLevel.playSound(null, serverPlayer, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0f, 0.0f);
            serverLevel.playSound(null, serverPlayer, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0f, 1.0f);
            serverLevel.playSound(null, serverPlayer, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0f, 0.5f);
        });
    }
}

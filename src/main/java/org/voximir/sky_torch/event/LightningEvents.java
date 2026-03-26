package org.voximir.sky_torch.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.ItemStack;
import org.voximir.sky_torch.item.ModItems;
import org.voximir.sky_torch.utils.Translatable;

public class LightningEvents {

    public static void init() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
            if (!(entity instanceof ServerPlayer player)) return;
            if (!source.is(DamageTypes.LIGHTNING_BOLT)) return;

            ItemStack held = player.getMainHandItem();
            if (!held.is(ModItems.BURNT_SHARD)) return;

            player.setItemInHand(player.getUsedItemHand(), new ItemStack(ModItems.SUPERCHARGED_SHARD));
            player.displayClientMessage(Translatable.component("overlay", "shard_supercharge"), true);

            ServerLevel serverLevel = player.level();

            serverLevel.playSound(null, player, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0f, 0.0f);
            serverLevel.playSound(null, player, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 1.0f, 1.0f);
            serverLevel.playSound(null, player, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0f, 0.5f);
        });
    }
}
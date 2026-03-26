package org.voximir.sky_torch.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.item.ItemStack;
import org.voximir.sky_torch.item.ModItems;

import static org.voximir.sky_torch.SkyTorch.MOD_ID;

public class LightningEvents {

    public static void init() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamageTaken, damageTaken, blocked) -> {
            if (!(entity instanceof ServerPlayer player)) return;
            if (!source.is(DamageTypes.LIGHTNING_BOLT)) return;

            ItemStack held = player.getMainHandItem();
            if (!held.is(ModItems.BURNT_SHARD)) return;

            // TODO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! FIX COOL DOWN

            held.shrink(1);
            player.addItem(new ItemStack(ModItems.SUPERCHARGED_SHARD));
            player.displayClientMessage(
                    // TODO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! MAKE THIS UTIL
                    Component.translatable(Identifier.fromNamespaceAndPath(MOD_ID, "shard_supercharge").toLanguageKey("overlay")),
                    true
            );

            // TODO!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ADD SOUND
        });
    }
}
package org.voximir.sky_torch.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
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

            held.shrink(1);
            player.addItem(new ItemStack(ModItems.SUPERCHARGED_SHARD));
            player.displayClientMessage(
                    Translatable.component(
                            Translatable.Prefixes.OVERLAY,
                            "shard_supercharge"
                    ),
                    true
            );
        });
    }
}
package org.voximir.sky_torch.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jspecify.annotations.NonNull;
import org.voximir.sky_torch.utils.Translatable;

import java.util.function.Consumer;

public class SuperchargedShardItem extends Item {

    public SuperchargedShardItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(@NonNull ItemStack itemStack) {
        return true;
    }

    @Override
    @Deprecated
    public void appendHoverText(
            @NonNull ItemStack itemStack,
            Item.@NonNull TooltipContext tooltipContext,
            @NonNull TooltipDisplay tooltipDisplay,
            Consumer<Component> consumer,
            @NonNull TooltipFlag tooltipFlag
    ) {
        consumer.accept(Translatable.component("tooltip", "supercharged_shard"));
    }
}
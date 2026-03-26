package org.voximir.sky_torch.item;

import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import org.jspecify.annotations.NonNull;
import org.voximir.sky_torch.utils.Translatable;

import java.util.function.Consumer;

public class SuperchargedShardItem extends Item implements TooltipProvider {

    public SuperchargedShardItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(@NonNull ItemStack itemStack) {
        return true;
    }

    @Override
    public void addToTooltip(@NonNull TooltipContext tooltipContext, Consumer<Component> consumer, @NonNull TooltipFlag tooltipFlag, @NonNull DataComponentGetter dataComponentGetter) {
        consumer.accept(Translatable.component("tooltip", "supercharged_shard"));
    }
}
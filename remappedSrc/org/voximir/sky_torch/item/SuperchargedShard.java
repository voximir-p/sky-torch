package org.voximir.sky_torch.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SuperchargedShard extends Item {

    public SuperchargedShard(Properties settings) {
        super(settings);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
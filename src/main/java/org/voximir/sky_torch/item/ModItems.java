package org.voximir.sky_torch.item;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Function;

import static org.voximir.sky_torch.SkyTorch.MOD_ID;

public class ModItems {
    public static final Item BURNT_SHARD = registerItem(
            "burnt_shard",
            Item::new,
            new Item.Properties()
                    .stacksTo(1)
    );

    public static final Item SUPERCHARGED_SHARD = registerItem(
            "supercharged_shard",
            SuperchargedShardItem::new,
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
    );

    private static <T extends Item> T registerItem(String name, Function<Item.Properties, T> function, Item.Properties properties) {
        var itemResourceKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
        var item = function.apply(properties.setId(itemResourceKey));

        return Registry.register(BuiltInRegistries.ITEM, itemResourceKey, item);
    }

    public static void init() {
    }
}

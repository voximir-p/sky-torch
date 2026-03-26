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

    public static final Item BURNT_SHARD = register(
            "burnt_shard",
            Item::new,
            new Item.Properties()
                    .stacksTo(1)
    );

    public static final Item SUPERCHARGED_SHARD = register(
            "supercharged_shard",
            SuperchargedShardItem::new,
            new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
    );

    private static <T extends Item> T register(String name, Function<Item.Properties, T> factory, Item.Properties settings) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, name));
        T item = factory.apply(settings.setId(key));

        return Registry.register(BuiltInRegistries.ITEM, key, item);
    }

    public static void init() {
    }
}
package org.voximir.sky_torch.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import static org.voximir.sky_torch.SkyTorch.MOD_ID;

public class ModItemGroups {

    public static final ResourceKey<CreativeModeTab> SKY_TORCH = register("sky_torch");

    private static ResourceKey<CreativeModeTab> register(String id) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(MOD_ID, id));
    }

    public static void init() {
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                SKY_TORCH,
                FabricItemGroup.builder()
                        .title(Component.translatable(SKY_TORCH.identifier().toLanguageKey("itemGroup")))
                        .icon(() -> new ItemStack(ModItems.SUPERCHARGED_SHARD))
                        .displayItems((displayContext, entries) -> {
                            entries.accept(new ItemStack(ModItems.BURNT_SHARD));
                            entries.accept(new ItemStack(ModItems.SUPERCHARGED_SHARD));
                        })
                        .build()
        );
    }
}
package org.voximir.sky_torch.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.voximir.sky_torch.utility.Translatable;

import static org.voximir.sky_torch.SkyTorch.MOD_ID;

public class ModCreativeModeTabs {
    public static final ResourceKey<CreativeModeTab> SKY_TORCH = createKey("sky_torch");

    private static ResourceKey<CreativeModeTab> createKey(String string) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, Identifier.fromNamespaceAndPath(MOD_ID, string));
    }

    public static void init() {
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                SKY_TORCH,
                FabricItemGroup.builder()
                        .title(Translatable.component("itemGroup", SKY_TORCH.identifier()))
                        .icon(() -> new ItemStack(ModItems.SUPERCHARGED_SHARD))
                        .displayItems((itemDisplayParameters, output) -> {
                            output.accept(new ItemStack(ModItems.BURNT_SHARD));
                            output.accept(new ItemStack(ModItems.SUPERCHARGED_SHARD));
                        })
                        .build()
        );
    }
}

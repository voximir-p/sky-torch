package org.voximir.sky_torch.client.providers;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.voximir.sky_torch.item.ModItems;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {

    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput exporter) {
        return new RecipeProvider(registries, exporter) {
            @Override
            public void buildRecipes() {

                List<ItemLike> BURNT_SHARD_SMELTABLES = List.of(Items.AMETHYST_SHARD);
                oreSmelting(BURNT_SHARD_SMELTABLES, RecipeCategory.MISC, ModItems.BURNT_SHARD, 0.25f, 200, "burnt_shard");
            }
        };
    }

    @Override
    public String getName() {
        return "ModRecipeProvider";
    }
}
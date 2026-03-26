package org.voximir.sky_torch.client.providers;

import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import org.jspecify.annotations.NonNull;
import org.voximir.sky_torch.item.ModItems;

public class ModModelProvider extends FabricModelProvider {

    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(@NonNull BlockModelGenerators blockStateModelGenerator) {
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(ModItems.BURNT_SHARD, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.SUPERCHARGED_SHARD, ModelTemplates.FLAT_ITEM);
    }

    @Override
    public @NonNull String getName() {
        return "ModModelProvider";
    }
}
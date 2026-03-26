package org.voximir.sky_torch.client;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.voximir.sky_torch.client.providers.ModEnglishLangProvider;
import org.voximir.sky_torch.client.providers.ModModelProvider;
import org.voximir.sky_torch.client.providers.ModRecipeProvider;

public class SkyTorchDataGenerator implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

        pack.addProvider(ModEnglishLangProvider::new);
        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModRecipeProvider::new);
    }
}
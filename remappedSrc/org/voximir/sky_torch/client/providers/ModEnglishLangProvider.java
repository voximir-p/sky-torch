package org.voximir.sky_torch.client.providers;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup.Provider;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ModEnglishLangProvider extends FabricLanguageProvider {

    public ModEnglishLangProvider(FabricDataOutput dataOutput, CompletableFuture<Provider> registryLookup) {
        super(dataOutput, "en_us", registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.@NonNull Provider wrapperLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add("itemGroup.sky_torch.sky_torch", "Sky Torch");

        translationBuilder.add("item.sky_torch.burnt_shard", "Burnt Shard");
        translationBuilder.add("item.sky_torch.supercharged_shard", "Supercharged Shard");

        translationBuilder.add("overlay.sky_torch.shard_supercharge", "§eYou can feel some kind of §6§lenergy§r§e in the shard...");
    }
}
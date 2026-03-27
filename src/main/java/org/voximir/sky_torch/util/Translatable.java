package org.voximir.sky_torch.util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static org.voximir.sky_torch.SkyTorch.MOD_ID;

public class Translatable {
    public static String string(String prefix, String id) {
        return String.format("%s.%s.%s", prefix, MOD_ID, id);
    }

    public static String string(String prefix, Identifier id) {
        return id.toLanguageKey(prefix);
    }

    public static Component component(String prefix, String id) {
        return Component.translatable(Identifier.fromNamespaceAndPath(MOD_ID, id).toLanguageKey(prefix));
    }

    public static Component component(String prefix, Identifier id) {
        return Component.translatable(id.toLanguageKey(prefix));
    }
}

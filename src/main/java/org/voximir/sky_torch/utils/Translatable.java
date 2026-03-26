package org.voximir.sky_torch.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import static org.voximir.sky_torch.SkyTorch.MOD_ID;

public class Translatable {

    public enum Prefixes {
        CREATIVE_MODE_TAB("itemGroup"),
        ITEM("item"),
        OVERLAY("overlay"),
        TOOLTIP("tooltip");

        private final String prefix;

        Prefixes(String prefix) {
            this.prefix = prefix;
        }

        public String get() {
            return prefix;
        }
    }

    public static String string(Prefixes prefix, String id) {
        return String.format("%s.%s.%s", prefix.get(), MOD_ID, id);
    }

    public static String string(Prefixes prefix, Identifier id) {
        return id.toLanguageKey(prefix.get());
    }

    public static Component component(Prefixes prefix, String id) {
        return Component.translatable(Identifier.fromNamespaceAndPath(MOD_ID, id).toLanguageKey(prefix.get()));
    }

    public static Component component(Prefixes prefix, Identifier id) {
        return Component.translatable(id.toLanguageKey(prefix.get()));
    }
}
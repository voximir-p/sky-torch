package org.voximir.sky_torch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.voximir.sky_torch.event.ModEvents;
import org.voximir.sky_torch.item.ModCreativeModeTabs;
import org.voximir.sky_torch.item.ModItems;
import org.voximir.sky_torch.networking.ModNetworking;

import net.fabricmc.api.ModInitializer;

public class SkyTorch implements ModInitializer {
    public static final String MOD_ID = "sky_torch";
    public static final Logger LOGGER = LoggerFactory.getLogger("Sky Torch");

    @Override
    public void onInitialize() {
        ModItems.init();
        ModCreativeModeTabs.init();
        ModEvents.init();
        ModNetworking.init();

        LOGGER.info("Sky Torch initialized!");
    }
}

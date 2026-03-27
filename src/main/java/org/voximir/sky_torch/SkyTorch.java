package org.voximir.sky_torch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.voximir.sky_torch.event.LightningEvents;
import org.voximir.sky_torch.event.TickSchedulerEvents;
import org.voximir.sky_torch.item.ModCreativeModeTabs;
import org.voximir.sky_torch.item.ModItems;

import net.fabricmc.api.ModInitializer;
import org.voximir.sky_torch.networking.ModNetworking;

public class SkyTorch implements ModInitializer {
    public static final String MOD_ID = "sky_torch";
    public static final Logger LOGGER = LoggerFactory.getLogger("Sky Torch");

    @Override
    public void onInitialize() {
        ModItems.init();
        ModCreativeModeTabs.init();

        LightningEvents.init();
        TickSchedulerEvents.init();

        ModNetworking.init();

        LOGGER.info("Sky Torch initialized!");
    }
}

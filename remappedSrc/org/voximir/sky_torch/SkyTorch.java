package org.voximir.sky_torch;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.voximir.sky_torch.event.LightningEvents;
import org.voximir.sky_torch.item.ModItemGroups;
import org.voximir.sky_torch.item.ModItems;

public class SkyTorch implements ModInitializer {

    public static final String MOD_ID = "sky_torch";
    public static final Logger LOGGER = LoggerFactory.getLogger("Sky Torch");

    @Override
    public void onInitialize() {
        ModItems.init();
        ModItemGroups.init();

        LightningEvents.init();

        LOGGER.info("Sky Torch initialized!");
    }
}
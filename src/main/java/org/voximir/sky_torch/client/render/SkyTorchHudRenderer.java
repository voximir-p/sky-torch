package org.voximir.sky_torch.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;

public final class SkyTorchHudRenderer {
    private SkyTorchHudRenderer() {}

    @SuppressWarnings("deprecation")
    public static void register() {
        HudRenderCallback.EVENT.register(SkyTorchHudRenderer::render);
    }

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        ClientBlindingOverlay overlay = SkyTorchRenderState.getBlindingOverlay();
        if (overlay == null) return;

        float alpha = overlay.getAlpha();
        if (alpha <= 0) return;

        int a = (int) (255 * alpha);
        int color = (a << 24) | 0x00FFFFFF;
        int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        int height = Minecraft.getInstance().getWindow().getGuiScaledHeight();
        graphics.fill(0, 0, width, height, color);
    }
}

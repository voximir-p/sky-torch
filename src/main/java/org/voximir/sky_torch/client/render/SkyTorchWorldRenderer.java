package org.voximir.sky_torch.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public final class SkyTorchWorldRenderer {
    private SkyTorchWorldRenderer() {}

    public static void register() {
        WorldRenderEvents.AFTER_ENTITIES.register(SkyTorchWorldRenderer::render);
    }

    private static void render(WorldRenderContext context) {
        PoseStack poseStack = context.matrices();
        MultiBufferSource bufferSource = context.consumers();
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().position();
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        double camX = camera.x;
        double camY = camera.y;
        double camZ = camera.z;

        for (ClientCloud cloud : SkyTorchRenderState.getClouds()) {
            renderBlock(poseStack, bufferSource, blockRenderer,
                    cloud.getBlockState(), cloud.getTransform(camX, camY, camZ));
        }

        for (ClientFlyingBlock block : SkyTorchRenderState.getFlyingBlocks()) {
            renderBlock(poseStack, bufferSource, blockRenderer,
                    block.getBlockState(), block.getTransform(camX, camY, camZ));
        }

        for (ClientLaser laser : SkyTorchRenderState.getLasers().values()) {
            if (!laser.isActive()) continue;
            Vec3 vector = laser.getEndPos().subtract(laser.getTipPos());
            if (vector.lengthSqr() == 0) continue;

            renderBlock(poseStack, bufferSource, blockRenderer,
                    Blocks.SMOOTH_QUARTZ.defaultBlockState(),
                    laser.getBeamTransform(camX, camY, camZ));

            renderBlock(poseStack, bufferSource, blockRenderer,
                    Blocks.WHITE_STAINED_GLASS.defaultBlockState(),
                    laser.getGlowTransform(camX, camY, camZ));
        }
    }

    private static void renderBlock(PoseStack poseStack, MultiBufferSource bufferSource,
                                    BlockRenderDispatcher blockRenderer,
                                    BlockState blockState, Matrix4f transform) {
        poseStack.pushPose();
        poseStack.mulPose(transform);
        blockRenderer.renderSingleBlock(blockState, poseStack, bufferSource,
                LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        poseStack.popPose();
    }
}

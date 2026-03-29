package org.voximir.sky_torch.client.render;

import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SkyTorchRenderState {
    private static final List<ClientCloud> clouds = new ArrayList<>();
    private static final List<ClientFlyingBlock> flyingBlocks = new ArrayList<>();
    private static final Map<Integer, ClientLaser> lasers = new HashMap<>();
    private static ClientBlindingOverlay blindingOverlay;

    private SkyTorchRenderState() {}

    public static void addCloud(ClientCloud cloud) {
        clouds.add(cloud);
    }

    public static void addFlyingBlock(ClientFlyingBlock block) {
        flyingBlocks.add(block);
    }

    public static void updateLaser(int laserId, Vec3 tipPos, Vec3 endPos,
                                   float beamWidth, float glowWidth, boolean active) {
        if (!active) {
            lasers.remove(laserId);
            return;
        }
        ClientLaser laser = lasers.get(laserId);
        if (laser == null) {
            laser = new ClientLaser(tipPos, endPos, beamWidth, glowWidth);
            lasers.put(laserId, laser);
        } else {
            laser.update(tipPos, endPos, beamWidth, glowWidth, active);
        }
    }

    public static void setBlindingOverlay(ClientBlindingOverlay overlay) {
        blindingOverlay = overlay;
    }

    public static List<ClientCloud> getClouds() {
        return clouds;
    }

    public static List<ClientFlyingBlock> getFlyingBlocks() {
        return flyingBlocks;
    }

    public static Map<Integer, ClientLaser> getLasers() {
        return lasers;
    }

    public static ClientBlindingOverlay getBlindingOverlay() {
        return blindingOverlay;
    }

    public static void tick() {
        clouds.removeIf(cloud -> { cloud.tick(); return cloud.isDead(); });
        flyingBlocks.removeIf(block -> { block.tick(); return block.isDead(); });

        if (blindingOverlay != null) {
            blindingOverlay.tick();
            if (blindingOverlay.isDead()) {
                blindingOverlay = null;
            }
        }
    }

    public static void clear() {
        clouds.clear();
        flyingBlocks.clear();
        lasers.clear();
        blindingOverlay = null;
    }
}

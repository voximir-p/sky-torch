package org.voximir.sky_torch.client.render;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;

import java.util.List;

public class ClientCloud {
    private double x, y, z;
    private final double vx, vy, vz;
    private double size;
    private final double growth;
    private final int maxAge;
    private final List<BlockState> blocks;
    private int age = 0;

    private float pitch;
    private float yaw;
    private final float pitchVelocity;
    private final float yawVelocity;

    public ClientCloud(double x, double y, double z,
                       double vx, double vy, double vz,
                       double size, double growth, int maxAge,
                       float pitch, float yaw,
                       float pitchVelocity, float yawVelocity,
                       List<Integer> blockStateIds) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        this.size = size;
        this.growth = growth;
        this.maxAge = maxAge;
        this.pitch = pitch;
        this.yaw = yaw;
        this.pitchVelocity = pitchVelocity;
        this.yawVelocity = yawVelocity;
        this.blocks = blockStateIds.stream().map(Block::stateById).toList();
    }

    public void tick() {
        size += growth;
        x += vx;
        y += vy;
        z += vz;
        pitch += pitchVelocity;
        yaw += yawVelocity;
        age++;
    }

    public boolean isDead() {
        return age >= maxAge;
    }

    public BlockState getBlockState() {
        int index = Math.min((int) ((float) age / maxAge * blocks.size()), blocks.size() - 1);
        return blocks.get(index);
    }

    public Matrix4f getTransform(double camX, double camY, double camZ) {
        float s = (float) size;
        return new Matrix4f()
                .translate((float) (x - camX), (float) (y - camY), (float) (z - camZ))
                .rotateXYZ(pitch, yaw, 0f)
                .translate(-s / 2f, -s / 2f, 0f)
                .scale(s, s, s);
    }
}

package org.voximir.sky_torch.client.render;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ClientFlyingBlock {
    private double x, y, z;
    private double vx, vy, vz;
    private final BlockState blockState;
    private final float initialPitch;
    private final float initialYaw;
    private final Vector3f rotateAxis;
    private final double rotateSpeed;
    private double rotation = 0.0;
    private int age = 0;

    private static final double GRAVITY = 0.08;
    private static final double AIR_DRAG = 0.02;
    private static final int MAX_AGE = 20 * 10;

    public ClientFlyingBlock(double x, double y, double z,
                             double vx, double vy, double vz,
                             int blockStateId,
                             float initialPitch, float initialYaw,
                             float rotateAxisX, float rotateAxisY, float rotateAxisZ,
                             double rotateSpeed) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
        this.blockState = Block.stateById(blockStateId);
        this.initialPitch = initialPitch;
        this.initialYaw = initialYaw;
        this.rotateAxis = new Vector3f(rotateAxisX, rotateAxisY, rotateAxisZ);
        this.rotateSpeed = rotateSpeed;
    }

    public void tick() {
        vx *= (1 - AIR_DRAG);
        vy -= GRAVITY;
        vz *= (1 - AIR_DRAG);
        rotation += rotateSpeed;
        x += vx;
        y += vy;
        z += vz;
        age++;
    }

    public boolean isDead() {
        return age >= MAX_AGE;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public Matrix4f getTransform(double camX, double camY, double camZ) {
        float size = 1f;
        return new Matrix4f()
                .translate((float) (x - camX), (float) (y - camY), (float) (z - camZ))
                .rotate((float) rotation, rotateAxis)
                .rotateXYZ(initialPitch, initialYaw, 0f)
                .translate(-size / 2f, -size / 2f, 0f)
                .scale(size, size, size);
    }
}

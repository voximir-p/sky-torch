package org.voximir.sky_torch.client.render;

import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class ClientLaser {
    private Vec3 tipPos;
    private Vec3 endPos;
    private float beamWidth;
    private float glowWidth;
    private boolean active;

    public ClientLaser(Vec3 tipPos, Vec3 endPos, float beamWidth, float glowWidth) {
        this.tipPos = tipPos;
        this.endPos = endPos;
        this.beamWidth = beamWidth;
        this.glowWidth = glowWidth;
        this.active = true;
    }

    public void update(Vec3 tipPos, Vec3 endPos, float beamWidth, float glowWidth, boolean active) {
        this.tipPos = tipPos;
        this.endPos = endPos;
        this.beamWidth = beamWidth;
        this.glowWidth = glowWidth;
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public Matrix4f getBeamTransform(double camX, double camY, double camZ) {
        return buildLineTransform(camX, camY, camZ, beamWidth, 0);
    }

    public Matrix4f getGlowTransform(double camX, double camY, double camZ) {
        return buildLineTransform(camX, camY, camZ, glowWidth, glowWidth);
    }

    private Matrix4f buildLineTransform(double camX, double camY, double camZ, float thickness, float backOffset) {
        Vec3 from = tipPos;
        Vec3 to = endPos;
        Vec3 vector = to.subtract(from);
        if (vector.lengthSqr() == 0) return new Matrix4f();

        Vec3 direction = vector.normalize();
        Vec3 up = resolveUpVector(direction);

        Vec3 lineFrom = from.add(direction.scale(-20 + backOffset));

        return new Matrix4f()
                .translate((float) (lineFrom.x - camX), (float) (lineFrom.y - camY), (float) (lineFrom.z - camZ))
                .rotateTowards(vector.toVector3f(), up.toVector3f())
                .translate(-thickness / 2f, -thickness / 2f, 0f)
                .scale(thickness, thickness, (float) vector.length());
    }

    private static Vec3 resolveUpVector(Vec3 direction) {
        if (direction.x == 0.0 && direction.z == 0.0) {
            return new Vec3(0, 0, 1);
        }
        return new Vec3(0, 1, 0);
    }

    public Vec3 getTipPos() {
        return tipPos;
    }

    public Vec3 getEndPos() {
        return endPos;
    }
}

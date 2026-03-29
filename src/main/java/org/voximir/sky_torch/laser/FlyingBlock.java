package org.voximir.sky_torch.laser;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.voximir.sky_torch.networking.SpawnFlyingBlockS2CPayload;
import org.voximir.sky_torch.utility.GameUtil;
import org.voximir.sky_torch.utility.TickScheduler;

import java.util.concurrent.ThreadLocalRandom;

public class FlyingBlock extends GameObject {
    public Vec3 location;
    public Vec3 velocity;
    public BlockState blockState;

    public final ServerLevel level;
    public final float initialPitch = (float) ThreadLocalRandom.current().nextDouble(2 * Math.PI);
    public final float initialYaw = (float) ThreadLocalRandom.current().nextDouble(2 * Math.PI);
    public final Vector3f rotateAxis;
    public final double rotateSpeed = ThreadLocalRandom.current().nextDouble(0.1, 0.8);
    public double rotation = 0.0;

    private static final double GRAVITY = 0.08;
    private static final double AIR_DRAG = 0.02;

    public FlyingBlock(ServerLevel level, Vec3 renderLocation, Vec3 location, Vec3 velocity,
                       BlockState blockState) {
        this.level = level;
        this.location = location;
        this.velocity = velocity;
        this.blockState = blockState;

        // Rotate axis = velocity × (0,1,0), falling back to X axis if degenerate.
        Vec3 cross = velocity.cross(new Vec3(0, 1, 0));
        this.rotateAxis = cross.lengthSqr() > 0 ? cross.normalize().toVector3f() : new Vector3f(1, 0, 0);

        TickScheduler.schedule(20 * 10, this::remove);

        // Send spawn packet to all players
        SpawnFlyingBlockS2CPayload payload = new SpawnFlyingBlockS2CPayload(
                location.x, location.y, location.z,
                velocity.x, velocity.y, velocity.z,
                Block.getId(blockState),
                initialPitch, initialYaw,
                rotateAxis.x(), rotateAxis.y(), rotateAxis.z(),
                rotateSpeed
        );
        for (ServerPlayer player : level.players()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Override
    public void update() {
        // Remove if below terrain and moving downward fast.
        if (velocity.y < -10) {
            BlockState below = level.getBlockState(GameUtil.toBlockPos(location));
            if (below.isSolid()) {
                remove();
                return;
            }
        }

        velocity = new Vec3(
                velocity.x * (1 - AIR_DRAG),
                velocity.y - GRAVITY,
                velocity.z * (1 - AIR_DRAG)
        );
        rotation += rotateSpeed;
        location = location.add(velocity);
    }

    @Override
    public void render() {
        // Rendering handled client-side
    }
}

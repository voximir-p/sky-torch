package org.voximir.sky_torch.laser;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.voximir.sky_torch.networking.SpawnCloudS2CPayload;
import org.voximir.sky_torch.utility.TickScheduler;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Cloud extends GameObject {
    public Vec3 location;
    public Vec3 velocity;
    public double size;
    public final double growth;
    public final int maxAge;
    public final List<BlockState> blocks;
    public final ServerLevel level;
    public int age = 0;

    public float pitch = ThreadLocalRandom.current().nextFloat() * 360f;
    public float yaw = ThreadLocalRandom.current().nextFloat() * 360f;
    public final float pitchVelocity = ThreadLocalRandom.current().nextFloat() * 0.01f;
    public final float yawVelocity = ThreadLocalRandom.current().nextFloat() * 0.01f;

    /** Cached ground position for raycasting throttle in BurnWave/ShockWave. */
    public Vec3 cachedGround = null;
    public int groundAge = 0;

    public Runnable onUpdate = () -> {};

    public Cloud(ServerLevel level, Vec3 location, Vec3 renderLocation, Vec3 velocity,
                 double size, double growth, int maxAge, List<BlockState> blocks) {
        this.level = level;
        this.location = location;
        this.velocity = velocity;
        this.size = size;
        this.growth = growth;
        this.maxAge = maxAge;
        this.blocks = blocks;

        TickScheduler.schedule(maxAge, this::remove);

        // Send spawn packet to all players
        List<Integer> blockStateIds = blocks.stream().map(Block::getId).toList();
        SpawnCloudS2CPayload payload = new SpawnCloudS2CPayload(
                location.x, location.y, location.z,
                velocity.x, velocity.y, velocity.z,
                size, growth, maxAge,
                pitch, yaw, pitchVelocity, yawVelocity,
                blockStateIds
        );
        for (ServerPlayer player : level.players()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Override
    public void update() {
        onUpdate.run();

        size += growth;
        location = location.add(velocity);
        pitch += pitchVelocity;
        yaw += yawVelocity;
        age++;
    }

    @Override
    public void render() {
        // Rendering handled client-side
    }
}

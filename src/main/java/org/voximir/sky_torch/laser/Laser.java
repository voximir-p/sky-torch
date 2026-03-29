package org.voximir.sky_torch.laser;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.voximir.sky_torch.networking.LaserStateS2CPayload;
import org.voximir.sky_torch.utility.GameUtil;
import org.voximir.sky_torch.utility.SeriesScheduler;
import org.voximir.sky_torch.utility.SetBlockUndo;
import org.voximir.sky_torch.utility.SoundUtil;
import org.voximir.sky_torch.utility.TickScheduler;

import java.util.concurrent.ThreadLocalRandom;

public class Laser extends GameObject {

    private static int nextLaserId = 0;
    private final int laserId = nextLaserId++;

    private final LaserPlacement placement;
    private final LaserOptions options;
    private final Burner burner;

    private Vec3 hitPos;
    private Vec3 origin;
    private final Vec3 originalHitLocation;

    private boolean retracting = false;
    private int startFrame = 0;
    private int endTime = 0;
    private int time = 0;
    private double driftTimeX = 0.0;
    private double driftTimeZ = 0.0;
    private double currentGlowWidth;

    private final Runnable onHitCallback;
    private final Runnable onRetractEndCallback;
    private boolean hitFired = false;
    private boolean retractEndFired = false;

    public Laser(LaserPlacement placement, LaserOptions options) {
        this.placement = placement;
        this.options = options;
        this.burner = new Burner(options.burn);
        this.hitPos = placement.hitPos();
        this.origin = placement.origin();
        this.originalHitLocation = placement.hitPos();
        this.currentGlowWidth = options.glowWidthMin;

        TickScheduler.schedule((int) options.duration, () -> retracting = true);

        // Explode/radiation offset positions (computed once from initial hitPos).
        Vec3 towardOrigin = hitPos.subtract(origin).normalize().scale(options.explodePlacementOffset);
        Vec3 explodeOrigin = hitPos.add(towardOrigin);
        Vec3 radiationOrigin = explodeOrigin.add(0, options.flashBurnPlacementOffset, 0);

        // Lower the hit point slightly.
        hitPos = new Vec3(hitPos.x, hitPos.y - options.digDepth, hitPos.z);

        this.onHitCallback = () -> {
            ServerLevel level = (ServerLevel) placement.level();

            if (options.applyNightVision) {
                for (ServerPlayer player : level.players()) {
                    player.addEffect(new MobEffectInstance(
                            MobEffects.NIGHT_VISION, 10000, 1, true, false, false));
                }
            }

            new BlindingEffect.BlindEffect(
                    placement.affectedPlayers().stream()
                            .filter(p -> p instanceof ServerPlayer)
                            .map(p -> (ServerPlayer) p)
                            .toList(),
                    options.blinding);

            TickScheduler.schedule(1, () -> {
                SoundUtil.playLocalScaledSoundAtPlayer(placement, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.AMBIENT, 0.0f, 1.5f, 0.0f);
                SoundUtil.playLocalScaledSoundAtPlayer(placement, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.AMBIENT, 0.0f, 1.5f, 1.0f);
                SoundUtil.playLocalScaledSoundAtPlayer(placement, SoundEvents.TOTEM_USE, SoundSource.AMBIENT, 0.0f, 1.5f, 0.5f);
            });

            ShockWave.spawnShockwave(
                    new ShockWave.ShockWavePlacement(level, explodeOrigin, placement.render()),
                    options.shockwave, burner);
            TickScheduler.schedule(1, () ->
                    BurnWave.spawnBurnWave(
                            new BurnWave.BurnWavePlacement(level, explodeOrigin, placement.render()),
                            options.burnWave, burner));
            TickScheduler.schedule(3, () ->
                    FlashBurn.flashBurn(
                            new FlashBurn.FlashBurnPlacement(level, radiationOrigin, placement.render()),
                            options.flashBurn, burner));
        };

        this.onRetractEndCallback = () -> {
            onEnd();
            TickScheduler.schedule(20 * 60, this::remove);
        };
    }

    @Override
    public void remove() {
        burner.close();
        onEnd();
        super.remove();
    }

    private void onEnd() {
        if (placement.level() instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                player.removeEffect(MobEffects.NIGHT_VISION);
            }
        }
    }

    @Override
    public void update() {
        if (endTime > options.beamEndFrames) {
            return;
        }

        time++;
        driftTimeX += ThreadLocalRandom.current().nextDouble() * 2.0;
        driftTimeZ += ThreadLocalRandom.current().nextDouble() * 2.2;

        origin = origin.add(placement.velocityDir().scale(options.flySpeed));

        hitPos = new Vec3(
                originalHitLocation.x + Math.sin(driftTimeX / options.randomnessPeriod * Math.PI) * options.randomnessMagnitude,
                hitPos.y,
                originalHitLocation.z + Math.sin(driftTimeZ / options.randomnessPeriod * Math.PI) * options.randomnessMagnitude
        );

        startFrame++;
        if (startFrame == options.beamStartFrames && !hitFired) {
            hitFired = true;
            onHitCallback.run();
        }
        if (startFrame >= options.beamStartFrames) {
            boreHole();
        }

        if (retracting) {
            endTime++;
            if (endTime == options.beamEndFrames && !retractEndFired) {
                retractEndFired = true;
                onRetractEndCallback.run();
            }
        }

        double glowSin = Math.sin((double) time / options.glowPeriod * Math.PI) / 2.0 + 0.5;
        currentGlowWidth = options.glowWidthMin + (options.glowWidthMax - options.glowWidthMin) * glowSin;
    }

    private void boreHole() {
        if (options.boreRadius == 0.0) return;

        Vec3 tip = laserTip();
        Vec3 end = laserEnd();
        Vec3 diff = end.subtract(tip);
        if (diff.lengthSqr() == 0) return;
        double totalDistance = diff.length();
        double boreDistance = Math.min(totalDistance, options.boreDistance);
        if (boreDistance <= 0.0) return;
        Vec3 stride = diff.normalize().scale(options.boreRadius);

        ServerLevel level = (ServerLevel) placement.level();
        int tries = (int) Math.ceil(boreDistance / options.boreRadius);

        for (int i = 0; i < tries; i++) {
            Vec3 position = tip.add(stride.scale(i));
            BlockPos center = GameUtil.toBlockPos(position);

            for (org.joml.Vector3i offset : GameUtil.sphereBlockOffsets((int) Math.ceil(options.boreRadius))) {
                BlockPos blockPos = center.offset(offset.x, offset.y, offset.z);
                if (!level.getBlockState(blockPos).isAir()) {
                    SetBlockUndo.setBlock(level, blockPos, Blocks.AIR.defaultBlockState());
                }
            }

            for (org.joml.Vector3i offset : GameUtil.sphereBlockOffsets((int) Math.ceil(options.boreBurnRadius))) {
                BlockPos blockPos = center.offset(offset.x, offset.y, offset.z);
                burner.burn(level, blockPos, placement.render(), 1.0f);
            }
        }
    }

    @Override
    public void render() {
        Vec3 tip = laserTip();
        Vec3 end = laserEnd();
        boolean active = endTime <= options.beamEndFrames;
        LaserStateS2CPayload payload = new LaserStateS2CPayload(
                laserId,
                tip.x, tip.y, tip.z,
                end.x, end.y, end.z,
                (float) options.beamWidth, (float) currentGlowWidth,
                active
        );
        if (placement.level() instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                ServerPlayNetworking.send(player, payload);
            }
        }
    }

    private Vec3 laserTip() {
        double t = Math.min((double) startFrame / options.beamStartFrames, 1.0);
        return interpolate(origin, hitPos, t);
    }

    private Vec3 laserEnd() {
        double t = Math.min((double) endTime / options.beamEndFrames, 1.0);
        return interpolate(origin, hitPos, t);
    }

    private static Vec3 interpolate(Vec3 from, Vec3 to, double t) {
        double c = Math.clamp(t, 0.0, 1.0);
        return from.scale(1.0 - c).add(to.scale(c));
    }
}

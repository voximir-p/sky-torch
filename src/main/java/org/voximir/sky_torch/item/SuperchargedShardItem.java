package org.voximir.sky_torch.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;
import org.voximir.sky_torch.laser.Laser;
import org.voximir.sky_torch.laser.LaserOptions;
import org.voximir.sky_torch.laser.LaserPlacement;
import org.voximir.sky_torch.utility.SeriesScheduler;
import org.voximir.sky_torch.utility.Translatable;
import org.voximir.sky_torch.utility.SoundUtil;

import java.util.Objects;
import java.util.function.Consumer;

public class SuperchargedShardItem extends Item {
    private static final double MAX_RANGE = 64.0d;
    private static final double SOUND_RADIUS = 128.0d;
    private static final double ORIGIN_RADIUS = 300.0d;

    public SuperchargedShardItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(@NonNull ItemStack itemStack) {
        return true;
    }

    @Override
    @Deprecated
    public void appendHoverText(
            @NonNull ItemStack itemStack,
            Item.@NonNull TooltipContext tooltipContext,
            @NonNull TooltipDisplay tooltipDisplay,
            Consumer<Component> consumer,
            @NonNull TooltipFlag tooltipFlag
    ) {
        consumer.accept(Translatable.component("tooltip", "supercharged_shard"));
    }

    @Override
    public @NonNull InteractionResult use(Level level, Player player, @NonNull InteractionHand interactionHand) {
        var itemStack = player.getMainHandItem();

        if (level.isClientSide() || interactionHand == InteractionHand.OFF_HAND || !itemStack.is(this))
            return InteractionResult.PASS;

        var startPos = player.getEyePosition();
        var clipDir = player.getViewVector(1.0f);
        var endPos = startPos.add(clipDir.scale(MAX_RANGE));
        BlockHitResult result = level.clip(new ClipContext(
                startPos, endPos,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                player
        ));

        var blockPos = result.getBlockPos();
        var blockState = level.getBlockState(blockPos);
        if (blockState.is(Blocks.AIR)) {
            player.displayClientMessage(Translatable.component("overlay", "exceed_range"), true);
            level.playSound(null, player, SoundEvents.THORNS_HIT, SoundSource.PLAYERS, 0.4f, 2.0f);
            return InteractionResult.FAIL;
        }

        if (!Objects.requireNonNull(player.gameMode()).isCreative())
            player.setItemInHand(interactionHand, ItemStack.EMPTY);

        // Laser
        var hitPos = blockPos.getCenter();
        // Origin: a fixed point on a circle of radius ORIGIN_RADIUS centered above the hit
        // position at the max build height. The angle follows the attack direction (+ 30°).
        var xzFlat = new Vec3(clipDir.x(), 0, clipDir.z());
        var xzDir = xzFlat.lengthSqr() > 1e-8
                ? xzFlat.yRot((float) (Math.PI / 6.0)).normalize()
                : new Vec3(1, 0, 0); // fallback: player looking straight up/down
        var origin = new Vec3(
                hitPos.x() + xzDir.x() * ORIGIN_RADIUS,
                level.getMaxY(),
                hitPos.z() + xzDir.z() * ORIGIN_RADIUS
        );
        var velocityDir = new Vec3(clipDir.x(), 0.0, clipDir.z()).normalize();
        var render = player.position();

        var box = new AABB(blockPos).inflate(SOUND_RADIUS + 1.0d);
        var affectedPlayers = level.getEntitiesOfClass(
                Player.class,
                box,
                _player -> _player.distanceToSqr(hitPos) <= (SOUND_RADIUS * SOUND_RADIUS)
        );

        chargeAndSpawn(
                new LaserPlacement(
                        SOUND_RADIUS,
                        affectedPlayers,
                        level,
                        hitPos,
                        origin,
                        velocityDir,
                        render
                ),
                new LaserOptions(),
                player
        );

        return InteractionResult.SUCCESS;
    }

    private static void chargeAndSpawn(LaserPlacement placement, LaserOptions options, Player player) {
        new SeriesScheduler()
                // Target lock
                .run(() -> SoundUtil.playSoundFromPlayer(placement.level(), player, SoundEvents.RESPAWN_ANCHOR_CHARGE, 1.0f, 0.0f))
                .sleep(20)

                // Charge up
                .run(() -> {
                    SoundUtil.playLocalScaledSoundAtPlayer(placement, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.AMBIENT, 0.0f, 1.5f, 0.8f);
                    SoundUtil.playLocalScaledSoundAtPlayer(placement, SoundEvents.CONDUIT_AMBIENT, SoundSource.AMBIENT, 0.0f, 1.5f, 0.0f);
                })

                // Fire
                .sleep(20)
                .run(() -> new Laser(placement, options));
    }
}

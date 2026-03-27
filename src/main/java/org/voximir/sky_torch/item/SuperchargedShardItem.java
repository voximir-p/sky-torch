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
import org.voximir.sky_torch.util.SeriesScheduler;
import org.voximir.sky_torch.util.Translatable;
import org.voximir.sky_torch.util.SoundUtil;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class SuperchargedShardItem extends Item {
    private static final double MAX_RANGE = 64.0d;
    private static final double SOUND_RADIUS = 128.0d;

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

        player.displayClientMessage(Component.translatable(
                        "debug.sky_torch.fire",
                        blockPos.getX(),
                        blockPos.getY(),
                        blockPos.getZ()),
                false
        );

        if (!Objects.requireNonNull(player.gameMode()).isCreative())
            player.setItemInHand(interactionHand, ItemStack.EMPTY);

        // Laser
        var hitPos = blockPos.getCenter();
        var origin = hitPos.add(
                clipDir.add(new Vec3(0.0, 1.0, 0.0))
                        .yRot((float) (Math.PI / 6.0))
                        .scale(600.0)
        );
        var velocityDir = new Vec3(clipDir.x, 0.0, clipDir.z).normalize();
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
                        blockPos,
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

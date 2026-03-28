package org.voximir.sky_torch.laser;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public record LaserPlacement(
        double soundRadius,
        List<Player> affectedPlayers,
        Level level,
        Vec3 hitPos,
        Vec3 origin,
        Vec3 velocityDir,
        Vec3 render
) {
}

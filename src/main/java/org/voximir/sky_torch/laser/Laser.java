package org.voximir.sky_torch.laser;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.voximir.sky_torch.util.SoundUtil;

public class Laser {
    public Laser(LaserPlacement placement, LaserOptions options) {
        SoundUtil.playLocalScaledSoundAtPlayer(placement, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.AMBIENT, 0.0f, 1.5f, 0.0f);
        SoundUtil.playLocalScaledSoundAtPlayer(placement, SoundEvents.WARDEN_SONIC_CHARGE, SoundSource.AMBIENT, 0.0f, 1.5f, 1.0f);
        SoundUtil.playLocalScaledSoundAtPlayer(placement, SoundEvents.TOTEM_USE, SoundSource.AMBIENT, 0.0f, 1.5f, 0.5f);
    }
}

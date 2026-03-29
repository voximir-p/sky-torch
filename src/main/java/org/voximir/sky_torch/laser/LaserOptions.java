package org.voximir.sky_torch.laser;

public class LaserOptions {
    public long duration = (long) (20 * 1.5);
    public int beamStartFrames = 4;
    public int beamEndFrames = beamStartFrames * 4;
    public double randomnessMagnitude = .5;
    public int randomnessPeriod = 12;
    public double flySpeed = 6.0 * 1.2;
    public double beamWidth = .8;
    public double glowWidthMin = beamWidth - .1;
    public double glowWidthMax = beamWidth + .5;
    public int glowPeriod = 3;
    public boolean applyNightVision = true;
    public double boreRadius = 3.0;
    public double boreBurnRadius = 5.0;
    public int boreDistance = 50;
    public Burner.BurnOptions burn = new Burner.BurnOptions();
    public ShockWave.ShockWaveOptions shockwave = new ShockWave.ShockWaveOptions();
    public BurnWave.BurnWaveOptions burnWave = new BurnWave.BurnWaveOptions();
    public FlashBurn.FlashBurnOptions flashBurn = new FlashBurn.FlashBurnOptions();
    public BlindingEffect.BlindingEffectOptions blinding = new BlindingEffect.BlindingEffectOptions();
    public double explodePlacementOffset = 2.0;
    public double flashBurnPlacementOffset = 5.0;
    public double digDepth = 1.0;
}

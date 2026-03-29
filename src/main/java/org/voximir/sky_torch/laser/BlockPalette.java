package org.voximir.sky_torch.laser;

public enum BlockPalette {
    ORANGE(BurnPalette.orange),
    BLUE(BurnPalette.blue),
    PURPLE(BurnPalette.purple),
    GLASS_ONLY(BurnPalette.glassOnly()),
    ORANGE_WITHOUT_TRAIL(BurnPalette.orangeWithoutTrail());

    public final BurnPalette burn;

    BlockPalette(BurnPalette burn) {
        this.burn = burn;
    }
}

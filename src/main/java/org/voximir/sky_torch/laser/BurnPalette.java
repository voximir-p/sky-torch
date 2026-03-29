package org.voximir.sky_torch.laser;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BurnPalette {

    // -------------------------------------------------------------------------
    // Instance fields — palette members
    // -------------------------------------------------------------------------
    public List<List<BlockState>> burnWave;
    public List<BlockState> firePalette;
    public List<BlockState> preBurn;
    public List<BlockState> midBurn;
    public List<BlockState> midHeatPalette;
    public List<BlockState> highHeat;

    // Pre-combined heat lists (base + midHeatPalette), built once at construction.
    List<BlockState> grassPaletteWithMidHeat;
    List<BlockState> stonePaletteWithMidHeat;
    List<BlockState> darkPrismarinePaletteWithMidHeat;

    // Pre-built smoke variant lists.
    private List<BlockState> smokeVariantA;
    private List<BlockState> smokeVariantB;

    // -------------------------------------------------------------------------
    // Default (orange) constructor
    // -------------------------------------------------------------------------
    public BurnPalette() {
        firePalette = stateOf(Blocks.FIRE);
        preBurn = stateOf(Blocks.ORANGE_TERRACOTTA);
        midBurn = stateOf(Blocks.MAGMA_BLOCK, Blocks.ORANGE_TERRACOTTA);
        midHeatPalette = stateOf(Blocks.MAGMA_BLOCK);
        highHeat = stateOf(Blocks.MAGMA_BLOCK, Blocks.BLACKSTONE, Blocks.MUD, Blocks.TUFF);

        burnWave = mapToWave(stateOf(
                rep(3, Blocks.ORANGE_STAINED_GLASS),
                rep(2, Blocks.SHROOMLIGHT),
                Blocks.ORANGE_TERRACOTTA,
                Blocks.ORANGE_CONCRETE,
                Blocks.HONEYCOMB_BLOCK
        ));

        buildDerivedFields();
    }

    /** Must be called after all base palette fields are set. */
    private void buildDerivedFields() {
        grassPaletteWithMidHeat = combined(grassPalette, midHeatPalette);
        stonePaletteWithMidHeat = combined(stonePalette, midHeatPalette);
        darkPrismarinePaletteWithMidHeat = combined(darkPrismarinePalette, midHeatPalette);

        smokeVariantA = stateOf(
                Blocks.SHROOMLIGHT,
                Blocks.ORANGE_CONCRETE,
                Blocks.ORANGE_TERRACOTTA,
                Blocks.BLACK_CONCRETE,
                Blocks.ORANGE_TERRACOTTA
        );
        smokeVariantB = stateOf(
                Blocks.SHROOMLIGHT,
                Blocks.ORANGE_CONCRETE,
                Blocks.ORANGE_STAINED_GLASS,
                Blocks.BLACK_STAINED_GLASS
        );
    }

    private static List<BlockState> combined(List<BlockState> a, List<BlockState> b) {
        List<BlockState> out = new ArrayList<>(a.size() + b.size());
        out.addAll(a);
        out.addAll(b);
        return out;
    }

    // -------------------------------------------------------------------------
    // Named palette factories
    // -------------------------------------------------------------------------

    public static BurnPalette glassOnly() {
        BurnPalette p = new BurnPalette();
        p.burnWave = List.of(List.of(Blocks.ORANGE_STAINED_GLASS.defaultBlockState()));
        return p;
    }

    public static BurnPalette orangeWithoutTrail() {
        BurnPalette p = new BurnPalette();
        List<List<BlockState>> out = new ArrayList<>();
        for (List<BlockState> item : p.burnWave) {
            out.add(List.of(item.get(0)));
        }
        p.burnWave = out;
        return p;
    }

    // -------------------------------------------------------------------------
    // Core burn logic
    // -------------------------------------------------------------------------

    public List<Pair<Long, BlockState>> burn(ServerLevel level, BlockPos pos, float heat) {
        BlockState state = level.getBlockState(pos);

        if (state.is(Blocks.SHORT_GRASS)) {
            return List.of(new Pair<>(rand(1, 10), random(shortGrassPalette)));
        }

        if (state.is(BlockTags.LOGS_THAT_BURN)) {
            return List.of(
                    new Pair<>(rand(0, 1), random(preBurn)),
                    new Pair<>(rand(1, 10), random(midBurn)),
                    new Pair<>(rand(20, 200), Blocks.POLISHED_BASALT.defaultBlockState())
            );
        }

        if (state.is(BlockTags.LEAVES)) {
            return List.of(
                    new Pair<>(rand(1, 10), random(preBurn)),
                    new Pair<>(rand(1, 10), random(midBurn)),
                    new Pair<>(rand(10, 80), random(leavesPalette))
            );
        }

        if (state.is(Blocks.STONE_BRICK_STAIRS)) {
            return List.of(new Pair<>(rand(10, 60), random(stoneBrickStairsPalette)));
        }

        if (state.is(Blocks.STONE_BRICK_SLAB)) {
            return List.of(new Pair<>(rand(10, 60), random(stoneBrickSlabPalette)));
        }

        if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.SNOW_BLOCK)
                || state.is(Blocks.SAND) || state.is(Blocks.PODZOL) || state.is(Blocks.COARSE_DIRT)) {
            return List.of(
                    new Pair<>(rand(1, 5), random(midBurn)),
                    new Pair<>(rand(10, 60), random(selectHeat(grassPalette, grassPaletteWithMidHeat, heat)))
            );
        }

        if (state.is(Blocks.STONE) || state.is(Blocks.ANDESITE) || state.is(Blocks.GRAVEL)
                || state.is(Blocks.COBBLESTONE)) {
            return List.of(
                    new Pair<>(rand(1, 5), random(midBurn)),
                    new Pair<>(rand(10, 60), random(selectHeat(stonePalette, stonePaletteWithMidHeat, heat)))
            );
        }

        if (state.is(Blocks.DARK_PRISMARINE)) {
            return List.of(new Pair<>(rand(10, 60), random(selectHeat(darkPrismarinePalette, darkPrismarinePaletteWithMidHeat, heat))));
        }

        if (state.is(BlockTags.WOODEN_FENCES) || state.is(BlockTags.WOODEN_DOORS)
                || state.is(BlockTags.WOODEN_TRAPDOORS)) {
            if (ThreadLocalRandom.current().nextFloat() < 0.2f) {
                return null;
            }
            return List.of(new Pair<>(rand(10, 60), Blocks.AIR.defaultBlockState()));
        }

        if (!state.isSolid()) {
            return List.of(new Pair<>(rand(1, 10), Blocks.AIR.defaultBlockState()));
        }

        return null;
    }

    public List<BlockState> smokePalette() {
        return ThreadLocalRandom.current().nextFloat() < 0.25f ? smokeVariantA : smokeVariantB;
    }

    // -------------------------------------------------------------------------
    // Blue palette
    // -------------------------------------------------------------------------

    private static BurnPalette createBlue() {
        BurnPalette p = new BurnPalette();

        List<List<BlockState>> extension = new ArrayList<>();
        for (BlockState data : stateOf(
                rep(3, Blocks.BLUE_STAINED_GLASS),
                rep(2, Blocks.LAPIS_BLOCK),
                Blocks.BLUE_TERRACOTTA,
                Blocks.BLUE_CONCRETE,
                Blocks.TUBE_CORAL_BLOCK
        )) {
            List<BlockState> row = new ArrayList<>();
            row.add(data);
            row.add(data);
            row.add(data);
            row.addAll(burnWaveTrail);
            extension.add(row);
            extension.add(new ArrayList<>(row));
        }

        List<List<BlockState>> merged = new ArrayList<>(orange.burnWave);
        merged.addAll(extension);
        p.burnWave = merged;

        List<net.minecraft.world.level.block.Block> blocks =
                List.of(Blocks.BLUE_CONCRETE, Blocks.TUBE_CORAL_BLOCK, Blocks.CRYING_OBSIDIAN);
        p.preBurn = repeatBlocks(orange.preBurn, 4 * blocks.size());
        for (var block : blocks) {
            p.preBurn.add(block.defaultBlockState());
        }
        p.midBurn = repeatBlocks(orange.midBurn, 4 * blocks.size());
        for (var block : blocks) {
            p.midBurn.add(block.defaultBlockState());
        }
        return p;
    }

    // -------------------------------------------------------------------------
    // Purple palette
    // -------------------------------------------------------------------------

    private static BurnPalette createPurple() {
        BurnPalette p = new BurnPalette();

        List<List<BlockState>> extension = new ArrayList<>();
        for (BlockState data : stateOf(
                rep(2, Blocks.PURPLE_STAINED_GLASS),
                rep(2, Blocks.CHORUS_PLANT),
                rep(2, Blocks.AMETHYST_BLOCK),
                Blocks.PURPLE_TERRACOTTA,
                Blocks.PURPLE_CONCRETE,
                Blocks.BUBBLE_CORAL_BLOCK
        )) {
            List<BlockState> row = new ArrayList<>();
            row.add(data);
            row.add(data);
            row.add(data);
            row.addAll(burnWaveTrail);
            extension.add(row);
            extension.add(new ArrayList<>(row));
        }

        List<List<BlockState>> merged = new ArrayList<>(orange.burnWave);
        merged.addAll(extension);
        p.burnWave = merged;

        List<net.minecraft.world.level.block.Block> blocks =
                List.of(Blocks.PURPLE_CONCRETE, Blocks.AMETHYST_BLOCK, Blocks.CRYING_OBSIDIAN);
        p.preBurn = repeatBlocks(orange.preBurn, 4 * blocks.size());
        for (var block : blocks) {
            p.preBurn.add(block.defaultBlockState());
        }
        p.midBurn = repeatBlocks(orange.midBurn, 4 * blocks.size());
        for (var block : blocks) {
            p.midBurn.add(block.defaultBlockState());
        }
        return p;
    }

    // -------------------------------------------------------------------------
    // Static palettes (must be declared before the static instances block)
    // -------------------------------------------------------------------------

    private static final List<BlockState> burnWaveTrail = stateOf(
            Blocks.ORANGE_STAINED_GLASS,
            Blocks.BLACK_STAINED_GLASS,
            Blocks.GRAY_STAINED_GLASS,
            Blocks.LIGHT_GRAY_STAINED_GLASS
    );

    private static final List<BlockState> shortGrassPalette = stateOf(
            rep(4, Blocks.DEAD_BUSH),
            rep(8, Blocks.DEAD_BUSH),
            Blocks.DEAD_BRAIN_CORAL_FAN,
            Blocks.DEAD_BRAIN_CORAL,
            Blocks.DEAD_BUBBLE_CORAL_FAN,
            Blocks.DEAD_FIRE_CORAL_FAN,
            Blocks.DEAD_FIRE_CORAL,
            Blocks.DEAD_HORN_CORAL_FAN,
            Blocks.DEAD_TUBE_CORAL_FAN
    );

    private static final List<BlockState> grassPalette = stateOf(
            rep(4, Blocks.COARSE_DIRT),
            rep(4, Blocks.ROOTED_DIRT),
            rep(2, Blocks.TUFF),
            Blocks.DEAD_HORN_CORAL_BLOCK,
            Blocks.DEAD_FIRE_CORAL_BLOCK
    );

    private static final List<BlockState> stonePalette = stateOf(
            rep(3, Blocks.TUFF),
            Blocks.ANDESITE,
            Blocks.DEAD_HORN_CORAL_BLOCK,
            Blocks.DEEPSLATE
    );

    private static final List<BlockState> leavesPalette = stateOf(
            Blocks.MANGROVE_ROOTS,
            rep(5, Blocks.AIR)
    );

    private static final List<BlockState> stoneBrickSlabPalette = stateOf(
            rep(15, Blocks.STONE_BRICK_SLAB),
            Blocks.ANDESITE_SLAB,
            Blocks.COBBLESTONE_SLAB,
            Blocks.TUFF_SLAB
    );

    private static final List<BlockState> stoneBrickStairsPalette = stateOf(
            rep(15, Blocks.STONE_BRICK_STAIRS),
            Blocks.ANDESITE_STAIRS,
            Blocks.COBBLESTONE_STAIRS,
            Blocks.TUFF_STAIRS
    );

    private static final List<BlockState> darkPrismarinePalette = stateOf(
            rep(15, Blocks.DARK_PRISMARINE),
            Blocks.DEEPSLATE,
            Blocks.COBBLED_DEEPSLATE
    );

    // -------------------------------------------------------------------------
    // Static instances (declared after palette constants to guarantee init order)
    // -------------------------------------------------------------------------
    public static final BurnPalette orange;
    public static final BurnPalette blue;
    public static final BurnPalette purple;

    static {
        orange = new BurnPalette();
        blue = createBlue();
        purple = createPurple();
    }

    // -------------------------------------------------------------------------
    // Helper methods
    // -------------------------------------------------------------------------

    /** Returns the appropriate heat-adjusted list without allocating. */
    private List<BlockState> selectHeat(List<BlockState> base, List<BlockState> withMidHeat, float heat) {
        if (heat > 0.9f) return highHeat;
        if (heat < 0.2f) return base;
        return withMidHeat;
    }

    private static long rand(long a, long b) {
        return ThreadLocalRandom.current().nextLong(a, b);
    }

    private static <T> T random(List<T> list) {
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

    private static net.minecraft.world.level.block.Block[] rep(int count, net.minecraft.world.level.block.Block block) {
        net.minecraft.world.level.block.Block[] out = new net.minecraft.world.level.block.Block[count];
        Arrays.fill(out, block);
        return out;
    }

    /** Strips the WATERLOGGED property (sets it to false) so no palette entry ever places water. */
    private static BlockState dry(BlockState state) {
        if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
            return state.setValue(BlockStateProperties.WATERLOGGED, false);
        }
        return state;
    }

    /** Builds a flat list of BlockStates from Blocks and Block[] varargs. */
    @SafeVarargs
    private static List<BlockState> stateOf(Object... blocksAndArrays) {
        List<BlockState> out = new ArrayList<>();
        for (Object item : blocksAndArrays) {
            if (item instanceof net.minecraft.world.level.block.Block block) {
                out.add(dry(block.defaultBlockState()));
            } else if (item instanceof net.minecraft.world.level.block.Block[] blocks) {
                for (net.minecraft.world.level.block.Block b : blocks) {
                    out.add(dry(b.defaultBlockState()));
                }
            } else if (item instanceof BlockState state) {
                out.add(dry(state));
            } else {
                throw new IllegalArgumentException("Unsupported stateOf argument: " + item);
            }
        }
        return out;
    }

    /** Overload accepting a pre-built BlockState as a direct element. */
    private static List<BlockState> stateOf(BlockState... states) {
        List<BlockState> out = new ArrayList<>(states.length);
        for (BlockState s : states) out.add(dry(s));
        return out;
    }

    private static List<List<BlockState>> mapToWave(List<BlockState> blocks) {
        List<List<BlockState>> out = new ArrayList<>();
        for (BlockState block : blocks) {
            List<BlockState> row = new ArrayList<>();
            row.add(block);
            row.add(block);
            row.add(block);
            row.addAll(burnWaveTrail);
            out.add(row);
        }
        return out;
    }

    private static List<BlockState> repeatBlocks(List<BlockState> list, int count) {
        List<BlockState> out = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            out.addAll(list);
        }
        return out;
    }

    // -------------------------------------------------------------------------
    // Pair record
    // -------------------------------------------------------------------------

    public record Pair<A, B>(A first, B second) {
    }
}

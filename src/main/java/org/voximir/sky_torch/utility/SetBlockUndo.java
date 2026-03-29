package org.voximir.sky_torch.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.Map;

public final class SetBlockUndo {
    private static final Map<BlockPos, BlockState> savedData = new HashMap<>();

    private SetBlockUndo() {
    }

    public static void setBlock(ServerLevel level, BlockPos pos, BlockState state) {
        saveAdjacentBlocks(level, pos);
        level.setBlock(pos, state, 3);
    }

    public static void undoSetBlock(ServerLevel level) {
        for (Map.Entry<BlockPos, BlockState> entry : savedData.entrySet()) {
            level.setBlock(entry.getKey(), entry.getValue(), 3);
        }
        savedData.clear();
    }

    private static void saveAdjacentBlocks(ServerLevel level, BlockPos pos) {
        saveBlock(level, pos);
        saveBlock(level, pos.above());
        saveBlock(level, pos.below());
        saveBlock(level, pos.east());
        saveBlock(level, pos.west());
        saveBlock(level, pos.south());
        saveBlock(level, pos.north());
    }

    private static void saveBlock(ServerLevel level, BlockPos pos) {
        if (!savedData.containsKey(pos)) {
            savedData.put(pos.immutable(), level.getBlockState(pos));
        }
    }
}

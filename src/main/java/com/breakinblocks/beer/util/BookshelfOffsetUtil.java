package com.breakinblocks.beer.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;

import java.util.List;

public class BookshelfOffsetUtil {
    
    public static List<BlockPos> getOffsetsForTable(Level level, BlockPos pos) {
        if (EnchantingTableDataUtil.hasEnchantingTable(level, pos)) {
            var rangeData = EnchantingTableDataUtil.getRangeData(level, pos);
            return BlockPos.betweenClosedStream(
                -rangeData.getEffectiveRangeX(), 0, -rangeData.getEffectiveRangeZ(),
                rangeData.getEffectiveRangeX(), rangeData.getEffectiveRangeY(), rangeData.getEffectiveRangeZ()
            )
            .filter(blockPos -> Math.abs(blockPos.getX()) >= 2 || Math.abs(blockPos.getZ()) >= 2)
            .map(BlockPos::immutable)
            .toList();
        }

        return EnchantingTableBlock.BOOKSHELF_OFFSETS;
    }
}
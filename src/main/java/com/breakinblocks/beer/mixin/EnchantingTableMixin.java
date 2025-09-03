package com.breakinblocks.beer.mixin;

import com.breakinblocks.beer.Config;
import com.breakinblocks.beer.util.EnchantingTableDataUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.*;
import net.minecraft.world.level.block.EnchantingTableBlock;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantingTableBlock.class)
public class EnchantingTableMixin {
    @Shadow @Final @Mutable
    public static List<BlockPos> BOOKSHELF_OFFSETS;

//    @Inject(method = "<clinit>", at = @At("TAIL"))
//    private static void modifyBookshelfOffsets(CallbackInfo ci) {
//        // Set to maximum possible range for compatibility
//        BOOKSHELF_OFFSETS = BlockPos.betweenClosedStream(-16, -16, -16, 16, 16, 16)
//                .filter(blockPos -> Math.abs(blockPos.getX()) > 1 || Math.abs(blockPos.getZ()) > 1)
//                .map(BlockPos::immutable)
//                .toList();
//    }

}

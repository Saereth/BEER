package com.breakinblocks.beer.mixin;

import com.breakinblocks.beer.Config;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
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

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void modifyBookshelfOffsets(CallbackInfo ci) {
        BOOKSHELF_OFFSETS = BlockPos.betweenClosedStream(-16, -16, -16, 16, 16, 16)
                .filter(blockPos -> Math.abs(blockPos.getX()) > 1 || Math.abs(blockPos.getZ()) > 1)
                .map(BlockPos::immutable)
                .toList();
    }

    @Unique
    private static void Beer$updateBookshelfOffsetsFromConfig() {
        BOOKSHELF_OFFSETS = BlockPos.betweenClosedStream(
                    -Config.rangeX, -Config.rangeY, -Config.rangeZ,
                    Config.rangeX, Config.rangeY, Config.rangeZ
                )
                .filter(blockPos -> Math.abs(blockPos.getX()) > 1 || Math.abs(blockPos.getZ()) > 1)
                .map(BlockPos::immutable)
                .toList();
    }

    @Inject(method = "newBlockEntity", at = @At("HEAD"))
    private void onPlaceInject(BlockPos pos, BlockState state, CallbackInfoReturnable<BlockEntity> cir) {
        Beer$updateBookshelfOffsetsFromConfig();
    }

}

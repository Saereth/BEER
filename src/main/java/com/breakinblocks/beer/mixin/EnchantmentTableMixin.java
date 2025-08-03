package com.breakinblocks.beer.mixin;

import com.breakinblocks.beer.BeerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantmentTableBlock.class)
public class EnchantmentTableMixin {
    @Shadow @Final @Mutable
    public static List<BlockPos> BOOKSHELF_OFFSETS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void modifyBookshelfOffsets(CallbackInfo ci) {
        // Set to default values, or leave as-is
        BOOKSHELF_OFFSETS = BlockPos.betweenClosedStream(-2, -2, -2, 2, 2, 2)
                .filter(blockPos -> Math.abs(blockPos.getX()) > 1 || Math.abs(blockPos.getZ()) > 1)
                .map(BlockPos::immutable)
                .toList();
    }

    private static void updateBookshelfOffsetsFromConfig() {
        int rangeX = BeerConfig.getRangeX();
        int rangeY = BeerConfig.getRangeY();
        int rangeZ = BeerConfig.getRangeZ();
        BOOKSHELF_OFFSETS = BlockPos.betweenClosedStream(-rangeX, -rangeY, -rangeZ, rangeX, rangeY, rangeZ)
                .filter(blockPos -> Math.abs(blockPos.getX()) > 1 || Math.abs(blockPos.getZ()) > 1)
                .map(BlockPos::immutable)
                .toList();
    }

    @Inject(method = "newBlockEntity", at = @At("HEAD"))
    private void onPlaceInject(BlockPos p_153186_, BlockState p_153187_, CallbackInfoReturnable<BlockEntity> cir) {
        updateBookshelfOffsetsFromConfig();
    }
}
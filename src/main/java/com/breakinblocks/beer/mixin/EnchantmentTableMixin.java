package com.breakinblocks.beer.mixin;

import com.breakinblocks.beer.BeerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantmentTableBlock.class)
public class EnchantmentTableMixin {
    @Shadow @Final @Mutable
    public static List<BlockPos> BOOKSHELF_OFFSETS;

    private static void updateBookshelfOffsetsFromConfig() {
        int rangeX = BeerConfig.getRangeX();
        int rangeY = BeerConfig.getRangeY();
        int rangeZ = BeerConfig.getRangeZ();
        BOOKSHELF_OFFSETS = BlockPos.betweenClosedStream(-rangeX, -rangeY, -rangeZ, rangeX, rangeY, rangeZ)
                .filter(blockPos -> Math.abs(blockPos.getX()) > 1 || Math.abs(blockPos.getZ()) > 1)
                .map(BlockPos::immutable)
                .toList();
    }

    @Inject(method = "use", at = @At("HEAD"))
    private void onUseInject(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        updateBookshelfOffsetsFromConfig();
    }
}
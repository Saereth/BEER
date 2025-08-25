package com.breakinblocks.beer.compat;


import com.breakinblocks.beer.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class BeerJadePlugin implements IWailaPlugin, IBlockComponentProvider {

    @Override
    public void registerClient(IWailaClientRegistration reg) {
        reg.registerBlockComponent(this, Block.class);
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlock() == Blocks.ENCHANTING_TABLE) {
            tooltip.add(Component.translatable("tooltip.beer.enchanting_table.range",
                    Config.rangeX, Config.rangeY, Config.rangeZ).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("beer", "beer");
    }
}

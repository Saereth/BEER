package com.breakinblocks.beer.compat;

import com.breakinblocks.beer.util.EnchantingTableDataUtil;
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
            try {
                int[] boundingBoxSizes = EnchantingTableDataUtil.getEffectiveBoundingBoxSizes(accessor.getLevel(), accessor.getPosition());
                tooltip.add(Component.translatable("tooltip.beer.enchanting_table.range",
                        boundingBoxSizes[0], boundingBoxSizes[1], boundingBoxSizes[2]).withStyle(ChatFormatting.GRAY));
                
                // Debug: Show if there are item modifications
                var data = EnchantingTableDataUtil.getRangeData(accessor.getLevel(), accessor.getPosition());
                if (data.hasItemModifications()) {
                    tooltip.add(Component.literal("§8[Item mods: " + data.getItemModifiersX() + "," + 
                        data.getItemModifiersY() + "," + data.getItemModifiersZ() + "]"));
                }
            } catch (Exception e) {
                tooltip.add(Component.literal("§cError reading enchanting table data").withStyle(ChatFormatting.RED));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return ResourceLocation.fromNamespaceAndPath("beer", "beer");
    }
}

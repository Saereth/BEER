package com.breakinblocks.beer.client;

import com.breakinblocks.beer.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(value = Dist.CLIENT)
public class TooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (event.getItemStack().getItem() == Items.ENCHANTING_TABLE) {
            event.getToolTip().add(Component.translatable("tooltip.beer.enchanting_table.range", 
                Config.rangeX, Config.rangeY, Config.rangeZ).withStyle(ChatFormatting.GRAY));
        }
    }
}
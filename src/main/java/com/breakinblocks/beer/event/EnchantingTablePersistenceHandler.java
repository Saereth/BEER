package com.breakinblocks.beer.event;

import com.breakinblocks.beer.Config;
import com.breakinblocks.beer.data.EnchantingTableRangeData;
import com.breakinblocks.beer.util.EnchantingTableDataUtil;
import com.breakinblocks.beer.network.NetworkHandler;
import com.breakinblocks.beer.network.SyncEnchantingDataPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber
public class EnchantingTablePersistenceHandler {

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!Config.enableItemModifiers) {
            return; // Don't restore data if item modifiers are disabled
        }

        if (!(event.getLevel() instanceof ServerLevel level)) {
            return; // Only handle server-side
        }

        BlockPos pos = event.getPos();
        
        // Only handle enchanting tables
        if (!event.getPlacedBlock().is(Blocks.ENCHANTING_TABLE)) {
            return;
        }

        // Get the item stack that was placed
        if (!(event.getEntity() instanceof net.minecraft.world.entity.player.Player player)) {
            return;
        }

        ItemStack stack = player.getMainHandItem();
        if (!stack.is(Blocks.ENCHANTING_TABLE.asItem())) {
            stack = player.getOffhandItem();
            if (!stack.is(Blocks.ENCHANTING_TABLE.asItem())) {
                return;
            }
        }

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        System.out.println("BEER DEBUG: Placing enchanting table at " + pos);
        System.out.println("BEER DEBUG: Stack has custom data: " + (customData != null));
        
        if (customData != null) {
            CompoundTag beerData = customData.copyTag();
            System.out.println("BEER DEBUG: Custom data contains: " + beerData);
            
            if (beerData.contains("ItemModifiersX") || beerData.contains("ItemModifiersY") || beerData.contains("ItemModifiersZ")) {
                int modX = beerData.getInt("ItemModifiersX");
                int modY = beerData.getInt("ItemModifiersY");
                int modZ = beerData.getInt("ItemModifiersZ");
                
                System.out.println("BEER DEBUG: Restoring modifiers - X:" + modX + " Y:" + modY + " Z:" + modZ);
                
                // Restore the modifier data using the existing utility method
                // Schedule for next tick to ensure block entity exists
                level.getServer().execute(() -> {
                    EnchantingTableDataUtil.setRanges(level, pos, modX, modY, modZ);
                    
                    // Get the restored data to sync to clients
                    EnchantingTableRangeData restoredData = EnchantingTableDataUtil.getRangeData(level, pos);
                    
                    // Sync the data to clients so Jade can see it
                    SyncEnchantingDataPacket syncPacket = SyncEnchantingDataPacket.create(pos, restoredData);
                    NetworkHandler.sendToAllPlayers(syncPacket);
                    
                    // Get the block entity and mark it as changed to force sync
                    var blockEntity = level.getBlockEntity(pos);
                    if (blockEntity != null) {
                        blockEntity.setChanged();
                    }
                    
                    // Force block update to refresh visual components
                    level.sendBlockUpdated(pos, event.getPlacedBlock(), event.getPlacedBlock(), 3);
                    
                    System.out.println("BEER DEBUG: Modifiers restored and synced to clients");
                });
            } else {
                System.out.println("BEER DEBUG: No modifier data found in custom data");
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!Config.enableItemModifiers) {
            return; // Don't preserve data if item modifiers are disabled
        }

        if (!(event.getLevel() instanceof ServerLevel level)) {
            return; // Only handle server-side
        }
        
        BlockPos pos = event.getPos();
        
        // Only handle enchanting tables
        if (!event.getState().is(Blocks.ENCHANTING_TABLE)) {
            return;
        }

        // Debug: Print that we're handling the break
        System.out.println("BEER DEBUG: Enchanting table broken at " + pos);

        // Get the enchanting table data
        EnchantingTableRangeData data = EnchantingTableDataUtil.getRangeData(level, pos);
        
        // Debug: Print the modifier data
        System.out.println("BEER DEBUG: Modifiers - X:" + data.getItemModifiersX() + " Y:" + data.getItemModifiersY() + " Z:" + data.getItemModifiersZ());
        
        // Only preserve data if there are actual modifications
        if (!data.hasItemModifications()) {
            System.out.println("BEER DEBUG: No modifications found, allowing normal break");
            return;
        }

        System.out.println("BEER DEBUG: Modifications found, preserving data");

        // Cancel the default break event to handle drops manually
        event.setCanceled(true);

        // Remove the block manually
        level.removeBlock(pos, false);

        // Create the enchanting table item with preserved data
        ItemStack enchantingTableStack = new ItemStack(Blocks.ENCHANTING_TABLE);
        
        CompoundTag beerData = new CompoundTag();
        beerData.putInt("ItemModifiersX", data.getItemModifiersX());
        beerData.putInt("ItemModifiersY", data.getItemModifiersY());
        beerData.putInt("ItemModifiersZ", data.getItemModifiersZ());
        
        CustomData customData = CustomData.of(beerData);
        enchantingTableStack.set(DataComponents.CUSTOM_DATA, customData);
        
        // Add lore to show the modifiers (but keep original name)
        java.util.List<net.minecraft.network.chat.Component> lore = new java.util.ArrayList<>();
        
        // Title line
        lore.add(net.minecraft.network.chat.Component.literal("Bookshelf Range Modifiers:")
                .withStyle(net.minecraft.ChatFormatting.GRAY));
        
        // Build colored modifier line
        var modifierLine = net.minecraft.network.chat.Component.empty();
        boolean hasModifiers = false;
        
        if (data.getItemModifiersX() != 0) {
            if (hasModifiers) modifierLine = modifierLine.append(" ");
            modifierLine = modifierLine.append(net.minecraft.network.chat.Component.literal("X: " + (data.getItemModifiersX() > 0 ? "+" : "") + data.getItemModifiersX())
                    .withStyle(net.minecraft.ChatFormatting.RED));
            hasModifiers = true;
        }
        if (data.getItemModifiersY() != 0) {
            if (hasModifiers) modifierLine = modifierLine.append(" ");
            modifierLine = modifierLine.append(net.minecraft.network.chat.Component.literal("Y: " + (data.getItemModifiersY() > 0 ? "+" : "") + data.getItemModifiersY())
                    .withStyle(net.minecraft.ChatFormatting.GREEN));
            hasModifiers = true;
        }
        if (data.getItemModifiersZ() != 0) {
            if (hasModifiers) modifierLine = modifierLine.append(" ");
            modifierLine = modifierLine.append(net.minecraft.network.chat.Component.literal("Z: " + (data.getItemModifiersZ() > 0 ? "+" : "") + data.getItemModifiersZ())
                    .withStyle(net.minecraft.ChatFormatting.BLUE));
        }
        
        lore.add(modifierLine);
        
        enchantingTableStack.set(DataComponents.LORE, new net.minecraft.world.item.component.ItemLore(lore));

        // Debug: Confirm data was set
        System.out.println("BEER DEBUG: Data set on item: " + customData.copyTag());

        // Drop the modified enchanting table
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.5; 
        double z = pos.getZ() + 0.5;
        ItemEntity itemEntity = new ItemEntity(level, x, y, z, enchantingTableStack);
        level.addFreshEntity(itemEntity);

        System.out.println("BEER DEBUG: Modified enchanting table dropped");

        // Award experience points like normal enchanting table breaking (default 0 for enchanting tables)
        event.getState().getBlock().popExperience(level, pos, 0);
    }
}
package com.breakinblocks.beer.event;

import com.breakinblocks.beer.Config;
import com.breakinblocks.beer.data.BeerDataAttachments;
import com.breakinblocks.beer.data.EnchantingTableRangeData;
import com.breakinblocks.beer.network.ApplyItemModifierPacket;
import com.breakinblocks.beer.network.NetworkHandler;
import com.breakinblocks.beer.network.SyncEnchantingDataPacket;
import com.breakinblocks.beer.util.EnchantingTableDataUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = "beer")
public class ItemModifierHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!Config.enableItemModifiers) {
            return;
        }

        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        InteractionHand hand = event.getHand();
        
        // Only handle main hand interactions to avoid double processing
        if (hand != InteractionHand.MAIN_HAND) {
            return;
        }
        
        // Must be shift-clicking
        if (!player.isShiftKeyDown()) {
            return;
        }
        
        // Must be clicking an enchanting table
        if (!level.getBlockState(pos).is(Blocks.ENCHANTING_TABLE)) {
            return;
        }
        
        ItemStack heldItem = player.getItemInHand(hand);
        ItemStack offhandItem = player.getOffhandItem();
        
        // Check for reset command (stick)
        if (heldItem.is(Items.STICK)) {
            if (!level.isClientSide()) {
                EnchantingTableDataUtil.forceResetAll(level, pos);
                level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, 0.8F);
                System.out.println("[BEER DEBUG] Reset enchanting table data to defaults");
            }
            event.setCanceled(true);
            return;
        }
        
        // Determine modifier type based on held item
        ModifierType modifierType = getModifierType(heldItem);
        if (modifierType == null) {
            return;
        }
        
        // Check if nether quartz is in offhand (decrease mode)
        boolean decreaseMode = offhandItem.is(Items.QUARTZ);
        
        // On server side, apply the modification
        if (!level.isClientSide()) {
            applyModification(level, pos, player, modifierType, decreaseMode, heldItem);
        }
        
        // Cancel the interaction to prevent opening the enchanting table GUI
        event.setCanceled(true);
    }
    
    private static ModifierType getModifierType(ItemStack stack) {
        if (stack.is(Items.REDSTONE)) {
            return ModifierType.X_WIDTH;
        } else if (stack.is(Items.GLOWSTONE_DUST)) {
            return ModifierType.Z_WIDTH;
        } else if (stack.is(Items.LAPIS_LAZULI)) {
            return ModifierType.Y_HEIGHT;
        }
        return null;
    }
    
    private static void applyModification(Level level, BlockPos pos, Player player, ModifierType modifierType, boolean decreaseMode, ItemStack heldItem) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof EnchantingTableBlockEntity)) {
            return;
        }
        
        // Get or create enchanting table data
        EnchantingTableRangeData data = blockEntity.getData(BeerDataAttachments.ENCHANTING_TABLE_RANGE.get());
        if (data == null) {
            data = new EnchantingTableRangeData();
            blockEntity.setData(BeerDataAttachments.ENCHANTING_TABLE_RANGE.get(), data);
        }
        
        int modifierAmount = Config.modifierAmountPerUse;
        if (decreaseMode) {
            modifierAmount = -modifierAmount;
        }
        
        // Apply the modification based on type
        boolean success = false;
        System.out.println("[BEER DEBUG] Before modification - Item mods: X=" + data.getItemModifiersX() + ", Y=" + data.getItemModifiersY() + ", Z=" + data.getItemModifiersZ());
        System.out.println("[BEER DEBUG] Modifier type: " + modifierType + ", Amount: " + modifierAmount);
        
        switch (modifierType) {
            case X_WIDTH:
                int newX = data.getItemModifiersX() + modifierAmount;
                if (isValidModifierX(newX)) {
                    data.addItemModifierX(modifierAmount);
                    success = true;
                    System.out.println("[BEER DEBUG] X_WIDTH modification successful");
                } else {
                    System.out.println("[BEER DEBUG] X_WIDTH modification failed - valid modifier: " + isValidModifierX(newX));
                }
                break;
            case Z_WIDTH:
                int newZ = data.getItemModifiersZ() + modifierAmount;
                if (isValidModifierZ(newZ)) {
                    data.addItemModifierZ(modifierAmount);
                    success = true;
                }
                break;
            case Y_HEIGHT:
                int newY = data.getItemModifiersY() + modifierAmount;
                if (isValidModifierY(newY)) {
                    data.addItemModifierY(modifierAmount);
                    success = true;
                }
                break;
        }
        
        System.out.println("[BEER DEBUG] After modification - Item mods: X=" + data.getItemModifiersX() + ", Y=" + data.getItemModifiersY() + ", Z=" + data.getItemModifiersZ());
        System.out.println("[BEER DEBUG] Modification success: " + success);
        
        if (success) {
            // Handle XP costs if enabled
            if (Config.enableXpCosts) {
                int xpCost = Config.xpCostPerModifier;
                if (decreaseMode) {
                    // Decreasing gives XP back
                    player.giveExperiencePoints(xpCost);
                    
                    // Show XP gained message in action bar
                    if (player instanceof ServerPlayer serverPlayer) {
                        Component xpGainMessage = Component.translatable("beer.message.xp_gained", xpCost)
                                .withStyle(net.minecraft.ChatFormatting.GREEN);
                        serverPlayer.sendSystemMessage(xpGainMessage, true);
                    }
                } else {
                    // Increasing costs XP - check if player has enough
                    int totalXp = getTotalExperience(player);
                    if (totalXp < xpCost && !player.getAbilities().instabuild) {
                        // Not enough XP - play failure sound and show message
                        level.playSound(null, pos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 1.0F, 1.0F);
                        
                        // Send action bar message about XP requirement
                        if (player instanceof ServerPlayer serverPlayer) {
                            Component xpMessage = Component.translatable("beer.message.insufficient_xp", xpCost, totalXp)
                                    .withStyle(net.minecraft.ChatFormatting.RED);
                            serverPlayer.sendSystemMessage(xpMessage, true); // true = action bar
                            NetworkHandler.sendToPlayer(new ApplyItemModifierPacket(pos, modifierType, true, false, 
                                0, 0, 0), serverPlayer);
                        }
                        return; // Exit without applying changes
                    }
                    // Take XP from player
                    if (!player.getAbilities().instabuild) {
                        addExperience(player, -xpCost);
                    }
                    
                    // Show XP consumed message in action bar
                    if (player instanceof ServerPlayer serverPlayer) {
                        Component xpConsumedMessage = Component.translatable("beer.message.xp_consumed", xpCost)
                                .withStyle(net.minecraft.ChatFormatting.YELLOW);
                        serverPlayer.sendSystemMessage(xpConsumedMessage, true);
                    }
                }
            }
            
            // Mark the block entity as dirty to save data
            blockEntity.setChanged();
            
            // Consume item if configured to do so
            if (Config.consumeItems && !player.getAbilities().instabuild) {
                heldItem.shrink(1);
            }
            
            // Play success sound
            level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 1.0F, 1.2F);
            
            // Send success packet to client for visual effects
            if (player instanceof ServerPlayer serverPlayer) {
                NetworkHandler.sendToPlayer(new ApplyItemModifierPacket(pos, modifierType, !decreaseMode, true, 
                    data.getEffectiveRangeX(), data.getEffectiveRangeY(), data.getEffectiveRangeZ()), serverPlayer);
            }
            
            // Sync the updated data to nearby clients so Jade displays correctly
            if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                SyncEnchantingDataPacket syncPacket = SyncEnchantingDataPacket.create(pos, data);
                NetworkHandler.sendToPlayersNear(syncPacket, serverLevel, pos, 64.0);
            }
        } else {
            // Play failure sound
            level.playSound(null, pos, SoundEvents.VILLAGER_NO, SoundSource.BLOCKS, 1.0F, 1.0F);
            
            // Send failure packet to client
            if (player instanceof ServerPlayer serverPlayer) {
                NetworkHandler.sendToPlayer(new ApplyItemModifierPacket(pos, modifierType, !decreaseMode, false, 
                    0, 0, 0), serverPlayer);
            }
        }
    }
    
    private static boolean isValidModifier(int modifier) {
        return Math.abs(modifier) <= Config.maxItemModifiersPerAxis;
    }
    
    private static boolean isValidModifierX(int modifier) {
        // X can go from -2 to +maxItemModifiersPerAxis
        return modifier >= -2 && modifier <= Config.maxItemModifiersPerAxis;
    }
    
    private static boolean isValidModifierY(int modifier) {
        // Y can go from -1 to +maxItemModifiersPerAxis
        return modifier >= -1 && modifier <= Config.maxItemModifiersPerAxis;
    }
    
    private static boolean isValidModifierZ(int modifier) {
        // Z can go from -2 to +maxItemModifiersPerAxis
        return modifier >= -2 && modifier <= Config.maxItemModifiersPerAxis;
    }
    
    private static boolean isValidEffectiveRange(int effectiveRange, int maxAllowed) {
        return effectiveRange >= 1 && effectiveRange <= maxAllowed;
    }
    
    private static boolean isValidEffectiveRangeY(int effectiveRange, int maxAllowed) {
        return effectiveRange >= 0 && effectiveRange <= maxAllowed;
    }
    
    // XP utility methods
    private static int getTotalExperience(Player player) {
        return (int)(getExperienceForLevel(player.experienceLevel) + (player.experienceProgress * player.getXpNeededForNextLevel()));
    }
    
    private static int getExperienceForLevel(int level) {
        if (level >= 30) {
            return (int) (4.5 * level * level - 162.5 * level + 2220);
        } else if (level >= 16) {
            return (int) (2.5 * level * level - 40.5 * level + 360);
        } else {
            return level * level + 6 * level;
        }
    }
    
    private static void addExperience(Player player, int xp) {
        int totalXp = getTotalExperience(player) + xp;
        player.totalExperience = totalXp;
        
        // Set level and progress based on total XP
        if (totalXp <= 0) {
            player.experienceLevel = 0;
            player.experienceProgress = 0.0f;
            return;
        }
        
        int level = getLevelForExperience(totalXp);
        player.experienceLevel = level;
        
        int xpForCurrentLevel = getExperienceForLevel(level);
        int xpForNextLevel = getExperienceForLevel(level + 1);
        int currentLevelXp = totalXp - xpForCurrentLevel;
        int neededForNext = xpForNextLevel - xpForCurrentLevel;
        
        player.experienceProgress = (float) currentLevelXp / neededForNext;
    }
    
    private static int getLevelForExperience(int xp) {
        if (xp >= 1395) { // Level 30+
            return (int) (8.1 + Math.sqrt(0.4 * (xp - 1395)));
        } else if (xp >= 315) { // Level 16-29  
            return (int) (8.1 + Math.sqrt(0.4 * (xp - 315)));
        } else { // Level 0-15
            return (int) (-3 + Math.sqrt(9 + 4 * xp)) / 2;
        }
    }
    
    public enum ModifierType {
        X_WIDTH,
        Z_WIDTH,
        Y_HEIGHT
    }
}
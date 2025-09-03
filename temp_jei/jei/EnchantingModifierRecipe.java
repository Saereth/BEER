package com.breakinblocks.beer.compat.jei;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class EnchantingModifierRecipe {
    
    private final ItemStack modifierItem;
    private final String effectName;
    private final String descriptionKey;
    private final ModifierAxis axis;
    
    public EnchantingModifierRecipe(Item modifierItem, String effectName, String descriptionKey, ModifierAxis axis) {
        this.modifierItem = new ItemStack(modifierItem);
        this.effectName = effectName;
        this.descriptionKey = descriptionKey;
        this.axis = axis;
    }
    
    public ItemStack getModifierItem() {
        return modifierItem;
    }
    
    public String getEffectName() {
        return effectName;
    }
    
    public String getDescriptionKey() {
        return descriptionKey;
    }
    
    public ModifierAxis getAxis() {
        return axis;
    }
    
    public enum ModifierAxis {
        X("X-Width", 0xFF5555), // Red
        Y("Y-Height", 0x55FF55), // Green  
        Z("Z-Width", 0x5555FF), // Blue
        DECREASE("Decrease", 0xFF8C00); // Orange
        
        private final String displayName;
        private final int color;
        
        ModifierAxis(String displayName, int color) {
            this.displayName = displayName;
            this.color = color;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public int getColor() {
            return color;
        }
    }
}
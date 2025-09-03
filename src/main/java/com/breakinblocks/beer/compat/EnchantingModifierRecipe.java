package com.breakinblocks.beer.compat;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class EnchantingModifierRecipe {
    private final Ingredient mainhandInput;
    private final Ingredient offhandInput;
    private final boolean consumesOffhand;
    private final String effectKey;
    private final String descriptionKey;
    private final ModifierType type;
    
    public EnchantingModifierRecipe(Ingredient mainhandInput, Ingredient offhandInput, boolean consumesOffhand, 
                                    String effectKey, String descriptionKey, ModifierType type) {
        this.mainhandInput = mainhandInput;
        this.offhandInput = offhandInput;
        this.consumesOffhand = consumesOffhand;
        this.effectKey = effectKey;
        this.descriptionKey = descriptionKey;
        this.type = type;
    }
    
    public Ingredient getMainhandInput() {
        return mainhandInput;
    }
    
    public Ingredient getOffhandInput() {
        return offhandInput;
    }
    
    public boolean consumesOffhand() {
        return consumesOffhand;
    }
    
    public String getEffectKey() {
        return effectKey;
    }
    
    public String getDescriptionKey() {
        return descriptionKey;
    }
    
    public ModifierType getType() {
        return type;
    }
    
    public boolean hasOffhandInput() {
        return offhandInput != Ingredient.EMPTY;
    }
    
    public enum ModifierType {
        X_WIDTH("+X Width", 0xFF5555),      // Red - matches ChatFormatting.RED
        Y_HEIGHT("+Y Height", 0x55FF55),    // Green - matches ChatFormatting.GREEN  
        Z_WIDTH("+Z Width", 0x5555FF),      // Blue - matches ChatFormatting.BLUE
        DECREASE("Decrease Mode", 0xFFFF55); // Yellow - matches ChatFormatting.YELLOW
        
        private final String displayName;
        private final int color;
        
        ModifierType(String displayName, int color) {
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
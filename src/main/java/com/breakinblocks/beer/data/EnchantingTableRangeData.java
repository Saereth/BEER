package com.breakinblocks.beer.data;

import com.breakinblocks.beer.Config;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class EnchantingTableRangeData implements INBTSerializable<CompoundTag> {
    private int itemModifiersX;
    private int itemModifiersY;
    private int itemModifiersZ;

    public EnchantingTableRangeData() {
        this.itemModifiersX = 0;
        this.itemModifiersY = 0;
        this.itemModifiersZ = 0;
    }

    public EnchantingTableRangeData(int rangeX, int rangeY, int rangeZ) {
        this.itemModifiersX = rangeX;
        this.itemModifiersY = rangeY;
        this.itemModifiersZ = rangeZ;
    }

    // Item modifier getters
    public int getItemModifiersX() {
        return itemModifiersX;
    }
    
    public int getItemModifiersY() {
        return itemModifiersY;
    }
    
    public int getItemModifiersZ() {
        return itemModifiersZ;
    }
    
    public int getEffectiveRangeX() {
        // Vanilla X range is 2 (-2 to +2), add item modifiers (minimum 0 when itemMod = -2)
        return Math.max(0, 2 + itemModifiersX);
    }
    
    public int getEffectiveRangeY() {
        // Vanilla Y range is 1 (0 to +1), add item modifiers (minimum 0 when itemMod = -1)
        return Math.max(0, 1 + itemModifiersY);
    }
    
    public int getEffectiveRangeZ() {
        // Vanilla Z range is 2 (-2 to +2), add item modifiers (minimum 0 when itemMod = -2)
        return Math.max(0, 2 + itemModifiersZ);
    }

    public void addItemModifierX(int amount) {
        this.itemModifiersX = clampItemModifierX(this.itemModifiersX + amount);
    }
    
    public void addItemModifierY(int amount) {
        this.itemModifiersY = clampItemModifierY(this.itemModifiersY + amount);
    }
    
    public void addItemModifierZ(int amount) {
        this.itemModifiersZ = clampItemModifierZ(this.itemModifiersZ + amount);
    }
    
    public void setItemModifierX(int modifier) {
        this.itemModifiersX = clampItemModifierX(modifier);
    }
    
    public void setItemModifierY(int modifier) {
        this.itemModifiersY = clampItemModifierY(modifier);
    }
    
    public void setItemModifierZ(int modifier) {
        this.itemModifiersZ = clampItemModifierZ(modifier);
    }

    private int clampItemModifierX(int modifier) {
        // X can go from -2 to +maxItemModifiersPerAxis
        return Math.max(-2, Math.min(modifier, Config.maxItemModifiersPerAxis));
    }
    
    private int clampItemModifierY(int modifier) {
        // Y can go from -1 to +maxItemModifiersPerAxis
        return Math.max(-1, Math.min(modifier, Config.maxItemModifiersPerAxis));
    }
    
    private int clampItemModifierZ(int modifier) {
        // Z can go from -2 to +maxItemModifiersPerAxis
        return Math.max(-2, Math.min(modifier, Config.maxItemModifiersPerAxis));
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("itemModifiersX", itemModifiersX);
        tag.putInt("itemModifiersY", itemModifiersY);
        tag.putInt("itemModifiersZ", itemModifiersZ);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.itemModifiersX = clampItemModifierX(tag.getInt("itemModifiersX"));
        this.itemModifiersY = clampItemModifierY(tag.getInt("itemModifiersY"));
        this.itemModifiersZ = clampItemModifierZ(tag.getInt("itemModifiersZ"));
    }


    
    public boolean hasItemModifications() {
        return itemModifiersX != 0 || itemModifiersY != 0 || itemModifiersZ != 0;
    }


    public void resetToDefaults() {
        this.itemModifiersX = 0;
        this.itemModifiersY = 0;
        this.itemModifiersZ = 0;
    }
    

    
    public void resetAll() {
        resetToDefaults();
    }
}
package com.breakinblocks.beer.compat;

import com.breakinblocks.beer.compat.jei.EnchantingModifierCategory;
import com.breakinblocks.beer.compat.jei.EnchantingModifierRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class BeerJEIPlugin implements IModPlugin {
    
    public static final ResourceLocation ENCHANTING_MODIFIER_CATEGORY = ResourceLocation.fromNamespaceAndPath("beer", "enchanting_modifiers");
    
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("beer", "jei_plugin");
    }
    
    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new EnchantingModifierCategory(registration.getJeiHelpers().getGuiHelper()));
    }
    
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<EnchantingModifierRecipe> recipes = createModifierRecipes();
        registration.addRecipes(ENCHANTING_MODIFIER_CATEGORY, recipes);
    }
    
    private List<EnchantingModifierRecipe> createModifierRecipes() {
        List<EnchantingModifierRecipe> recipes = new ArrayList<>();
        
        // Redstone - X Width modifier
        recipes.add(new EnchantingModifierRecipe(
            Items.REDSTONE,
            "X-Width",
            "beer.jei.modifier.x_width.description",
            EnchantingModifierRecipe.ModifierAxis.X
        ));
        
        // Glowstone Dust - Z Width modifier
        recipes.add(new EnchantingModifierRecipe(
            Items.GLOWSTONE_DUST,
            "Z-Width", 
            "beer.jei.modifier.z_width.description",
            EnchantingModifierRecipe.ModifierAxis.Z
        ));
        
        // Lapis Lazuli - Y Height modifier
        recipes.add(new EnchantingModifierRecipe(
            Items.LAPIS_LAZULI,
            "Y-Height",
            "beer.jei.modifier.y_height.description", 
            EnchantingModifierRecipe.ModifierAxis.Y
        ));
        
        // Nether Quartz - Decrease modifier (special case)
        recipes.add(new EnchantingModifierRecipe(
            Items.QUARTZ,
            "Decrease Mode",
            "beer.jei.modifier.decrease.description",
            EnchantingModifierRecipe.ModifierAxis.DECREASE
        ));
        
        return recipes;
    }
}
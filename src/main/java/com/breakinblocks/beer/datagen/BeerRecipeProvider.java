package com.breakinblocks.beer.datagen;

import com.breakinblocks.beer.Beer;
import com.breakinblocks.beer.compat.EnchantingModifierRecipe;
import com.breakinblocks.beer.recipe.EnchantingModifierRecipeType;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class BeerRecipeProvider extends RecipeProvider {

    public BeerRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
        enchantingModifierRecipe(output, "increase_x", 
            Ingredient.of(Items.REDSTONE_BLOCK), 
            Ingredient.EMPTY,
            "+X Width",
            "beer.jei.modifier.x_width.description",
            EnchantingModifierRecipe.ModifierType.X_WIDTH);

        enchantingModifierRecipe(output, "decrease_x", 
            Ingredient.of(Items.REDSTONE_BLOCK), 
            Ingredient.of(Items.QUARTZ),
            "-X Width",
            "beer.jei.modifier.x_width.description",
            EnchantingModifierRecipe.ModifierType.X_WIDTH);

        enchantingModifierRecipe(output, "increase_y", 
            Ingredient.of(Items.GLOWSTONE), 
            Ingredient.EMPTY,
            "+Y Height",
            "beer.jei.modifier.y_height.description",
            EnchantingModifierRecipe.ModifierType.Y_HEIGHT);

        enchantingModifierRecipe(output, "decrease_y", 
            Ingredient.of(Items.GLOWSTONE), 
            Ingredient.of(Items.QUARTZ),
            "-Y Height",
            "beer.jei.modifier.y_height.description",
            EnchantingModifierRecipe.ModifierType.Y_HEIGHT);

        enchantingModifierRecipe(output, "increase_z", 
            Ingredient.of(Items.LAPIS_BLOCK), 
            Ingredient.EMPTY,
            "+Z Width",
            "beer.jei.modifier.z_width.description",
            EnchantingModifierRecipe.ModifierType.Z_WIDTH);

        enchantingModifierRecipe(output, "decrease_z", 
            Ingredient.of(Items.LAPIS_BLOCK), 
            Ingredient.of(Items.QUARTZ),
            "-Z Width",
            "beer.jei.modifier.z_width.description",
            EnchantingModifierRecipe.ModifierType.Z_WIDTH);
    }

    private void enchantingModifierRecipe(RecipeOutput output, String name, 
                                        Ingredient mainhandInput, Ingredient offhandInput,
                                        String effectKey, String descriptionKey,
                                        EnchantingModifierRecipe.ModifierType modifierType) {
        
        EnchantingModifierRecipeType recipe = new EnchantingModifierRecipeType(
            mainhandInput, offhandInput, false, effectKey, descriptionKey, modifierType
        );
        
        output.accept(
            ResourceLocation.fromNamespaceAndPath(Beer.MODID, "enchanting_modifier/" + name),
            recipe,
            null
        );
    }
}
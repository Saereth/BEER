package com.breakinblocks.beer.compat;

import com.breakinblocks.beer.Config;
import com.breakinblocks.beer.recipe.BeerRecipes;
import com.breakinblocks.beer.recipe.EnchantingModifierRecipeType;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@JeiPlugin
public class BeerJEIPlugin implements IModPlugin {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("beer", "enchanting_modifiers");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new EnchantingTableCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        LOGGER.info("[BEER JEI] registerRecipes() called - Config.enableItemModifiers: {}", Config.enableItemModifiers);
        
        if (!Config.enableItemModifiers) {
            LOGGER.info("[BEER JEI] Item modifiers disabled, skipping recipe registration");
            return; // Don't register recipes if item modifiers are disabled
        }

        List<EnchantingModifierRecipeType> jeiRecipes = new ArrayList<>();
        
        LOGGER.info("[BEER JEI] Attempting to load datapack recipes...");
        LOGGER.info("[BEER JEI] Minecraft instance: {}", Minecraft.getInstance());
        LOGGER.info("[BEER JEI] Current level: {}", (Minecraft.getInstance().level != null ? "Available" : "NULL"));
        
        try {
            if (Minecraft.getInstance().level != null) {
                var recipeManager = Minecraft.getInstance().level.getRecipeManager();
                var recipeType = BeerRecipes.ENCHANTING_MODIFIER_TYPE.get();
                
                LOGGER.info("[BEER JEI] Recipe manager: {}", recipeManager);
                LOGGER.info("[BEER JEI] Recipe type: {}", recipeType);
                
                if (recipeManager != null && recipeType != null) {
                    var allRecipes = recipeManager.getAllRecipesFor(recipeType);
                    LOGGER.info("[BEER JEI] Found {} recipes of type {}", allRecipes.size(), recipeType);
                    
                    for (var holder : allRecipes) {
                        LOGGER.info("[BEER JEI] Recipe found: {} -> {}", holder.id(), holder.value().getEffectKey());
                    }
                    
                    jeiRecipes = allRecipes
                        .stream()
                        .sorted((r1, r2) -> r1.id().compareNamespaced(r2.id()))
                        .map(recipeHolder -> recipeHolder.value())
                        .collect(Collectors.toList());
                    
                    // Log successful recipe loading
                    if (!jeiRecipes.isEmpty()) {
                        LOGGER.info("[BEER JEI] Loaded {} datapack enchanting modifier recipes", jeiRecipes.size());
                    }
                } else {
                    LOGGER.warn("[BEER JEI] Either recipe manager or recipe type is null");
                }
            } else {
                LOGGER.warn("[BEER JEI] Minecraft level is null - cannot access recipe manager");
            }
        } catch (Exception e) {
            LOGGER.error("[BEER JEI] Failed to load datapack recipes", e);
        }
        
        if (jeiRecipes.isEmpty()) {
            LOGGER.info("[BEER JEI] No datapack recipes found, using hardcoded recipes as fallback");
            jeiRecipes = createHardcodedRecipes();
        } else {
            // Optionally combine datapack recipes with hardcoded ones
            LOGGER.info("[BEER JEI] Using {} datapack recipes for JEI display", jeiRecipes.size());
        }
        
        LOGGER.info("[BEER JEI] Final recipe count for JEI: {}", jeiRecipes.size());
        registration.addRecipes(EnchantingTableCategory.TYPE, jeiRecipes);

        registration.addItemStackInfo(
            new ItemStack(Blocks.ENCHANTING_TABLE),
            Component.translatable("jei.beer.enchanting_table.info.title").withStyle(ChatFormatting.GOLD),
            Component.translatable("jei.beer.enchanting_table.info.bookshelf_range").withStyle(ChatFormatting.BLACK),
            Component.translatable("jei.beer.enchanting_table.info.configuration").withStyle(ChatFormatting.BLACK),
            Component.translatable("jei.beer.enchanting_table.info.range_modification").withStyle(ChatFormatting.BLACK)
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Blocks.ENCHANTING_TABLE), EnchantingTableCategory.TYPE);
    }

    private List<EnchantingModifierRecipeType> createHardcodedRecipes() {
        List<EnchantingModifierRecipeType> recipes = new ArrayList<>();

        // Redstone - X Width modifier (normal)
        recipes.add(new EnchantingModifierRecipeType(
            Ingredient.of(Items.REDSTONE),
            Ingredient.EMPTY,
            false,
            "+X Width",
            "beer.jei.modifier.x_width.description",
            EnchantingModifierRecipe.ModifierType.X_WIDTH
        ));

        // Redstone - X Width modifier (with Quartz for decrease)
        recipes.add(new EnchantingModifierRecipeType(
            Ingredient.of(Items.REDSTONE),
            Ingredient.of(Items.QUARTZ),
            false,
            "-X Width",
            "beer.jei.modifier.x_width.description",
            EnchantingModifierRecipe.ModifierType.X_WIDTH
        ));

        // Glowstone Dust - Z Width modifier (normal)
        recipes.add(new EnchantingModifierRecipeType(
            Ingredient.of(Items.GLOWSTONE_DUST),
            Ingredient.EMPTY,
            false,
            "+Z Width",
            "beer.jei.modifier.z_width.description",
            EnchantingModifierRecipe.ModifierType.Z_WIDTH
        ));

        // Glowstone Dust - Z Width modifier (with Quartz for decrease)
        recipes.add(new EnchantingModifierRecipeType(
            Ingredient.of(Items.GLOWSTONE_DUST),
            Ingredient.of(Items.QUARTZ),
            false,
            "-Z Width",
            "beer.jei.modifier.z_width.description",
            EnchantingModifierRecipe.ModifierType.Z_WIDTH
        ));

        // Lapis Lazuli - Y Height modifier (normal)
        recipes.add(new EnchantingModifierRecipeType(
            Ingredient.of(Items.LAPIS_LAZULI),
            Ingredient.EMPTY,
            false,
            "+Y Height", 
            "beer.jei.modifier.y_height.description",
            EnchantingModifierRecipe.ModifierType.Y_HEIGHT
        ));

        // Lapis Lazuli - Y Height modifier (with Quartz for decrease)
        recipes.add(new EnchantingModifierRecipeType(
            Ingredient.of(Items.LAPIS_LAZULI),
            Ingredient.of(Items.QUARTZ),
            false,
            "-Y Height", 
            "beer.jei.modifier.y_height.description",
            EnchantingModifierRecipe.ModifierType.Y_HEIGHT
        ));

        return recipes;
    }

}
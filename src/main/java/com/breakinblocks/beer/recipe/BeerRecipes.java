package com.breakinblocks.beer.recipe;

import com.breakinblocks.beer.Beer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BeerRecipes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = 
        DeferredRegister.create(Registries.RECIPE_TYPE, Beer.MODID);
    
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = 
        DeferredRegister.create(Registries.RECIPE_SERIALIZER, Beer.MODID);

    public static final Supplier<RecipeType<EnchantingModifierRecipeType>> ENCHANTING_MODIFIER_TYPE = 
        RECIPE_TYPES.register("enchanting_modifier", () -> new RecipeType<EnchantingModifierRecipeType>(){});

    public static final Supplier<RecipeSerializer<EnchantingModifierRecipeType>> ENCHANTING_MODIFIER_SERIALIZER = 
        RECIPE_SERIALIZERS.register("enchanting_modifier", () -> EnchantingModifierRecipeType.SERIALIZER);

    public static void register(IEventBus modEventBus) {
        var logger = com.mojang.logging.LogUtils.getLogger();
        logger.info("[BEER] Registering recipe types and serializers");
        logger.info("[BEER] Recipe type supplier: {}", ENCHANTING_MODIFIER_TYPE);
        logger.info("[BEER] Recipe serializer supplier: {}", ENCHANTING_MODIFIER_SERIALIZER);
        
        // Add event listeners to track when registration completes
        modEventBus.addListener((net.neoforged.neoforge.registries.RegisterEvent event) -> {
            if (event.getRegistryKey().equals(net.minecraft.core.registries.Registries.RECIPE_TYPE)) {
                logger.info("[BEER] Recipe type registration event fired");
            }
            if (event.getRegistryKey().equals(net.minecraft.core.registries.Registries.RECIPE_SERIALIZER)) {
                logger.info("[BEER] Recipe serializer registration event fired");
            }
        });
        
        RECIPE_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
        
        logger.info("[BEER] Recipe registration completed - Type: {}, Serializer: {}", 
            ENCHANTING_MODIFIER_TYPE, ENCHANTING_MODIFIER_SERIALIZER);
        // Don't call .get() here as registration hasn't completed yet
    }
}
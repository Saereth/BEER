package com.breakinblocks.beer;

import com.breakinblocks.beer.data.BeerDataAttachments;
import com.breakinblocks.beer.recipe.BeerRecipes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;

@Mod(Beer.MODID)
public class Beer {
    public static final String MODID = "beer";

    public Beer(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        
        // Register data attachments
        BeerDataAttachments.register(modEventBus);
        
        // Register recipes
        BeerRecipes.register(modEventBus);
        
        // Register event handler
        NeoForge.EVENT_BUS.register(this);
        
        // Add resource reload listener for debugging
        NeoForge.EVENT_BUS.addListener(this::onAddReloadListener);
    }
    
    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        // Test if recipes are loaded properly
        var logger = com.mojang.logging.LogUtils.getLogger();
        var recipeManager = event.getServer().getRecipeManager();
        var recipeType = BeerRecipes.ENCHANTING_MODIFIER_TYPE.get();
        
        logger.info("[BEER] Server started event - checking recipes");
        logger.info("[BEER] Recipe manager: {}", recipeManager);
        logger.info("[BEER] Recipe type: {}", recipeType);
        logger.info("[BEER] Recipe type key: {}", recipeType != null ? recipeType.toString() : "null");
        
        if (recipeManager != null && recipeType != null) {
            var recipes = recipeManager.getAllRecipesFor(recipeType);
            logger.info("[BEER] Found {} enchanting modifier recipes on server start", recipes.size());
            
            recipes.forEach(recipeHolder -> {
                logger.info("[BEER] Recipe: {} - {}", recipeHolder.id(), recipeHolder.value().getEffectKey());
            });
        } else {
            logger.warn("[BEER] Recipe manager or recipe type is null on server start!");
        }
    }
    
    @SubscribeEvent
    public void onAddReloadListener(AddReloadListenerEvent event) {
        var logger = com.mojang.logging.LogUtils.getLogger();
        logger.info("[BEER] AddReloadListener event fired - this is when recipes should be loaded");
        logger.info("[BEER] ResourceManager: {}", event.getServerResources());
    }
}

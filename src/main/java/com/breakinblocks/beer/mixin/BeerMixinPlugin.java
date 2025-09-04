package com.breakinblocks.beer.mixin;

import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.ModList;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class BeerMixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LoggerFactory.getLogger("BEER-MixinPlugin");
    private static final String APOTHIC_ENCHANTING_MOD_ID = "apothic_enchanting";

    @Override
    public void onLoad(String mixinPackage) {
        LOGGER.info("BEER Mixin Plugin loaded for package: {}", mixinPackage);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("TableStatsMixin")) {
            return isModLoaded(APOTHIC_ENCHANTING_MOD_ID);
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null; // Use default behavior
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        if (mixinClassName.contains("TableStatsMixin")) {
            LOGGER.info("Successfully applied BEER mixin {} to {}", mixinClassName, targetClassName);
        }
    }

    /**
     * Check if a mod is loaded using the appropriate method based on loading stage
     */
    private boolean isModLoaded(String modId) {
        try {
            // First try using FMLLoader's mod container list (available earlier)
            if (FMLLoader.getLoadingModList().getModFileById(modId) != null) {
                LOGGER.debug("Found {} in FMLLoader mod list", modId);
                return true;
            }
            
            // Fallback to ModList if available
            try {
                boolean isLoaded = ModList.get().isLoaded(modId);
                LOGGER.debug("ModList check for {}: {}", modId, isLoaded);
                return isLoaded;
            } catch (Exception e) {
                LOGGER.debug("ModList not available yet, checking FMLLoader for {}", modId);
            }
            
            LOGGER.debug("Mod {} not found in available mod lists", modId);
            return false;
        } catch (Exception e) {
            LOGGER.warn("Error checking if mod {} is loaded: {}", modId, e.getMessage());
            return false;
        }
    }
}
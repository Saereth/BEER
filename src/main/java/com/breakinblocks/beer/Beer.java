package com.breakinblocks.beer;

import net.minecraftforge.fml.common.Mod;

@Mod(Beer.MODID)
public class Beer {
    public static final String MODID = "beer";

    public Beer() {
        net.minecraftforge.fml.ModLoadingContext.get().registerConfig(
            net.minecraftforge.fml.config.ModConfig.Type.COMMON,
            BeerConfig.COMMON_SPEC
        );
    }
}

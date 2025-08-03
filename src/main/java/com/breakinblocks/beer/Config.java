package com.breakinblocks.beer;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;
import java.util.List;


@EventBusSubscriber(modid = Beer.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<List<? extends Integer>> RANGE = BUILDER.comment("Range for X, Y, Z. Default: [16, 16, 16]").defineList(
            "range",
            List.of(16, 16, 16),
            obj -> obj instanceof Integer // validation function to ensure all items are integers
    );
    static final ModConfigSpec SPEC = BUILDER.build();

    public static List<? extends Integer> range;
    public static int rangeX;
    public static int rangeY;
    public static int rangeZ;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        range = RANGE.get();
        rangeX = range.get(0);
        rangeY = range.get(1);
        rangeZ = range.get(2);
    }

}

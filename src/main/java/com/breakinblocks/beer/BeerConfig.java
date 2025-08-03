package com.breakinblocks.beer;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class BeerConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class Common {
        public final ForgeConfigSpec.IntValue rangeX;
        public final ForgeConfigSpec.IntValue rangeY;
        public final ForgeConfigSpec.IntValue rangeZ;

        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("range");
            rangeX = builder
                .comment("Range for X axis (min 2, max 64)")
                .defineInRange("x", 16, 2, 64);
            rangeY = builder
                .comment("Range for Y axis (min 2, max 64)")
                .defineInRange("y", 16, 2, 64);
            rangeZ = builder
                .comment("Range for Z axis (min 2, max 64)")
                .defineInRange("z", 16, 2, 64);
            builder.pop();
        }
    }

    public static int getRangeX() {
        return COMMON.rangeX.get();
    }
    public static int getRangeY() {
        return COMMON.rangeY.get();
    }
    public static int getRangeZ() {
        return COMMON.rangeZ.get();
    }
}

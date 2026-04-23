package com.breakinblocks.beer.data;

import com.breakinblocks.beer.Beer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class BeerDataAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Beer.MODID);

    public static final Supplier<AttachmentType<EnchantingTableRangeData>> ENCHANTING_TABLE_RANGE =
        ATTACHMENT_TYPES.register("enchanting_table_range", () ->
            AttachmentType.serializable(EnchantingTableRangeData::new)
                .copyOnDeath()
                .build()
        );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}

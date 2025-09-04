package com.breakinblocks.beer.data;

import com.breakinblocks.beer.Beer;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class BeerDataAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Beer.MODID);

    private static final Codec<EnchantingTableRangeData> RANGE_DATA_CODEC = Codec.INT.listOf().xmap(
        list -> {
            return new EnchantingTableRangeData(list.get(0), list.get(1), list.get(2));
        }, data -> {
            return java.util.List.of(data.getItemModifiersX(), data.getItemModifiersY(), data.getItemModifiersZ());
        }
    );


    public static final Supplier<AttachmentType<EnchantingTableRangeData>> ENCHANTING_TABLE_RANGE = 
        ATTACHMENT_TYPES.register("enchanting_table_range", () -> 
            AttachmentType.builder(EnchantingTableRangeData::new)
                .serialize(RANGE_DATA_CODEC)
                .copyOnDeath()
                .build()
        );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPES.register(eventBus);
    }
}
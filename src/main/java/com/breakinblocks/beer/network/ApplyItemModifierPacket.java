package com.breakinblocks.beer.network;

import com.breakinblocks.beer.event.ItemModifierHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.IntFunction;

public record ApplyItemModifierPacket(
        BlockPos pos,
        ItemModifierHandler.ModifierType modifierType,
        boolean increase,
        boolean success,
        int rangeX,
        int rangeY,
        int rangeZ
) implements CustomPacketPayload {

    public static final Type<ApplyItemModifierPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("beer", "apply_item_modifier"));

    public static final StreamCodec<ByteBuf, ApplyItemModifierPacket> STREAM_CODEC = new StreamCodec<ByteBuf, ApplyItemModifierPacket>() {
        @Override
        public ApplyItemModifierPacket decode(ByteBuf buffer) {
            BlockPos pos = BlockPos.STREAM_CODEC.decode(buffer);
            ItemModifierHandler.ModifierType modifierType = ModifierTypeStreamCodec.INSTANCE.decode(buffer);
            boolean increase = buffer.readBoolean();
            boolean success = buffer.readBoolean();
            int rangeX = buffer.readInt();
            int rangeY = buffer.readInt();
            int rangeZ = buffer.readInt();
            return new ApplyItemModifierPacket(pos, modifierType, increase, success, rangeX, rangeY, rangeZ);
        }
        
        @Override
        public void encode(ByteBuf buffer, ApplyItemModifierPacket packet) {
            BlockPos.STREAM_CODEC.encode(buffer, packet.pos());
            ModifierTypeStreamCodec.INSTANCE.encode(buffer, packet.modifierType());
            buffer.writeBoolean(packet.increase());
            buffer.writeBoolean(packet.success());
            buffer.writeInt(packet.rangeX());
            buffer.writeInt(packet.rangeY());
            buffer.writeInt(packet.rangeZ());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ApplyItemModifierPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            if (level.isClientSide()) {
                handleClient(packet, level);
            }
        });
    }

    private static void handleClient(ApplyItemModifierPacket packet, Level level) {
        // Client-side handling - particles removed
        // This packet can be used for other client-side effects if needed in the future
    }


    private static class ModifierTypeStreamCodec implements StreamCodec<ByteBuf, ItemModifierHandler.ModifierType> {
        public static final ModifierTypeStreamCodec INSTANCE = new ModifierTypeStreamCodec();

        private static final IntFunction<ItemModifierHandler.ModifierType> BY_ID = ByIdMap.continuous(
                ItemModifierHandler.ModifierType::ordinal,
                ItemModifierHandler.ModifierType.values(),
                ByIdMap.OutOfBoundsStrategy.ZERO
        );

        @Override
        public ItemModifierHandler.ModifierType decode(ByteBuf buffer) {
            int id = buffer.readByte();
            return BY_ID.apply(id);
        }

        @Override
        public void encode(ByteBuf buffer, ItemModifierHandler.ModifierType modifierType) {
            buffer.writeByte(modifierType.ordinal());
        }
    }
}
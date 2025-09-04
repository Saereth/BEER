package com.breakinblocks.beer.network;

import com.breakinblocks.beer.data.BeerDataAttachments;
import com.breakinblocks.beer.data.EnchantingTableRangeData;
import com.breakinblocks.beer.compat.BeerJadePlugin;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SyncEnchantingDataPacket(
        BlockPos pos,
        int itemModX,
        int itemModY,
        int itemModZ
) implements CustomPacketPayload {

    public static final Type<SyncEnchantingDataPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("beer", "sync_enchanting_data"));

    public static final StreamCodec<ByteBuf, SyncEnchantingDataPacket> STREAM_CODEC = new StreamCodec<ByteBuf, SyncEnchantingDataPacket>() {
        @Override
        public SyncEnchantingDataPacket decode(ByteBuf buffer) {
            BlockPos pos = BlockPos.STREAM_CODEC.decode(buffer);
            int itemModX = buffer.readInt();
            int itemModY = buffer.readInt();
            int itemModZ = buffer.readInt();
            return new SyncEnchantingDataPacket(pos, itemModX, itemModY, itemModZ);
        }

        @Override
        public void encode(ByteBuf buffer, SyncEnchantingDataPacket packet) {
            BlockPos.STREAM_CODEC.encode(buffer, packet.pos());
            buffer.writeInt(packet.itemModX());
            buffer.writeInt(packet.itemModY());
            buffer.writeInt(packet.itemModZ());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(SyncEnchantingDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            if (level.isClientSide()) {
                handleClient(packet, level);
            }
        });
    }

    private static void handleClient(SyncEnchantingDataPacket packet, Level level) {
        BlockEntity blockEntity = level.getBlockEntity(packet.pos());
        if (blockEntity instanceof EnchantingTableBlockEntity enchantingTable) {
            EnchantingTableRangeData data = new EnchantingTableRangeData();
            data.setItemModifierX(packet.itemModX());
            data.setItemModifierY(packet.itemModY());
            data.setItemModifierZ(packet.itemModZ());
            
            enchantingTable.setData(BeerDataAttachments.ENCHANTING_TABLE_RANGE.get(), data);
            
            BeerJadePlugin.clearPendingRequest(packet.pos());
        }
    }
    
    public static SyncEnchantingDataPacket create(BlockPos pos, EnchantingTableRangeData data) {
        return new SyncEnchantingDataPacket(
            pos,
            data.getItemModifiersX(),
            data.getItemModifiersY(),
            data.getItemModifiersZ()
        );
    }
}
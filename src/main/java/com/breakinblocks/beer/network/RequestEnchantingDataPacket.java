package com.breakinblocks.beer.network;

import com.breakinblocks.beer.data.EnchantingTableRangeData;
import com.breakinblocks.beer.util.EnchantingTableDataUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.EnchantingTableBlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RequestEnchantingDataPacket(BlockPos pos) implements CustomPacketPayload {

    public static final Type<RequestEnchantingDataPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("beer", "request_enchanting_data"));

    public static final StreamCodec<ByteBuf, RequestEnchantingDataPacket> STREAM_CODEC = new StreamCodec<ByteBuf, RequestEnchantingDataPacket>() {
        @Override
        public RequestEnchantingDataPacket decode(ByteBuf buffer) {
            BlockPos pos = BlockPos.STREAM_CODEC.decode(buffer);
            return new RequestEnchantingDataPacket(pos);
        }

        @Override
        public void encode(ByteBuf buffer, RequestEnchantingDataPacket packet) {
            BlockPos.STREAM_CODEC.encode(buffer, packet.pos());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RequestEnchantingDataPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                ServerLevel level = serverPlayer.serverLevel();
                var blockEntity = level.getBlockEntity(packet.pos());
                
                if (blockEntity instanceof EnchantingTableBlockEntity) {
                    EnchantingTableRangeData data = EnchantingTableDataUtil.getRangeData(level, packet.pos());
                    
                    // Send the data back to the requesting player
                    SyncEnchantingDataPacket syncPacket = SyncEnchantingDataPacket.create(packet.pos(), data);
                    NetworkHandler.sendToPlayer(syncPacket, serverPlayer);
                }
            }
        });
    }
    
    public static RequestEnchantingDataPacket create(BlockPos pos) {
        return new RequestEnchantingDataPacket(pos);
    }
}
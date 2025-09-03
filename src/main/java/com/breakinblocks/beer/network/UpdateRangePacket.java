package com.breakinblocks.beer.network;

import com.breakinblocks.beer.util.EnchantingTableDataUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdateRangePacket(BlockPos pos, int rangeX, int rangeY, int rangeZ) implements CustomPacketPayload {
    
    public static final CustomPacketPayload.Type<UpdateRangePacket> TYPE = 
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("beer", "update_range"));

    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateRangePacket> STREAM_CODEC = 
        StreamCodec.composite(
            BlockPos.STREAM_CODEC, UpdateRangePacket::pos,
            ByteBufCodecs.INT, UpdateRangePacket::rangeX,
            ByteBufCodecs.INT, UpdateRangePacket::rangeY,
            ByteBufCodecs.INT, UpdateRangePacket::rangeZ,
            UpdateRangePacket::new
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(UpdateRangePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            System.out.println("[BEER DEBUG] UpdateRangePacket received for position: " + packet.pos());
            System.out.println("[BEER DEBUG] Ranges: X=" + packet.rangeX() + ", Y=" + packet.rangeY() + ", Z=" + packet.rangeZ());
            
            if (context.flow().isServerbound()) {
                var player = context.player();
                System.out.println("[BEER DEBUG] Player: " + player);
                if (player != null && player.level() != null) {
                    System.out.println("[BEER DEBUG] Checking for enchanting table at: " + packet.pos());
                    // Validate that the enchanting table exists at the position
                    if (EnchantingTableDataUtil.hasEnchantingTable(player.level(), packet.pos())) {
                        System.out.println("[BEER DEBUG] Enchanting table found, checking distance");
                        // Check if player is within reasonable distance (prevents cheating)
                        double distance = player.distanceToSqr(packet.pos().getX() + 0.5, packet.pos().getY() + 0.5, packet.pos().getZ() + 0.5);
                        System.out.println("[BEER DEBUG] Distance: " + distance);
                        if (distance <= 64.0) { // 8 blocks max distance
                            System.out.println("[BEER DEBUG] Distance valid, applying ranges");
                            // Apply ranges directly - validation happens in the data layer
                            EnchantingTableDataUtil.setRanges(player.level(), packet.pos(), packet.rangeX(), packet.rangeY(), packet.rangeZ());
                            System.out.println("[BEER DEBUG] Ranges saved successfully");
                            
                            // Sync the updated data to nearby clients so Jade displays correctly
                            if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                                var updatedData = EnchantingTableDataUtil.getRangeData(player.level(), packet.pos());
                                SyncEnchantingDataPacket syncPacket = SyncEnchantingDataPacket.create(packet.pos(), updatedData);
                                NetworkHandler.sendToPlayersNear(syncPacket, serverLevel, packet.pos(), 64.0);
                            }
                        } else {
                            System.out.println("[BEER DEBUG] Player too far from enchanting table");
                        }
                    } else {
                        System.out.println("[BEER DEBUG] No enchanting table found at position");
                    }
                } else {
                    System.out.println("[BEER DEBUG] Player or level is null");
                }
            } else {
                System.out.println("[BEER DEBUG] Packet is not serverbound");
            }
        });
    }
}
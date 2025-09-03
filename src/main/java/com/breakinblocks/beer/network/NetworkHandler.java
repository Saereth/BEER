package com.breakinblocks.beer.network;

import com.breakinblocks.beer.Beer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Beer.MODID, bus = EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Beer.MODID)
            .versioned(PROTOCOL_VERSION);

        registrar.playToServer(
            UpdateRangePacket.TYPE,
            UpdateRangePacket.STREAM_CODEC,
            UpdateRangePacket::handle
        );
        
        registrar.playToClient(
            ApplyItemModifierPacket.TYPE,
            ApplyItemModifierPacket.STREAM_CODEC,
            ApplyItemModifierPacket::handle
        );
        
        registrar.playToClient(
            SyncEnchantingDataPacket.TYPE,
            SyncEnchantingDataPacket.STREAM_CODEC,
            SyncEnchantingDataPacket::handle
        );
    }

    public static void sendToServer(CustomPacketPayload packet) {
        PacketDistributor.sendToServer(packet);
    }

    public static void sendToPlayer(CustomPacketPayload packet, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendToAllPlayers(CustomPacketPayload packet) {
        PacketDistributor.sendToAllPlayers(packet);
    }

    public static void sendToPlayersNear(CustomPacketPayload packet, net.minecraft.server.level.ServerLevel level, net.minecraft.core.BlockPos pos, double range) {
        PacketDistributor.sendToPlayersNear(level, null, pos.getX(), pos.getY(), pos.getZ(), range, packet);
    }
}
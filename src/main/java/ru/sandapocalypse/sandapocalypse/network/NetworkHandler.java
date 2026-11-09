package ru.sandapocalypse.sandapocalypse.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import ru.sandapocalypse.sandapocalypse.SandApocalypse;

public final class NetworkHandler {
    private static final String PROTOCOL = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SandApocalypse.MOD_ID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static boolean registered = false;

    public static void register() {
        if (registered) return;
        registered = true;
        CHANNEL.messageBuilder(ThirstSyncPacket.class, 1, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ThirstSyncPacket::encode)
                .decoder(ThirstSyncPacket::decode)
                .consumerMainThread(ThirstSyncPacket::handle)
                .add();
    }

    public static void syncThirst(ServerPlayer player, int thirst) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ThirstSyncPacket(thirst));
    }

    private NetworkHandler() {}
}

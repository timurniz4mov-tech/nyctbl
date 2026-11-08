package ru.sandapocalypse.sandapocalypse.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import ru.sandapocalypse.sandapocalypse.client.ClientThirstOverlay;

import java.util.function.Supplier;

public class ThirstSyncPacket {
    private final int thirst;

    public ThirstSyncPacket(int thirst) {
        this.thirst = Math.max(0, Math.min(100, thirst));
    }

    public static void encode(ThirstSyncPacket message, FriendlyByteBuf buffer) {
        buffer.writeVarInt(message.thirst);
    }

    public static ThirstSyncPacket decode(FriendlyByteBuf buffer) {
        return new ThirstSyncPacket(buffer.readVarInt());
    }

    public static void handle(ThirstSyncPacket message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(
                net.minecraftforge.api.distmarker.Dist.CLIENT,
                () -> () -> ClientThirstOverlay.setThirst(message.thirst)
        ));
        context.setPacketHandled(true);
    }
}

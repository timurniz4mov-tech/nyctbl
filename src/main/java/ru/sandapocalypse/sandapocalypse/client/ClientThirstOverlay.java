package ru.sandapocalypse.sandapocalypse.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.util.Mth;
import ru.sandapocalypse.sandapocalypse.SandApocalypse;

@Mod.EventBusSubscriber(modid = SandApocalypse.MOD_ID, value = Dist.CLIENT)
public final class ClientThirstOverlay {
    private static int thirst = 100;

    public static void setThirst(int value) {
        thirst = Mth.clamp(value, 0, 100);
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.HOTBAR.id())) return;

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) return;

        int x = 10;
        int y = event.getWindow().getGuiScaledHeight() - 70;
        int color = thirst <= 20 ? 0xFFFF5555 : (thirst <= 45 ? 0xFFFFB347 : 0xFFFFD36A);
        event.getGuiGraphics().drawString(minecraft.font,
                Component.translatable("hud.sandapocalypse.thirst", thirst), x, y, color, true);
        event.getGuiGraphics().fill(x, y + 11, x + 102, y + 17, 0x99000000);
        if (thirst > 0) {
            event.getGuiGraphics().fill(x + 1, y + 12, x + 1 + thirst, y + 16, 0xFFFFC857);
        }
    }

    private ClientThirstOverlay() {}
}

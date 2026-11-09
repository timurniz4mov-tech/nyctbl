package ru.sandapocalypse.sandapocalypse.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraft.resources.ResourceLocation;
import ru.sandapocalypse.sandapocalypse.entity.DuneStalker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.sandapocalypse.sandapocalypse.SandApocalypse;
import ru.sandapocalypse.sandapocalypse.registry.ModEntities;
import ru.sandapocalypse.sandapocalypse.entity.Colossus;

@Mod.EventBusSubscriber(modid = SandApocalypse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModEntityRenderers {
    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.DUNE_STALKER.get(), DuneStalkerRenderer::new);
        event.registerEntityRenderer(ModEntities.COLOSSUS.get(), ColossusRenderer::new);
        event.registerEntityRenderer(ModEntities.DESERT_MASTER.get(), WanderingTraderRenderer::new);
    }

    private static class DuneStalkerRenderer extends ZombieRenderer<DuneStalker> {
        public DuneStalkerRenderer(EntityRendererProvider.Context context) { super(context); }
        @Override protected ResourceLocation getTextureLocation(DuneStalker entity) {
            return new ResourceLocation(SandApocalypse.MOD_ID, "textures/entity/dune_stalker.png");
        }
    }

    private static class ColossusRenderer extends ZombieRenderer<Colossus> {
        public ColossusRenderer(EntityRendererProvider.Context context) { super(context); }
        @Override protected ResourceLocation getTextureLocation(Colossus entity) {
            return new ResourceLocation(SandApocalypse.MOD_ID, "textures/entity/desert_colossus.png");
        }
        @Override protected void scale(Colossus entity, PoseStack poseStack, float partialTick) {
            poseStack.scale(2.15F, 2.15F, 2.15F);
        }
    }
}

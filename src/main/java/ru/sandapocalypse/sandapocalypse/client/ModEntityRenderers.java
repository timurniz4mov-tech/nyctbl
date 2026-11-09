package ru.sandapocalypse.sandapocalypse.client;

import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.ZombieModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import ru.sandapocalypse.sandapocalypse.SandApocalypse;
import ru.sandapocalypse.sandapocalypse.entity.Colossus;
import ru.sandapocalypse.sandapocalypse.entity.DesertMaster;
import ru.sandapocalypse.sandapocalypse.entity.DuneStalker;
import ru.sandapocalypse.sandapocalypse.registry.ModEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.mojang.blaze3d.vertex.PoseStack;

@Mod.EventBusSubscriber(modid = SandApocalypse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModEntityRenderers {
    private static final ResourceLocation DUNE_STALKER_TEXTURE = new ResourceLocation(SandApocalypse.MOD_ID, "textures/entity/dune_stalker.png");
    private static final ResourceLocation COLOSSUS_TEXTURE = new ResourceLocation(SandApocalypse.MOD_ID, "textures/entity/desert_colossus.png");
    private static final ResourceLocation MASTER_TEXTURE = new ResourceLocation("minecraft", "textures/entity/wandering_trader.png");

    @SubscribeEvent
    public static void register(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.DUNE_STALKER.get(), DuneStalkerRenderer::new);
        event.registerEntityRenderer(ModEntities.COLOSSUS.get(), ColossusRenderer::new);
        event.registerEntityRenderer(ModEntities.DESERT_MASTER.get(), DesertMasterRenderer::new);
    }

    private static class DuneStalkerRenderer extends MobRenderer<DuneStalker, ZombieModel<DuneStalker>> {
        DuneStalkerRenderer(EntityRendererProvider.Context context) {
            super(context, new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.5F);
        }
        @Override public ResourceLocation getTextureLocation(DuneStalker entity) { return DUNE_STALKER_TEXTURE; }
    }

    private static class ColossusRenderer extends MobRenderer<Colossus, ZombieModel<Colossus>> {
        ColossusRenderer(EntityRendererProvider.Context context) {
            super(context, new ZombieModel<>(context.bakeLayer(ModelLayers.ZOMBIE)), 0.9F);
        }
        @Override public ResourceLocation getTextureLocation(Colossus entity) { return COLOSSUS_TEXTURE; }
        @Override protected void scale(Colossus entity, PoseStack poseStack, float partialTick) {
            poseStack.scale(2.15F, 2.15F, 2.15F);
        }
    }

    private static class DesertMasterRenderer extends MobRenderer<DesertMaster, VillagerModel<DesertMaster>> {
        DesertMasterRenderer(EntityRendererProvider.Context context) {
            super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.WANDERING_TRADER)), 0.5F);
        }
        @Override public ResourceLocation getTextureLocation(DesertMaster entity) { return MASTER_TEXTURE; }
    }
}

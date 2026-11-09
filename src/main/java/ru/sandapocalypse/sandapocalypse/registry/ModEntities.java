package ru.sandapocalypse.sandapocalypse.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.sandapocalypse.sandapocalypse.SandApocalypse;
import ru.sandapocalypse.sandapocalypse.entity.Colossus;
import ru.sandapocalypse.sandapocalypse.entity.DesertMaster;
import ru.sandapocalypse.sandapocalypse.entity.DuneStalker;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SandApocalypse.MOD_ID);
    public static final RegistryObject<EntityType<DuneStalker>> DUNE_STALKER = TYPES.register("dune_stalker", () -> EntityType.Builder.of(DuneStalker::new, MobCategory.MONSTER).sized(0.6F, 1.95F).clientTrackingRange(8).build("dune_stalker"));
    public static final RegistryObject<EntityType<Colossus>> COLOSSUS = TYPES.register("desert_colossus", () -> EntityType.Builder.of(Colossus::new, MobCategory.MONSTER).sized(1.35F, 4.2F).clientTrackingRange(10).build("desert_colossus"));
    public static final RegistryObject<EntityType<DesertMaster>> DESERT_MASTER = TYPES.register("desert_master", () -> EntityType.Builder.of(DesertMaster::new, MobCategory.CREATURE).sized(0.6F, 1.95F).clientTrackingRange(10).build("desert_master"));

    @Mod.EventBusSubscriber(modid = SandApocalypse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEntityAttributes {
        @SubscribeEvent public static void register(EntityAttributeCreationEvent event) {
            event.put(DUNE_STALKER.get(), Zombie.createAttributes().add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 26.0D).add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.29D).build());
            event.put(COLOSSUS.get(), Zombie.createAttributes().add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 100.0D).add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 0.19D).add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 13.0D).build());
            event.put(DESERT_MASTER.get(), WanderingTrader.createAttributes().build());
        }
    }
    private ModEntities() {}
}

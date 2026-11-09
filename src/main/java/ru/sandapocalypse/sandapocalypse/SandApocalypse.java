package ru.sandapocalypse.sandapocalypse;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import ru.sandapocalypse.sandapocalypse.network.NetworkHandler;
import ru.sandapocalypse.sandapocalypse.registry.ModBlocks;
import ru.sandapocalypse.sandapocalypse.registry.ModCreativeTabs;
import ru.sandapocalypse.sandapocalypse.registry.ModEntities;
import ru.sandapocalypse.sandapocalypse.registry.ModItems;

@Mod(SandApocalypse.MOD_ID)
public class SandApocalypse {
    public static final String MOD_ID = "sandapocalypse";
    public SandApocalypse() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlocks.BLOCKS.register(modBus); ModItems.ITEMS.register(modBus); ModEntities.TYPES.register(modBus); ModCreativeTabs.TABS.register(modBus);
        NetworkHandler.register();
    }
}

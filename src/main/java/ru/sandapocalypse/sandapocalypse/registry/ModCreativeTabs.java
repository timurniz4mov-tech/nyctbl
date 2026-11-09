package ru.sandapocalypse.sandapocalypse.registry;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;
import ru.sandapocalypse.sandapocalypse.SandApocalypse;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SandApocalypse.MOD_ID);
    public static final net.minecraftforge.registries.RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.sandapocalypse")).icon(() -> new ItemStack(ModBlocks.DESERT_HEART.get())).displayItems((parameters, output) -> { output.accept(ModBlocks.DESERT_HEART.get()); output.accept(ModItems.CANTEEN.get()); output.accept(ModItems.DESERT_CHARM.get()); output.accept(ModItems.SAND_WARD.get()); }).build());
    private ModCreativeTabs() {}
}

package ru.sandapocalypse.sandapocalypse.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.sandapocalypse.sandapocalypse.SandApocalypse;
import ru.sandapocalypse.sandapocalypse.item.CanteenItem;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SandApocalypse.MOD_ID);

    public static final RegistryObject<Item> DESERT_HEART_ITEM = ITEMS.register("desert_heart", () ->
            new BlockItem(ModBlocks.DESERT_HEART.get(), new Item.Properties()));

    public static final RegistryObject<Item> CANTEEN = ITEMS.register("canteen", () ->
            new CanteenItem(new Item.Properties().stacksTo(16)));

    private ModItems() {}
}

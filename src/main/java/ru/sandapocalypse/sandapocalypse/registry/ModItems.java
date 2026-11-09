package ru.sandapocalypse.sandapocalypse.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import ru.sandapocalypse.sandapocalypse.SandApocalypse;
import ru.sandapocalypse.sandapocalypse.item.CanteenItem;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SandApocalypse.MOD_ID);
    public static final RegistryObject<Item> DESERT_HEART_ITEM = ITEMS.register("desert_heart", () -> new BlockItem(ModBlocks.DESERT_HEART.get(), new Item.Properties()));
    public static final RegistryObject<Item> CANTEEN = ITEMS.register("canteen", () -> new CanteenItem(new Item.Properties().stacksTo(16)));
    public static final RegistryObject<Item> DESERT_CHARM = ITEMS.register("desert_charm", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> SAND_WARD = ITEMS.register("sand_ward", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> STORM_GOGGLES = ITEMS.register("storm_goggles", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> DESERT_HELMET = ITEMS.register("desert_helmet", () -> new ArmorItem(DesertArmorMaterial.DESERT, EquipmentSlot.HEAD, new Item.Properties()));
    public static final RegistryObject<Item> DESERT_CHESTPLATE = ITEMS.register("desert_chestplate", () -> new ArmorItem(DesertArmorMaterial.DESERT, EquipmentSlot.CHEST, new Item.Properties()));
    public static final RegistryObject<Item> DESERT_LEGGINGS = ITEMS.register("desert_leggings", () -> new ArmorItem(DesertArmorMaterial.DESERT, EquipmentSlot.LEGS, new Item.Properties()));
    public static final RegistryObject<Item> DESERT_BOOTS = ITEMS.register("desert_boots", () -> new ArmorItem(DesertArmorMaterial.DESERT, EquipmentSlot.FEET, new Item.Properties()));
    private ModItems() {}
}

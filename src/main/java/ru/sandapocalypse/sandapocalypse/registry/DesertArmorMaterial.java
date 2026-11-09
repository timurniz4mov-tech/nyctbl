package ru.sandapocalypse.sandapocalypse.registry;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Items;

public enum DesertArmorMaterial implements ArmorMaterial {
    DESERT;
    @Override public int getDurabilityForType(ArmorItem.Type type) {
        return switch (type) { case HELMET, BOOTS -> 180; case CHESTPLATE -> 240; case LEGGINGS -> 210; };
    }
    @Override public int getDefenseForType(ArmorItem.Type type) {
        return switch (type) { case HELMET -> 2; case CHESTPLATE -> 5; case LEGGINGS -> 4; case BOOTS -> 2; default -> 0; };
    }
    @Override public int getEnchantmentValue() { return 12; }
    @Override public SoundEvent getEquipSound() { return SoundEvents.ARMOR_EQUIP_LEATHER; }
    @Override public Ingredient getRepairIngredient() { return Ingredient.of(Items.LEATHER, Items.RABBIT_HIDE); }
    @Override public String getName() { return "sandapocalypse:desert_guardian"; }
    @Override public float getToughness() { return 1.0F; }
    @Override public float getKnockbackResistance() { return 0.0F; }
}

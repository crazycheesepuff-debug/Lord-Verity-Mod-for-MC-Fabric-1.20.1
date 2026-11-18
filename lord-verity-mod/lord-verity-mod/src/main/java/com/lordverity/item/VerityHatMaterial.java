package com.lordverity.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

/** Lets the hat be worn on your head. Texture: assets/minecraft/textures/models/armor/verity_hat_layer_1.png */
public enum VerityHatMaterial implements ArmorMaterial {
    INSTANCE;

    @Override public int getDurability(ArmorItem.Type type) { return 165; }
    @Override public int getProtection(ArmorItem.Type type) { return 1; }
    @Override public int getEnchantability() { return 15; }
    @Override public SoundEvent getEquipSound() { return SoundEvents.ITEM_ARMOR_EQUIP_LEATHER; }
    @Override public Ingredient getRepairIngredient() { return Ingredient.EMPTY; }
    @Override public String getName() { return "verity_hat"; }
    @Override public float getToughness() { return 0.0f; }
    @Override public float getKnockbackResistance() { return 0.0f; }
}

package com.lordverity;

import com.lordverity.item.VerityDiscItem;
import com.lordverity.item.VerityHatMaterial;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public final class ModItems {
    private ModItems() {}

    public static final Item LORD_VERITY_HAT = register("lord_verity_hat",
            new ArmorItem(VerityHatMaterial.INSTANCE, ArmorItem.Type.HELMET,
                    new Item.Settings().maxCount(1).rarity(Rarity.EPIC)));

    public static final Item LORD_VERITY_DISC = register("lord_verity_disc",
            new VerityDiscItem(14, ModSounds.MASTER_OF_HUMANITY,
                    new Item.Settings().maxCount(1).rarity(Rarity.RARE),
                    VerityConfig.DISC_LENGTH_SECONDS));

    public static final Item LORD_VERITY_SPAWN_EGG = register("lord_verity_spawn_egg",
            new SpawnEggItem(ModEntities.LORD_VERITY, 0xF4F4F8, 0xE2A81C, new Item.Settings()));

    private static Item register(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier(LordVerityMod.MOD_ID, name), item);
    }

    public static void register() {
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(e -> e.add(LORD_VERITY_DISC));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(e -> e.add(LORD_VERITY_HAT));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(e -> e.add(LORD_VERITY_SPAWN_EGG));
    }
}

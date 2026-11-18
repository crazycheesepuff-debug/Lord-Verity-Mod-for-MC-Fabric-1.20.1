package com.lordverity;

import com.lordverity.entity.VerityEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModEntities {
    private ModEntities() {}

    /** 1.0 wide, 4.0 tall (an Enderman is 2.9). */
    public static final EntityType<VerityEntity> LORD_VERITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(LordVerityMod.MOD_ID, "lord_verity"),
            EntityType.Builder.create(VerityEntity::new, SpawnGroup.MISC)
                    .setDimensions(1.0f, 4.0f)
                    .maxTrackingRange(10)
                    .build("lord_verity"));

    public static void register() {
        FabricDefaultAttributeRegistry.register(LORD_VERITY, VerityEntity.createVerityAttributes());
    }
}

package com.lordverity.client;

import com.lordverity.LordVerityMod;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public final class ModModelLayers {
    private ModModelLayers() {}

    public static final EntityModelLayer VERITY =
            new EntityModelLayer(new Identifier(LordVerityMod.MOD_ID, "lord_verity"), "main");
}

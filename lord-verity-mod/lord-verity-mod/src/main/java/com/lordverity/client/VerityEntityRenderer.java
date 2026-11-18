package com.lordverity.client;

import com.lordverity.LordVerityMod;
import com.lordverity.entity.VerityEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

public class VerityEntityRenderer extends MobEntityRenderer<VerityEntity, VerityModel> {
    private static final Identifier TEXTURE =
            new Identifier(LordVerityMod.MOD_ID, "textures/entity/lord_verity.png");

    public VerityEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new VerityModel(ctx.getPart(ModModelLayers.VERITY)), 1.0f);
    }

    @Override
    public Identifier getTexture(VerityEntity entity) {
        return TEXTURE;
    }
}

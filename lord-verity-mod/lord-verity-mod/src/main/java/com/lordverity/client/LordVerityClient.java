package com.lordverity.client;

import com.lordverity.ModEntities;
import com.lordverity.ModSounds;
import com.lordverity.VerityNetworking;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class LordVerityClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.VERITY, VerityModel::createModelData);
        EntityRendererRegistry.register(ModEntities.LORD_VERITY, VerityEntityRenderer::new);

        // Global (non-positional) sound: same volume wherever you are, even if he's 200 blocks away.
        ClientPlayNetworking.registerGlobalReceiver(VerityNetworking.TAUNT_ID,
                (client, handler, buf, responseSender) -> client.execute(() ->
                        client.getSoundManager().play(
                                PositionedSoundInstance.master(ModSounds.DAMN, 1.0f, 1.0f))));
    }
}

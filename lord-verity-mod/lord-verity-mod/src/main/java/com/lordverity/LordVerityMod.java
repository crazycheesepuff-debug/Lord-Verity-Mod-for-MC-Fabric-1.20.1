package com.lordverity;

import net.fabricmc.api.ModInitializer;

public class LordVerityMod implements ModInitializer {
    public static final String MOD_ID = "lordverity";

    @Override
    public void onInitialize() {
        ModSounds.register();      // sounds first: items reference them
        ModEntities.register();    // entity type before the spawn egg
        ModItems.register();
        VerityTrigger.register();  // the chat listener
    }
}

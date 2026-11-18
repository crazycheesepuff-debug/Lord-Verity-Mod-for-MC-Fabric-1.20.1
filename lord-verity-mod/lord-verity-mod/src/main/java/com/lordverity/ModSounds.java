package com.lordverity;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public final class ModSounds {
    private ModSounds() {}

    public static final SoundEvent DAMN = register("damn");
    public static final SoundEvent MASTER_OF_HUMANITY = register("master_of_humanity");

    private static SoundEvent register(String name) {
        Identifier id = new Identifier(LordVerityMod.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }

    public static void register() {}
}

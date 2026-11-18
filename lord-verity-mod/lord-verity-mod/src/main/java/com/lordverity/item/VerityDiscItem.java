package com.lordverity.item;

import net.minecraft.item.MusicDiscItem;
import net.minecraft.sound.SoundEvent;

/** MusicDiscItem's constructor is protected, so we subclass it. */
public class VerityDiscItem extends MusicDiscItem {
    public VerityDiscItem(int comparatorOutput, SoundEvent sound, Settings settings, int lengthInSeconds) {
        super(comparatorOutput, sound, settings, lengthInSeconds);
    }
}

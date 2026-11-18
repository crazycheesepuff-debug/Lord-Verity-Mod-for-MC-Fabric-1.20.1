package com.lordverity;

import net.minecraft.util.Identifier;

public final class VerityNetworking {
    private VerityNetworking() {}

    /** Server -> client: "play the I-don't-give-a-damn clip, globally". */
    public static final Identifier TAUNT_ID = new Identifier(LordVerityMod.MOD_ID, "taunt");
}

package com.lordverity;

/**
 * All the knobs for the gag in one place. Change a number, rebuild, done.
 * (Times are in ticks: 20 ticks = 1 second.)
 */
public final class VerityConfig {
    private VerityConfig() {}

    /** Minimum time between Verity visits for the same player. */
    public static final int COOLDOWN_TICKS = 20 * 30;

    /** How far from the player he lightning-strikes into existence (blocks). */
    public static final double SPAWN_MIN_DIST = 12.0;
    public static final double SPAWN_MAX_DIST = 18.0;

    /** Time he stands there cackling before he starts running. The "I don't give a damn!" part is ~2.4s of the clip. */
    public static final int INTRO_TICKS = 50;

    /** Movement speed attribute while chasing. Zombie = 0.23, sprinting player ~0.13. 1.0 is absurdly fast. */
    public static final double MOVE_SPEED = 1.0;

    /** How close (horizontal blocks) he has to be to slap. */
    public static final double SLAP_REACH = 2.6;

    /** Slap damage in half-hearts. It never takes you below half a heart. Set to 0 for a purely emotional slap. */
    public static final float SLAP_DAMAGE = 1.0f;

    /** How hard the slap shoves you. */
    public static final double SLAP_KNOCKBACK = 1.2;

    /** How far the slap whips your head sideways (degrees, randomly left or right). */
    public static final float SLAP_HEAD_SPIN = 110.0f;

    /** Delay between the slap landing and the poof of smoke. */
    public static final int SLAP_TO_POOF_TICKS = 14;

    /** If he is still not there after this long (stuck behind terrain?), he teleports next to you. */
    public static final int CHASE_TIMEOUT_TICKS = 20 * 8;

    /** Hard safety net: he always vanishes after this long. */
    public static final int MAX_LIFETIME_TICKS = 20 * 45;

    /** Set the world time to this (18000 = midnight). */
    public static final long MIDNIGHT = 18000L;

    /** Music disc length in seconds (the song is 2:20.8). Used by jukeboxes / comparators. */
    public static final int DISC_LENGTH_SECONDS = 141;
}

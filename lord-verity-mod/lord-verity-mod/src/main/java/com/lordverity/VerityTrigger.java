package com.lordverity;

import com.lordverity.entity.VerityEntity;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;

/** Listens to chat; when somebody asks a question, summons Lord Verity. */
public final class VerityTrigger {
    private VerityTrigger() {}

    private static final Map<UUID, Integer> LAST_TRIGGER = new HashMap<>();

    /** Messages that START with one of these count as questions even without a "?". */
    private static final Pattern QUESTION_START = Pattern.compile(
            "^(what|what's|whats|who|who's|whos|whom|whose|where|where's|wheres|when|when's|why|why's|how|how's|hows|which)\\b.*");

    public static void register() {
        ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
            if (!isQuestion(message.getSignedContent())) return;
            MinecraftServer server = sender.getServer();
            if (server != null) server.execute(() -> trigger(sender));
        });
    }

    static boolean isQuestion(String raw) {
        if (raw == null) return false;
        String s = raw.trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty()) return false;
        return s.endsWith("?") || QUESTION_START.matcher(s).matches();
    }

    private static void trigger(ServerPlayerEntity player) {
        if (!player.isAlive() || player.isSpectator()) return;
        ServerWorld world = player.getServerWorld();
        MinecraftServer server = world.getServer();

        int now = server.getTicks();
        Integer last = LAST_TRIGGER.get(player.getUuid());
        if (last != null && now - last < VerityConfig.COOLDOWN_TICKS) return;
        if (hasActiveVerity(world, player)) return;
        LAST_TRIGGER.put(player.getUuid(), now);

        Vec3d pos = findSpawnPos(world, player);

        // 1) lightning strike (cosmetic: no fire, no damage)
        LightningEntity bolt = EntityType.LIGHTNING_BOLT.create(world);
        if (bolt != null) {
            bolt.refreshPositionAfterTeleport(pos.x, pos.y, pos.z);
            bolt.setCosmetic(true);
            world.spawnEntity(bolt);
        }

        // 2) midnight
        setMidnight(world, server);

        // 3) Lord Verity appears, already looking at you
        VerityEntity verity = ModEntities.LORD_VERITY.create(world);
        if (verity != null) {
            double dx = player.getX() - pos.x, dz = player.getZ() - pos.z;
            float yaw = (float) (MathHelper.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
            verity.refreshPositionAndAngles(pos.x, pos.y, pos.z, yaw, 0.0f);
            verity.setBodyYaw(yaw);
            verity.setHeadYaw(yaw);
            verity.setVerityTarget(player.getUuid());
            world.spawnEntity(verity);
        }

        // 4) "I don't give a damn! ahahaha!" for every player, globally
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            if (ServerPlayNetworking.canSend(p, VerityNetworking.TAUNT_ID)) {
                ServerPlayNetworking.send(p, VerityNetworking.TAUNT_ID, PacketByteBufs.create());
            }
        }
    }

    private static boolean hasActiveVerity(ServerWorld world, ServerPlayerEntity player) {
        Box area = player.getBoundingBox().expand(160.0);
        return !world.getEntitiesByClass(VerityEntity.class, area,
                e -> player.getUuid().equals(e.getTargetId())).isEmpty();
    }

    private static void setMidnight(ServerWorld world, MinecraftServer server) {
        long day = world.getTimeOfDay() / 24000L;
        world.setTimeOfDay(day * 24000L + VerityConfig.MIDNIGHT);
        // push it to clients immediately instead of waiting for the next periodic sync
        server.getPlayerManager().sendToDimension(
                new WorldTimeUpdateS2CPacket(world.getTime(), world.getTimeOfDay(),
                        world.getGameRules().getBoolean(GameRules.DO_DAYLIGHT_CYCLE)),
                world.getRegistryKey());
    }

    /** Finds open ground 12-18 blocks away with room for a 4-block-tall guy; falls back to just in front of the player. */
    private static Vec3d findSpawnPos(ServerWorld world, ServerPlayerEntity player) {
        Random r = world.getRandom();
        for (int attempt = 0; attempt < 60; attempt++) {
            double angle = r.nextDouble() * Math.PI * 2.0;
            double dist = VerityConfig.SPAWN_MIN_DIST
                    + r.nextDouble() * (VerityConfig.SPAWN_MAX_DIST - VerityConfig.SPAWN_MIN_DIST);
            int bx = MathHelper.floor(player.getX() + Math.cos(angle) * dist);
            int bz = MathHelper.floor(player.getZ() + Math.sin(angle) * dist);
            int startY = MathHelper.floor(player.getY()) + 6;
            for (int y = startY; y >= startY - 12; y--) {
                BlockPos pos = new BlockPos(bx, y, bz);
                BlockPos below = pos.down();
                BlockState ground = world.getBlockState(below);
                if (!ground.isSideSolidFullSquare(world, below, Direction.UP)) continue;
                if (!world.getFluidState(pos).isEmpty()) continue;
                Box box = ModEntities.LORD_VERITY.getDimensions().getBoxAt(bx + 0.5, y, bz + 0.5);
                if (world.isSpaceEmpty(box)) return new Vec3d(bx + 0.5, y, bz + 0.5);
            }
        }
        Vec3d look = Vec3d.fromPolar(0.0f, player.getYaw()).normalize();
        return player.getPos().add(look.multiply(5.0));
    }
}

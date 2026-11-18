package com.lordverity.entity;

import com.lordverity.ModItems;
import com.lordverity.VerityConfig;
import java.util.EnumSet;
import java.util.UUID;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * Lord Verity. Spawned by {@link com.lordverity.VerityTrigger}; lives for one gag:
 * INTRO (cackle) -> CHASE (run at the player) -> SLAPPED (swing) -> poof + drops.
 */
public class VerityEntity extends PathAwareEntity {
    public static final int PHASE_INTRO = 0;
    public static final int PHASE_CHASE = 1;
    public static final int PHASE_SLAPPED = 2;

    private static final TrackedData<Integer> PHASE =
            DataTracker.registerData(VerityEntity.class, TrackedDataHandlerRegistry.INTEGER);

    @Nullable
    private UUID targetId;
    private int phaseTicks;

    public VerityEntity(EntityType<? extends PathAwareEntity> type, World world) {
        super(type, world);
        this.setPersistent();        // never despawns on its own
        this.setInvulnerable(true);  // also makes suffocation/falling harmless if he has to teleport
    }

    public static DefaultAttributeContainer.Builder createVerityAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, VerityConfig.MOVE_SPEED)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 64.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(PHASE, PHASE_INTRO);
    }

    public int getPhase() { return this.dataTracker.get(PHASE); }
    private void setPhase(int phase) { this.dataTracker.set(PHASE, phase); this.phaseTicks = 0; }

    public void setVerityTarget(UUID id) { this.targetId = id; }
    @Nullable public UUID getTargetId() { return this.targetId; }

    @Override
    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 3.5f;
    }

    // ------------------------------------------------------------------ the gag

    @Override
    protected void mobTick() {
        super.mobTick();
        if (!(this.getWorld() instanceof ServerWorld world)) return;

        if (this.age > VerityConfig.MAX_LIFETIME_TICKS) { poof(world, true); return; }

        ServerPlayerEntity target = findTarget(world);
        if (target == null) { poof(world, false); return; }

        switch (getPhase()) {
            case PHASE_INTRO -> {
                this.getNavigation().stop();
                faceTarget(target);
                if (++this.phaseTicks >= VerityConfig.INTRO_TICKS) setPhase(PHASE_CHASE);
            }
            case PHASE_CHASE -> {
                this.phaseTicks++;
                if (inReach(target)) {
                    slap(world, target);
                    setPhase(PHASE_SLAPPED);
                    return;
                }
                if (this.phaseTicks == VerityConfig.CHASE_TIMEOUT_TICKS) teleportBehind(target);
                if (this.age % 2 == 0 || this.getNavigation().isIdle()) {
                    this.getNavigation().startMovingTo(target, 1.0);
                }
            }
            default -> {
                this.getNavigation().stop();
                faceTarget(target);
                if (++this.phaseTicks >= VerityConfig.SLAP_TO_POOF_TICKS) poof(world, true);
            }
        }
    }

    @Nullable
    private ServerPlayerEntity findTarget(ServerWorld world) {
        if (this.targetId == null) {
            // Summoned by /summon or the spawn egg: adopt the nearest player as the victim.
            net.minecraft.entity.player.PlayerEntity nearest = world.getClosestPlayer(this, 64.0);
            if (nearest == null) return null;
            this.targetId = nearest.getUuid();
        }
        ServerPlayerEntity p = world.getServer().getPlayerManager().getPlayer(this.targetId);
        if (p == null || p.isRemoved() || !p.isAlive() || p.getWorld() != world) return null;
        return p;
    }

    private boolean inReach(ServerPlayerEntity target) {
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double dy = target.getY() - this.getY();
        return dx * dx + dz * dz <= VerityConfig.SLAP_REACH * VerityConfig.SLAP_REACH && dy > -2.5 && dy < 4.0;
    }

    private void faceTarget(ServerPlayerEntity target) {
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        float yaw = (float) (MathHelper.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0f;
        this.setYaw(yaw);
        this.setBodyYaw(yaw);
        this.setHeadYaw(yaw);
    }

    private void teleportBehind(ServerPlayerEntity target) {
        // Stuck behind terrain? Pop up right in front of the player instead.
        Vec3d look = Vec3d.fromPolar(0.0f, target.getYaw()).normalize();
        Vec3d pos = target.getPos().add(look.multiply(2.2));
        this.getNavigation().stop();
        this.refreshPositionAndAngles(pos.x, target.getY(), pos.z, this.getYaw(), 0.0f);
    }

    private void slap(ServerWorld world, ServerPlayerEntity target) {
        faceTarget(target);
        this.swingHand(Hand.MAIN_HAND);
        world.playSound(null, this.getX(), this.getY() + 2.0, this.getZ(),
                SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.HOSTILE, 1.6f, 0.7f);

        // Never lethal: leaves you at least half a heart.
        float dmg = Math.min(VerityConfig.SLAP_DAMAGE, Math.max(0.0f, target.getHealth() - 1.0f));
        target.damage(this.getDamageSources().mobAttack(this), dmg);
        target.takeKnockback(VerityConfig.SLAP_KNOCKBACK,
                this.getX() - target.getX(), this.getZ() - target.getZ());
        target.velocityModified = true;

        // Whip the victim's head sideways, like they've been slapped across the face.
        float spin = (this.random.nextBoolean() ? 1.0f : -1.0f) * (VerityConfig.SLAP_HEAD_SPIN + this.random.nextInt(20));
        target.networkHandler.requestTeleport(0.0, 0.0, 0.0,
                target.getYaw() + spin, MathHelper.clamp(target.getPitch() - 8.0f, -90.0f, 90.0f),
                EnumSet.of(PositionFlag.X, PositionFlag.Y, PositionFlag.Z));
    }

    private void poof(ServerWorld world, boolean dropLoot) {
        double x = this.getX(), y = this.getY() + 2.0, z = this.getZ();
        world.spawnParticles(ParticleTypes.POOF, x, y, z, 90, 0.6, 1.8, 0.6, 0.08);
        world.spawnParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 40, 0.6, 1.8, 0.6, 0.04);
        world.playSound(null, x, y, z, SoundEvents.ENTITY_ILLUSIONER_MIRROR_MOVE,
                SoundCategory.HOSTILE, 1.6f, 1.0f);
        if (dropLoot) {
            this.dropStack(new ItemStack(ModItems.LORD_VERITY_HAT));
            this.dropStack(new ItemStack(ModItems.LORD_VERITY_DISC));
        }
        this.discard();
    }

    // ------------------------------------------------------------------ save / load

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.targetId != null) nbt.putUuid("VerityTarget", this.targetId);
        nbt.putInt("VerityPhase", getPhase());
        nbt.putInt("VerityPhaseTicks", this.phaseTicks);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.containsUuid("VerityTarget")) this.targetId = nbt.getUuid("VerityTarget");
        this.dataTracker.set(PHASE, nbt.getInt("VerityPhase"));
        this.phaseTicks = nbt.getInt("VerityPhaseTicks");
    }
}

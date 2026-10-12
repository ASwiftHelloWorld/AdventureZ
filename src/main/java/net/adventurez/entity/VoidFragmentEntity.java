package net.adventurez.entity;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;

import net.minecraft.tags.DamageTypeTags;
import org.jetbrains.annotations.Nullable;

import net.adventurez.entity.nonliving.ThrownRockEntity;
import net.adventurez.entity.nonliving.VoidBulletEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;

public class VoidFragmentEntity extends AdventureFlyingMob implements Enemy {

    public static final EntityDataAccessor<Boolean> IS_VOID_ORB = SynchedEntityData.defineId(VoidFragmentEntity.class, EntityDataSerializers.BOOLEAN);
    public boolean isVoidOrb;
    private int deathTick;

    public VoidFragmentEntity(EntityType<? extends AdventureFlyingMob> entityType, Level world) {
        super(entityType, world);
    }

    public static AttributeSupplier.Builder createVoidFragmentAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 10.0D);
    }

    @Override
    public SpawnGroupData initialize(ServerLevelAccessor world, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        if (spawnReason == EntitySpawnReason.SPAWN_EGG) {
            this.setVoidOrb(true);
        }

        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_VOID_ORB, false);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("IsVoidOrb", this.isVoidOrb);

    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        this.isVoidOrb = input.getBooleanOr("IsVoidOrb", false);
        this.setVoidOrb(this.isVoidOrb);
    }

    public void setVoidOrb(boolean orb) {
        this.isVoidOrb = orb;
        entityData.set(IS_VOID_ORB, orb);
        this.refreshPosition();
        this.calculateDimensions();
    }

    @Override
    protected EntityDimensions getBaseDimensions(Pose pose) {
        return super.getBaseDimensions(pose).scaled(1.0F, (this.isVoidOrb || this.entityData.get(IS_VOID_ORB)) ? 2.1F : 1.2F);
    }

    @Override
    public void onTrackedDataSet(EntityDataAccessor<?> data) {
        if (IS_VOID_ORB.equals(data)) {
            this.refreshPosition();
            this.calculateDimensions();
            this.setYaw(this.headYaw);
            this.bodyYaw = this.headYaw;
        }

        super.onTrackedDataSet(data);
    }

    @Override
    public void updatePostDeath() {
        if (this.level().isClientSide()) {
            for (int i = 0; i < 10; ++i) {
                double d = this.random.nextGaussian() * 0.05D;
                double e = this.random.nextGaussian() * 0.05D;
                double f = this.random.nextGaussian() * 0.05D;
                this.level().addParticle(ParticleTypes.END_ROD, this.getParticleX(1.0D), this.getRandomBodyY(), this.getParticleZ(1.0D), d, e, f);
            }
        }
        ++this.deathTick;
        if (this.deathTick >= 20 && !this.level().isClientSide()) {
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    @Override
    public void onDeath(DamageSource source) {
        if (!this.level().isClientSide() && this.isVoidOrb) {
            AABB box = new AABB(this.getBlockPos());
            List<VoidShadowEntity> list = this.level().getEntitiesOfClass(VoidShadowEntity.class, box.inflate(120D), EntitySelector.NO_SPECTATORS);
            for (int i = 0; i < list.size(); ++i) {
                list.get(i).damage(this.getDamageSources().magic(), 40f);
            }
        }
        super.onDeath(source);
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if (!this.level().isClientSide() && this.level().getClosestPlayer(TargetingConditions.createAttackable().setBaseMaxDistance(0.8D), this) != null) {
            this.dead = true;
            this.level().createExplosion(this, this.getX(), this.getY(), this.getZ(), this.isVoidOrb ? 4.0F : 3.0F, ExplosionInteraction.NONE);
            this.discard();
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return super.damage(source, amount);
        } else if ((source.getAttacker() != null && source.getAttacker() instanceof Player && ((Player) source.getAttacker()).isCreative())) {
            return super.damage(source, amount);
        }
        if (this.isInvulnerableTo(source) || source.getSource() instanceof ThrownRockEntity || (this.isVoidOrb && !(source.getSource() instanceof VoidBulletEntity))) {
            return false;
        } else {
            return super.damage(source, source.getSource() instanceof VoidBulletEntity ? this.getHealth() : amount);
        }
    }

    @Override
    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    public boolean addStatusEffect(MobEffectInstance effect, Entity entity) {
        return false;
    }

    @Override
    public boolean canStartRiding(Entity entity) {
        return false;
    }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return false;
    }

    @Override
    public boolean canHaveStatusEffect(MobEffectInstance effect) {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isPushedByFluids() {
        return false;
    }

}

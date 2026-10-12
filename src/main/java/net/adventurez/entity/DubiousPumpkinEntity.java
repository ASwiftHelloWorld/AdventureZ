package net.adventurez.entity;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import net.adventurez.init.SoundInit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.TimeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DubiousPumpkinEntity extends PathfinderMob implements NeutralMob {
    public static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(DubiousPumpkinEntity.class, EntityDataSerializers.BOOLEAN);
    private static final UniformInt ANGER_TIME_RANGE = TimeUtil.betweenSeconds(10, 29);
    private int angerTime;
    @Nullable
    private UUID angryAt;

    public DubiousPumpkinEntity(EntityType<? extends DubiousPumpkinEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new DubiousPumpkinEntity.AttackGoal());
        this.goalSelector.addGoal(5, new WanderAroundGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtEntityGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new LookAroundGoal(this));
        this.targetSelector.addGoal(1, new DubiousPumpkinEntity.DubiousPumpkinRevengeGoal());
        this.targetSelector.addGoal(3, new ActiveTargetGoal<>(this, Player.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.addGoal(4, new ActiveTargetGoal<>(this, Zombie.class, 10, true, true, null));
        this.targetSelector.addGoal(5, new UniversalAngerGoal<>(this, false));
    }

    public static AttributeSupplier.Builder createDubiousPumpkinAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 10.0).add(Attributes.FOLLOW_RANGE, 20.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.ATTACK_DAMAGE, 1.0);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput input) {
        super.readAdditionalSaveData(input);
        this.readAngerFromNbt(this.level(), input);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput output) {
        super.addAdditionalSaveData(output);
        this.writeAngerToNbt(output);
    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }

    @Override
    public void setAngerTime(int angerTime) {
        this.angerTime = angerTime;
    }

    @Override
    public int getAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setAngryAt(@Nullable UUID angryAt) {
        this.entityData.set(ANGRY, angryAt != null);
        this.angryAt = angryAt;
    }

    @Override
    public void stopAnger() {
        NeutralMob.super.stopAnger();
        this.entityData.set(ANGRY, false);
    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return this.angryAt;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundInit.DUBIOUS_PUMPKIN_IDLE_EVENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundInit.DUBIOUS_PUMPKIN_HURT_EVENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundInit.DUBIOUS_PUMPKIN_DEATH_EVENT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundInit.DUBIOUS_PUMPKIN_STEP_EVENT, 0.15F, 1.0F);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANGRY, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            this.tickAngerLogic((ServerLevel) this.level(), true);
        }
    }

    @Override
    protected float getBaseMovementSpeedMultiplier() {
        return 0.98F;
    }

    private class AttackGoal extends MeleeAttackGoal {
        public AttackGoal() {
            super(DubiousPumpkinEntity.this, 1.25, true);
        }

        @Override
        protected void attack(LivingEntity target) {
            if (this.canAttack(target)) {
                this.resetCooldown();
                this.mob.tryAttack(target);
            } else if (this.mob.squaredDistanceTo(target) < (target.getWidth() + 3.0F) * (target.getWidth() + 3.0F)) {
                if (this.isCooledDown()) {
                    this.resetCooldown();
                }
            } else {
                this.resetCooldown();
            }
        }

        @Override
        public void stop() {
            super.stop();
        }
    }

    private class DubiousPumpkinRevengeGoal extends RevengeGoal {
        public DubiousPumpkinRevengeGoal() {
            super(DubiousPumpkinEntity.this);
        }

        @Override
        public void start() {
            super.start();
            if (DubiousPumpkinEntity.this.isBaby()) {
                this.callSameTypeForRevenge();
                this.stop();
            }
        }

        @Override
        protected void setMobEntityTarget(Mob mob, LivingEntity target) {
            if (mob instanceof DubiousPumpkinEntity && !mob.isBaby()) {
                super.setMobEntityTarget(mob, target);
            }
        }
    }

}

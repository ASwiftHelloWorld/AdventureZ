package net.adventurez.entity;

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
    public static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.registerData(DubiousPumpkinEntity.class, EntityDataSerializers.BOOLEAN);
    private static final UniformInt ANGER_TIME_RANGE = TimeUtil.betweenSeconds(10, 29);
    private int angerTime;
    @Nullable
    private UUID angryAt;

    public DubiousPumpkinEntity(EntityType<? extends DubiousPumpkinEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new DubiousPumpkinEntity.AttackGoal());
        this.goalSelector.add(5, new WanderAroundGoal(this, 1.0));
        this.goalSelector.add(6, new LookAtEntityGoal(this, Player.class, 6.0F));
        this.goalSelector.add(7, new LookAroundGoal(this));
        this.targetSelector.add(1, new DubiousPumpkinEntity.DubiousPumpkinRevengeGoal());
        this.targetSelector.add(3, new ActiveTargetGoal<>(this, Player.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(4, new ActiveTargetGoal<>(this, Zombie.class, 10, true, true, null));
        this.targetSelector.add(5, new UniversalAngerGoal<>(this, false));
    }

    public static AttributeSupplier.Builder createDubiousPumpkinAttributes() {
        return Mob.createMobAttributes().add(Attributes.GENERIC_MAX_HEALTH, 10.0).add(Attributes.GENERIC_FOLLOW_RANGE, 20.0).add(Attributes.GENERIC_MOVEMENT_SPEED, 0.25).add(Attributes.GENERIC_ATTACK_DAMAGE, 1.0);
    }

    @Override
    public void readCustomDataFromNbt(CompoundTag nbt) {
        super.readCustomDataFromNbt(nbt);
        this.readAngerFromNbt(this.level(), nbt);
    }

    @Override
    public void writeCustomDataToNbt(CompoundTag nbt) {
        super.writeCustomDataToNbt(nbt);
        this.writeAngerToNbt(nbt);
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
        this.dataTracker.set(ANGRY, angryAt != null);
        this.angryAt = angryAt;
    }

    @Override
    public void stopAnger() {
        NeutralMob.super.stopAnger();
        this.dataTracker.set(ANGRY, false);
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
    protected void initDataTracker(SynchedEntityData.Builder builder) {
        super.initDataTracker(builder);
        builder.add(ANGRY, false);
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

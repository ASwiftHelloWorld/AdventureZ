package net.adventurez.entity;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import net.adventurez.init.SoundInit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.skeleton.WitherSkeleton;

public class PiglinBeastEntity extends Monster {
    public static final EntityDataAccessor<Float> ATTACK_TICK_VISUAL = SynchedEntityData.registerData(PiglinBeastEntity.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Float> LEAD_ARM = SynchedEntityData.registerData(PiglinBeastEntity.class, EntityDataSerializers.FLOAT);
    private float attackTick;
    public float armTick;
    private int makePiglinsAngry = 0;

    public PiglinBeastEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.experiencePoints = 30;
        this.setPathfindingPenalty(PathType.LAVA, 8.0F);
    }

    public static AttributeSupplier.Builder createPiglinBeastAttributes() {
        return Monster.createHostileAttributes().add(Attributes.GENERIC_MAX_HEALTH, 80.0D).add(Attributes.GENERIC_MOVEMENT_SPEED, 0.225D)
                .add(Attributes.GENERIC_KNOCKBACK_RESISTANCE, 0.5D).add(Attributes.GENERIC_ATTACK_DAMAGE, 9.0D).add(Attributes.GENERIC_ATTACK_KNOCKBACK, 2D)
                .add(Attributes.GENERIC_FOLLOW_RANGE, 38.0D);
    }

    @Override
    public void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new FloatGoal(this));
        this.goalSelector.add(1, new AttackGoal());
        this.goalSelector.add(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.add(5, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.add(6, new RandomLookAroundGoal(this));
        this.goalSelector.add(7, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.add(1, new NearestAttackableTargetGoal<>(this, WitherSkeleton.class, true));
        this.targetSelector.add(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.add(3, new NearestAttackableTargetGoal<>(this, BlazeGuardianEntity.class, true));
        this.targetSelector.add(4, (new HurtByTargetGoal(this, new Class[] { Piglin.class })));
    }

    @Override
    public void mobTick() {
        if (attackTick > 0F) {
            attackTick = attackTick - 0.08F;
            dataTracker.set(ATTACK_TICK_VISUAL, attackTick);
        }
        if (this.getTarget() instanceof Player) {
            makePiglinsAngry++;
            if (makePiglinsAngry == 600) {
                dataTracker.set(LEAD_ARM, 1F);
                getPiglins();
                this.level().playSoundFromEntity(null, this, SoundInit.PIGLINBEAST_SHOUT_EVENT, SoundSource.HOSTILE, 1.0F, 1.0F);
                this.getAttributeInstance(Attributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.0D);
            }
            if (makePiglinsAngry > 600) {
                dataTracker.set(LEAD_ARM, dataTracker.get(LEAD_ARM) - 0.02F);
            }
            if (makePiglinsAngry == 650) {
                this.getAttributeInstance(Attributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.23D);
                makePiglinsAngry = 0;
            }
        }
    }

    @Override
    public void writeCustomDataToNbt(CompoundTag tag) {
        super.writeCustomDataToNbt(tag);
        tag.putFloat("AttackTick", this.attackTick);
        tag.putFloat("LeadArm", 0F);
    }

    @Override
    public void readCustomDataFromNbt(CompoundTag tag) {
        super.readCustomDataFromNbt(tag);
        this.attackTick = tag.getFloat("AttackTick");
        this.armTick = tag.getFloat("LeadArm");
    }

    @Override
    protected void initDataTracker(Builder builder) {
        super.initDataTracker(builder);
        builder.add(ATTACK_TICK_VISUAL, 0F);
        builder.add(LEAD_ARM, 0F);
    }

    @Override
    public boolean canSpawn(LevelReader world) {
        return !world.containsFluid(this.getBoundingBox());
    }

    @Override
    public boolean canImmediatelyDespawn(double num) {
        return false;
    }

    @Override
    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }

    }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundInit.PIGLINBEAST_IDLE_EVENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundInit.PIGLINBEAST_HURT_EVENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundInit.PIGLINBEAST_DEATH_EVENT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundInit.PIGLINBEAST_WALK_EVENT, 0.5F, 1.0F);
    }

    private class AttackGoal extends MeleeAttackGoal {

        public AttackGoal() {
            super(PiglinBeastEntity.this, 1.0D, true);
        }

        @Override
        protected void attack(LivingEntity target) {
            if (canAttack(target) && attackTick <= 0F) {
                this.mob.level().playSoundFromEntity(null, PiglinBeastEntity.this, SoundInit.PIGLINBEAST_CLUBSWING_EVENT, SoundSource.HOSTILE, 1.0F, 1.0F);
                this.resetCooldown();
                this.mob.tryAttack(target);
                attackTick = 1F;
            }
        }
    }

    public void getPiglins() {
        List<Piglin> list = this.level().getEntitiesByClass(Piglin.class, this.getBoundingBox().inflate(40D), EntitySelector.EXCEPT_SPECTATOR);
        for (int i = 0; i < list.size(); ++i) {
            angerNearbyPiglins(list.get(i));
        }
    }

    private static void angerNearbyPiglins(AbstractPiglin piglin) {
        getNearbyPiglins(piglin).forEach((abstractPiglinEntity) -> {
            getNearestDetectedPlayer(abstractPiglinEntity).ifPresent((playerEntity) -> {
                becomeAngryWith(abstractPiglinEntity, playerEntity);
            });
        });
    }

    public static Optional<Player> getNearestDetectedPlayer(AbstractPiglin piglin) {
        return piglin.getBrain().hasMemoryModule(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER) ? piglin.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_TARGETABLE_PLAYER)
                : Optional.empty();
    }

    public static void becomeAngryWith(AbstractPiglin piglin, LivingEntity target) {
        piglin.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        piglin.getBrain().remember(MemoryModuleType.ANGRY_AT, target.getUuid(), 600L);

        if (target.getType() == EntityType.PLAYER && piglin.level().getGameRules().getBoolean(GameRules.UNIVERSAL_ANGER)) {
            piglin.getBrain().remember(MemoryModuleType.UNIVERSAL_ANGER, true, 600L);
        }
        if (!piglin.level().isClientSide()) {
            piglin.getAttributeInstance(Attributes.GENERIC_ATTACK_DAMAGE).setBaseValue(piglin.getAttributeValue(Attributes.GENERIC_ATTACK_DAMAGE) + 3.0D);
            piglin.heal(piglin.getMaxHealth());
        }

    }

    private static List<AbstractPiglin> getNearbyPiglins(AbstractPiglin piglin) {
        return (List<AbstractPiglin>) piglin.getBrain().getOptionalMemory(MemoryModuleType.NEARBY_ADULT_PIGLINS).orElse(ImmutableList.of());
    }

}

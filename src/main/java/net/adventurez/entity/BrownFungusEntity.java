package net.adventurez.entity;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.adventurez.init.SoundInit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

public class BrownFungusEntity extends PathfinderMob implements NeutralMob {
    private static final UniformInt ANGER_TIME_RANGE;
    private int angerTime;
    private UUID angryAt;

    public BrownFungusEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.experiencePoints = 2;
    }

    @Override
    public void initGoals() {
        this.goalSelector.add(0, new FloatGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.add(3, new TemptGoal(this, 1.25D, Ingredient.ofItems(Items.SEAGRASS), false));
        this.goalSelector.add(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.add(7, new RandomLookAroundGoal(this));
        this.targetSelector.add(1, new ResetUniversalAngerTargetGoal<>(this, true));
        this.targetSelector.add(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, this::shouldAngerAt));
        this.targetSelector.add(3, (new HurtByTargetGoal(this, new Class[0])).setGroupRevenge());
    }

    public static AttributeSupplier.Builder createBrownFungusAttributes() {
        return Mob.createMobAttributes().add(Attributes.GENERIC_MAX_HEALTH, 10.0D).add(Attributes.GENERIC_MOVEMENT_SPEED, 0.2D).add(Attributes.GENERIC_ATTACK_DAMAGE, 1.0D)
                .add(Attributes.GENERIC_FOLLOW_RANGE, 32.0D);
    }

    public static boolean canSpawn(EntityType<BrownFungusEntity> type, LevelAccessor world, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return world.getBlockState(pos.down()).isIn(BlockTags.MOOSHROOMS_SPAWNABLE_ON) && world.getBaseLightLevel(pos, 0) > 8;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundInit.FUNGUS_IDLE_EVENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundInit.FUNGUS_HURT_EVENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundInit.FUNGUS_DEATH_EVENT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 1.0F);
    }

    @Override
    public void writeCustomDataToNbt(CompoundTag nbt) {
        super.writeCustomDataToNbt(nbt);
        this.writeAngerToNbt(nbt);
    }

    @Override
    public void readCustomDataFromNbt(CompoundTag nbt) {
        super.readCustomDataFromNbt(nbt);
        this.readAngerFromNbt(this.level(), nbt);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (!this.level().isClientSide()) {
            this.tickAngerLogic((ServerLevel) this.level(), true);
            int randomMushroom = this.level().getRandom().nextInt(20000);
            if (randomMushroom == 1997
                    && (this.level().getBlockState(this.getBlockPos().down()).isOf(Blocks.MYCELIUM) || this.level().getBlockState(this.getBlockPos().down()).isOf(Blocks.GRASS_BLOCK))
                    && this.level().getBlockState(this.getBlockPos()).isAir()) {
                this.level().setBlockState(this.getBlockPos(), Blocks.BROWN_MUSHROOM.defaultBlockState());
            }
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.getSource() != null && source.getSource() instanceof Player attacker) {
            this.setAngryAt(attacker.getUuid());
        }
        return !this.isInvulnerableTo(source) && super.damage(source, amount);

    }

    @Override
    public void chooseRandomAngerTime() {
        this.setAngerTime(ANGER_TIME_RANGE.get(this.random));
    }

    @Override
    public void setAngerTime(int ticks) {
        this.angerTime = ticks;
    }

    @Override
    public int getAngerTime() {
        return this.angerTime;
    }

    @Override
    public void setAngryAt(@Nullable UUID uuid) {
        this.angryAt = uuid;
    }

    @Override
    public UUID getAngryAt() {
        return this.angryAt;
    }

    @Override
    public int getLimitPerChunk() {
        return 4;
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (!super.tryAttack(target)) {
            return false;
        } else {
            if (!this.level().isClientSide() && target instanceof LivingEntity && this.level().getRandom().nextInt(160) > 100) {
                ((LivingEntity) target).addStatusEffect(new MobEffectInstance(MobEffects.POISON, 80 + this.level().getRandom().nextInt(100), 0, false, false), this);
            }
            return true;
        }
    }

    static {
        ANGER_TIME_RANGE = TimeUtil.betweenSeconds(20, 60);
    }

}

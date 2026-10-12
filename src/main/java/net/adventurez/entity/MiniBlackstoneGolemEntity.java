package net.adventurez.entity;

import net.adventurez.init.SoundInit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

public class MiniBlackstoneGolemEntity extends Monster {

    public MiniBlackstoneGolemEntity(EntityType<? extends MiniBlackstoneGolemEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new FloatGoal(this));
        this.goalSelector.add(1, new MeleeAttackGoal(this, 0.85D, true));
        this.goalSelector.add(5, new WaterAvoidingRandomStrollGoal(this, 0.85D));
        this.goalSelector.add(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.add(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.add(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createSmallStoneGolemAttributes() {
        return Monster.createHostileAttributes().add(Attributes.GENERIC_MAX_HEALTH, 50.0D).add(Attributes.GENERIC_MOVEMENT_SPEED, 0.27D)
                .add(Attributes.GENERIC_KNOCKBACK_RESISTANCE, 2.5D).add(Attributes.GENERIC_ATTACK_DAMAGE, 6.0D).add(Attributes.GENERIC_ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.GENERIC_FOLLOW_RANGE, 38.0D);
    }

    public static boolean canSpawn(EntityType<MiniBlackstoneGolemEntity> type, ServerLevelAccessor world, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return canSpawnInDark(type, world, spawnReason, pos, random) || spawnReason == EntitySpawnReason.SPAWNER;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundInit.SMALL_GOLEM_IDLE_EVENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundInit.SMALL_GOLEM_HIT_EVENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundInit.SMALL_GOLEM_DEATH_EVENT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundInit.SMALL_GOLEM_WALK_EVENT, 0.15F, 1.0F);
    }

    @Override
    public int getLimitPerChunk() {
        return 1;
    }

    @Override
    public boolean canSpawn(LevelReader view) {
        return super.canSpawn(view) && this.level().getBlockState(this.getBlockPos()).getBlock().canMobSpawnInside(this.level().getBlockState(this.getBlockPos()));
    }

}
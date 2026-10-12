package net.adventurez.entity;

import net.adventurez.init.EntityInit;
import net.adventurez.init.SoundInit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;

public class IguanaEntity extends Animal {

    public static final EntityDataAccessor<Boolean> OPEN_MOUTH;

    public IguanaEntity(EntityType<? extends IguanaEntity> entityType, Level world) {
        super(entityType, world);
    }

    public static AttributeSupplier.Builder createIguanaAttributes() {
        return Mob.createMobAttributes().add(Attributes.GENERIC_MAX_HEALTH, 10.0D).add(Attributes.GENERIC_MOVEMENT_SPEED, 0.22D);
    }

    public static boolean isValidNaturalSpawn(EntityType<? extends Animal> type, LevelAccessor world, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return world.getBlockState(pos.down()).isOf(Blocks.RED_SAND) && world.getBaseLightLevel(pos, 0) > 8;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new FloatGoal(this));
        this.goalSelector.add(1, new PanicGoal(this, 1.8D));
        this.goalSelector.add(2, new BreedGoal(this, 1.0D));
        this.goalSelector.add(3, new TemptGoal(this, 1.25D, Ingredient.ofItems(Items.DEAD_BUSH), false));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.add(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.add(6, new LookAtPlayerGoal(this, Player.class, 5.0F));
        this.goalSelector.add(7, new RandomLookAroundGoal(this));
        this.goalSelector.add(8, new EatDeadBushGoal(this, 1.0D));
    }

    @Override
    protected void initDataTracker(Builder builder) {
        super.initDataTracker(builder);
        builder.add(OPEN_MOUTH, false);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundInit.IGUANA_IDLE_EVENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundInit.IGUANA_HURT_EVENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundInit.IGUANA_DEATH_EVENT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundInit.IGUANA_STEP_EVENT, 0.35F, 1.0F);
    }

    @Override
    public IguanaEntity createChild(ServerLevel serverWorld, AgeableMob passiveEntity) {
        return EntityInit.IGUANA.create(serverWorld);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return net.adventurez.util.MinecraftCompat.isItem(stack, Items.DEAD_BUSH);
    }

    static {
        OPEN_MOUTH = SynchedEntityData.registerData(IguanaEntity.class, EntityDataSerializers.BOOLEAN);
    }

    private class EatDeadBushGoal extends MoveToBlockGoal {
        private final IguanaEntity iguanaEntity;

        public EatDeadBushGoal(IguanaEntity iguanaEntity, double speed) {
            super(iguanaEntity, speed, 12);
            this.iguanaEntity = iguanaEntity;
        }

        @Override
        public boolean canStart() {
            return !this.iguanaEntity.isBaby() && !this.iguanaEntity.isInLove() && super.canStart();
        }

        @Override
        public void start() {
            super.start();
            this.iguanaEntity.dataTracker.set(IguanaEntity.OPEN_MOUTH, true);
        }

        @Override
        public void stop() {
            super.stop();
            this.iguanaEntity.dataTracker.set(IguanaEntity.OPEN_MOUTH, false);
        }

        @Override
        public void tick() {
            if (!this.iguanaEntity.level().isClientSide() && this.hasReached()) {
                if (this.iguanaEntity.getHealth() < this.iguanaEntity.getMaxHealth())
                    this.iguanaEntity.heal(2f);

                this.iguanaEntity.level().breakBlock(targetPos, false);
                this.iguanaEntity.setLoveTicks(600);
            }
            super.tick();
        }

        @Override
        public double getDesiredDistanceToTarget() {
            return 2.1D;
        }

        @Override
        protected int getInterval(PathfinderMob mob) {
            return 60 + mob.getRandom().nextInt(600);
        }

        @Override
        protected boolean isTargetPos(LevelReader world, BlockPos pos) {
            if (net.adventurez.util.MinecraftCompat.isBlock(world.getBlockState(pos), Blocks.DEAD_BUSH))
                return true;
            else
                return false;
        }
    }
}

package net.adventurez.entity;

import net.adventurez.init.EntityInit;
import net.adventurez.init.ParticleInit;
import net.adventurez.init.SoundInit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public class SkunkEntity extends Animal {

    private int fartTick = 1200;

    public SkunkEntity(EntityType<? extends SkunkEntity> entityType, Level world) {
        super(entityType, world);
    }

    public static AttributeSupplier.Builder createSkunkAttributes() {
        return Mob.createMobAttributes().add(Attributes.GENERIC_MAX_HEALTH, 8.0D).add(Attributes.GENERIC_MOVEMENT_SPEED, 0.21D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new EscapeDangerGoal(this, 1.8D));
        this.goalSelector.add(2, new AnimalMateGoal(this, 1.0D));
        this.goalSelector.add(3, new TemptGoal(this, 1.20D, Ingredient.ofItems(Items.SHORT_GRASS, Items.FERN), true));
        this.goalSelector.add(4, new FollowParentGoal(this, 1.25D));
        this.goalSelector.add(5, new EscapePlayerGoal(this, 2.0D));
        this.goalSelector.add(6, new WanderAroundFarGoal(this, 1.0D));
        this.goalSelector.add(7, new LookAtEntityGoal(this, Player.class, 6.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.fartTick--;

            Vec3 velocity = this.getVelocity();
            Vec3 rotationVec = this.getRotationVector();

            if (this.fartTick <= 0) {
                for (int i = 0; i < 10; i++) {
                    this.level().addParticle(ParticleInit.FART_PARTICLE, this.getX() - rotationVec.x * 0.5, this.getEyeY() - 0.1F, this.getZ() - rotationVec.z * 0.5, -velocity.x, 0.0, -velocity.z);
                }
                this.level().playSoundFromEntity(this, SoundInit.SKUNK_FART_EVENT, SoundSource.NEUTRAL, 0.7f, this.getRandom().nextFloat() * 0.4f + 0.8f);
                this.fartTick = 800 + this.getRandom().nextInt(400);
            }
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundInit.SKUNK_IDLE_EVENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundInit.SKUNK_HURT_EVENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundInit.SKUNK_DEATH_EVENT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WOLF_STEP, 0.15F, 1.0F);
    }

    @Override
    public SkunkEntity createChild(ServerLevel serverWorld, AgeableMob passiveEntity) {
        return EntityInit.SKUNK.create(serverWorld);
    }

    @Override
    public boolean isBreedingItem(ItemStack stack) {
        return net.adventurez.util.MinecraftCompat.isItem(stack, Items.SHORT_GRASS) || net.adventurez.util.MinecraftCompat.isItem(stack, Items.FERN) || net.adventurez.util.MinecraftCompat.isItem(stack, Items.TALL_GRASS);
    }

    private static class EscapePlayerGoal extends Goal {
        protected final SkunkEntity skunkEntity;
        protected final double speed;
        protected double targetX;
        protected double targetY;
        protected double targetZ;

        public EscapePlayerGoal(SkunkEntity skunkEntity, double speed) {
            this.skunkEntity = skunkEntity;
            this.speed = speed;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (this.skunkEntity.level().getClosestPlayer(this.skunkEntity, 4.0D) == null) {
                return false;
            } else {
                return this.findTarget();
            }
        }

        private boolean findTarget() {
            Vec3 vec3d = DefaultRandomPos.find(this.skunkEntity, 12, 4);
            if (vec3d == null) {
                return false;
            } else {
                this.targetX = vec3d.x;
                this.targetY = vec3d.y;
                this.targetZ = vec3d.z;
                return true;
            }
        }

        @Override
        public void start() {
            this.skunkEntity.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
        }

        @Override
        public boolean shouldContinue() {
            return !this.skunkEntity.getNavigation().isIdle();
        }

    }

}

package net.adventurez.entity;

import org.jetbrains.annotations.Nullable;

import net.adventurez.init.EntityInit;
import net.adventurez.init.SoundInit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.equine.SkeletonHorse;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class NightmareEntity extends SkeletonHorse {
    private int eatingGrassTicks;
    private int damageWaterTicks;
    private static final Identifier WALKING_SPEED_INCREASE_ID;
    private static final AttributeModifier WALKING_SPEED_INCREASE;

    public NightmareEntity(EntityType<? extends SkeletonHorse> entityType, Level world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathType.LAVA, 8.0F);
        this.experiencePoints = 5;
    }

    public static AttributeSupplier.Builder createNightmareAttributes() {
        return createBaseHorseAttributes().add(Attributes.MAX_HEALTH, 40.0D).add(Attributes.MOVEMENT_SPEED, 0.215D).add(Attributes.ATTACK_DAMAGE, 7.0D);
    }

    @Override
    public void tickMovement() {
        if (!this.level().isClientSide() && this.isAlive()) {
            if (this.random.nextInt(700) == 0 && this.deathTime == 0) {
                this.heal(1.0F);
            }

            if (this.eatsGrass()) {
                if (!this.isEatingGrass() && !this.hasPassengers() && this.random.nextInt(500) == 0 && this.level().getBlockState(this.getBlockPos().below()).isOf(Blocks.SOUL_SAND)) {
                    this.setEatingGrass(true);
                }

                if (this.isEatingGrass() && ++this.eatingGrassTicks > 50) {
                    this.eatingGrassTicks = 0;
                    this.setEatingGrass(false);
                }
            }

            this.walkToParent();
        }
        if (this.level().isClientSide()) {
            double g = this.level().getRandom().nextDouble() * 0.75F - 0.375F;
            double e = this.level().getRandom().nextDouble() * 0.9F;
            double f = this.level().getRandom().nextDouble() * 0.75F - 0.375F;
            if (!this.isBaby()) {
                double g2 = this.level().getRandom().nextDouble() * 0.75F - 0.375F;
                double e2 = this.level().getRandom().nextDouble() * 0.9F;
                double f2 = this.level().getRandom().nextDouble() * 0.75F - 0.375F;
                this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX() + g, this.getY() + 0.5D + e, this.getZ() + f, 0D, 0D, 0D);
                this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX() + g2, this.getY() + 0.5D + e2, this.getZ() + f2, 0D, 0D, 0D);
            } else {
                this.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, this.getX() + g, this.getY() + 0.5D + e, this.getZ() + f, 0D, 0D, 0D);
            }
        }
        if (this.touchingWater && !this.level().isClientSide()) {
            damageWaterTicks++;
            if (damageWaterTicks == 40) {
                this.damage(this.getDamageSources().freeze(), 1F);
                damageWaterTicks = 0;
            }
        }

        if (this.isOnSoulSpeedBlock() && !this.getAttributes().hasModifierForAttribute(Attributes.MOVEMENT_SPEED, WALKING_SPEED_INCREASE_ID)) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).addTemporaryModifier(WALKING_SPEED_INCREASE);
        } else if (!this.isOnSoulSpeedBlock() && this.getAttributes().hasModifierForAttribute(Attributes.MOVEMENT_SPEED, WALKING_SPEED_INCREASE_ID)) {
            this.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(WALKING_SPEED_INCREASE_ID);
        }
        super.tickMovement();
    }

    private boolean isOnSoulSpeedBlock() {
        return this.level().getBlockState(this.getVelocityAffectingPos()).isIn(BlockTags.SOUL_SPEED_BLOCKS);
    }

    @Override
    public void removeAllPassengers() {
        if (!this.isAlive() || !this.hasPassengers() || !this.getPassengerList().get(0).isAlive() || !(this.getPassengerList().get(0) instanceof SoulReaperEntity)) {
            super.removeAllPassengers();
        }
    }

    @Override
    public void walkToParent() {
        if (this.isBred() && this.isBaby() && !this.isEatingGrass()) {
            LivingEntity livingEntity = this.level().getClosestPlayer(this, 16D);
            if (livingEntity != null && this.squaredDistanceTo(livingEntity) > 4.0D) {
                this.navigation.findPathTo((Entity) livingEntity, 0);
            }
        }

    }

    @Override
    public InteractionResult interactMob(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (this.hasPassengers() || this.isBaby()) {
            return super.interactMob(player, hand);
        }

        if (!itemStack.isEmpty()) {
            if (net.adventurez.util.MinecraftCompat.isItem(itemStack, Items.WITHER_ROSE)) {
                return this.interactHorse(player, itemStack);
            }
            InteractionResult actionResult = itemStack.useOnEntity(player, this, hand);
            if (actionResult.isAccepted()) {
                return actionResult;
            }
            if (!this.isTame()) {
                this.level().playSoundFromEntity(null, this, SoundInit.NIGHTMARE_ANGRY_EVENT, SoundSource.HOSTILE, 1F, 1F);
                return InteractionResult.success(this.level().isClientSide());
            }
        }

        this.putPlayerOnBack(player);
        return InteractionResult.success(this.level().isClientSide());
    }

    @Override
    public SoundEvent getAngrySound() {
        return SoundInit.NIGHTMARE_ANGRY_EVENT;
    }

    @Override
    public SoundEvent getAmbientSound() {
        return SoundInit.NIGHTMARE_IDLE_EVENT;
    }

    @Override
    public SoundEvent getDeathSound() {
        return SoundInit.NIGHTMARE_DEATH_EVENT;
    }

    @Override
    public SoundEvent getHurtSound(DamageSource source) {
        return SoundInit.NIGHTMARE_HURT_EVENT;
    }

    @Override
    public boolean canHaveStatusEffect(MobEffectInstance effect) {
        return effect.getEffectType() == MobEffects.WITHER ? false : super.canHaveStatusEffect(effect);
    }

    @Override
    @Nullable
    public AgeableMob createChild(ServerLevel serverWorld, AgeableMob passiveEntity) {
        return EntityInit.NIGHTMARE.create(serverWorld);
    }

    static {
        WALKING_SPEED_INCREASE_ID = Identifier.of("adventurez:walking_speed");
        WALKING_SPEED_INCREASE = new AttributeModifier(WALKING_SPEED_INCREASE_ID, 0.5D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

}

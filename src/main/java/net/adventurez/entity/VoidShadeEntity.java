package net.adventurez.entity;

import java.util.EnumSet;

import net.adventurez.entity.nonliving.ThrownRockEntity;
import net.adventurez.entity.nonliving.VoidBulletEntity;
import net.adventurez.init.SoundInit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;

public class VoidShadeEntity extends FlyingMob implements Enemy {

    public VoidShadeEntity(EntityType<? extends FlyingMob> entityType, Level world) {
        super(entityType, world);
        this.moveControl = new VoidShadeEntity.VoidShadeMoveControl(this);
        this.experiencePoints = 0;
    }

    public static AttributeSupplier.Builder createVoidShadeAttributes() {
        return Monster.createHostileAttributes().add(Attributes.GENERIC_MAX_HEALTH, 20.0D).add(Attributes.GENERIC_MOVEMENT_SPEED, 0.15D)
                .add(Attributes.GENERIC_ATTACK_DAMAGE, 7.0D).add(Attributes.GENERIC_ATTACK_KNOCKBACK, 0.5D).add(Attributes.GENERIC_KNOCKBACK_RESISTANCE, 10.0D)
                .add(Attributes.GENERIC_FOLLOW_RANGE, 80.0D);
    }

    @Override
    public void initGoals() {
        super.initGoals();
        this.goalSelector.add(3, new VoidShadeEntity.LookAtTargetGoal(this));
        this.goalSelector.add(7, new RandomLookAroundGoal(this));
        this.goalSelector.add(3, new VoidShadeEntity.MoveGoal(this));
        this.goalSelector.add(5, new VoidShadeEntity.ShootBulletGoal(this));
        this.targetSelector.add(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source) || source.getSource() instanceof ThrownRockEntity) {
            return false;
        } else {
            return super.damage(source, amount);
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

    static class ShootBulletGoal extends Goal {
        private final VoidShadeEntity voidShadeEntity;
        public int cooldown;

        public ShootBulletGoal(VoidShadeEntity voidShadeEntity) {
            this.voidShadeEntity = voidShadeEntity;
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = this.voidShadeEntity.getTarget();
            return livingEntity != null && this.voidShadeEntity.canSee(livingEntity);
        }

        @Override
        public void start() {
            this.cooldown = 0;
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.voidShadeEntity.getTarget();
            if (livingEntity.distanceTo(this.voidShadeEntity) < 12.0D) {
                Level world = this.voidShadeEntity.level();
                ++this.cooldown;
                if (this.cooldown == 20) {
                    ((ServerLevel) world).playSoundFromEntity(null, this.voidShadeEntity, SoundInit.SHADOW_CAST_EVENT, SoundSource.HOSTILE, 1.0F, 1.0F);
                    Vec3 vec3d = this.voidShadeEntity.getRotationVec(1.0F);
                    vec3d.add(world.random.nextFloat() * 0.5F - 0.25F, 0, world.random.nextFloat() * 0.5F - 0.25F);
                    VoidBulletEntity voidBulletEntity = new VoidBulletEntity(this.voidShadeEntity, vec3d, world);
                    world.spawnEntity(voidBulletEntity);

                    this.cooldown = -40;
                }
                if (livingEntity.distanceTo(this.voidShadeEntity) < 1.0D) {
                    this.voidShadeEntity.tryAttack(livingEntity);
                }
            } else if (this.cooldown > 0) {
                --this.cooldown;
            }
        }
    }

    static class LookAtTargetGoal extends Goal {
        private final VoidShadeEntity voidShadeEntity;

        public LookAtTargetGoal(VoidShadeEntity voidShadeEntity) {
            this.voidShadeEntity = voidShadeEntity;
            this.setControls(EnumSet.of(Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return true;
        }

        @Override
        public void tick() {
            if (this.voidShadeEntity.getTarget() == null) {
                Vec3 vec3d = this.voidShadeEntity.getVelocity();
                this.voidShadeEntity.setYaw(-((float) Mth.atan2(vec3d.x, vec3d.z)) * 57.295776F);
                this.voidShadeEntity.bodyYaw = this.voidShadeEntity.getYaw();
            } else {
                LivingEntity livingEntity = this.voidShadeEntity.getTarget();
                if (livingEntity.squaredDistanceTo(this.voidShadeEntity) < 4096.0D) {
                    double e = livingEntity.getX() - this.voidShadeEntity.getX();
                    double f = livingEntity.getZ() - this.voidShadeEntity.getZ();
                    this.voidShadeEntity.setYaw(-((float) Mth.atan2(e, f)) * 57.295776F);
                    this.voidShadeEntity.bodyYaw = this.voidShadeEntity.getYaw();
                }
            }

        }
    }

    static class MoveGoal extends Goal {
        private final VoidShadeEntity voidShadeEntity;

        public MoveGoal(VoidShadeEntity voidShadeEntity) {
            this.voidShadeEntity = voidShadeEntity;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            if (this.voidShadeEntity.getTarget() != null) {
                return true;
            } else
                return false;
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }

        @Override
        public void start() {
            if (this.voidShadeEntity.getTarget() != null) {
                LivingEntity livingEntity = this.voidShadeEntity.getTarget();
                this.voidShadeEntity.getMoveControl().moveTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 1.0D);

            }
        }
    }

    static class VoidShadeMoveControl extends MoveControl {
        private final VoidShadeEntity voidShadeEntity;

        public VoidShadeMoveControl(VoidShadeEntity voidShadeEntity) {
            super(voidShadeEntity);
            this.voidShadeEntity = voidShadeEntity;
        }

        @Override
        public void tick() {
            if (this.state == MoveControl.State.MOVE_TO) {
                Vec3 vec3d = new Vec3(this.targetX - this.voidShadeEntity.getX(), this.targetY - this.voidShadeEntity.getY(), this.targetZ - this.voidShadeEntity.getZ());
                vec3d = vec3d.normalize();
                this.voidShadeEntity.setVelocity(this.voidShadeEntity.getVelocity().add(vec3d.multiply(0.012D)));
                if (this.voidShadeEntity.getTarget() == null) {
                    this.state = MoveControl.State.WAIT;
                }

            }
        }
    }

}

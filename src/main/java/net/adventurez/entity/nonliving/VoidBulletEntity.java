package net.adventurez.entity.nonliving;

import net.adventurez.entity.VoidShadeEntity;
import net.adventurez.init.EntityInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class VoidBulletEntity extends AbstractHurtingProjectile {

    private int removeTicker;

    public VoidBulletEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Environment(EnvType.CLIENT)
    public VoidBulletEntity(Level world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this(EntityInit.VOID_BULLET, world);
        this.refreshPositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
        this.setVelocity(velocityX, velocityY, velocityZ);
    }

    public VoidBulletEntity(LivingEntity owner, Vec3 velocity, Level world) {
        super(EntityInit.VOID_BULLET, owner, velocity, world);
        this.refreshPositionAndAngles(owner.getX() + velocity.getX() * 1.2D, owner.getY() + owner.getBoundingBox().getLengthY() * 0.6D + velocity.getY() * 1.2D, owner.getZ() + velocity.getZ() * 1.2D,
                -owner.getYaw(), owner.getPitch());
        this.prevPitch = owner.getPitch();
        this.setYaw(-owner.getYaw());
        this.accelerationPower = 0.32D;
    }

    @Override
    public SoundSource getSoundCategory() {
        return SoundSource.HOSTILE;
    }

    @Override
    protected ParticleOptions getParticleType() {
        return new BlockParticleOption(ParticleTypes.BLOCK, Blocks.AIR.defaultBlockState());
    }

    @Override
    protected float getDrag() {
        return 0.7F;
    }

    @Override
    public void tick() {
        super.tick();
        this.removeTicker++;
        if (this.level().isClientSide()) {
            if (this.removeTicker > 75)
                for (int i = 0; i < 20; i++) {
                    double d = (double) this.position().getX() + 0.3F * this.level().getRandom().nextFloat();
                    double e = (double) ((float) this.position().getY() + this.level().getRandom().nextFloat() * 0.3F);
                    double f = (double) this.position().getZ() + 0.3F * this.level().getRandom().nextFloat();
                    double g = (double) (this.level().getRandom().nextFloat() * 0.2D);
                    double h = (double) this.level().getRandom().nextFloat() * 0.2D;
                    double l = (double) (this.level().getRandom().nextFloat() * 0.2D);
                    this.level().addParticle(ParticleTypes.SMOKE, d, e, f, g, h, l);
                }
        } else if (this.removeTicker >= 80) {
            this.discard();
        }
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Environment(EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        return distance < 16384.0D;
    }

    @Override
    public float getBrightnessAtEyes() {
        return 1.0F;
    }

    @Override
    public void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity hittedEntity = entityHitResult.getEntity();

        if (!this.level().isClientSide() && !(hittedEntity instanceof VoidBulletEntity) && !(hittedEntity instanceof VoidShadeEntity)) {
            this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F);
            hittedEntity.damage(createDamageSource(this), 7.0F);
            this.discard();
        }

    }

    @Override
    public void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.level().isClientSide()) {
            for (int i = 0; i < 20; i++) {
                double d = (double) this.position().getX() + 0.3F * this.level().getRandom().nextFloat();
                double e = (double) ((float) this.position().getY() + this.level().getRandom().nextFloat() * 0.3F);
                double f = (double) this.position().getZ() + 0.3F * this.level().getRandom().nextFloat();
                double g = (double) (this.level().getRandom().nextFloat() * 0.1D);
                double h = (double) this.level().getRandom().nextFloat() * 0.1D;
                double l = (double) (this.level().getRandom().nextFloat() * 0.1D);
                ((ServerLevel) this.level()).spawnParticles(ParticleTypes.SMOKE, d, e, f, 3, g, h, l, 0.1D);
            }
            this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F);
            this.discard();
        }
    }

    private DamageSource createDamageSource(Entity entity) {
        return entity.getDamageSources().create(EntityInit.VOID_BULLET_KEY, entity);
    }

}

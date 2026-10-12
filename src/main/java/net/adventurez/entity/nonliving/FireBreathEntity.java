package net.adventurez.entity.nonliving;

import net.adventurez.init.EntityInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;

public class FireBreathEntity extends AbstractHurtingProjectile {

    private int removeTicker;

    public FireBreathEntity(EntityType<? extends AbstractHurtingProjectile> entityType, Level world) {
        super(entityType, world);
    }

    @Environment(EnvType.CLIENT)
    public FireBreathEntity(Level world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this(EntityInit.FIRE_BREATH, world);
        this.refreshPositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
        this.setVelocity(velocityX, velocityY, velocityZ);
    }

    public FireBreathEntity(Level world, LivingEntity owner, Vec3 velocity) {
        super(EntityInit.FIRE_BREATH, owner, velocity, world);
        Vec3 newVec3d = this.getVelocity().normalize().add(this.random.nextGaussian() * 0.1D, -this.random.nextDouble() * 0.1D, this.random.nextGaussian() * 0.1D);
        this.setVelocity(newVec3d);
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
        return 0.9F;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            for (int i = 0; i < 10; i++) {
                double d = (double) this.level().getRandom().nextGaussian() * 0.01D;
                double e = (double) this.level().getRandom().nextGaussian() * 0.01D;
                double f = (double) this.level().getRandom().nextGaussian() * 0.01D;
                this.level().addParticle(ParticleTypes.FLAME, this.getParticleX(1.0D), this.getRandomBodyY(), this.getParticleZ(1.0D), d, e, f);
            }
        } else {
            removeTicker++;
            if (removeTicker >= 80)
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
        Entity entity = this.getOwner();
        Entity hittedEntity = entityHitResult.getEntity();
        if (!this.level().isClientSide() && entity != null && hittedEntity instanceof LivingEntity) {
            hittedEntity.setOnFireFor(8);
            hittedEntity.damage(createDamageSource(this), 3.0F);
            this.discard();
        }

    }

    private DamageSource createDamageSource(Entity entity) {
        return entity.getDamageSources().create(EntityInit.FIRE_BREATH_KEY, entity);
    }

    @Override
    public void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        if (!this.level().isClientSide()) {
            Entity entity = this.getOwner();
            if (entity == null || !(entity instanceof Mob) || this.level().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                BlockPos blockPos = blockHitResult.getBlockPos().offset(blockHitResult.getSide());
                if (this.level().isAir(blockPos)) {
                    this.level().setBlockState(blockPos, BaseFireBlock.getState(this.level(), blockPos));
                }
            }
            this.discard();
        }
    }

}

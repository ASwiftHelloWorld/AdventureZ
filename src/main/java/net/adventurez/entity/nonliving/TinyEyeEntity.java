package net.adventurez.entity.nonliving;

import java.util.UUID;

import net.adventurez.init.EntityInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.hurtingprojectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

public class TinyEyeEntity extends AbstractHurtingProjectile {

    private Entity target;
    @Nullable
    private Direction direction;
    private int stepCount;
    private double targetX;
    private double targetY;
    private double targetZ;
    @Nullable
    private UUID targetUuid;

    public TinyEyeEntity(EntityType<? extends TinyEyeEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Environment(EnvType.CLIENT)
    public TinyEyeEntity(Level world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        this(EntityInit.TINY_EYE, world);
        this.refreshPositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
        this.setVelocity(velocityX, velocityY, velocityZ);
    }

    public TinyEyeEntity(Level world, LivingEntity owner, Entity target, Direction.Axis axis) {
        this(EntityInit.TINY_EYE, world);
        this.setOwner(owner);
        BlockPos blockPos = owner.getBlockPos();
        double d = (double) blockPos.getX() + 0.5D;
        double e = (double) blockPos.getY() + 0.5D;
        double f = (double) blockPos.getZ() + 0.5D;
        this.refreshPositionAndAngles(d, e, f, this.getYaw(), this.getPitch());
        this.target = target;
        this.direction = Direction.UP;
        this.movingAround();
    }

    @Override
    public SoundSource getSoundCategory() {
        return SoundSource.HOSTILE;
    }

    @Override
    public void writeCustomDataToNbt(CompoundTag tag) {
        super.writeCustomDataToNbt(tag);
        if (this.target != null)
            tag.putUuid("Target", this.target.getUuid());
        if (this.direction != null)
            tag.putInt("Dir", this.direction.getId());

        tag.putInt("Steps", this.stepCount);
        tag.putDouble("TXD", this.targetX);
        tag.putDouble("TYD", this.targetY);
        tag.putDouble("TZD", this.targetZ);
    }

    @Override
    public void readCustomDataFromNbt(CompoundTag tag) {
        super.readCustomDataFromNbt(tag);
        this.stepCount = tag.getInt("Steps");
        this.targetX = tag.getDouble("TXD");
        this.targetY = tag.getDouble("TYD");
        this.targetZ = tag.getDouble("TZD");
        if (tag.contains("Dir", 99))
            this.direction = Direction.byId(tag.getInt("Dir"));

        if (tag.containsUuid("Target"))
            this.targetUuid = tag.getUuid("Target");

    }

    @Override
    protected void initDataTracker(Builder builder) {
    }

    private void movingAround() {
        double d = 0.5D;
        BlockPos blockPos;
        if (this.target == null)
            blockPos = this.getBlockPos().down();
        else {
            d = (double) this.target.getHeight() * 0.5D;
            blockPos = BlockPos.ofFloored(this.target.getX(), this.target.getY() + d, this.target.getZ());
        }

        double e = (double) blockPos.getX() + 0.5D;
        double f = (double) blockPos.getY() + d;
        double g = (double) blockPos.getZ() + 0.5D;
        double h = e - this.getX();
        double j = f - this.getY();
        double k = g - this.getZ();
        double l = (double) Mth.sqrt((float) (h * h + j * j + k * k));
        if (l == 0.0D) {
            this.targetX = 0.0D;
            this.targetY = 0.0D;
            this.targetZ = 0.0D;
        } else {
            this.targetX = h / l * 0.15D;
            this.targetY = j / l * 0.15D;
            this.targetZ = k / l * 0.15D;
        }

        this.velocityDirty = true;
        this.stepCount = 10 + this.random.nextInt(5) * 10;
    }

    @Override
    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL)
            this.discard();
    }

    @Override
    public void tick() {
        Vec3 vec3d;
        if (!this.level().isClientSide()) {
            if (this.target == null && this.targetUuid != null) {
                this.target = ((ServerLevel) this.level()).getEntity(this.targetUuid);
                if (this.target == null) {
                    this.targetUuid = null;
                }
            }

            if (this.target == null || !this.target.isAlive() || this.target instanceof Player && ((Player) this.target).isSpectator()) {
                if (!this.hasNoGravity()) {
                    this.setVelocity(this.getVelocity().add(0.0D, -0.04D, 0.0D));
                }
            } else {
                this.targetX = Mth.clamp(this.targetX * 1.025D, -1.0D, 1.0D);
                this.targetY = Mth.clamp(this.targetY * 1.025D, -1.0D, 1.0D);
                this.targetZ = Mth.clamp(this.targetZ * 1.025D, -1.0D, 1.0D);
                vec3d = this.getVelocity();
                this.setVelocity(vec3d.add((this.targetX - vec3d.x) * 0.2D, (this.targetY - vec3d.y) * 0.2D, (this.targetZ - vec3d.z) * 0.2D));
            }

            HitResult hitResult = ProjectileUtil.getCollision(this, this::canHit);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onCollision(hitResult);
            }
        }

        this.checkBlockCollision();
        vec3d = this.getVelocity();
        this.updatePosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
        ProjectileUtil.setRotationFromVelocity(this, 0.5F);
        if (this.level().isClientSide()) {
        } else if (this.target != null && !this.target.isRemoved()) {
            if (this.stepCount > 0) {
                --this.stepCount;
                if (this.stepCount == 0) {
                    this.movingAround();
                }
            }

            if (this.direction != null) {
                BlockPos blockPos = this.getBlockPos();
                Direction.Axis axis = this.direction.getAxis();
                if (this.level().isTopSolid(blockPos.offset(this.direction), this)) {
                    this.movingAround();
                } else {
                    BlockPos blockPos2 = this.target.getBlockPos();
                    if (axis == Direction.Axis.X && blockPos.getX() == blockPos2.getX() || axis == Direction.Axis.Z && blockPos.getZ() == blockPos2.getZ()
                            || axis == Direction.Axis.Y && blockPos.getY() == blockPos2.getY()) {
                        this.movingAround();
                    }
                }
            }
        }

    }

    @Override
    public boolean canHit(Entity entity) {
        return super.canHit(entity) && !entity.noClip;
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
        if (!this.level().isClientSide() && entity != null && hittedEntity != entity && !(hittedEntity instanceof TinyEyeEntity) || hittedEntity instanceof Arrow) {
            this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F);
            if (hittedEntity instanceof LivingEntity) {
                this.teleportEntityRandom((LivingEntity) hittedEntity);
                hittedEntity.damage(createDamageSource(this), 3.0F);
            }
            this.discard();
        }

    }

    private DamageSource createDamageSource(Entity entity) {
        return entity.getDamageSources().create(EntityInit.TINY_EYE_KEY, entity);
    }

    @SuppressWarnings("deprecation")
    private void teleportEntityRandom(LivingEntity livingEntity) {
        if (!livingEntity.level().isClientSide() && livingEntity.level() instanceof ServerLevel serverWorld) {
            for (int counter = 0; counter < 100; counter++) {
                float randomFloat = this.level().getRandom().nextFloat() * 6.2831855F;
                int posX = livingEntity.getBlockPos().getX() + Mth.floor(Mth.cos(randomFloat) * 9.0F + serverWorld.getRandom().nextInt(30));
                int posZ = livingEntity.getBlockPos().getZ() + Mth.floor(Mth.sin(randomFloat) * 9.0F + serverWorld.getRandom().nextInt(30));
                int posY = serverWorld.getTopY(Heightmap.Type.WORLD_SURFACE, posX, posZ);
                BlockPos teleportPos = new BlockPos(posX, posY, posZ);
                if (serverWorld.isRegionLoaded(teleportPos.getX() - 4, teleportPos.getY() - 4, teleportPos.getZ() - 4, teleportPos.getX() + 4, teleportPos.getY() + 4, teleportPos.getZ() + 4)
                        && SpawnPlacements.canSpawn(EntityType.PLAYER, serverWorld, EntitySpawnReason.EVENT, teleportPos, serverWorld.getRandom())) {
                    if (!this.level().isClientSide()) {
                        livingEntity.teleport(teleportPos.getX(), teleportPos.getY(), teleportPos.getZ(), false);
                    }

                    serverWorld.playSound(null, posX, posY, posZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0f, 1.0f, serverWorld.getRandom().nextLong());
                    break;
                }
            }
        }
    }

    @Override
    public void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.playSound(SoundEvents.ENTITY_ENDER_EYE_DEATH, 1.0F, 1.0F);
        if (!this.level().isClientSide())
            this.discard();
    }

    @Override
    public void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
    }

}

package net.adventurez.entity;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import net.adventurez.entity.nonliving.TinyEyeEntity;
import net.adventurez.init.EffectInit;
import net.adventurez.init.EntityInit;
import net.adventurez.init.ItemInit;
import net.adventurez.init.SoundInit;
import net.adventurez.init.TagInit;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.BossEvent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class TheEyeEntity extends FlyingMob implements Enemy {
    private static final EntityDataAccessor<Integer> BEAM_TARGET_ID;
    public static final EntityDataAccessor<Integer> INVUL_TIMER;
    private int field_7082;
    @Nullable
    private final ServerBossEvent bossBar;
    private LivingEntity cachedBeamTarget;
    private boolean gotDamage;
    private int attackTpCounter;
    private int deathTimer;
    private int duplicationTimer = 0;

    private final boolean isVoidZLoaded = FabricLoader.getInstance().isModLoaded("voidz");

    public TheEyeEntity(EntityType<? extends TheEyeEntity> entityType, Level world) {
        super(entityType, world);
        if (this.duplicationTimer <= 0) {
            this.bossBar = (ServerBossEvent) (new ServerBossEvent(this.getDisplayName(), BossEvent.Color.PURPLE, BossEvent.Style.PROGRESS));
            this.experiencePoints = 80;
        } else {
            this.bossBar = null;
            this.experiencePoints = 0;
        }
        this.moveControl = new TheEyeEntity.EyeMoveControl(this);
    }

    public static AttributeSupplier.Builder createTheEntityAttributes() {
        return Monster.createHostileAttributes().add(Attributes.GENERIC_MAX_HEALTH, 800.0D).add(Attributes.GENERIC_MOVEMENT_SPEED, 0.35D)
                .add(Attributes.GENERIC_FOLLOW_RANGE, 60.0D).add(Attributes.GENERIC_ARMOR, 5.0D);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new ShootBulletGoal(this));
        this.goalSelector.add(1, new FireBeamGoal(this));
        this.goalSelector.add(2, new DuplicateAttack(this));
        this.goalSelector.add(3, new FlyRandomlyGoal(this));
        this.goalSelector.add(2, new LookAtTargetGoal(this));
        this.goalSelector.add(8, new RandomLookAroundGoal(this));
        this.targetSelector.add(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void initDataTracker(Builder builder) {
        super.initDataTracker(builder);
        builder.add(INVUL_TIMER, 0);
        builder.add(BEAM_TARGET_ID, 0);
    }

    @Override
    public void writeCustomDataToNbt(CompoundTag tag) {
        super.writeCustomDataToNbt(tag);
        tag.putInt("Invul", this.getInvulnerableTimer());
        tag.putInt("DeathTimer", this.deathTimer);
        tag.putInt("DuplicationTimer", this.duplicationTimer);
    }

    @Override
    public void readCustomDataFromNbt(CompoundTag tag) {
        super.readCustomDataFromNbt(tag);
        this.setInvulTimer(tag.getInt("Invul"));
        if (this.hasCustomName() && this.bossBar != null) {
            this.bossBar.setName(this.getDisplayName());
        }
        this.deathTimer = tag.getInt("DeathTimer");
        this.duplicationTimer = tag.getInt("DuplicationTimer");
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        if (this.bossBar != null)
            this.bossBar.setName(this.getDisplayName());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundInit.EYE_IDLE_EVENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundInit.EYE_HURT_EVENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundInit.EYE_DEATH_EVENT;
    }

    @Override
    public void tickMovement() {
        Vec3 vec3d = this.getVelocity().multiply(0.8D, 0.5D, 0.8D);
        if (!this.level().isClientSide() && this.getTarget() != null) {
            Entity entity = this.getTarget();
            double d = Math.max(0.0D, vec3d.y);

            if (this.getY() < entity.getY() + 12.0D) {
                d += 0.3D - d * 0.6D;
            } else if (this.getY() > entity.getY() + 26.0D)
                d -= 0.3D - d * 0.6D;

            vec3d = new Vec3(vec3d.x, d, vec3d.z);
            Vec3 vec3d2 = new Vec3(entity.getX() - this.getX(), 0.0D, entity.getZ() - this.getZ());
            if (squaredDistanceTo(vec3d2) > 16.0D) {
                Vec3 vec3d3 = vec3d2.normalize();
                vec3d = vec3d.add(vec3d3.x * 0.05D - vec3d.x * 0.05D, 0.0D, vec3d3.z * 0.05D - vec3d.z * 0.05D);
            }
        }
        this.setVelocity(vec3d);
        super.tickMovement();
    }

    @Override
    public void mobTick() {
        if (!this.hasNoGravity()) {
            this.setNoGravity(true);
        }
        int j;
        if (this.getInvulnerableTimer() > 0) {
            j = this.getInvulnerableTimer() - 1;
            if (j <= 0) {
                this.level().createExplosion(this, this.getX(), this.getEyeY(), this.getZ(), 7.0F, false, Level.ExplosionSourceType.MOB);
                if (!this.isSilent()) {
                    this.level().syncGlobalEvent(1023, this.getBlockPos(), 0);
                }
            }
            this.getNavigation().stop();
            this.setTarget(null);
            this.setInvulTimer(j);
        } else {
            super.mobTick();
            LivingEntity livingEntity = this.getTarget();
            if (livingEntity != null && livingEntity.isAlive()) {
                if (attackTpCounter >= 120 && !this.hasBeamTarget()) {
                    for (int counter = 0; counter < 100; counter++) {
                        float randomFloat = this.level().getRandom().nextFloat() * 6.2831855F;
                        int posX = livingEntity.getBlockPos().getX() + Mth.floor(Mth.cos(randomFloat) * 9.0F + livingEntity.level().getRandom().nextInt(12));
                        int posZ = livingEntity.getBlockPos().getZ() + Mth.floor(Mth.sin(randomFloat) * 9.0F + livingEntity.level().getRandom().nextInt(12));
                        int posY = livingEntity.level().getTopY(Heightmap.Type.WORLD_SURFACE, posX, posZ) + 10 + livingEntity.level().getRandom().nextInt(12);
                        BlockPos teleportPos = new BlockPos(posX, posY, posZ);
                        if (livingEntity.level().isRegionLoaded(teleportPos.getX() - 4, teleportPos.getY() - 4, teleportPos.getZ() - 4, teleportPos.getX() + 4, teleportPos.getY() + 4,
                                teleportPos.getZ() + 4)) {
                            this.lookControl.lookAt(teleportPos.getX(), teleportPos.getY(), teleportPos.getZ());
                            if (!this.level().isClientSide()) {
                                livingEntity.teleport(teleportPos.getX(), teleportPos.getY(), teleportPos.getZ(), false);
                            }
                            livingEntity.level().playSound(null, teleportPos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 1.0F);
                            if (this.level().isClientSide()) {
                                this.despawnParticlesServer(livingEntity);
                            }

                            attackTpCounter = -100;
                            break;
                        }
                    }
                } else {
                    attackTpCounter++;
                }
            }

            int n;
            if (this.field_7082 > 0) {
                --this.field_7082;
                if (this.field_7082 == 0 && this.level().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                    j = Mth.floor(this.getY());
                    n = Mth.floor(this.getX());
                    int o = Mth.floor(this.getZ());
                    boolean bl = false;

                    for (int p = -1; p <= 1; ++p) {
                        for (int q = -1; q <= 1; ++q) {
                            for (int r = 0; r <= 3; ++r) {
                                int s = n + p;
                                int t = j + r;
                                int u = o + q;
                                BlockPos blockPos = new BlockPos(s, t, u);
                                BlockState blockState = this.level().getBlockState(blockPos);
                                if (canDestroy(blockState)) {
                                    bl = this.level().breakBlock(blockPos, true, this) || bl;
                                }
                            }
                        }
                    }

                    if (bl) {
                        this.level().syncWorldEvent((Player) null, 1022, this.getBlockPos(), 0);
                    }
                }
            }

            if (this.age % 20 == 0) {
                this.heal(1.0F);
            }
            if (this.bossBar != null)
                this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide() && this.duplicationTimer > 0) {
            this.duplicationTimer--;
            if (this.duplicationTimer == 20) {
                for (int i = 0; i < 50; i++) {
                    double d = (double) this.getX() - 1.5F + this.level().getRandom().nextFloat() * 3.0F;
                    double e = (double) ((float) this.getRandomBodyY() + this.level().getRandom().nextFloat() * 0.1F);
                    double f = (double) this.getZ() - 1.5F + this.level().getRandom().nextFloat() * 3.0F;
                    double g = (double) (this.level().getRandom().nextFloat() * 0.2D);
                    double h = (double) this.level().getRandom().nextFloat() * 0.1D;
                    double l = (double) (this.level().getRandom().nextFloat() * 0.2D);
                    ((ServerLevel) this.level()).spawnParticles(ParticleTypes.PORTAL, d, e, f, 4, g, h, l, 1.0D);
                }
                this.setAiDisabled(true);
            }
            if (this.duplicationTimer == 1) {
                this.discard();
            }
        }
    }

    private boolean canDestroy(BlockState block) {
        return !block.isAir() && !net.adventurez.util.MinecraftCompat.isBlockInTag(block, TagInit.UNBREAKABLE_BLOCKS);
    }

    public void setEyeInvulnerabletime() {
        this.setInvulTimer(220);
    }

    @Override
    public void onStartedTrackingBy(ServerPlayer player) {
        super.onStartedTrackingBy(player);
        if (this.duplicationTimer <= 0)
            this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayer player) {
        super.onStoppedTrackingBy(player);
        if (this.bossBar != null)
            this.bossBar.removePlayer(player);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return super.damage(source, amount);
        } else if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!source.is(DamageTypeTags.IS_DROWNING) && !(source.getAttacker() instanceof TheEyeEntity)) {
            if (this.getInvulnerableTimer() > 0 && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                return false;
            } else {
                Entity entity2;
                entity2 = source.getAttacker();
                if (!(entity2 instanceof Player) && entity2 instanceof LivingEntity livingEntity && net.adventurez.util.MinecraftCompat.isEntityTypeInTag(livingEntity.getType(), EntityTypeTags.SENSITIVE_TO_SMITE)) {
                    return false;
                } else {
                    if (this.field_7082 <= 0) {
                        this.field_7082 = 20;
                    }
                    if (this.level().getRandom().nextFloat() < 0.6F) {
                        this.gotDamage = true;
                    }
                    return super.damage(source, amount);
                }
            }
        } else {
            return false;
        }
    }

    private boolean gotDamage() {
        if (this.gotDamage) {
            this.gotDamage = false;
            return true;
        } else
            return false;
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

    public int getInvulnerableTimer() {
        return (Integer) this.dataTracker.get(INVUL_TIMER);
    }

    public void setInvulTimer(int ticks) {
        this.dataTracker.set(INVUL_TIMER, ticks);
    }

    @Override
    protected boolean canStartRiding(Entity entity) {
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

    private void setBeamTarget(int entityId) {
        this.dataTracker.set(BEAM_TARGET_ID, entityId);
    }

    public boolean hasBeamTarget() {
        return (Integer) this.dataTracker.get(BEAM_TARGET_ID) != 0;
    }

    @Nullable
    public LivingEntity getBeamTarget() {
        if (!this.hasBeamTarget()) {
            return null;
        } else if (this.level().isClientSide()) {
            if (this.cachedBeamTarget != null) {
                return this.cachedBeamTarget;
            } else {
                Entity entity = this.level().getEntityById((Integer) this.dataTracker.get(BEAM_TARGET_ID));
                if (entity instanceof LivingEntity) {
                    this.cachedBeamTarget = (LivingEntity) entity;
                    return this.cachedBeamTarget;
                } else {
                    return null;
                }
            }
        } else {
            return this.getTarget();
        }
    }

    public int getWarmupTime() {
        return 120;
    }

    @Override
    public void onTrackedDataSet(EntityDataAccessor<?> data) {
        super.onTrackedDataSet(data);
        if (BEAM_TARGET_ID.equals(data)) {
            this.cachedBeamTarget = null;
        }

    }

    @Override
    public boolean canImmediatelyDespawn(double num) {
        return false;
    }

    @Override
    public void updatePostDeath() {
        if (this.duplicationTimer <= 0) {
            this.deathTimer++;
            this.move(MoverType.SELF, new Vec3(0.0D, 0.005D, 0.0D));
            this.setAiDisabled(true);
            this.setTarget(null);
            if (this.level().isClientSide()) {
                despawnParticlesServer(this);

            }
            if (!this.level().isClientSide()) {
                this.bossBar.setPercent(0.0F);
                BlockPos deathPos = BlockPos.ofFloored(this.getX(), this.getY() - 1, this.getZ());
                if (deathTimer == 20 && !this.isVoidZLoaded) {
                    AABB box = new AABB(this.getBlockPos());
                    List<Player> list = this.level().getEntitiesByClass(Player.class, box.inflate(128D), EntitySelector.EXCEPT_SPECTATOR);
                    for (int i = 0; i < list.size(); ++i) {
                        Player playerEntity = (Player) list.get(i);
                        if (playerEntity instanceof Player) {
                            playerEntity.addStatusEffect(new MobEffectInstance(EffectInit.FAME, 48000, 0, false, false, true));
                        }
                    }
                }
                if (deathTimer == 140) {
                    this.level().playSound(null, deathPos, SoundInit.EYE_DEATH_PLATFORM_EVENT, SoundSource.HOSTILE, 1F, 1F);
                }
                if (deathTimer >= 200) {
                    for (int o = 0; o < 15; o++) {
                        ((ServerLevel) this.level()).spawnParticles(ParticleTypes.EXPLOSION, deathPos.getX() - 6 + this.level().getRandom().nextInt(13),
                                deathPos.getY() - 1 + this.level().getRandom().nextInt(11), deathPos.getZ() - 6 + this.level().getRandom().nextInt(13), 0, 0.0D, 0.0D, 0.0D, 0.01D);
                        this.level().playSound(null, deathPos, SoundEvents.ENTITY_GENERIC_EXPLODE.value(), SoundSource.BLOCKS, 1F, 1F);
                    }
                    // Platform
                    if (this.level().getHeight() - 10 < deathPos.getY())
                        deathPos = deathPos.down(deathPos.getY() - this.level().getHeight() + 10);
                    this.placeDeathStructure(deathPos);
                    if (this.isVoidZLoaded) {
                        this.level().setBlockState(deathPos.up(8).north().west(), BuiltInRegistries.BLOCK.get(Identifier.of("voidz", "void_portal")).defaultBlockState(), 3);
                    } else {
                        this.level().setBlockState(deathPos.up(8).north().west(), Blocks.DRAGON_EGG.defaultBlockState(), 3);
                    }
                    if (this.level().getRandom().nextFloat() <= 0.01F) {
                        this.dropItem(ItemInit.PRIME_EYE);
                    }
                    this.discard();
                }

            }
        } else
            this.discard();

    }

    private void despawnParticlesServer(LivingEntity entity) {
        for (int i = 0; i < 12; ++i) {
            double d = this.random.nextGaussian() * 0.025D;
            double e = this.random.nextGaussian() * 0.025D;
            double f = this.random.nextGaussian() * 0.025D;
            double x = Mth.nextDouble(random, entity.getBoundingBox().minX - 1.5D, entity.getBoundingBox().maxX + 1.5D);
            double y = Mth.nextDouble(random, entity.getBoundingBox().minY - 1.5D, entity.getBoundingBox().maxY + 1.5D);
            double z = Mth.nextDouble(random, entity.getBoundingBox().minZ - 1.5D, entity.getBoundingBox().maxZ + 1.5D);
            entity.level().addParticle(ParticleTypes.PORTAL, x, y, z, d, e, f);
        }
    }

    private void placeDeathStructure(BlockPos blockPos) {
        StructureTemplateManager structureTemplateManager = ((ServerLevel) this.level()).getStructureTemplateManager();
        Optional<StructureTemplate> structure = structureTemplateManager.getTemplate(Identifier.of("adventurez:eyeland"));
        structure.get().place((ServerLevel) this.level(), blockPos.west(5).north(5), blockPos,
                (new StructurePlaceSettings()).setMirror(Mirror.NONE).setRotation(Rotation.NONE).setIgnoreEntities(true), this.level().getRandom(), Block.NOTIFY_LISTENERS);
    }

    public float getBeamProgress() {
        return 0.0f;
    }

    static {
        INVUL_TIMER = SynchedEntityData.registerData(TheEyeEntity.class, EntityDataSerializers.INTEGER);
        BEAM_TARGET_ID = SynchedEntityData.registerData(TheEyeEntity.class, EntityDataSerializers.INTEGER);
    }

    private class FireBeamGoal extends Goal {
        private final TheEyeEntity theEye;
        private int beamTicks;

        public FireBeamGoal(TheEyeEntity theEye) {
            this.theEye = theEye;
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = this.theEye.getTarget();
            return livingEntity != null && livingEntity.isAlive();
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity livingEntity = this.theEye.getTarget();
            if (this.theEye.gotDamage()) {
                return false;
            }
            return super.shouldContinue() && this.theEye.squaredDistanceTo(this.theEye.getTarget()) > 8.0D && livingEntity != null && livingEntity.getY() < this.theEye.getY();
        }

        @Override
        public void start() {
            this.beamTicks = -20;
            this.theEye.getNavigation().stop();
            this.theEye.getLookControl().lookAt(this.theEye.getTarget(), 90.0F, 90.0F);
            this.theEye.velocityDirty = true;
        }

        @Override
        public void stop() {
            this.theEye.setBeamTarget(0);
            this.theEye.setTarget((LivingEntity) null);
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = this.theEye.getTarget();
            this.theEye.getLookControl().lookAt(livingEntity, 90.0F, 90.0F);
            if (!this.theEye.canSee(livingEntity)) {
                this.theEye.setTarget((LivingEntity) null);
            } else {
                ++this.beamTicks;
                if (this.beamTicks == 0) {
                    this.theEye.setBeamTarget(this.theEye.getTarget().getId());
                } else if (this.beamTicks >= this.theEye.getWarmupTime()) {
                    float f = 4.0F;
                    if (this.theEye.level().getDifficulty() == Difficulty.HARD) {
                        f += 1.0F;
                    }
                    livingEntity.damage(this.theEye.getDamageSources().magic(), f);
                    livingEntity.damage(this.theEye.getDamageSources().mobAttack(this.theEye), (float) this.theEye.getAttributeValue(Attributes.GENERIC_ATTACK_DAMAGE));
                    this.theEye.setTarget((LivingEntity) null);
                }

                super.tick();
            }
        }
    }

    private class ShootBulletGoal extends Goal {
        private int counter;
        private final TheEyeEntity theEyeEntity;

        public ShootBulletGoal(TheEyeEntity theEyeEntity) {
            this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
            this.theEyeEntity = theEyeEntity;
        }

        @Override
        public boolean canStart() {
            this.counter++;
            LivingEntity livingEntity = theEyeEntity.getTarget();
            if (livingEntity != null && livingEntity.isAlive() && theEyeEntity.getHealth() < theEyeEntity.getMaxHealth() / 2 && this.counter >= 400) {
                return theEyeEntity.level().getDifficulty() != Difficulty.PEACEFUL;
            } else {
                return false;
            }
        }

        @Override
        public void tick() {
            LivingEntity livingEntity = theEyeEntity.getTarget();
            if (livingEntity != null) {
                int additions = 0;
                if (theEyeEntity.getHealth() < theEyeEntity.getMaxHealth() / 4) {
                    additions = 1;
                }
                for (int i = 0; i < 3 + additions; i++) {
                    theEyeEntity.level().spawnEntity(new TinyEyeEntity(theEyeEntity.level(), theEyeEntity, livingEntity, Direction.Axis.Y));
                    theEyeEntity.playSound(SoundEvents.ENTITY_SHULKER_SHOOT, 2.0F, (theEyeEntity.random.nextFloat() - theEyeEntity.random.nextFloat()) * 0.2F + 1.0F);
                }
                super.tick();
                this.counter = theEyeEntity.random.nextInt(12) * 20;
            }
        }
    }

    private class FlyRandomlyGoal extends Goal {
        private final TheEyeEntity theEyeEntity;

        public FlyRandomlyGoal(TheEyeEntity theEyeEntity) {
            this.theEyeEntity = theEyeEntity;
            this.setControls(EnumSet.of(Goal.Control.MOVE));
        }

        @Override
        public boolean canStart() {
            MoveControl moveControl = this.theEyeEntity.getMoveControl();
            if (!moveControl.isMoving()) {
                return true;
            } else {
                double d = moveControl.getTargetX() - this.theEyeEntity.getX();
                double e = moveControl.getTargetY() - this.theEyeEntity.getY();
                double f = moveControl.getTargetZ() - this.theEyeEntity.getZ();
                double g = d * d + e * e + f * f;
                return g < 1.0D || g > 3600.0D;
            }
        }

        @Override
        public boolean shouldContinue() {
            return false;
        }

        @Override
        public void start() {
            RandomSource random = this.theEyeEntity.getRandom();
            double d = this.theEyeEntity.getX() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            double e = this.theEyeEntity.getY() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 1.2F);
            double f = this.theEyeEntity.getZ() + (double) ((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
            this.theEyeEntity.getMoveControl().moveTo(d, e, f, 1.0D);
        }
    }

    private class LookAtTargetGoal extends Goal {
        private final TheEyeEntity theEyeEntity;

        public LookAtTargetGoal(TheEyeEntity theEyeEntity) {
            this.theEyeEntity = theEyeEntity;
            this.setControls(EnumSet.of(Goal.Control.LOOK));
        }

        @Override
        public boolean canStart() {
            return theEyeEntity.getInvulnerableTimer() <= 0;
        }

        @Override
        public void tick() {
            if (this.theEyeEntity.getTarget() == null) {
                Vec3 vec3d = this.theEyeEntity.getVelocity();
                this.theEyeEntity.setYaw(-((float) Mth.atan2(vec3d.x, vec3d.z)) * 57.295776F);
                this.theEyeEntity.bodyYaw = this.theEyeEntity.getYaw();
            } else {
                LivingEntity livingEntity = this.theEyeEntity.getTarget();
                if (livingEntity.squaredDistanceTo(this.theEyeEntity) < 4096.0D) {
                    double e = livingEntity.getX() - this.theEyeEntity.getX();
                    double f = livingEntity.getZ() - this.theEyeEntity.getZ();
                    if (Math.abs(theEyeEntity.getTarget().getX() - theEyeEntity.getX()) > 0.3D && Math.abs(theEyeEntity.getTarget().getZ() - theEyeEntity.getZ()) > 0.3D) {
                        this.theEyeEntity.setYaw(-((float) Mth.atan2(e, f)) * 57.295776F);
                        this.theEyeEntity.bodyYaw = this.theEyeEntity.getYaw();
                    }
                }
            }

        }
    }

    private class EyeMoveControl extends MoveControl {
        private final TheEyeEntity theEyeEntity;
        private int collisionCheckCooldown;

        public EyeMoveControl(TheEyeEntity theEyeEntity) {
            super(theEyeEntity);
            this.theEyeEntity = theEyeEntity;
        }

        @Override
        public void tick() {
            if (this.state == MoveControl.State.MOVE_TO) {
                if (this.collisionCheckCooldown-- <= 0) {
                    this.collisionCheckCooldown += this.theEyeEntity.getRandom().nextInt(5) + 2;
                    Vec3 vec3d = new Vec3(this.targetX - this.theEyeEntity.getX(), this.targetY - this.theEyeEntity.getY(), this.targetZ - this.theEyeEntity.getZ());
                    double d = vec3d.length();
                    vec3d = vec3d.normalize();
                    if (theEyeEntity.getTarget() != null && Math.abs(theEyeEntity.getTarget().getX() - theEyeEntity.getX()) < 3.0D
                            && Math.abs(theEyeEntity.getTarget().getZ() - theEyeEntity.getZ()) < 3.0D) {
                        this.state = MoveControl.State.WAIT;
                    } else if (this.willCollide(vec3d, Mth.ceil(d))) {
                        this.theEyeEntity.setVelocity(this.theEyeEntity.getVelocity().add(vec3d.multiply(0.1D)));
                    } else {
                        this.state = MoveControl.State.WAIT;
                    }
                }

            }
        }

        private boolean willCollide(Vec3 direction, int steps) {
            AABB box = this.theEyeEntity.getBoundingBox();
            for (int i = 1; i < steps; ++i) {
                box = box.offset(direction);
                if (!this.theEyeEntity.level().isSpaceEmpty(this.theEyeEntity, box)) {
                    return false;
                }
            }
            return true;
        }
    }

    private class DuplicateAttack extends Goal {
        private int cooldown;
        private final TheEyeEntity theEyeEntity;

        public DuplicateAttack(TheEyeEntity theEyeEntity) {
            this.theEyeEntity = theEyeEntity;
        }

        @Override
        public boolean canStart() {
            this.cooldown++;
            LivingEntity livingEntity = theEyeEntity.getTarget();

            if (livingEntity != null && livingEntity.isAlive() && theEyeEntity.duplicationTimer <= 0 && this.cooldown >= 600) {
                List<TheEyeEntity> list = theEyeEntity.level().getEntitiesByClass(TheEyeEntity.class, theEyeEntity.getBoundingBox().inflate(120D), EntitySelector.EXCEPT_CREATIVE_OR_SPECTATOR);
                if (!list.isEmpty())
                    for (int i = 0; i < list.size(); i++)
                        if (list.get(i).duplicationTimer > 0) {
                            this.cooldown = 0;
                            return false;
                        }
                return true;
            } else
                return false;
        }

        @Override
        public void start() {
            LivingEntity livingEntity = theEyeEntity.getTarget();
            if (livingEntity != null) {
                for (int i = 0; i < 2; i++) {
                    for (int k = 0; k < 10; k++) {
                        BlockPos pos = theEyeEntity.getBlockPos();
                        pos = pos.add(pos.getX() - livingEntity.getBlockPos().getX() + theEyeEntity.level().getRandom().nextInt(6) * 5, 0,
                                pos.getZ() - livingEntity.getBlockPos().getZ() + theEyeEntity.level().getRandom().nextInt(6) * 5);
                        if (theEyeEntity.level().getBlockState(pos).isAir()
                                && SpawnPlacements.canSpawn(EntityInit.THE_EYE, (ServerLevel) theEyeEntity.level(), EntitySpawnReason.EVENT, pos, theEyeEntity.level().getRandom())) {
                            TheEyeEntity theEyeEntityDuplicate = EntityInit.THE_EYE.create(theEyeEntity.level());
                            theEyeEntityDuplicate.refreshPositionAndAngles(pos, theEyeEntity.level().getRandom().nextFloat() * 360F, 0.0F);
                            theEyeEntityDuplicate.initialize((ServerLevel) theEyeEntity.level(), theEyeEntity.level().getLocalDifficulty(pos), EntitySpawnReason.EVENT, null);
                            theEyeEntityDuplicate.duplicationTimer = 800;
                            theEyeEntityDuplicate.setAttacker(livingEntity);
                            theEyeEntityDuplicate.getAttributeInstance(Attributes.GENERIC_MAX_HEALTH).setBaseValue(100.0D);
                            theEyeEntity.level().spawnEntity(theEyeEntityDuplicate);
                            for (int u = 0; u < 50; u++) {
                                double d = (double) theEyeEntityDuplicate.getX() - 1.5F + theEyeEntity.level().getRandom().nextFloat() * 3.0F;
                                double e = (double) ((float) theEyeEntityDuplicate.getRandomBodyY() + theEyeEntity.level().getRandom().nextFloat() * 0.1F);
                                double f = (double) theEyeEntityDuplicate.getZ() - 1.5F + theEyeEntity.level().getRandom().nextFloat() * 3.0F;
                                double g = (double) (theEyeEntity.level().getRandom().nextFloat() * 0.2D);
                                double h = (double) theEyeEntity.level().getRandom().nextFloat() * 0.1D;
                                double l = (double) (theEyeEntity.level().getRandom().nextFloat() * 0.2D);
                                ((ServerLevel) theEyeEntity.level()).spawnParticles(ParticleTypes.PORTAL, d, e, f, 4, g, h, l, 1.0D);
                            }
                            break;
                        }
                    }
                }
            }
            this.stop();

        }

        @Override
        public void stop() {
            this.cooldown = 0;
        }
    }

}

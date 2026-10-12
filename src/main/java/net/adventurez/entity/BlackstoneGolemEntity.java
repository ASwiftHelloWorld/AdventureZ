package net.adventurez.entity;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import net.adventurez.entity.nonliving.ThrownRockEntity;
import net.adventurez.init.EntityInit;
import net.adventurez.init.SoundInit;
import net.adventurez.init.TagInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.BossEvent;

public class BlackstoneGolemEntity extends Monster {

    public static final EntityDataAccessor<Integer> THROW_COOLDOWN;
    public static final EntityDataAccessor<Boolean> INVULNERABLE;
    public static final EntityDataAccessor<Integer> INVULNERABLE_TIMER;
    public static final EntityDataAccessor<Integer> LAVA_TEXTURE;
    public static final EntityDataAccessor<Boolean> HALF_LIFE_CHANGE;
    private static final Identifier WALKING_SPEED_INCREASE_ID;
    private static final AttributeModifier WALKING_SPEED_INCREASE;
    private static final Predicate<Entity> NOT_STONEGOLEM = (entity) -> {
        return entity.isAlive() && !(entity instanceof BlackstoneGolemEntity);
    };
    private int cooldown = 0;
    private int thrownStoneCooldown = 120;
    private int attackTick;
    private int stunTick;
    private int roarTick;
    private int powerPhaseActivate = 0;
    private int lavaRegenerateLife = 0;

    private final ServerBossEvent bossBar;

    private final boolean isDungeonZLoaded = FabricLoader.getInstance().isModLoaded("dungeonz");

    public BlackstoneGolemEntity(EntityType<? extends BlackstoneGolemEntity> entityType, Level world) {
        super(entityType, world);
        this.experiencePoints = 200;
        this.bossBar = new ServerBossEvent(this.getDisplayName(), BossEvent.Color.RED, BossEvent.Style.PROGRESS);
    }

    public static AttributeSupplier.Builder createStoneGolemAttributes() {
        return Monster.createHostileAttributes().add(Attributes.GENERIC_MAX_HEALTH, 600.0D).add(Attributes.GENERIC_MOVEMENT_SPEED, 0.24D)
                .add(Attributes.GENERIC_KNOCKBACK_RESISTANCE, 2.5D).add(Attributes.GENERIC_ATTACK_DAMAGE, 14.0D).add(Attributes.GENERIC_ATTACK_KNOCKBACK, 2.8D)
                .add(Attributes.GENERIC_FOLLOW_RANGE, 38.0D);
    }

    @Override
    public void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new FloatGoal(this));
        this.goalSelector.add(1, new BlackstoneGolemEntity.AttackGoal());
        this.goalSelector.add(2, new WaterAvoidingRandomStrollGoal(this, 0.6D));
        this.goalSelector.add(3, new LookAtPlayerGoal(this, Player.class, 60.0F));
        this.goalSelector.add(4, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.add(1, new NearestAttackableTargetGoal<>(this, Player.class, false));
        this.targetSelector.add(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));

    }

    @Override
    public void writeCustomDataToNbt(CompoundTag tag) {
        super.writeCustomDataToNbt(tag);
        tag.putInt("AttackTick", this.attackTick);
        tag.putInt("StunTick", this.stunTick);
        tag.putInt("RoarTick", this.roarTick);
        tag.putInt("Invul", this.getInvulnerableTimer());
        tag.putInt("LavaTexture", this.getLavaTexture());
    }

    @Override
    public void readCustomDataFromNbt(CompoundTag tag) {
        super.readCustomDataFromNbt(tag);
        this.attackTick = tag.getInt("AttackTick");
        this.stunTick = tag.getInt("StunTick");
        this.roarTick = tag.getInt("RoarTick");
        this.setInvulTimer(tag.getInt("Invul"));
        this.setLavaTexture(tag.getInt("LavaTexture"));
        if (this.hasCustomName()) {
            this.bossBar.setName(this.getDisplayName());
        }
    }

    @Override
    public void setCustomName(Component name) {
        super.setCustomName(name);
        this.bossBar.setName(this.getDisplayName());
    }

    @Override
    public void onStartedTrackingBy(ServerPlayer player) {
        super.onStartedTrackingBy(player);
        this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayer player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    protected void initDataTracker(Builder builder) {
        super.initDataTracker(builder);
        builder.add(THROW_COOLDOWN, 0);
        builder.add(INVULNERABLE, true);
        builder.add(LAVA_TEXTURE, 400);
        builder.add(HALF_LIFE_CHANGE, false);
        builder.add(INVULNERABLE_TIMER, 0);
    }

    @Override
    protected PathNavigation createNavigation(Level world) {
        return new BlackstoneGolemEntity.Navigation(this, world);
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    public void mobTick() {
        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
    }

    @Override
    protected float getSoundVolume() {
        return 1.3F;
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        if (this.isAlive()) {
            if (this.getInvulnerableTimer() > 0) {
                this.setInvulTimer(this.getInvulnerableTimer() - 1);
            }
            if (this.getInvulnerableTimer() == 0) {
                if (this.getLavaTexture() < 400) {
                    this.setLavaTexture(this.getLavaTexture() + 1);
                }
                if (dataTracker.get(INVULNERABLE)) {
                    dataTracker.set(INVULNERABLE, false);
                    this.playSound(SoundInit.GOLEM_AWAKENS_EVENT, 2.0F, 1.0F);
                }
            }
            if (this.getHealth() <= this.getMaxHealth() / 2) {
                if (this.powerPhaseActivate <= 80) {
                    this.powerPhaseActivate++;
                    if (this.powerPhaseActivate == 1 || this.powerPhaseActivate == 2) {
                        this.setAiDisabled(true);
                    }
                    if (this.powerPhaseActivate == 78 || this.powerPhaseActivate == 79) {
                        this.setAiDisabled(false);
                        this.roar();
                    }
                }
                if (this.powerPhaseActivate == 80) {
                    this.getAttributeInstance(Attributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3D);
                    this.getAttributeInstance(Attributes.GENERIC_ATTACK_KNOCKBACK).setBaseValue(4.0D);
                    dataTracker.set(HALF_LIFE_CHANGE, true);
                }
            } else if (this.isInLava() && this.getHealth() < this.getMaxHealth()) {
                this.lavaRegenerateLife++;
                if (this.lavaRegenerateLife >= 200) {
                    this.setHealth(this.getHealth() + 2F);
                    this.lavaRegenerateLife = 0;
                }
            }
            if (this.horizontalCollision && this.level().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
                boolean bl = false;
                AABB box = this.getBoundingBox().inflate(0.25D);
                Iterator<BlockPos> iterator = BlockPos.iterate(Mth.floor(box.minX), Mth.floor(box.minY + 0.25D), Mth.floor(box.minZ), Mth.floor(box.maxX),
                        Mth.floor(box.maxY + 0.4D), Mth.floor(box.maxZ)).iterator();

                while (true) {
                    BlockPos blockPos = null;
                    Block block = null;
                    BlockState blockState = null;

                    if (!iterator.hasNext()) {
                        if (!bl && this.isOnGround()) {
                            this.jump();
                        }
                        break;
                    }

                    blockPos = iterator.next();
                    blockState = this.level().getBlockState(blockPos);
                    block = blockState.getBlock();

                    if (block instanceof Block && !net.adventurez.util.MinecraftCompat.isBlockInTag(blockState, TagInit.UNBREAKABLE_BLOCKS)) {
                        if (isDungeonZLoaded && this.level().getRegistryKey().getValue().equals(Identifier.of("dungeonz", "dungeon"))) {
                            break;
                        }
                        bl = this.level().breakBlock(blockPos, false, this) || bl;
                    }
                }
            }

            if (this.roarTick > 0) {
                --this.roarTick;
                if (this.roarTick == 10) {
                    this.roar();
                }
            }

            if (this.attackTick > 0) {
                --this.attackTick;
            }

            if (this.stunTick > 0) {
                --this.stunTick;
                this.spawnStunnedParticles();
                if (this.stunTick == 0) {
                    this.playSound(SoundInit.GOLEM_ROAR_EVENT, 1.0F, 1.0F);
                    this.roarTick = 30;
                }
            }
            if (this.getTarget() != null && this.canSee(this.getTarget())) {
                if (this.squaredDistanceTo(getTarget()) < 1400D && this.squaredDistanceTo(getTarget()) > 100D) {
                    dataTracker.set(THROW_COOLDOWN, cooldown);
                    this.cooldown++;
                    if (cooldown == thrownStoneCooldown - 10) {
                        this.playSound(SoundInit.GOLEM_ROAR_EVENT, 1F, 1F);
                        throwRock(this.getTarget());
                    }
                    if (cooldown >= thrownStoneCooldown) {
                        this.cooldown = -80;
                    }
                }
            }
            if (this.isOnSoulSpeedBlock() && !this.getAttributes().hasModifierForAttribute(Attributes.GENERIC_MOVEMENT_SPEED, WALKING_SPEED_INCREASE_ID)) {
                this.getAttributeInstance(Attributes.GENERIC_MOVEMENT_SPEED).addTemporaryModifier(WALKING_SPEED_INCREASE);
            } else if (!this.isOnSoulSpeedBlock() && this.getAttributes().hasModifierForAttribute(Attributes.GENERIC_MOVEMENT_SPEED, WALKING_SPEED_INCREASE_ID)) {
                this.getAttributeInstance(Attributes.GENERIC_MOVEMENT_SPEED).removeModifier(WALKING_SPEED_INCREASE_ID);
            }
            if (this.isInLava()) {
                this.setVelocity(this.getVelocity().multiply(1.9D));
            }
        }
    }

    private boolean isOnSoulSpeedBlock() {
        return this.level().getBlockState(this.getVelocityAffectingPos()).isIn(BlockTags.SOUL_SPEED_BLOCKS);
    }

    private void spawnStunnedParticles() {
        if (this.random.nextInt(6) == 0) {
            double d = this.getX() - (double) this.getWidth() * Math.sin((double) (this.bodyYaw * 0.017453292F)) + (this.random.nextDouble() * 0.6D - 0.3D);
            double e = this.getY() + (double) this.getHeight() - 0.3D;
            double f = this.getZ() + (double) this.getWidth() * Math.cos((double) (this.bodyYaw * 0.017453292F)) + (this.random.nextDouble() * 0.6D - 0.3D);
            this.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0.49803922F, 0.5137255F, 0.57254905F), d, e, f, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected boolean isImmobile() {
        return super.isImmobile() || this.attackTick > 0 || this.stunTick > 0 || this.roarTick > 0 || (this.cooldown > thrownStoneCooldown - 30 && this.cooldown > 0)
                || this.getDataTracker().get(INVULNERABLE);
    }

    @Override
    public boolean canSee(Entity entity) {
        return this.stunTick <= 0 && this.roarTick <= 0 ? super.canSee(entity) : false;
    }

    @Override
    protected void knockback(LivingEntity target) {
        if (this.roarTick == 0) {
            if (this.random.nextDouble() < 0.5D) {
                this.stunTick = 40;
                this.playSound(SoundInit.GOLEM_IDLE_EVENT, 1.0F, 1.0F);
                this.level().sendEntityStatus(this, (byte) 39);
                target.pushAwayFrom(this);
            } else {
                this.knockBack(target);
            }

            target.velocityModified = true;
        }

    }

    private void roar() {
        if (this.isAlive()) {
            List<LivingEntity> list = this.level().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().inflate(5.0D), NOT_STONEGOLEM);

            LivingEntity entity;
            for (Iterator<LivingEntity> var2 = list.iterator(); var2.hasNext(); this.knockBack(entity)) {
                entity = (LivingEntity) var2.next();
                entity.damage(this.getDamageSources().mobAttack(this), 10.0F);
                entity.setVelocity(entity.getVelocity().add(0.0D, 0.63D, 0.0D));
            }

            Vec3 vec3d = this.getBoundingBox().getCenter();

            for (int i = 0; i < 50; ++i) {
                double d = this.random.nextGaussian() * 0.2D;
                double e = this.random.nextGaussian() * 0.2D;
                double f = this.random.nextGaussian() * 0.2D;
                this.level().addParticle(ParticleTypes.POOF, vec3d.x, vec3d.y, vec3d.z, d, e, f);
            }
        }

    }

    private void knockBack(Entity entity) {
        double d = entity.getX() - this.getX();
        double e = entity.getZ() - this.getZ();
        double f = Math.max(d * d + e * e, 0.001D);
        entity.addVelocity(d / f * 4.0D, 0.2D, e / f * 4.0D);
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void handleStatus(byte status) {

        if (status == 4) {
            this.attackTick = 10;
            this.playSound(SoundInit.GOLEM_HIT_EVENT, 1.0F, 1.0F);
        } else if (status == 39) {
            this.stunTick = 40;
        }

        super.handleStatus(status);
    }

    @Environment(EnvType.CLIENT)
    public int getAttackTick() {
        return this.attackTick;
    }

    @Environment(EnvType.CLIENT)
    public int getStunTick() {
        return this.stunTick;
    }

    @Environment(EnvType.CLIENT)
    public int getRoarTick() {
        return this.roarTick;
    }

    @Override
    public boolean tryAttack(Entity target) {
        this.attackTick = 10;
        this.level().sendEntityStatus(this, (byte) 4);
        this.playSound(SoundInit.GOLEM_HIT_EVENT, 1.0F, 1.0F);
        if (target instanceof LivingEntity)
            ((LivingEntity) target).setOnFireFor(3 + this.level().getRandom().nextInt(6));
        return super.tryAttack(target);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (this.getDataTracker().get(INVULNERABLE)) {
            return null;
        } else
            return SoundInit.GOLEM_IDLE_EVENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundInit.GOLEM_HIT_EVENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundInit.GOLEM_DEATH_EVENT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundInit.GOLEM_WALK_EVENT, 0.15F, 1.0F);
    }

    @Override
    public boolean canSpawn(LevelReader world) {
        return !world.containsFluid(this.getBoundingBox());
    }

    @Override
    public boolean canImmediatelyDespawn(double num) {
        return false;
    }

    @Override
    public void checkDespawn() {
        if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
            this.discard();
        }

    }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return super.canUsePortals(allowVehicles);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return super.damage(source, amount);
        } else if (this.getDataTracker().get(INVULNERABLE)) {
            return false;
        } else {
            if (source.getSource() instanceof LivingEntity attacker) {
                this.setAttacker(attacker);
                this.setTarget(attacker);
                if (this.level().getRandom().nextFloat() <= 0.05F) {
                    MiniBlackstoneGolemEntity smallStoneGolemEntity = EntityInit.MINI_BLACKSTONE_GOLEM.create(this.level());
                    smallStoneGolemEntity.refreshPositionAndAngles(this.getBlockPos(), this.level().getRandom().nextFloat() * 360F, 0.0F);
                    this.level().spawnEntity(smallStoneGolemEntity);
                }
            }
            return super.damage(source, amount);
        }
    }

    private void throwRock(LivingEntity target) {
        Vec3 vec3d_1 = this.getRotationVec(1.0F);
        double x_vector;
        double z_vector;
        if (vec3d_1.x < 0 && vec3d_1.z < 0) {
            x_vector = vec3d_1.x + 1.8D;
            z_vector = vec3d_1.z - 2.8D;
        } else if (vec3d_1.x < 0 && vec3d_1.z > 0) {
            x_vector = vec3d_1.x - 1.8D;
            z_vector = vec3d_1.z - 2.8D;
        } else if (vec3d_1.x > 0 && vec3d_1.z < 0) {
            x_vector = vec3d_1.x + 1.8D;
            z_vector = vec3d_1.z + 2.8D;
        } else if (vec3d_1.x > 0 && vec3d_1.z > 0) {
            x_vector = vec3d_1.x - 1.8D;
            z_vector = vec3d_1.z + 2.8D;
        } else {
            x_vector = vec3d_1.x + 1.8D;
            z_vector = vec3d_1.z - 2.8D;
        }
        double x = target.getX() - this.getX() - x_vector;
        double y = target.getBodyY(1D) - this.getBodyY(0.1D);
        double z = target.getZ() - this.getZ() - z_vector;
        ThrownRockEntity thrownRockEntity = new ThrownRockEntity(this.level(), this.getX() + x_vector, this.getY() + 0.5D, this.getZ() + z_vector);
        thrownRockEntity.setOwner(this);
        if (!this.level().isClientSide()) {
            if (this.squaredDistanceTo(getTarget()) < 800D) {
                thrownRockEntity.setVelocity(x, y, z, 1.8F, 3.0F);
                this.level().spawnEntity(thrownRockEntity);
            } else {
                thrownRockEntity.setVelocity(x, y + 4D, z, 1.8F, 2.0F);
                this.level().spawnEntity(thrownRockEntity);
            }
        }
    }

    public void sendtoEntity() {
        this.setInvulTimer(220);
        this.setLavaTexture(0);
    }

    public int getInvulnerableTimer() {
        return (Integer) this.dataTracker.get(INVULNERABLE_TIMER);
    }

    public void setInvulTimer(int ticks) {
        this.dataTracker.set(INVULNERABLE_TIMER, ticks);
    }

    public int getLavaTexture() {
        return (Integer) this.dataTracker.get(LAVA_TEXTURE);
    }

    public void setLavaTexture(int ticks) {
        this.dataTracker.set(LAVA_TEXTURE, ticks);
    }

    @Override
    public void onDeath(DamageSource source) {
        if (!this.level().isClientSide()) {
            MiniBlackstoneGolemEntity smallStoneGolemEntity = (MiniBlackstoneGolemEntity) EntityInit.MINI_BLACKSTONE_GOLEM.create(this.level());
            MiniBlackstoneGolemEntity smallStoneGolemEntitySecond = (MiniBlackstoneGolemEntity) EntityInit.MINI_BLACKSTONE_GOLEM.create(this.level());
            smallStoneGolemEntity.refreshPositionAndAngles(this.getBlockPos().east(), this.level().getRandom().nextFloat() * 360F, 0.0F);
            smallStoneGolemEntitySecond.refreshPositionAndAngles(this.getBlockPos().west(), this.level().getRandom().nextFloat() * 360F, 0.0F);
            this.level().spawnEntity(smallStoneGolemEntity);
            this.level().spawnEntity(smallStoneGolemEntitySecond);
        }
        super.onDeath(source);
    }

    private static class PathNodeMaker extends WalkNodeEvaluator {
        private PathNodeMaker() {
        }

        // @Override
        // protected PathType adjustNodeType(BlockView world, BlockPos pos, PathType type) {
        // return type == PathType.LAVA ? PathType.OPEN : super.adjustNodeType(world, pos, type);
        // }
    }

    private static class Navigation extends GroundPathNavigation {
        public Navigation(Mob mobEntity, Level world) {
            super(mobEntity, world);
        }

        @Override
        protected boolean canWalkOnPath(PathType pathType) {
            return pathType != PathType.LAVA && pathType != PathType.DAMAGE_FIRE && pathType != PathType.DANGER_FIRE ? super.canWalkOnPath(pathType) : true;
        }

        @Override
        public boolean isValidPosition(BlockPos pos) {
            return net.adventurez.util.MinecraftCompat.isBlock(this.world.getBlockState(pos), Blocks.LAVA) || super.isValidPosition(pos);
        }

        @Override
        protected PathFinder createPathNodeNavigator(int range) {
            this.nodeMaker = new BlackstoneGolemEntity.PathNodeMaker();
            return new PathFinder(this.nodeMaker, range);
        }
    }

    private class AttackGoal extends MeleeAttackGoal {
        public AttackGoal() {
            super(BlackstoneGolemEntity.this, 1.0D, true);
        }

    }

    static {
        THROW_COOLDOWN = SynchedEntityData.registerData(BlackstoneGolemEntity.class, EntityDataSerializers.INTEGER);
        INVULNERABLE = SynchedEntityData.registerData(BlackstoneGolemEntity.class, EntityDataSerializers.BOOLEAN);
        INVULNERABLE_TIMER = SynchedEntityData.registerData(BlackstoneGolemEntity.class, EntityDataSerializers.INTEGER);
        LAVA_TEXTURE = SynchedEntityData.registerData(BlackstoneGolemEntity.class, EntityDataSerializers.INTEGER);
        HALF_LIFE_CHANGE = SynchedEntityData.registerData(BlackstoneGolemEntity.class, EntityDataSerializers.BOOLEAN);
        WALKING_SPEED_INCREASE_ID = Identifier.of("adventurez:walking_speed_increase");
        WALKING_SPEED_INCREASE = new AttributeModifier(WALKING_SPEED_INCREASE_ID, 0.5D, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }
}

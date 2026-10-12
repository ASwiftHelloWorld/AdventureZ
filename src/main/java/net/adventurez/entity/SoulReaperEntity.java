package net.adventurez.entity;

import org.jetbrains.annotations.Nullable;

import net.adventurez.init.EntityInit;
import net.adventurez.init.SoundInit;
import net.adventurez.mixin.accessor.ProjectileEntityAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;

public class SoulReaperEntity extends Monster implements RangedAttackMob {
    private final RangedBowAttackGoal<SoulReaperEntity> bowAttackGoal = new RangedBowAttackGoal<>(this, 1.0D, 40, 15.0F);
    private final MeleeAttackGoal meleeAttackGoal = new MeleeAttackGoal(this, 1.2D, true) {
        @Override
        public void stop() {
            super.stop();
            SoulReaperEntity.this.setAttacking(false);
        }

        @Override
        public void start() {
            super.start();
            SoulReaperEntity.this.setAttacking(true);
        }
    };

    public SoulReaperEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.setPathfindingPenalty(PathType.LAVA, 8.0F);
        this.experiencePoints = 30;
    }

    public static AttributeSupplier.Builder createSoulReaperAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MOVEMENT_SPEED, 0.25D).add(Attributes.MAX_HEALTH, 120.0D);
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[] { SoulReaperEntity.class })));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractPiglin.class, true));
    }

    @Override
    @Nullable
    public SpawnGroupData initialize(ServerLevelAccessor serverWorldAccess, DifficultyInstance difficulty, EntitySpawnReason spawnReason, @Nullable SpawnGroupData entityData) {
        entityData = super.initialize(serverWorldAccess, difficulty, spawnReason, entityData);
        RandomSource random = this.level().getRandom();
        this.initEquipment(random, difficulty);
        this.updateEnchantments(serverWorldAccess, random, difficulty);
        this.bowAttackGoal.setAttackInterval(40);
        this.goalSelector.addGoal(4, this.bowAttackGoal);
        if (spawnReason.equals(EntitySpawnReason.COMMAND) || spawnReason.equals(EntitySpawnReason.NATURAL) || spawnReason.equals(EntitySpawnReason.CHUNK_GENERATION)) {
            NightmareEntity nightmareEntity = EntityInit.NIGHTMARE.create(this.level());
            nightmareEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
            nightmareEntity.initialize(serverWorldAccess, difficulty, spawnReason, null);
            serverWorldAccess.spawnEntity(nightmareEntity);
            this.startRiding(nightmareEntity);
        }
        return entityData;
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    public static boolean canSpawn(EntityType<SoulReaperEntity> type, ServerLevelAccessor world, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return (Monster.checkMonsterSpawnRules(type, world, spawnReason, pos, random) && world.getBlockState(pos.above(3)).isAir()
                && world.getEntitiesOfClass(SoulReaperEntity.class, new AABB(pos).inflate(60D), EntitySelector.NO_SPECTATORS).isEmpty() && random.nextInt(7) == 0)
                || spawnReason == EntitySpawnReason.SPAWNER;
    }

    @Override
    public void tickRiding() {
        super.tickRiding();
        if (this.getVehicle() instanceof PathfinderMob) {
            PathfinderMob pathAwareEntity = (PathfinderMob) this.getVehicle();
            this.bodyYaw = pathAwareEntity.bodyYaw;
        }

    }

    @Override
    public int getLimitPerChunk() {
        return 1;
    }

    @Override
    public boolean canHaveStatusEffect(MobEffectInstance effect) {
        return effect.getEffectType() == MobEffects.WITHER ? false : super.canHaveStatusEffect(effect);
    }

    @Override
    public void initEquipment(RandomSource random, DifficultyInstance difficulty) {
        super.initEquipment(random, difficulty);
        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
    }

    @Override
    public boolean tryAttack(Entity target) {
        if (target instanceof LivingEntity livingEntity) {
            livingEntity.addStatusEffect(new MobEffectInstance(MobEffects.WITHER, 300));
        }
        return super.tryAttack(target);
    }

    @Override
    public void tickMovement() {
        if (this.level() != null && !this.level().isClient) {
            LivingEntity target = this.getTarget();
            if (target != null) {
                if (this.distanceTo(target) > 7F) {
                    if (this.getItemBySlot(EquipmentSlot.MAINHAND).getItem().equals(Items.NETHERITE_SWORD)) {
                        this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
                    }
                    this.goalSelector.addGoal(4, this.bowAttackGoal);
                    this.goalSelector.remove(this.meleeAttackGoal);
                } else {
                    this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                    this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));
                    this.goalSelector.addGoal(4, this.meleeAttackGoal);
                    this.goalSelector.remove(this.bowAttackGoal);
                }
            } else {
                if (this.getItemBySlot(EquipmentSlot.MAINHAND).getItem().equals(Items.BOW)) {
                    this.goalSelector.addGoal(4, this.bowAttackGoal);
                } else {
                    this.goalSelector.addGoal(4, this.meleeAttackGoal);
                }
            }
        }
        super.tickMovement();
    }

    @Override
    public void shootAt(LivingEntity target, float pullProgress) {
        ItemStack itemStack = this.getProjectileType(this.getItemInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW)));
        AbstractArrow persistentProjectileEntity = this.createArrowProjectile(itemStack, pullProgress);
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333D) - persistentProjectileEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = (double) Mth.sqrt((float) (d * d + f * f));
        persistentProjectileEntity.setVelocity(d, e + g * 0.21D, f, 1.3F, (float) (14 - this.level().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level().spawnEntity(persistentProjectileEntity);
    }

    public AbstractArrow createArrowProjectile(ItemStack arrow, float damageModifier) {
        ItemStack blackarrow = new ItemStack(Items.ARROW);
        ItemStack itemStack = this.getItemInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
        AbstractArrow persistentProjectileEntity = ProjectileUtil.createArrowProjectile(this, blackarrow, 4F, itemStack);
        if (persistentProjectileEntity instanceof Arrow arrowEntity) {
            arrowEntity.addEffect(new MobEffectInstance(MobEffects.WITHER, 200));
        }
        ((ProjectileEntityAccessor) persistentProjectileEntity).callSetPierceLevel((byte) 1);

        return persistentProjectileEntity;
    }

    @Override
    public boolean canUseRangedWeapon(ProjectileWeaponItem weapon) {
        return weapon == Items.BOW;
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        super.equipStack(slot, stack);
    }

    @Override
    public boolean canUsePortals(boolean allowVehicles) {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundInit.SOULREAPER_IDLE_EVENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundInit.SOULREAPER_HURT_EVENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundInit.SOULREAPER_DEATH_EVENT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WITHER_SKELETON_STEP, 0.5F, 1.0F);
    }

}
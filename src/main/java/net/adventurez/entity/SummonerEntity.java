package net.adventurez.entity;

import java.util.List;

import net.adventurez.init.EntityInit;
import net.adventurez.init.SoundInit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.ServerLevelAccessor;

public class SummonerEntity extends SpellCastingEntity {
    public static final EntityDataAccessor<Boolean> INVULNERABLE_SHIELD = SynchedEntityData.registerData(SummonerEntity.class, EntityDataSerializers.BOOLEAN);
    private int invulnerableMagicTick;
    private boolean gotShotByABow = false;

    public SummonerEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    public static AttributeSupplier.Builder createSummonerAttributes() {
        return Monster.createHostileAttributes().add(Attributes.GENERIC_MAX_HEALTH, 55.0D).add(Attributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                .add(Attributes.GENERIC_ATTACK_DAMAGE, 6.0D).add(Attributes.GENERIC_ATTACK_KNOCKBACK, 2.2D).add(Attributes.GENERIC_FOLLOW_RANGE, 35.0D);
    }

    @Override
    public void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new FloatGoal(this));
        this.goalSelector.add(1, new LookAtTargetGoalNecro());
        this.goalSelector.add(2, new NormalAttack(this, 1.0D, false));
        this.goalSelector.add(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.7D, 0.9D));
        this.goalSelector.add(4, new SummonPuppetGoal());
        this.goalSelector.add(5, new ThunderboltSpellGoal());
        this.goalSelector.add(6, new InvulnerableSpellGoal());
        this.goalSelector.add(7, new TeleportSpellGoal());
        this.goalSelector.add(8, new LookAtPlayerGoal(this, Player.class, 7.0F, 1.0F));
        this.goalSelector.add(9, new RandomStrollGoal(this, 0.9D));
        this.goalSelector.add(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.add(1, (new HurtByTargetGoal(this, new Class[] { SkeletonVanguardEntity.class })));
        this.targetSelector.add(2, (new HurtByTargetGoal(this, new Class[] { Zombie.class })));
        this.targetSelector.add(3, (new HurtByTargetGoal(this, new Class[] { Skeleton.class })));
        this.targetSelector.add(4, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setMaxTimeWithoutVisibility(300));
    }

    public static boolean canSpawn(EntityType<SummonerEntity> type, ServerLevelAccessor world, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return (canSpawnInDark(type, world, spawnReason, pos, random) && world.isSkyVisible(pos) && world.getLevelProperties().isRaining()) || spawnReason == EntitySpawnReason.SPAWNER;
    }

    @Override
    public void writeCustomDataToNbt(CompoundTag tag) {
        super.writeCustomDataToNbt(tag);
        tag.putInt("InvulnerableMagicTick", this.invulnerableMagicTick);
    }

    @Override
    public void readCustomDataFromNbt(CompoundTag tag) {
        super.readCustomDataFromNbt(tag);
        this.invulnerableMagicTick = tag.getInt("InvulnerableMagicTick");
    }

    @Override
    protected void initDataTracker(Builder builder) {
        super.initDataTracker(builder);
        builder.add(INVULNERABLE_SHIELD, false);
    }

    @Override
    public void mobTick() {
        super.mobTick();
        if (dataTracker.get(INVULNERABLE_SHIELD)) {
            this.setInvulnerable(true);
            this.getAttributeInstance(Attributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.0D);
            this.invulnerableMagicTick--;
            if (this.invulnerableMagicTick < 0) {
                this.setInvulnerable(false);
                this.getAttributeInstance(Attributes.GENERIC_MOVEMENT_SPEED).setBaseValue(0.3D);
                dataTracker.set(INVULNERABLE_SHIELD, false);
            }
        }

    }

    @Override
    public int getLimitPerChunk() {
        return 1;
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_WITHER_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SKELETON_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_WITHER_SKELETON_STEP, 0.5F, 0.7F);
    }

    @Override
    public SoundEvent getCastSpellSound() {
        return SoundEvents.ENTITY_EVOKER_CAST_SPELL;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        int chance = 0;
        if (source.is(DamageTypeTags.IS_LIGHTNING)) {
            return false;
        }
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            chance = this.level().getRandom().nextInt(2);
            this.gotShotByABow = true;
        }
        if (!source.is(DamageTypeTags.BYPASSES_INVULNERABILITY) &&chance == 1) {
            this.level().playSoundFromEntity(null, this, SoundInit.MAGIC_SHIELD_HIT_EVENT, SoundSource.HOSTILE, 1.0F, 1.0F);
            if (source.getSource() != null && source.getSource() instanceof Arrow) {
                if (!this.level().isClientSide()) {
                    source.getSource().discard();
                }
            }
            return false;
        } else {
            return !this.isInvulnerableTo(source) && super.damage(source, amount);
        }
    }

    private class ThunderboltSpellGoal extends SpellCastingEntity.CastSpellGoal {
        private ThunderboltSpellGoal() {
            super();
        }

        @Override
        protected int getInitialCooldown() {
            return 200;
        }

        @Override
        public int getSpellTicks() {
            return 20;
        }

        @Override
        public int startTimeDelay() {
            return 20;
        }

        @Override
        public boolean canStart() {
            LivingEntity attacker = SummonerEntity.this.getAttacker();
            if (super.canStart() && attacker != null && attacker.getMainHandItem().getItem() instanceof ProjectileWeaponItem && SummonerEntity.this.squaredDistanceTo(attacker) > 12
                    && SummonerEntity.this.gotShotByABow) {
                return true;
            } else
                return false;
        }

        @Override
        public void castSpell() {
            LivingEntity attacker = SummonerEntity.this.getAttacker();
            if (attacker != null) {
                ServerLevel serverWorld = (ServerLevel) attacker.level();
                double posX = attacker.getX() + SummonerEntity.this.level().getRandom().nextInt(3);
                double posY = attacker.getY();
                double posZ = attacker.getZ() + SummonerEntity.this.level().getRandom().nextInt(3);
                BlockPos pos = BlockPos.ofFloored(posX, posY, posZ);
                LightningBolt lightningEntity = (LightningBolt) EntityType.LIGHTNING_BOLT.create(attacker.level());
                lightningEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
                serverWorld.spawnEntity(lightningEntity);
            }
            gotShotByABow = false;
        }

        @Override
        public SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
        }

        @Override
        public SpellCastingEntity.Spell getSpell() {
            return SpellCastingEntity.Spell.THUNDERBOLT;
        }
    }

    private class TeleportSpellGoal extends SpellCastingEntity.CastSpellGoal {
        private TeleportSpellGoal() {
            super();
        }

        @Override
        protected int getInitialCooldown() {
            return 200;
        }

        @Override
        public int getSpellTicks() {
            return 40;
        }

        @Override
        public int startTimeDelay() {
            return 60;
        }

        @Override
        public boolean canStart() {
            if (!super.canStart()) {
                return false;
            } else
                return true;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void castSpell() {
            for (int counter = 0; counter < 100; counter++) {
                float randomFloat = SummonerEntity.this.level().getRandom().nextFloat() * 6.2831855F;
                int posX = SummonerEntity.this.getBlockPos().getX() + Mth.floor(Mth.cos(randomFloat) * 8.0F + SummonerEntity.this.level().getRandom().nextInt(12));
                int posZ = SummonerEntity.this.getBlockPos().getZ() + Mth.floor(Mth.sin(randomFloat) * 8.0F + SummonerEntity.this.level().getRandom().nextInt(12));
                int posY = SummonerEntity.this.level().getTopY(Heightmap.Type.WORLD_SURFACE, posX, posZ);
                BlockPos teleportPos = new BlockPos(posX, posY, posZ);
                if (SummonerEntity.this.level().isRegionLoaded(teleportPos.getX() - 4, teleportPos.getY() - 4, teleportPos.getZ() - 4, teleportPos.getX() + 4, teleportPos.getY() + 4,
                        teleportPos.getZ() + 4)
                        && SpawnPlacements.canSpawn(EntityInit.SUMMONER, (ServerLevel) SummonerEntity.this.level(), EntitySpawnReason.EVENT, teleportPos, SummonerEntity.this.getRandom())) {
                    SummonerEntity.this.teleport(teleportPos.getX(), teleportPos.getY(), teleportPos.getZ(), true);
                    break;
                }
            }
            SummonerEntity.this.playSpawnEffects();

        }

        @Override
        public SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
        }

        @Override
        public SpellCastingEntity.Spell getSpell() {
            return SpellCastingEntity.Spell.TELEPORT;
        }
    }

    private class InvulnerableSpellGoal extends SpellCastingEntity.CastSpellGoal {
        private InvulnerableSpellGoal() {
            super();
        }

        @Override
        protected int getInitialCooldown() {
            return 420;
        }

        @Override
        public int getSpellTicks() {
            return 40;
        }

        @Override
        public int startTimeDelay() {
            return 60;
        }

        @Override
        public boolean canStart() {
            if (!super.canStart() || (SummonerEntity.this.getTarget() != null && SummonerEntity.this.squaredDistanceTo(SummonerEntity.this.getTarget()) > 7D)) {
                return false;
            } else
                return true;
        }

        @Override
        public void tick() {
            --this.spellCooldown;
            if (this.spellCooldown == 0) {
                this.castSpell();
                SummonerEntity.this.playSound(SoundInit.SPELL_CAST_SHIELD_EVENT, 1.0F, 1.0F);
            }

        }

        @Override
        public void castSpell() {
            dataTracker.set(INVULNERABLE_SHIELD, true);
            SummonerEntity.this.invulnerableMagicTick = 160;
            for (int i = 0; i < 60; ++i) {
                ((ServerLevel) SummonerEntity.this.level()).spawnParticles(ParticleTypes.END_ROD, SummonerEntity.this.getParticleX(1.5D), SummonerEntity.this.getRandomBodyY(),
                        SummonerEntity.this.getParticleZ(1.5D), 0, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            if (SummonerEntity.this.getTarget() != null) {
                SummonerEntity.this.getTarget().addStatusEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 1, false, false));
            }

        }

        @Override
        public SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
        }

        @Override
        public SpellCastingEntity.Spell getSpell() {
            return SpellCastingEntity.Spell.SHIELD;
        }
    }

    private class SummonPuppetGoal extends SpellCastingEntity.CastSpellGoal {
        private final TargetingConditions PUPPET_PREDICATE = TargetingConditions.createAttackable().setBaseMaxDistance(24.0D).ignoreVisibility().ignoreDistanceScalingFactor();

        private SummonPuppetGoal() {
            super();

        }

        @Override
        public boolean canStart() {
            if (!super.canStart() || tooManySlaves()) {
                return false;
            } else {
                int i = SummonerEntity.this.level().getTargets(WitherPuppetEntity.class, this.PUPPET_PREDICATE, SummonerEntity.this, SummonerEntity.this.getBoundingBox().inflate(16.0D)).size();
                return SummonerEntity.this.getRandom().nextInt(8) + 1 > i;
            }
        }

        @Override
        public int getSpellTicks() {
            return 140;
        }

        @Override
        public int startTimeDelay() {
            return 340;
        }

        @Override
        public void castSpell() {
            ServerLevel serverWorld = (ServerLevel) SummonerEntity.this.level();
            int spellCount = 0;
            for (int i = 0; i < 20; ++i) {
                BlockPos blockPos = SummonerEntity.this.getBlockPos().add(-2 + serverWorld.getRandom().nextInt(5), serverWorld.getRandom().nextInt(3), -2 + serverWorld.getRandom().nextInt(5));
                if (SpawnPlacements.canSpawn(EntityInit.SKELETON_VANGUARD, serverWorld, EntitySpawnReason.EVENT, blockPos, serverWorld.getRandom())) {
                    spellCount++;
                    if (SummonerEntity.this.getHealth() <= 40.0F || SummonerEntity.this.getEntityWorld().isDay()) {
                        SkeletonVanguardEntity skeletonVanguardEntity = EntityInit.SKELETON_VANGUARD.create(serverWorld);
                        skeletonVanguardEntity.refreshPositionAndAngles(blockPos, serverWorld.getRandom().nextFloat() * 360F, 0.0F);
                        skeletonVanguardEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(blockPos), EntitySpawnReason.EVENT, null);
                        serverWorld.spawnEntityAndPassengers(skeletonVanguardEntity);
                    } else {
                        Zombie zombieEntity = EntityType.ZOMBIE.create(serverWorld);
                        zombieEntity.refreshPositionAndAngles(blockPos, serverWorld.getRandom().nextFloat() * 360F, 0.0F);
                        zombieEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(blockPos), EntitySpawnReason.EVENT, null);
                        serverWorld.spawnEntityAndPassengers(zombieEntity);
                        int skeletonChance = serverWorld.getRandom().nextInt(8);
                        if (skeletonChance == 0) {
                            Skeleton skeletonEntity = EntityType.SKELETON.create(serverWorld);
                            skeletonEntity.refreshPositionAndAngles(blockPos, serverWorld.getRandom().nextFloat() * 360F, 0.0F);
                            skeletonEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(blockPos), EntitySpawnReason.EVENT, null);
                            if (SummonerEntity.this.gotShotByABow) {
                                skeletonEntity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
                            }
                            serverWorld.spawnEntityAndPassengers(skeletonEntity);
                        }
                    }
                }
                if (spellCount >= 3)
                    break;
            }

        }

        @Override
        public SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
        }

        @Override
        public SpellCastingEntity.Spell getSpell() {
            return SpellCastingEntity.Spell.SUMMON_PUPPET;
        }
    }

    private class LookAtTargetGoalNecro extends SpellCastingEntity.LookAtTargetGoal {
        private LookAtTargetGoalNecro() {
            super();
        }

        @Override
        public void tick() {
            if (SummonerEntity.this.getTarget() != null) {
                SummonerEntity.this.getLookControl().lookAt(SummonerEntity.this.getTarget(), (float) SummonerEntity.this.getMaxHeadRotation(), (float) SummonerEntity.this.getMaxLookPitchChange());
            }

        }
    }

    private boolean tooManySlaves() {
        List<LivingEntity> list = this.level().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().inflate(40D), EntitySelector.EXCEPT_SPECTATOR);
        if (!list.isEmpty()) {
            int vanguards = 0;
            int othermobs = 0;
            for (int i = 0; i < list.size(); i++) {
                LivingEntity entity = (LivingEntity) list.get(i);
                if (entity.getType() == EntityInit.SKELETON_VANGUARD) {
                    vanguards++;
                } else if (entity.getType() == EntityType.ZOMBIE || entity.getType() == EntityType.SKELETON) {
                    othermobs++;
                }
                if (vanguards >= 5 || othermobs >= 7) {
                    return true;
                }
            }
        }
        return false;
    }

    private class NormalAttack extends MeleeAttackGoal {
        private final SummonerEntity summonerEntity;

        public NormalAttack(PathfinderMob mob, double speed, boolean pauseWhenMobIdle) {
            super(mob, speed, pauseWhenMobIdle);
            this.summonerEntity = (SummonerEntity) mob;
        }

        @Override
        public boolean canStart() {
            LivingEntity livingEntity = this.summonerEntity.getTarget();
            return livingEntity != null && livingEntity.isAlive() && this.summonerEntity.canTarget(livingEntity) && this.summonerEntity.squaredDistanceTo(livingEntity) < 7.0D && super.canStart();
        }

        @Override
        public boolean shouldContinue() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity == null || this.summonerEntity.squaredDistanceTo(livingEntity) > 5D) {
                return false;
            } else if (!livingEntity.isAlive()) {
                return false;
            } else if (!this.mob.isInWalkTargetRange(livingEntity.getBlockPos())) {
                return false;
            } else {
                return !(livingEntity instanceof Player player) || !livingEntity.isSpectator() && !player.isCreative();
            }
        }

    }

}

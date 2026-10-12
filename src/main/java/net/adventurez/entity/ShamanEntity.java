package net.adventurez.entity;

import java.util.List;

import net.adventurez.init.SoundInit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnPlacements;
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
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.monster.spider.CaveSpider;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.skeleton.Skeleton;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class ShamanEntity extends SpellCastingEntity {

    public ShamanEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
    }

    public static AttributeSupplier.Builder createShamanAttributes() {
        return Monster.createHostileAttributes().add(Attributes.GENERIC_MAX_HEALTH, 40.0D).add(Attributes.GENERIC_MOVEMENT_SPEED, 0.3D)
                .add(Attributes.GENERIC_ATTACK_DAMAGE, 6.0D).add(Attributes.GENERIC_ATTACK_KNOCKBACK, 1.0D).add(Attributes.GENERIC_FOLLOW_RANGE, 35.0D);
    }

    @Override
    public void initGoals() {
        super.initGoals();
        this.goalSelector.add(0, new FloatGoal(this));
        this.goalSelector.add(1, new LookAtTargetGoalShaman());
        this.goalSelector.add(2, new NormalAttack(this, 1.0D, false));
        this.goalSelector.add(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.7D, 0.9D));
        this.goalSelector.add(4, new SummonCompanionsGoal());
        this.goalSelector.add(5, new ThunderboltSpellGoal());
        this.goalSelector.add(7, new EffectSpellGoal());
        this.goalSelector.add(8, new LookAtPlayerGoal(this, Player.class, 7.0F, 1.0F));
        this.goalSelector.add(9, new RandomStrollGoal(this, 0.9D));
        this.goalSelector.add(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.add(1, (new HurtByTargetGoal(this, new Class[] { Witch.class })));
        this.targetSelector.add(2, (new HurtByTargetGoal(this, new Class[] { Zombie.class })));
        this.targetSelector.add(3, (new HurtByTargetGoal(this, new Class[] { Skeleton.class })));
        this.targetSelector.add(3, (new HurtByTargetGoal(this, new Class[] { Spider.class })));
        this.targetSelector.add(3, (new HurtByTargetGoal(this, new Class[] { CaveSpider.class })));
        this.targetSelector.add(4, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setMaxTimeWithoutVisibility(300));
    }

    public static boolean canSpawn(EntityType<ShamanEntity> type, ServerLevelAccessor world, EntitySpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return (canSpawnInDark(type, world, spawnReason, pos, random) && world.isSkyVisible(pos)) || spawnReason == EntitySpawnReason.SPAWNER;

    }

    @Override
    public int getLimitPerChunk() {
        return 1;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundInit.SHAMAN_IDLE_EVENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundInit.SHAMAN_HURT_EVENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundInit.SHAMAN_DEATH_EVENT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundInit.SHAMAN_WALK_EVENT, 0.5F, 1.0F);
    }

    @Override
    public SoundEvent getCastSpellSound() {
        return SoundEvents.ENTITY_EVOKER_CAST_SPELL;
    }

    private boolean tooManyCompanions() {
        List<LivingEntity> list = this.level().getEntitiesByClass(LivingEntity.class, this.getBoundingBox().inflate(40D), EntitySelector.EXCEPT_SPECTATOR);
        if (!list.isEmpty()) {
            int spiders = 0;
            int othermobs = 0;
            for (int i = 0; i < list.size(); i++) {
                LivingEntity entity = (LivingEntity) list.get(i);
                if (entity.getType() == EntityType.CAVE_SPIDER) {
                    spiders++;
                } else if (entity.getType() == EntityType.ZOMBIE || entity.getType() == EntityType.SKELETON || entity.getType() == EntityType.SPIDER) {
                    othermobs++;
                }
                if (spiders >= 2 || othermobs >= 4) {
                    return true;
                }
            }
        }
        return false;
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
            LivingEntity attacker = ShamanEntity.this.getAttacker();
            if (super.canStart() && attacker != null && attacker.getMainHandItem().getItem() instanceof ProjectileWeaponItem && ShamanEntity.this.squaredDistanceTo(attacker) > 12
                    && ShamanEntity.this.level().isRaining()) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void castSpell() {
            LivingEntity attacker = ShamanEntity.this.getAttacker();
            if (attacker != null) {
                ServerLevel serverWorld = (ServerLevel) attacker.level();
                double posX = attacker.getX() + ShamanEntity.this.level().getRandom().nextInt(3);
                double posY = attacker.getY();
                double posZ = attacker.getZ() + ShamanEntity.this.level().getRandom().nextInt(3);
                BlockPos pos = BlockPos.ofFloored(posX, posY, posZ);
                LightningBolt lightningEntity = (LightningBolt) EntityType.LIGHTNING_BOLT.create(attacker.level());
                lightningEntity.refreshPositionAndAngles(pos, 0.0F, 0.0F);
                serverWorld.spawnEntity(lightningEntity);
            }
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

    private class EffectSpellGoal extends SpellCastingEntity.CastSpellGoal {
        private EffectSpellGoal() {
            super();
        }

        @Override
        protected int getInitialCooldown() {
            return 200;
        }

        @Override
        public int getSpellTicks() {
            return 60;
        }

        @Override
        public int startTimeDelay() {
            return 10;
        }

        @Override
        public boolean canStart() {
            if (!super.canStart()) {
                return false;
            } else
                return true;
        }

        @Override
        public void castSpell() {
            LivingEntity livingEntity = ShamanEntity.this.getTarget();
            if (livingEntity != null) {
                Holder<MobEffect> statusEffect;
                if (ShamanEntity.this.getRandom().nextFloat() <= 0.5F) {
                    statusEffect = MobEffects.WEAKNESS;
                } else {
                    statusEffect = MobEffects.POISON;
                }
                livingEntity.addStatusEffect(new MobEffectInstance(statusEffect, 120 + ShamanEntity.this.getRandom().nextInt(80), 1, false, false, true));
            }

        }

        @Override
        public SoundEvent getSoundPrepare() {
            return SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON;
        }

        @Override
        public SpellCastingEntity.Spell getSpell() {
            return SpellCastingEntity.Spell.WITHERING;
        }
    }

    private class SummonCompanionsGoal extends SpellCastingEntity.CastSpellGoal {
        private SummonCompanionsGoal() {
            super();
        }

        @Override
        public boolean canStart() {
            if (!super.canStart() || tooManyCompanions()) {
                return false;
            } else
                return true;
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
            ServerLevel serverWorld = (ServerLevel) ShamanEntity.this.level();
            int spellCount = 0;
            for (int i = 0; i < 20; ++i) {
                BlockPos blockPos = ShamanEntity.this.getBlockPos().add(-3 + ShamanEntity.this.getRandom().nextInt(6), ShamanEntity.this.getRandom().nextInt(3),
                        -3 + ShamanEntity.this.getRandom().nextInt(6));
                if (SpawnPlacements.canSpawn(EntityType.ZOMBIE, serverWorld, EntitySpawnReason.EVENT, blockPos, serverWorld.getRandom())) {
                    spellCount++;
                    if (ShamanEntity.this.getRandom().nextFloat() < 0.6F) {
                        CaveSpider caveSpiderEntity = EntityType.CAVE_SPIDER.create(serverWorld);
                        caveSpiderEntity.refreshPositionAndAngles(blockPos, serverWorld.getRandom().nextFloat() * 360F, 0.0F);
                        caveSpiderEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(blockPos), EntitySpawnReason.EVENT, null);
                        serverWorld.spawnEntityAndPassengers(caveSpiderEntity);
                    } else {
                        Mob mobEntity;
                        int randomInt = ShamanEntity.this.getRandom().nextInt(3);
                        if (randomInt == 0) {
                            mobEntity = EntityType.ZOMBIE.create(serverWorld);
                        } else if (randomInt == 1) {
                            mobEntity = EntityType.SKELETON.create(serverWorld);
                        } else {
                            mobEntity = EntityType.SPIDER.create(serverWorld);
                        }
                        mobEntity.refreshPositionAndAngles(blockPos, serverWorld.getRandom().nextFloat() * 360F, 0.0F);
                        mobEntity.initialize(serverWorld, serverWorld.getLocalDifficulty(blockPos), EntitySpawnReason.EVENT, null);
                        serverWorld.spawnEntityAndPassengers(mobEntity);
                    }
                }
                if (spellCount >= 3) {
                    break;
                }
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

    private class LookAtTargetGoalShaman extends SpellCastingEntity.LookAtTargetGoal {
        private LookAtTargetGoalShaman() {
            super();
        }

        @Override
        public void tick() {
            if (ShamanEntity.this.getTarget() != null) {
                ShamanEntity.this.getLookControl().lookAt(ShamanEntity.this.getTarget(), (float) ShamanEntity.this.getMaxHeadRotation(), (float) ShamanEntity.this.getMaxLookPitchChange());
            }

        }
    }

    private class NormalAttack extends MeleeAttackGoal {
        private final ShamanEntity summonerEntity;

        public NormalAttack(PathfinderMob mob, double speed, boolean pauseWhenMobIdle) {
            super(mob, speed, pauseWhenMobIdle);
            this.summonerEntity = (ShamanEntity) mob;
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
                return !(livingEntity instanceof Player) || !livingEntity.isSpectator() && !((Player) livingEntity).isCreative();
            }
        }

    }

}

package net.adventurez.entity;

import net.adventurez.init.ConfigInit;
import net.adventurez.init.SoundInit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.entity.*;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class OrcEntity extends Monster {
    public static final EntityDataAccessor<Integer> ORK_SIZE;
    public static final EntityDataAccessor<Boolean> DOUBLE_HAND_ATTACK;
    public static final EntityDataAccessor<Integer> INVENTORY_ITEM_ID;

    public final SimpleContainer inventory = new SimpleContainer(1);

    @SuppressWarnings("deprecation")
    public OrcEntity(EntityType<? extends Monster> entityType, Level world) {
        super(entityType, world);
        this.reinitDimensions();
    }

    public static AttributeSupplier.Builder createOrcAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.225D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2D).add(Attributes.ATTACK_DAMAGE, 7.0D).add(Attributes.ATTACK_KNOCKBACK, 1D)
                .add(Attributes.FOLLOW_RANGE, 38.0D).add(Attributes.ARMOR, 1.0D);
    }

    @Override
    public void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(0, new SwimGoal(this));
        this.goalSelector.addGoal(1, new OrcEscapeGoal(this, 1.4D));
        this.goalSelector.addGoal(2, new OrcAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(3, new OrcGroupGoal(this));
        this.goalSelector.addGoal(4, new WanderAroundVeryFarGoal(this, 0.9D, 0.7F));
        this.goalSelector.addGoal(5, new LookAtEntityGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new LookAroundGoal(this));
        this.goalSelector.addGoal(7, new WanderAroundGoal(this, 0.9D));
        this.targetSelector.addGoal(1, new ActiveTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(2, new ActiveTargetGoal<>(this, WanderingTrader.class, true));
        this.targetSelector.addGoal(3, new ActiveTargetGoal<>(this, Villager.class, true));
        this.targetSelector.addGoal(4, (new RevengeGoal(this, new Class[]{OrcEntity.class})));
    }

    public static boolean canSpawn(EntityType<OrcEntity> type, ServerLevelAccessor world, SpawnReason spawnReason, BlockPos pos, RandomSource random) {
        return (Monster.checkMonsterSpawnRules(type, world, spawnReason, pos, random) && world.isSkyVisible(pos)) || spawnReason == SpawnReason.SPAWNER;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ORK_SIZE, 1);
        builder.define(DOUBLE_HAND_ATTACK, false);
        builder.define(INVENTORY_ITEM_ID, -1);
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        this.setSize(input.getIntOr("OrkSize", 1), false);
        input.read("OrcItem", ItemStack.CODEC).ifPresent(stack -> {
            this.inventory.setStack(0, stack);
            this.setItemId(stack.getItem());
        });
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("OrkSize", this.getSize());
        if (!this.inventory.getStack(0).isEmpty()) {
            output.store("OrcItem", ItemStack.CODEC, this.inventory.getStack(0));
        }
    }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) {
        if (!this.inventory.getStack(0).isEmpty()) {
            return false;
        }
        return true;
    }

    public int getSize() {
        return (Integer) this.entityData.get(ORK_SIZE);
    }

    public void setSize(int size, boolean healOrc) {
        this.entityData.set(ORK_SIZE, size);
        this.refreshPosition();
        this.calculateDimensions();
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double) (16D + (float) size * 6D));
        this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue((double) (0.26F - 0.012F * (float) size));
        this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue((double) size * 3D + 2D);
        if (healOrc) {
            this.setHealth(this.getMaxHealth());
        }
    }

    @Override
    public void onTrackedDataSet(EntityDataAccessor<?> data) {
        if (ORK_SIZE.equals(data)) {
            this.calculateDimensions();
            this.setYaw(this.headYaw);
            this.bodyYaw = this.headYaw;
        }

        super.onTrackedDataSet(data);
    }

    @Override
    protected float getSoundVolume() {
        return 0.3F * (float) this.getSize();
    }

    @Override
    public float getSoundPitch() {
        return 1.6F - ((float) 0.2F * this.getSize());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundInit.ORC_IDLE_EVENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundInit.ORC_HURT_EVENT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundInit.ORC_DEATH_EVENT;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundInit.ORC_STEP_EVENT, 1.0F, 1.0F);
    }

    @Nullable
    @Override
    public EntityData initialize(ServerLevelAccessor world, DifficultyInstance difficulty, SpawnReason spawnReason, @Nullable EntityData entityData) {
        this.setSize(this.getRandom().nextInt(3) + 1, true);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    protected EntityDimensions getBaseDimensions(EntityPose pose) {
        return super.getBaseDimensions(pose).scaled(0.55F * (float) this.getSize());
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.level().getRandom().nextInt(3) == 0 && !this.entityData.get(DOUBLE_HAND_ATTACK)) {
            entityData.set(DOUBLE_HAND_ATTACK, true);
        } else {
            entityData.set(DOUBLE_HAND_ATTACK, false);
        }
        return !this.isInvulnerableTo(source) && super.damage(source, amount);
    }

    @Override
    public int getLimitPerChunk() {
        return 4;
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
        if (this.inventory != null && !this.inventory.isEmpty()) {
            this.dropStack(this.inventory.getStack(0));
        }
    }

    public boolean isBigOrc() {
        if (this.getSize() == 3) {
            return true;
        } else {
            return false;
        }
    }

    private void setItemId(Item item) {
        this.entityData.set(INVENTORY_ITEM_ID, BuiltInRegistries.ITEM.getRawId(item));
    }

    static {
        ORK_SIZE = SynchedEntityData.defineId(OrcEntity.class, EntityDataSerializers.INTEGER);
        DOUBLE_HAND_ATTACK = SynchedEntityData.defineId(OrcEntity.class, EntityDataSerializers.BOOLEAN);
        INVENTORY_ITEM_ID = SynchedEntityData.defineId(OrcEntity.class, EntityDataSerializers.INTEGER);
    }

    private class OrcAttackGoal extends MeleeAttackGoal {
        private final OrcEntity orcEntity;

        public OrcAttackGoal(OrcEntity orcEntity, double speed, boolean pauseWhenMobIdle) {
            super(orcEntity, speed, pauseWhenMobIdle);
            this.orcEntity = orcEntity;
        }

        @Override
        protected void attack(LivingEntity target) {
            if (canAttack(target) && this.getCooldown() <= 0) {
                this.resetCooldown();
                this.mob.swingHand(InteractionHand.MAIN_HAND);
                this.mob.tryAttack(target);
                if (ConfigInit.CONFIG.allow_orc_stealing && !this.orcEntity.isBigOrc() && this.orcEntity.inventory.isEmpty() && this.orcEntity.level().getRandom().nextFloat() <= 0.3F && target instanceof Player playerEntity) {
                    for (int i = 45; i > 0; i--) {
                        if (!playerEntity.getInventory().getStack(i).isEmpty() && playerEntity.getInventory().getStack(i).getItem() instanceof TieredItem
                                && playerEntity.level().getRandom().nextFloat() < 0.3F) {
                            this.orcEntity.inventory.setStack(0, playerEntity.getInventory().getStack(i));
                            this.orcEntity.setItemId(this.orcEntity.inventory.getStack(0).getItem());
                            playerEntity.getInventory().setStack(i, ItemStack.EMPTY);
                            break;
                        }
                    }

                }
            }

        }
    }

    private class OrcGroupGoal extends Goal {
        private final OrcEntity orcEntity;
        private int moveDelay;
        private int checkSurroundingDelay;
        private OrcEntity orcLeader;

        public OrcGroupGoal(OrcEntity orcEntity) {
            this.orcEntity = orcEntity;
            this.checkSurroundingDelay = this.getSurroundingSearchDelay(orcEntity);
        }

        private int getSurroundingSearchDelay(OrcEntity orcEntity) {
            return 200 + orcEntity.getRandom().nextInt(200) % 20;
        }

        private boolean isCloseEnoughToOrcLeader() {
            return this.orcEntity.distanceTo(this.orcLeader) <= 10.0F;
        }

        @Override
        public boolean canUse() {
            if (this.orcEntity.getSize() == 3 || this.orcEntity.isAttacking()) {
                return false;
            }
            if (this.orcLeader != null && this.isCloseEnoughToOrcLeader()) {
                return false;
            }
            if (this.checkSurroundingDelay > 0) {
                --this.checkSurroundingDelay;
                return false;
            } else {
                List<? extends OrcEntity> list = orcEntity.level().getNonSpectatingEntities(orcEntity.getClass(), orcEntity.getBoundingBox().inflate(30.0D, 8.0D, 30.0D));
                if (list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getSize() == 3) {
                            this.orcLeader = list.get(i);
                            return true;
                        }
                    }
                    this.checkSurroundingDelay = this.getSurroundingSearchDelay(orcEntity);
                }
            }
            return false;
        }

        @Override
        public boolean canContinueToUse() {
            if (this.orcLeader != null || this.orcEntity.getAttacker() == null) {
                return !this.isCloseEnoughToOrcLeader();
            } else
                return false;
        }

        @Override
        public void start() {
            this.moveDelay = 0;
        }

        @Override
        public void stop() {
            this.checkSurroundingDelay = this.getSurroundingSearchDelay(orcEntity);
        }

        @Override
        public void tick() {
            if (--this.moveDelay <= 0) {
                this.moveDelay = 20;
                if (this.orcLeader != null) {
                    this.orcEntity.getNavigation().startMovingTo(this.orcLeader, 1.0D);
                }
            }
        }
    }

    public class WanderAroundVeryFarGoal extends WanderAroundGoal {
        protected final float probability;
        private final OrcEntity orcEntity;

        public WanderAroundVeryFarGoal(OrcEntity orcEntity, double speed, float probability) {
            super(orcEntity, speed);
            this.probability = probability;
            this.orcEntity = orcEntity;
        }

        @Nullable
        @Override
        protected Vec3 getWanderTarget() {
            if (this.orcEntity.isInsideWaterOrBubbleColumn()) {
                Vec3 vec3d = DefaultRandomPos.find(this.orcEntity, 15, 7);
                return vec3d == null ? super.getWanderTarget() : vec3d;
            } else {
                return this.orcEntity.getRandom().nextFloat() >= this.probability && orcEntity.isBigOrc() ? DefaultRandomPos.find(this.orcEntity, 30, 5) : super.getWanderTarget();
            }
        }
    }

    private class OrcEscapeGoal extends Goal {
        protected final OrcEntity orcEntity;
        protected final double speed;
        protected double targetX;
        protected double targetY;
        protected double targetZ;

        public OrcEscapeGoal(OrcEntity orcEntity, double speed) {
            this.orcEntity = orcEntity;
            this.speed = speed;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (this.orcEntity.isBigOrc() || this.orcEntity.inventory.isEmpty() || this.orcEntity.level().getClosestPlayer(this.orcEntity, 16D) == null) {
                return false;
            } else {
                return this.findTarget();
            }
        }

        protected boolean findTarget() {
            Vec3 vec3d = DefaultRandomPos.find(this.orcEntity, 14, 4);
            if (vec3d == null) {
                return false;
            } else {
                this.targetX = vec3d.x;
                this.targetY = vec3d.y;
                this.targetZ = vec3d.z;
                return true;
            }
        }

        @Override
        public void start() {
            this.orcEntity.getNavigation().startMovingTo(this.targetX, this.targetY, this.targetZ, this.speed);
        }

        @Override
        public boolean canContinueToUse() {
            if (this.orcEntity.level().getClosestPlayer(this.orcEntity, 18D) == null) {
                return false;
            } else {
                return !this.orcEntity.getNavigation().isIdle();
            }
        }

    }

}

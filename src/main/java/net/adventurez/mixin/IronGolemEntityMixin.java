package net.adventurez.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.adventurez.access.EntityAccess;
import net.adventurez.init.ItemInit;
import net.adventurez.init.SoundInit;
import net.adventurez.init.TagInit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;

@Mixin(IronGolem.class)
public abstract class IronGolemEntityMixin extends AbstractGolem implements EntityAccess {

    private static final EntityDataAccessor<Boolean> BLACKSTONED = SynchedEntityData.registerData(IronGolem.class, EntityDataSerializers.BOOLEAN);

    public IronGolemEntityMixin(EntityType<? extends AbstractGolem> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "initDataTracker", at = @At(value = "TAIL"))
    protected void initDataTrackerMixin(SynchedEntityData.Builder builder, CallbackInfo info) {
        builder.add(BLACKSTONED, false);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "TAIL"))
    private void writeCustomDataToNbtMixin(CompoundTag nbt, CallbackInfo info) {
        nbt.putBoolean("Blackstoned", this.dataTracker.get(BLACKSTONED));
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "TAIL"))
    private void readCustomDataFromNbtMixin(CompoundTag nbt, CallbackInfo info) {
        this.dataTracker.set(BLACKSTONED, nbt.getBoolean("Blackstoned"));
        if (nbt.getBoolean("Blackstoned")) {
            this.targetSelector.add(3, new NearestAttackableTargetGoal<>(this, Creeper.class, false, true));
        }
    }

    @Inject(method = "interactMob", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/player/Player;getStackInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    protected void interactMobMixin(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info, ItemStack itemStack) {
        if (this.dataTracker.get(BLACKSTONED)) {
            if (itemStack.getItem() instanceof BlockItem blockItem) {
                if (net.adventurez.util.MinecraftCompat.isBlockInTag(blockItem.getBlock().defaultBlockState(), TagInit.PLATFORM_NETHER_BLOCKS)) {
                    this.heal(10.0F);
                    info.setReturnValue(InteractionResult.success(this.level().isClientSide()));
                }
            } else if (net.adventurez.util.MinecraftCompat.isItem(itemStack, ItemInit.GILDED_BLACKSTONE_SHARD)) {
                this.heal(this.getMaxHealth() * 0.2F);
                info.setReturnValue(InteractionResult.success(this.level().isClientSide()));
            } else if (net.adventurez.util.MinecraftCompat.isItem(itemStack, ItemInit.BLACKSTONE_GOLEM_HEART)) {
                this.heal(this.getMaxHealth());
                info.setReturnValue(InteractionResult.success(this.level().isClientSide()));
            }
        } else if (net.adventurez.util.MinecraftCompat.isItem(itemStack, ItemInit.BLACKSTONE_GOLEM_HEART)) {
            if (!this.level().isClientSide()) {
                this.targetSelector.add(3, new NearestAttackableTargetGoal<>(this, Creeper.class, false, true));
                this.dataTracker.set(BLACKSTONED, true);
                this.getAttributeInstance(Attributes.GENERIC_MAX_HEALTH).setBaseValue(this.getAttributeBaseValue(Attributes.GENERIC_MAX_HEALTH) + 100.0D);
                this.getAttributeInstance(Attributes.GENERIC_ATTACK_DAMAGE).setBaseValue(this.getAttributeBaseValue(Attributes.GENERIC_ATTACK_DAMAGE) + 5.0D);
                this.getAttributeInstance(Attributes.GENERIC_MOVEMENT_SPEED).setBaseValue(this.getAttributeBaseValue(Attributes.GENERIC_MOVEMENT_SPEED) + 0.04D);
                this.setHealth(this.getMaxHealth());
                itemStack.decrement(1);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundInit.GOLEM_AWAKENS_EVENT, SoundSource.NEUTRAL, 1.4F, 1.0F);
            info.setReturnValue(InteractionResult.success(this.level().isClientSide()));
        }
    }

    @Inject(method = "canTarget", at = @At(value = "HEAD"), cancellable = true)
    private void canTargetMixin(EntityType<?> type, CallbackInfoReturnable<Boolean> info) {
        if (type == EntityType.CREEPER && this.dataTracker.get(BLACKSTONED) == true) {
            info.setReturnValue(true);
        }
    }

    @Override
    public EntityDataAccessor<Boolean> getTrackedDataBoolean() {
        return IronGolemEntityMixin.BLACKSTONED;
    }

}

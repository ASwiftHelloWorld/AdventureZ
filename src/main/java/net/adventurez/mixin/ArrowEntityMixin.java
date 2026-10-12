package net.adventurez.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.adventurez.init.ItemInit;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(Arrow.class)
public abstract class ArrowEntityMixin extends AbstractArrow {

    @Shadow
    @Mutable
    @Final
    private static EntityDataAccessor<Integer> COLOR;

    public ArrowEntityMixin(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "Lnet/minecraft/world/entity/projectile/arrow/Arrow;<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V", at = @At("TAIL"))
    private void initMixin(Level world, LivingEntity owner, ItemStack stack, @Nullable ItemStack shotFrom, CallbackInfo info) {
        if (net.adventurez.util.MinecraftCompat.isItem(stack, ItemInit.IVORY_ARROW)) {
            this.setDamage(this.getDamage() + 4.0D);
        }
    }

    @Inject(method = "spawnParticles", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/projectile/arrow/Arrow;getColor()I"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void spawnParticlesMixin(int amount, CallbackInfo info, int i) {
        if (i == 14340520) {
            info.cancel();
        }
    }

    @Inject(method = "initColor", at = @At("TAIL"), cancellable = true)
    private void initColorMixin(CallbackInfo info) {
        if (net.adventurez.util.MinecraftCompat.isItem(this.getItemStack(), ItemInit.IVORY_ARROW)) {
            this.dataTracker.set(COLOR, 14340520);
        }
    }

}

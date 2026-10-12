package net.adventurez.item;

import java.util.List;

import net.adventurez.entity.nonliving.ThrownRockEntity;
import net.adventurez.init.ConfigInit;
import net.adventurez.init.ItemInit;
import net.adventurez.init.SoundInit;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class BlackstoneGolemArm extends Item {

    public BlackstoneGolemArm(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (ConfigInit.CONFIG.allow_extra_tooltips) {
            tooltip.add(Component.translatable("item.adventurez.moreinfo.tooltip"));
            if (InputConstants.isKeyPressed(Minecraft.getInstance().getWindow().getHandle(), 340)) {
                tooltip.remove(Component.translatable("item.adventurez.moreinfo.tooltip"));
                tooltip.add(Component.translatable("item.adventurez.blackstone_golem_arm.tooltip"));
            }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof Player playerEntity) {
            int stoneCounter = this.getMaxUseTime(stack, user) - remainingUseTicks;
            if (stoneCounter >= 30) {
                stack.set(ItemInit.LAVA_LIGHT, false);
                if (!world.isClientSide()) {
                    float strength = getStoneStrength(stoneCounter);
                    stack.damage(1, playerEntity, LivingEntity.getSlotForHand(user.getActiveHand()));
                    ThrownRockEntity thrownRockEntity = new ThrownRockEntity(world, playerEntity);
                    thrownRockEntity.setVelocity(playerEntity, playerEntity.getPitch(), playerEntity.getYaw(), 0.0F, strength * 1.2F, 1.0F);
                    world.spawnEntity(thrownRockEntity);
                    world.playSoundFromEntity((Player) null, thrownRockEntity, SoundInit.ROCK_THROW_EVENT, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        }
    }

    @Override
    public InteractionResult<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (itemStack.getDamage() >= itemStack.getMaxDamage() - 1) {
            return InteractionResult.fail(itemStack);
        } else {
            user.setCurrentHand(hand);
            return InteractionResult.success(itemStack);
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        Player player = (Player) entity;
        MobEffectInstance slowness = new MobEffectInstance(MobEffects.SLOWNESS, 9, 0, false, false, false);
        if (selected && !world.isClientSide()) {
            player.addStatusEffect(slowness);
        }

        if (world.isClientSide()) {
            if (player.getItemUseTimeLeft() < 71970 && player.getItemUseTimeLeft() != 0
                    && player.getItemBySlot(player.getActiveHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND) == stack) {
                stack.set(ItemInit.LAVA_LIGHT, true);
            }

            if (stack.get(ItemInit.LAVA_LIGHT) != null && stack.get(ItemInit.LAVA_LIGHT)
                    && player.getItemBySlot(player.getActiveHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND) != stack) {
                stack.set(ItemInit.LAVA_LIGHT, false);
            }
        }
    }

    @Override
    public ItemUseAnimation getUseAction(ItemStack stack) {
        return ItemUseAnimation.NONE;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        return ingredient.getItem() == Items.NETHERITE_SCRAP;
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        Vec3 vec3d_1 = attacker.getRotationVec(1.0F);
        double x_vector = vec3d_1.x / 2D;
        double z_vector = vec3d_1.z / 2D;
        stack.damage(1, attacker, LivingEntity.getSlotForHand(attacker.getActiveHand()));
        target.addVelocity(x_vector, 0.45D, z_vector);
        return true;
    }

    public static float getStoneStrength(int useTicks) {
        float f = (float) useTicks / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

}
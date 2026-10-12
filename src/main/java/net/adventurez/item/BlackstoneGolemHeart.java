package net.adventurez.item;

import java.util.List;

import net.adventurez.init.ConfigInit;
import net.adventurez.init.EffectInit;
import net.adventurez.init.SoundInit;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;

public class BlackstoneGolemHeart extends Item {

    public BlackstoneGolemHeart(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (ConfigInit.CONFIG.allow_extra_tooltips) {
            tooltip.add(Component.translatable("item.adventurez.moreinfo.tooltip"));
            if (InputConstants.isKeyPressed(Minecraft.getInstance().getWindow().getHandle(), 340)) {
                tooltip.remove(Component.translatable("item.adventurez.moreinfo.tooltip"));
                tooltip.add(Component.translatable("item.adventurez.blackstone_golem_heart.tooltip"));
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if ((int) world.getTime() % 100 == 0) {
            world.playSound(null, entity.getBlockPos(), SoundInit.HEART_BEAT_EVENT, SoundSource.AMBIENT, 1F, 1F);
        }
    }

    @Override
    public InteractionResult<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (user.isSneaking()) {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundInit.GOLEM_AWAKENS_EVENT, SoundSource.PLAYERS, 1.4F, 1.0F);
            if (!world.isClientSide()) {
                user.addStatusEffect(new MobEffectInstance(EffectInit.BLACKSTONED_HEART, ConfigInit.CONFIG.stoned_heart_duration, ConfigInit.CONFIG.stoned_heart_amplifier, false, false, true));
                itemStack.decrement(1);
            }
            return InteractionResult.success(itemStack, world.isClientSide());
        } else {
            return InteractionResult.pass(itemStack);
        }
    }
}
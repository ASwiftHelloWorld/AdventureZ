package net.adventurez.item;

import java.util.List;

import net.adventurez.entity.EnderWhaleEntity;
import net.adventurez.init.ConfigInit;
import net.adventurez.init.SoundInit;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

public class EnderFlute extends Item {

    public EnderFlute(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (ConfigInit.CONFIG.allow_extra_tooltips) {
            tooltip.add(Component.translatable("item.adventurez.moreinfo.tooltip"));
            if (InputConstants.isKeyPressed(Minecraft.getInstance().getWindow().getHandle(), 340)) {
                tooltip.remove(Component.translatable("item.adventurez.moreinfo.tooltip"));
                tooltip.add(Component.translatable("item.adventurez.ender_flute.tooltip"));
            }
        }
    }

    @Override
    public InteractionResult<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundInit.FLUTE_CALL_EVENT, SoundSource.PLAYERS, 1.0F, world.getRandom().nextFloat() * 0.2F + 0.9F);
        if (!world.isClientSide()) {
            itemStack.damage(1, user, LivingEntity.getSlotForHand(hand));
            List<EnderWhaleEntity> list = world.getEntitiesOfClass(EnderWhaleEntity.class, new AABB(user.getBlockPos()).inflate(100D), EntitySelector.NO_SPECTATORS);
            if (!list.isEmpty()) {
                list.get(0).getMoveControl().moveTo(user.getX(), user.getY(), user.getZ(), 1.0D);
            }
        }

        user.getItemCooldownManager().set(itemStack.getItem(), 40);
        return InteractionResult.success(itemStack, world.isClientSide());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        if (world.isClientSide() && entity instanceof Player) {
            Player playerEntity = (Player) entity;
            float cooldownProgress = playerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), 0.0F);
            if (cooldownProgress > 0.0F) {
                world.addParticle(ParticleTypes.NOTE, (double) playerEntity.getX() + playerEntity.getRotationVecClient().getX() + world.getRandom().nextDouble() * 0.4D,
                        (double) playerEntity.getY() + playerEntity.getHeight() * 0.8D, (double) playerEntity.getZ() + playerEntity.getRotationVecClient().getZ() + world.getRandom().nextDouble() * 0.4D,
                        playerEntity.getRotationVecClient().getX() * world.getRandom().nextDouble(), 0.0D, 0.0D);
            }
        }
    }
}

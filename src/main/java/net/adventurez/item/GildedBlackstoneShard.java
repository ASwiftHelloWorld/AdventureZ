package net.adventurez.item;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.adventurez.entity.nonliving.GildedBlackstoneShardEntity;
import net.adventurez.init.ConfigInit;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public class GildedBlackstoneShard extends Item {

    private final Supplier<EntityType<GildedBlackstoneShardEntity>> typeSupplier;
    private EntityType<GildedBlackstoneShardEntity> cachedType = null;

    public GildedBlackstoneShard(Settings settings, Supplier<EntityType<GildedBlackstoneShardEntity>> typeSupplier) {
        super(settings);
        this.typeSupplier = typeSupplier;
    }

    public EntityType<GildedBlackstoneShardEntity> getType() {
        if (cachedType == null) {
            cachedType = typeSupplier.get();
        }
        return cachedType;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (ConfigInit.CONFIG.allow_extra_tooltips) {
            tooltip.add(Component.translatable("item.adventurez.moreinfo.tooltip"));
            if (InputConstants.isKeyPressed(Minecraft.getInstance().getWindow().getHandle(), 340)) {
                tooltip.remove(Component.translatable("item.adventurez.moreinfo.tooltip"));
                tooltip.add(Component.translatable("item.adventurez.gilded_blackstone_shard.tooltip"));
            }
        }
    }

    @Override
    public InteractionResult<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (ConfigInit.CONFIG.allow_gilded_blackstone_shard_throw && !user.isSneaking()) {
            world.playSound((Player) null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_EGG_THROW, SoundSource.PLAYERS, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
            if (!world.isClientSide()) {
                GildedBlackstoneShardEntity gildedStoneEntity = new GildedBlackstoneShardEntity(world, user.getX(), user.getY() + 1.6D, user.getZ());
                gildedStoneEntity.setItem(itemStack);
                gildedStoneEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.4F, 1.0F);
                world.spawnEntity(gildedStoneEntity);
            }
            if (!user.isCreative()) {
                itemStack.decrement(1);
            }

            return InteractionResult.success(itemStack, world.isClientSide());
        }
        return InteractionResult.pass(itemStack);
    }
}
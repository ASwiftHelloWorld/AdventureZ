package net.adventurez.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class MinecraftCompat {
    private MinecraftCompat() {
    }

    public static boolean isItem(ItemStack stack, Item item) {
        return stack.getItem() == item;
    }

    public static boolean isItemInTag(ItemStack stack, TagKey<Item> tag) {
        return stack.typeHolder().is(tag);
    }

    public static boolean isBlock(BlockState state, Block block) {
        return state.getBlock() == block;
    }

    public static boolean isBlockInTag(BlockState state, TagKey<Block> tag) {
        return state.is(tag, candidate -> true);
    }

    public static boolean isEntityTypeInTag(EntityType<?> type, TagKey<EntityType<?>> tag) {
        return type.builtInRegistryHolder().is(tag);
    }
}

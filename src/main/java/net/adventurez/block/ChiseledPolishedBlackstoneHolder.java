package net.adventurez.block;

import java.util.List;

import net.adventurez.init.BlockInit;
import net.adventurez.init.ConfigInit;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

import net.adventurez.block.entity.ChiseledPolishedBlackstoneHolderEntity;
import net.adventurez.init.TagInit;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Containers;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public class ChiseledPolishedBlackstoneHolder extends Block implements EntityBlock {
    private static final VoxelShape SHAPE;

    public ChiseledPolishedBlackstoneHolder(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChiseledPolishedBlackstoneHolderEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BlockInit.CHISELED_POLISHED_BLACKSTONE_HOLDER_ENTITY,
                world.isClientSide() ? ChiseledPolishedBlackstoneHolderEntity::clientTick : ChiseledPolishedBlackstoneHolderEntity::serverTick);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag options) {
        super.appendTooltip(stack, context, tooltip, options);
        if (ConfigInit.CONFIG.allow_extra_tooltips) {
            tooltip.add(Component.translatable("item.adventurez.moreinfo.tooltip"));
            if (InputConstants.isKeyPressed(Minecraft.getInstance().getWindow().getHandle(), 340)) {
                tooltip.remove(Component.translatable("item.adventurez.moreinfo.tooltip"));
                tooltip.add(Component.translatable("block.adventurez.chiseled_polished_blackstone_holder.tooltip"));
                tooltip.add(Component.translatable("block.adventurez.chiseled_polished_blackstone_holder.tooltip2"));
                tooltip.add(Component.translatable("block.adventurez.chiseled_polished_blackstone_holder.tooltip3"));
            }
        }
    }

    @Override
    public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        ItemStack itemStack = player.getMainHandItem();
        Container blockEntity = (Container) world.getBlockEntity(pos);
        ItemStack blockStack = blockEntity.getStack(0);
        if (blockStack.isEmpty()) {
            if ((net.adventurez.util.MinecraftCompat.isItemInTag(itemStack, TagInit.HOLDER_ITEMS) || ConfigInit.CONFIG.allow_all_items_on_holder) && world.getBlockState(pos.above()).isAir()) {
                if (!world.isClientSide()) {
                    blockEntity.setStack(0, new ItemStack(itemStack.getItem(), 1));
                    if (!player.isCreative()) {
                        itemStack.decrement(1);
                    }
                }
                return InteractionResult.success(world.isClientSide());
            } else {
                return InteractionResult.CONSUME;
            }
        } else {
            if (!world.isClientSide() && !player.getInventory().insertStack(blockStack)) {
                player.dropItem(blockStack, false);
            }

            blockEntity.clear();
            return InteractionResult.success(world.isClientSide());
        }

    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void onStateReplaced(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!net.adventurez.util.MinecraftCompat.isBlock(state, newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof Container) {
                Containers.spawn(world, pos, (Container) blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    static {
        SHAPE = Shapes.union(Block.box(0D, 0D, 0D, 16D, 14D, 16D), Block.box(0D, 14D, 0D, 16D, 16D, 3D), Block.box(0D, 14D, 13D, 16D, 16D, 16D),
            Block.box(13D, 14D, 3D, 16D, 16D, 13D), Block.box(0D, 14D, 3D, 3D, 16D, 13D));
    }

}

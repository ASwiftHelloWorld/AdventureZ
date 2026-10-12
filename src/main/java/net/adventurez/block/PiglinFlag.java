package net.adventurez.block;

import net.adventurez.init.BlockInit;
import net.adventurez.init.ConfigInit;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

import java.util.List;

import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.adventurez.block.entity.PiglinFlagEntity;

public class PiglinFlag extends Block implements EntityBlock {
    public static final EnumProperty<Direction> FACING;
    private static final VoxelShape BASE;
    private static final VoxelShape POLE;
    private static final VoxelShape BAR_1;
    private static final VoxelShape BAR_2;
    private static final VoxelShape BAR_3;
    private static final VoxelShape BAR_4;
    private static final VoxelShape NORTH_FLAG;
    private static final VoxelShape EAST_FLAG;
    private static final VoxelShape SOUTH_FLAG;
    private static final VoxelShape WEST_FLAG;

    public PiglinFlag(BlockBehaviour.Properties settings) {
        super(settings);
        this.setDefaultState(this.stateManager.defaultBlockState().with(FACING, Direction.NORTH));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PiglinFlagEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, BlockInit.PIGLIN_FLAG_ENTITY, world.isClientSide() ? PiglinFlagEntity::clientTick : PiglinFlagEntity::serverTick);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag options) {
        super.appendTooltip(stack, context, tooltip, options);
        if (ConfigInit.CONFIG.allow_extra_tooltips) {
            tooltip.add(Component.translatable("item.adventurez.moreinfo.tooltip"));
            if (InputConstants.isKeyPressed(Minecraft.getInstance().getWindow().getHandle(), 340)) {
                tooltip.remove(Component.translatable("item.adventurez.moreinfo.tooltip"));
                tooltip.add(Component.translatable("block.adventurez.piglin_flag.tooltip"));
                tooltip.add(Component.translatable("block.adventurez.piglin_flag.tooltip2"));
                tooltip.add(Component.translatable("block.adventurez.piglin_flag.tooltip3"));
            }
        }
    }

    @Override
    public BlockState getPlacementState(BlockPlaceContext ctx) {
        return this.defaultBlockState().with(FACING, ctx.getHorizontalPlayerFacing().rotateYClockwise());
    }

    @Override
    public void appendProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return this.getShape(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return BASE;
    }

    @Override
    public RenderShape getRenderType(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected boolean canPathfindThrough(BlockState state, PathComputationType type) {
        return false;
    }

    private VoxelShape getShape(BlockState state) {
        Direction direction = state.get(FACING);
        if (direction == Direction.NORTH) {
            return NORTH_FLAG;
        } else if (direction == Direction.EAST) {
            return EAST_FLAG;
        } else if (direction == Direction.SOUTH) {
            return SOUTH_FLAG;
        } else if (direction == Direction.WEST) {
            return WEST_FLAG;
        } else {
            return NORTH_FLAG;
        }
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker) {
        return expectedType == givenType ? (BlockEntityTicker<A>) ticker : null;
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        BASE = Block.box(6D, 0D, 6D, 10D, 11D, 10D);
        POLE = Shapes.union(BASE, Block.box(7D, 11D, 7D, 9D, 40D, 9D));
        BAR_1 = Block.box(6D, 40D, -8D, 10D, 44D, 10D);
        BAR_2 = Block.box(6D, 40D, 6D, 24D, 44D, 10D);
        BAR_3 = Block.box(6D, 40D, 6D, 10D, 44D, 24D);
        BAR_4 = Block.box(-8D, 40D, 6D, 10D, 44D, 10D);
        NORTH_FLAG = Shapes.union(POLE, BAR_1);
        EAST_FLAG = Shapes.union(POLE, BAR_2);
        SOUTH_FLAG = Shapes.union(POLE, BAR_3);
        WEST_FLAG = Shapes.union(POLE, BAR_4);
    }

}
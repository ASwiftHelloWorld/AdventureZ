package net.adventurez.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;

import net.adventurez.entity.AmethystGolemEntity;
import net.minecraft.world.level.block.AmethystBlock;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

@Mixin(AmethystClusterBlock.class)
public abstract class AmethystClusterBlockMixin extends AmethystBlock {

    public AmethystClusterBlockMixin(BlockBehaviour.Properties settings) {
        super(settings);
    }

    @Override
    public BlockState onBreak(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide()) {
            List<AmethystGolemEntity> list = world.getEntitiesByClass(AmethystGolemEntity.class, new AABB(pos).inflate(16.0D), EntitySelector.EXCEPT_SPECTATOR);
            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    list.get(i).amethystGolemRageMode();
                }
            }
        }
        return super.onBreak(world, pos, state, player);
    }

}

package net.adventurez.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.adventurez.entity.DragonEntity;
import net.adventurez.init.ConfigInit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

@Mixin(NetherPortalBlock.class)
public class NetherPortalBlockMixin {

    public InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.hasVehicle() && player.getVehicle() instanceof DragonEntity dragonEntity && player.canUsePortals(true) && ConfigInit.CONFIG.allow_ender_dragon_nether_portal) {
            player.stopRiding();
            dragonEntity.portalManager.setInPortal(true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;

    }

}
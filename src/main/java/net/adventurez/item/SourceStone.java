package net.adventurez.item;

import java.util.List;

import net.adventurez.entity.nonliving.VoidBulletEntity;
import net.adventurez.init.ConfigInit;
import net.adventurez.init.SoundInit;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.Level;

public class SourceStone extends Item {

    public SourceStone(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
        super.appendTooltip(stack, context, tooltip, type);
        if (ConfigInit.CONFIG.allow_extra_tooltips) {
            tooltip.add(Component.translatable("item.adventurez.moreinfo.tooltip"));
            if (InputConstants.isKeyPressed(Minecraft.getInstance().getWindow().getHandle(), 340)) {
                tooltip.remove(Component.translatable("item.adventurez.moreinfo.tooltip"));
                tooltip.add(Component.translatable("item.adventurez.source_stone.tooltip"));
            }
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof Player playerEntity) {
            if (!world.isClientSide()) {
                if (ConfigInit.CONFIG.allow_source_stone_tp && playerEntity.isSneaking()) {
                    HitResult hitResult = playerEntity.raycast(8D, 1.0F, false);
                    if (hitResult != null) {
                        if (hitResult.getType() == HitResult.Type.BLOCK) {
                            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                            tryTeleport((ServerLevel) world, blockHitResult.getBlockPos(), blockHitResult.getSide(), playerEntity);
                        } else if (hitResult.getType() == HitResult.Type.ENTITY) {
                            EntityHitResult entityHitResult = (EntityHitResult) hitResult;
                            tryTeleport((ServerLevel) world, entityHitResult.getEntity().getBlockPos(), entityHitResult.getEntity().getHorizontalFacing(), playerEntity);
                        }
                    }
                } else {
                    Vec3 vec3d = playerEntity.getRotationVec(1.0F);
                    VoidBulletEntity voidBulletEntity = new VoidBulletEntity(playerEntity, vec3d, world);
                    world.spawnEntity(voidBulletEntity);
                    world.playSoundFromEntity((Player) null, voidBulletEntity, SoundInit.SHADOW_CAST_EVENT, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
            playerEntity.getItemCooldownManager().set(stack.getItem(), 40);
        }
    }

    private void tryTeleport(ServerLevel world, BlockPos blockPos, Direction direction, Player playerEntity) {
        if (!world.getBlockState(blockPos).isAir()) {
            for (int i = 2; i < 6; i++) {
                BlockPos newBlockPos = new BlockPos(blockPos).offset(direction.getOpposite(), i);
                BlockPos upperBlockPos = newBlockPos.above();
                if (!world.getBlockState(newBlockPos).isSolidBlock(world, newBlockPos) && !world.getBlockState(upperBlockPos).isSolidBlock(world, upperBlockPos)) {
                    playerEntity
                            .teleportTo(new TeleportTransition(world, newBlockPos.toBottomCenterPos(), playerEntity.getVelocity(), playerEntity.getYaw(), playerEntity.getPitch(), TeleportTransition.NO_OP));

                    world.playSoundFromEntity((Player) null, playerEntity, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                    break;
                }
            }
        }
    }

    @Override
    public InteractionResult<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        user.setCurrentHand(hand);
        return InteractionResult.success(itemStack);
    }

}

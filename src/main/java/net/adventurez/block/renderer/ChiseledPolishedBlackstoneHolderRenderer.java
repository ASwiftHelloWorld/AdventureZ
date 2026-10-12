package net.adventurez.block.renderer;

import net.adventurez.block.entity.ChiseledPolishedBlackstoneHolderEntity;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

@Environment(EnvType.CLIENT)
public class ChiseledPolishedBlackstoneHolderRenderer implements BlockEntityRenderer<ChiseledPolishedBlackstoneHolderEntity> {

    public ChiseledPolishedBlackstoneHolderRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(ChiseledPolishedBlackstoneHolderEntity blockEntity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        if (!blockEntity.isEmpty()) {
            matrices.push();
            double offset = Math.sin((blockEntity.getLevel().getTime() + tickDelta) / 8.0D) / 4.5D;
            matrices.translate(0.5D, 1.3D + offset, 0.5D);
            matrices.multiply(Axis.POSITIVE_Y.rotationDegrees((blockEntity.getLevel().getTime() + tickDelta) * 4F));
            Minecraft.getInstance().getItemRenderer().renderItem(blockEntity.getStack(0), ItemDisplayContext.GROUND, light, overlay, matrices, vertexConsumers, blockEntity.getLevel(),
                    (int) blockEntity.getBlockPos().asLong());
            matrices.pop();
        }
    }
}
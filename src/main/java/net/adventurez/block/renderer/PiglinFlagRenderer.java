package net.adventurez.block.renderer;

import net.adventurez.block.PiglinFlag;
import net.adventurez.block.entity.PiglinFlagEntity;
import net.adventurez.init.RenderInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import com.mojang.math.Axis;

@Environment(EnvType.CLIENT)
public class PiglinFlagRenderer implements BlockEntityRenderer<PiglinFlagEntity> {

    private static final Identifier TEXTURE = Identifier.of("adventurez", "textures/block/piglin_flag.png");
    private final ModelPart flag;
    private final ModelPart holder;

    public PiglinFlagRenderer(BlockEntityRendererProvider.Context ctx) {
        this.holder = ctx.getLayerModelPart(RenderInit.PIGLIN_FLAG_LAYER).getChild("holder");
        this.flag = holder.getChild("flag");
    }

    public PiglinFlagRenderer(ModelPart root) {
        this.holder = root.getChild("holder");
        this.flag = holder.getChild("flag");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition holder = modelPartData.addChild(
                "holder", CubeListBuilder.create().uv(0, 42).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)).uv(10, 20)
                        .cuboid(-2.0F, 40.0F, -2.0F, 4.0F, 4.0F, 18.0F, new CubeDeformation(0.0F)).uv(36, 0).cuboid(-1.0F, 11.0F, -1.0F, 2.0F, 29.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.pivot(0.0F, -20.0F, 0.0F));

        holder.addChild("flag", CubeListBuilder.create().uv(0, 0).cuboid(0.0F, -24.0F, -6.6F, 0.0F, 24.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.pivot(0.0F, 40.0F, 8.0F));
        return LayerDefinition.of(modelData, 64, 64);
    }

    @Override
    public void render(PiglinFlagEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumerProvider, int light, int overlay) {
        matrices.push();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderType.getEntityCutoutNoCull(TEXTURE));
        BlockState state = entity.getBlockState();
        Direction blockDirection = state.get(HorizontalDirectionalBlock.FACING);
        if (state.getBlock() instanceof PiglinFlag) {
            if (blockDirection.equals(Direction.NORTH)) {
                matrices.translate(1D, 0D, 1D);
                matrices.multiply(Axis.POSITIVE_Y.rotationDegrees(180F));
            } else if (blockDirection.equals(Direction.EAST)) {
                matrices.translate(0D, 0D, 1D);
                matrices.multiply(Axis.POSITIVE_Y.rotationDegrees(90F));
            } else if (blockDirection.equals(Direction.SOUTH)) {
            } else if (blockDirection.equals(Direction.WEST)) {
                matrices.translate(1D, 0D, 0D);
                matrices.multiply(Axis.POSITIVE_Y.rotationDegrees(270F));
            }
        }
        matrices.translate(0.5D, 1.25D, 0.5D);
        BlockPos blockPos = entity.getBlockPos();
        long worldTime = entity.getLevel().getGameTime();
        float goodFloat = ((float) Math.floorMod((long) (blockPos.getX() * 7 + blockPos.getY() * 9 + blockPos.getZ() * 13) + worldTime, 100L) + tickDelta) / 100.0F;
        flag.roll = Mth.cos(6.2831855F * goodFloat) / 4;
        holder.render(matrices, vertexConsumer, light, overlay);
        matrices.pop();
    }
}
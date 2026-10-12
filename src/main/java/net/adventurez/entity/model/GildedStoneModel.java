package net.adventurez.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

@Environment(EnvType.CLIENT)
public class GildedStoneModel extends Model {

    private final ModelPart base;

    public GildedStoneModel(ModelPart root) {
        super(RenderType::getEntitySolid);
        this.base = root.getChild("base");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addChild("base",
                CubeListBuilder.create().uv(12, 2).cuboid(1.5F, -3.0F, -0.5F, 1.0F, 5.0F, 1.0F).uv(12, 2).cuboid(-0.5F, -6.0F, -0.5F, 1.0F, 12.0F, 1.0F).uv(12, 2)
                        .cuboid(-0.5F, -5.0F, 0.5F, 1.0F, 8.0F, 1.0F).uv(12, 2).cuboid(-0.5F, -4.0F, 1.5F, 1.0F, 4.0F, 1.0F).uv(12, 2).cuboid(-0.5F, -4.0F, -1.5F, 1.0F, 9.0F, 1.0F).uv(12, 2)
                        .cuboid(-0.5F, -3.0F, -2.5F, 1.0F, 5.0F, 1.0F).uv(12, 2).cuboid(-1.5F, -4.0F, -0.5F, 1.0F, 8.0F, 1.0F).uv(12, 2).cuboid(-2.5F, -3.0F, -0.5F, 1.0F, 3.0F, 1.0F).uv(12, 2)
                        .cuboid(0.5F, -5.0F, -0.5F, 1.0F, 9.0F, 1.0F).uv(12, 2).cuboid(0.5F, -2.0F, -1.5F, 1.0F, 4.0F, 1.0F).uv(12, 2).cuboid(0.5F, -4.0F, 0.5F, 1.0F, 6.0F, 1.0F).uv(12, 2)
                        .cuboid(-1.5F, -3.0F, -1.5F, 1.0F, 5.0F, 1.0F).uv(12, 4).cuboid(-1.5F, -4.0F, 0.5F, 1.0F, 6.0F, 1.0F),
                PartPose.pivot(0.0F, 18.0F, 0.0F));
        return LayerDefinition.of(modelData, 32, 32);
    }

    @Override
    public void render(PoseStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.base.render(matrices, vertices, light, overlay, color);
    }

}

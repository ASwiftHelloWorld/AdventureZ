package net.adventurez.entity.model;

import com.google.common.collect.ImmutableList;

import net.adventurez.entity.DesertRhinoEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.ListModel;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class DesertRhinoModel<T extends DesertRhinoEntity> extends ListModel<T> {
    private final ModelPart main;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart head;
    private final ModelPart right_front_leg;
    private final ModelPart right_back_leg;
    private final ModelPart left_back_leg;
    private final ModelPart left_front_leg;

    public DesertRhinoModel(ModelPart root) {
        this.main = root.getChild("main");
        this.left_front_leg = this.main.getChild("left_front_leg");
        this.left_back_leg = this.main.getChild("left_back_leg");
        this.right_back_leg = this.main.getChild("right_back_leg");
        this.right_front_leg = this.main.getChild("right_front_leg");
        this.body = this.main.getChild("body");
        this.head = this.body.getChild("head");
        this.tail = this.body.getChild("tail");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition modelPartData1 = modelPartData.addChild("main", CubeListBuilder.create(), PartPose.pivot(0.0F, 24.0F, 0.0F));
        PartDefinition modelPartData2 = modelPartData1.addChild("body",
                CubeListBuilder.create().uv(0, 0).cuboid(-12.0F, -23.0F, -36.0F, 24.0F, 23.0F, 27.0F).uv(23, 82).cuboid(-8.0F, -28.0F, -32.0F, 4.0F, 5.0F, 4.0F).uv(54, 50)
                        .cuboid(5.0F, -29.0F, -25.0F, 4.0F, 6.0F, 4.0F).uv(86, 73).cuboid(-6.0F, -27.0F, -22.0F, 4.0F, 4.0F, 4.0F).uv(39, 82).cuboid(0.0F, -26.0F, -16.0F, 4.0F, 3.0F, 4.0F).uv(0, 50)
                        .cuboid(-10.0F, -18.0F, -9.0F, 20.0F, 18.0F, 14.0F),
                PartPose.pivot(0.0F, -13.0F, 13.0F));
        modelPartData2.addChild("tail", CubeListBuilder.create().uv(85, 85).cuboid(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 13.0F).uv(0, 0).cuboid(-3.0F, -2.0F, 13.0F, 6.0F, 6.0F, 6.0F),
                PartPose.pivot(0.0F, -13.0F, 5.0F));
        modelPartData2.addChild("head", CubeListBuilder.create().uv(68, 50).cuboid(-5.0F, -5.0F, -13.0F, 10.0F, 10.0F, 13.0F).uv(16, 12).cuboid(-1.0F, -10.0F, -6.0F, 2.0F, 5.0F, 2.0F).uv(0, 12)
                .cuboid(-2.0F, -14.0F, -12.0F, 4.0F, 9.0F, 4.0F), PartPose.pivot(0.0F, -8.0F, -36.0F));
        modelPartData1.addChild("right_front_leg", CubeListBuilder.create().uv(75, 0).cuboid(-4.0F, 0.0F, -6.0F, 9.0F, 13.0F, 12.0F), PartPose.pivot(6.0F, -13.0F, -14.0F));
        modelPartData1.addChild("right_back_leg", CubeListBuilder.create().uv(32, 89).cuboid(-4.0F, 0.0F, -4.0F, 7.0F, 13.0F, 9.0F), PartPose.pivot(6.0F, -13.0F, 12.0F));
        modelPartData1.addChild("left_back_leg", CubeListBuilder.create().uv(0, 82).cuboid(-3.0F, 0.0F, -4.0F, 7.0F, 13.0F, 9.0F), PartPose.pivot(-6.0F, -13.0F, 12.0F));
        modelPartData1.addChild("left_front_leg", CubeListBuilder.create().uv(56, 73).cuboid(-5.0F, 0.0F, -6.0F, 9.0F, 13.0F, 12.0F), PartPose.pivot(-6.0F, -13.0F, -14.0F));
        return LayerDefinition.of(modelData, 128, 128);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.main);
    }

    @Override
    public void setAngles(T fungus, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.tail.pitch = -1.0F;
        this.head.pitch = headPitch * 0.010453292F;
        this.head.yaw = headYaw * 0.010453292F;
        this.right_back_leg.pitch = Mth.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
        this.left_back_leg.pitch = Mth.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
        this.right_front_leg.pitch = Mth.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
        this.left_front_leg.pitch = Mth.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
        this.tail.yaw = Mth.cos(limbAngle * 0.6662F) * 1.4F * limbDistance * 0.3F;

        float k = Mth.sin(this.handSwingProgress * 3.1415927F);

        if (k > 0.0F)
            this.head.pitch = -k * 0.8F;

    }

}

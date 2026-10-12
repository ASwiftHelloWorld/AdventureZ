package net.adventurez.entity.model;

import com.google.common.collect.ImmutableList;

import net.adventurez.entity.VoidShadeEntity;
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
public class VoidShadeModel<T extends VoidShadeEntity> extends ListModel<T> {
    private final ModelPart body;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public VoidShadeModel(ModelPart root) {
        this.body = root.getChild("body");
        this.leftArm = this.body.getChild("leftArm");
        this.rightArm = this.body.getChild("rightArm");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition modelPartData1 = modelPartData.addChild("body",
                CubeListBuilder.create().uv(25, 28).cuboid(-4.0F, -9.0F, -5.0F, 8.0F, 6.0F, 9.0F).uv(34, 12).cuboid(-3.0F, -13.0F, -3.0F, 6.0F, 4.0F, 10.0F).uv(34, 0)
                        .cuboid(-3.0F, -9.0F, 4.0F, 6.0F, 4.0F, 6.0F).uv(0, 0).cuboid(-6.0F, -3.0F, -5.0F, 12.0F, 12.0F, 10.0F).uv(0, 37).cuboid(-2.0F, 13.0F, 0.0F, 4.0F, 4.0F, 11.0F).uv(0, 22)
                        .cuboid(-3.0F, 9.0F, -3.0F, 6.0F, 4.0F, 11.0F),
                PartPose.pivot(0.0F, 7.0F, -3.0F));
        modelPartData1.addChild("rightArm", CubeListBuilder.create().uv(46, 43).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 10.0F, 4.0F), PartPose.pivot(-8.0F, 1.0F, 0.0F));
        modelPartData1.addChild("leftArm", CubeListBuilder.create().uv(30, 43).cuboid(-2.0F, -2.0F, -2.0F, 4.0F, 10.0F, 4.0F), PartPose.pivot(8.0F, 1.0F, 0.0F));
        return LayerDefinition.of(modelData, 128, 128);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.body);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.body.pivotY = 4.0F + Mth.sin(animationProgress * 0.1262F) * 0.8F;
        this.rightArm.pitch = -1.5708F + Mth.sin(animationProgress * 0.1262F) * 0.1F;
        this.leftArm.pitch = this.rightArm.pitch;
        float k = Mth.sin(this.handSwingProgress * 3.1415927F);
        if (k > 0) {
            this.rightArm.pitch = -k * 1.5F;
        }
    }
}
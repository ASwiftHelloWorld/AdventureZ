package net.adventurez.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

@Environment(EnvType.CLIENT)
public class WitherPuppetModel<T extends Mob> extends HumanoidModel<T> {

    public WitherPuppetModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = HumanoidModel.getModelData(CubeDeformation.NONE, 0.0F);
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addChild("body", CubeListBuilder.create().uv(0, 16).cuboid(-4.0F, -3.0F, -2.0F, 8.0F, 7.0F, 4.0F), PartPose.pivot(0.0F, 14.0F, 0.0F));
        modelPartData.addChild("head", CubeListBuilder.create().uv(0, 0).cuboid(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.pivot(0.0F, 10.0F, 0.0F));
        modelPartData.addChild("right_arm", CubeListBuilder.create().uv(0, 27).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 7.0F, 2.0F), PartPose.pivot(5.0F, 12.0F, 0.0F));
        modelPartData.addChild("left_arm", CubeListBuilder.create().uv(24, 24).cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 7.0F, 2.0F), PartPose.pivot(-5.0F, 12.0F, 0.0F));
        modelPartData.addChild("right_leg", CubeListBuilder.create().uv(24, 0).cuboid(-1.0F, 1.0F, -1.0F, 2.0F, 6.0F, 2.0F), PartPose.pivot(2.0F, 17.0F, 0.0F));
        modelPartData.addChild("left_leg", CubeListBuilder.create().uv(0, 0).cuboid(-1.0F, 1.0F, -1.0F, 2.0F, 6.0F, 2.0F), PartPose.pivot(-2.0F, 17.0F, 0.0F));
        return LayerDefinition.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(T puppet, float f, float g, float h, float i, float j) {
        float k = Mth.sin(this.handSwingProgress * 3.1415927F);
        this.rightArm.pitch = Mth.cos(f * 0.6662F + 3.1415927F) * 2.0F * g * 0.5F;
        this.leftArm.pitch = Mth.cos(f * 0.6662F) * 2.0F * g * 0.5F;
        this.rightArm.roll = 0.0F;
        this.leftArm.roll = 0.0F;
        if (k > 0) {
            this.rightArm.yaw = -(0.1F - k * 0.6F);
            this.rightArm.pitch = -k * 1.5F;
        }
        this.rightLeg.pitch = Mth.cos(f * 0.6662F) * 1.4F * g;
        this.leftLeg.pitch = Mth.cos(f * 0.6662F + 3.1415927F) * 1.4F * g;
    }

}

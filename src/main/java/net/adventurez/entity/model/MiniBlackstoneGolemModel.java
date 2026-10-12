package net.adventurez.entity.model;

import com.google.common.collect.ImmutableList;

import net.adventurez.entity.MiniBlackstoneGolemEntity;
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
public class MiniBlackstoneGolemModel<T extends MiniBlackstoneGolemEntity> extends ListModel<T> {
    private final ModelPart head;
    private final ModelPart torso;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public MiniBlackstoneGolemModel(ModelPart root) {
        this.head = root.getChild("head");
        this.torso = root.getChild("torso");
        this.rightArm = root.getChild("rightArm");
        this.leftArm = root.getChild("leftArm");
        this.rightLeg = root.getChild("rightLeg");
        this.leftLeg = root.getChild("leftLeg");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addChild("head", CubeListBuilder.create().uv(0, 0).cuboid(-4.0F, -8.2796F, -7.044F, 8.0F, 10.0F, 8.0F).uv(24, 0).cuboid(-1.0F, -1.2796F, -9.044F, 2.0F, 4.0F, 2.0F),
                PartPose.pivot(0.0F, 13.0F, -5.0F));
        modelPartData.addChild("torso", CubeListBuilder.create().uv(0, 40).cuboid(-9.0F, -5.7148F, -5.5702F, 18.0F, 11.0F, 11.0F), PartPose.pivot(0.0F, 15.0F, 0.0F));
        modelPartData.addChild("rightArm", CubeListBuilder.create().uv(60, 21).cuboid(9.0F, -2.4685F, -2.9247F, 4.0F, 13.0F, 6.0F), PartPose.pivot(0.0F, 15.0F, -4.0F));
        modelPartData.addChild("leftArm", CubeListBuilder.create().uv(60, 58).cuboid(-13.0F, -2.4685F, -2.9247F, 4.0F, 13.0F, 6.0F), PartPose.pivot(0.0F, 15.0F, -4.0F));
        modelPartData.addChild("rightLeg", CubeListBuilder.create().uv(37, 0).cuboid(1.5F, -1.7043F, -2.5805F, 6.0F, 11.0F, 5.0F), PartPose.pivot(0.0F, 16.0F, 5.0F));
        modelPartData.addChild("leftLeg", CubeListBuilder.create().uv(60, 0).cuboid(-7.5F, -1.7043F, -2.5805F, 6.0F, 11.0F, 5.0F), PartPose.pivot(0.0F, 16.0F, 5.0F));
        return LayerDefinition.of(modelData, 128, 128);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.head, this.torso, this.rightLeg, this.leftLeg, this.rightArm, this.leftArm);
    }

    @Override
    public void setAngles(T stoneGolem, float f, float g, float h, float i, float j) {
        this.head.yaw = i * 0.0077453292F;
        this.head.pitch = j * 0.0017453292F + 0.1745F;
        this.rightLeg.yaw = 0.0F;
        this.leftLeg.yaw = 0.0F;
        this.rightLeg.pitch = -0.3F * Mth.wrap(f, 13.0F) * g * 1.3F + 0.4363F;
        this.leftLeg.pitch = 0.3F * Mth.wrap(f, 13.0F) * g * 1.3F + 0.4363F;
        this.torso.pitch = 1.309F;
        this.rightArm.pitch = 0.5F * Mth.wrap(f, 13.0F) * g * 1.3F - 0.6109F;
        this.leftArm.pitch = -0.5F * Mth.wrap(f, 13.0F) * g * 1.3F - 0.6109F;
        float k = Mth.sin(this.handSwingProgress * 3.1415927F);
        if (k > 0) {
            this.rightArm.pitch = -k * 1.5F;
        }
    }

}

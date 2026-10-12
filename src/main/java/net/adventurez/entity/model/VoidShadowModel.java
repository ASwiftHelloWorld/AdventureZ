package net.adventurez.entity.model;

import com.google.common.collect.ImmutableList;

import net.adventurez.entity.VoidShadowEntity;
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
public class VoidShadowModel<T extends VoidShadowEntity> extends ListModel<T> {

    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart body;
    private final ModelPart rightParticle;
    private final ModelPart bone;
    private final ModelPart bone2;
    private final ModelPart bone3;
    private final ModelPart bone4;
    private final ModelPart bone5;
    private final ModelPart bone6;
    private final ModelPart bone7;
    private final ModelPart leftParticle;
    private final ModelPart bone8;
    private final ModelPart bone9;
    private final ModelPart bone10;
    private final ModelPart bone11;
    private final ModelPart bone12;

    private float throwingBlocksTicker;

    public VoidShadowModel(ModelPart root) {
        this.rightArm = root.getChild("rightArm");
        this.leftArm = root.getChild("leftArm");
        this.body = root.getChild("body");
        this.leftParticle = this.body.getChild("leftParticle");
        this.bone12 = this.leftParticle.getChild("bone12");
        this.bone11 = this.leftParticle.getChild("bone11");
        this.bone10 = this.leftParticle.getChild("bone10");
        this.bone9 = this.leftParticle.getChild("bone9");
        this.bone8 = this.leftParticle.getChild("bone8");
        this.rightParticle = this.body.getChild("rightParticle");
        this.bone7 = this.rightParticle.getChild("bone7");
        this.bone6 = this.rightParticle.getChild("bone6");
        this.bone5 = this.rightParticle.getChild("bone5");
        this.bone4 = this.rightParticle.getChild("bone4");
        this.bone3 = this.rightParticle.getChild("bone3");
        this.bone2 = this.rightParticle.getChild("bone2");
        this.bone = this.rightParticle.getChild("bone");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addChild("rightArm",
                CubeListBuilder.create().uv(44, 58).cuboid(1.0F, 6.0F, -2.0F, 2.0F, 2.0F, 4.0F).uv(0, 50).cuboid(-3.0F, 8.0F, -2.0F, 6.0F, 2.0F, 4.0F).uv(20, 62)
                        .cuboid(-1.0F, 8.0F, -4.0F, 4.0F, 2.0F, 2.0F).uv(0, 20).cuboid(-1.0F, 14.0F, -4.0F, 2.0F, 2.0F, 2.0F).uv(0, 16).cuboid(-1.0F, 14.0F, 2.0F, 2.0F, 2.0F, 2.0F).uv(8, 62)
                        .cuboid(-1.0F, 8.0F, 2.0F, 4.0F, 2.0F, 2.0F).uv(0, 28).cuboid(-1.0F, 10.0F, -4.0F, 6.0F, 4.0F, 8.0F).uv(48, 28).cuboid(-3.0F, 14.0F, -2.0F, 6.0F, 2.0F, 4.0F).uv(32, 58)
                        .cuboid(1.0F, 16.0F, -2.0F, 2.0F, 2.0F, 4.0F).uv(24, 56).cuboid(-3.0F, 16.0F, -2.0F, 2.0F, 2.0F, 4.0F).uv(0, 56).cuboid(-1.0F, 18.0F, -2.0F, 2.0F, 2.0F, 4.0F).uv(12, 56)
                        .cuboid(-5.0F, 18.0F, -2.0F, 2.0F, 2.0F, 4.0F).uv(26, 16).cuboid(-5.0F, 10.0F, -2.0F, 2.0F, 2.0F, 2.0F),
                PartPose.pivot(11.0F, 1.0F, 0.0F));
        modelPartData.addChild("leftArm",
                CubeListBuilder.create().uv(26, 20).cuboid(3.0F, 10.0F, -2.0F, 2.0F, 2.0F, 2.0F).uv(36, 52).cuboid(-3.0F, 8.0F, -2.0F, 6.0F, 2.0F, 4.0F).uv(60, 60)
                        .cuboid(-3.0F, 8.0F, 2.0F, 4.0F, 2.0F, 2.0F).uv(16, 40).cuboid(-3.0F, 8.0F, -4.0F, 4.0F, 2.0F, 2.0F).uv(0, 4).cuboid(-1.0F, 14.0F, -4.0F, 2.0F, 2.0F, 2.0F).uv(0, 0)
                        .cuboid(-1.0F, 14.0F, 2.0F, 2.0F, 2.0F, 2.0F).uv(0, 62).cuboid(-5.0F, 6.0F, -2.0F, 2.0F, 2.0F, 4.0F).uv(28, 28).cuboid(-5.0F, 10.0F, -4.0F, 6.0F, 4.0F, 8.0F).uv(60, 34)
                        .cuboid(1.0F, 16.0F, -2.0F, 2.0F, 2.0F, 4.0F).uv(52, 60).cuboid(-1.0F, 18.0F, -2.0F, 2.0F, 2.0F, 4.0F).uv(58, 44).cuboid(-3.0F, 16.0F, -2.0F, 2.0F, 2.0F, 4.0F).uv(20, 50)
                        .cuboid(-3.0F, 14.0F, -2.0F, 6.0F, 2.0F, 4.0F).uv(58, 0).cuboid(3.0F, 18.0F, -2.0F, 2.0F, 2.0F, 4.0F),
                PartPose.pivot(-11.0F, 1.0F, 0.0F));
        PartDefinition modelPartData1 = modelPartData.addChild("body",
                CubeListBuilder.create().uv(26, 16).cuboid(-6.0F, -11.0F, -4.0F, 6.0F, 2.0F, 8.0F).uv(0, 40).cuboid(2.0F, -11.0F, -4.0F, 4.0F, 2.0F, 8.0F).uv(50, 34)
                        .cuboid(-8.0F, -9.0F, -3.0F, 2.0F, 4.0F, 6.0F).uv(20, 28).cuboid(8.0F, -9.0F, -3.0F, 2.0F, 2.0F, 6.0F).uv(0, 0).cuboid(-6.0F, -9.0F, -5.0F, 14.0F, 6.0F, 10.0F).uv(0, 16)
                        .cuboid(-4.0F, -3.0F, -5.0F, 8.0F, 2.0F, 10.0F).uv(38, 0).cuboid(-2.0F, -1.0F, -4.0F, 6.0F, 2.0F, 8.0F).uv(42, 44).cuboid(-2.0F, 1.0F, -3.0F, 5.0F, 2.0F, 6.0F).uv(46, 10)
                        .cuboid(-1.0F, 3.0F, -3.0F, 5.0F, 2.0F, 6.0F).uv(48, 20).cuboid(-2.0F, 5.0F, -3.0F, 4.0F, 2.0F, 6.0F).uv(24, 40).cuboid(-4.0F, 7.0F, -2.0F, 8.0F, 6.0F, 4.0F).uv(52, 54)
                        .cuboid(-2.0F, 13.0F, -2.0F, 4.0F, 2.0F, 4.0F),
                PartPose.pivot(0.0F, 8.0F, 0.0F));
        PartDefinition modelPartData2 = modelPartData1.addChild("rightParticle", CubeListBuilder.create(), PartPose.pivot(8.0F, -9.0F, 0.0F));
        modelPartData2.addChild("bone", CubeListBuilder.create().uv(40, 58).cuboid(12.0F, -22.0F, -2.0F, 2.0F, 2.0F, 2.0F), PartPose.pivot(-8.0F, 24.0F, 0.0F));
        modelPartData2.addChild("bone2", CubeListBuilder.create().uv(8, 56).cuboid(10.0F, -26.0F, 0.0F, 2.0F, 2.0F, 2.0F), PartPose.pivot(-8.0F, 24.0F, 0.0F));
        modelPartData2.addChild("bone3", CubeListBuilder.create().uv(20, 56).cuboid(8.0F, -28.0F, -2.0F, 2.0F, 2.0F, 2.0F), PartPose.pivot(-8.0F, 24.0F, 0.0F));
        modelPartData2.addChild("bone4", CubeListBuilder.create().uv(46, 18).cuboid(14.0F, -28.0F, -2.0F, 2.0F, 2.0F, 2.0F), PartPose.pivot(-8.0F, 24.0F, 0.0F));
        modelPartData2.addChild("bone5", CubeListBuilder.create().uv(62, 10).cuboid(8.0F, -18.0F, 0.0F, 2.0F, 2.0F, 2.0F), PartPose.pivot(-8.0F, 24.0F, 0.0F));
        modelPartData2.addChild("bone6", CubeListBuilder.create().uv(16, 50).cuboid(10.0F, -30.0F, 0.0F, 2.0F, 2.0F, 2.0F), PartPose.pivot(-8.0F, 24.0F, 0.0F));
        modelPartData2.addChild("bone7", CubeListBuilder.create().uv(16, 44).cuboid(4.0F, -28.0F, -2.0F, 2.0F, 2.0F, 2.0F), PartPose.pivot(-8.0F, 24.0F, 0.0F));
        PartDefinition modelPartData3 = modelPartData1.addChild("leftParticle", CubeListBuilder.create(), PartPose.pivot(-6.0F, -9.0F, 0.0F));
        modelPartData3.addChild("bone8", CubeListBuilder.create().uv(0, 44).cuboid(-4.0F, -28.0F, -2.0F, 2.0F, 2.0F, 2.0F), PartPose.pivot(6.0F, 24.0F, 0.0F));
        modelPartData3.addChild("bone9", CubeListBuilder.create().uv(0, 40).cuboid(-2.0F, -30.0F, 0.0F, 2.0F, 2.0F, 2.0F), PartPose.pivot(6.0F, 24.0F, 0.0F));
        modelPartData3.addChild("bone10", CubeListBuilder.create().uv(38, 4).cuboid(-8.0F, -28.0F, -2.0F, 2.0F, 2.0F, 2.0F), PartPose.pivot(6.0F, 24.0F, 0.0F));
        modelPartData3.addChild("bone11", CubeListBuilder.create().uv(0, 32).cuboid(-12.0F, -24.0F, 0.0F, 2.0F, 2.0F, 2.0F), PartPose.pivot(6.0F, 24.0F, 0.0F));
        modelPartData3.addChild("bone12", CubeListBuilder.create().uv(0, 28).cuboid(-12.0F, -18.0F, 0.0F, 2.0F, 2.0F, 2.0F), PartPose.pivot(6.0F, 24.0F, 0.0F));
        modelPartData3.addChild("bone13", CubeListBuilder.create().uv(38, 0).cuboid(-15.0F, -30.0F, 0.0F, 2.0F, 2.0F, 2.0F), PartPose.pivot(6.0F, 24.0F, 0.0F));
        return LayerDefinition.of(modelData, 128, 128);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.rightArm, this.leftArm, this.body);
    }

    @Override
    public void setAngles(T shadow, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

        this.body.pivotY = 8.0F + Mth.sin(animationProgress / 12.5663706F) / 2.0F;
        this.rightArm.pivotY = 1.0F + Mth.sin(animationProgress / 12.5663706F) / 1.5F;
        this.leftArm.pivotY = 1.0F + Mth.sin(animationProgress / 12.5663706F) / 1.5F;
        this.bone.pivotY = 24F + Mth.cos(2.6F + animationProgress / 12.5663706F) / 1.1F;
        this.bone2.pivotY = 24F + Mth.sin(2.4F + animationProgress / 12.5663706F) / 1.3F;
        this.bone3.pivotY = 24F + Mth.cos(2.2F + animationProgress / 12.5663706F) / 1.45F;
        this.bone4.pivotY = 24F + Mth.sin(2.0F + animationProgress / 12.5663706F) / 1.6F;
        this.bone5.pivotY = 24F + Mth.cos(1.8F + animationProgress / 12.5663706F) / 1.25F;
        this.bone6.pivotY = 24F + Mth.cos(1.6F + animationProgress / 12.5663706F) / 1.4F;
        this.bone7.pivotY = 24F + Mth.sin(1.4F + animationProgress / 12.5663706F);
        this.bone8.pivotY = 24F + Mth.sin(1.2F + animationProgress / 12.5663706F) / 1.25F;
        this.bone9.pivotY = 24F + Mth.cos(1.0F + animationProgress / 12.5663706F) / 1.35F;
        this.bone10.pivotY = 24F + Mth.cos(0.8F + animationProgress / 12.5663706F) / 1.2F;
        this.bone11.pivotY = 24F + Mth.sin(0.6F + animationProgress / 12.5663706F) / 0.9F;
        this.bone12.pivotY = 24F + Mth.cos(0.4F + animationProgress / 12.5663706F) / 1.15F;
        this.rightArm.roll = 0.0F;
        this.leftArm.roll = 0.0F;

        if (shadow.getDataTracker().get(VoidShadowEntity.IS_THROWING_BLOCKS)) {
            throwingBlocksTicker = Mth.clamp(throwingBlocksTicker + 0.05817666666666F, 0.0F, 1.7453F);
        } else {
            throwingBlocksTicker = Mth.clamp(throwingBlocksTicker - 0.05817666666666F, 0.0F, 1.7453F);
        }

        this.rightArm.pitch = -throwingBlocksTicker;
        this.leftArm.pitch = this.rightArm.pitch;
        this.rightArm.yaw = -throwingBlocksTicker * 0.20002291869F;
        this.leftArm.yaw = throwingBlocksTicker * 0.20002291869F;

        if (shadow.getDataTracker().get(VoidShadowEntity.HOVERING_MAGIC_HANDS)) {
            this.rightArm.roll = -2.4871F - Mth.cos(animationProgress * 0.3662F) * 0.1F;
            this.leftArm.roll = 2.4871F + Mth.cos(animationProgress * 0.3662F) * 0.1F;
            this.rightArm.pitch = -Mth.cos(animationProgress * 0.1662F) * 0.25F;
            this.leftArm.pitch = Mth.cos(animationProgress * 0.1662F) * 0.25F;
            // this.rightAttackingArm.pitch = Mth.cos(animationProgress * 0.6662F) *
            // 0.25F;
            // this.leftAttackingArm.pitch = Mth.cos(animationProgress * 0.6662F) *
            // 0.25F;
        } else if (shadow.getDataTracker().get(VoidShadowEntity.CIRCLING_HANDS)) {
            this.rightArm.roll = -1.5708F;
            this.leftArm.roll = 1.5708F;
        }

        // this.rightArm.pitch = this.handSwingProgress;

        // Summon attack or change block attack
        // setRotationAngle(leftArm, 0.0F, 0.0F, 2.4871F);
        // setRotationAngle(rightArm, 0.0F, 0.0F, -2.4871F);

        // Throw blocks attack needs floatings
        // setRotationAngle(rightArm, -1.7453F, -0.3491F, 0.0F);
        // setRotationAngle(leftArm, -1.7453F, 0.3491F, 0.0F);
        // 0,011636666666
    }

}

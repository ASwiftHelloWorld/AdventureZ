package net.adventurez.entity.model;

import com.google.common.collect.ImmutableList;

import net.adventurez.entity.AmethystGolemEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.ListModel;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class AmethystGolemModel<T extends AmethystGolemEntity> extends ListModel<T> {
    private final ModelPart root;
    private final ModelPart amethyst_golem;
    private final ModelPart body;
    private final ModelPart body2;
    private final ModelPart amethyst2;
    private final ModelPart amethyst2_r1;
    private final ModelPart amethyst3;
    private final ModelPart head;
    private final ModelPart head_r1;
    private final ModelPart arms;
    private final ModelPart left_arm;
    private final ModelPart amethyst;
    private final ModelPart right_arm;
    private final ModelPart amethyst4;
    private final ModelPart legs;
    private final ModelPart left_leg;
    private final ModelPart right_leg;

    public AmethystGolemModel(ModelPart root) {
        this.root = root.getChild("root");
        this.amethyst_golem = this.root.getChild("amethyst_golem");
        this.legs = this.amethyst_golem.getChild("legs");
        this.right_leg = this.legs.getChild("right_leg");
        this.left_leg = this.legs.getChild("left_leg");
        this.arms = this.amethyst_golem.getChild("arms");
        this.right_arm = this.arms.getChild("right_arm");
        this.amethyst4 = this.right_arm.getChild("amethyst4");
        this.left_arm = this.arms.getChild("left_arm");
        this.amethyst = this.left_arm.getChild("amethyst");
        this.head = this.amethyst_golem.getChild("head");
        this.head_r1 = this.head.getChild("head_r1");
        this.body = this.amethyst_golem.getChild("body");
        this.body2 = this.body.getChild("body2");
        this.amethyst3 = this.body2.getChild("amethyst3");
        this.amethyst2 = this.body2.getChild("amethyst2");
        this.amethyst2_r1 = this.amethyst2.getChild("amethyst2_r1");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        PartDefinition modelPartData1 = modelPartData.addChild("root", CubeListBuilder.create(), PartPose.pivot(0.0F, 24.0F, 0.0F));
        PartDefinition modelPartData2 = modelPartData1.addChild("amethyst_golem", CubeListBuilder.create(), PartPose.pivot(0.0F, 0.0F, 0.0F));
        PartDefinition modelPartData3 = modelPartData2.addChild("body", CubeListBuilder.create().uv(24, 24).cuboid(-5.5F, -7.0F, -4.0F, 11.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)),
                PartPose.pivot(0.0F, -15.0F, 0.0F));
        PartDefinition modelPartData4 = modelPartData3.addChild("body2",
                CubeListBuilder.create().uv(0, 0).cuboid(-9.0F, -11.0F, -6.0F, 18.0F, 12.0F, 12.0F).uv(60, 74).cuboid(2.5F, -7.0F, 3.5F, 7.0F, 6.0F, 3.0F).uv(0, 0)
                        .cuboid(-0.5F, -6.0F, 3.5F, 3.0F, 4.0F, 3.0F).uv(0, 7).cuboid(3.5F, -1.0F, 3.5F, 2.0F, 1.0F, 3.0F).uv(17, 54).cuboid(-9.5F, -10.5F, -6.5F, 8.0F, 12.0F, 7.0F).uv(67, 64)
                        .cuboid(-9.5F, -6.5F, 0.5F, 8.0F, 6.0F, 4.0F).uv(79, 18).cuboid(-9.5F, -8.5F, 0.5F, 8.0F, 2.0F, 2.0F),
                PartPose.pivot(0.0F, -7.0F, 0.0F));
        PartDefinition modelPartData5 = modelPartData4.addChild("amethyst2", CubeListBuilder.create(), PartPose.pivot(3.0F, -11.0F, 2.0F));
        modelPartData5.addChild("amethyst2_r1", CubeListBuilder.create().uv(76, 46).cuboid(-2.0F, -39.0F, 0.0F, 10.0F, 8.0F, 0.0F).uv(0, 63).cuboid(3.0F, -39.0F, -5.0F, 0.0F, 8.0F, 10.0F),
                PartPose.pivot(-3.0F, 32.0F, 0.0F));
        modelPartData4.addChild("amethyst3", CubeListBuilder.create().uv(40, 73).cuboid(-5.0F, -7.5F, 0.0F, 10.0F, 9.0F, 0.0F).uv(62, 27).cuboid(0.0F, -7.5F, -5.0F, 0.0F, 9.0F, 10.0F),
                PartPose.pivot(-8.0F, -5.0F, -6.0F));
        PartDefinition modelPartData6 = modelPartData2.addChild("head",
                CubeListBuilder.create().uv(52, 46).cuboid(-4.0F, -6.0F, -7.5F, 8.0F, 10.0F, 8.0F).uv(24, 40).cuboid(-4.5F, -0.5F, -8.0F, 9.0F, 5.0F, 9.0F),
                PartPose.pivot(0.0F, -29.0F, -5.0F));
        modelPartData6.addChild("head_r1", CubeListBuilder.create().uv(0, 24).cuboid(-1.0F, -1.0F, -1.5F, 2.0F, 2.0F, 1.0F), PartPose.pivot(0.0F, -1.0F, -7.0F));
        PartDefinition modelPartData7 = modelPartData2.addChild("arms", CubeListBuilder.create(), PartPose.pivot(0.0F, 2.0F, 0.0F));
        PartDefinition modelPartData8 = modelPartData7.addChild("left_arm", CubeListBuilder.create().uv(0, 24).cuboid(-1.0F, -2.5F, -3.0F, 6.0F, 27.0F, 6.0F).uv(0, 57)
                .cuboid(2.0F, 5.5F, -4.0F, 4.0F, 4.0F, 4.0F).uv(60, 0).cuboid(-1.5F, 14.5F, -3.5F, 7.0F, 11.0F, 7.0F), PartPose.pivot(10.0F, -31.0F, 0.0F));
        modelPartData8.addChild("amethyst", CubeListBuilder.create().uv(20, 73).cuboid(-5.0F, -7.5F, 0.0F, 10.0F, 9.0F, 0.0F).uv(47, 54).cuboid(0.0F, -7.5F, -5.0F, 0.0F, 9.0F, 10.0F),
                PartPose.pivot(4.0F, -1.0F, 0.0F));
        PartDefinition modelPartData9 = modelPartData7.addChild("right_arm", CubeListBuilder.create().uv(0, 24).cuboid(-5.0F, -2.5F, -3.0F, 6.0F, 27.0F, 6.0F, true).uv(0, 57)
                .cuboid(-6.0F, 6.5F, -4.0F, 4.0F, 4.0F, 4.0F, true).uv(60, 0).cuboid(-5.5F, 14.5F, -3.5F, 7.0F, 11.0F, 7.0F, true), PartPose.pivot(-10.0F, -31.0F, 0.0F));
        modelPartData9.addChild("amethyst4", CubeListBuilder.create().uv(20, 73).cuboid(-5.0F, -7.5F, 0.0F, 10.0F, 9.0F, 0.0F, true).uv(47, 54).cuboid(0.0F, -7.5F, -5.0F, 0.0F, 9.0F, 10.0F, true),
                PartPose.pivot(-4.0F, -1.0F, 0.0F));
        PartDefinition modelPartData10 = modelPartData2.addChild("legs", CubeListBuilder.create(), PartPose.pivot(0.0F, -13.0F, 0.0F));
        modelPartData10.addChild("left_leg", CubeListBuilder.create().uv(62, 18).cuboid(-2.5F, -1.0F, -3.0F, 6.0F, 14.0F, 5.0F), PartPose.pivot(4.0F, 0.0F, 0.0F));
        modelPartData10.addChild("right_leg", CubeListBuilder.create().uv(62, 18).cuboid(-3.5F, -1.0F, -3.0F, 6.0F, 14.0F, 5.0F, true), PartPose.pivot(-4.0F, 0.0F, 0.0F));
        return LayerDefinition.of(modelData, 128, 128);

    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.root);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.amethyst2.pitch = -0.1745F;
        this.amethyst2.yaw = 0.1745F;
        this.amethyst2_r1.yaw = 0.3927F;
        this.amethyst3.pitch = 1.2217F;
        this.amethyst3.yaw = 0.4363F;
        this.head_r1.pitch = -0.0436F;
        this.amethyst.roll = 0.6981F;
        this.amethyst4.roll = -0.6981F;
        this.head.yaw = headYaw * 0.0089453292F;
        this.head.pitch = headPitch * 0.0047453292F;
        this.right_leg.pitch = Mth.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance;
        this.left_leg.pitch = Mth.cos(limbAngle * 0.6662F) * 1.4F * limbDistance;
        this.right_arm.pitch = -Mth.cos(limbAngle * 0.6662F + 3.1415927F) * 2.0F * limbDistance * 0.3F;
        this.left_arm.pitch = Mth.cos(limbAngle * 0.6662F + 3.1415927F) * 2.0F * limbDistance * 0.3F;
        float k = Mth.sin(entity.handSwingProgress * 3.1415927F);
        if (k > 0) {
            this.right_arm.pitch = -k;
        }
        if (entity.getEntityData().get(AmethystGolemEntity.BACK_CRYSTALS) == 4) {
            amethyst.visible = true;
            amethyst2.visible = true;
            amethyst3.visible = true;
            amethyst4.visible = true;
        } else if (entity.getEntityData().get(AmethystGolemEntity.BACK_CRYSTALS) == 3) {
            amethyst.visible = true;
            amethyst2.visible = true;
            amethyst3.visible = false;
            amethyst4.visible = true;
        } else if (entity.getEntityData().get(AmethystGolemEntity.BACK_CRYSTALS) == 2) {
            amethyst.visible = true;
            amethyst2.visible = true;
            amethyst3.visible = false;
            amethyst4.visible = false;
        } else if (entity.getEntityData().get(AmethystGolemEntity.BACK_CRYSTALS) == 1) {
            amethyst.visible = false;
            amethyst2.visible = true;
            amethyst3.visible = false;
            amethyst4.visible = false;
        } else if (entity.getEntityData().get(AmethystGolemEntity.BACK_CRYSTALS) == 0) {
            amethyst.visible = false;
            amethyst2.visible = false;
            amethyst3.visible = false;
            amethyst4.visible = false;
        }
    }

}

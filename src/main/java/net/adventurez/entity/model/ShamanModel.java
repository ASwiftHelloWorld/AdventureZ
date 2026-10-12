package net.adventurez.entity.model;

import com.google.common.collect.ImmutableList;

import net.adventurez.entity.ShamanEntity;
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
public class ShamanModel<T extends ShamanEntity> extends ListModel<T> {
    private final ModelPart body1;
    private final ModelPart body2;
    private final ModelPart thighR;
    private final ModelPart kneeR;
    private final ModelPart thighL;
    private final ModelPart kneeL;
    private final ModelPart armL1;
    private final ModelPart shoulderskullL;
    private final ModelPart armR1;
    private final ModelPart shoulderskullR;
    private final ModelPart staffshaft1;
    private final ModelPart staffshaft2;
    private final ModelPart staffshaft3;
    private final ModelPart staffshaft4;
    private final ModelPart staffdetail5;
    private final ModelPart staffdetail6;
    private final ModelPart staffdetail1;
    private final ModelPart staffdetail2;
    private final ModelPart staffdetail3;
    private final ModelPart staffdetail4;
    private final ModelPart tail1;
    private final ModelPart tail2;
    private final ModelPart tail3;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart skullmask;
    private final ModelPart feathercrownM;
    private final ModelPart feathercrownL;
    private final ModelPart feathercrownR;

    public ShamanModel(ModelPart root) {
        this.body1 = root.getChild("body1");
        this.neck = this.body1.getChild("neck");
        this.head = this.neck.getChild("head");
        this.skullmask = this.head.getChild("skullmask");
        this.feathercrownR = this.skullmask.getChild("feathercrownR");
        this.feathercrownL = this.skullmask.getChild("feathercrownL");
        this.feathercrownM = this.skullmask.getChild("feathercrownM");
        this.jaw = this.head.getChild("jaw");
        this.tail1 = this.body1.getChild("tail1");
        this.tail2 = this.tail1.getChild("tail2");
        this.tail3 = this.tail2.getChild("tail3");
        this.armR1 = this.body1.getChild("armR1");
        this.staffshaft1 = this.armR1.getChild("staffshaft1");
        this.staffdetail1 = this.staffshaft1.getChild("staffdetail1");
        this.staffdetail3 = this.staffshaft1.getChild("staffdetail3");
        this.staffdetail2 = this.staffshaft1.getChild("staffdetail2");
        this.staffdetail4 = this.staffshaft1.getChild("staffdetail4");
        this.staffshaft2 = this.staffshaft1.getChild("staffshaft2");
        this.staffdetail6 = this.staffshaft2.getChild("staffdetail6");
        this.staffshaft3 = this.staffshaft2.getChild("staffshaft3");
        this.staffdetail5 = this.staffshaft3.getChild("staffdetail5");
        this.staffshaft4 = this.staffshaft3.getChild("staffshaft4");
        this.shoulderskullR = this.armR1.getChild("shoulderskullR");
        this.armL1 = this.body1.getChild("armL1");
        this.shoulderskullL = this.armL1.getChild("shoulderskullL");
        this.body2 = this.body1.getChild("body2");
        this.thighL = this.body2.getChild("thighL");
        this.kneeL = this.thighL.getChild("kneeL");
        this.thighR = this.body2.getChild("thighR");
        this.kneeR = this.thighR.getChild("kneeR");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition modelPartData1 = modelPartData.addChild("body1", CubeListBuilder.create().uv(0, 117).cuboid(-4.0F, -7.0F, -2.0F, 8.0F, 7.0F, 4.0F), PartPose.pivot(0.0F, 6.0F, 0.0F));
        PartDefinition modelPartData2 = modelPartData1.addChild("body2", CubeListBuilder.create().uv(30, 117).cuboid(-3.5F, 0.0F, -1.5F, 7.0F, 7.0F, 3.0F), PartPose.pivot(0.0F, -1.0F, 0.3F));
        PartDefinition modelPartData3 = modelPartData2.addChild("thighR", CubeListBuilder.create().uv(0, 70).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F), PartPose.pivot(2.0F, 6.0F, 0.0F));
        modelPartData3.addChild("kneeR", CubeListBuilder.create().uv(20, 70).cuboid(-2.0F, -1.0F, 0.0F, 4.0F, 9.0F, 4.0F), PartPose.pivot(0.0F, 5.0F, 0.5F));
        PartDefinition modelPartData4 = modelPartData2.addChild("thighL", CubeListBuilder.create().uv(0, 70).cuboid(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, true),
                PartPose.pivot(-2.0F, 6.0F, 0.0F));
        modelPartData4.addChild("kneeL", CubeListBuilder.create().uv(20, 70).cuboid(-2.0F, -1.0F, 0.0F, 4.0F, 9.0F, 4.0F, true), PartPose.pivot(0.0F, 5.0F, 0.5F));
        PartDefinition modelPartData5 = modelPartData1.addChild("armL1", CubeListBuilder.create().uv(0, 90).cuboid(-2.0F, -2.0F, -2.0F, 3.0F, 13.0F, 4.0F), PartPose.pivot(-5.0F, -5.0F, 0.0F));
        modelPartData5.addChild("shoulderskullL", CubeListBuilder.create().uv(20, 100).cuboid(-4.0F, -2.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.pivot(1.0F, -2.0F, 0.0F));
        PartDefinition modelPartData6 = modelPartData1.addChild("armR1", CubeListBuilder.create().uv(0, 90).cuboid(-1.0F, -2.0F, -2.0F, 3.0F, 13.0F, 4.0F, true),
                PartPose.pivot(5.0F, -5.0F, 0.0F));
        modelPartData6.addChild("shoulderskullR", CubeListBuilder.create().uv(20, 100).cuboid(0.0F, -2.0F, -2.0F, 4.0F, 3.0F, 4.0F, true), PartPose.pivot(-1.0F, -2.0F, 0.0F));
        PartDefinition modelPartData7 = modelPartData6.addChild("staffshaft1", CubeListBuilder.create().uv(59, 0).cuboid(-0.5F, 0.0F, -10.0F, 1.0F, 1.0F, 33.0F, new CubeDeformation(0.01F)),
                PartPose.pivot(1.0F, 9.0F, 0.0F));
        PartDefinition modelPartData8 = modelPartData7.addChild("staffshaft2", CubeListBuilder.create().uv(0, 30).cuboid(-0.5F, -4.0F, -1.0F, 1.0F, 4.0F, 1.0F),
                PartPose.pivot(0.0F, 0.4F, -9.2F));
        PartDefinition modelPartData9 = modelPartData8.addChild("staffshaft3", CubeListBuilder.create().uv(10, 30).cuboid(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.02F)),
                PartPose.pivot(0.0F, -3.4F, -0.4F));
        modelPartData9.addChild("staffshaft4", CubeListBuilder.create().uv(20, 30).cuboid(-0.5F, -4.0F, 0.0F, 1.0F, 4.0F, 1.0F), PartPose.pivot(0.0F, -3.8F, 0.7F));
        modelPartData9.addChild("staffdetail5", CubeListBuilder.create().uv(20, 20).cuboid(0.0F, 0.0F, -3.5F, 0.0F, 2.0F, 5.0F), PartPose.pivot(0.0F, -3.1F, 0.8F));
        modelPartData8.addChild("staffdetail6", CubeListBuilder.create().uv(0, 20).cuboid(0.0F, 0.0F, -3.5F, 0.0F, 2.0F, 5.0F), PartPose.pivot(0.0F, -3.0F, 0.0F));
        modelPartData7.addChild("staffdetail1", CubeListBuilder.create().uv(80, 0).cuboid(0.0F, 0.0F, -1.5F, 0.0F, 5.0F, 3.0F).uv(70, 5).cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F),
                PartPose.pivot(-0.7F, 0.5F, -9.8F));
        modelPartData7.addChild("staffdetail2", CubeListBuilder.create().uv(80, 0).cuboid(0.0F, 0.0F, -1.5F, 0.0F, 5.0F, 3.0F), PartPose.pivot(0.7F, 0.5F, -9.8F));
        modelPartData7.addChild("staffdetail3", CubeListBuilder.create().uv(70, 5).cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 5.0F, 0.0F), PartPose.pivot(0.0F, -0.4F, -9.8F));
        modelPartData7.addChild("staffdetail4", CubeListBuilder.create(), PartPose.pivot(0.0F, 1.3F, -9.8F));
        PartDefinition modelPartData10 = modelPartData1.addChild("bonearmor1", CubeListBuilder.create().uv(30, 50).cuboid(-4.0F, -3.5F, -2.0F, 8.0F, 7.0F, 4.0F, new CubeDeformation(0.2F)),
                PartPose.pivot(0.0F, -3.2F, 0.0F));
        modelPartData10.addChild("spine", CubeListBuilder.create().uv(40, 70).cuboid(-0.5F, -3.5F, 0.0F, 1.0F, 7.0F, 2.0F), PartPose.pivot(0.0F, 0.0F, 2.0F));
        PartDefinition modelPartData11 = modelPartData1.addChild("tail1", CubeListBuilder.create().uv(100, 40).cuboid(-2.5F, -2.0F, -1.0F, 5.0F, 4.0F, 6.0F), PartPose.pivot(0.0F, 5.0F, 0.0F));
        PartDefinition modelPartData12 = modelPartData11.addChild("tail2", CubeListBuilder.create().uv(100, 60).cuboid(-2.0F, -1.5F, -1.0F, 4.0F, 3.0F, 6.0F),
                PartPose.pivot(0.0F, -0.1F, 5.0F));
        modelPartData12.addChild("tail3", CubeListBuilder.create().uv(100, 80).cuboid(-1.5F, -1.0F, -1.0F, 3.0F, 2.0F, 5.0F), PartPose.pivot(0.0F, -0.2F, 5.0F));
        PartDefinition modelPartData13 = modelPartData1.addChild("neck", CubeListBuilder.create().uv(40, 0).cuboid(-1.5F, -4.0F, -1.5F, 3.0F, 4.0F, 3.0F), PartPose.pivot(0.0F, -5.7F, 0.5F));
        PartDefinition modelPartData14 = modelPartData13.addChild("head", CubeListBuilder.create().uv(60, 115).cuboid(-3.0F, -6.0F, 0.0F, 6.0F, 7.0F, 3.0F, new CubeDeformation(-0.1F)),
                PartPose.pivot(0.0F, -4.0F, 0.0F));
        PartDefinition modelPartData15 = modelPartData14.addChild("jaw", CubeListBuilder.create().uv(60, 100).cuboid(-3.0F, -6.0F, -2.0F, 6.0F, 7.0F, 2.0F), PartPose.pivot(0.0F, 0.0F, 0.0F));
        modelPartData15.addChild("lowerteeth", CubeListBuilder.create().uv(20, 0).cuboid(-3.0F, -3.5F, 0.0F, 6.0F, 7.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.pivot(0.0F, -2.5F, -0.5F));
        PartDefinition modelPartData16 = modelPartData14.addChild("skullmask", CubeListBuilder.create().uv(90, 115).cuboid(-3.0F, -3.0F, -1.2F, 6.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)),
                PartPose.pivot(0.0F, -3.0F, 0.8F));
        modelPartData16.addChild("feathercrownM", CubeListBuilder.create().uv(-4, 12).cuboid(-3.5F, 0.0F, 0.0F, 7.0F, 0.0F, 6.0F), PartPose.pivot(0.0F, 3.0F, 2.0F));
        modelPartData16.addChild("feathercrownL", CubeListBuilder.create().uv(20, 14).cuboid(-6.0F, 0.0F, -2.5F, 6.0F, 0.0F, 5.0F, true), PartPose.pivot(-2.5F, 4.0F, 1.3F));
        modelPartData16.addChild("feathercrownR", CubeListBuilder.create().uv(20, 14).cuboid(0.0F, 0.0F, -2.5F, 6.0F, 0.0F, 5.0F), PartPose.pivot(2.5F, 4.0F, 1.3F));
        modelPartData14.addChild("topteeth", CubeListBuilder.create().uv(0, 0).cuboid(-3.0F, -3.5F, -2.0F, 6.0F, 7.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.pivot(0.0F, -2.5F, 0.0F));
        return LayerDefinition.of(modelData, 128, 128);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.body1);
    }

    @Override
    public void setAngles(T entity, float f, float g, float h, float i, float j) {
        this.body1.pitch = 0.4887F;
        this.body2.pitch = -0.2094F;
        this.thighR.pitch = -0.7156F + Mth.cos(f * 0.6662F) * 1.4F * g * 0.5F;
        this.thighR.yaw = -0.1745F;
        this.thighR.roll = -0.0873F;
        this.kneeR.pitch = 0.4014F;
        this.kneeR.roll = 0.1571F;
        this.thighL.pitch = -0.7156F + Mth.cos(f * 0.6662F + 3.1415927F) * 1.4F * g * 0.5F;
        this.thighL.yaw = 0.1745F;
        this.thighL.roll = 0.0873F;
        this.kneeL.pitch = 0.4014F;
        this.kneeL.roll = -0.1571F;
        this.armL1.pitch = -0.4712F + Mth.cos(f * 0.6662F) * 2.0F * g * 0.2F;
        this.armL1.roll = 0.3316F;
        this.shoulderskullL.roll = -0.4189F;
        this.armR1.pitch = -2.0246F + Mth.cos(f * 0.6662F + 3.1415927F) * 2.0F * g * 0.1F;
        this.armR1.yaw = -0.6109F;
        this.armR1.roll = 0.192F;
        this.shoulderskullR.roll = 0.4189F;
        this.staffshaft2.pitch = 0.5934F;
        this.staffshaft3.pitch = 1.3614F;
        this.staffshaft4.pitch = 1.3963F;
        this.staffdetail5.pitch = 1.5533F;
        this.staffdetail5.yaw = -0.0349F;
        this.staffdetail6.pitch = 1.5533F;
        this.staffdetail6.yaw = -0.0349F;
        this.staffdetail1.pitch = 1.5533F;
        this.staffdetail1.yaw = -0.4363F;
        this.staffdetail2.pitch = 1.5533F;
        this.staffdetail2.yaw = 0.4363F;
        this.staffdetail3.pitch = 1.85F;
        this.staffdetail4.pitch = 1.1519F;
        this.tail1.pitch = -0.7854F + Mth.cos(f * 0.6662F) * 1.4F * g * 0.1F;
        this.tail1.yaw = -0.1571F + Mth.cos(f * 0.6662F + 3.1415927F) * 1.4F * g * 0.1F;
        this.tail2.pitch = 0.2094F + Mth.cos(f * 0.6662F) * 1.4F * g * 0.05F;
        this.tail2.yaw = -0.1222F + Mth.cos(f * 0.6662F + 3.1415927F) * 1.4F * g * 0.05F;
        this.tail3.pitch = 0.2618F + Mth.cos(f * 0.6662F) * 1.4F * g * 0.01F;
        this.tail3.yaw = -0.1745F + Mth.cos(f * 0.6662F + 3.1415927F) * 1.4F * g * 0.01F;
        this.neck.pitch = 0.5411F;
        this.head.yaw = i * 0.0079453292F;
        this.head.roll = -i * 0.0079453292F;
        this.head.pitch = 0.5585F + j * 0.0107453292F;
        this.jaw.pitch = 0.5585F;
        this.skullmask.pitch = 0.1571F;
        this.feathercrownM.pitch = -0.5411F;
        this.feathercrownL.pitch = -0.192F;
        this.feathercrownL.yaw = 0.5411F;
        this.feathercrownL.roll = -0.6807F;
        this.feathercrownR.pitch = -0.192F;
        this.feathercrownR.yaw = -0.5411F;
        this.feathercrownR.roll = 0.6807F;

        if (entity.isSpellcasting()) {
            this.armR1.pitch = -2.0246F + Mth.cos(h * 0.6662F) * 0.1F - 0.6F;
        }
    }

}

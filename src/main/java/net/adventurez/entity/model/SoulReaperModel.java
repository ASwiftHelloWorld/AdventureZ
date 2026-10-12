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
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class SoulReaperModel<T extends Mob & RangedAttackMob> extends HumanoidModel<T> {

    public SoulReaperModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = HumanoidModel.getModelData(CubeDeformation.NONE, 0.0F);
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addChild("body", CubeListBuilder.create().uv(0, 16).cuboid(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F), PartPose.pivot(0.0F, 0.0F, 0.0F));
        modelPartData.addChild("head", CubeListBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.pivot(0.0F, 0.0F, 0.0F));
        modelPartData.addChild("right_arm",
                CubeListBuilder.create().uv(8, 32).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F).uv(47, 11).cuboid(-2.0F, -2.0F, -1.0F, 1.0F, 5.0F, 2.0F).uv(47, 20)
                        .cuboid(-2.0F, 3.0F, -1.0F, 1.0F, 1.0F, 1.0F).uv(28, 16).cuboid(-2.0F, -1.0F, 1.0F, 3.0F, 3.0F, 1.0F).uv(35, 41).cuboid(-3.0F, -1.0F, 1.0F, 1.0F, 2.0F, 1.0F).uv(32, 8)
                        .cuboid(-3.0F, -2.0F, -1.0F, 1.0F, 3.0F, 2.0F).uv(32, 25).cuboid(-4.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F).uv(36, 16).cuboid(-4.0F, -4.0F, -1.0F, 1.0F, 4.0F, 1.0F).uv(40, 37)
                        .cuboid(-3.0F, -2.0F, -2.0F, 1.0F, 2.0F, 1.0F).uv(20, 16).cuboid(-3.0F, -3.0F, -2.0F, 1.0F, 1.0F, 3.0F).uv(52, 20).cuboid(-3.0F, -4.0F, 0.0F, 1.0F, 1.0F, 1.0F).uv(32, 13)
                        .cuboid(-2.0F, -3.0F, -1.0F, 1.0F, 1.0F, 2.0F).uv(59, 20).cuboid(-3.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F).uv(38, 21).cuboid(-2.0F, -2.0F, 1.0F, 2.0F, 1.0F, 1.0F).uv(41, 26)
                        .cuboid(-1.0F, 2.0F, 1.0F, 1.0F, 1.0F, 1.0F).uv(24, 20).cuboid(-2.0F, -1.0F, -2.0F, 3.0F, 3.0F, 1.0F).uv(38, 29).cuboid(-2.0F, -2.0F, -2.0F, 2.0F, 1.0F, 1.0F).uv(40, 33)
                        .cuboid(-1.0F, 2.0F, -2.0F, 1.0F, 1.0F, 1.0F),
                PartPose.pivot(-5.0F, 2.0F, 0.0F));
        modelPartData.addChild("left_arm",
                CubeListBuilder.create().uv(16, 32).cuboid(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F).uv(49, 1).cuboid(1.0F, -2.0F, -1.0F, 1.0F, 5.0F, 2.0F).uv(42, 42)
                        .cuboid(1.0F, 3.0F, 0.0F, 1.0F, 1.0F, 1.0F).uv(32, 20).cuboid(2.0F, -2.0F, -1.0F, 1.0F, 3.0F, 2.0F).uv(41, 7).cuboid(2.0F, -1.0F, 1.0F, 1.0F, 2.0F, 1.0F).uv(42, 2)
                        .cuboid(2.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F).uv(32, 36).cuboid(3.0F, -5.0F, 0.0F, 1.0F, 5.0F, 1.0F).uv(37, 24).cuboid(3.0F, -4.0F, -1.0F, 1.0F, 4.0F, 1.0F).uv(41, 23)
                        .cuboid(2.0F, -2.0F, -2.0F, 1.0F, 2.0F, 1.0F).uv(56, 4).cuboid(2.0F, -3.0F, -2.0F, 1.0F, 1.0F, 3.0F).uv(42, 0).cuboid(2.0F, -4.0F, 0.0F, 1.0F, 1.0F, 1.0F).uv(34, 29)
                        .cuboid(1.0F, -3.0F, -1.0F, 1.0F, 1.0F, 2.0F).uv(32, 32).cuboid(-1.0F, -1.0F, -2.0F, 3.0F, 3.0F, 1.0F).uv(39, 31).cuboid(0.0F, -2.0F, -2.0F, 2.0F, 1.0F, 1.0F).uv(28, 42)
                        .cuboid(0.0F, 2.0F, -2.0F, 1.0F, 1.0F, 1.0F).uv(56, 11).cuboid(-1.0F, -1.0F, 1.0F, 3.0F, 3.0F, 1.0F).uv(39, 35).cuboid(0.0F, -2.0F, 1.0F, 2.0F, 1.0F, 1.0F).uv(24, 42)
                        .cuboid(0.0F, 2.0F, 1.0F, 1.0F, 1.0F, 1.0F),
                PartPose.pivot(5.0F, 2.0F, 0.0F));
        modelPartData.addChild("right_leg",
                CubeListBuilder.create().uv(24, 24).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F).uv(39, 40).cuboid(0.0F, 0.0F, 1.0F, 1.0F, 2.0F, 1.0F).uv(32, 4)
                        .cuboid(0.0F, 0.0F, -2.0F, 1.0F, 2.0F, 1.0F).uv(28, 38).cuboid(-1.0F, 0.0F, 1.0F, 1.0F, 3.0F, 1.0F).uv(24, 38).cuboid(-1.0F, 0.0F, -2.0F, 1.0F, 3.0F, 1.0F).uv(49, 24)
                        .cuboid(-2.0F, 0.0F, -1.0F, 1.0F, 3.0F, 1.0F).uv(36, 36).cuboid(-2.0F, 0.0F, 0.0F, 1.0F, 4.0F, 1.0F),
                PartPose.pivot(-2.0F, 12.0F, 0.0F));
        modelPartData.addChild("left_leg",
                CubeListBuilder.create().uv(0, 32).cuboid(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F).uv(36, 4).cuboid(1.0F, 0.0F, 0.0F, 1.0F, 4.0F, 1.0F).uv(39, 13)
                        .cuboid(1.0F, 0.0F, -1.0F, 1.0F, 3.0F, 1.0F).uv(38, 9).cuboid(0.0F, 0.0F, -2.0F, 1.0F, 3.0F, 1.0F).uv(38, 0).cuboid(0.0F, 0.0F, 1.0F, 1.0F, 3.0F, 1.0F).uv(40, 17)
                        .cuboid(-1.0F, 0.0F, -2.0F, 1.0F, 2.0F, 1.0F).uv(40, 4).cuboid(-1.0F, 0.0F, 1.0F, 1.0F, 2.0F, 1.0F),
                PartPose.pivot(2.0F, 12.0F, 0.0F));
        modelPartData.addChild("hat", CubeListBuilder.create().cuboid(0F, 0F, 0F, 0F, 0F, 0F), PartPose.pivot(0.0F, 0.0F, 0.0F));

        return LayerDefinition.of(modelData, 64, 64);
    }

    @Override
    public void animateModel(T mobEntity, float f, float g, float h) {
        this.rightArmPose = HumanoidModel.ArmPose.EMPTY;
        this.leftArmPose = HumanoidModel.ArmPose.EMPTY;
        ItemStack itemStack = mobEntity.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemStack.getItem() == Items.BOW && mobEntity.isAttacking() && !this.riding) {
            this.rightArmPose = HumanoidModel.ArmPose.BOW_AND_ARROW;
        }
        super.animateModel((T) mobEntity, f, g, h);
    }

    @Override
    public void setAngles(T mobEntity, float f, float g, float h, float i, float j) {
        super.setAngles((T) mobEntity, f, g, h, i, j);
        ItemStack itemStack = mobEntity.getMainHandItem();
        if (mobEntity.isAttacking() && (itemStack.isEmpty() || itemStack.getItem() != Items.BOW) && !this.riding) {
            float k = Mth.sin(this.handSwingProgress * 3.1415927F);
            float l = Mth.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * 3.1415927F);
            this.rightArm.roll = 0.0F;
            this.leftArm.roll = 0.0F;
            this.rightArm.yaw = -(0.1F - k * 0.6F);
            this.leftArm.yaw = 0.1F - k * 0.6F;
            this.rightArm.pitch = -1.2707964F;
            this.leftArm.pitch = Mth.cos(f * 0.6662F) * g * 0.5F;
            ModelPart var10000 = this.rightArm;
            var10000.pitch -= k * 1.2F - l * 0.4F;
        }
        if (this.riding) {
            this.rightArm.pitch = 0.3F;
            this.leftArm.pitch = 0.3F;
            if (mobEntity.isAttacking()) {
                if (itemStack.getItem() == Items.BOW) {
                    this.rightArm.yaw = -0.1F + this.head.yaw - 0.4F;
                    this.leftArm.yaw = 0.1F + this.head.yaw;
                    this.rightArm.pitch = -1.0F + this.head.pitch;
                    this.leftArm.pitch = -1.0F + this.head.pitch;
                } else {
                    float k = Mth.sin(this.handSwingProgress * 3.1415927F);
                    float l = Mth.sin((1.0F - (1.0F - this.handSwingProgress) * (1.0F - this.handSwingProgress)) * 3.1415927F);
                    this.rightArm.roll = 0.0F;
                    this.leftArm.roll = 0.0F;
                    this.rightArm.yaw = -(0.1F - k * 0.6F);
                    this.leftArm.yaw = 0.1F - k * 0.6F;
                    this.rightArm.pitch = -0.7F;
                    this.leftArm.pitch = 0.3F;
                    ModelPart var10000 = this.rightArm;
                    var10000.pitch -= k * 1.2F - l * 0.4F;
                }
            }
            this.body.pivotY = 5.2F;
            this.head.pivotY = 5.2F;
            this.rightArm.pivotY = 5.2F;
            this.leftArm.pivotY = 5.2F;
            this.rightLeg.pivotY = rightLeg.pivotY + 4F;
            this.leftLeg.pivotY = leftLeg.pivotY + 4F;
            ModelPart var10000 = this.rightArm;
            var10000 = this.rightArm;
            var10000.pitch += -0.62831855F;
            var10000 = this.leftArm;
            var10000.pitch += -0.62831855F;
            this.rightLeg.pitch = -1.4137167F;
            this.rightLeg.yaw = 0.31415927F;
            this.rightLeg.roll = 0.07853982F;
            this.leftLeg.pitch = -1.4137167F;
            this.leftLeg.yaw = -0.31415927F;
            this.leftLeg.roll = -0.07853982F;
        }

    }

    @Override
    public void setArmAngle(HumanoidArm arm, PoseStack matrices) {
        float f = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
        ModelPart modelPart = this.getArm(arm);
        modelPart.pivotX += f;
        modelPart.rotate(matrices);
        modelPart.pivotX -= f;
    }
}

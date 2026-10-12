package net.adventurez.entity.model;

import com.google.common.collect.ImmutableList;

import net.adventurez.entity.BrownFungusEntity;
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
public class BrownFungusModel<T extends BrownFungusEntity> extends ListModel<T> {
    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftFoot;
    private final ModelPart rightFoot;

    public BrownFungusModel(ModelPart root) {
        this.head = root.getChild("head");
        this.body = root.getChild("body");
        this.leftFoot = root.getChild("leftFoot");
        this.rightFoot = root.getChild("rightFoot");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addChild("head", CubeListBuilder.create().uv(0, 0).cuboid(-8.5F, -5.204F, -8.5F, 17.0F, 7.0F, 16.0F), PartPose.pivot(0.0F, 10.0F, 0.0F));
        modelPartData.addChild("body", CubeListBuilder.create().uv(0, 23).cuboid(-4.0F, -4.0F, -3.0F, 8.0F, 7.0F, 5.0F), PartPose.pivot(0.0F, 15.0F, 0.0F));
        modelPartData.addChild("leftFoot", CubeListBuilder.create().uv(26, 26).cuboid(-1.0F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), PartPose.pivot(-2.0F, 18.0F, -0.5F));
        modelPartData.addChild("rightFoot", CubeListBuilder.create().uv(0, 0).cuboid(-2.0F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F), PartPose.pivot(2.0F, 18.0F, -0.5F));
        return LayerDefinition.of(modelData, 128, 128);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.head, this.body, this.leftFoot, this.rightFoot);
    }

    @Override
    public void setAngles(T fungus, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.pitch = -0.1745F;
        this.rightFoot.pitch = Mth.cos(limbAngle * 0.6662F) * 1.4F * limbDistance * 0.5F;
        this.leftFoot.pitch = Mth.cos(limbAngle * 0.6662F + 3.1415927F) * 1.4F * limbDistance * 0.5F;

        float k = Mth.sin(this.handSwingProgress * 3.1415927F);
        if (k > 0) {
            this.head.pitch = -k * 1.2F;
        }

    }

}

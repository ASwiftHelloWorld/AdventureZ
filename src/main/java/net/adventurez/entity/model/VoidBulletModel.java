package net.adventurez.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.ListModel;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class VoidBulletModel<T extends Entity> extends ListModel<T> {
    private final ModelPart body;

    public VoidBulletModel(ModelPart root) {
        this.body = root.getChild("body");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addChild("body",
                CubeListBuilder.create().uv(18, 20).cuboid(-1.0F, -1.0F, -4.0F, 2.0F, 2.0F, 1.0F).uv(12, 20).cuboid(-1.0F, -1.0F, 3.0F, 2.0F, 2.0F, 1.0F).uv(16, 5)
                        .cuboid(-2.0F, -2.0F, -3.0F, 4.0F, 4.0F, 1.0F).uv(15, 15).cuboid(-3.0F, -2.0F, -2.0F, 6.0F, 4.0F, 1.0F).uv(16, 12).cuboid(-2.0F, -3.0F, -2.0F, 4.0F, 1.0F, 1.0F).uv(16, 10)
                        .cuboid(-2.0F, 2.0F, -2.0F, 4.0F, 1.0F, 1.0F).uv(0, 16).cuboid(-2.0F, -2.0F, 1.0F, 4.0F, 4.0F, 2.0F).uv(0, 0).cuboid(-3.0F, -3.0F, -1.0F, 6.0F, 6.0F, 2.0F),
                PartPose.pivot(0.0F, 21.0F, 0.0F));
        return LayerDefinition.of(modelData, 32, 32);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.body);
    }

    @Override
    public void setAngles(T bullet, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.body.yaw = headYaw * 0.017453292F;
        this.body.pitch = headPitch * 0.017453292F;
    }
}
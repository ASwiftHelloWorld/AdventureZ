package net.adventurez.entity.model;

import com.google.common.collect.ImmutableList;
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
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public class AmethystShardModel<T extends Entity> extends ListModel<T> {
    private final ModelPart shard;
    private final ModelPart cube_r1;
    private final ModelPart cube_r2;
    private final ModelPart cube_r3;
    private final ModelPart cube_r4;
    private final ModelPart cube_r5;
    private final ModelPart cube_r6;

    public AmethystShardModel(ModelPart root) {
        this.shard = root.getChild("shard");
        this.cube_r6 = this.shard.getChild("cube_r6");
        this.cube_r5 = this.shard.getChild("cube_r5");
        this.cube_r4 = this.shard.getChild("cube_r4");
        this.cube_r3 = this.shard.getChild("cube_r3");
        this.cube_r2 = this.shard.getChild("cube_r2");
        this.cube_r1 = this.shard.getChild("cube_r1");
    }

    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();
        PartDefinition modelPartData1 = modelPartData.addChild("shard", CubeListBuilder.create().uv(0, 0).cuboid(-1.0F, -7.0F, -2.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.2F)),
                PartPose.pivot(0.0F, 19.0F, 0.0F));
        modelPartData1.addChild("cube_r1", CubeListBuilder.create().uv(8, 15).cuboid(0.0F, -10.0F, -1.0F, 1.0F, 11.0F, 1.0F), PartPose.pivot(2.0F, 4.0F, 1.0F));
        modelPartData1.addChild("cube_r2", CubeListBuilder.create().uv(12, 20).cuboid(-1.0F, -8.0F, -1.0F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.23F)), PartPose.pivot(0.0F, 4.0F, -2.0F));
        modelPartData1.addChild("cube_r3", CubeListBuilder.create().uv(12, 12).cuboid(-1.0F, -5.0F, 0.0F, 3.0F, 6.0F, 2.0F, new CubeDeformation(-0.17F)), PartPose.pivot(0.0F, 4.0F, -3.0F));
        modelPartData1.addChild("cube_r4", CubeListBuilder.create().uv(12, 0).cuboid(-1.0F, -9.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.15F)), PartPose.pivot(1.0F, 4.0F, 1.0F));
        modelPartData1.addChild("cube_r5", CubeListBuilder.create().uv(0, 15).cuboid(-1.0F, -8.0F, -2.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.22F)), PartPose.pivot(2.0F, 5.0F, 0.0F));
        modelPartData1.addChild("cube_r6", CubeListBuilder.create().uv(20, 0).cuboid(0.0F, -6.0F, -1.0F, 1.0F, 7.0F, 2.0F, new CubeDeformation(0.4F)), PartPose.pivot(-2.0F, 4.0F, 0.0F));
        return LayerDefinition.of(modelData, 32, 32);
    }

    @Override
    public Iterable<ModelPart> getParts() {
        return ImmutableList.of(this.shard);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.shard.yaw = headYaw * 0.017453292F;
        this.shard.roll = headPitch * 0.17453292F;
        this.cube_r1.pitch = -0.1309F;
        this.cube_r1.yaw = 0.2618F;
        this.cube_r2.pitch = 0.2182F;
        this.cube_r2.yaw = 0.6545F;
        this.cube_r3.pitch = 0.1309F;
        this.cube_r4.pitch = -0.1772F;
        this.cube_r4.yaw = 0.1719F;
        this.cube_r4.roll = -0.0306F;
        this.cube_r5.roll = 0.2618F;
        this.cube_r6.roll = -0.2182F;
    }
}
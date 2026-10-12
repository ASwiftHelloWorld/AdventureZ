package net.adventurez.entity.render;

import net.adventurez.AdventureMain;
import net.adventurez.entity.model.VoidBulletModel;
import net.adventurez.entity.nonliving.VoidBulletEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class VoidBulletRenderer extends EntityRenderer<VoidBulletEntity> {
    private static final Identifier TEXTURE = AdventureMain.identifierOf("textures/entity/void_bullet.png");
    private final VoidBulletModel<VoidBulletEntity> model = new VoidBulletModel<>(VoidBulletModel.getTexturedModelData().createModel());

    public VoidBulletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public int getBlockLight(VoidBulletEntity voidBulletEntity, BlockPos blockPos) {
        return voidBulletEntity.level().getLightLevel(blockPos);
    }

    @Override
    public void render(VoidBulletEntity voidBulletEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        matrixStack.push();
        float h = Mth.lerpAngleDegrees(g, voidBulletEntity.prevYaw, voidBulletEntity.getYaw());
        float j = Mth.lerp(g, voidBulletEntity.prevPitch, voidBulletEntity.getPitch());
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        matrixStack.translate(0.0D, -1.55D, 0.0D);
        this.model.setAngles(voidBulletEntity, 0.0F, 0.0F, 0.0F, h, j);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(voidBulletEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(VoidBulletEntity voidBulletEntity) {
        return TEXTURE;
    }

}

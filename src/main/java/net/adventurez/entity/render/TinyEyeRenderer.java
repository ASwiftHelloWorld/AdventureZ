package net.adventurez.entity.render;

import net.adventurez.AdventureMain;
import net.adventurez.entity.model.TinyEyeModel;
import net.adventurez.entity.nonliving.TinyEyeEntity;
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
public class TinyEyeRenderer extends EntityRenderer<TinyEyeEntity> {
    private static final Identifier TEXTURE = AdventureMain.identifierOf("textures/entity/the_eye.png");
    private final TinyEyeModel<TinyEyeEntity> model = new TinyEyeModel<>(TinyEyeModel.getTexturedModelData().createModel());

    public TinyEyeRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public int getBlockLight(TinyEyeEntity tinyEyeEntity, BlockPos blockPos) {
        return tinyEyeEntity.level().getLightLevel(blockPos);
    }

    @Override
    public void render(TinyEyeEntity tinyEyeEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        matrixStack.push();
        float h = Mth.lerpAngleDegrees(g, tinyEyeEntity.prevYaw, tinyEyeEntity.getYaw());
        matrixStack.scale(-0.5F, -0.5F, 0.5F);
        matrixStack.translate(0.0D, -1.4D, 0.0D);
        this.model.setAngles(tinyEyeEntity, 0.0F, 0.0F, 0.0F, h, 0.0F);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(tinyEyeEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(TinyEyeEntity eye) {
        return TEXTURE;
    }

}

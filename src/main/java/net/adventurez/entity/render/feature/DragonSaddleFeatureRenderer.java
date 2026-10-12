package net.adventurez.entity.render.feature;

import net.adventurez.entity.DragonEntity;
import net.adventurez.entity.model.DragonModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class DragonSaddleFeatureRenderer extends RenderLayer<DragonEntity, DragonModel<DragonEntity>> {
    private static final RenderType SADDLE_LAYER = RenderType.getEntityCutoutNoCull(Identifier.of("adventurez:textures/entity/feature/dragon_saddle.png"));

    public DragonSaddleFeatureRenderer(RenderLayerParent<DragonEntity, DragonModel<DragonEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int light, DragonEntity dragonEntity, float limbAngle, float limbDistance, float tickDelta,
            float animationProgress, float headYaw, float headPitch) {
        if (dragonEntity.getEntityData().get(DragonEntity.HAS_SADDLE)) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(SADDLE_LAYER);
            this.getContextModel().render(matrixStack, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
        }
    }

}
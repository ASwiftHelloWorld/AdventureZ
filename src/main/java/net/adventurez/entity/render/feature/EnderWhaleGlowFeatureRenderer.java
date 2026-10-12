package net.adventurez.entity.render.feature;

import net.adventurez.entity.EnderWhaleEntity;
import net.adventurez.entity.model.EnderWhaleModel;
import net.adventurez.entity.render.feature.layer.ExtraRenderLayer;
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
public class EnderWhaleGlowFeatureRenderer extends RenderLayer<EnderWhaleEntity, EnderWhaleModel<EnderWhaleEntity>> {
    private static final RenderType GLOW_LAYER = ExtraRenderLayer.getGlowing(Identifier.of("adventurez:textures/entity/feature/ender_whale_glow_feature.png"));

    public EnderWhaleGlowFeatureRenderer(RenderLayerParent<EnderWhaleEntity, EnderWhaleModel<EnderWhaleEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, EnderWhaleEntity enderWhaleEntity, float f, float g, float h, float j, float k, float l) {
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(GLOW_LAYER);
        this.getContextModel().render(matrixStack, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV);
    }

}
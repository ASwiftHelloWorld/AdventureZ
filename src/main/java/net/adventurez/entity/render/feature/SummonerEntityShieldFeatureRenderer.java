package net.adventurez.entity.render.feature;

import net.adventurez.entity.SummonerEntity;
import net.adventurez.entity.model.SummonerModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import com.mojang.blaze3d.vertex.PoseStack;

@Environment(EnvType.CLIENT)
public class SummonerEntityShieldFeatureRenderer extends RenderLayer<SummonerEntity, SummonerModel<SummonerEntity>> {
    private static final RenderType SHIELD_LAYER = RenderType.getEntityGlint();

    public SummonerEntityShieldFeatureRenderer(RenderLayerParent<SummonerEntity, SummonerModel<SummonerEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, SummonerEntity summonerEntity, float f, float g, float h, float j, float k, float l) {
        if (summonerEntity.getDataTracker().get(SummonerEntity.INVULNERABLE_SHIELD)) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(SHIELD_LAYER);
            this.getContextModel().render(matrixStack, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV);
        }
    }

}

package net.adventurez.entity.render.feature;

import net.adventurez.entity.SoulReaperEntity;
import net.adventurez.entity.model.SoulReaperModel;
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
public class SoulReaperEyesFeatureRenderer extends RenderLayer<SoulReaperEntity, SoulReaperModel<SoulReaperEntity>> {
    private static final RenderType EYE_LAYER = ExtraRenderLayer.getGlowing(Identifier.of("adventurez:textures/entity/feature/soul_reaper_eyes_feature.png"));

    public SoulReaperEyesFeatureRenderer(RenderLayerParent<SoulReaperEntity, SoulReaperModel<SoulReaperEntity>> featureRendererContext) {
        super(featureRendererContext);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, SoulReaperEntity soulReaperEntity, float f, float g, float h, float j, float k, float l) {
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(EYE_LAYER);
        this.getContextModel().render(matrixStack, vertexConsumer, 15728640, OverlayTexture.DEFAULT_UV);
    }

}
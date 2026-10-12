package net.adventurez.entity.render.feature;

import net.adventurez.entity.OrcEntity;
import net.adventurez.entity.model.OrcModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.item.ItemDisplayContext;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.registries.BuiltInRegistries;
import com.mojang.math.Axis;

@Environment(EnvType.CLIENT)
public class OrcInventoryFeatureRenderer extends RenderLayer<OrcEntity, OrcModel<OrcEntity>> {

    public OrcInventoryFeatureRenderer(RenderLayerParent<OrcEntity, OrcModel<OrcEntity>> featureRendererContext) {
        super(featureRendererContext);

    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, OrcEntity orcEntity, float f, float g, float h, float j, float k, float l) {
        int itemId = orcEntity.getEntityData().get(OrcEntity.INVENTORY_ITEM_ID);
        if (!orcEntity.isBigOrc() && itemId != -1) {
            matrixStack.push();
            ModelPart modelPart = this.getContextModel().getTorso();
            modelPart.rotate(matrixStack);
            matrixStack.multiply(Axis.POSITIVE_Y.rotationDegrees(90F));
            matrixStack.translate(-0.295D, -0.3D, 0.0D);
            Minecraft.getInstance().getEntityRenderDispatcher().getHeldItemRenderer().renderItem(orcEntity, BuiltInRegistries.ITEM.get(itemId).getDefaultStack(),
                    ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, false, matrixStack, vertexConsumerProvider, i);
            matrixStack.pop();
        }
    }

}

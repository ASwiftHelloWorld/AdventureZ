package net.adventurez.entity.render;

import net.adventurez.AdventureMain;
import net.adventurez.entity.model.GildedStoneModel;
import net.adventurez.entity.nonliving.GildedBlackstoneShardEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.texture.OverlayTexture;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;

@Environment(EnvType.CLIENT)
public class GildedStoneRenderer extends EntityRenderer<GildedBlackstoneShardEntity> {
    private static final Identifier TEXTURE = AdventureMain.identifierOf("textures/item/gilded_stone.png");
    private final GildedStoneModel model = new GildedStoneModel(GildedStoneModel.getTexturedModelData().createModel());

    public GildedStoneRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public int getBlockLight(GildedBlackstoneShardEntity gildedStoneEntity, BlockPos blockPos) {
        return 15;
    }

    @Override
    public void render(GildedBlackstoneShardEntity gildedStoneEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {

        matrixStack.push();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(this.getTexture(gildedStoneEntity)));
        int lightAbove = LevelRenderer.getLightmapCoordinates(gildedStoneEntity.getEntityWorld(), gildedStoneEntity.getBlockPos().above());
        matrixStack.scale(1.0F, -1.0F, 1.0F);
        matrixStack.translate(0D, -1.45D, 0D);
        model.render(matrixStack, vertexConsumer, lightAbove, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(gildedStoneEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(GildedBlackstoneShardEntity gildedStoneEntity) {
        return TEXTURE;
    }
}

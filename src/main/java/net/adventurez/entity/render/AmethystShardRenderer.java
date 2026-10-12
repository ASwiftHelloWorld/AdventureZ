package net.adventurez.entity.render;

import net.adventurez.AdventureMain;
import net.adventurez.entity.model.AmethystShardModel;
import net.adventurez.entity.nonliving.AmethystShardEntity;
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
public class AmethystShardRenderer extends EntityRenderer<AmethystShardEntity> {
    private static final Identifier TEXTURE = AdventureMain.identifierOf("textures/entity/amethyst_shard.png");
    private final AmethystShardModel<AmethystShardEntity> model = new AmethystShardModel<>(AmethystShardModel.getTexturedModelData().createModel());

    public AmethystShardRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public int getBlockLight(AmethystShardEntity amethystShardEntity, BlockPos blockPos) {
        return amethystShardEntity.level().getLightLevel(blockPos);
    }

    @Override
    public void render(AmethystShardEntity amethystShardEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        matrixStack.push();
        float h = Mth.lerpAngleDegrees(g, amethystShardEntity.prevYaw, amethystShardEntity.getYaw());
        float j = Mth.lerp(g, amethystShardEntity.prevPitch, amethystShardEntity.getPitch());
        matrixStack.scale(-1.0F, -1.0F, 1.0F);
        matrixStack.translate(0.0D, -1.55D, 0.0D);
        this.model.setAngles(amethystShardEntity, 0.0F, 0.0F, 0.0F, h, j);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
        matrixStack.pop();
        super.render(amethystShardEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(AmethystShardEntity amethystShardEntity) {
        return TEXTURE;
    }

}

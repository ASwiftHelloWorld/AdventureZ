package net.adventurez.entity.render;

import net.adventurez.AdventureMain;
import net.adventurez.entity.DragonEntity;
import net.adventurez.entity.model.DragonModel;
import net.adventurez.entity.render.feature.DragonEyesFeatureRenderer;
import net.adventurez.entity.render.feature.DragonSaddleFeatureRenderer;
import net.adventurez.init.RenderInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class DragonRenderer extends MobRenderer<DragonEntity, DragonModel<DragonEntity>> {
    private static final Identifier TEXTURE = AdventureMain.identifierOf("textures/entity/dragon.png");
    private static final Identifier RED_DRAGON_TEXTURE = AdventureMain.identifierOf("textures/entity/red_dragon.png");

    public DragonRenderer(EntityRendererProvider.Context context) {
        super(context, new DragonModel<>(context.getPart(RenderInit.DRAGON_LAYER)), 0.4F);
        this.addFeature(new DragonEyesFeatureRenderer(this));
        this.addFeature(new DragonSaddleFeatureRenderer(this));
    }

    @Override
    public void render(DragonEntity dragonEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        this.shadowRadius = 0.6F * (float) dragonEntity.getSize();
        super.render(dragonEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public void scale(DragonEntity dragonEntity, PoseStack matrixStack, float f) {
        matrixStack.scale((float) dragonEntity.getSize() / 3.0F, (float) dragonEntity.getSize() / 3.0F, (float) dragonEntity.getSize() / 3.0F);
    }

    @Override
    public Identifier getTexture(DragonEntity dragonEntity) {
        if (dragonEntity.getEntityData().get(DragonEntity.RED_DRAGON)) {
            return RED_DRAGON_TEXTURE;
        }
        return TEXTURE;
    }
}

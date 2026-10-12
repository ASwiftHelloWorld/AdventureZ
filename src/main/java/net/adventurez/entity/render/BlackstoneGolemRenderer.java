package net.adventurez.entity.render;

import net.adventurez.AdventureMain;
import net.adventurez.entity.BlackstoneGolemEntity;
import net.adventurez.entity.model.BlackstoneGolemModel;
import net.adventurez.entity.render.feature.BlackstoneGolemBlueLavaFeatureRenderer;
import net.adventurez.entity.render.feature.StoneGolemLavaFeatureRenderer;
import net.adventurez.init.RenderInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class BlackstoneGolemRenderer extends MobRenderer<BlackstoneGolemEntity, BlackstoneGolemModel<BlackstoneGolemEntity>> {
    private static final Identifier TEXTURE = AdventureMain.identifierOf("textures/entity/blackstone_golem.png");

    public BlackstoneGolemRenderer(EntityRendererProvider.Context context) {
        super(context, new BlackstoneGolemModel<>(context.getPart(RenderInit.BLACKSTONE_GOLEM_LAYER)), 1.7F);
        this.addFeature(new StoneGolemLavaFeatureRenderer(this));
        this.addFeature(new BlackstoneGolemBlueLavaFeatureRenderer(this));
    }

    @Override
    public void scale(BlackstoneGolemEntity stone, PoseStack matrixStack, float f) {
        matrixStack.scale(2.4F, 2.4F, 2.4F);
    }

    @Override
    public Identifier getTexture(BlackstoneGolemEntity stoneGolem) {
        return TEXTURE;
    }
}

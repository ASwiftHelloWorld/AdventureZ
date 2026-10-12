package net.adventurez.entity.render;

import net.adventurez.AdventureMain;
import net.adventurez.entity.SummonerEntity;
import net.adventurez.entity.model.SummonerModel;
import net.adventurez.entity.render.feature.SummonerEntityShieldFeatureRenderer;
import net.adventurez.entity.render.feature.SummonerEyesFeatureRenderer;
import net.adventurez.init.RenderInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class SummonerRenderer extends MobRenderer<SummonerEntity, SummonerModel<SummonerEntity>> {
    private static final Identifier TEXTURE = AdventureMain.identifierOf("textures/entity/summoner.png");

    public SummonerRenderer(EntityRendererProvider.Context context) {
        super(context, new SummonerModel<>(context.getPart(RenderInit.SUMMONER_LAYER)), 0.7F);
        this.addFeature(new SummonerEntityShieldFeatureRenderer(this));
        this.addFeature(new SummonerEyesFeatureRenderer(this));
    }

    @Override
    public void scale(SummonerEntity summonerEntity, PoseStack matrixStack, float f) {
        matrixStack.scale(1.3F, 1.3F, 1.3F);
    }

    @Override
    public Identifier getTexture(SummonerEntity summonerEntity) {
        return TEXTURE;
    }
}

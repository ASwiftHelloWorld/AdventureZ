package net.adventurez.entity.render;

import net.adventurez.AdventureMain;
import net.adventurez.entity.EnderWhaleEntity;
import net.adventurez.entity.model.EnderWhaleModel;
import net.adventurez.entity.render.feature.EnderWhaleGlowFeatureRenderer;
import net.adventurez.init.RenderInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class EnderWhaleRenderer extends MobRenderer<EnderWhaleEntity, EnderWhaleModel<EnderWhaleEntity>> {
    private static final Identifier TEXTURE = AdventureMain.identifierOf("textures/entity/ender_whale.png");

    public EnderWhaleRenderer(EntityRendererProvider.Context context) {
        super(context, new EnderWhaleModel<>(context.getPart(RenderInit.ENDER_WHALE_LAYER)), 2.3F);
        this.addFeature(new EnderWhaleGlowFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(EnderWhaleEntity enderWhaleEntity) {
        return TEXTURE;
    }
}

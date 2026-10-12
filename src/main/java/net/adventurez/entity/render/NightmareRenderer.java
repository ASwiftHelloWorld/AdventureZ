package net.adventurez.entity.render;

import net.adventurez.AdventureMain;
import net.adventurez.entity.NightmareEntity;
import net.adventurez.entity.render.feature.NightmareEyesFeatureRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.AbstractHorseRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.animal.equine.HorseModel;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public final class NightmareRenderer extends AbstractHorseRenderer<NightmareEntity, HorseModel<NightmareEntity>> {

    private static final Identifier TEXTURE = AdventureMain.identifierOf("textures/entity/nightmare.png");

    public NightmareRenderer(EntityRendererProvider.Context context) {
        super(context, new HorseModel<>(context.getPart(ModelLayers.HORSE)), 1.1F);
        this.addFeature(new NightmareEyesFeatureRenderer(this));
    }

    @Override
    public Identifier getTexture(NightmareEntity nightmareEntity) {
        return TEXTURE;
    }
}

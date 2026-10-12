package net.adventurez.entity.render;

import net.adventurez.AdventureMain;
import net.adventurez.entity.DeerEntity;
import net.adventurez.entity.model.DeerModel;
import net.adventurez.init.RenderInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class DeerRenderer extends MobRenderer<DeerEntity, DeerModel<DeerEntity>> {
    private static final Identifier TEXTURE = AdventureMain.identifierOf("textures/entity/deer.png");

    public DeerRenderer(EntityRendererProvider.Context context) {
        super(context, new DeerModel<>(context.getPart(RenderInit.DEER_LAYER)), 0.7F);
    }

    @Override
    public Identifier getTexture(DeerEntity deerEntity) {
        return TEXTURE;
    }
}

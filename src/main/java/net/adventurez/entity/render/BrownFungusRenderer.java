package net.adventurez.entity.render;

import net.adventurez.AdventureMain;
import net.adventurez.entity.BrownFungusEntity;
import net.adventurez.entity.model.BrownFungusModel;
import net.adventurez.init.RenderInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class BrownFungusRenderer extends MobRenderer<BrownFungusEntity, BrownFungusModel<BrownFungusEntity>> {
    private static final Identifier TEXTURE = AdventureMain.identifierOf("textures/entity/brown_fungus.png");

    public BrownFungusRenderer(EntityRendererProvider.Context context) {
        super(context, new BrownFungusModel<>(context.getPart(RenderInit.BROWN_FUNGUS_LAYER)), 0.4F);
    }

    @Override
    public void scale(BrownFungusEntity brownFungusEntity, PoseStack matrixStack, float f) {
        matrixStack.scale(1.3F, 1.3F, 1.3F);
    }

    @Override
    public Identifier getTexture(BrownFungusEntity fungusEntity) {
        return TEXTURE;
    }
}

package net.adventurez.entity.render;

import net.adventurez.AdventureMain;
import net.adventurez.entity.BlazeGuardianEntity;
import net.adventurez.entity.model.BlazeGuardianModel;
import net.adventurez.init.RenderInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class BlazeGuardianRenderer extends MobRenderer<BlazeGuardianEntity, BlazeGuardianModel<BlazeGuardianEntity>> {
    private static final Identifier TEXTURE = AdventureMain.identifierOf("textures/entity/blaze_guardian.png");

    public BlazeGuardianRenderer(EntityRendererProvider.Context context) {
        super(context, new BlazeGuardianModel<>(context.getPart(RenderInit.BLAZE_GUARDIAN_LAYER)), 0.7F);
    }

    @Override
    public void scale(BlazeGuardianEntity blazeGuardianEntity, PoseStack matrixStack, float f) {
        matrixStack.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public Identifier getTexture(BlazeGuardianEntity blazeGuardianEntity) {
        return TEXTURE;
    }
}

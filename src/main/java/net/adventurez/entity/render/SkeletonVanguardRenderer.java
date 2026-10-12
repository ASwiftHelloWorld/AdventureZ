package net.adventurez.entity.render;

import net.adventurez.AdventureMain;
import net.adventurez.entity.SkeletonVanguardEntity;
import net.adventurez.entity.model.SkeletonVanguardModel;
import net.adventurez.init.RenderInit;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class SkeletonVanguardRenderer extends MobRenderer<SkeletonVanguardEntity, SkeletonVanguardModel<SkeletonVanguardEntity>> {
    private static final Identifier TEXTURE = AdventureMain.identifierOf("textures/entity/skeleton_vanguard.png");

    public SkeletonVanguardRenderer(EntityRendererProvider.Context context) {
        super(context, new SkeletonVanguardModel<>(context.getPart(RenderInit.SKELETON_VANGUARD_LAYER)), 0.5F);
    }

    @Override
    public Identifier getTexture(SkeletonVanguardEntity skeletonVanguardEntity) {
        return TEXTURE;
    }
}

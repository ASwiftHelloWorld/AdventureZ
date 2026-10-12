package net.adventurez.entity.render.feature.layer;

import net.fabricmc.api.Environment;
import net.irisshaders.iris.api.v0.IrisApi;
import net.adventurez.init.RenderInit;
import net.fabricmc.api.EnvType;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
public class ExtraRenderLayer {
    public static RenderType getGlowing(Identifier identifier) {
        if (RenderInit.isIrisLoaded && IrisApi.getInstance().isShaderPackInUse()) {
            return RenderType.getEyes(identifier);
        } else if (RenderInit.isCanvasLoaded)
            return RenderType.getEntityDecal(identifier);
        else
            return RenderType.getEntityAlpha(identifier);
    }

}
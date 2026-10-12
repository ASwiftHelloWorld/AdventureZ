package net.adventurez.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.adventurez.access.EntityAccess;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(IronGolemRenderer.class)
public class IronGolemEntityRendererMixin {

    private static final Identifier BLACKSTONED_TEXTURE = Identifier.of("adventurez:textures/entity/blackstone_golem.png");

    @Inject(method = "getTexture", at = @At("HEAD"), cancellable = true)
    private void getTextureMixin(IronGolem ironGolemEntity, CallbackInfoReturnable<Identifier> info) {
        if (ironGolemEntity.getDataTracker().get(((EntityAccess) ironGolemEntity).getTrackedDataBoolean())) {
            info.setReturnValue(BLACKSTONED_TEXTURE);
        }
    }
}

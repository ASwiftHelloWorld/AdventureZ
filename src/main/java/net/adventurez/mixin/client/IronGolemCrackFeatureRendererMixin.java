package net.adventurez.mixin.client;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.fabricmc.api.Environment;
import net.adventurez.access.EntityAccess;
import net.fabricmc.api.EnvType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.IronGolemCrackinessLayer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.Crackiness;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.resources.Identifier;

@Environment(EnvType.CLIENT)
@Mixin(IronGolemCrackinessLayer.class)
public class IronGolemCrackFeatureRendererMixin {

    private static final Map<Crackiness.CrackLevel, Identifier> BLACKSTONED_CRACKINESS_TO_TEXTURE = ImmutableMap.of(Crackiness.CrackLevel.LOW,
            Identifier.of("adventurez:textures/entity/feature/iron_golem_blackstoned_low.png"), Crackiness.CrackLevel.MEDIUM,
            Identifier.of("adventurez:textures/entity/feature/iron_golem_blackstoned_medium.png"), Crackiness.CrackLevel.HIGH,
            Identifier.of("adventurez:textures/entity/feature/iron_golem_blackstoned_high.png"));

    @ModifyVariable(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/IronGolemCrackinessLayer;getContextModel()Lnet/minecraft/client/render/entity/model/EntityModel;"), ordinal = 0)
    private Identifier renderMixin(Identifier original, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i, IronGolem ironGolemEntity, float f, float g, float h,
            float j, float k, float l) {
        if (ironGolemEntity.getEntityData().get(((EntityAccess) ironGolemEntity).getTrackedDataBoolean())) {
            return BLACKSTONED_CRACKINESS_TO_TEXTURE.get(ironGolemEntity.getCrackLevel());
        } else {
            return original;
        }
    }

}

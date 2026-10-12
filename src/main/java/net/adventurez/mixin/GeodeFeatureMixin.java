package net.adventurez.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.adventurez.entity.AmethystGolemEntity;
import net.adventurez.init.ConfigInit;
import net.adventurez.init.EntityInit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.GeodeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

@Mixin(GeodeFeature.class)
public class GeodeFeatureMixin {

    @Inject(method = "generate", at = @At(value = "RETURN"))
    private void generateMixin(FeaturePlaceContext<GeodeConfiguration> context, CallbackInfoReturnable<Boolean> info) {
        if (!context.level().isClientSide() && info.getReturnValue()) {
            int amethystSpawnChance = ConfigInit.CONFIG.amethyst_golem_spawn_chance;
            if (amethystSpawnChance != 0) {
                int spawnChanceInt = context.getRandom().nextInt(amethystSpawnChance) + 1;
                BlockPos spawnPos = context.getOrigin().south(4).east(4);
                if (spawnChanceInt == 1 && (context.level().isAir(spawnPos) || context.level().isAir(spawnPos.above()))) {
                    AmethystGolemEntity amethystGolemEntity = (AmethystGolemEntity) EntityInit.AMETHYST_GOLEM.create(context.level().toServerWorld());
                    amethystGolemEntity.getEntityData().set(AmethystGolemEntity.DEEPSLATE_VARIANT, spawnPos.getY() <= 0);
                    if (!context.level().isAir(spawnPos)) {
                        spawnPos = spawnPos.above();
                    }
                    amethystGolemEntity.refreshPositionAndAngles(spawnPos, context.getRandom().nextFloat() * 360F, 0.0F);
                    context.level().spawnEntity(amethystGolemEntity);
                }
            }
        }
    }
}

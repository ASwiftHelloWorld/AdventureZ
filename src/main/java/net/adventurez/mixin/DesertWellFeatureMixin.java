package net.adventurez.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.adventurez.entity.DesertRhinoEntity;
import net.adventurez.init.ConfigInit;
import net.adventurez.init.EntityInit;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.DesertWellFeature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

@Mixin(DesertWellFeature.class)
public class DesertWellFeatureMixin {

    @Inject(method = "generate", at = @At(value = "RETURN"))
    private void generateMixin(FeaturePlaceContext<NoneFeatureConfiguration> context, CallbackInfoReturnable<Boolean> info) {
        if (info.getReturnValue()) {
            int rhinoSpawnChance = ConfigInit.CONFIG.desert_rhino_well_spawn_chance;
            if (!context.level().isClientSide() && rhinoSpawnChance != 0) {
                int spawnChanceInt = context.getRandom().nextInt(rhinoSpawnChance) + 1;
                BlockPos spawnPos = context.getOrigin().south(3).west();
                if (spawnChanceInt == 1) {
                    for (int i = 0; i < 4; i++) {
                        if (i == 1) {
                            spawnPos = spawnPos.east(9);
                        }
                        if (i == 2) {
                            spawnPos = spawnPos.south(5).east(4);
                        }
                        if (i == 3) {
                            spawnPos = spawnPos.north(5).east(3);
                        }
                        if (context.level().isAir(spawnPos)
                                && SpawnPlacements.canSpawn(EntityInit.DESERT_RHINO, context.level(), EntitySpawnReason.CHUNK_GENERATION, spawnPos, context.getRandom())) {
                            DesertRhinoEntity desertRhinoEntity = EntityInit.DESERT_RHINO.create(context.level().toServerWorld());
                            desertRhinoEntity.refreshPositionAndAngles(spawnPos, context.getRandom().nextFloat() * 360F, 0.0F);
                            context.level().spawnEntity(desertRhinoEntity);
                            break;
                        }
                    }
                }
            }
        }
    }
}

package net.adventurez.mixin;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.adventurez.init.ConfigInit;
import net.adventurez.init.EntityInit;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.structures.NetherFortressStructure;

@Mixin(NaturalSpawner.class)
public class SpawnHelperMixin {

    private static final WeightedList<MobSpawnSettings.SpawnEntry> ADDED_SPAWNS;

    @Inject(method = "getSpawnEntries", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/levelgen/structure/structures/NetherFortressStructure;MONSTER_SPAWNS:Lnet/minecraft/util/random/WeightedList;", ordinal = 0), cancellable = true)
    private static void getSpawnEntriesMixin(ServerLevel world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, MobCategory spawnGroup, BlockPos pos,
            @Nullable Holder<Biome> biomeEntry, CallbackInfoReturnable<WeightedList<MobSpawnSettings.SpawnEntry>> info) {
        List<MobSpawnSettings.SpawnEntry> spawnersList = new ArrayList<>(NetherFortressStructure.MONSTER_SPAWNS.getEntries());
        spawnersList.addAll(ADDED_SPAWNS.getEntries());
        info.setReturnValue(WeightedList.of(spawnersList));
    }

    static {
        ADDED_SPAWNS = WeightedList.of(new MobSpawnSettings.SpawnEntry(EntityInit.NECROMANCER, ConfigInit.CONFIG.necromancer_spawn_weight, 1, 1),
                new MobSpawnSettings.SpawnEntry(EntityInit.BLAZE_GUARDIAN, ConfigInit.CONFIG.blaze_guardian_spawn_weight, 1, 1));
    }
}

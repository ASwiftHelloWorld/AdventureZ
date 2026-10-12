package net.adventurez.init;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;

import net.adventurez.entity.*;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;

public class SpawnInit {

    public static void init() {
        setSpawnRestriction();
        addSpawnEntries();
    }

    // MONSTER tries to spawn often, CREATURE tries more rarely to spawn + in groups
    private static void addSpawnEntries() {
        BiomeModifications.addSpawn(BiomeSelectors.includeByKey(Biomes.BASALT_DELTAS), MobCategory.MONSTER, EntityInit.MINI_BLACKSTONE_GOLEM, ConfigInit.CONFIG.mini_blackstone_golem_spawn_weight,
                1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.foundInTheNether().and(BiomeSelectors.excludeByKey(Biomes.BASALT_DELTAS)), MobCategory.MONSTER, EntityInit.BLAZE_GUARDIAN,
                ConfigInit.CONFIG.blaze_guardian_spawn_weight, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.NETHER_FOSSIL_HAS_STRUCTURE), MobCategory.MONSTER, EntityInit.SOUL_REAPER, ConfigInit.CONFIG.nightmare_spawn_weight, 1, 1);

        BiomeModifications.addSpawn(BiomeSelectors.tag(TagInit.IS_MUSHROOM), MobCategory.CREATURE, EntityInit.RED_FUNGUS, ConfigInit.CONFIG.fungus_spawn_weight, 2, 3);
        BiomeModifications.addSpawn(BiomeSelectors.tag(TagInit.IS_MUSHROOM), MobCategory.CREATURE, EntityInit.BROWN_FUNGUS, ConfigInit.CONFIG.fungus_spawn_weight, 2, 3);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.VILLAGE_PLAINS_HAS_STRUCTURE), MobCategory.MONSTER, EntityInit.ORC, ConfigInit.CONFIG.orc_spawn_weight, 2, 4);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IGLOO_HAS_STRUCTURE), MobCategory.CREATURE, EntityInit.MAMMOTH, ConfigInit.CONFIG.mammoth_spawn_weight, 2, 2);
        BiomeModifications.addSpawn(BiomeSelectors.foundInTheEnd(), MobCategory.CREATURE, EntityInit.ENDER_WHALE, ConfigInit.CONFIG.ender_whale_spawn_weight, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_BADLANDS), MobCategory.CREATURE, EntityInit.IGUANA, ConfigInit.CONFIG.iguana_spawn_weight, 1, 2);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.DESERT_PYRAMID_HAS_STRUCTURE), MobCategory.MONSTER, EntityInit.DESERT_RHINO, ConfigInit.CONFIG.desert_rhino_spawn_weight, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.SWAMP_HUT_HAS_STRUCTURE), MobCategory.MONSTER, EntityInit.SHAMAN, ConfigInit.CONFIG.shaman_spawn_weight, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.foundInTheEnd().and(BiomeSelectors.excludeByKey(Biomes.THE_END, Biomes.END_BARRENS)), MobCategory.MONSTER, EntityInit.ENDERWARTHOG,
                ConfigInit.CONFIG.enderwarthog_spawn_weight, 1, 1);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_FOREST), MobCategory.CREATURE, EntityInit.DEER, ConfigInit.CONFIG.deer_spawn_weight, 2, 3);
        BiomeModifications.addSpawn(BiomeSelectors.tag(BiomeTags.IS_FOREST), MobCategory.CREATURE, EntityInit.SKUNK, ConfigInit.CONFIG.skunk_spawn_weight, 1, 2);
    }

    private static void setSpawnRestriction() {
        SpawnPlacements.register(EntityInit.MINI_BLACKSTONE_GOLEM, SpawnPlacementTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MiniBlackstoneGolemEntity::canSpawn);
        SpawnPlacements.register(EntityInit.NECROMANCER, SpawnPlacementTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, NecromancerEntity::canSpawn);
        SpawnPlacements.register(EntityInit.SUMMONER, SpawnPlacementTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SummonerEntity::canSpawn);
        SpawnPlacements.register(EntityInit.BLAZE_GUARDIAN, SpawnPlacementTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BlazeGuardianEntity::canSpawn);
        SpawnPlacements.register(EntityInit.PIGLIN_BEAST, SpawnPlacementTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Monster::canSpawnIgnoreLightLevel);
        SpawnPlacements.register(EntityInit.SOUL_REAPER, SpawnPlacementTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, SoulReaperEntity::canSpawn);
        SpawnPlacements.register(EntityInit.RED_FUNGUS, SpawnPlacementTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, RedFungusEntity::canSpawn);
        SpawnPlacements.register(EntityInit.BROWN_FUNGUS, SpawnPlacementTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, BrownFungusEntity::canSpawn);
        SpawnPlacements.register(EntityInit.ORC, SpawnPlacementTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, OrcEntity::canSpawn);
        SpawnPlacements.register(EntityInit.MAMMOTH, SpawnPlacementTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::isValidNaturalSpawn);
        SpawnPlacements.register(EntityInit.ENDER_WHALE, SpawnPlacementTypes.UNRESTRICTED, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EnderWhaleEntity::canSpawn);
        SpawnPlacements.register(EntityInit.IGUANA, SpawnPlacementTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, IguanaEntity::isValidNaturalSpawn);
        SpawnPlacements.register(EntityInit.DESERT_RHINO, SpawnPlacementTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, DesertRhinoEntity::canSpawn);
        SpawnPlacements.register(EntityInit.SHAMAN, SpawnPlacementTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, ShamanEntity::canSpawn);
        SpawnPlacements.register(EntityInit.DEER, SpawnPlacementTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::isValidNaturalSpawn);
        SpawnPlacements.register(EntityInit.SKUNK, SpawnPlacementTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, Animal::isValidNaturalSpawn);
        SpawnPlacements.register(EntityInit.ENDERWARTHOG, SpawnPlacementTypes.ON_GROUND, Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EnderwarthogEntity::canSpawn);
    }

}

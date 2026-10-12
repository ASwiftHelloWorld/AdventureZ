package net.adventurez.mixin;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.adventurez.entity.SummonerEntity;
import net.adventurez.init.ConfigInit;
import net.adventurez.init.EntityInit;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;

@Mixin(ServerLevel.class)
public abstract class ServerWorldMixin extends Level {

    public ServerWorldMixin(WritableLevelData properties, ResourceKey<Level> registryRef, RegistryAccess registryManager, Holder<DimensionType> dimensionEntry,
            Supplier<ProfilerFiller> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LightningEntity;setCosmetic(Z)V", shift = Shift.AFTER))
    public void tickChunk(LevelChunk chunk, int randomTickSpeed, CallbackInfo info) {
        int summonerSpawnChance = ConfigInit.CONFIG.summoner_thunder_spawn_chance;
        if (summonerSpawnChance != 0) {
            int spawnChanceInt = this.getRandom().nextInt(summonerSpawnChance) + 1;
            if (spawnChanceInt == 1) {
                ChunkPos chunkPos = chunk.getPos();
                int i = chunkPos.getStartX();
                int j = chunkPos.getStartZ();

                BlockPos blockPos = this.getTopPosition(Heightmap.Type.MOTION_BLOCKING, this.getRandomPosInChunk(i, 0, j, 15));
                if (SpawnPlacements.canSpawn(EntityInit.SUMMONER, (ServerLevel) (Object) this, EntitySpawnReason.EVENT, blockPos, this.getRandom())) {
                    SummonerEntity summonerEntity = EntityInit.SUMMONER.create(this);
                    summonerEntity.updatePosition((double) blockPos.getX(), (double) blockPos.getY(), (double) blockPos.getZ());
                    summonerEntity.initialize((ServerLevel) (Object) this, this.getLocalDifficulty(blockPos), EntitySpawnReason.EVENT, null);
                    this.spawnEntity(summonerEntity);
                    summonerEntity.playSpawnEffects();
                }
            }
        }
    }

}
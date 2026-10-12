package net.adventurez.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.adventurez.init.ConfigInit;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;

@Mixin(EnderDragonFight.class)
public class EnderDragonFightMixin {

    @Shadow
    private boolean previouslyKilled;
    @Shadow
    @Mutable
    @Final
    private ServerLevel world;
    @Shadow
    @Mutable
    @Final
    private BlockPos origin;

    @Inject(method = "dragonKilled", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/dimension/end/EnderDragonFight;generateNewEndGateway()V"))
    public void dragonKilledMixin(EnderDragon dragon, CallbackInfo info) {
        if (!this.world.isClient && ConfigInit.CONFIG.resummoned_ender_dragon_drops_egg && this.previouslyKilled
                && this.world.getBlockState(this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, EndPodiumFeature.offsetOrigin(this.origin))).getBlock() != Blocks.DRAGON_EGG) {
            this.world.setBlockState(this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, EndPodiumFeature.offsetOrigin(this.origin)), Blocks.DRAGON_EGG.defaultBlockState());
        }
    }

}

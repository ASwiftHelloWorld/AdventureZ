package net.adventurez.block.entity;

import java.util.List;

import net.adventurez.init.BlockInit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.Level;

public class PiglinFlagEntity extends BlockEntity {
    private int flagWave;

    public PiglinFlagEntity(BlockPos pos, BlockState state) {
        super(BlockInit.PIGLIN_FLAG_ENTITY, pos, state);
    }

    @Override
    protected void readNbt(CompoundTag nbt, Provider registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.flagWave = nbt.getInt("Flagging");
    }

    @Override
    public void writeNbt(CompoundTag nbt, Provider registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putInt("Flagging", flagWave);
    }

    public static void clientTick(Level world, BlockPos pos, BlockState state, PiglinFlagEntity blockEntity) {
        blockEntity.update();
    }

    public static void serverTick(Level world, BlockPos pos, BlockState state, PiglinFlagEntity blockEntity) {
        blockEntity.update();
    }

    @Override
    public void markDirty() {
        super.setChanged();
        this.sendUpdate();
    }

    private void sendUpdate() {
        if (this.getLevel() != null) {
            BlockState state = this.getLevel().getBlockState(this.getBlockPos());
            this.getLevel().updateListeners(this.getBlockPos(), state, state, 3);
        }
    }

    private void update() {
        if (this.getLevel().getTime() % 20 == 0) {
            if (this.flagWave < 120) {
                this.flagWave++;
            }
            if (this.flagWave >= 120 && this.getLevel().getClosestPlayer((double) this.getBlockPos().getX(), (double) this.getBlockPos().getY(), (double) this.getBlockPos().getZ(), 3D, null) != null) {
                this.getPiglins();
                this.setChanged();
                if (this.getLevel().isClientSide()) {
                    for (int i = 0; i < 20; i++) {
                        double d = (double) this.getBlockPos().getX() + (double) this.getLevel().getRandom().nextFloat();
                        double e = (double) this.getBlockPos().getY() + (double) this.getLevel().getRandom().nextFloat() * 2.0F;
                        double f = (double) this.getBlockPos().getZ() + (double) this.getLevel().getRandom().nextFloat();
                        this.getLevel().addParticle(ParticleTypes.HAPPY_VILLAGER, d, e, f, 0.0D, 1.0D, 0.0D);
                    }
                }
                this.flagWave = 0;
            }
        }
    }

    public void getPiglins() {
        List<LivingEntity> list = this.getLevel().getEntitiesByClass(LivingEntity.class, new AABB(this.getBlockPos()).inflate(40D), EntitySelector.EXCEPT_SPECTATOR);
        for (int i = 0; i < list.size(); ++i) {
            LivingEntity entity = list.get(i);
            if (entity.getType() == EntityType.PIGLIN) {
                Piglin piglin = (Piglin) entity;
                if (piglin.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET)) {
                    piglin.getBrain().forget(MemoryModuleType.ANGRY_AT);
                    piglin.getBrain().forget(MemoryModuleType.ATTACK_TARGET);
                    piglin.getBrain().forget(MemoryModuleType.WALK_TARGET);
                }
            }
        }
    }

}
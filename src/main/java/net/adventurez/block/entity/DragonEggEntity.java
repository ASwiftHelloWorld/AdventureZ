package net.adventurez.block.entity;

import net.adventurez.entity.DragonEntity;
import net.adventurez.entity.TheEyeEntity;
import net.adventurez.init.BlockInit;
import net.adventurez.init.ConfigInit;
import net.adventurez.init.EffectInit;
import net.adventurez.init.EntityInit;
import net.adventurez.init.TagInit;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class DragonEggEntity extends BlockEntity {
    private int overallSummoningTick;
    private int overallHatchingTick;
    private int hatchTick;
    private int summoningTick;
    private boolean isHatchAble;

    public DragonEggEntity(BlockPos pos, BlockState state) {
        super(BlockInit.DRAGON_EGG_ENTITY, pos, state);
    }

    @Override
    protected void readNbt(CompoundTag nbt, Provider registryLookup) {
        super.readNbt(nbt, registryLookup);
        hatchTick = nbt.getInt("Hatch_Tick");
        isHatchAble = nbt.getBoolean("Hatch_Able");
        summoningTick = nbt.getInt("Summoning_Tick");
    }

    @Override
    protected void writeNbt(CompoundTag nbt, Provider registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putBoolean("Hatch_Able", isHatchAble);
        nbt.putInt("Hatch_Tick", hatchTick);
        nbt.putInt("Summoning_Tick", summoningTick);
    }

    public static void clientTick(Level world, BlockPos pos, BlockState state, DragonEggEntity blockEntity) {
        blockEntity.tick();
    }

    public static void serverTick(Level world, BlockPos pos, BlockState state, DragonEggEntity blockEntity) {
        blockEntity.tick();
    }

    @Override
    public void markDirty() {
        super.setChanged();
        sendUpdate();
    }

    private void sendUpdate() {
        if (this.getLevel() != null) {
            BlockState state = this.getLevel().getBlockState(this.pos);
            (this.getLevel()).updateListeners(this.pos, state, state, 3);
        }
    }

    private boolean isValid(Level world, BlockPos pos, BlockState state) {
        int stoneCounter;
        int stoneCounter2;
        int stoneCounter3 = 0;
        int stoneCounter4;
        int stoneCounter5;
        BlockState rod_1 = world.getBlockState(pos.north().east());
        BlockState rod_2 = world.getBlockState(pos.north().west());
        BlockState rod_3 = world.getBlockState(pos.south().east());
        BlockState rod_4 = world.getBlockState(pos.south().west());
        if (rod_1.equals(Blocks.END_ROD.defaultBlockState()) && rod_2.equals(Blocks.END_ROD.defaultBlockState()) && rod_3.equals(Blocks.END_ROD.defaultBlockState())
                && rod_4.equals(Blocks.END_ROD.defaultBlockState())) {
            for (stoneCounter4 = -1; stoneCounter4 < 2; stoneCounter4++) {
                for (stoneCounter5 = -1; stoneCounter5 < 2; stoneCounter5++) {
                    BlockState middleState = world.getBlockState(pos.north(stoneCounter4).east(stoneCounter5).down());
                    if (middleState.getBlock().equals(Blocks.CRYING_OBSIDIAN)) {
                        stoneCounter3++;
                    }
                }
            }
            for (stoneCounter = -2; stoneCounter < 3; stoneCounter++) {
                for (stoneCounter2 = -2; stoneCounter2 < 3; stoneCounter2++) {
                    BlockState baseState = world.getBlockState(pos.north(stoneCounter).east(stoneCounter2).down(2));
                    if (net.adventurez.util.MinecraftCompat.isBlockInTag(baseState, TagInit.PLATFORM_END_BLOCKS)) {
                        stoneCounter3++;
                    }
                }
            }
        }
        if (stoneCounter3 == 34) {
            return true;
        } else {
            return false;
        }
    }

    private void tick() {
        this.updateTheEyeSummoning();
        this.updateDragonHatching();
    }

    private void updateDragonHatching() {
        if (ConfigInit.CONFIG.allow_dragon_hatching && !this.getLevel().isClientSide()) {
            overallHatchingTick++;
            if (overallHatchingTick == 20) {
                if (!this.isHatchAble) {
                    if (((ServerLevel) this.getLevel()).getEnderDragonFight() != null && !((ServerLevel) this.getLevel()).getEnderDragonFight().hasPreviouslyKilled())
                        return;
                    Player playerEntity = this.getLevel().getClosestPlayer(this.getBlockPos().getX() + 0.5D, this.getBlockPos().getY(), this.getBlockPos().getZ() + 0.5D, 2.5D, true);
                    if (playerEntity != null && playerEntity.hasStatusEffect(EffectInit.FAME))
                        enableEggHatching();
                } else {
                    this.hatchTick++;
                    if (this.hatchTick % 60 == 0) {
                        for (int i = 0; i < 20; i++) {
                            double d = (double) pos.getX() + (double) this.getLevel().getRandom().nextFloat();
                            double e = (double) pos.getY() + (double) this.getLevel().getRandom().nextFloat();
                            double f = (double) pos.getZ() + (double) this.getLevel().getRandom().nextFloat();
                            ((ServerLevel) this.getLevel()).spawnParticles(ParticleTypes.HAPPY_VILLAGER, d, e, f, 0, 0.0D, 0.0D, 0.0D, 0.01D);
                        }
                    }
                    if (this.hatchTick >= 598) {
                        this.getLevel().breakBlock(this.pos, false);
                        DragonEntity dragonEntity = EntityInit.DRAGON.create(this.getLevel());
                        dragonEntity.refreshPositionAndAngles((double) this.getBlockPos().getX() + 0.5D, (double) this.getBlockPos().getY() + 0.55D, (double) this.getBlockPos().getZ() + 0.5D, 90F, 0.0F);
                        dragonEntity.initialize(((ServerLevel) this.getLevel()), this.getLevel().getLocalDifficulty(pos), EntitySpawnReason.STRUCTURE, null);
                        dragonEntity.setSize(1);
                        this.getLevel().spawnEntity(dragonEntity);
                    }
                }

            }
            if (overallHatchingTick >= 20) {
                overallHatchingTick = 0;
            }
        }
    }

    private void updateTheEyeSummoning() {
        if (ConfigInit.CONFIG.allow_the_eye_summoning) {
            if (this.getLevel().getBlockState(pos.down()).equals(Blocks.CRYING_OBSIDIAN.defaultBlockState()) && this.getLevel().getRegistryKey() == Level.END) {
                overallSummoningTick++;
                BlockState state = this.getBlockState();
                if (overallSummoningTick == 20 && this.isValid(this.getLevel(), pos, state)) {
                    summoningTick++;
                    if (!this.getLevel().isClientSide() && summoningTick >= 60) {
                        TheEyeEntity theEyeEntity = (TheEyeEntity) EntityInit.THE_EYE.create(this.getLevel());
                        theEyeEntity.refreshPositionAndAngles((double) this.getBlockPos().getX() + 0.5D, (double) this.getBlockPos().getY() + 0.55D, (double) this.getBlockPos().getZ() + 0.5D, 90F, 0.0F);
                        theEyeEntity.initialize(((ServerLevel) this.getLevel()), this.getLevel().getLocalDifficulty(pos), EntitySpawnReason.STRUCTURE, null);
                        theEyeEntity.setEyeInvulnerabletime();
                        this.getLevel().spawnEntity(theEyeEntity);
                        this.getLevel().breakBlock(pos, false);
                    }
                }
                if (overallSummoningTick >= 20) {
                    overallSummoningTick = 0;
                }
                if (this.getLevel().isClientSide() && summoningTick != 0) {
                    if (summoningTick % 10 == 0) {
                        this.dragonAltartParticle(1);
                    }
                    if (summoningTick % 5 == 0) {
                        this.dragonAltartParticle(2);
                    }
                }

            }
        }
    }

    private void dragonAltartParticle(int distance) {
        double d = (double) pos.east(distance).north(distance).getX() + (double) world.random.nextFloat();
        double e = (double) pos.up(1 - distance).getY() + (double) world.random.nextFloat() * 2.0D;
        double f = (double) pos.east(distance).north(distance).getZ() + (double) world.random.nextFloat();
        double d1 = (double) pos.west(distance).north(distance).getX() + (double) world.random.nextFloat();
        double e1 = (double) pos.up(1 - distance).getY() + (double) world.random.nextFloat() * 2.0D;
        double f1 = (double) pos.west(distance).north(distance).getZ() + (double) world.random.nextFloat();
        double d2 = (double) pos.south(distance).east(distance).getX() + (double) world.random.nextFloat();
        double e2 = (double) pos.up(1 - distance).getY() + (double) world.random.nextFloat() * 2.0D;
        double f2 = (double) pos.south(distance).east(distance).getZ() + (double) world.random.nextFloat();
        double d3 = (double) pos.south(distance).west(distance).getX() + (double) world.random.nextFloat();
        double e3 = (double) pos.up(1 - distance).getY() + (double) world.random.nextFloat() * 2.0D;
        double f3 = (double) pos.south(distance).west(distance).getZ() + (double) world.random.nextFloat();
        world.addParticle(ParticleTypes.END_ROD, d, e, f, 0.0D, 0.0D, 0.0D);
        world.addParticle(ParticleTypes.END_ROD, d1, e1, f1, 0.0D, 0.0D, 0.0D);
        world.addParticle(ParticleTypes.END_ROD, d2, e2, f2, 0.0D, 0.0D, 0.0D);
        world.addParticle(ParticleTypes.END_ROD, d3, e3, f3, 0.0D, 0.0D, 0.0D);
    }

    public void enableEggHatching() {
        this.isHatchAble = true;
    }

}
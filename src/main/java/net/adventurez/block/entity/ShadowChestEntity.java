package net.adventurez.block.entity;

import net.adventurez.init.BlockInit;
import net.adventurez.init.ConfigInit;
import net.adventurez.init.ItemInit;
import net.adventurez.init.SoundInit;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Holder;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class ShadowChestEntity extends ChestBlockEntity {

    public ShadowChestEntity(BlockPos pos, BlockState state) {
        super(BlockInit.SHADOW_CHEST_ENTITY, pos, state);
    }

    public static void clientTick(Level world, BlockPos pos, BlockState state, ShadowChestEntity blockEntity) {
        ChestBlockEntity.lidAnimateTick(world, pos, state, blockEntity);
        if (blockEntity.getOpenNess(1.0F) > 0.0F) {
            for (int i = 0; i < 3; ++i) {
                int j = world.getRandom().nextInt(2) * 2 - 1;
                int k = world.getRandom().nextInt(2) * 2 - 1;
                double d = (double) pos.getX() + 0.5D + 0.25D * (double) j;
                double e = (double) ((float) pos.getY() + world.getRandom().nextFloat());
                double f = (double) pos.getZ() + 0.5D + 0.25D * (double) k;
                double g = (double) world.getRandom().nextFloat() * (float) j * 0.1D;
                double h = (double) world.getRandom().nextFloat() * 0.4D;
                double l = (double) world.getRandom().nextFloat() * (float) k * 0.1D;
                world.addParticle(ParticleTypes.END_ROD, d, e, f, g, h, l);
            }
        }
    }

    @Override
    public void startOpen(ContainerUser user) {
        super.startOpen(user);
    }

    @Override
    public void stopOpen(ContainerUser user) {
        super.stopOpen(user);
        if (this.isEmpty() && this.getLevel() != null && !this.getLevel().isClientSide()) {
            this.getLevel().destroyBlock(this.getBlockPos(), true);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        BlockPos pos = this.getBlockPos();
        return this.getLevel() != null && this.getLevel().getBlockEntity(pos) == this
                && player.distanceToSqr((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
    }

    public void onScheduledTick() {
        if (!this.removed) {
            this.recheckOpen();
        }

    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("adventurez.container.shadowchest");
    }

    @Override
    protected AbstractContainerMenu createMenu(int syncId, Inventory playerInventory) {
        return ChestMenu.createGeneric9x3(syncId, playerInventory, this);
    }

    @Override
    protected void signalOpenCount(Level world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        super.signalOpenCount(world, pos, state, oldViewerCount, newViewerCount);
        if (oldViewerCount == 0 && newViewerCount > 0) {
            playContainerSound(world, pos, SoundInit.OPEN_SHADOW_CHEST_EVENT);
        } else if (oldViewerCount > 0 && newViewerCount == 0) {
            playContainerSound(world, pos, SoundInit.CLOSE_SHADOW_CHEST_EVENT);
        }
    }

    private static void playContainerSound(Level world, BlockPos pos, SoundEvent sound) {
        world.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, sound, SoundSource.BLOCKS, 0.5F,
                world.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    public void setRandomLoot() {
        if (!this.getLevel().isClientSide()) {
            List<ItemStack> tableList = this.getLevel().getServer().getReloadableRegistries().getLootTable(BuiltInLootTables.END_CITY_TREASURE_CHEST)
                    .generateLoot(new LootParams.Builder((ServerLevel) this.getLevel()).add(LootContextParams.ORIGIN, Vec3.ofCenter(this.pos)).build(LootContextParamSets.CHEST));

            for (int i = 0; i < 27; i++) {
                if (i == 20 && this.getLevel().getRandom().nextFloat() < 0.2F && FabricLoader.getInstance().isModLoaded("medievalweapons")) {
                    this.getItems().set(i, new ItemStack(BuiltInRegistries.ITEM.get(Identifier.of("medievalweapons", "thalleous_sword"))));
                    continue;
                }
                if (i == 13)
                    this.getItems().set(i, new ItemStack(ItemInit.SOURCE_STONE));
                else if (i == 1)
                    this.getItems().set(i, new ItemStack(Items.DRAGON_EGG));
                else {
                    switch (this.getLevel().getRandom().nextInt(10)) {
                        case 0:
                            this.getItems().set(i, new ItemStack(Items.ENDER_PEARL, this.getLevel().getRandom().nextInt(5) + 1));
                            break;
                        case 4:
                            this.getItems().set(i, new ItemStack(Items.GOLDEN_APPLE, 1));
                            break;
                        case 5:
                            this.getItems().set(i, new ItemStack(Items.EXPERIENCE_BOTTLE, this.getLevel().getRandom().nextInt(12) + 1));
                            break;
                        case 6:
                            ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
                            RegistryAccess dynamicRegistryManager = this.getLevel().getRegistryManager();
                            Optional<Holder.Reference<Enchantment>> optional = dynamicRegistryManager.get(Registries.ENCHANTMENT).getRandom(this.getLevel().getRandom());
                            if (optional.isPresent()) {
                                stack.addEnchantment(optional.get(), optional.get().value().getMaxLevel() + (ConfigInit.CONFIG.allow_special_enchant_loot ? 1 : 0));
                            }
                            stack.set(ItemInit.VOID_DROP, true);

                            this.getItems().set(i, stack);
                            break;
                        case 7:
                            this.getItems().set(i, tableList.get(this.getLevel().getRandom().nextInt(tableList.size())));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

}

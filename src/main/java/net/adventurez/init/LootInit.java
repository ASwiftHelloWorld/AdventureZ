package net.adventurez.init;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.resources.ResourceKey;

import java.util.List;

import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.entries.LootItem;

public class LootInit {

    private static final List<ResourceKey<LootTable>> ADDED_LOOT_TABLE = List.of(BuiltInLootTables.PIGLIN_BARTERING_GAMEPLAY, BuiltInLootTables.BASTION_BRIDGE_CHEST, BuiltInLootTables.BASTION_HOGLIN_STABLE_CHEST,
            BuiltInLootTables.BASTION_OTHER_CHEST, BuiltInLootTables.BASTION_TREASURE_CHEST);

    private static boolean addedLootTable(ResourceKey<LootTable> lootTable) {
        for (ResourceKey<LootTable> id : ADDED_LOOT_TABLE) {
            if (id.equals(lootTable)) {
                return true;
            }
        }
        return false;
    }

    public static void init() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if (addedLootTable(key)) {
                LootPool pool = LootPool.builder().with(LootItem.builder(ItemInit.GILDED_BLACKSTONE_SHARD).build()).rolls(BinomialDistributionGenerator.create(1, 0.01F)).build();
                tableBuilder.pool(pool);
                if (BuiltInLootTables.BASTION_TREASURE_CHEST.equals(key)) {
                    pool = LootPool.builder().with(LootItem.builder(ItemInit.GILDED_UPGRADE_SMITHING_TEMPLATE).build()).rolls(BinomialDistributionGenerator.create(1, 0.5F)).build();
                    tableBuilder.pool(pool);
                }
            } else if ("minecraft:entities/piglin_brute".equals(key.getValue().toString())) {
                LootPool pool = LootPool.builder().with(LootItem.builder(ItemInit.GILDED_BLACKSTONE_SHARD).build()).rolls(BinomialDistributionGenerator.create(1, 0.1F)).build();
                tableBuilder.pool(pool);
            }
        });

    }

}
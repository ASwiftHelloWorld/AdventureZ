# AdventureZ: Vibe Edition Port Status

## Current State

- Target: Minecraft Java Edition 26.2, Fabric, Java 25.
- Branch: `port/minecraft-26.2`.
- Checkpoint: `6324ab5 checkpoint: preserve Minecraft 26.2 port progress` (port base is the upstream 1.21 commit `692a33a`).
- Worktree: 39 modified Java files plus this progress log since the checkpoint; no untracked source files. Latest `git diff --check` was clean before this log update.
- Build configuration: Loom `1.19.0-alpha.2`, Gradle wrapper `9.8.0`, Loader `0.19.5`, Fabric API `0.161.0+26.2`, Cloth Config `26.2.155`, Mod Menu `20.0.3`, Java 25. The current code uses Loom's no-remap plugin for the already-named Minecraft 26.2 jar.

## Completed Work

- Checkpointed the original in-progress port; no existing changes have been reset or discarded.
- Migrated Minecraft class/package references for the 26.2 unobfuscated environment across the existing mod sources. This initial pass still needs cleanup where APIs were redesigned or imported through wildcards.
- Added `MinecraftCompat` for typed item/block identity and tag checks, replacing verified old predicates without changing intended checks.
- Migrated common entity/block-entity accessors and primitive entity persistence patterns.
- Migrated synchronized entity data in 21 entity classes to `SynchedEntityData.defineId`, `defineSynchedData`, `Builder.define`, and `entityData`.
- Migrated all 22 entity `readCustomDataFromNbt`/`writeCustomDataToNbt` pairs to the 26.2 `ValueInput`/`ValueOutput` lifecycle. Complex Dragon item/UUID data, Orc inventory, VoidShadow block-position array, TinyEye target UUID, and VoidCloud owner UUID were converted to codecs/child-list APIs.
- Migrated entity AI lifecycle and goal registration names in 30 entity/goal files. Goal priorities, goals, and control flags were retained; the post-batch compile found no old AI lifecycle signatures.
- Migrated verified 26.2 attribute keys, `createMonsterAttributes`, and runtime `getAttribute` accessors across 31 entity/effect/mixin files; the post-batch compile reported zero attribute-specific diagnostics.
- Migrated 17 spawn restrictions into Fabric 26.2 `FabricEntityType.Builder.Mob.spawnPlacement` registrations and mapped Monster/Animal/NaturalSpawner predicates to verified target methods. Removed the duplicate old `SpawnInit` registration block while preserving all biome spawn entries. The latest compile exits `1` at the 1,000-error cap with zero `EntityInit`/`SpawnInit` diagnostics.
- Migrated `DragonEggEntity` and `PiglinFlagEntity` persistence to 26.2 block-entity `ValueInput`/`ValueOutput`; updated Piglin Flag change notifications to `setChanged`/`sendBlockUpdated` and its periodic clock to `getGameTime`. Migrated its nearby-player lookup, spectator filtering, and Piglin Brain memory clearing; the latest compile has zero `PiglinFlagEntity` diagnostics.
- Added `AdventureFlyingMob` as the 26.2 replacement for removed `FlyingMob`, matching 1.21 water/lava/air travel drag, fall suppression, and non-climbing behavior. Migrated EnderWhale, VoidShadow, VoidShade, TheEye, and VoidFragment; the latest compiler output contains no `FlyingMob` or `AdventureFlyingMob` diagnostics.
- Replaced shared `getDataTracker`, `getEntitiesByClass`, spectator selector, and BlockPos `up/down` calls with verified 26.2 APIs across Java sources.
- Migrated creative-tab registration to the 26.2 `CreativeModeTabEvents` API, preserving the existing custom tab and entries.
- Migrated custom block properties, directional properties, shapes, and the holder inventory/persistence contract. The shadow chest was migrated to a standalone 26.2 chest lifecycle while preserving its custom loot/menu/sound behavior.
- Updated Mod Menu to 20.0.3, the published release constrained to Minecraft 26.2; updated Java metadata and CI to Java 25.

## Build and Test Results

- Original upstream 1.21 baseline: `bash gradlew compileJava --no-daemon` succeeded before porting.
- Current checkpoint baseline: `./gradlew compileJava --no-daemon` exited 1. Javac reached its configured 1,000-error limit; the complete output is saved in ignored `build/porting/compile-baseline-current.log`.
- Synchronized-data batch: `./gradlew compileJava --no-daemon` exited 1 at the 1,000-error limit. Follow-up diagnostic inspection found zero errors in the migrated synchronized-data contracts.
- Primitive and complex entity-persistence batches: compile commands exited 1 at the 1,000-error limit. The reports are in `build/porting/compile-entity-persistence.log`, `build/porting/compile-dragon-persistence.log`, and `build/porting/compile-complex-persistence.log`. Before the last persistence changes, reported errors were unrelated to the migrated entity save/load contracts; TinyEye's missing tracker import was restored and recompiled.
- AI goal batch: `./gradlew compileJava --no-daemon` exited 1 at the 1,000-error limit. Inspection found no remaining old AI lifecycle signatures in entity source or corresponding compiler diagnostics.
- Attribute batch: `./gradlew compileJava --no-daemon` exited 1 at the 1,000-error limit; no attribute-specific errors were present. Full report: `build/porting/compile-attributes-current.log`.
- Spawn-placement batch: `./gradlew compileJava --no-daemon` exited 1 at the 1,000-error limit; no `EntityInit` or `SpawnInit` errors were present. Full report: `build/porting/compile-spawn-registration.log`.
- Block-entity sync batch: `./gradlew compileJava --no-daemon` exited 1 at the 1,000-error limit. The old Dragon Egg/Piglin Flag read/write callback diagnostics are gone; remaining errors are unrelated Level/player-query/Piglin Brain/container APIs. Full report: `build/porting/compile-block-entity-sync.log`.
- Latest Piglin Flag batch: `./gradlew compileJava --no-daemon` exited 1 at the 1,000-error limit; `PiglinFlagEntity.java` has zero diagnostics after the 26.2 player/Brain migration. Full report: `build/porting/compile-piglin-flag.log`.
- Shared accessors and flight-base batch: `./gradlew compileJava --no-daemon` exited `1` with 4,652 javac errors, below the temporary 5,000 error cap. Full report: `build/porting/compile-flying-base.log`.
- Gradle configuration check: `bash gradlew help --no-daemon` succeeded before the latest source edits.
- `git diff --check` passed at the last repository inspection.
- No full `build`, packaged JAR inspection, client/server launch, or gameplay test has succeeded yet. No functional JAR is available.

## Remaining Errors and Risks

- Latest compile: `./gradlew compileJava --no-daemon` exited `1` with 4,652 javac errors, below the temporary 5,000 cap. Full report: `build/porting/compile-flying-base.log`.
- The diagnostic-only `-Xmaxerrs 5000` setting remains in `build.gradle` and must be removed before completion.
- Still unresolved: major 26.2 rendering/model API redesign (`pivot`, `uv`, missing model/buffer classes), entity/item/world method changes, block-entity/container contracts, Fabric particle and registry APIs, mixin targets/descriptors, wildcard Yarn imports in `OrcEntity`, and unsupported optional Patchouli/VoidZ integrations.
- Some persisted formats have only been mechanically migrated and require compatibility/runtime review, particularly legacy Dragon owner-name conversion and Dragon inventory list encoding.
- Client-only code isolation, dedicated-server class loading, actual Mixin application, world save/reload behavior, and gameplay features have not been runtime-verified.

## Next Steps

1. Continue with server-side entity creation/world/item APIs and block-entity/container contracts; compile after each coherent group.
3. Resolve remaining Fabric APIs and item components while preserving all original registrations and behavior.
4. Migrate renderers/models and audit every Mixin target, descriptor, and injection point against Minecraft 26.2.
5. Remove the diagnostic cap and run `./gradlew compileJava --no-daemon` to success, then `./gradlew build --no-daemon`.
6. Inspect the produced JAR and metadata; attempt client and dedicated-server startup and report any unverified gameplay features honestly.

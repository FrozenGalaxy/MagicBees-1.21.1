package eu.dsix.magicbees.forestry;

import forestry.api.apiculture.ForestryBeeSpecies;
import forestry.api.core.genetics.alleles.BeeChromosomes;
import forestry.api.core.genetics.alleles.ForestryAlleles;
import forestry.api.plugin.IApicultureRegistration;
import forestry.api.plugin.IHiveBuilder;
import eu.dsix.magicbees.MagicBees;
import eu.dsix.magicbees.config.MagicBeesConfig;
import eu.dsix.magicbees.forestry.hive.MagicHiveBiomes;
import eu.dsix.magicbees.forestry.hive.MagicHiveDefinition;
import eu.dsix.magicbees.forestry.hive.MagicHivePlacements;
import eu.dsix.magicbees.registry.MagicBeesBlocks;
import eu.dsix.magicbees.registry.MagicBeesItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** Faithful registration of the six legacy Magic Bees wild hives and their eight generation descriptions. */
final class MagicBeeHives {
    /** Secondary generation-description IDs. The block itself still keys drops by its bee species ID. */
    static final net.minecraft.resources.ResourceLocation INFERNAL_OVERWORLD_HIVE = MagicBees.id("hive_infernal_overworld");
    static final net.minecraft.resources.ResourceLocation OBLIVION_OVERWORLD_HIVE = MagicBees.id("hive_oblivion_overworld");

    private MagicBeeHives() {
    }

    static void registerInitial(IApicultureRegistration apiculture) {
        Supplier<List<ItemStack>> mundane = () -> List.of(new ItemStack(MagicBeesItems.comb("mundane").get()));

        MagicHiveDefinition curious = new MagicHiveDefinition(
                MagicBeeSpecies.MYSTICAL,
                MagicBeesBlocks.CURIOUS_HIVE.get().defaultBlockState(),
                MagicHivePlacements.TREE,
                false,
                (biome, species) -> MagicHiveDefinition.any(biome,
                        MagicHiveBiomes.FOREST, MagicHiveBiomes.PLAINS, MagicHiveBiomes.HILL),
                MagicHiveDefinition.PostGeneration.NONE);
        defaultDrops(apiculture.registerHive(MagicBeeSpecies.MYSTICAL, curious).setGenerationChance(3.0f),
                MagicBeeSpecies.MYSTICAL, 0.15, mundane);

        MagicHiveDefinition unusual = new MagicHiveDefinition(
                MagicBeeSpecies.UNUSUAL,
                MagicBeesBlocks.UNUSUAL_HIVE.get().defaultBlockState(),
                MagicHivePlacements.GROUND_DIRT,
                false,
                (biome, species) -> MagicHiveDefinition.any(biome,
                        MagicHiveBiomes.PLAINS, MagicHiveBiomes.MOUNTAIN, MagicHiveBiomes.HILL, MagicHiveBiomes.RIVER),
                MagicHiveDefinition.PostGeneration.NONE);
        defaultDrops(apiculture.registerHive(MagicBeeSpecies.UNUSUAL, unusual).setGenerationChance(1.0f),
                MagicBeeSpecies.UNUSUAL, 0.15, mundane);

        MagicHiveDefinition resonant = new MagicHiveDefinition(
                MagicBeeSpecies.SORCEROUS,
                MagicBeesBlocks.RESONANT_HIVE.get().defaultBlockState(),
                MagicHivePlacements.GROUND_SAND,
                false,
                (biome, species) -> MagicHiveDefinition.any(biome,
                        MagicHiveBiomes.SANDY, MagicHiveBiomes.BADLANDS, MagicHiveBiomes.HOT,
                        MagicHiveBiomes.MAGICAL, MagicHiveBiomes.THAUMATURGE_MAGICAL),
                MagicHiveDefinition.PostGeneration.NONE);
        defaultDrops(apiculture.registerHive(MagicBeeSpecies.SORCEROUS, resonant).setGenerationChance(0.9f),
                MagicBeeSpecies.SORCEROUS, 0.20, mundane);

        MagicHiveDefinition deep = new MagicHiveDefinition(
                MagicBeeSpecies.ATTUNED,
                MagicBeesBlocks.DEEP_HIVE.get().defaultBlockState(),
                MagicHivePlacements.undergroundStone(10, 15, 5),
                true,
                (biome, species) -> MagicHiveDefinition.any(biome,
                        MagicHiveBiomes.HILL, MagicHiveBiomes.MOUNTAIN, MagicHiveBiomes.MAGICAL,
                        MagicHiveBiomes.THAUMATURGE_MAGICAL),
                MagicBeeHives::postGenRedstone);
        defaultDrops(apiculture.registerHive(MagicBeeSpecies.ATTUNED, deep).setGenerationChance(3.8f),
                MagicBeeSpecies.ATTUNED, 0.20, mundane);

        registerInfernal(apiculture);
        registerOblivion(apiculture);
    }

    private static void registerInfernal(IApicultureRegistration apiculture) {
        Supplier<List<ItemStack>> drops = () -> List.of(
                new ItemStack(MagicBeesItems.comb("molten").get()),
                new ItemStack(Items.GLOWSTONE_DUST, 6));

        // 1.12 had two independent IHiveDescription entries with separate generation chances.
        MagicHiveDefinition nether = new MagicHiveDefinition(
                MagicBeeSpecies.INFERNAL,
                MagicBeesBlocks.INFERNAL_HIVE.get().defaultBlockState(),
                MagicHivePlacements.nether(0, 175, 6),
                true,
                (biome, species) -> MagicHiveDefinition.any(biome, MagicHiveBiomes.NETHER),
                MagicBeeHives::postGenNetherQuartz);
        infernalDrops(apiculture.registerHive(MagicBeeSpecies.INFERNAL, nether).setGenerationChance(50.0f), drops);

        MagicHiveDefinition overworld = new MagicHiveDefinition(
                MagicBeeSpecies.INFERNAL,
                MagicBeesBlocks.INFERNAL_HIVE.get().defaultBlockState(),
                MagicHivePlacements.undergroundStone(5, 13, 6),
                true,
                (biome, species) -> MagicHiveDefinition.any(biome, MagicHiveBiomes.MAGICAL, MagicHiveBiomes.THAUMATURGE_MAGICAL, MagicHiveBiomes.HOT),
                MagicBeeHives::postGenGlowstone);
        infernalDrops(apiculture.registerHive(INFERNAL_OVERWORLD_HIVE, overworld).setGenerationChance(0.95f), drops);
    }

    private static void infernalDrops(IHiveBuilder builder, Supplier<List<ItemStack>> drops) {
        builder.addDrop(0.80, MagicBeeSpecies.INFERNAL, drops, 0.50f)
                .addDrop(0.03, ForestryBeeSpecies.STEADFAST, drops);
    }

    private static void registerOblivion(IApicultureRegistration apiculture) {
        Supplier<List<ItemStack>> drops = () -> List.of(
                new ItemStack(MagicBeesItems.comb("forgotten").get()), new ItemStack(Items.ENDER_PEARL));

        MagicHiveDefinition end = new MagicHiveDefinition(
                MagicBeeSpecies.OBLIVION,
                MagicBeesBlocks.OBLIVION_HIVE.get().defaultBlockState(),
                MagicHivePlacements.END,
                true,
                (biome, species) -> MagicHiveDefinition.any(biome, MagicHiveBiomes.END),
                MagicBeeHives::postGenObsidianSpike);
        oblivionDrops(apiculture.registerHive(MagicBeeSpecies.OBLIVION, end).setGenerationChance(20.0f), drops);

        MagicHiveDefinition overworld = new MagicHiveDefinition(
                MagicBeeSpecies.OBLIVION,
                MagicBeesBlocks.OBLIVION_HIVE.get().defaultBlockState(),
                MagicHivePlacements.undergroundStone(5, 5, 5),
                true,
                (biome, species) -> MagicHiveDefinition.any(biome, MagicHiveBiomes.MAGICAL, MagicHiveBiomes.THAUMATURGE_MAGICAL, MagicHiveBiomes.COLD),
                MagicBeeHives::postGenEndstone);
        oblivionDrops(apiculture.registerHive(OBLIVION_OVERWORLD_HIVE, overworld).setGenerationChance(0.87f), drops);
    }

    private static void oblivionDrops(IHiveBuilder builder, Supplier<List<ItemStack>> drops) {
        builder.addDrop(0.80, MagicBeeSpecies.OBLIVION, drops)
                .addDrop(0.09, ForestryBeeSpecies.STEADFAST, drops);
    }

    private static void defaultDrops(IHiveBuilder builder, net.minecraft.resources.ResourceLocation species,
                                     double rainResistantChance, Supplier<List<ItemStack>> comb) {
        builder.addDrop(0.80, species, comb, 0.70f)
                .addDrop(rainResistantChance, species, comb, 0.0f,
                        Map.of(BeeChromosomes.TOLERATES_RAIN, ForestryAlleles.TRUE_RECESSIVE))
                .addDrop(0.05, ForestryBeeSpecies.VALIANT, comb, 0.0f,
                        Map.of(BeeChromosomes.TOLERATES_RAIN, ForestryAlleles.TRUE_RECESSIVE));
    }

    private static void postGenRedstone(WorldGenLevel level, RandomSource random, BlockPos pos) {
        if (!MagicBeesConfig.COMMON.postGenRedstone.get()) {
            return;
        }
        for (Direction direction : Direction.values()) {
            MagicHiveWorldgen.spawnLegacyStoneVein(level, random, pos.relative(direction), 5,
                    Blocks.REDSTONE_BLOCK.defaultBlockState());
        }
    }

    private static void postGenNetherQuartz(WorldGenLevel level, RandomSource random, BlockPos pos) {
        if (!MagicBeesConfig.COMMON.postGenNetherQuartz.get()) {
            return;
        }
        for (Direction direction : Direction.values()) {
            MagicHiveWorldgen.spawnNetherrackVein(level, random, pos.relative(direction), 4,
                    Blocks.QUARTZ_BLOCK.defaultBlockState());
        }
    }

    private static void postGenGlowstone(WorldGenLevel level, RandomSource random, BlockPos pos) {
        if (!MagicBeesConfig.COMMON.postGenGlowstone.get()) {
            return;
        }
        for (Direction direction : Direction.values()) {
            BlockPos adjacent = pos.relative(direction);
            if (direction.getAxis() != Direction.Axis.Y) {
                MagicHiveWorldgen.spawnLegacyStoneVein(level, random, adjacent, random.nextInt(4) + 1,
                        Blocks.GLOWSTONE.defaultBlockState());
            } else if (MagicHivePlacements.isLegacyStone(level.getBlockState(adjacent))) {
                // 1.12 placed vertical glowstone directly; it did not pass through spawnVein's extra random roll.
                level.setBlock(adjacent, Blocks.GLOWSTONE.defaultBlockState(), Block.UPDATE_CLIENTS);
            }
        }
    }

    private static void postGenObsidianSpike(WorldGenLevel level, RandomSource random, BlockPos pos) {
        if (!MagicBeesConfig.COMMON.postGenObsidianSpikes.get()) {
            return;
        }
        int height = random.nextInt(8) + 3;
        for (int offset = 1; offset < height && pos.getY() - offset > 0; offset++) {
            level.setBlock(pos.below(offset), Blocks.OBSIDIAN.defaultBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    private static void postGenEndstone(WorldGenLevel level, RandomSource random, BlockPos pos) {
        if (!MagicBeesConfig.COMMON.postGenEndstone.get()) {
            return;
        }
        for (Direction direction : Direction.values()) {
            int count = random.nextInt(direction.getAxis() == Direction.Axis.Y ? 3 : 6) + 1;
            MagicHiveWorldgen.spawnLegacyStoneVein(level, random, pos.relative(direction), count,
                    Blocks.END_STONE.defaultBlockState());
        }
    }
}

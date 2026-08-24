package magicbees.forestry;

import forestry.api.apiculture.ForestryBeeEffects;
import forestry.api.apiculture.ForestryFlowerTypes;
import forestry.api.core.HumidityType;
import forestry.api.core.TemperatureType;
import forestry.api.core.genetics.ForestryTaxa;
import forestry.api.core.genetics.alleles.BeeChromosomes;
import forestry.api.core.genetics.alleles.ForestryAlleles;
import forestry.api.plugin.IBeeSpeciesBuilder;
import forestry.api.plugin.IGeneticRegistration;
import forestry.api.plugin.ITaxonBuilder;
import forestry.core.engine.genetics.flowers.TagFlowerType;
import magicbees.MagicBees;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/** Taxonomy/genome defaults from EnumBeeBranches in Magic Bees 1.12. */
final class MagicBeeTaxa {
    static final String VEILED = "Velatapis";
    static final String ARCANE = "Arcanapis";
    static final String SUPERNATURAL = "Occultapis";
    static final String ABOMINABLE = "Detestabilapis";
    static final String EXTRINSIC = "Extrarapis";
    static final String MAGICAL = "Magicapis";
    static final String SCHOLARLY = "Doctapis";
    static final String THAUMIC = "Thaumiapis";
    static final String SKULKING = "Malevolenapis";
    static final String METALLIC = "Metalliapis";
    static final String SOUL = "Animapis";
    static final String FLESHY = "Carnosapis";
    static final String TIME = "Tempestivapis";
    static final String GEM = "Lapidapis";
    static final String TRANSMUTING = "Transmutapis";
    static final String THERMAL = "Thermametallic";
    static final String BOTANICAL = "Botanica";

    /**
     * Legacy branch secondary colors from EnumBeeBranches. Species that did not provide an explicit second
     * color in 1.12 inherit these values.
     */
    static int legacySecondaryColor(String genus) {
        if (ARCANE.equals(genus) || SUPERNATURAL.equals(genus)) {
            return 0xFF9D60;
        }
        if (SKULKING.equals(genus)) {
            return 0xE15236;
        }
        if (ABOMINABLE.equals(genus)) {
            return 0x960F00;
        }
        if (EXTRINSIC.equals(genus)) {
            return 0xF696FF;
        }
        if (THAUMIC.equals(genus)) {
            return 0x999999;
        }
        if (BOTANICAL.equals(genus)) {
            return 0xFFB2BB;
        }
        return 0xFF7C26;
    }

    /** Modern normalized ID for the legacy magicbees:flowersBookshelf allele/provider. */
    static final ResourceLocation BOOKSHELF_FLOWERS = MagicBees.id("bookshelf_flowers");
    static final ResourceLocation THAUMIC_FLOWERS = MagicBees.id("thaumic_flowers");
    static final ResourceLocation BOTANICAL_FLOWERS = MagicBees.id("botanical_flowers");

    private MagicBeeTaxa() {
    }

    static void registerInitial(IGeneticRegistration genetics) {
        ForestryTaxonomyBridge.registerBeeSpine(genetics);
        genetics.registerFlowerType(BOOKSHELF_FLOWERS, new TagFlowerType(blockTag(BOOKSHELF_FLOWERS), false));
        genetics.registerFlowerType(BOTANICAL_FLOWERS, new BotanicalFlowerType());
        genetics.defineTaxon(ForestryTaxa.CLASS_INSECTS, ForestryTaxa.ORDER_HYMNOPTERA, order ->
                order.defineSubTaxon(ForestryTaxa.FAMILY_BEES, MagicBeeTaxa::registerBranches));
    }

    private static TagKey<Block> blockTag(ResourceLocation id) {
        return TagKey.create(Registries.BLOCK, id);
    }

    /**
     * Defines the Magic Bees genera and their inherited genome defaults.
     *
     * Kept separate from {@link #registerInitial(IGeneticRegistration)} so the legacy branch table can be
     * unit-tested without bootstrapping Forestry's internal root taxonomy bridge.
     */
    static void registerBranches(ITaxonBuilder family) {
        family.defineSubTaxon(VEILED);

        family.defineSubTaxon(ARCANE, genus -> {
            genus.setDefaultChromosome(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
            genus.setDefaultChromosome(BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_SLOW);
            // 1.12 EnumAllele.Fertility.HIGH = 3 offspring.
            genus.setDefaultChromosome(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_3);
        });

        family.defineSubTaxon(SUPERNATURAL, genus -> {
            setNeverSleeps(genus);
            genus.setDefaultChromosome(BeeChromosomes.SPEED, ForestryAlleles.SPEED_NORMAL);
            genus.setDefaultChromosome(BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_SLOWEST);
        });

        family.defineSubTaxon(SCHOLARLY, genus -> {
            genus.setDefaultChromosome(BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOW);
            genus.setDefaultChromosome(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_ELONGATED);
            genus.setDefaultChromosome(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_UP_2);
            genus.setDefaultChromosome(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
            setNeverSleeps(genus);
            genus.setDefaultChromosome(BeeChromosomes.FLOWER_TYPE, BOOKSHELF_FLOWERS);
        });

        family.defineSubTaxon(SKULKING, MagicBeeTaxa::setSkulkingDefaults);

        family.defineSubTaxon(MAGICAL, genus -> {
            genus.setDefaultChromosome(BeeChromosomes.SPEED, ForestryAlleles.SPEED_NORMAL);
            genus.setDefaultChromosome(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_NORMAL);
        });

        family.defineSubTaxon(TIME, genus -> {
            genus.setDefaultChromosome(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
            // 1.12 EnumAllele.Fertility.NORMAL = 2 offspring.
            genus.setDefaultChromosome(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_2);
            genus.setDefaultChromosome(BeeChromosomes.SPEED, ForestryAlleles.SPEED_NORMAL);
            genus.setDefaultChromosome(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
        });

        family.defineSubTaxon(SOUL, genus -> {
            genus.setDefaultChromosome(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
            genus.setDefaultChromosome(BeeChromosomes.TOLERATES_RAIN, ForestryAlleles.TRUE_RECESSIVE);
        });

        family.defineSubTaxon(ABOMINABLE, genus -> {
            genus.setDefaultChromosome(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_DOWN_2);
            genus.setDefaultChromosome(BeeChromosomes.FLOWER_TYPE, ForestryFlowerTypes.NETHER);
            setNeverSleeps(genus);
            genus.setDefaultChromosome(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
            genus.setDefaultChromosome(BeeChromosomes.EFFECT, ForestryBeeEffects.AGGRESSIVE);
            genus.setDefaultChromosome(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_SHORT);
        });

        family.defineSubTaxon(EXTRINSIC, genus -> {
            genus.setDefaultChromosome(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_UP_2);
            genus.setDefaultChromosome(BeeChromosomes.EFFECT, ForestryBeeEffects.AGGRESSIVE);
            setNeverSleeps(genus);
            genus.setDefaultChromosome(BeeChromosomes.FLOWER_TYPE, ForestryFlowerTypes.END);
            genus.setDefaultChromosome(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
        });

        family.defineSubTaxon(METALLIC, MagicBeeTaxa::setMetallicDefaults);
        family.defineSubTaxon(GEM, MagicBeeTaxa::setMetallicDefaults);

        family.defineSubTaxon(TRANSMUTING, genus -> {
            genus.setDefaultChromosome(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_SHORTEST);
            genus.setDefaultChromosome(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_UP_1);
            genus.setDefaultChromosome(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_DOWN_2);
        });

        family.defineSubTaxon(THAUMIC, genus -> {
            genus.setDefaultChromosome(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_1);
            genus.setDefaultChromosome(BeeChromosomes.FLOWER_TYPE, THAUMIC_FLOWERS);
        });

        // Legacy FLESHY delegates directly to SKULKING.setBranchProperties(...).
        family.defineSubTaxon(FLESHY, MagicBeeTaxa::setSkulkingDefaults);

        // BeeIntegrationInterface.getTemplateTE(...) supplied these branch defaults in 1.12.
        family.defineSubTaxon(THERMAL, genus -> {
            genus.setDefaultChromosome(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_2);
            genus.setDefaultChromosome(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_2);
            genus.setDefaultChromosome(BeeChromosomes.TOLERATES_RAIN, ForestryAlleles.TRUE_RECESSIVE);
            genus.setDefaultChromosome(BeeChromosomes.SPEED, ForestryAlleles.SPEED_FAST);
            genus.setDefaultChromosome(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_3);
            genus.setDefaultChromosome(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_LONGER);
        });

        family.defineSubTaxon(BOTANICAL, genus -> {
            genus.setDefaultChromosome(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
            genus.setDefaultChromosome(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
            genus.setDefaultChromosome(BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOWER);
        });
    }

    /** Applies the non-genome branch properties from EnumBeeBranches.setIndividualProperties(...). */
    static void applySpeciesDefaults(String genus, IBeeSpeciesBuilder species) {
        if (SCHOLARLY.equals(genus)) {
            species.setHumidity(HumidityType.ARID);
        } else if (ABOMINABLE.equals(genus)) {
            species.setTemperature(TemperatureType.HELLISH);
            species.setHumidity(HumidityType.ARID);
        } else if (EXTRINSIC.equals(genus)) {
            species.setTemperature(TemperatureType.COLD);
        }
    }

    private static void setSkulkingDefaults(ITaxonBuilder genus) {
        genus.setDefaultChromosome(BeeChromosomes.SPEED, ForestryAlleles.SPEED_FAST);
        genus.setDefaultChromosome(BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_FASTER);
        setNeverSleeps(genus);
        // 1.12 EnumAllele.Fertility.NORMAL = 2 offspring.
        genus.setDefaultChromosome(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_2);
    }

    private static void setMetallicDefaults(ITaxonBuilder genus) {
        genus.setDefaultChromosome(BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOWEST);
        genus.setDefaultChromosome(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_1);
        setNeverSleeps(genus);
        genus.setDefaultChromosome(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
    }

    private static void setNeverSleeps(ITaxonBuilder genus) {
        // ForestryCE folded the old NEVER_SLEEPS boolean into ACTIVITY. Use our recessive always-active value rather
        // than Forestry's dominant METATURNAL allele so 1.12 hybrid inheritance remains intact.
        genus.setDefaultChromosome(BeeChromosomes.ACTIVITY, MagicBeeActivityTypes.NEVER_SLEEPS);
    }


    private static final class BotanicalFlowerType extends TagFlowerType {
        private static final String[] COLORS = {"white","orange","magenta","light_blue","yellow","lime","pink","gray","light_gray","cyan","purple","blue","brown","green","red","black"};

        private BotanicalFlowerType() {
            super(blockTag(BOTANICAL_FLOWERS), false);
        }

        @Override public boolean plantRandomFlower(Level level, BlockPos pos, List<BlockState> nearbyFlowers) {
            if (!level.getBlockState(pos).isAir()) return false;
            String color = COLORS[level.random.nextInt(COLORS.length)];
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("botania", color + "_mystical_flower");
            if (!BuiltInRegistries.BLOCK.containsKey(id)) return false;
            Block block = BuiltInRegistries.BLOCK.get(id);
            if (block == Blocks.AIR) return false;
            BlockState state = block.defaultBlockState();
            return state.canSurvive(level, pos) && level.setBlock(pos, state, 3);
        }
    }
}

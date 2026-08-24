package magicbees.forestry;

import forestry.api.apiculture.ForestryBeeEffects;
import forestry.api.apiculture.ForestryFlowerTypes;
import forestry.api.core.HumidityType;
import forestry.api.core.TemperatureType;
import forestry.api.core.genetics.ITaxon;
import forestry.api.core.genetics.alleles.Allele;
import forestry.api.core.genetics.alleles.BeeChromosomes;
import forestry.api.core.genetics.alleles.ForestryAlleles;
import forestry.api.core.genetics.alleles.IChromosome;
import forestry.api.plugin.IBeeSpeciesBuilder;
import forestry.api.plugin.ITaxonBuilder;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Exact branch-default regression coverage against Magic Bees 1.12 EnumBeeBranches. */
class MagicBeeTaxaParityTest {
    @Test
    void legacyGenusNamesMatchMagicBees112() {
        assertEquals("Malevolenapis", MagicBeeTaxa.SKULKING);
        assertEquals("Magicapis", MagicBeeTaxa.MAGICAL);
        assertEquals("Metalliapis", MagicBeeTaxa.METALLIC);
    }

    @Test
    void legacyBranchSecondaryColorsMatchMagicBees112() {
        assertEquals(0xFF7C26, MagicBeeTaxa.legacySecondaryColor(MagicBeeTaxa.VEILED));
        assertEquals(0xFF9D60, MagicBeeTaxa.legacySecondaryColor(MagicBeeTaxa.ARCANE));
        assertEquals(0xFF9D60, MagicBeeTaxa.legacySecondaryColor(MagicBeeTaxa.SUPERNATURAL));
        assertEquals(0xFF7C26, MagicBeeTaxa.legacySecondaryColor(MagicBeeTaxa.SCHOLARLY));
        assertEquals(0xE15236, MagicBeeTaxa.legacySecondaryColor(MagicBeeTaxa.SKULKING));
        assertEquals(0xFF7C26, MagicBeeTaxa.legacySecondaryColor(MagicBeeTaxa.MAGICAL));
        assertEquals(0xFF7C26, MagicBeeTaxa.legacySecondaryColor(MagicBeeTaxa.TIME));
        assertEquals(0xFF7C26, MagicBeeTaxa.legacySecondaryColor(MagicBeeTaxa.SOUL));
        assertEquals(0x960F00, MagicBeeTaxa.legacySecondaryColor(MagicBeeTaxa.ABOMINABLE));
        assertEquals(0xF696FF, MagicBeeTaxa.legacySecondaryColor(MagicBeeTaxa.EXTRINSIC));
        assertEquals(0xFF7C26, MagicBeeTaxa.legacySecondaryColor(MagicBeeTaxa.METALLIC));
        assertEquals(0xFF7C26, MagicBeeTaxa.legacySecondaryColor(MagicBeeTaxa.GEM));
        assertEquals(0xFF7C26, MagicBeeTaxa.legacySecondaryColor(MagicBeeTaxa.TRANSMUTING));
        assertEquals(0x999999, MagicBeeTaxa.legacySecondaryColor(MagicBeeTaxa.THAUMIC));
        assertEquals(0xFF7C26, MagicBeeTaxa.legacySecondaryColor(MagicBeeTaxa.FLESHY));
        assertEquals(0xFF7C26, MagicBeeTaxa.legacySecondaryColor(MagicBeeTaxa.THERMAL));
        assertEquals(0xFFB2BB, MagicBeeTaxa.legacySecondaryColor(MagicBeeTaxa.BOTANICAL));
    }

    @Test
    void legacyBranchGenomeDefaultsAreRegistered() {
        CapturingTaxon family = new CapturingTaxon();
        MagicBeeTaxa.registerBranches(family);

        assertEquals(Set.of(
                MagicBeeTaxa.VEILED,
                MagicBeeTaxa.ARCANE,
                MagicBeeTaxa.SUPERNATURAL,
                MagicBeeTaxa.SCHOLARLY,
                MagicBeeTaxa.SKULKING,
                MagicBeeTaxa.MAGICAL,
                MagicBeeTaxa.TIME,
                MagicBeeTaxa.SOUL,
                MagicBeeTaxa.ABOMINABLE,
                MagicBeeTaxa.EXTRINSIC,
                MagicBeeTaxa.METALLIC,
                MagicBeeTaxa.GEM,
                MagicBeeTaxa.TRANSMUTING,
                MagicBeeTaxa.THAUMIC,
                MagicBeeTaxa.FLESHY,
                MagicBeeTaxa.THERMAL,
                MagicBeeTaxa.BOTANICAL
        ), family.children.keySet());
        assertTrue(family.child(MagicBeeTaxa.VEILED).defaults.isEmpty());

        CapturingTaxon arcane = family.child(MagicBeeTaxa.ARCANE);
        assertData(arcane, BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
        assertData(arcane, BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_SLOW);
        assertData(arcane, BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_3);

        CapturingTaxon supernatural = family.child(MagicBeeTaxa.SUPERNATURAL);
        assertReference(supernatural, BeeChromosomes.ACTIVITY, MagicBeeActivityTypes.NEVER_SLEEPS);
        assertData(supernatural, BeeChromosomes.SPEED, ForestryAlleles.SPEED_NORMAL);
        assertData(supernatural, BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_SLOWEST);

        CapturingTaxon scholarly = family.child(MagicBeeTaxa.SCHOLARLY);
        assertData(scholarly, BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOW);
        assertData(scholarly, BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_ELONGATED);
        assertData(scholarly, BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_UP_2);
        assertData(scholarly, BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
        assertReference(scholarly, BeeChromosomes.ACTIVITY, MagicBeeActivityTypes.NEVER_SLEEPS);
        assertReference(scholarly, BeeChromosomes.FLOWER_TYPE, MagicBeeTaxa.BOOKSHELF_FLOWERS);

        assertSkulkingDefaults(family.child(MagicBeeTaxa.SKULKING));
        assertSkulkingDefaults(family.child(MagicBeeTaxa.FLESHY));

        CapturingTaxon magical = family.child(MagicBeeTaxa.MAGICAL);
        assertData(magical, BeeChromosomes.SPEED, ForestryAlleles.SPEED_NORMAL);
        assertData(magical, BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_NORMAL);

        CapturingTaxon time = family.child(MagicBeeTaxa.TIME);
        assertData(time, BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
        assertData(time, BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_2);
        assertData(time, BeeChromosomes.SPEED, ForestryAlleles.SPEED_NORMAL);
        assertData(time, BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);

        CapturingTaxon soul = family.child(MagicBeeTaxa.SOUL);
        assertData(soul, BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
        assertData(soul, BeeChromosomes.TOLERATES_RAIN, ForestryAlleles.TRUE_RECESSIVE);

        CapturingTaxon abominable = family.child(MagicBeeTaxa.ABOMINABLE);
        assertData(abominable, BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_DOWN_2);
        assertReference(abominable, BeeChromosomes.FLOWER_TYPE, ForestryFlowerTypes.NETHER);
        assertReference(abominable, BeeChromosomes.ACTIVITY, MagicBeeActivityTypes.NEVER_SLEEPS);
        assertData(abominable, BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
        assertReference(abominable, BeeChromosomes.EFFECT, ForestryBeeEffects.AGGRESSIVE);
        assertData(abominable, BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_SHORT);

        CapturingTaxon extrinsic = family.child(MagicBeeTaxa.EXTRINSIC);
        assertData(extrinsic, BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_UP_2);
        assertReference(extrinsic, BeeChromosomes.EFFECT, ForestryBeeEffects.AGGRESSIVE);
        assertReference(extrinsic, BeeChromosomes.ACTIVITY, MagicBeeActivityTypes.NEVER_SLEEPS);
        assertReference(extrinsic, BeeChromosomes.FLOWER_TYPE, ForestryFlowerTypes.END);
        assertData(extrinsic, BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);

        assertMetallicDefaults(family.child(MagicBeeTaxa.METALLIC));
        assertMetallicDefaults(family.child(MagicBeeTaxa.GEM));

        CapturingTaxon transmuting = family.child(MagicBeeTaxa.TRANSMUTING);
        assertData(transmuting, BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_SHORTEST);
        assertData(transmuting, BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_UP_1);
        assertData(transmuting, BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_DOWN_2);

        CapturingTaxon thaumic = family.child(MagicBeeTaxa.THAUMIC);
        assertData(thaumic, BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_1);
        assertReference(thaumic, BeeChromosomes.FLOWER_TYPE, MagicBeeTaxa.THAUMIC_FLOWERS);

        CapturingTaxon thermal = family.child(MagicBeeTaxa.THERMAL);
        assertData(thermal, BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_2);
        assertData(thermal, BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_2);
        assertData(thermal, BeeChromosomes.TOLERATES_RAIN, ForestryAlleles.TRUE_RECESSIVE);
        assertData(thermal, BeeChromosomes.SPEED, ForestryAlleles.SPEED_FAST);
        assertData(thermal, BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_3);
        assertData(thermal, BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_LONGER);

        CapturingTaxon botanical = family.child(MagicBeeTaxa.BOTANICAL);
        assertData(botanical, BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
        assertData(botanical, BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
        assertData(botanical, BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOWER);
    }

    @Test
    void legacyBranchClimateDefaultsAreAppliedToSpeciesBuilders() {
        ClimateCapture scholarly = climate(MagicBeeTaxa.SCHOLARLY);
        assertNull(scholarly.temperature);
        assertEquals(HumidityType.ARID, scholarly.humidity);

        ClimateCapture abominable = climate(MagicBeeTaxa.ABOMINABLE);
        assertEquals(TemperatureType.HELLISH, abominable.temperature);
        assertEquals(HumidityType.ARID, abominable.humidity);

        ClimateCapture extrinsic = climate(MagicBeeTaxa.EXTRINSIC);
        assertEquals(TemperatureType.COLD, extrinsic.temperature);
        assertNull(extrinsic.humidity);

        ClimateCapture ordinary = climate(MagicBeeTaxa.ARCANE);
        assertNull(ordinary.temperature);
        assertNull(ordinary.humidity);
    }

    @Test
    void bookshelfFlowerTypeUsesNormalizedLegacyIdentity() {
        assertEquals(ResourceLocation.fromNamespaceAndPath("magicbees", "bookshelf_flowers"),
                MagicBeeTaxa.BOOKSHELF_FLOWERS);
    }

    private static void assertSkulkingDefaults(CapturingTaxon genus) {
        assertData(genus, BeeChromosomes.SPEED, ForestryAlleles.SPEED_FAST);
        assertData(genus, BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_FASTER);
        assertReference(genus, BeeChromosomes.ACTIVITY, MagicBeeActivityTypes.NEVER_SLEEPS);
        assertData(genus, BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_2);
    }

    private static void assertMetallicDefaults(CapturingTaxon genus) {
        assertData(genus, BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOWEST);
        assertData(genus, BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_1);
        assertReference(genus, BeeChromosomes.ACTIVITY, MagicBeeActivityTypes.NEVER_SLEEPS);
        assertData(genus, BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
    }

    private static void assertData(CapturingTaxon genus, IChromosome<?> chromosome, Allele<?> expected) {
        assertEquals(ITaxon.TaxonAllele.data(expected, true), genus.defaults.get(chromosome),
                "Wrong default for chromosome " + chromosome);
    }

    private static void assertReference(CapturingTaxon genus, IChromosome<ResourceLocation> chromosome,
                                        ResourceLocation expected) {
        assertEquals(ITaxon.TaxonAllele.reference(expected, true), genus.defaults.get(chromosome),
                "Wrong reference default for chromosome " + chromosome);
    }

    private static ClimateCapture climate(String genus) {
        ClimateCapture capture = new ClimateCapture();
        IBeeSpeciesBuilder builder = (IBeeSpeciesBuilder) Proxy.newProxyInstance(
                MagicBeeTaxaParityTest.class.getClassLoader(),
                new Class<?>[]{IBeeSpeciesBuilder.class},
                (proxy, method, args) -> {
                    return switch (method.getName()) {
                        case "setTemperature" -> {
                            capture.temperature = (TemperatureType) args[0];
                            yield proxy;
                        }
                        case "setHumidity" -> {
                            capture.humidity = (HumidityType) args[0];
                            yield proxy;
                        }
                        case "toString" -> "ClimateCaptureBuilder";
                        default -> throw new AssertionError("Unexpected builder call: " + method);
                    };
                });
        MagicBeeTaxa.applySpeciesDefaults(genus, builder);
        return capture;
    }

    private static final class ClimateCapture {
        private TemperatureType temperature;
        private HumidityType humidity;
    }

    private static final class CapturingTaxon implements ITaxonBuilder {
        private final Map<String, CapturingTaxon> children = new LinkedHashMap<>();
        private final Map<IChromosome<?>, ITaxon.TaxonAllele> defaults = new LinkedHashMap<>();

        @Override
        public void defineSubTaxon(String name) {
            this.children.put(name, new CapturingTaxon());
        }

        @Override
        public void defineSubTaxon(String name, Consumer<ITaxonBuilder> action) {
            CapturingTaxon child = new CapturingTaxon();
            this.children.put(name, child);
            action.accept(child);
        }

        @Override
        public <V> void setDefaultChromosome(IChromosome<V> chromosome, Allele<V> allele, boolean required) {
            this.defaults.put(chromosome, ITaxon.TaxonAllele.data(allele, required));
        }

        @Override
        public void setDefaultChromosome(IChromosome<ResourceLocation> chromosome, ResourceLocation id,
                                         boolean required) {
            this.defaults.put(chromosome, ITaxon.TaxonAllele.reference(id, required));
        }

        private CapturingTaxon child(String name) {
            CapturingTaxon child = this.children.get(name);
            if (child == null) {
                throw new AssertionError("Missing taxon " + name);
            }
            return child;
        }
    }
}

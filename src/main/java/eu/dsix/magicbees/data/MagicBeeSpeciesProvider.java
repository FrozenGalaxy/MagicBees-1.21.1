package eu.dsix.magicbees.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import forestry.api.apiculture.ForestryBeeJubilances;
import forestry.api.apiculture.IBeeJubilance;
import forestry.api.apiculture.genetics.IBeeSpeciesType;
import forestry.api.core.genetics.IGenome;
import forestry.api.core.genetics.alleles.Allele;
import forestry.api.core.genetics.alleles.AlleleOverride;
import forestry.api.core.genetics.alleles.IChromosome;
import forestry.api.plugin.IApicultureRegistration;
import forestry.api.plugin.IBeeSpeciesBuilder;
import forestry.api.plugin.IGenomeBuilder;
import forestry.apiculture.bees.genetics.BeeSpeciesDefinition;
import forestry.apiculture.bees.genetics.DefaultBeeJubilance;
import forestry.apiculture.plugin.ApicultureRegistration;
import forestry.core.platform.util.SpeciesUtil;
import eu.dsix.magicbees.forestry.MagicBeeSpecies;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Projects Magic Bees' Java bee builders into Forestry's datapack species format.
 *
 * <p>Forestry 3.0.0-alpha7 contains an addon-oriented provider in its source tree, but excludes
 * {@code forestry.core.data} from the published jar. This local bridge mirrors only its projection step.</p>
 */
public final class MagicBeeSpeciesProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    public MagicBeeSpeciesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "bee_species");
        this.lookupProvider = lookupProvider;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        return this.lookupProvider.thenCompose(provider -> {
            RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, provider);
            List<CompletableFuture<?>> futures = new ArrayList<>();
            buildDefinitions().forEach((id, definition) -> {
                JsonElement json = BeeSpeciesDefinition.codec().encodeStart(ops, definition).getOrThrow();
                futures.add(DataProvider.saveStable(cache, json, this.pathProvider.json(id)));
            });
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        });
    }

    static Map<ResourceLocation, BeeSpeciesDefinition> buildDefinitions() {
        IBeeSpeciesType type = SpeciesUtil.BEE_TYPE.get();
        ApicultureRegistration registration = new ApicultureRegistration(type);
        MagicBeeSpecies.registerInitial(registration);

        Map<ResourceLocation, BeeSpeciesDefinition> definitions = new LinkedHashMap<>();
        registration.forEachSpeciesBuilder((id, builder) -> definitions.put(id, buildDefinition(builder)));
        return definitions;
    }

    private static BeeSpeciesDefinition buildDefinition(IBeeSpeciesBuilder builder) {
        IBeeJubilance jubilance = builder.getJubilance();
        if (jubilance != DefaultBeeJubilance.INSTANCE) {
            throw new IllegalStateException("Unsupported custom jubilance for " + builder.getGenus() + ' ' + builder.getSpecies());
        }

        RecordingGenomeBuilder genome = new RecordingGenomeBuilder();
        builder.buildGenome(genome);

        return new BeeSpeciesDefinition(
                builder.getGenus(), builder.getSpecies(), builder.isDominant(), builder.hasGlint(), builder.isSecret(),
                builder.getComplexity(), builder.getAuthority(), builder.getEscritoireColor(), builder.getTemperature(),
                builder.getHumidity(), builder.getBody(), builder.getStripes(), builder.getOutline(),
                builder.buildProducts(), builder.buildSpecialties(), ForestryBeeJubilances.DEFAULT, genome.overrides
        );
    }

    @Override
    public String getName() {
        return "Magic Bees Bee Species";
    }

    private static final class RecordingGenomeBuilder implements IGenomeBuilder {
        private final Map<ResourceLocation, AlleleOverride<?>> overrides = new LinkedHashMap<>();

        @Override
        public <V> void set(IChromosome<V> chromosome, Allele<V> allele) {
            this.overrides.put(chromosome.id(), AlleleOverride.both(allele));
        }

        @Override
        public void set(IChromosome<ResourceLocation> chromosome, ResourceLocation id) {
            this.overrides.put(chromosome.id(), AlleleOverride.both(Allele.reference(id)));
        }

        @Override
        public <V> void setActive(IChromosome<V> chromosome, Allele<V> allele) {
            record(chromosome, AlleleOverride.onlyActive(allele));
        }

        @Override
        public <V> void setInactive(IChromosome<V> chromosome, Allele<V> allele) {
            record(chromosome, AlleleOverride.onlyInactive(allele));
        }

        @Override
        public IGenome build() {
            return null;
        }

        @Override
        public void setRemainingDefault() {
            // Datagen records only sparse overrides.
        }

        @Override
        public boolean isEmpty() {
            return this.overrides.isEmpty();
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private void record(IChromosome<?> chromosome, AlleleOverride<?> override) {
            this.overrides.merge(chromosome.id(), override,
                    (existing, added) -> ((AlleleOverride) existing).overrideWith(added));
        }
    }
}

package magicbees.forestry.hive;

import forestry.api.IForestryApi;
import forestry.api.apiculture.genetics.IBeeSpecies;
import forestry.api.apiculture.genetics.IBeeSpeciesType;
import forestry.api.apiculture.hives.IHiveDefinition;
import forestry.api.apiculture.hives.IHivePlacement;
import forestry.api.core.HumidityType;
import forestry.api.core.TemperatureType;
import forestry.api.core.genetics.ClimateHelper;
import forestry.api.core.genetics.ForestrySpeciesTypes;
import forestry.api.core.genetics.alleles.BeeChromosomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.BiPredicate;

/** One modern hive definition. Climate checks intentionally use the registered Magic Bees species genome. */
public final class MagicHiveDefinition implements IHiveDefinition {
    private final ResourceLocation speciesId;
    private final BlockState state;
    private final IHivePlacement placement;
    private final boolean ignoreClimate;
    private final BiPredicate<Holder<Biome>, ResourceLocation> biomePredicate;
    private final PostGeneration postGeneration;

    public MagicHiveDefinition(ResourceLocation speciesId, BlockState state, IHivePlacement placement,
                               boolean ignoreClimate, BiPredicate<Holder<Biome>, ResourceLocation> biomePredicate,
                               PostGeneration postGeneration) {
        this.speciesId = speciesId;
        this.state = state;
        this.placement = placement;
        this.ignoreClimate = ignoreClimate;
        this.biomePredicate = biomePredicate;
        this.postGeneration = postGeneration;
    }

    @Override
    public IHivePlacement getHiveGen() {
        return placement;
    }

    @Override
    public BlockState getBlockState() {
        return state;
    }

    @Override
    public boolean isGoodBiome(Holder<Biome> biome) {
        return biomePredicate.test(biome, speciesId);
    }

    @Override
    public boolean isGoodHumidity(HumidityType humidity) {
        if (ignoreClimate) {
            return true;
        }
        IBeeSpecies species = species();
        return ClimateHelper.isWithinLimits(humidity, species.getHumidity(),
                species.getDefaultGenome().getActiveValue(BeeChromosomes.HUMIDITY_TOLERANCE));
    }

    @Override
    public boolean isGoodTemperature(TemperatureType temperature) {
        if (ignoreClimate) {
            return true;
        }
        IBeeSpecies species = species();
        return ClimateHelper.isWithinLimits(temperature, species.getTemperature(),
                species.getDefaultGenome().getActiveValue(BeeChromosomes.TEMPERATURE_TOLERANCE));
    }

    @Override
    public void postGen(WorldGenLevel level, RandomSource random, BlockPos pos) {
        postGeneration.run(level, random, pos);
    }

    private IBeeSpecies species() {
        IBeeSpeciesType type = IForestryApi.INSTANCE.getGeneticManager()
                .getSpeciesType(ForestrySpeciesTypes.BEE, IBeeSpeciesType.class);
        return type.getSpecies(speciesId);
    }

    @FunctionalInterface
    public interface PostGeneration {
        PostGeneration NONE = (level, random, pos) -> { };
        void run(WorldGenLevel level, RandomSource random, BlockPos pos);
    }

    public static boolean any(Holder<Biome> biome, TagKey<Biome>... tags) {
        for (TagKey<Biome> tag : tags) {
            if (biome.is(tag)) {
                return true;
            }
        }
        return false;
    }
}

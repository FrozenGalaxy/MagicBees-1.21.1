package eu.dsix.magicbees.forestry.hive;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

/**
 * NeoForge common-tag equivalents for the Forge 1.12 {@code BiomeDictionary.Type} values used by Magic Bees.
 *
 * <p>Using the {@code c:} tags is deliberate: the old BiomeDictionary categories were cross-mod categories,
 * not vanilla-only biome tests. This also lets modern biome mods opt into the same categories without Magic Bees
 * taking a hard dependency on them.</p>
 */
public final class MagicHiveBiomes {
    public static final TagKey<Biome> FOREST = common("is_forest");
    public static final TagKey<Biome> PLAINS = common("is_plains");
    public static final TagKey<Biome> HILL = common("is_hill");
    public static final TagKey<Biome> MOUNTAIN = common("is_mountain");
    public static final TagKey<Biome> RIVER = common("is_river");
    public static final TagKey<Biome> SANDY = common("is_sandy");
    public static final TagKey<Biome> BADLANDS = common("is_badlands"); // 1.12 BiomeDictionary.Type.MESA
    public static final TagKey<Biome> HOT = common("is_hot");
    public static final TagKey<Biome> COLD = common("is_cold");
    public static final TagKey<Biome> MAGICAL = common("is_magical");
    // Current Thaumaturge exposes its magical-biome classification under its own namespace.
    // Check it alongside the modern cross-mod c:is_magical category so that the supplied current target
    // retains the old BiomeDictionary.MAGICAL behavior even if it does not populate the common tag.
    public static final TagKey<Biome> THAUMATURGE_MAGICAL = TagKey.create(Registries.BIOME,
            ResourceLocation.fromNamespaceAndPath("thaumaturge", "is_magical"));
    public static final TagKey<Biome> NETHER = common("is_nether");
    public static final TagKey<Biome> END = common("is_end");

    private MagicHiveBiomes() {
    }

    private static TagKey<Biome> common(String path) {
        return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath("c", path));
    }
}

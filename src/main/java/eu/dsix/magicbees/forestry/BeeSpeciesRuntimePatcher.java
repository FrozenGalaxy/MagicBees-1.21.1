package eu.dsix.magicbees.forestry;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.dsix.magicbees.MagicBees;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/** Applies loadout-dependent legacy registration and output rules before Forestry decodes bee species JSON. */
public final class BeeSpeciesRuntimePatcher {
    private static final Set<String> THERMAL_SPECIES = Set.of(
            "te_blizzy", "te_gelid", "te_dante", "te_pyro", "te_shocking", "te_amped", "te_grounded", "te_rocking",
            "te_coal", "te_destabilized", "te_lux"
    );
    private static final Set<String> DISABLED_THERMAL_LINE = Set.of("te_winsome", "te_endearing");
    private static final Set<String> BOTANIA_CONFIGURABLE_SECRET = Set.of(
            "bot_rooted", "bot_botanic", "bot_blossom", "bot_floral", "bot_vazbee", "bot_alfheim"
    );
    private static final List<String> PETAL_COLORS = List.of(
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
    );
    private static final List<String> PASTURE_SEEDS = List.of(
            "pasture_seeds", "dry_pasture_seeds", "golden_pasture_seeds", "vivid_pasture_seeds",
            "scorched_pasture_seeds", "infused_pasture_seeds", "mutated_pasture_seeds"
    );
    private static final Map<String, OreOutput> ORE_OUTPUTS = Map.ofEntries(
            Map.entry("silver", ore(0.16f, "silver")), Map.entry("lead", ore(0.17f, "lead")),
            Map.entry("aluminium", ore(0.20f, "aluminum", "aluminium")), Map.entry("ardite", ore(0.18f, "ardite")),
            Map.entry("cobalt", ore(0.18f, "cobalt")), Map.entry("manyullyn", ore(0.16f, "manyullyn")),
            Map.entry("osmium", ore(0.16f, "osmium")), Map.entry("electrum", ore(0.18f, "electrum")),
            Map.entry("platinum", ore(0.18f, "platinum")), Map.entry("nickel", ore(0.18f, "nickel")),
            Map.entry("invar", ore(0.18f, "invar")),
            Map.entry("silicon", exact(0.16f, "ae2:silicon")),
            Map.entry("certus", exact(0.08f, "ae2:certus_quartz_crystal")),
            Map.entry("fluix", exact(0.06f, "ae2:fluix_crystal"))
    );

    private BeeSpeciesRuntimePatcher() {
    }

    public record Loadout(boolean thaumaturge, boolean thermal, boolean botania, boolean ae2,
                          boolean showAllBees) {
    }

    public static Map<ResourceLocation, JsonElement> patch(Map<ResourceLocation, JsonElement> definitions,
                                                            Loadout loadout,
                                                            Predicate<ResourceLocation> itemExists,
                                                            Function<ResourceLocation, Optional<ResourceLocation>> tagItem) {
        Map<ResourceLocation, JsonElement> patched = new LinkedHashMap<>(definitions.size());
        definitions.forEach((id, json) -> {
            if (!MagicBees.MOD_ID.equals(id.getNamespace()) || !json.isJsonObject()) {
                patched.put(id, json);
                return;
            }
            String path = id.getPath();
            if (inactiveOptional(path, loadout)) {
                return;
            }
            if (DISABLED_THERMAL_LINE.contains(path)) {
                return;
            }

            JsonObject species = json.getAsJsonObject().deepCopy();
            OreOutput ore = ORE_OUTPUTS.get(path);
            if (ore != null) {
                Optional<ResourceLocation> item = ore.resolve(itemExists, tagItem);
                if (item.isEmpty()) {
                    return;
                }
                addSpecialty(species, item.get(), ore.chance());
            }

            applySecretPolicy(path, species, loadout.showAllBees());
            applyConditionalOutputs(path, species, loadout, itemExists);
            patched.put(id, species);
        });
        return patched;
    }

    private static boolean inactiveOptional(String path, Loadout loadout) {
        return (!loadout.thaumaturge() && path.startsWith("tc_"))
                || (!loadout.thermal() && THERMAL_SPECIES.contains(path))
                || (!loadout.botania() && path.startsWith("bot_"))
                || (!loadout.ae2() && path.equals("ae_skystone"));
    }

    private static void applySecretPolicy(String path, JsonObject species, boolean showAllBees) {
        if (showAllBees || species.has("secret") && species.get("secret").getAsBoolean()) {
            return;
        }
        String genus = species.has("genus") ? species.get("genus").getAsString() : "";
        boolean branchSecret = !genus.equals("Velatapis") && !genus.equals("Malevolenapis") && !genus.equals("Botanica");
        if (branchSecret || BOTANIA_CONFIGURABLE_SECRET.contains(path)) {
            species.addProperty("secret", true);
        }
    }

    private static void applyConditionalOutputs(String path, JsonObject species, Loadout loadout,
                                                Predicate<ResourceLocation> itemExists) {
        if (loadout.thaumaturge()) {
            if (path.equals("batty")) addSpecialty(species, ResourceLocation.withDefaultNamespace("gunpowder"), 0.33f);
            if (path.equals("scholarly")) addSpecialty(species, MagicBees.id("resource_lore_fragment"), 0.15f);
            if (path.equals("savant")) addSpecialty(species, MagicBees.id("resource_lore_fragment"), 0.40f);
        }
        if (loadout.ae2() && path.equals("ae_skystone")) {
            addExistingSpecialty(species, "ae2:sky_stone_block", 0.02f, itemExists);
        }
        if (loadout.thermal()) {
            switch (path) {
                case "te_blizzy" -> addExistingSpecialty(species, "thermal:blizz_powder", 0.09f, itemExists);
                case "te_dante" -> addExistingSpecialty(species, "thermal:sulfur_dust", 0.09f, itemExists);
                case "te_shocking" -> addExistingSpecialty(species, "thermal:blitz_powder", 0.09f, itemExists);
                case "te_grounded" -> addExistingSpecialty(species, "thermal:basalz_powder", 0.09f, itemExists);
            }
        }
        if (loadout.botania()) {
            float petalChance = switch (path) {
                case "bot_botanic" -> 0.01f;
                case "bot_blossom" -> 0.04f;
                case "bot_floral" -> 0.16f;
                default -> 0.0f;
            };
            if (petalChance > 0.0f) {
                for (String color : PETAL_COLORS) {
                    addExistingSpecialty(species, "botania:" + color + "_mystical_petal", petalChance, itemExists);
                }
            }
            if (path.equals("bot_vazbee")) {
                for (String seed : PASTURE_SEEDS) {
                    addExistingSpecialty(species, "botania:" + seed, 0.04f, itemExists);
                }
            }
        }
    }

    private static void addExistingSpecialty(JsonObject species, String id, float chance,
                                             Predicate<ResourceLocation> itemExists) {
        ResourceLocation item = ResourceLocation.parse(id);
        if (itemExists.test(item)) addSpecialty(species, item, chance);
    }

    private static void addSpecialty(JsonObject species, ResourceLocation item, float chance) {
        JsonArray specialties = species.has("specialties") ? species.getAsJsonArray("specialties") : new JsonArray();
        for (JsonElement element : specialties) {
            if (element.isJsonObject() && element.getAsJsonObject().has("item")
                    && element.getAsJsonObject().get("item").getAsString().equals(item.toString())) {
                return;
            }
        }
        JsonObject output = new JsonObject();
        output.addProperty("chance", chance);
        output.addProperty("item", item.toString());
        specialties.add(output);
        species.add("specialties", specialties);
    }

    public static Optional<ResourceLocation> firstItemInTag(ResourceLocation tagId) {
        TagKey<net.minecraft.world.item.Item> tag = TagKey.create(Registries.ITEM, tagId);
        return BuiltInRegistries.ITEM.getTag(tag).flatMap(holders -> holders.stream().findFirst())
                .flatMap(holder -> holder.unwrapKey().map(key -> key.location()));
    }

    public static boolean resourceSpeciesAvailable(String path) {
        if (DISABLED_THERMAL_LINE.contains(path)) {
            return false;
        }
        OreOutput output = ORE_OUTPUTS.get(path);
        return output == null || output.resolve(BuiltInRegistries.ITEM::containsKey,
                BeeSpeciesRuntimePatcher::firstItemInTag).isPresent();
    }

    public static boolean hasRuntimeSpecialties(String path) {
        return ORE_OUTPUTS.containsKey(path) || path.equals("batty") || path.equals("scholarly") || path.equals("savant")
                || path.equals("ae_skystone") || Set.of("te_blizzy", "te_dante", "te_shocking", "te_grounded",
                "bot_botanic", "bot_blossom", "bot_floral", "bot_vazbee").contains(path);
    }

    private static OreOutput ore(float chance, String... materials) {
        return new OreOutput(chance, List.of(), java.util.Arrays.stream(materials)
                .map(material -> ResourceLocation.fromNamespaceAndPath("c", "nuggets/" + material)).toList());
    }

    private static OreOutput exact(float chance, String... ids) {
        return new OreOutput(chance, java.util.Arrays.stream(ids).map(ResourceLocation::parse).toList(), List.of());
    }

    private record OreOutput(float chance, List<ResourceLocation> exactItems, List<ResourceLocation> itemTags) {
        Optional<ResourceLocation> resolve(Predicate<ResourceLocation> itemExists,
                                           Function<ResourceLocation, Optional<ResourceLocation>> tagItem) {
            return exactItems.stream().filter(itemExists).findFirst()
                    .or(() -> itemTags.stream().map(tagItem).flatMap(Optional::stream).findFirst());
        }
    }
}

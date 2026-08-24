package magicbees;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import magicbees.gametest.LegacyBeeParityData;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MagicBeesStaticFidelityTest {
    private record MutationEdge(String first, String second) {
    }

    private static final List<String> REPRESENTATIVE_SPECIES = List.of(
            "mystical", "sorcerous", "unusual", "attuned", "infernal", "oblivion",
            "ethereal", "watery", "earthy", "firey", "windy", "scholarly", "soul", "skulking"
    );
    private static final List<String> NATIVE_SPECIES = List.of(
            "mystical", "sorcerous", "unusual", "attuned", "eldritch",
            "esoteric", "mysterious", "arcane", "charmed", "enchanted", "supernatural",
            "ghastly", "iron", "aware", "spirit", "soul", "skulking", "spidery", "smouldering",
            "batty", "hateful", "brainy", "bigbad", "infernal", "oblivion", "ethereal", "watery",
            "earthy", "firey", "windy", "pupil", "scholarly", "savant",
            "invisible", "chicken", "beef", "pork", "sheepish", "horse", "catty",
            "timely", "lordly", "doctoral", "spiteful", "withering", "nameless", "abandoned", "forlorn", "draconic",
            "mutable", "transmuting", "crumbling",
            "gold", "copper", "tin", "silver", "lead", "aluminium", "ardite", "cobalt", "manyullyn", "osmium",
            "electrum", "platinum", "nickel", "invar", "bronze", "diamond", "emerald", "apatite", "silicon", "certus", "fluix",
            "te_blizzy", "te_gelid", "te_dante", "te_pyro", "te_shocking", "te_amped", "te_grounded", "te_rocking",
            "te_coal", "te_destabilized", "te_lux", "te_winsome", "te_endearing",
            "bot_rooted", "bot_botanic", "bot_blossom", "bot_floral", "bot_vazbee", "bot_somnolent", "bot_dreaming", "bot_alfheim",
            "ae_skystone"
        );
        private static final List<String> THAUMIC_SPECIES = List.of(
            "tc_air", "tc_fire", "tc_water", "tc_earth", "tc_order", "tc_entropy",
            "tc_vis", "tc_rejuvenating", "tc_empowering", "tc_nexus", "tc_taint", "tc_pure",
            "tc_hungry", "tc_void", "tc_wispy"
        );
            private static final List<String> THAUMIC_MUTATIONS = List.of(
                "tc_air", "tc_fire", "tc_water", "tc_earth", "tc_order", "tc_entropy",
                "tc_vis", "tc_rejuvenating", "tc_empowering", "tc_nexus", "tc_taint", "tc_pure",
                "tc_wispy", "tc_void"
            );

    @Test
    void modernPresentationKeysExistForRepresentativeContent() throws IOException {
        String language = resource("assets/magicbees/lang/en_us.json");

        for (String species : REPRESENTATIVE_SPECIES) {
            assertTrue(language.contains("\"allele.forestry.bee_species.magicbees." + species + "\""),
                    "Missing species translation: " + species);
        }
        for (String item : List.of("bee_comb_te_destabilized", "bee_comb_te_carbon", "drop_enchanted")) {
            assertTrue(language.contains("\"item.magicbees." + item + "\""),
                    "Missing item translation: " + item);
        }
        for (String species : THAUMIC_SPECIES) {
            assertTrue(language.contains("\"allele.forestry.bee_species.magicbees." + species + "\""),
                "Missing Thaumic species translation: " + species);
        }
    }

    @Test
    void representativeMutationAndSpeciesResourcesArePackaged() throws IOException {
        for (String species : NATIVE_SPECIES) {
            assertTrue(getClass().getClassLoader().getResource(
                    "data/magicbees/bee_species/" + species + ".json") != null,
                    "Missing live bee species definition: " + species);
        }
        for (String mutation : List.of("aware", "spirit_from_ethereal", "soul", "skulking", "bigbad")) {
            assertTrue(getClass().getClassLoader().getResource(
                    "data/magicbees/recipe/bee_mutation/" + mutation + ".json") != null,
                    "Missing mutation resource: " + mutation);
        }
        for (String species : THAUMIC_SPECIES) {
            assertTrue(getClass().getClassLoader().getResource(
                "data/magicbees/bee_species/" + species + ".json") != null,
                "Missing Thaumic live bee species definition: " + species);
        }
        for (String recipe : List.of("centrifuge_tc_air", "centrifuge_tc_fire", "centrifuge_tc_water",
            "centrifuge_tc_earth", "centrifuge_tc_order", "centrifuge_tc_entropy",
            "centrifuge_propolis_air", "centrifuge_propolis_fire", "centrifuge_propolis_water",
            "centrifuge_propolis_earth", "centrifuge_propolis_order", "centrifuge_propolis_entropy")) {
            String path = "data/magicbees/recipe/thaumaturge/" + recipe + ".json";
            assertTrue(getClass().getClassLoader().getResource(path) != null, "Missing Thaumic recipe: " + recipe);
            String json = resource(path);
            assertTrue(json.contains("\"modid\": \"thaumaturge\""),
                "Thaumic recipe is not dependency guarded: " + recipe);
        }
            for (String mutation : THAUMIC_MUTATIONS) {
                String path = "data/magicbees/recipe/bee_mutation/" + mutation + ".json";
                assertTrue(getClass().getClassLoader().getResource(path) != null,
                    "Missing Thaumic mutation: " + mutation);
                assertTrue(resource(path).contains("\"modid\": \"thaumaturge\""),
                    "Thaumic mutation is not dependency guarded: " + mutation);
            }
            String tempus = resource("data/magicbees/thaumaturge/aspect/tempus.json");
            assertTrue(tempus.contains("thaumaturge:vacuos"), "Tempus aspect must use the canonical Vacuos aspect");
    }

        @Test
        void clientResourcePathsAndMutationSchemasAreModernized() throws IOException {
            for (String path : List.of(
                    "assets/magicbees/textures/block/manaapiarybooster0.png",
                    "assets/magicbees/textures/block/manaapiarybooster1.png")) {
                assertTrue(getClass().getClassLoader().getResource(path) != null, "Missing normalized asset path: " + path);
            }
            for (String mutation : List.of("tc_void", "tc_nexus", "hateful", "smouldering")) {
                String json = resource("data/magicbees/recipe/bee_mutation/" + mutation + ".json");
                assertTrue(!json.contains("\"biome\": \"#"), "Mutation uses obsolete biome tag syntax: " + mutation);
            }
            String brainy = resource("data/magicbees/recipe/bee_mutation/brainy.json");
            assertTrue(brainy.contains("\"neoforge:conditions\""), "Brainy lacks its optional dependency guard");
            assertTrue(!brainy.contains("\"conditions\": [{\"type\": \"neoforge:mod_loaded\""),
                    "Brainy puts a NeoForge condition in Forestry's condition list");
        }

        @Test
        void ae2SkyStoneProgressionMatchesLegacyGraph() throws IOException {
            String skyStone = resource("data/magicbees/recipe/bee_mutation/ae_skystone.json");
            assertTrue(skyStone.contains("\"modid\": \"ae2\""), "Sky Stone mutation is not AE2-guarded");
            assertTrue(skyStone.contains("\"chance\": 0.2"), "Sky Stone mutation chance drifted");
            assertTrue(skyStone.contains("\"first\": \"magicbees:earthy\""), "Sky Stone first parent drifted");
            assertTrue(skyStone.contains("\"second\": \"magicbees:windy\""), "Sky Stone second parent drifted");
            assertTrue(skyStone.contains("\"Name\": \"ae2:sky_stone_block\""), "Sky Stone resource requirement drifted");

            String silicon = resource("data/magicbees/recipe/bee_mutation/silicon_ae2.json");
            assertTrue(silicon.contains("magicbees:ae_skystone") && silicon.contains("magicbees:iron"),
                    "Silicon must use Sky Stone + Iron when AE2 is loaded");
            String certus = resource("data/magicbees/recipe/bee_mutation/certus_ae2.json");
            assertTrue(certus.contains("magicbees:silicon") && certus.contains("magicbees:ae_skystone"),
                    "Certus must use Silicon + Sky Stone when AE2 is loaded");
            String fluix = resource("data/magicbees/recipe/bee_mutation/fluix_ae2.json");
            assertTrue(fluix.contains("magicbees:certus") && fluix.contains("magicbees:ae_skystone"),
                    "Fluix must use Certus + Sky Stone when AE2 is loaded");
        }

        @Test
        void baseForestryProcessingRecipesMatchLegacyLedger() throws IOException {
            for (String name : List.of(
                    "mundane_comb", "molten_comb", "forgotten_comb", "occult_comb", "otherworldly_comb",
                    "papery_comb", "intellect_comb", "furtive_comb", "soul_comb", "temporal_comb",
                    "transmuted_comb", "airy_comb", "firey_comb", "watery_comb", "earthy_comb")) {
                String path = "data/magicbees/recipe/centrifuge/" + name + ".json";
                assertTrue(getClass().getClassLoader().getResource(path) != null,
                        "Missing legacy base centrifuge recipe: " + name);
                assertTrue(resource(path).contains("\"type\": \"forestry:centrifuge\""),
                        "Wrong centrifuge recipe type: " + name);
            }

            String mundane = resource("data/magicbees/recipe/centrifuge/mundane_comb.json");
            assertTrue(mundane.contains("\"forestry:beeswax\"") && mundane.contains("0.9"),
                    "Mundane comb lost beeswax 90% output");
            assertTrue(mundane.contains("\"forestry:honey_drop\"") && mundane.contains("0.6"),
                    "Mundane comb lost honey drop 60% output");
            assertTrue(mundane.contains("\"magicbees:wax_magic\"") && mundane.contains("0.1"),
                    "Mundane comb lost magic wax 10% output");

            String watery = resource("data/magicbees/recipe/centrifuge/watery_comb.json");
            assertTrue(watery.contains("minecraft:ink_sac"),
                    "Legacy Items.DYE metadata 0 must map to modern ink_sac");

            for (String name : List.of("candles_string", "candles_silk_wisp", "aromatic_lump_1", "aromatic_lump_2")) {
                String path = "data/magicbees/recipe/carpenter/" + name + ".json";
                assertTrue(getClass().getClassLoader().getResource(path) != null,
                        "Missing legacy carpenter recipe: " + name);
                assertTrue(resource(path).contains("\"type\": \"forestry:carpenter\""),
                        "Wrong carpenter recipe type: " + name);
            }
            String candles = resource("data/magicbees/recipe/carpenter/candles_string.json");
            assertTrue(candles.contains("\"amount\": 600") && candles.contains("\"count\": 24"),
                    "24-candle carpenter recipe drifted");
            String aromatic = resource("data/magicbees/recipe/carpenter/aromatic_lump_1.json");
            assertTrue(aromatic.contains("forestry:honey") && aromatic.contains("\"amount\": 1000")
                            && aromatic.contains("magicbees:resource_aromatic_lump") && aromatic.contains("\"count\": 2"),
                    "Aromatic Lump carpenter recipe drifted");

            String waxTag = resource("data/magicbees/tags/item/waxes.json");
            for (String wax : List.of("wax_magic", "wax_soul", "wax_amnesic")) {
                assertTrue(waxTag.contains("magicbees:" + wax), "Wax wildcard compatibility tag lost " + wax);
            }
        }

        @Test
        void everySpeciesHasACompleteReachableLegacyAcquisitionDefinition() throws IOException {
            Set<String> expected = new HashSet<>(NATIVE_SPECIES);
            expected.addAll(THAUMIC_SPECIES);
            Map<String, List<MutationEdge>> mutations = new HashMap<>();
            Path mutationDirectory = Path.of("src/main/resources/data/magicbees/recipe/bee_mutation");
            try (var files = Files.list(mutationDirectory)) {
                for (Path file : files.filter(path -> path.toString().endsWith(".json")).toList()) {
                    JsonObject json = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
                    assertTrue(json.has("first") && json.has("second") && json.has("result") && json.has("chance"),
                            "Mutation is structurally incomplete: " + file);
                    String first = json.get("first").getAsString();
                    String second = json.get("second").getAsString();
                    String result = json.get("result").getAsString();
                    float chance = json.get("chance").getAsFloat();
                    assertTrue(chance > 0.0f && chance <= 1.0f, "Mutation chance cannot breed: " + file + " = " + chance);
                    if (first.equals(second)) {
                        String resultPath = result.startsWith("magicbees:")
                                ? result.substring("magicbees:".length()) : result;
                        assertTrue(LegacyBeeParityData.LEGACY_SELF_PARENT_BREEDING.contains(resultPath),
                                "Non-legacy mutation uses the same parent twice: " + file);
                    }
                    if (result.startsWith("magicbees:")) {
                        String path = result.substring("magicbees:".length());
                        assertTrue(expected.contains(path), "Mutation produces an unknown Magic Bees species: " + result);
                        mutations.computeIfAbsent(path, ignored -> new ArrayList<>()).add(new MutationEdge(first, second));
                    }
                    for (String parent : List.of(first, second)) {
                        if (parent.startsWith("magicbees:")) {
                            assertTrue(expected.contains(parent.substring("magicbees:".length())),
                                    "Mutation references an unknown Magic Bees parent: " + parent + " in " + file);
                        }
                    }
                }
            }

            Set<String> breedingOnly = new HashSet<>(expected);
            breedingOnly.removeAll(LegacyBeeParityData.NON_BREEDING_ACQUISITIONS.keySet());
            assertTrue(mutations.keySet().equals(breedingOnly),
                    "Every breeding-only species must have a mutation and only non-breeding acquisitions may omit one. Missing/extra: "
                            + symmetricDifference(mutations.keySet(), breedingOnly));

            Set<String> reachable = new HashSet<>();
            LegacyBeeParityData.NON_BREEDING_ACQUISITIONS.forEach((species, acquisition) -> {
                if (acquisition == LegacyBeeParityData.Acquisition.NATURAL_HIVE) {
                    reachable.add(species);
                }
            });
            boolean changed;
            do {
                changed = false;
                for (Map.Entry<String, List<MutationEdge>> entry : mutations.entrySet()) {
                    for (MutationEdge edge : entry.getValue()) {
                        if (staticallyReachableParent(reachable, edge.first())
                                && staticallyReachableParent(reachable, edge.second())) {
                            changed |= reachable.add(entry.getKey());
                            break;
                        }
                    }
                }
                for (Map.Entry<String, LegacyBeeParityData.Conversion> entry
                        : LegacyBeeParityData.BOTANIA_CONVERSIONS.entrySet()) {
                    if (reachable.contains(entry.getValue().inputSpecies())) {
                        changed |= reachable.add(entry.getKey());
                    }
                }
            } while (changed);
            assertTrue(reachable.equals(expected),
                    "Species acquisition graph contains unreachable definitions: " + symmetricDifference(reachable, expected));
        }

        @Test
        void everyCombRecipeMatchesEveryExactLegacyOutputAndChance() throws IOException {
            assertTrue(LegacyBeeParityData.CENTRIFUGE.size() == 25,
                    "The exhaustive legacy comb ledger must contain all 25 registered comb types");
            for (Map.Entry<String, LegacyBeeParityData.Centrifuge> entry : LegacyBeeParityData.CENTRIFUGE.entrySet()) {
                String comb = entry.getKey();
                LegacyBeeParityData.Centrifuge expected = entry.getValue();
                String path = expected.requiredMod() == null
                        ? "data/magicbees/recipe/centrifuge/" + comb + "_comb.json"
                        : "data/magicbees/recipe/thaumaturge/centrifuge_" + comb + ".json";
                JsonObject json = JsonParser.parseString(resource(path)).getAsJsonObject();
                assertTrue(json.get("type").getAsString().equals("forestry:centrifuge"),
                        "Comb recipe is not a Forestry centrifuge recipe: " + comb);
                assertTrue(json.get("id").getAsString().equals("magicbees:" + expected.recipePath()),
                        "Comb recipe's declared ID differs from its actual datapack location: " + comb);
                assertTrue(json.getAsJsonObject("input").get("item").getAsString().equals("magicbees:bee_comb_" + comb),
                        "Comb recipe input drifted: " + comb);
                assertTrue(json.get("time").getAsInt() == 20, "Comb recipe processing time drifted: " + comb);
                Map<String, Float> actual = new HashMap<>();
                json.getAsJsonArray("products").forEach(element -> {
                    JsonObject product = element.getAsJsonObject();
                    actual.put(product.get("item").getAsString(), product.get("chance").getAsFloat());
                });
                Map<String, Float> exact = new HashMap<>();
                expected.products().forEach(product -> exact.put(product.itemId(), product.chance()));
                assertTrue(actual.keySet().equals(exact.keySet()),
                        "Comb recipe output items drifted: " + comb + " -> " + actual.keySet());
                exact.forEach((item, chance) -> assertTrue(Math.abs(actual.get(item) - chance) < 0.000001f,
                        "Comb recipe chance drifted: " + comb + " -> " + item));
                assertTrue(actual.keySet().stream().anyMatch(item -> !item.equals("forestry:honey_drop")
                                && !item.equals("forestry:honeydew")),
                        "Comb produces only generic honey: " + comb);
            }
        }

        @Test
        void clientPresentationResourcesMatchLegacyStep17Ledger() throws IOException {
            String moon = resource("assets/magicbees/models/item/moondial.json");
            assertTrue(moon.contains("magicbees:moon_phase"), "Moon Dial lost its moon-phase item property");
            for (int phase = 0; phase < 8; phase++) {
                assertTrue(getClass().getClassLoader().getResource(
                        "assets/magicbees/models/item/moondial_" + phase + ".json") != null,
                        "Missing Moon Dial phase model " + phase);
            }

            String magnet = resource("assets/magicbees/models/item/mysteriousmagnet.json");
            assertTrue(magnet.contains("magicbees:active"), "Magnet lost its active-state model property");
            assertTrue(getClass().getClassLoader().getResource("assets/magicbees/models/item/mysteriousmagnet_off.json") != null,
                    "Missing inactive Magnet model");
            assertTrue(getClass().getClassLoader().getResource("assets/magicbees/models/item/mysteriousmagnet_on.json") != null,
                    "Missing active Magnet model");

            String jarBlock = resource("assets/magicbees/models/block/effectjar.json");
            String jarItem = resource("assets/magicbees/models/item/effectjar.json");
            assertTrue(jarBlock.contains("neoforge:composite") && jarBlock.contains("collector_jar_glass")
                            && jarBlock.contains("collector_jar_lid"),
                    "Default Effect Jar block model must combine glass and lid");
            assertTrue(jarItem.contains("neoforge:composite") && jarItem.contains("collector_jar_glass")
                            && jarItem.contains("collector_jar_lid"),
                    "Default Effect Jar inventory model must combine glass and lid");
            assertTrue(resource("assets/magicbees/models/block/collector_jar_glass.json")
                            .contains("minecraft:translucent"),
                    "Effect Jar glass lost its translucent render type");

            String oldJar = resource("assets/magicbees/models/block/effectjar_old.json");
            assertTrue(oldJar.contains("neoforge:composite") && oldJar.contains("neoforge:obj"),
                    "Legacy oldJarmodel path is not backed by NeoForge OBJ geometry");
            assertTrue(oldJar.contains("jarBase") && oldJar.contains("jarLid")
                            && oldJar.contains("minecraft:translucent") && oldJar.contains("minecraft:solid"),
                    "Legacy OBJ model lost separate base/lid render passes");
            String obj = resource("assets/magicbees/models/block/obj/effectjar.obj");
            assertTrue(obj.contains("o jarBase") && obj.contains("o jarLid"),
                    "Legacy Effect Jar OBJ lost its named parts");
            assertTrue(resource("assets/magicbees/models/block/obj/effectjarlibstuff.txt")
                            .contains("magicbees:model/jartexture"),
                    "Legacy Effect Jar material lost its original texture mapping");
            assertTrue(getClass().getClassLoader().getResource("assets/magicbees/textures/model/jartexture.png") != null,
                    "Legacy Effect Jar OBJ texture is missing");
            assertTrue(getClass().getClassLoader().getResource("assets/magicbees/textures/inventory/jarscreen.png") != null,
                    "Legacy Effect Jar screen texture is missing");

            String language = resource("assets/magicbees/lang/en_us.json");
            for (String key : List.of("hive.curious", "hive.unusual", "hive.resonant", "hive.deep",
                    "hive.infernal", "hive.oblivion", "aromatic_lump")) {
                assertTrue(language.contains("\"magicbees.jei.description." + key + "\""),
                        "Missing legacy JEI description translation: " + key);
            }
        }

        @Test
        void finalOptionalDependencyPackagingIsDeterministic() throws IOException {
            for (String species : NATIVE_SPECIES) {
                String json = resource("data/magicbees/bee_species/" + species + ".json");
                for (String namespace : List.of("thermal:", "ae2:", "botania:", "railcraft:", "thaumaturge:")) {
                    assertTrue(!json.contains("\"item\": \"" + namespace),
                            "Native species " + species + " bakes an optional-mod item specialty: " + namespace);
                }
            }

            String mixins = resource("magicbees.mixins.json");
            assertTrue(mixins.contains("BeeSpeciesManagerMixin"),
                    "Thaumaturge-absent species filter mixin is not packaged");
            String modMetadata = resource("META-INF/neoforge.mods.toml");
            assertTrue(modMetadata.contains("magicbees.mixins.json"),
                    "Magic Bees mixin config is not registered in NeoForge metadata");

            List<String> commonFields = java.util.Arrays.stream(magicbees.config.MagicBeesConfig.Common.class.getDeclaredFields())
                    .map(java.lang.reflect.Field::getName)
                    .toList();
            assertTrue(commonFields.contains("showAllBees"),
                    "Legacy showAllBees behavior is not exposed to the raw species reload patcher");
            for (String dead : List.of("removeUnneededBees", "enableGemBees")) {
                assertTrue(!commonFields.contains(dead), "Dead legacy config knob is still exposed: " + dead);
            }
        }

    private static boolean staticallyReachableParent(Set<String> reachable, String parent) {
        return !parent.startsWith("magicbees:") || reachable.contains(parent.substring("magicbees:".length()));
    }

    private static Set<String> symmetricDifference(Set<String> first, Set<String> second) {
        Set<String> difference = new HashSet<>(first);
        difference.addAll(second);
        Set<String> intersection = new HashSet<>(first);
        intersection.retainAll(second);
        difference.removeAll(intersection);
        return difference;
    }

    private static String resource(String path) throws IOException {
        try (InputStream stream = MagicBeesStaticFidelityTest.class.getClassLoader().getResourceAsStream(path)) {
            assertTrue(stream != null, "Missing resource: " + path);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}

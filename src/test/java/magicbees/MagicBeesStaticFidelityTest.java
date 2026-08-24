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
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MagicBeesStaticFidelityTest {
    private static final Pattern JAVA_STRING_LITERAL = Pattern.compile("\"((?:\\\\.|[^\"\\\\])*)\"");
    private static final Pattern TRANSLATION_KEY_SHAPE = Pattern.compile("[a-z][a-z0-9_]*(?:\\.[a-z0-9_]+)+");

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
            "te_coal", "te_destabilized", "te_lux",
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
        for (String item : List.of("bee_comb_te_destabilized", "bee_comb_te_carbon", "drop_enchanted",
                "backpack_thaumaturge", "backpack_thaumaturge_t1", "backpack_thaumaturge_t2")) {
            assertTrue(language.contains("\"item.magicbees." + item + "\""),
                    "Missing item translation: " + item);
        }
        for (String species : THAUMIC_SPECIES) {
            assertTrue(language.contains("\"allele.forestry.bee_species.magicbees." + species + "\""),
                "Missing Thaumic species translation: " + species);
        }
    }

    @Test
    void everyRawTranslatableKeyUsedByCodeHasEnglishTranslation() throws IOException {
        JsonObject language = JsonParser.parseString(resource("assets/magicbees/lang/en_us.json")).getAsJsonObject();
        Set<String> required = new TreeSet<>();
        try (var files = Files.walk(Path.of("src/main/java"))) {
            for (Path file : files.filter(path -> path.toString().endsWith(".java")).toList()) {
                String source = Files.readString(file);
                int searchFrom = 0;
                while (true) {
                    int call = source.indexOf("Component.translatable(", searchFrom);
                    if (call < 0) {
                        break;
                    }
                    int statementEnd = source.indexOf(';', call);
                    if (statementEnd < 0) {
                        statementEnd = Math.min(source.length(), call + 500);
                    }
                    Matcher literals = JAVA_STRING_LITERAL.matcher(source.substring(call, statementEnd));
                    while (literals.find()) {
                        String key = literals.group(1);
                        if (TRANSLATION_KEY_SHAPE.matcher(key).matches()) {
                            required.add(key);
                        }
                    }
                    searchFrom = call + "Component.translatable(".length();
                }
            }
        }

        for (int phase = 0; phase < 8; phase++) {
            required.add("magicbees.moon_phase." + phase);
        }
        for (String modifier : List.of("production", "genetic_decay", "mutation", "lifespan", "territory", "flowering")) {
            required.add("magicbees.frame.modifier." + modifier);
        }
        for (String key : List.of("hive.curious", "hive.unusual", "hive.resonant", "hive.deep",
                "hive.infernal", "hive.oblivion", "aromatic_lump")) {
            required.add("magicbees.jei.description." + key);
        }
        for (Path speciesRoot : List.of(
                Path.of("src/main/resources/data/magicbees/bee_species"),
                Path.of("src/generated/resources/data/magicbees/bee_species"))) {
            if (!Files.isDirectory(speciesRoot)) {
                continue;
            }
            try (var files = Files.list(speciesRoot)) {
                for (Path file : files.filter(path -> path.toString().endsWith(".json")).toList()) {
                JsonObject json = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
                if (!json.has("genome")) {
                    continue;
                }
                JsonObject genome = json.getAsJsonObject("genome");
                if (genome.has("forestry:flower_type")) {
                    String flowerType = genome.getAsJsonObject("forestry:flower_type").get("value").getAsString();
                    if (flowerType.startsWith("magicbees:")) {
                        String path = flowerType.substring("magicbees:".length());
                        required.add("allele.forestry.flower_type.magicbees." + path);
                        required.add("allele.forestry.flower_type." + path);
                        required.add("magicbees." + path);
                    }
                }
                if (genome.has("forestry:bee_effect")) {
                    String effect = genome.getAsJsonObject("forestry:bee_effect").get("value").getAsString();
                    if (effect.startsWith("magicbees:")) {
                        String path = effect.substring("magicbees:".length());
                        required.add("allele.forestry.bee_effect.magicbees." + path);
                        required.add("magicbees." + path);
                    }
                }
            }
            }
        }

        Set<String> missing = new TreeSet<>();
        for (String key : required) {
            if (!language.has(key)) {
                missing.add(key);
            }
        }
        assertTrue(missing.isEmpty(), "Missing English translations for raw code keys: " + missing);
    }

    @Test
    void customFlowerTypesUsedBySpeciesUseAnalyzerVisibleTags() throws IOException {
        Set<String> flowerTypes = magicBeesFlowerTypesUsedBySpecies();
        assertTrue(!flowerTypes.isEmpty(), "No custom Magic Bees flower types were found in species data");

        String taxonomy = Files.readString(Path.of("src/main/java/magicbees/forestry/MagicBeeTaxa.java"));
        String thaumaturge = Files.readString(Path.of("src/main/java/magicbees/integration/thaumaturge/ThaumaturgeIntegration.java"));
        String combinedSource = taxonomy + "\n" + thaumaturge;
        assertTrue(!combinedSource.contains("BlockFlowerType") && combinedSource.contains("TagFlowerType"),
                "Custom Magic Bees flower alleles must be Forestry TagFlowerType-compatible so analyzer hover can show accepted flowers");

        for (String flowerType : flowerTypes) {
            Path tag = Path.of("src/main/resources/data/magicbees/tags/block/" + flowerType + ".json");
            assertTrue(Files.isRegularFile(tag), "Missing analyzer-visible block tag for flower type magicbees:" + flowerType);
            JsonObject json = JsonParser.parseString(Files.readString(tag)).getAsJsonObject();
            assertTrue(json.has("values") && json.getAsJsonArray("values").size() > 0,
                    "Flower type tag must list accepted flowers: " + tag);
        }

        assertTrue(Files.readString(Path.of("src/main/resources/data/magicbees/tags/block/bookshelf_flowers.json"))
                        .contains("\"minecraft:bookshelf\""),
                "Bookshelf flowers tag must expose vanilla bookshelves");
        assertTrue(Files.readString(Path.of("src/main/resources/data/magicbees/tags/block/botanical_flowers.json"))
                        .contains("\"#botania:small_mystical_flowers\"")
                        && Files.readString(Path.of("src/main/resources/data/magicbees/tags/block/botanical_flowers.json"))
                        .contains("\"#botania:tall_mystical_flowers\""),
                "Botanical flowers tag must expose Botania mystical flower tags");
        assertTrue(Files.readString(Path.of("src/main/resources/data/magicbees/tags/block/thaumic_flowers.json"))
                        .contains("\"thaumaturge:shimmerleaf\"")
                        && Files.readString(Path.of("src/main/resources/data/magicbees/tags/block/thaumic_flowers.json"))
                        .contains("\"thaumaturge:cinderpearl\""),
                "Thaumic flower tag must expose Thaumaturge Shimmerleaf and Cinderpearl blocks");
        assertTrue(Files.readString(Path.of("src/main/resources/data/magicbees/tags/block/aura_node_flowers.json"))
                        .contains("\"thaumaturge:vishroom\""),
                "Aura node flower tag must expose the Thaumaturge Vishroom block");
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
            assertTrue(LegacyBeeParityData.CENTRIFUGE.size() == 24,
                    "The exhaustive legacy comb ledger must contain all 24 enabled registered comb types");
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
            assertTrue(language.contains("\"gui.magicbees.effectjar.health_bar\"")
                            && language.contains("\"gui.magicbees.effectjar.health_bar.value\"")
                            && language.contains("\"gui.magicbees.effectjar.health_bar.time_left\"")
                            && language.contains("\"gui.magicbees.effectjar.status.paused\"")
                            && language.contains("\"gui.magicbees.effectjar.inserted\"")
                            && language.contains("\"gui.magicbees.effectjar.showing_effect\"")
                            && language.contains("\"gui.magicbees.effectjar.ledger\"")
                            && language.contains("\"gui.magicbees.effectjar.ledger.effect\"")
                            && language.contains("\"tooltip.magicbees.effectjar\"")
                            && language.contains("\"gui.magicbees.effectjar.info\"")
                            && language.contains("\"gui.magicbees.effectjar.info.text\"")
                            && language.contains("\"gui.magicbees.effectjar.status.paused_short\"")
                            && language.contains("\"allele.forestry.bee_effect.magicbees.spawn_bat\"")
                            && language.contains("\"allele.forestry.bee_effect.magicbees.effect_dreaming\""),
                    "Effect Jar explanatory GUI translations are missing");
            for (String key : List.of("hive.curious", "hive.unusual", "hive.resonant", "hive.deep",
                    "hive.infernal", "hive.oblivion", "aromatic_lump")) {
                assertTrue(language.contains("\"magicbees.jei.description." + key + "\""),
                        "Missing legacy JEI description translation: " + key);
            }
            String jeiPlugin = Files.readString(Path.of("src/main/java/magicbees/integration/jei/MagicBeesJeiPlugin.java"));
            assertTrue(jeiPlugin.contains("registerVanillaCategoryExtensions")
                            && jeiPlugin.contains("ScornfulOblivionRecipe.class")
                            && jeiPlugin.contains("craftingGridHelper.createAndSetInputs")
                            && jeiPlugin.contains("craftingGridHelper.createAndSetOutputs")
                            && jeiPlugin.contains("essence_scornful_oblivion"),
                    "Scornful Oblivion's special crafting recipe must have an explicit vanilla-style JEI crafting display");
            String jarScreen = Files.readString(Path.of("src/main/java/magicbees/client/screen/EffectJarScreen.java"));
            assertTrue(jarScreen.contains("extends GuiForestry<EffectJarMenu>")
                            && jarScreen.contains("addErrorLedger(menu.jar())")
                            && jarScreen.contains("addClimateLedger(menu.jar())")
                            && jarScreen.contains("ContainedBeeLedger")
                            && jarScreen.contains("EffectJarInfoLedger")
                            && jarScreen.contains("MagicBeesBlocks.EFFECT_JAR")
                            && jarScreen.contains("drawDimmedEmptyBeeIcon")
                            && jarScreen.contains("ForestryBeeSpecies.FOREST")
                            && jarScreen.contains("0xAA808080")
                            && jarScreen.contains("RenderSystem.enableBlend()")
                            && jarScreen.contains("BACKGROUND")
                            && jarScreen.contains("BAR_U = 178")
                            && jarScreen.contains("gui.magicbees.effectjar.health_bar.time_left")
                            && jarScreen.contains("displayBee() == null")
                            && jarScreen.contains("gui.magicbees.effectjar.info.text")
                            && jarScreen.contains("gui.magicbees.effectjar.status.paused")
                            && jarScreen.contains("gui.magicbees.effectjar.ledger.effect"),
                    "Effect Jar screen must preserve the legacy texture and expose Forestry ledgers for jar state");
            assertTrue(!jarScreen.contains("effectjar_bg.png"),
                    "Effect Jar screen must use the single jarscreen.png texture atlas, not a separate placeholder background");
            assertTrue(!jarScreen.contains("markerY") && !jarScreen.contains("0xAA000000"),
                    "Effect Jar health bar must not draw a separate moving age marker over the health fill");
            assertTrue(!jarScreen.contains("drawVanillaPanel"),
                    "Effect Jar screen must not inflate the compact legacy GUI with an oversized custom panel");
            String jarRenderer = Files.readString(Path.of("src/main/java/magicbees/client/renderer/EffectJarRenderer.java"));
            assertTrue(!jarRenderer.contains("Minecraft.useFancyGraphics()"),
                    "Effect Jar bee rendering must not disappear when global fancy graphics is disabled");
            assertTrue(jarRenderer.indexOf("getQueenStack()") < jarRenderer.indexOf("getVisibleStack()"),
                    "Effect Jar renderer must display the active hidden Queen before queued visible Drones");
            assertTrue(jarRenderer.contains("BeeLifeStage.QUEEN") && !jarRenderer.contains("bee.createStack(BeeLifeStage.DRONE)"),
                    "Effect Jar in-world renderer must display the active hidden Queen with a Queen sprite, not a Drone sprite");
            assertTrue(jarScreen.contains("BeeLifeStage.QUEEN") && !jarScreen.contains("createStack(queen, forestry.api.apiculture.genetics.BeeLifeStage.DRONE)"),
                    "Effect Jar GUI ledger must display the active hidden Queen with a Queen sprite, not a Drone sprite");
            assertTrue(!jarScreen.contains("gui.magicbees.effectjar.health_bar.time_left\", formatTicks(menu.ticksUntilDeath()))\n            ), mouseX, mouseY)"),
                    "Effect Jar health-bar tooltip must not show time left when no bee is contained");
            String jarBlockSource = Files.readString(Path.of("src/main/java/magicbees/block/EffectJarBlock.java"));
            assertTrue(jarBlockSource.contains("tooltip.magicbees.effectjar"),
                    "Effect Jar item tooltip must explain that drones are inserted to run bee effects");
            String thaumaturgeSource = Files.readString(Path.of("src/main/java/magicbees/integration/thaumaturge/ThaumaturgeIntegration.java"));
            assertTrue(thaumaturgeSource.contains("MAX_NEARBY_WISPS = 2")
                            && thaumaturgeSource.contains("ThrottledBeeEffect.getBounding(housing, genome)"),
                    "Wispy Effect Jar must preserve its legacy throttle/chance while capping nearby spawned Wisps");
            String spawnMobSource = Files.readString(Path.of("src/main/java/magicbees/forestry/effect/SpawnMobBeeEffect.java"));
            assertTrue(spawnMobSource.contains("mob.getClass()") && !spawnMobSource.contains("getBaseClass()")
                            && spawnMobSource.contains("angryOnPlayers") && spawnMobSource.contains("mob::setTarget")
                            && spawnMobSource.contains("spawnsAboveHousing") && spawnMobSource.contains("random.nextInt(3) - 1")
                            && spawnMobSource.contains("level.noCollision(mob)") && spawnMobSource.contains("attempt < 12"),
                    "Spawn-mob effects must cap against the concrete spawned entity class, use jar-local spawn positions, and require collision space");
            String hiveacynthSource = Files.readString(Path.of("src/main/java/magicbees/integration/botania/blockentity/HiveacynthBlockEntity.java"));
            assertTrue(hiveacynthSource.contains("p.getX()-RANGE+level.random.nextInt(RANGE*2+1),p.getY()+1,p.getZ()-RANGE+level.random.nextInt(RANGE*2+1)"),
                    "Hiveacynth output item spawn position must match the 1.12 random range");
            String hibeescusSource = Files.readString(Path.of("src/main/java/magicbees/integration/botania/blockentity/HibeescusBlockEntity.java"));
            assertTrue(hibeescusSource.contains("p.getX()-RANGE+level.random.nextInt((int)(RANGE*2+1))"),
                    "Hibeescus output item spawn position must match the 1.12 random range");
        }

        @Test
        void finalOptionalDependencyPackagingIsDeterministic() throws IOException {
            Map<String, String> optionalMagicBeesRecipeReferences = Map.ofEntries(
                    Map.entry("magicbees:bee_comb_te_", "thermal_foundation"),
                    Map.entry("magicbees:drop_destabilized", "thermal_foundation"),
                    Map.entry("magicbees:drop_carbon", "thermal_foundation"),
                    Map.entry("magicbees:drop_lux", "thermal_foundation"),
                    Map.entry("magicbees:bee_comb_tc_", "thaumaturge"),
                    Map.entry("magicbees:propolis_air", "thaumaturge"),
                    Map.entry("magicbees:propolis_fire", "thaumaturge"),
                    Map.entry("magicbees:propolis_water", "thaumaturge"),
                    Map.entry("magicbees:propolis_earth", "thaumaturge"),
                    Map.entry("magicbees:propolis_order", "thaumaturge"),
                    Map.entry("magicbees:propolis_entropy", "thaumaturge"),
                    Map.entry("magicbees:resource_tc_", "thaumaturge"),
                    Map.entry("magicbees:backpack_thaumaturge", "thaumaturge"),
                    Map.entry("magicbees:manasteel", "botania"),
                    Map.entry("magicbees:beegonia", "botania"),
                    Map.entry("magicbees:hiveacynth", "botania"),
                    Map.entry("magicbees:hibeescus", "botania")
            );
            try (var files = Files.walk(Path.of("src/main/resources/data/magicbees/recipe"))) {
                for (Path file : files.filter(path -> path.toString().endsWith(".json")).toList()) {
                    String json = Files.readString(file);
                    for (Map.Entry<String, String> guardedReference : optionalMagicBeesRecipeReferences.entrySet()) {
                        if (json.contains(guardedReference.getKey())) {
                            assertTrue(hasModGuard(json, guardedReference.getValue()),
                                    "Recipe references optional Magic Bees content without its mod guard: "
                                            + file + " -> " + guardedReference);
                        }
                    }
                }
            }

            for (String species : NATIVE_SPECIES) {
                String json = resource("data/magicbees/bee_species/" + species + ".json");
                for (String namespace : List.of("thermal:", "ae2:", "botania:", "railcraft:", "thaumaturge:")) {
                    assertTrue(!json.contains("\"item\": \"" + namespace),
                            "Native species " + species + " bakes an optional-mod item specialty: " + namespace);
                }
            }
            for (String disabled : List.of(
                    "data/magicbees/bee_species/te_winsome.json",
                    "data/magicbees/bee_species/te_endearing.json",
                    "data/magicbees/recipe/bee_mutation/te_winsome.json",
                    "data/magicbees/recipe/bee_mutation/te_endearing.json",
                    "data/magicbees/recipe/centrifuge/te_endearing_comb.json",
                    "data/magicbees/recipe/thermal/crucible_endearing_drop.json")) {
                assertTrue(getClass().getClassLoader().getResource(disabled) == null,
                        "Disabled platinum-dependent Thermal line is still packaged: " + disabled);
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

    private static boolean hasModGuard(String json, String modId) {
        return json.contains("\"modid\": \"" + modId + "\"") || json.contains("\"modid\":\"" + modId + "\"");
    }

    private static Set<String> magicBeesFlowerTypesUsedBySpecies() throws IOException {
        Set<String> flowerTypes = new TreeSet<>();
        for (Path speciesRoot : List.of(
                Path.of("src/main/resources/data/magicbees/bee_species"),
                Path.of("src/generated/resources/data/magicbees/bee_species"))) {
            if (!Files.isDirectory(speciesRoot)) {
                continue;
            }
            try (var files = Files.list(speciesRoot)) {
                for (Path file : files.filter(path -> path.toString().endsWith(".json")).toList()) {
                    JsonObject json = JsonParser.parseString(Files.readString(file)).getAsJsonObject();
                    if (!json.has("genome")) {
                        continue;
                    }
                    JsonObject genome = json.getAsJsonObject("genome");
                    if (!genome.has("forestry:flower_type")) {
                        continue;
                    }
                    String flowerType = genome.getAsJsonObject("forestry:flower_type").get("value").getAsString();
                    if (flowerType.startsWith("magicbees:")) {
                        flowerTypes.add(flowerType.substring("magicbees:".length()));
                    }
                }
            }
        }
        return flowerTypes;
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

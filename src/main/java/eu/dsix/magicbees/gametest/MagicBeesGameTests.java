package eu.dsix.magicbees.gametest;

import forestry.api.IForestryApi;
import forestry.api.apiculture.ForestryActivityTypes;
import forestry.api.apiculture.ForestryBeeEffects;
import forestry.api.apiculture.ForestryFlowerTypes;
import forestry.api.core.HumidityType;
import forestry.api.core.ForestryError;
import forestry.api.core.IProduct;
import forestry.api.core.TemperatureType;
import forestry.api.core.machines.ICarpenterRecipe;
import forestry.api.core.machines.ICentrifugeRecipe;
import forestry.api.apiculture.genetics.IBeeSpecies;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.api.core.genetics.capability.IIndividualHandlerItem;
import forestry.api.core.genetics.alleles.Allele;
import forestry.api.core.genetics.alleles.BeeChromosomes;
import forestry.api.core.genetics.alleles.IChromosome;
import forestry.api.core.genetics.alleles.ForestryAlleles;
import forestry.api.core.genetics.IGenome;
import forestry.api.core.genetics.IMutation;
import forestry.api.core.genetics.alleles.AllelePair;
import forestry.apiculture.plugin.ApicultureRegistration;
import forestry.core.engine.genetics.flowers.TagFlowerType;
import forestry.core.platform.util.SpeciesUtil;
import eu.dsix.magicbees.MagicBees;
import eu.dsix.magicbees.registry.MagicBeesBlocks;
import eu.dsix.magicbees.registry.MagicBeesItems;
import eu.dsix.magicbees.block.entity.EffectJarBlockEntity;
import eu.dsix.magicbees.item.MagnetState;
import eu.dsix.magicbees.item.MysteriousMagnetItem;
import eu.dsix.magicbees.menu.EffectJarMenu;
import eu.dsix.magicbees.recipe.MagnetUpgradeRecipe;
import eu.dsix.magicbees.recipe.ScornfulOblivionRecipe;
import eu.dsix.magicbees.forestry.MagicBeeSpecies;
import eu.dsix.magicbees.forestry.BeeSpeciesRuntimePatcher;
import eu.dsix.magicbees.forestry.effect.SpawnMobBeeEffect;
import eu.dsix.magicbees.forestry.effect.WorldTransformBeeEffect;
import eu.dsix.magicbees.forestry.mutation.MoonPhaseMutationCondition;
import eu.dsix.magicbees.forestry.mutation.RequiredBlockTagMutationCondition;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.GameType;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/** Regression tests for registered Magic Bees content, assets, localization, and live Forestry data. */
@GameTestHolder(MagicBees.MOD_ID)
@PrefixGameTestTemplate(false)
public final class MagicBeesGameTests {
    private MagicBeesGameTests() {
    }

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
    private static final Set<ResourceLocation> EXPECTED_NATIVE_SPECIES = NATIVE_SPECIES.stream()
            .map(MagicBees::id)
            .collect(java.util.stream.Collectors.toUnmodifiableSet());
    private static final Set<ResourceLocation> EXPECTED_THAUMIC_SPECIES = List.of(
            "tc_air", "tc_fire", "tc_water", "tc_earth", "tc_order", "tc_entropy",
            "tc_vis", "tc_rejuvenating", "tc_empowering", "tc_nexus", "tc_taint", "tc_pure",
            "tc_hungry", "tc_void", "tc_wispy"
    ).stream().map(MagicBees::id).collect(java.util.stream.Collectors.toUnmodifiableSet());
    private static final Set<ResourceLocation> EXPECTED_THERMAL_SPECIES = List.of(
            "te_blizzy", "te_gelid", "te_dante", "te_pyro", "te_shocking", "te_amped", "te_grounded", "te_rocking",
            "te_coal", "te_destabilized", "te_lux"
    ).stream().map(MagicBees::id).collect(java.util.stream.Collectors.toUnmodifiableSet());
    private static final Set<ResourceLocation> EXPECTED_BOTANIA_SPECIES = List.of(
            "bot_rooted", "bot_botanic", "bot_blossom", "bot_floral",
            "bot_vazbee", "bot_somnolent", "bot_dreaming", "bot_alfheim"
    ).stream().map(MagicBees::id).collect(java.util.stream.Collectors.toUnmodifiableSet());
    private static final ResourceLocation AE_SKYSTONE = MagicBees.id("ae_skystone");

    private static Set<ResourceLocation> expectedLiveNativeSpecies() {
        Set<ResourceLocation> expected = new HashSet<>(EXPECTED_NATIVE_SPECIES);
        if (!ModList.get().isLoaded("thermal_foundation")) {
            expected.removeAll(EXPECTED_THERMAL_SPECIES);
        }
        if (!ModList.get().isLoaded("botania")) {
            expected.removeAll(EXPECTED_BOTANIA_SPECIES);
        }
        if (!ModList.get().isLoaded("ae2")) {
            expected.remove(AE_SKYSTONE);
        }
        expected.removeIf(id -> !BeeSpeciesRuntimePatcher.resourceSpeciesAvailable(id.getPath()));
        return expected;
    }

    private static Set<ResourceLocation> expectedLiveMagicBeesSpecies() {
        Set<ResourceLocation> expected = expectedLiveNativeSpecies();
        if (ModList.get().isLoaded("thaumaturge")) {
            expected.addAll(EXPECTED_THAUMIC_SPECIES);
        }
        return expected;
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void registeredItemsHaveModelsAndTranslations(GameTestHelper helper) {
        String language = readResource(helper, "assets/magicbees/lang/en_us.json");
        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
            if (!MagicBees.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            String translationPrefix = item instanceof net.minecraft.world.item.BlockItem ? "block" : "item";
            helper.assertTrue(language.contains("\"" + translationPrefix + ".magicbees." + id.getPath() + "\""),
                    "Missing modern translation key for " + id);
            helper.assertTrue(hasResource(helper, "assets/magicbees/models/item/" + id.getPath() + ".json"),
                    "Missing item model for " + id);
        }
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void nativeSpeciesDatapackProjectionMatchesJavaBuilders(GameTestHelper helper) {
        Set<ResourceLocation> liveMagicBees = new HashSet<>();
        for (IBeeSpecies species : SpeciesUtil.getAllBeeSpecies()) {
            if (MagicBees.MOD_ID.equals(species.id().getNamespace())) {
                liveMagicBees.add(species.id());
            }
        }

        Set<ResourceLocation> expectedLiveNative = expectedLiveNativeSpecies();
        Set<ResourceLocation> expectedCurrentMagicBees = expectedLiveMagicBeesSpecies();

        Set<ResourceLocation> missing = new HashSet<>(expectedCurrentMagicBees);
        missing.removeAll(liveMagicBees);
        helper.assertTrue(missing.isEmpty(), "Expected active species missing after datapack reload: " + missing);

        Set<ResourceLocation> unexpected = new HashSet<>(liveMagicBees);
        unexpected.removeAll(expectedCurrentMagicBees);
        helper.assertTrue(unexpected.isEmpty(), "Unexpected Magic Bees live species for current optional-mod matrix: " + unexpected);

        Set<ResourceLocation> inactiveNative = new HashSet<>(EXPECTED_NATIVE_SPECIES);
        inactiveNative.removeAll(expectedLiveNative);
        inactiveNative.retainAll(liveMagicBees);
        helper.assertTrue(inactiveNative.isEmpty(),
                "Legacy removeUnneededBees=true species remained live without their defining mod: " + inactiveNative);

        if (!ModList.get().isLoaded("thaumaturge")) {
            Set<ResourceLocation> inactiveThaumic = new HashSet<>(EXPECTED_THAUMIC_SPECIES);
            inactiveThaumic.retainAll(liveMagicBees);
            helper.assertTrue(inactiveThaumic.isEmpty(),
                    "Thaumic species remained live without Thaumaturge: " + inactiveThaumic);
        }

        helper.assertTrue(liveMagicBees.size() == expectedCurrentMagicBees.size(),
                "Expected " + expectedCurrentMagicBees.size() + " live Magic Bees species for current optional-mod matrix, found "
                        + liveMagicBees.size());

        ApicultureRegistration registration = new ApicultureRegistration(SpeciesUtil.BEE_TYPE.get());
        MagicBeeSpecies.registerInitial(registration);
        Map<ResourceLocation, IBeeSpecies> codeBuilt = registration.buildAll();
        helper.assertTrue(codeBuilt.keySet().equals(EXPECTED_NATIVE_SPECIES),
                "Java builder set does not equal the complete 95-definition native ledger: " + codeBuilt.keySet());

        for (ResourceLocation id : expectedLiveNative) {
            IBeeSpecies expected = codeBuilt.get(id);
            IBeeSpecies actual = SpeciesUtil.getBeeSpecies(id);
            helper.assertTrue(actual != null, "Expected active generated species is not live: " + id);
            assertSpeciesEquivalent(helper, expected, actual);
        }
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void liveMagicBeesSpeciesHaveLocalizedPresentation(GameTestHelper helper) {
        Map<ResourceLocation, int[]> legacyColors = Map.ofEntries(
                Map.entry(MagicBees.id("mystical"), new int[]{0xAFFFB7, 0xFF7C26}),
                Map.entry(MagicBees.id("sorcerous"), new int[]{0xEA9A9A, 0xFF7C26}),
                Map.entry(MagicBees.id("unusual"), new int[]{0x72D361, 0xFF7C26}),
                Map.entry(MagicBees.id("attuned"), new int[]{0x0086A8, 0xFF7C26}),
                Map.entry(MagicBees.id("eldritch"), new int[]{0x8D75A0, 0xFF7C26}),
                Map.entry(MagicBees.id("esoteric"), new int[]{0x001099, 0xFF9D60}),
                Map.entry(MagicBees.id("mysterious"), new int[]{0x762BC2, 0xFF9D60}),
                Map.entry(MagicBees.id("arcane"), new int[]{0xD242DF, 0xFF9D60}),
                Map.entry(MagicBees.id("charmed"), new int[]{0x48EEEC, 0xFF9D60}),
                Map.entry(MagicBees.id("enchanted"), new int[]{0x18E726, 0xFF9D60}),
                Map.entry(MagicBees.id("supernatural"), new int[]{0x005614, 0xFF9D60}),
                Map.entry(MagicBees.id("ghastly"), new int[]{0xCCCCEE, 0xBF877C}),
                Map.entry(MagicBees.id("iron"), new int[]{0x686868, 0xE9E9E9}),
                Map.entry(MagicBees.id("aware"), new int[]{0x5E95B5, 0xFF7C26}),
                Map.entry(MagicBees.id("spirit"), new int[]{0xB2964B, 0xFF7C26}),
                Map.entry(MagicBees.id("soul"), new int[]{0x7D591B, 0xFF7C26}),
                Map.entry(MagicBees.id("skulking"), new int[]{0x524827, 0xE15236}),
                Map.entry(MagicBees.id("spidery"), new int[]{0x888888, 0x222222}),
                Map.entry(MagicBees.id("smouldering"), new int[]{0xFFC747, 0xEA8344}),
                Map.entry(MagicBees.id("batty"), new int[]{0x5B482B, 0x271B0F}),
                Map.entry(MagicBees.id("hateful"), new int[]{0xDB00DB, 0x960F00}),
                Map.entry(MagicBees.id("brainy"), new int[]{0x83FF70, 0xE15236}),
                Map.entry(MagicBees.id("bigbad"), new int[]{0xA9344B, 0x453536}),
                Map.entry(MagicBees.id("infernal"), new int[]{0xFF1C1C, 0x960F00}),
                Map.entry(MagicBees.id("oblivion"), new int[]{0xD5C3E5, 0xF696FF}),
                Map.entry(MagicBees.id("ethereal"), new int[]{0xBA3B3B, 0xEFF8FF}),
                Map.entry(MagicBees.id("watery"), new int[]{0x313C5E, 0xFF7C26}),
                Map.entry(MagicBees.id("earthy"), new int[]{0x78822D, 0xFF7C26}),
                Map.entry(MagicBees.id("firey"), new int[]{0xD35119, 0xFF7C26}),
                Map.entry(MagicBees.id("windy"), new int[]{0xFFFDBA, 0xFF7C26}),
                Map.entry(MagicBees.id("pupil"), new int[]{0xFFFF00, 0xFF7C26}),
                Map.entry(MagicBees.id("scholarly"), new int[]{0x6E0000, 0xFF7C26}),
                Map.entry(MagicBees.id("savant"), new int[]{0xFFA042, 0xFF7C26}),
                Map.entry(MagicBees.id("tc_air"), new int[]{0xD9D636, 0x999999}),
                Map.entry(MagicBees.id("tc_fire"), new int[]{0xE50B0B, 0x999999}),
                Map.entry(MagicBees.id("tc_water"), new int[]{0x36CFD9, 0x999999}),
                Map.entry(MagicBees.id("tc_earth"), new int[]{0x005100, 0x999999}),
                Map.entry(MagicBees.id("tc_order"), new int[]{0xAA32FC, 0x999999}),
                Map.entry(MagicBees.id("tc_entropy"), new int[]{0xCCCCCC, 0x999999}),
                Map.entry(MagicBees.id("tc_vis"), new int[]{0x004C99, 0x675ED1}),
                Map.entry(MagicBees.id("tc_rejuvenating"), new int[]{0x91D0D9, 0x675ED1}),
                Map.entry(MagicBees.id("tc_empowering"), new int[]{0x96FFBC, 0x675ED1}),
                Map.entry(MagicBees.id("tc_nexus"), new int[]{0x15AFAF, 0x675ED1}),
                Map.entry(MagicBees.id("tc_taint"), new int[]{0x91376A, 0x675ED1}),
                Map.entry(MagicBees.id("tc_pure"), new int[]{0xE23F65, 0x675ED1}),
                Map.entry(MagicBees.id("tc_hungry"), new int[]{0xDCA5E2, 0x675ED1}),
                Map.entry(MagicBees.id("tc_void"), new int[]{0x180A29, 0x4B2A74}),
                Map.entry(MagicBees.id("tc_wispy"), new int[]{0x9CB8D5, 0xE15236}),
                Map.entry(MagicBees.id("te_blizzy"), new int[]{0x0073C4, 0xFF7C26}),
                Map.entry(MagicBees.id("te_gelid"), new int[]{0x4AAFF7, 0xFF7C26}),
                Map.entry(MagicBees.id("te_dante"), new int[]{0xF7AC4A, 0xFF7C26}),
                Map.entry(MagicBees.id("te_pyro"), new int[]{0xFA930C, 0xFF7C26}),
                Map.entry(MagicBees.id("te_shocking"), new int[]{0xC5FF26, 0xF8EE00}),
                Map.entry(MagicBees.id("te_amped"), new int[]{0x8AFFFF, 0xECE670}),
                Map.entry(MagicBees.id("te_grounded"), new int[]{0xCEC1C1, 0x826767}),
                Map.entry(MagicBees.id("te_rocking"), new int[]{0x980000, 0xAB9D9B}),
                Map.entry(MagicBees.id("te_coal"), new int[]{0x2E2D2D, 0xFF7C26}),
                Map.entry(MagicBees.id("te_destabilized"), new int[]{0x5E0203, 0xFF7C26}),
                Map.entry(MagicBees.id("te_lux"), new int[]{0xF1FA89, 0xFF7C26}),
                Map.entry(MagicBees.id("bot_rooted"), new int[]{0x00A800, 0xFFB2BB}),
                Map.entry(MagicBees.id("bot_botanic"), new int[]{0x94C661, 0xFFB2BB}),
                Map.entry(MagicBees.id("bot_blossom"), new int[]{0xA4C193, 0xFFB2BB}),
                Map.entry(MagicBees.id("bot_floral"), new int[]{0x29D81A, 0xFFB2BB}),
                Map.entry(MagicBees.id("bot_vazbee"), new int[]{0xFF6B9C, 0xFFB2BB}),
                Map.entry(MagicBees.id("bot_somnolent"), new int[]{0x2978C6, 0xFFB2BB}),
                Map.entry(MagicBees.id("bot_dreaming"), new int[]{0x123456, 0xFFB2BB}),
                Map.entry(MagicBees.id("bot_alfheim"), new int[]{0xFFFFFF, 0xFFFFFF}),
                Map.entry(MagicBees.id("ae_skystone"), new int[]{0x4B8381, 0x252929})
        );

        String language = readResource(helper, "assets/magicbees/lang/en_us.json");
        int count = 0;
        int colorChecked = 0;
        for (var species : SpeciesUtil.getAllBeeSpecies()) {
            if (!MagicBees.MOD_ID.equals(species.id().getNamespace())) {
                continue;
            }
            count++;
            helper.assertTrue(species.getOutline() != -1, "Species has no outline tint: " + species.id());
            helper.assertTrue(species.getBody() != 0, "Species has no body tint: " + species.id());
            helper.assertTrue("Elec332".equals(species.getAuthority()),
                    "Wrong legacy authority for " + species.id() + ": " + species.getAuthority());
            int[] expectedColors = legacyColors.get(species.id());
            if (expectedColors != null) {
                colorChecked++;
                helper.assertTrue(species.getOutline() == expectedColors[0],
                        "Wrong legacy outline tint for " + species.id() + ": 0x"
                                + Integer.toHexString(species.getOutline()));
                helper.assertTrue(species.getBody() == expectedColors[1],
                        "Wrong legacy body tint for " + species.id() + ": 0x"
                                + Integer.toHexString(species.getBody()));
            }
            helper.assertTrue(language.contains("\"allele.forestry.bee_species.magicbees." + species.id().getPath() + "\""),
                    "Missing Forestry species translation key for " + species.id());
        }
        helper.assertTrue(count > 0, "No Magic Bees species reached the live Forestry species registry");
        Set<ResourceLocation> expectedLiveColors = new HashSet<>(legacyColors.keySet());
        expectedLiveColors.retainAll(expectedLiveMagicBeesSpecies());
        helper.assertTrue(colorChecked == expectedLiveColors.size(),
                "Not all currently live legacy species colors were checked: " + colorChecked + "/" + expectedLiveColors.size());
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void legacyAlwaysSecretSpeciesRemainSecret(GameTestHelper helper) {
        for (String path : List.of("skulking", "ghastly", "spidery", "smouldering", "brainy")) {
            helper.assertTrue(requireSpecies(helper, path).isSecret(),
                    "Legacy always-secret species became visible: magicbees:" + path);
        }
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void liveMagicBeesSpeciesHaveValidOutputs(GameTestHelper helper) {
        int speciesCount = 0;
        for (var species : SpeciesUtil.getAllBeeSpecies()) {
            if (!MagicBees.MOD_ID.equals(species.id().getNamespace())) {
                continue;
            }
            speciesCount++;
            int productCount = species.getProducts().size() + species.getSpecialties().size();
            helper.assertTrue(productCount > 0, "Live species has no output or explicit no-output policy: " + species.id());
            species.getProducts().forEach(product -> assertProduct(helper, species.id(), product));
            species.getSpecialties().forEach(product -> assertProduct(helper, species.id(), product));
        }
        helper.assertTrue(speciesCount >= 20, "Expected native plus Thaumic live species, found " + speciesCount);
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void everyLiveSpeciesHasACompleteReachableAcquisitionPath(GameTestHelper helper) {
        Map<ResourceLocation, IBeeSpecies> live = new HashMap<>();
        for (IBeeSpecies species : SpeciesUtil.getAllBeeSpecies()) {
            if (MagicBees.MOD_ID.equals(species.id().getNamespace())) {
                live.put(species.id(), species);
            }
        }
        helper.assertTrue(live.keySet().equals(expectedLiveMagicBeesSpecies()),
                "Acquisition audit did not receive the exact live species projection: " + live.keySet());

        var mutations = IForestryApi.INSTANCE.getGeneticManager().getMutations(SpeciesUtil.BEE_TYPE.get());
        Set<ResourceLocation> reachable = new HashSet<>();

        for (Map.Entry<String, LegacyBeeParityData.Acquisition> entry
                : LegacyBeeParityData.NON_BREEDING_ACQUISITIONS.entrySet()) {
            ResourceLocation id = MagicBees.id(entry.getKey());
            if (!live.containsKey(id)) {
                continue;
            }
            if (entry.getValue() == LegacyBeeParityData.Acquisition.NATURAL_HIVE) {
                helper.assertTrue(!IForestryApi.INSTANCE.getHiveManager().getDrops(id).isEmpty(),
                        "Natural-only species has no live hive acquisition: " + id);
                reachable.add(id);
            } else {
                assertBotaniaConversion(helper, entry.getKey());
            }
        }

        for (IBeeSpecies species : live.values()) {
            if (LegacyBeeParityData.NON_BREEDING_ACQUISITIONS.containsKey(species.id().getPath())) {
                continue;
            }
            var into = mutations.getMutationsInto(species);
            helper.assertTrue(!into.isEmpty(), "Breeding-only species has no decoded mutation: " + species.id());
            boolean hasLiveParents = false;
            for (IMutation<?> mutation : into) {
                helper.assertTrue(mutation.getChance() > 0.0f && mutation.getChance() <= 1.0f,
                        "Mutation chance is not breedable for " + species.id() + ": " + mutation.getChance());
                if (mutation.getFirstParent().id().equals(mutation.getSecondParent().id())) {
                    helper.assertTrue(LegacyBeeParityData.LEGACY_SELF_PARENT_BREEDING.contains(species.id().getPath()),
                            "Non-legacy mutation uses the same parent twice for " + species.id());
                }
                boolean firstLive = parentIsLive(live, mutation.getFirstParent());
                boolean secondLive = parentIsLive(live, mutation.getSecondParent());
                hasLiveParents |= firstLive && secondLive;
                helper.assertTrue(firstLive && secondLive,
                        "Mutation for " + species.id() + " references an unavailable parent: "
                                + mutation.getFirstParent().id() + " + " + mutation.getSecondParent().id());
                for (var condition : mutation.getConditions()) {
                    if (condition instanceof RequiredBlockTagMutationCondition blockTag) {
                        helper.assertTrue(BuiltInRegistries.BLOCK.getTag(blockTag.tag())
                                        .map(holders -> holders.stream().findAny().isPresent()).orElse(false),
                                "Mutation for " + species.id() + " requires an empty block tag: " + blockTag.tag().location());
                    }
                }
            }
            helper.assertTrue(hasLiveParents, "No usable live-parent mutation exists for " + species.id());
        }

        boolean changed;
        do {
            changed = false;
            for (IMutation<?> mutation : mutations.getAllMutations()) {
                ResourceLocation result = mutation.getResult().id();
                if (live.containsKey(result)
                        && reachableParent(reachable, mutation.getFirstParent())
                        && reachableParent(reachable, mutation.getSecondParent())) {
                    changed |= reachable.add(result);
                }
            }
            for (Map.Entry<String, LegacyBeeParityData.Conversion> entry
                    : LegacyBeeParityData.BOTANIA_CONVERSIONS.entrySet()) {
                ResourceLocation result = MagicBees.id(entry.getKey());
                ResourceLocation input = MagicBees.id(entry.getValue().inputSpecies());
                if (live.containsKey(result) && reachable.contains(input)) {
                    changed |= reachable.add(result);
                }
            }
        } while (changed);

        Set<ResourceLocation> unreachable = new HashSet<>(live.keySet());
        unreachable.removeAll(reachable);
        helper.assertTrue(unreachable.isEmpty(),
                "Live species are individually defined but unreachable through the complete acquisition graph: " + unreachable);
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void everyRegisteredCombHasExactLegacyFunctionalCentrifugeOutputs(GameTestHelper helper) {
        helper.assertTrue(MagicBeesItems.COMBS.keySet().equals(LegacyBeeParityData.CENTRIFUGE.keySet()),
                "Registered combs and the exhaustive legacy centrifuge ledger differ: " + MagicBeesItems.COMBS.keySet());

        var recipes = helper.getLevel().getRecipeManager();
        Set<ResourceLocation> genericHoney = Set.of(
                ResourceLocation.fromNamespaceAndPath("forestry", "honey_drop"),
                ResourceLocation.fromNamespaceAndPath("forestry", "honeydew"));

        for (Map.Entry<String, LegacyBeeParityData.Centrifuge> entry : LegacyBeeParityData.CENTRIFUGE.entrySet()) {
            String comb = entry.getKey();
            LegacyBeeParityData.Centrifuge expected = entry.getValue();
            ResourceLocation recipeId = MagicBees.id(expected.recipePath());
            var holder = recipes.byKey(recipeId).orElse(null);
            boolean shouldLoad = expected.requiredMod() == null || ModList.get().isLoaded(expected.requiredMod());
            helper.assertTrue((holder != null) == shouldLoad,
                    "Conditional centrifuge recipe load state drifted for " + comb + ": " + recipeId);
            if (!shouldLoad) {
                continue;
            }
            helper.assertTrue(holder.value() instanceof ICentrifugeRecipe,
                    "Recipe is not a decoded Forestry centrifuge recipe: " + recipeId);
            ICentrifugeRecipe actual = (ICentrifugeRecipe) holder.value();
            ItemStack combStack = new ItemStack(MagicBeesItems.comb(comb).get());
            helper.assertTrue(actual.getInput().test(combStack), "Centrifuge input does not accept its exact comb: " + comb);
            helper.assertTrue(actual.getProcessingTime() == 20, "Legacy centrifuge time drifted for " + comb);

            Map<ResourceLocation, Float> expectedProducts = new HashMap<>();
            expected.products().forEach(product -> expectedProducts.put(product.item(), product.chance()));
            Map<ResourceLocation, Float> actualProducts = new HashMap<>();
            for (IProduct product : actual.getAllProducts()) {
                ItemStack stack = product.createStack();
                helper.assertTrue(!stack.isEmpty() && stack.getCount() == 1,
                        "Centrifuge advertises an empty or wrong-count output for " + comb);
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
                helper.assertTrue(actualProducts.put(itemId, product.chance()) == null,
                        "Centrifuge repeats output " + itemId + " for " + comb);
            }
            helper.assertTrue(actualProducts.keySet().equals(expectedProducts.keySet()),
                    "Exact centrifuge output items drifted for " + comb + ": " + actualProducts.keySet());
            expectedProducts.forEach((item, chance) -> helper.assertTrue(
                    Math.abs(actualProducts.get(item) - chance) < 0.000001f,
                    "Exact centrifuge chance drifted for " + comb + " -> " + item + ": " + actualProducts.get(item)));
            helper.assertTrue(actualProducts.keySet().stream().anyMatch(item -> !genericHoney.contains(item)),
                    "Comb has no meaningful non-honey centrifuge output: " + comb);

            Set<ResourceLocation> forcedOutputs = new HashSet<>();
            for (ItemStack stack : actual.getProducts(RandomSource.create(0x4D424545L), 1000.0)) {
                helper.assertTrue(!stack.isEmpty(), "Centrifuge produced an empty runtime stack for " + comb);
                forcedOutputs.add(BuiltInRegistries.ITEM.getKey(stack.getItem()));
            }
            helper.assertTrue(forcedOutputs.equals(expectedProducts.keySet()),
                    "Centrifuge cannot actually produce every declared legacy output for " + comb + ": " + forcedOutputs);
        }

        for (IBeeSpecies species : SpeciesUtil.getAllBeeSpecies()) {
            if (!MagicBees.MOD_ID.equals(species.id().getNamespace())) {
                continue;
            }
            species.getProducts().forEach(product -> assertCombHasLiveCentrifuge(helper, recipes, species.id(), product));
            species.getSpecialties().forEach(product -> assertCombHasLiveCentrifuge(helper, recipes, species.id(), product));
        }
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void customFlowerTypesUsedByMagicBeesAreAnalyzerVisible(GameTestHelper helper) {
        Set<ResourceLocation> flowerTypes = new HashSet<>();
        for (IBeeSpecies species : SpeciesUtil.getAllBeeSpecies()) {
            if (!MagicBees.MOD_ID.equals(species.id().getNamespace())) {
                continue;
            }
            AllelePair<ResourceLocation> flowers = species.getDefaultGenome().getAllelePair(BeeChromosomes.FLOWER_TYPE);
            if (MagicBees.MOD_ID.equals(flowers.active().value().getNamespace())) {
                flowerTypes.add(flowers.active().value());
            }
            if (MagicBees.MOD_ID.equals(flowers.inactive().value().getNamespace())) {
                flowerTypes.add(flowers.inactive().value());
            }
        }

        helper.assertTrue(!flowerTypes.isEmpty(), "No custom Magic Bees flower types were exercised");
        for (ResourceLocation flowerType : flowerTypes) {
            var resolved = IForestryApi.INSTANCE.getFlowerTypeManager().getFlowerType(flowerType);
            helper.assertTrue(resolved instanceof TagFlowerType,
                    flowerType + " must resolve to Forestry TagFlowerType so analyzer hover can show accepted flowers");
            TagFlowerType tagFlowerType = (TagFlowerType) resolved;
            helper.assertTrue(tagFlowerType.acceptableFlowers().location().equals(flowerType),
                    flowerType + " analyzer tag should match its flower type id, got #" + tagFlowerType.acceptableFlowers().location());
        }
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void liveBranchDefaultsSurviveDatapackReload(GameTestHelper helper) {
        var infernal = SpeciesUtil.getBeeSpecies(MagicBees.id("infernal"));
        helper.assertTrue(infernal != null, "Infernal species is not live");
        helper.assertTrue(infernal.getTemperature() == TemperatureType.HELLISH,
                "Infernal lost Abominable HELLISH temperature");
        helper.assertTrue(infernal.getHumidity() == HumidityType.ARID,
                "Infernal lost Abominable ARID humidity");
        var infernalGenome = infernal.getDefaultGenome();
        helper.assertTrue(infernalGenome.getActiveAllele(BeeChromosomes.TEMPERATURE_TOLERANCE)
                        .equals(ForestryAlleles.TOLERANCE_DOWN_2),
                "Infernal lost Abominable DOWN_2 temperature tolerance");
        helper.assertTrue(infernalGenome.getActiveValue(BeeChromosomes.FLOWER_TYPE).equals(ForestryFlowerTypes.NETHER),
                "Infernal lost Abominable Nether flowers");
        helper.assertTrue(infernalGenome.getActiveValue(BeeChromosomes.ACTIVITY).equals(MagicBees.id("activity_never_sleeps")),
                "Infernal lost Abominable never-sleeps activity");
        helper.assertTrue(!infernalGenome.getActiveAllele(BeeChromosomes.ACTIVITY).dominant(),
                "Infernal never-sleeps activity stopped being recessive");
        var infernalActivity = infernalGenome.<forestry.api.apiculture.IActivityType>resolveActive(BeeChromosomes.ACTIVITY);
        helper.assertTrue(infernalActivity.isActive(0, 0, BlockPos.ZERO)
                        && infernalActivity.isActive(0, forestry.api.apiculture.IActivityType.NIGHT_TIME, BlockPos.ZERO),
                "Infernal never-sleeps activity is not active day and night");
        helper.assertTrue(infernalGenome.getActiveValue(BeeChromosomes.CAVE_DWELLING),
                "Infernal lost Abominable cave dwelling");
        helper.assertTrue(infernalGenome.getActiveValue(BeeChromosomes.EFFECT).equals(ForestryBeeEffects.AGGRESSIVE),
                "Infernal lost Abominable aggressive effect");
        helper.assertTrue(infernalGenome.getActiveAllele(BeeChromosomes.LIFESPAN).equals(ForestryAlleles.LIFESPAN_SHORT),
                "Infernal lost Abominable short lifespan");

        var oblivion = SpeciesUtil.getBeeSpecies(MagicBees.id("oblivion"));
        helper.assertTrue(oblivion != null, "Oblivion species is not live");
        helper.assertTrue(oblivion.getTemperature() == TemperatureType.COLD,
                "Oblivion lost Extrinsic COLD temperature");
        var oblivionGenome = oblivion.getDefaultGenome();
        helper.assertTrue(oblivionGenome.getActiveAllele(BeeChromosomes.TEMPERATURE_TOLERANCE)
                        .equals(ForestryAlleles.TOLERANCE_UP_2),
                "Oblivion lost Extrinsic UP_2 temperature tolerance");
        helper.assertTrue(oblivionGenome.getActiveValue(BeeChromosomes.FLOWER_TYPE).equals(ForestryFlowerTypes.END),
                "Oblivion lost Extrinsic End flowers");
        helper.assertTrue(oblivionGenome.getActiveValue(BeeChromosomes.ACTIVITY).equals(MagicBees.id("activity_never_sleeps")),
                "Oblivion lost Extrinsic never-sleeps activity");
        helper.assertTrue(!oblivionGenome.getActiveAllele(BeeChromosomes.ACTIVITY).dominant(),
                "Oblivion never-sleeps activity stopped being recessive");
        helper.assertTrue(oblivionGenome.getActiveValue(BeeChromosomes.CAVE_DWELLING),
                "Oblivion lost Extrinsic cave dwelling");
        helper.assertTrue(oblivionGenome.getActiveValue(BeeChromosomes.EFFECT).equals(ForestryBeeEffects.AGGRESSIVE),
                "Oblivion lost Extrinsic aggressive effect");
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void thaumicSpeciesRetainLegacyGenomesAndOutputs(GameTestHelper helper) {
        if (!ModList.get().isLoaded("thaumaturge")) {
            assertSpeciesAbsent(helper, EXPECTED_THAUMIC_SPECIES, "Thaumaturge");
            helper.succeed();
            return;
        }

        IBeeSpecies air = requireSpecies(helper, "tc_air");
        assertAllele(helper, air, BeeChromosomes.SPEED, ForestryAlleles.SPEED_FASTEST);
        assertAllele(helper, air, BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_SHORTENED);
        assertAllele(helper, air, BeeChromosomes.TERRITORY, ForestryAlleles.TERRITORY_LARGEST);
        assertAllele(helper, air, BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_MAXIMUM);
        assertAllele(helper, air, BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
        assertReference(helper, air, BeeChromosomes.FLOWER_TYPE, MagicBees.id("thaumic_flowers"));
        assertReference(helper, air, BeeChromosomes.EFFECT, MagicBees.id("thaumaturge_air_speed"));
        helper.assertTrue(air.hasGlint(), "tc_air lost legacy setHasEffect/glint");
        assertOutputs(helper, air,
                Map.of("magicbees:bee_comb_tc_air", 0.20f),
                Map.of("magicbees:propolis_air", 0.18f));

        IBeeSpecies fire = requireSpecies(helper, "tc_fire");
        helper.assertTrue(fire.getTemperature() == TemperatureType.HOT, "tc_fire lost HOT climate");
        helper.assertTrue(fire.getHumidity() == HumidityType.ARID, "tc_fire lost ARID climate");
        assertAllele(helper, fire, BeeChromosomes.SPEED, ForestryAlleles.SPEED_FASTER);
        assertAllele(helper, fire, BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_NORMAL);
        assertAllele(helper, fire, BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_DOWN_3);
        assertAllele(helper, fire, BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_UP_2);
        assertAllele(helper, fire, BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
        assertReference(helper, fire, BeeChromosomes.FLOWER_TYPE, MagicBees.id("thaumic_flowers"));
        assertReference(helper, fire, BeeChromosomes.EFFECT, ForestryBeeEffects.IGNITION);
        helper.assertTrue(fire.hasGlint(), "tc_fire lost legacy setHasEffect/glint");
        assertOutputs(helper, fire,
                Map.of("magicbees:bee_comb_tc_fire", 0.20f),
                Map.of("magicbees:propolis_fire", 0.18f));

        IBeeSpecies water = requireSpecies(helper, "tc_water");
        helper.assertTrue(water.getHumidity() == HumidityType.DAMP, "tc_water lost DAMP climate");
        assertAllele(helper, water, BeeChromosomes.SPEED, ForestryAlleles.SPEED_FAST);
        assertAllele(helper, water, BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_LONG);
        assertAllele(helper, water, BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_DOWN_2);
        assertAllele(helper, water, BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
        assertAllele(helper, water, BeeChromosomes.TERRITORY, ForestryAlleles.TERRITORY_LARGE);
        assertReference(helper, water, BeeChromosomes.FLOWER_TYPE, MagicBees.id("thaumic_flowers"));
        assertReference(helper, water, BeeChromosomes.EFFECT, MagicBees.id("thaumaturge_water_cleansing"));
        helper.assertTrue(water.hasGlint(), "tc_water lost legacy setHasEffect/glint");
        assertOutputs(helper, water,
                Map.of("magicbees:bee_comb_tc_water", 0.20f),
                Map.of("magicbees:propolis_water", 0.18f));

        IBeeSpecies earth = requireSpecies(helper, "tc_earth");
        assertAllele(helper, earth, BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOW);
        assertAllele(helper, earth, BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_LONGER);
        assertAllele(helper, earth, BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_DOWN_1);
        assertAllele(helper, earth, BeeChromosomes.TOLERATES_RAIN, ForestryAlleles.TRUE_RECESSIVE);
        assertAllele(helper, earth, BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
        assertAllele(helper, earth, BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_FASTEST);
        assertReference(helper, earth, BeeChromosomes.FLOWER_TYPE, MagicBees.id("thaumic_flowers"));
        assertReference(helper, earth, BeeChromosomes.EFFECT, MagicBees.id("thaumaturge_earth_haste"));
        helper.assertTrue(earth.hasGlint(), "tc_earth lost legacy setHasEffect/glint");
        assertOutputs(helper, earth,
                Map.of("magicbees:bee_comb_tc_earth", 0.20f),
                Map.of("magicbees:propolis_earth", 0.18f));

        IBeeSpecies order = requireSpecies(helper, "tc_order");
        assertAllele(helper, order, BeeChromosomes.SPEED, ForestryAlleles.SPEED_NORMAL);
        assertAllele(helper, order, BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_NORMAL);
        helper.assertTrue(order.hasGlint(), "tc_order lost legacy setHasEffect/glint");
        assertOutputs(helper, order,
                Map.of("magicbees:bee_comb_tc_order", 0.20f),
                Map.of("magicbees:propolis_order", 0.18f));

        IBeeSpecies entropy = requireSpecies(helper, "tc_entropy");
        assertAllele(helper, entropy, BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_2);
        helper.assertTrue(entropy.hasGlint(), "tc_entropy lost legacy setHasEffect/glint");
        assertOutputs(helper, entropy,
                Map.of("magicbees:bee_comb_tc_entropy", 0.20f),
                Map.of("magicbees:propolis_entropy", 0.18f));

        IBeeSpecies vis = requireSpecies(helper, "tc_vis");
        assertAllele(helper, vis, BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
        assertAllele(helper, vis, BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
        assertAllele(helper, vis, BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOW);
        assertAllele(helper, vis, BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_SLOWER);
        assertReference(helper, vis, BeeChromosomes.FLOWER_TYPE, MagicBees.id("aura_node_flowers"));
        assertOutputs(helper, vis, Map.of("magicbees:bee_comb_intellect", 0.10f), Map.of());

        IBeeSpecies rejuvenating = requireSpecies(helper, "tc_rejuvenating");
        assertAllele(helper, rejuvenating, BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
        assertReference(helper, rejuvenating, BeeChromosomes.FLOWER_TYPE, MagicBees.id("aura_node_flowers"));
        assertReference(helper, rejuvenating, BeeChromosomes.EFFECT, MagicBees.id("thaumaturge_rejuvenating"));
        assertOutputs(helper, rejuvenating, Map.of("magicbees:bee_comb_intellect", 0.18f), Map.of());

        IBeeSpecies empowering = requireSpecies(helper, "tc_empowering");
        assertAllele(helper, empowering, BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
        assertAllele(helper, empowering, BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
        assertReference(helper, empowering, BeeChromosomes.FLOWER_TYPE, MagicBees.id("aura_node_flowers"));
        assertReference(helper, empowering, BeeChromosomes.EFFECT, MagicBees.id("thaumaturge_empowering"));
        assertOutputs(helper, empowering,
                Map.of("magicbees:bee_comb_intellect", 0.14f, "forestry:crystalline_pollen_cluster", 0.20f),
                Map.of());

        IBeeSpecies nexus = requireSpecies(helper, "tc_nexus");
        assertAllele(helper, nexus, BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
        assertAllele(helper, nexus, BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
        assertReference(helper, nexus, BeeChromosomes.FLOWER_TYPE, MagicBees.id("aura_node_flowers"));
        assertReference(helper, nexus, BeeChromosomes.EFFECT, MagicBees.id("thaumaturge_nexus"));
        helper.assertTrue(nexus.hasGlint(), "tc_nexus lost legacy setHasEffect/glint");
        assertOutputs(helper, nexus,
                Map.of("magicbees:bee_comb_intellect", 0.25f,
                        "forestry:crystalline_pollen_cluster", 0.20f,
                        "magicbees:bee_comb_temporal", 0.12f),
                Map.of());

        IBeeSpecies taint = requireSpecies(helper, "tc_taint");
        assertReference(helper, taint, BeeChromosomes.FLOWER_TYPE, MagicBees.id("aura_node_flowers"));
        assertReference(helper, taint, BeeChromosomes.EFFECT, MagicBees.id("thaumaturge_taint"));
        assertOutputs(helper, taint,
                Map.of("magicbees:bee_comb_intellect", 0.14f),
                Map.of("magicbees:propolis_unstable", 0.213f));

        IBeeSpecies pure = requireSpecies(helper, "tc_pure");
        assertReference(helper, pure, BeeChromosomes.FLOWER_TYPE, MagicBees.id("aura_node_flowers"));
        assertAllele(helper, pure, BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_AVERAGE);
        assertReference(helper, pure, BeeChromosomes.EFFECT, MagicBees.id("thaumaturge_purifying"));
        assertOutputs(helper, pure,
                Map.of("magicbees:bee_comb_intellect", 0.16f, "magicbees:bee_comb_soul", 0.19f),
                Map.of());

        IBeeSpecies hungry = requireSpecies(helper, "tc_hungry");
        assertReference(helper, hungry, BeeChromosomes.FLOWER_TYPE, MagicBees.id("aura_node_flowers"));
        assertAllele(helper, hungry, BeeChromosomes.TERRITORY, ForestryAlleles.TERRITORY_LARGEST);
        assertReference(helper, hungry, BeeChromosomes.EFFECT, MagicBees.id("thaumaturge_hungry"));
        assertOutputs(helper, hungry,
                Map.of("magicbees:bee_comb_intellect", 0.28f, "magicbees:bee_comb_temporal", 0.195f),
                Map.of());

        IBeeSpecies voidBee = requireSpecies(helper, "tc_void");
        helper.assertTrue("Metalliapis".equals(voidBee.getGenusName()), "tc_void is not in legacy Metalliapis genus");
        helper.assertTrue(voidBee.getTemperature() == TemperatureType.ICY, "tc_void lost ICY climate");
        assertAllele(helper, voidBee, BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOWEST);
        assertAllele(helper, voidBee, BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_1);
        assertReference(helper, voidBee, BeeChromosomes.ACTIVITY, MagicBees.id("activity_never_sleeps"));
        assertAllele(helper, voidBee, BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
        assertAllele(helper, voidBee, BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_UP_1);
        assertReference(helper, voidBee, BeeChromosomes.FLOWER_TYPE, ForestryFlowerTypes.VANILLA);
        assertReference(helper, voidBee, BeeChromosomes.EFFECT, ForestryBeeEffects.GLACIAL);
        assertOutputs(helper, voidBee,
                Map.of("forestry:honey_comb", 0.10f),
                Map.of("thaumaturge:nugget_void", 0.155f));

        IBeeSpecies wispy = requireSpecies(helper, "tc_wispy");
        assertAllele(helper, wispy, BeeChromosomes.SPEED, ForestryAlleles.SPEED_FAST);
        assertAllele(helper, wispy, BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_FASTER);
        assertReference(helper, wispy, BeeChromosomes.ACTIVITY, MagicBees.id("activity_never_sleeps"));
        assertAllele(helper, wispy, BeeChromosomes.TOLERATES_RAIN, ForestryAlleles.TRUE_RECESSIVE);
        assertReference(helper, wispy, BeeChromosomes.FLOWER_TYPE, MagicBees.id("thaumic_flowers"));
        assertReference(helper, wispy, BeeChromosomes.EFFECT, MagicBees.id("thaumaturge_wispy"));
        assertOutputs(helper, wispy,
                Map.of("forestry:silky_comb", 0.22f),
                Map.of("forestry:silk_wisp", 0.40f));

        for (String id : List.of("tc_air", "tc_fire", "tc_water", "tc_earth", "tc_order",
                "tc_vis", "tc_rejuvenating", "tc_empowering", "tc_nexus", "tc_taint",
                "tc_pure", "tc_hungry", "tc_wispy")) {
            assertAllele(helper, requireSpecies(helper, id), BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_1);
        }
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void expandedMutationGraphReloadsWithLegacyConditions(GameTestHelper helper) {
        var manager = IForestryApi.INSTANCE.getGeneticManager().getMutations(SpeciesUtil.BEE_TYPE.get());
        IMutation<?> bigBad = manager.getAllMutations().stream()
                .filter(m -> MagicBees.id("bigbad").equals(m.getResult().id()))
                .findFirst().orElse(null);
        helper.assertTrue(bigBad != null, "Big Bad mutation did not survive datapack reload");
        MoonPhaseMutationCondition moon = bigBad.getConditions().stream()
                .filter(MoonPhaseMutationCondition.class::isInstance)
                .map(MoonPhaseMutationCondition.class::cast).findFirst().orElse(null);
        helper.assertTrue(moon != null, "Big Bad lost its moon restriction");
        helper.assertTrue(moon.insideMultiplier() == 1.0f && moon.outsideMultiplier() == 0.0f,
                "Big Bad moon restriction is inverted after reload");

        helper.assertTrue(manager.getAllMutations().stream().anyMatch(m -> MagicBees.id("iron").equals(m.getResult().id())),
                "Iron mutation missing after reload");
        helper.assertTrue(manager.getAllMutations().stream().anyMatch(m -> MagicBees.id("mutable").equals(m.getResult().id())),
                "Mutable mutation missing after reload");
        helper.assertTrue(manager.getAllMutations().stream().anyMatch(m -> MagicBees.id("draconic").equals(m.getResult().id())),
                "Draconic mutation missing after reload");
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void expandedBeeEffectsPreserveLegacyParametersAndTransforms(GameTestHelper helper) {
        for (var effect : List.of(
                new Object[]{"spawn_wolf", new SpawnMobBeeEffect(EntityType.WOLF, 650, 40, 2)},
                new Object[]{"spawn_bat", new SpawnMobBeeEffect(EntityType.BAT, 150, 100, 5)},
                new Object[]{"spawn_cow", new SpawnMobBeeEffect(EntityType.COW, 640, 100, 3)},
                new Object[]{"spawn_chicken", new SpawnMobBeeEffect(EntityType.CHICKEN, 20, 100, 20)},
                new Object[]{"spawn_pig", new SpawnMobBeeEffect(EntityType.PIG, 350, 100, 4)},
                new Object[]{"spawn_sheep", new SpawnMobBeeEffect(EntityType.SHEEP, 450, 100, 5)},
                new Object[]{"spawn_cat", new SpawnMobBeeEffect(EntityType.CAT, 702, 60, 2)},
                new Object[]{"spawn_horse", new SpawnMobBeeEffect(EntityType.HORSE, 450, 59, 2)},
                new Object[]{"spawn_ghast", new SpawnMobBeeEffect(EntityType.GHAST, 2060, 10, 1)},
                new Object[]{"spawn_spider", new SpawnMobBeeEffect(EntityType.SPIDER, 400, 70, 4)},
                new Object[]{"spawn_blaze", new SpawnMobBeeEffect(EntityType.BLAZE, 800, 60, 2)},
                new Object[]{"spawn_zombie", new SpawnMobBeeEffect(EntityType.ZOMBIE, 800, 100, 2, true)}
        )) {
            SpawnMobBeeEffect spawn = (SpawnMobBeeEffect) effect[1];
            helper.assertTrue(spawn.throttle() > 0 && spawn.chance() > 0 && spawn.maxMobs() > 0,
                    effect[0] + " lost valid legacy spawning parameters");
        }

        helper.assertTrue(new SpawnMobBeeEffect(EntityType.WOLF, 650, 40, 2).chance() == 40,
                "Canine spawn chance drifted from 1.12");
        helper.assertTrue(new SpawnMobBeeEffect(EntityType.CHICKEN, 20, 100, 20).maxMobs() == 20,
                "Chicken spawn cap drifted from 1.12");
        helper.assertTrue(new SpawnMobBeeEffect(EntityType.GHAST, 2060, 10, 1).throttle() == 2060,
                "Ghast spawn throttle drifted from 1.12");
        helper.assertTrue(new SpawnMobBeeEffect(EntityType.HORSE, 450, 59, 2).chance() == 59
                        && new SpawnMobBeeEffect(EntityType.CAT, 702, 60, 2).chance() == 60,
                "Horse/Cat spawning chance drifted from 1.12");
        helper.assertTrue(new SpawnMobBeeEffect(EntityType.ZOMBIE, 800, 100, 2, true).angryOnPlayers(),
                "Brainy spawn effect lost the 1.12 angry-on-player flag");

        helper.assertTrue(WorldTransformBeeEffect.replacementForState(Blocks.SAND.defaultBlockState(), WorldTransformBeeEffect.Mode.TRANSMUTING, true).is(Blocks.SANDSTONE),
                "Transmuting must turn sand into sandstone in sandy biomes");
        helper.assertTrue(WorldTransformBeeEffect.replacementForState(Blocks.STONE.defaultBlockState(), WorldTransformBeeEffect.Mode.CRUMBLING, false).is(Blocks.COBBLESTONE), "Crumbling stone mapping lost");
        helper.assertTrue(WorldTransformBeeEffect.replacementForState(Blocks.COBBLESTONE.defaultBlockState(), WorldTransformBeeEffect.Mode.CRUMBLING, false).is(Blocks.MOSSY_COBBLESTONE), "Crumbling cobblestone mapping lost");
        helper.assertTrue(WorldTransformBeeEffect.replacementForState(Blocks.STONE_BRICKS.defaultBlockState(), WorldTransformBeeEffect.Mode.CRUMBLING, false).is(Blocks.CRACKED_STONE_BRICKS), "Crumbling stone brick mapping lost");
        helper.assertTrue(WorldTransformBeeEffect.replacementForState(Blocks.CRACKED_STONE_BRICKS.defaultBlockState(), WorldTransformBeeEffect.Mode.CRUMBLING, false).is(Blocks.MOSSY_STONE_BRICKS), "Crumbling cracked brick mapping lost");
        helper.assertTrue(WorldTransformBeeEffect.replacementForState(Blocks.COBBLESTONE_WALL.defaultBlockState(), WorldTransformBeeEffect.Mode.CRUMBLING, false).is(Blocks.MOSSY_COBBLESTONE_WALL), "Crumbling wall mapping lost");
        helper.assertTrue(WorldTransformBeeEffect.replacementForState(Blocks.GRAVEL.defaultBlockState(), WorldTransformBeeEffect.Mode.CRUMBLING, false).is(Blocks.SAND), "Crumbling gravel mapping lost");
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void railcraftTransmutationPreservesLegacyEligibilityAndPriority(GameTestHelper helper) {
        helper.assertTrue(MagicBees.id("dummy") != null, "Magic Bees resource locations must be available");
        ResourceLocation quarriedStone = ResourceLocation.fromNamespaceAndPath("railcraft", "quarried_stone");
        ResourceLocation quarriedCobble = ResourceLocation.fromNamespaceAndPath("railcraft", "quarried_cobblestone");
        ResourceLocation abyssalStone = ResourceLocation.fromNamespaceAndPath("railcraft", "abyssal_stone");
        ResourceLocation abyssalCobble = ResourceLocation.fromNamespaceAndPath("railcraft", "abyssal_cobblestone");

        helper.assertTrue(quarriedStone.equals(WorldTransformBeeEffect.railcraftReplacementId(
                Blocks.STONE.defaultBlockState(), true, false, false, false)),
                "Forest stone must map to modern Railcraft quarried stone");
        helper.assertTrue(quarriedCobble.equals(WorldTransformBeeEffect.railcraftReplacementId(
                Blocks.COBBLESTONE.defaultBlockState(), true, false, false, false)),
                "Forest cobblestone must map to modern Railcraft quarried cobblestone");
        helper.assertTrue(abyssalStone.equals(WorldTransformBeeEffect.railcraftReplacementId(
                Blocks.STONE.defaultBlockState(), false, false, true, false)),
                "Ocean stone must map to modern Railcraft abyssal stone");
        helper.assertTrue(abyssalCobble.equals(WorldTransformBeeEffect.railcraftReplacementId(
                Blocks.COBBLESTONE.defaultBlockState(), false, false, true, false)),
                "Ocean cobblestone must map to modern Railcraft abyssal cobblestone");

        helper.assertTrue(WorldTransformBeeEffect.railcraftReplacementId(
                Blocks.STONE.defaultBlockState(), true, true, false, false) == null,
                "Snowy forests must not receive Quarried transmutation");
        helper.assertTrue(WorldTransformBeeEffect.railcraftReplacementId(
                Blocks.STONE.defaultBlockState(), false, false, true, true) == null,
                "River-tagged water biomes must not receive Abyssal transmutation");
        helper.assertTrue(WorldTransformBeeEffect.railcraftReplacementId(
                Blocks.ANDESITE.defaultBlockState(), true, false, true, false) == null,
                "Non-plain stone variants must remain ineligible");
        helper.assertTrue(quarriedStone.equals(WorldTransformBeeEffect.railcraftReplacementId(
                Blocks.STONE.defaultBlockState(), true, false, true, false)),
                "Forest mapping must retain legacy priority over the water/ocean mapping");
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void hiveBlocksRemainRegistered(GameTestHelper helper) {
        for (var block : List.of(MagicBeesBlocks.CURIOUS_HIVE, MagicBeesBlocks.UNUSUAL_HIVE,
                MagicBeesBlocks.RESONANT_HIVE, MagicBeesBlocks.DEEP_HIVE,
                MagicBeesBlocks.INFERNAL_HIVE, MagicBeesBlocks.OBLIVION_HIVE)) {
            ResourceLocation id = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block.get());
            helper.assertTrue(MagicBees.MOD_ID.equals(id.getNamespace()), "Hive escaped Magic Bees namespace: " + id);
            helper.assertTrue(id.getPath().endsWith("_hive"), "Hive has unexpected registry ID: " + id);
        }
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void thermalSpeciesPreserveLegacyTemplatesAndEffects(GameTestHelper helper) {
        if (!ModList.get().isLoaded("thermal_foundation")) {
            assertSpeciesAbsent(helper, EXPECTED_THERMAL_SPECIES, "Thermal Foundation");
            helper.succeed();
            return;
        }

        IBeeSpecies coal = requireSpecies(helper, "te_coal");
        assertAllele(helper, coal, BeeChromosomes.SPEED, ForestryAlleles.SPEED_FAST);
        assertAllele(helper, coal, BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_LONGER);
        assertAllele(helper, coal, BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_3);
        assertAllele(helper, coal, BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_2);
        assertAllele(helper, coal, BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_2);
        assertAllele(helper, coal, BeeChromosomes.TOLERATES_RAIN, ForestryAlleles.TRUE_RECESSIVE);
        assertReference(helper, coal, BeeChromosomes.ACTIVITY, ForestryActivityTypes.DIURNAL);
        assertReference(helper, coal, BeeChromosomes.FLOWER_TYPE, ForestryFlowerTypes.VANILLA);
        assertReference(helper, coal, BeeChromosomes.EFFECT, ForestryBeeEffects.NONE);

        IBeeSpecies blizzy = requireSpecies(helper, "te_blizzy");
        assertAllele(helper, blizzy, BeeChromosomes.SPEED, ForestryAlleles.SPEED_FASTEST);
        assertAllele(helper, blizzy, BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_LONGEST);
        assertReference(helper, blizzy, BeeChromosomes.FLOWER_TYPE, ForestryFlowerTypes.SNOW);
        assertReference(helper, blizzy, BeeChromosomes.EFFECT, ForestryBeeEffects.GLACIAL);

        IBeeSpecies amped = requireSpecies(helper, "te_amped");
        assertAllele(helper, amped, BeeChromosomes.SPEED, Allele.recessive(2.0f));
        assertReference(helper, amped, BeeChromosomes.EFFECT, MagicBees.id("spawn_blitz"));

        assertSpeciesAbsent(helper, Set.of(MagicBees.id("te_winsome"), MagicBees.id("te_endearing")),
                "the disabled platinum-dependent Thermal line");

        assertSpecialty(helper, blizzy, "thermal:blizz_powder", 0.09f);
        assertSpecialty(helper, requireSpecies(helper, "te_dante"), "thermal:sulfur_dust", 0.09f);
        assertSpecialty(helper, requireSpecies(helper, "te_shocking"), "thermal:blitz_powder", 0.09f);
        assertSpecialty(helper, requireSpecies(helper, "te_grounded"), "thermal:basalz_powder", 0.09f);

        SpawnMobBeeEffect blizz = new SpawnMobBeeEffect(ResourceLocation.fromNamespaceAndPath("thermal", "blizz"), 100, 80, 5);
        SpawnMobBeeEffect blitz = new SpawnMobBeeEffect(ResourceLocation.fromNamespaceAndPath("thermal", "blitz"), 100, 80, 5);
        SpawnMobBeeEffect basalz = new SpawnMobBeeEffect(ResourceLocation.fromNamespaceAndPath("thermal", "basalz"), 100, 80, 5);
        for (SpawnMobBeeEffect effect : List.of(blizz, blitz, basalz)) {
            helper.assertTrue(effect.throttle() == 100, "Thermal spawn effect throttle drifted");
            helper.assertTrue(effect.chance() == 80, "Thermal spawn effect chance drifted");
            helper.assertTrue(effect.maxMobs() == 5, "Thermal spawn effect cap drifted");
        }
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void botaniaSpeciesRespectOptionalIntegrationBoundary(GameTestHelper helper) {
        if (!ModList.get().isLoaded("botania")) {
            assertSpeciesAbsent(helper, EXPECTED_BOTANIA_SPECIES, "Botania");
            helper.succeed();
            return;
        }

        for (ResourceLocation id : EXPECTED_BOTANIA_SPECIES) {
            helper.assertTrue(findSpecies(id) != null,
                    "Botania species is missing with Botania loaded: " + id);
        }
        helper.assertTrue(requireSpecies(helper, "bot_botanic").getSpecialties().size() == 16,
                "Botanic must restore all 16 legacy petal specialties");
        helper.assertTrue(requireSpecies(helper, "bot_blossom").getSpecialties().size() == 16,
                "Blossom must restore all 16 legacy petal specialties");
        helper.assertTrue(requireSpecies(helper, "bot_floral").getSpecialties().size() == 16,
                "Floral must restore all 16 legacy petal specialties");
        helper.assertTrue(requireSpecies(helper, "bot_vazbee").getSpecialties().size() == 7,
                "Vazbee must expose the seven pasture-seed concepts retained by Botania build455");
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void ae2SkyStoneSpeciesPreservesLegacyGenome(GameTestHelper helper) {
        if (!ModList.get().isLoaded("ae2")) {
            helper.assertTrue(findSpecies(AE_SKYSTONE) == null,
                    "AE Sky Stone species must be absent when AE2 is not loaded");
            helper.succeed();
            return;
        }
        IBeeSpecies skyStone = requireSpecies(helper, "ae_skystone");
        assertAllele(helper, skyStone, BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
        assertAllele(helper, skyStone, BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_2);
        assertAllele(helper, skyStone, BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_SHORTER);
        assertAllele(helper, skyStone, BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_NONE);
        assertAllele(helper, skyStone, BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_NONE);
        assertReference(helper, skyStone, BeeChromosomes.EFFECT, ForestryBeeEffects.IGNITION);
        assertOutputs(helper, skyStone, Map.of("magicbees:bee_comb_earthy", 0.19f), Map.of("ae2:sky_stone_block", 0.02f));
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void baseForestryProcessingRecipesReload(GameTestHelper helper) {
        var manager = helper.getLevel().getRecipeManager();
        for (String name : List.of(
                "mundane_comb", "molten_comb", "forgotten_comb", "occult_comb", "otherworldly_comb",
                "papery_comb", "intellect_comb", "furtive_comb", "soul_comb", "temporal_comb",
                "transmuted_comb", "airy_comb", "firey_comb", "watery_comb", "earthy_comb")) {
            ResourceLocation id = MagicBees.id("centrifuge/" + name);
            helper.assertTrue(manager.byKey(id).isPresent(), "Missing live centrifuge recipe after reload: " + id);
        }
        for (String name : List.of("candles_string", "candles_silk_wisp", "aromatic_lump_1", "aromatic_lump_2")) {
            ResourceLocation id = MagicBees.id("carpenter/" + name);
            helper.assertTrue(manager.byKey(id).isPresent(), "Missing live carpenter recipe after reload: " + id);
        }
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void baseCraftingProgressionRecipesReloadAndPreserveDragonEgg(GameTestHelper helper) {
        var manager = helper.getLevel().getRecipeManager();
        List<String> recipes = List.of(
                "exp", "soulsand1", "soulsand2", "moondial",
                "nsk", "skullfragment", "dragonchunk", "dragonegg",
                "essence_eld1", "essence_eld2", "essence_fl1", "essence_fl2", "essence_cg1", "essence_cg2",
                "essence_lt1", "essence_lt2", "essence_fp1", "essence_fp2", "essence_scob",
                "magicframe", "resilientframe", "gentleframe", "necroticframe", "metabolicframe",
                "temporal_frame", "temporalframe_essence", "oblivionframe",
                "enchanted_earth_1", "enchanted_earth_2", "dimensionalsingularity");
        helper.assertTrue(recipes.size() == 30, "#15B crafting regression list must contain exactly 30 routes");
        for (String name : recipes) {
            ResourceLocation id = MagicBees.id("crafting/" + name);
            helper.assertTrue(manager.byKey(id).isPresent(), "Missing live base crafting recipe after reload: " + id);
        }

        ScornfulOblivionRecipe recipe = new ScornfulOblivionRecipe(CraftingBookCategory.MISC);
        CraftingInput input = CraftingInput.of(3, 3, List.of(
                new ItemStack(MagicBeesItems.resource("essence_shallow_grave").get()), new ItemStack(Items.WITHER_SKELETON_SKULL), new ItemStack(MagicBeesItems.resource("essence_lost_time").get()),
                new ItemStack(Items.WITHER_SKELETON_SKULL), new ItemStack(Items.DRAGON_EGG), new ItemStack(Items.WITHER_SKELETON_SKULL),
                new ItemStack(MagicBeesItems.resource("essence_lost_time").get()), new ItemStack(Items.WITHER_SKELETON_SKULL), new ItemStack(MagicBeesItems.resource("essence_shallow_grave").get())));
        helper.assertTrue(recipe.matches(input, helper.getLevel()), "Scornful Oblivion legacy pattern no longer matches");
        CraftingInput mirroredInput = CraftingInput.of(3, 3, List.of(
                new ItemStack(MagicBeesItems.resource("essence_lost_time").get()), new ItemStack(Items.WITHER_SKELETON_SKULL), new ItemStack(MagicBeesItems.resource("essence_shallow_grave").get()),
                new ItemStack(Items.WITHER_SKELETON_SKULL), new ItemStack(Items.DRAGON_EGG), new ItemStack(Items.WITHER_SKELETON_SKULL),
                new ItemStack(MagicBeesItems.resource("essence_shallow_grave").get()), new ItemStack(Items.WITHER_SKELETON_SKULL), new ItemStack(MagicBeesItems.resource("essence_lost_time").get())));
        helper.assertTrue(recipe.matches(mirroredInput, helper.getLevel()), "Scornful Oblivion mirrored legacy pattern no longer matches");
        ItemStack result = recipe.assemble(input, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(MagicBeesItems.resource("essence_scornful_oblivion").get()), "Scornful Oblivion output drifted");
        var remaining = recipe.getRemainingItems(input);
        helper.assertTrue(remaining.get(4).is(Items.DRAGON_EGG), "Dragon Egg catalyst was consumed instead of returned");
        for (int slot = 0; slot < remaining.size(); slot++) {
            if (slot != 4) helper.assertTrue(remaining.get(slot).isEmpty(), "Unexpected crafting remainder in slot " + slot);
        }
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void everyRegisteredMagicBeesItemHasRecipeOrBeeSource(GameTestHelper helper) {
        Set<String> magicItems = new TreeSet<>();
        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (MagicBees.MOD_ID.equals(id.getNamespace()) && !isIgnoredHiveItem(item, id)) {
                magicItems.add(id.toString());
            }
        }

        Set<String> recipeOutputs = recipeOutputItemIds(helper.getLevel().getRecipeManager(), helper.getLevel().registryAccess());
        recipeOutputs.addAll(beeProductItemIds());
        Set<String> missing = new TreeSet<>(magicItems);
        missing.removeAll(recipeOutputs);

        if (!missing.isEmpty()) {
            MagicBees.LOGGER.error("Registered Magic Bees items/blocks without any live recipe or bee-production source: {}", missing);
        }
        helper.assertTrue(missing.isEmpty(),
                "Registered Magic Bees items/blocks without any live recipe or bee-production source: "
                        + missing.size() + " missing, first entries " + missing.stream().limit(12).toList());
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void mysteriousMagnetPreservesLegacyStateUpgradeAndAttraction(GameTestHelper helper) {
        var manager = helper.getLevel().getRecipeManager();
        helper.assertTrue(manager.byKey(MagicBees.id("crafting/mysteriousmagnet")).isPresent(), "Missing live base Magnet recipe");
        helper.assertTrue(manager.byKey(MagicBees.id("crafting/mysteriousmagnet_upgrade")).isPresent(), "Missing live Magnet upgrade recipe");

        ItemStack left = new ItemStack(MagicBeesItems.MYSTERIOUS_MAGNET.get());
        ItemStack right = new ItemStack(MagicBeesItems.MYSTERIOUS_MAGNET.get());
        MysteriousMagnetItem.setState(left, new MagnetState(6, false));
        MysteriousMagnetItem.setState(right, new MagnetState(6, false));
        helper.assertTrue(MysteriousMagnetItem.level(left) == 6 && !MysteriousMagnetItem.active(left), "Magnet state component did not preserve level/inactive state");
        double expectedRange = eu.dsix.magicbees.config.MagicBeesConfig.COMMON.magnetBaseRange.get()
                + 6 * eu.dsix.magicbees.config.MagicBeesConfig.COMMON.magnetLevelMultiplier.get();
        helper.assertTrue(Math.abs(MysteriousMagnetItem.configuredRange(left) - expectedRange) < 0.000001D, "Magnet range formula drifted");
        helper.assertTrue(Math.abs(MysteriousMagnetItem.effectiveRange(left) - (expectedRange - 0.2D)) < 0.000001D, "Magnet legacy -0.2 effective-radius fudge drifted");

        MagnetUpgradeRecipe recipe = new MagnetUpgradeRecipe(CraftingBookCategory.MISC);
        List<ItemStack> grid = new ArrayList<>(List.of(
                ItemStack.EMPTY, new ItemStack(Items.DIAMOND), ItemStack.EMPTY,
                left, new ItemStack(MagicBeesItems.resource("dimensional_singularity").get()), right,
                ItemStack.EMPTY, new ItemStack(Items.REDSTONE_BLOCK), ItemStack.EMPTY));
        CraftingInput input = CraftingInput.of(3, 3, grid);
        helper.assertTrue(recipe.matches(input, helper.getLevel()), "Two equal inactive level-6 magnets should match the upgrade recipe");
        ItemStack output = recipe.assemble(input, helper.getLevel().registryAccess());
        helper.assertTrue(output.is(MagicBeesItems.MYSTERIOUS_MAGNET.get()) && MysteriousMagnetItem.level(output) == 7 && !MysteriousMagnetItem.active(output),
                "Magnet level 6 -> 7 upgrade output drifted");
        MysteriousMagnetItem.setState(left, new MagnetState(6, true));
        helper.assertTrue(!recipe.matches(CraftingInput.of(3, 3, grid), helper.getLevel()), "Active magnets must not match legacy even-metadata upgrade semantics");

        Vec3 vector = MysteriousMagnetItem.attractionVector(new Vec3(2, 0, 0), new Vec3(0.2, 0, 0), Vec3.ZERO);
        helper.assertTrue(Math.abs(vector.x - 0.6D) < 0.000001D && Math.abs(vector.y) < 0.000001D && Math.abs(vector.z) < 0.000001D,
                "Legacy 0.5-block/tick attraction vector drifted: " + vector);
        helper.assertTrue(!MysteriousMagnetItem.attractsItemAge(9) && MysteriousMagnetItem.attractsItemAge(10), "Dropped-item age threshold drifted");
        helper.assertTrue(!MysteriousMagnetItem.processesArrowsAtLevel(6) && MysteriousMagnetItem.processesArrowsAtLevel(7), "Arrow-processing level threshold drifted");
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void effectJarRunsContainedQueenEffectAndPreservesLegacyState(GameTestHelper helper) {
        var manager = helper.getLevel().getRecipeManager();
        helper.assertTrue(manager.byKey(MagicBees.id("crafting/effectjar")).isPresent(), "Missing live Effect Jar recipe after reload");
        helper.assertTrue(EffectJarBlockEntity.AGE_THROTTLE == 550, "Effect Jar aging throttle drifted");
        helper.assertTrue(Math.abs(EffectJarBlockEntity.AGE_STEP - 0.26F) < 0.000001F, "Effect Jar aging step drifted");

        BlockPos jarPos = new BlockPos(2, 2, 2);
        helper.setBlock(jarPos, MagicBeesBlocks.EFFECT_JAR.get());
        // The legacy jar still used Forestry's normal canWork() checks; it did not magically provide flowers.
        // Timely inherits the ordinary vanilla flower type, so give the housing a valid nearby flower.
        helper.setBlock(jarPos.offset(1, -1, 0), Blocks.DIRT);
        helper.setBlock(jarPos.offset(1, 0, 0), Blocks.POPPY);
        EffectJarBlockEntity jar = helper.getBlockEntity(jarPos);
        IBeeSpecies timely = requireSpecies(helper, "timely");
        ItemStack drones = timely.createStack(BeeLifeStage.DRONE);
        drones.setCount(2);
        jar.setVisibleStack(drones);
        helper.assertTrue(jar.getCurrentBeeHealth() > 0,
                "Effect Jar GUI health bar must update as soon as a bee is placed in the slot");
        helper.assertTrue(jar.getCurrentBeeColour() == timely.getBody(),
                "Effect Jar GUI health bar colour must match the inserted bee species");
        EffectJarBlockEntity.serverTick(helper.getLevel(), helper.absolutePos(jarPos), MagicBeesBlocks.EFFECT_JAR.get().defaultBlockState(), jar);
        helper.assertTrue(jar.getVisibleStack().getCount() == 1, "Effect Jar must instantly consume one inserted Drone as its hidden running Queen");
        helper.assertTrue(IIndividualHandlerItem.getLifeStage(jar.getQueenStack()) == BeeLifeStage.QUEEN, "Effect Jar did not convert inserted Drone into internal Queen");
        helper.assertTrue(jar.getCurrentBeeHealth() > 0, "Effect Jar Queen presentation health was not initialized");
        helper.assertTrue(jar.getTicksUntilDeath() > 0, "Effect Jar did not expose time left until the contained Queen dies");
        IBee originalQueen = (IBee) IIndividualHandlerItem.getIndividual(jar.getQueenStack());
        jar.setVisibleStack(requireSpecies(helper, "batty").createStack(BeeLifeStage.DRONE));
        helper.assertTrue(((IBee) IIndividualHandlerItem.getIndividual(jar.getQueenStack())).getSpecies() == originalQueen.getSpecies(),
                "Queued replacement Drones must not change the currently displayed/running hidden Queen");
        EffectJarBlockEntity.serverTick(helper.getLevel(), helper.absolutePos(jarPos), MagicBeesBlocks.EFFECT_JAR.get().defaultBlockState(), jar);
        helper.assertTrue(jar.isActive(), "Effect Jar housing cannot work with a valid nearby vanilla flower");

        Cow cow = helper.spawn(EntityType.COW, 3, 2, 2);
        for (int i = 0; i < 205; i++) {
            EffectJarBlockEntity.serverTick(helper.getLevel(), helper.absolutePos(jarPos), MagicBeesBlocks.EFFECT_JAR.get().defaultBlockState(), jar);
        }
        helper.assertTrue(cow.hasEffect(MobEffects.MOVEMENT_SLOWDOWN), "Contained Timely Queen did not execute its real slowdown bee effect");

        jar.setThrottleForTest(123);
        var saved = jar.saveWithoutMetadata(helper.getLevel().registryAccess());
        EffectJarBlockEntity restored = new EffectJarBlockEntity(helper.absolutePos(jarPos), MagicBeesBlocks.EFFECT_JAR.get().defaultBlockState());
        restored.setLevel(helper.getLevel());
        restored.loadWithComponents(saved, helper.getLevel().registryAccess());
        helper.assertTrue(restored.getVisibleStack().getCount() == 1, "Effect Jar remaining Drone input did not survive NBT round-trip");
        helper.assertTrue(IIndividualHandlerItem.getLifeStage(restored.getQueenStack()) == BeeLifeStage.QUEEN, "Effect Jar internal Queen did not survive NBT round-trip");
        helper.assertTrue(restored.getThrottle() == 123, "Effect Jar aging throttle did not survive NBT round-trip");
        helper.assertTrue(restored.getTicksUntilDeath() > 0, "Effect Jar time-left menu data did not survive NBT round-trip");
        helper.assertTrue(!restored.isActive(), "Effect Jar transient active state must reset after reload");

        ItemStack removed = restored.removeItemNoUpdate(0);
        helper.assertTrue(removed.getCount() == 1, "Effect Jar slot removal must return only unconsumed Drones");
        helper.assertTrue(IIndividualHandlerItem.getLifeStage(restored.getQueenStack()) == BeeLifeStage.QUEEN,
                "Removing unconsumed Drones must not stop the hidden Effect Jar Queen");
        restored.setVisibleStack(removed);
        EffectJarBlockEntity.serverTick(helper.getLevel(), helper.absolutePos(jarPos), MagicBeesBlocks.EFFECT_JAR.get().defaultBlockState(), restored);

        AABB dropsArea = new AABB(helper.absolutePos(jarPos)).inflate(2.0D);
        jar.setVisibleStack(restored.getVisibleStack());
        jar.setQueenStack(restored.getQueenStack());
        helper.setBlock(jarPos, Blocks.AIR);
        List<ItemEntity> drops = helper.getLevel().getEntitiesOfClass(ItemEntity.class, dropsArea);
        long dronesDropped = drops.stream()
                .filter(e -> IIndividualHandlerItem.getLifeStage(e.getItem()) == BeeLifeStage.DRONE)
                .mapToLong(e -> e.getItem().getCount()).sum();
        long queensDropped = drops.stream().filter(e -> IIndividualHandlerItem.getLifeStage(e.getItem()) == BeeLifeStage.QUEEN).count();
        helper.assertTrue(dronesDropped == 1, "Breaking Effect Jar must return only its unconsumed Drone stack");
        helper.assertTrue(queensDropped == 0, "Legacy Effect Jar break rule must not return the internally running Queen");
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void effectJarBattySpawnsBatsOnlyWhileQueenCanWork(GameTestHelper helper) {
        BlockPos inactivePos = new BlockPos(2, 2, 2);
        helper.setBlock(inactivePos, MagicBeesBlocks.EFFECT_JAR.get());
        EffectJarBlockEntity inactiveJar = helper.getBlockEntity(inactivePos);
        inactiveJar.setVisibleStack(requireSpecies(helper, "batty").createStack(BeeLifeStage.DRONE));
        EffectJarBlockEntity.serverTick(helper.getLevel(), helper.absolutePos(inactivePos), MagicBeesBlocks.EFFECT_JAR.get().defaultBlockState(), inactiveJar);
        int pausedTimeLeft = inactiveJar.getTicksUntilDeath();
        for (int i = 0; i < 170; i++) {
            EffectJarBlockEntity.serverTick(helper.getLevel(), helper.absolutePos(inactivePos), MagicBeesBlocks.EFFECT_JAR.get().defaultBlockState(), inactiveJar);
        }
        helper.assertTrue(!inactiveJar.isActive(), "Effect Jar must report inactive when the Queen cannot satisfy Forestry work conditions");
        helper.assertTrue(inactiveJar.getErrorLogic().contains(ForestryError.NO_FLOWER),
                "Effect Jar error ledger must report missing flowers while the Queen cannot find valid flowers");
        helper.assertTrue(inactiveJar.getTicksUntilDeath() == pausedTimeLeft,
                "Effect Jar life timer must pause instead of pretending to tick while the Queen cannot work");
        helper.assertTrue(helper.getLevel().getEntitiesOfClass(Bat.class, new AABB(helper.absolutePos(inactivePos)).inflate(12.0D)).isEmpty(),
                "Batty Effect Jar must not spawn bats while normal bee work conditions fail");
        helper.setBlock(inactivePos.offset(1, -1, 0), Blocks.DIRT);
        helper.setBlock(inactivePos.offset(1, 0, 0), Blocks.POPPY);
        for (int i = 0; i < 10; i++) {
            EffectJarBlockEntity.serverTick(helper.getLevel(), helper.absolutePos(inactivePos), MagicBeesBlocks.EFFECT_JAR.get().defaultBlockState(), inactiveJar);
        }
        helper.assertTrue(inactiveJar.isActive(),
                "Effect Jar active/status sync must update after valid flowers are placed near an already-paused Queen");
        helper.assertTrue(!inactiveJar.getErrorLogic().contains(ForestryError.NO_FLOWER),
                "Effect Jar error ledger must clear missing-flowers quickly after valid flowers are placed");
        helper.assertTrue(inactiveJar.getTicksUntilDeath() < pausedTimeLeft,
                "Effect Jar life timer must resume after valid flowers are placed near an already-paused Queen");

        BlockPos activePos = new BlockPos(8, 2, 2);
        helper.setBlock(activePos, MagicBeesBlocks.EFFECT_JAR.get());
        helper.setBlock(activePos.offset(1, -1, 0), Blocks.DIRT);
        helper.setBlock(activePos.offset(1, 0, 0), Blocks.POPPY);
        EffectJarBlockEntity activeJar = helper.getBlockEntity(activePos);
        activeJar.setVisibleStack(requireSpecies(helper, "batty").createStack(BeeLifeStage.DRONE));
        for (int i = 0; i < 170; i++) {
            EffectJarBlockEntity.serverTick(helper.getLevel(), helper.absolutePos(activePos), MagicBeesBlocks.EFFECT_JAR.get().defaultBlockState(), activeJar);
        }
        helper.assertTrue(activeJar.isActive(), "Batty Effect Jar did not become active with a valid nearby flower");
        helper.assertTrue(activeJar.getTicksUntilDeath() < pausedTimeLeft, "Active Effect Jar life timer did not tick down");
        List<Bat> bats = helper.getLevel().getEntitiesOfClass(Bat.class, new AABB(helper.absolutePos(activePos)).inflate(12.0D));
        helper.assertTrue(!bats.isEmpty(),
                "Batty Effect Jar did not execute its Spawn Bat effect after the 150-tick legacy throttle");
        BlockPos activeAbs = helper.absolutePos(activePos);
        Bat bat = bats.getFirst();
        helper.assertTrue(Math.abs(bat.getX() - (activeAbs.getX() + 0.5D)) < 0.001D
                        && Math.abs(bat.getY() - (activeAbs.getY() + 1.0D)) < 0.001D
                        && Math.abs(bat.getZ() - (activeAbs.getZ() + 0.5D)) < 0.001D,
                "Flying jar spawns such as bats must appear above the jar, not at a random point in bee territory");
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty", timeoutTicks = 1200)
    public static void effectJarWispyPreservesLegacyThrottleButCapsNearbyWisps(GameTestHelper helper) {
        if (!ModList.get().isLoaded("thaumaturge")) {
            helper.succeed();
            return;
        }
        var flower = BuiltInRegistries.BLOCK.getOptional(ResourceLocation.fromNamespaceAndPath("thaumaturge", "shimmerleaf"));
        var wispType = BuiltInRegistries.ENTITY_TYPE.getOptional(ResourceLocation.fromNamespaceAndPath("thaumaturge", "wisp"));
        if (flower.isEmpty() || wispType.isEmpty()) {
            helper.succeed();
            return;
        }

        BlockPos jarPos = new BlockPos(3, 2, 3);
        helper.setBlock(jarPos, MagicBeesBlocks.EFFECT_JAR.get());
        helper.setBlock(jarPos.offset(1, -1, 0), Blocks.DIRT);
        helper.setBlock(jarPos.offset(1, 0, 0), flower.get());
        EffectJarBlockEntity jar = helper.getBlockEntity(jarPos);
        jar.setVisibleStack(requireSpecies(helper, "tc_wispy").createStack(BeeLifeStage.DRONE));

        for (int i = 0; i < 900; i++) {
            EffectJarBlockEntity.serverTick(helper.getLevel(), helper.absolutePos(jarPos), MagicBeesBlocks.EFFECT_JAR.get().defaultBlockState(), jar);
        }

        AABB bounds = new AABB(helper.absolutePos(jarPos)).inflate(12.0D);
        int wisps = helper.getLevel()
                .getEntities((net.minecraft.world.entity.Entity) null, bounds, entity -> entity.getType() == wispType.get())
                .size();
        helper.assertTrue(wisps <= 2, "Wispy Effect Jar must cap nearby Wisps instead of flooding the area, found " + wisps);
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void wildHiveDefinitionsMatchLegacyLedger(GameTestHelper helper) {
        List<forestry.api.apiculture.hives.IHive> magicHives = IForestryApi.INSTANCE.getHiveManager().getHives().stream()
                .filter(hive -> MagicBees.MOD_ID.equals(net.minecraft.core.registries.BuiltInRegistries.BLOCK
                        .getKey(hive.getHiveBlockState().getBlock()).getNamespace()))
                .toList();
        helper.assertTrue(magicHives.size() == 8,
                "Expected six hive blocks backed by eight legacy generation descriptions, found " + magicHives.size());

        assertHiveChances(helper, magicHives, MagicBeesBlocks.CURIOUS_HIVE.get(), 3.0f);
        assertHiveChances(helper, magicHives, MagicBeesBlocks.UNUSUAL_HIVE.get(), 1.0f);
        assertHiveChances(helper, magicHives, MagicBeesBlocks.RESONANT_HIVE.get(), 0.9f);
        assertHiveChances(helper, magicHives, MagicBeesBlocks.DEEP_HIVE.get(), 3.8f);
        assertHiveChances(helper, magicHives, MagicBeesBlocks.INFERNAL_HIVE.get(), 0.95f, 50.0f);
        assertHiveChances(helper, magicHives, MagicBeesBlocks.OBLIVION_HIVE.get(), 0.87f, 20.0f);

        var biomes = helper.getLevel().registryAccess().lookupOrThrow(net.minecraft.core.registries.Registries.BIOME);
        var forest = biomes.getOrThrow(net.minecraft.world.level.biome.Biomes.FOREST);
        var plains = biomes.getOrThrow(net.minecraft.world.level.biome.Biomes.PLAINS);
        var hills = biomes.getOrThrow(net.minecraft.world.level.biome.Biomes.WINDSWEPT_HILLS);
        var river = biomes.getOrThrow(net.minecraft.world.level.biome.Biomes.RIVER);
        var mountain = biomes.getOrThrow(net.minecraft.world.level.biome.Biomes.JAGGED_PEAKS);
        var desert = biomes.getOrThrow(net.minecraft.world.level.biome.Biomes.DESERT);
        var badlands = biomes.getOrThrow(net.minecraft.world.level.biome.Biomes.BADLANDS);
        var nether = biomes.getOrThrow(net.minecraft.world.level.biome.Biomes.NETHER_WASTES);
        var end = biomes.getOrThrow(net.minecraft.world.level.biome.Biomes.THE_END);
        var taiga = biomes.getOrThrow(net.minecraft.world.level.biome.Biomes.TAIGA);

        var curious = onlyHive(helper, magicHives, MagicBeesBlocks.CURIOUS_HIVE.get());
        helper.assertTrue(curious.isGoodBiome(forest) && curious.isGoodBiome(plains) && curious.isGoodBiome(hills),
                "Curious hive lost FOREST/PLAINS/HILLS BiomeDictionary equivalents");
        helper.assertTrue(!curious.isGoodBiome(nether), "Curious hive unexpectedly accepts Nether biome");

        var unusual = onlyHive(helper, magicHives, MagicBeesBlocks.UNUSUAL_HIVE.get());
        helper.assertTrue(unusual.isGoodBiome(plains) && unusual.isGoodBiome(mountain)
                        && unusual.isGoodBiome(hills) && unusual.isGoodBiome(river),
                "Unusual hive lost PLAINS/MOUNTAIN/HILLS/RIVER BiomeDictionary equivalents");

        var resonant = onlyHive(helper, magicHives, MagicBeesBlocks.RESONANT_HIVE.get());
        helper.assertTrue(resonant.isGoodBiome(desert) && resonant.isGoodBiome(badlands),
                "Resonant hive lost SANDY/MESA/HOT biome coverage");

        var deep = onlyHive(helper, magicHives, MagicBeesBlocks.DEEP_HIVE.get());
        helper.assertTrue(deep.isGoodBiome(hills) && deep.isGoodBiome(mountain),
                "Deep hive lost HILLS/MOUNTAIN biome coverage");

        var infernal = hivesForBlock(magicHives, MagicBeesBlocks.INFERNAL_HIVE.get());
        var infernalNether = hiveWithChance(helper, infernal, 50.0f);
        var infernalOverworld = hiveWithChance(helper, infernal, 0.95f);
        helper.assertTrue(infernalNether.isGoodBiome(nether), "Infernal Nether description lost NETHER biome coverage");
        helper.assertTrue(infernalOverworld.isGoodBiome(desert), "Infernal overworld description lost HOT biome coverage");

        var oblivion = hivesForBlock(magicHives, MagicBeesBlocks.OBLIVION_HIVE.get());
        var oblivionEnd = hiveWithChance(helper, oblivion, 20.0f);
        var oblivionOverworld = hiveWithChance(helper, oblivion, 0.87f);
        helper.assertTrue(oblivionEnd.isGoodBiome(end), "Oblivion End description lost END biome coverage");
        helper.assertTrue(oblivionOverworld.isGoodBiome(taiga), "Oblivion overworld description lost COLD biome coverage");

        var mundaneDrops = IForestryApi.INSTANCE.getHiveManager().getDrops(MagicBeeSpecies.MYSTICAL);
        helper.assertTrue(mundaneDrops.size() == 3, "Default Magic Bees hive must have three legacy drop entries");
        assertDropChance(helper, mundaneDrops, 0.80, 0.70);
        assertDropChance(helper, mundaneDrops, 0.15, 0.0);
        assertDropChance(helper, mundaneDrops, 0.05, 0.0);

        var infernalDrops = IForestryApi.INSTANCE.getHiveManager().getDrops(MagicBeeSpecies.INFERNAL);
        helper.assertTrue(infernalDrops.size() == 2, "Infernal hive must have two legacy drop entries");
        var infernalMainDrop = findDrop(helper, infernalDrops, 0.80);
        helper.assertTrue(Math.abs(infernalMainDrop.getIgnobleChance(helper.getLevel(), helper.absolutePos(BlockPos.ZERO), 0) - 0.50) < 0.0001,
                "Infernal main drop lost 50% ignoble chance");
        List<ItemStack> infernalExtras = infernalMainDrop.getExtraItems(helper.getLevel(), helper.absolutePos(BlockPos.ZERO), 0);
        helper.assertTrue(infernalExtras.stream().anyMatch(stack -> stack.is(Items.GLOWSTONE_DUST) && stack.getCount() == 6),
                "Infernal hive lost six Glowstone Dust bonus");

        var oblivionDrops = IForestryApi.INSTANCE.getHiveManager().getDrops(MagicBeeSpecies.OBLIVION);
        helper.assertTrue(oblivionDrops.size() == 2, "Oblivion hive must have two legacy drop entries");
        var oblivionMainDrop = findDrop(helper, oblivionDrops, 0.80);
        List<ItemStack> oblivionExtras = oblivionMainDrop.getExtraItems(helper.getLevel(), helper.absolutePos(BlockPos.ZERO), 0);
        helper.assertTrue(oblivionExtras.stream().anyMatch(stack -> stack.is(Items.ENDER_PEARL)),
                "Oblivion hive lost Ender Pearl bonus");

        for (var block : List.of(Blocks.STONE, Blocks.GRANITE, Blocks.POLISHED_GRANITE, Blocks.DIORITE,
                Blocks.POLISHED_DIORITE, Blocks.ANDESITE, Blocks.POLISHED_ANDESITE)) {
            helper.assertTrue(eu.dsix.magicbees.forestry.hive.MagicHivePlacements.isLegacyStone(block.defaultBlockState()),
                    "Lost flattened 1.12 Blocks.STONE variant " + block);
        }
        helper.assertTrue(!eu.dsix.magicbees.forestry.hive.MagicHivePlacements.isLegacyStone(Blocks.DEEPSLATE.defaultBlockState()),
                "Deepslate did not exist in the 1.12 Blocks.STONE metadata family");
        helper.assertTrue(!eu.dsix.magicbees.forestry.hive.MagicHivePlacements.isLegacyStone(Blocks.TUFF.defaultBlockState()),
                "Tuff did not exist in the 1.12 Blocks.STONE metadata family");

        for (var block : List.of(Blocks.DIRT, Blocks.COARSE_DIRT, Blocks.PODZOL, Blocks.GRASS_BLOCK)) {
            helper.assertTrue(eu.dsix.magicbees.forestry.hive.MagicHivePlacements.isLegacyDirtGround(block.defaultBlockState()),
                    "Lost flattened 1.12 dirt/grass hive ground " + block);
        }
        helper.assertTrue(!eu.dsix.magicbees.forestry.hive.MagicHivePlacements.isLegacyDirtGround(Blocks.ROOTED_DIRT.defaultBlockState()),
                "Rooted dirt must not be silently added to the legacy hive ground set");

        for (var block : List.of(Blocks.SAND, Blocks.RED_SAND, Blocks.SANDSTONE, Blocks.CHISELED_SANDSTONE,
                Blocks.SMOOTH_SANDSTONE)) {
            helper.assertTrue(eu.dsix.magicbees.forestry.hive.MagicHivePlacements.isLegacySandGround(block.defaultBlockState()),
                    "Lost flattened 1.12 sand/sandstone hive ground " + block);
        }
        helper.assertTrue(!eu.dsix.magicbees.forestry.hive.MagicHivePlacements.isLegacySandGround(Blocks.RED_SANDSTONE.defaultBlockState()),
                "1.12 Blocks.RED_SANDSTONE was not part of the Resonant hive ground list");
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void effectJarMenuMatchesLegacyLayoutAndSync(GameTestHelper helper) {
        BlockPos relativeJarPos = new BlockPos(1, 1, 1);
        helper.setBlock(relativeJarPos, MagicBeesBlocks.EFFECT_JAR.get());
        BlockPos jarPos = helper.absolutePos(relativeJarPos);
        helper.assertTrue(helper.getLevel().getBlockEntity(jarPos) instanceof EffectJarBlockEntity,
                "Effect Jar block entity was not created for menu regression");
        EffectJarBlockEntity jar = (EffectJarBlockEntity) helper.getLevel().getBlockEntity(jarPos);

        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        EffectJarMenu menu = new EffectJarMenu(17, player.getInventory(), jar);
        helper.assertTrue(menu.slots.size() == 37, "Effect Jar menu must expose one jar slot plus 36 player slots");
        helper.assertTrue(menu.slots.get(0).x == 80 && menu.slots.get(0).y == 32,
                "Effect Jar input slot drifted from jarscreen.png (80,32)");
        helper.assertTrue(menu.slots.get(1).x == 8 && menu.slots.get(1).y == 84,
                "Player inventory start drifted from jarscreen.png (8,84)");
        helper.assertTrue(menu.slots.get(28).x == 8 && menu.slots.get(28).y == 142,
                "Hotbar start drifted from jarscreen.png (8,142)");
        IBeeSpecies timely = requireSpecies(helper, "timely");
        helper.assertTrue(menu.slots.get(0).mayPlace(timely.createStack(BeeLifeStage.DRONE)),
                "Effect Jar slot must accept Bee Drones");
        helper.assertTrue(!menu.slots.get(0).mayPlace(timely.createStack(BeeLifeStage.QUEEN)),
                "Effect Jar slot must reject direct Queen insertion");
        helper.assertTrue(!menu.slots.get(0).mayPlace(new ItemStack(Items.HONEYCOMB)),
                "Effect Jar slot must reject non-bee items");

        ItemStack drones = timely.createStack(BeeLifeStage.DRONE);
        drones.setCount(3);
        player.getInventory().setItem(0, drones.copy());
        ItemStack movedIntoJar = menu.quickMoveStack(player, 28);
        helper.assertTrue(movedIntoJar.getCount() == 3 && EffectJarBlockEntity.isDrone(movedIntoJar),
                "Shift-clicking player Drone stack must report the moved stack");
        helper.assertTrue(jar.getVisibleStack().getCount() == 3 && EffectJarBlockEntity.isDrone(jar.getVisibleStack()),
                "Shift-clicking player Drone stack must move it into the Effect Jar slot");
        helper.assertTrue(player.getInventory().getItem(0).isEmpty(),
                "Shift-clicking Drone stack into the Effect Jar must clear the source player slot");

        ItemStack movedOutOfJar = menu.quickMoveStack(player, 0);
        helper.assertTrue(movedOutOfJar.getCount() == 3 && EffectJarBlockEntity.isDrone(movedOutOfJar),
                "Shift-clicking the Effect Jar slot must report the moved Drone stack");
        helper.assertTrue(jar.getVisibleStack().isEmpty(),
                "Shift-clicking the Effect Jar slot must move queued Drones back to the player inventory");
        helper.assertTrue(countItem(player.getInventory(), movedOutOfJar) == 3,
                "Shift-clicked Effect Jar Drones did not arrive in the player inventory");

        player.getInventory().clearContent();
        player.getInventory().setItem(0, new ItemStack(Items.HONEYCOMB));
        menu.quickMoveStack(player, 28);
        helper.assertTrue(jar.getVisibleStack().isEmpty(),
                "Rejected non-Drone shift-click must leave the Effect Jar slot empty");
        helper.assertTrue(countItem(player.getInventory(), new ItemStack(Items.HONEYCOMB)) == 1,
                "Rejected non-Drone shift-click must leave the player stack in the player inventory");

        jar.menuData().set(0, 73);
        int colour = 0x9872FF;
        jar.menuData().set(1, colour & 0xFFFF);
        jar.menuData().set(2, (colour >>> 16) & 0xFF);
        jar.menuData().set(3, 41);
        jar.menuData().set(4, 1234);
        jar.menuData().set(5, 1);
        helper.assertTrue(menu.beeHealth() == 73, "Effect Jar health menu data drifted");
        helper.assertTrue(menu.beeColour() == colour,
                "Effect Jar must preserve the complete 24-bit species colour across 16-bit container data");
        helper.assertTrue(menu.ageProgress() == 41, "Effect Jar age progress menu data drifted");
        helper.assertTrue(menu.ticksUntilDeath() == 1234, "Effect Jar time-left menu data drifted");
        helper.assertTrue(menu.isActive(), "Effect Jar active menu data drifted");

        double cx = jarPos.getX() + 0.5D;
        double cy = jarPos.getY() + 0.5D;
        double cz = jarPos.getZ() + 0.5D;
        player.setPos(cx + 7.99D, cy, cz);
        helper.assertTrue(menu.stillValid(player), "Effect Jar menu must remain valid just inside the legacy 8-block range");
        player.setPos(cx + 8.01D, cy, cz);
        helper.assertTrue(!menu.stillValid(player), "Effect Jar menu must close outside the legacy 8-block range");
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void mutationResourcesArePresent(GameTestHelper helper) {
        int checked = 0;
        for (String name : List.of("aware", "spirit_from_ethereal", "spirit_from_attuned", "soul",
                "skulking", "ghastly", "batty", "spidery", "smouldering", "hateful", "brainy", "bigbad",
                "aluminium", "aluminium_british", "ardite", "cobalt", "manyullyn", "electrum", "platinum",
                "osmium_silver_cobalt", "osmium_silver_infernal", "osmium_imperial_cobalt",
                "osmium_imperial_infernal")) {
            String path = "data/magicbees/recipe/bee_mutation/" + name + ".json";
            helper.assertTrue(hasResource(helper, path), "Missing mutation resource " + path);
            checked++;
        }
        helper.assertTrue(checked == 23, "Mutation regression set was not checked completely");
        helper.succeed();
    }

    @GameTest(templateNamespace = MagicBees.MOD_ID, template = "empty")
    public static void customMutationConditionTypesAreRegistered(GameTestHelper helper) {
        for (ResourceLocation id : List.of(MagicBees.id("moon_phase"), MagicBees.id("aura_vis"),
                MagicBees.id("required_block_tag"))) {
            helper.assertTrue(forestry.api.ForestryRegistries.MUTATION_CONDITION_TYPE.containsKey(id),
                    "Custom mutation condition type is not registered in forestry:mutation_condition_type: " + id);
        }
        helper.succeed();
    }

    private static boolean parentIsLive(Map<ResourceLocation, IBeeSpecies> live,
                                        forestry.api.core.genetics.ISpecies<?> parent) {
        return MagicBees.MOD_ID.equals(parent.id().getNamespace())
                ? live.containsKey(parent.id())
                : SpeciesUtil.getBeeSpecies(parent.id()) != null;
    }

    private static boolean reachableParent(Set<ResourceLocation> reachable,
                                           forestry.api.core.genetics.ISpecies<?> parent) {
        return !MagicBees.MOD_ID.equals(parent.id().getNamespace()) || reachable.contains(parent.id());
    }

    private static void assertBotaniaConversion(GameTestHelper helper, String outputPath) {
        LegacyBeeParityData.Conversion expected = LegacyBeeParityData.BOTANIA_CONVERSIONS.get(outputPath);
        helper.assertTrue(expected != null, "Missing Botania acquisition ledger for magicbees:" + outputPath);
        var holder = helper.getLevel().getRecipeManager().byKey(MagicBees.id(expected.recipePath())).orElse(null);
        helper.assertTrue(holder != null, "Missing live Botania conversion recipe for magicbees:" + outputPath);
        try {
            Object recipe = holder.value();
            ResourceLocation input = (ResourceLocation) recipe.getClass().getMethod("inputSpecies").invoke(recipe);
            ResourceLocation output = (ResourceLocation) recipe.getClass().getMethod("outputSpecies").invoke(recipe);
            helper.assertTrue(input.equals(MagicBees.id(expected.inputSpecies())),
                    "Botania conversion input drifted for magicbees:" + outputPath + ": " + input);
            helper.assertTrue(output.equals(MagicBees.id(outputPath)),
                    "Botania conversion output drifted for magicbees:" + outputPath + ": " + output);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not inspect Botania conversion for magicbees:" + outputPath, exception);
        }
    }

    private static void assertCombHasLiveCentrifuge(GameTestHelper helper, RecipeManager recipes,
                                                     ResourceLocation speciesId, IProduct product) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(product.item());
        if (!MagicBees.MOD_ID.equals(itemId.getNamespace()) || !itemId.getPath().startsWith("bee_comb_")) {
            return;
        }
        String comb = itemId.getPath().substring("bee_comb_".length());
        LegacyBeeParityData.Centrifuge expected = LegacyBeeParityData.CENTRIFUGE.get(comb);
        helper.assertTrue(expected != null, "Species produces an unledgered comb: " + speciesId + " -> " + itemId);
        helper.assertTrue(recipes.byKey(MagicBees.id(expected.recipePath())).isPresent(),
                "Species produces a comb without a live centrifuge recipe: " + speciesId + " -> " + itemId);
    }

    private static List<forestry.api.apiculture.hives.IHive> hivesForBlock(
            List<forestry.api.apiculture.hives.IHive> hives, net.minecraft.world.level.block.Block block) {
        return hives.stream().filter(hive -> hive.getHiveBlockState().is(block)).toList();
    }

    private static forestry.api.apiculture.hives.IHive onlyHive(GameTestHelper helper,
            List<forestry.api.apiculture.hives.IHive> hives, net.minecraft.world.level.block.Block block) {
        List<forestry.api.apiculture.hives.IHive> matches = hivesForBlock(hives, block);
        helper.assertTrue(matches.size() == 1, "Expected one hive definition for " + block + ", found " + matches.size());
        return matches.getFirst();
    }

    private static forestry.api.apiculture.hives.IHive hiveWithChance(GameTestHelper helper,
            List<forestry.api.apiculture.hives.IHive> hives, float chance) {
        List<forestry.api.apiculture.hives.IHive> matches = hives.stream()
                .filter(hive -> Math.abs(hive.genChance() - chance) < 0.0001f).toList();
        helper.assertTrue(matches.size() == 1, "Expected one hive definition at generation chance " + chance);
        return matches.getFirst();
    }

    private static void assertHiveChances(GameTestHelper helper, List<forestry.api.apiculture.hives.IHive> hives,
            net.minecraft.world.level.block.Block block, float... expected) {
        List<Float> actual = hivesForBlock(hives, block).stream().map(forestry.api.apiculture.hives.IHive::genChance).sorted().toList();
        List<Float> wanted = new ArrayList<>();
        for (float value : expected) wanted.add(value);
        wanted.sort(Float::compare);
        helper.assertTrue(actual.size() == wanted.size(), "Wrong number of generation descriptions for " + block + ": " + actual);
        for (int i = 0; i < actual.size(); i++) {
            helper.assertTrue(Math.abs(actual.get(i) - wanted.get(i)) < 0.0001f,
                    "Generation chance drift for " + block + ": expected " + wanted + ", got " + actual);
        }
    }

    private static forestry.api.apiculture.hives.IHiveDrop findDrop(GameTestHelper helper,
            List<forestry.api.apiculture.hives.IHiveDrop> drops, double chance) {
        List<forestry.api.apiculture.hives.IHiveDrop> matches = drops.stream()
                .filter(drop -> Math.abs(drop.getChance(helper.getLevel(), helper.absolutePos(BlockPos.ZERO), 0) - chance) < 0.0001)
                .toList();
        helper.assertTrue(matches.size() == 1, "Expected one hive drop at chance " + chance);
        return matches.getFirst();
    }

    private static void assertDropChance(GameTestHelper helper, List<forestry.api.apiculture.hives.IHiveDrop> drops,
            double chance, double ignobleChance) {
        var drop = findDrop(helper, drops, chance);
        helper.assertTrue(Math.abs(drop.getIgnobleChance(helper.getLevel(), helper.absolutePos(BlockPos.ZERO), 0) - ignobleChance) < 0.0001,
                "Hive drop " + chance + " lost ignoble chance " + ignobleChance);
    }

    private static void assertSpeciesEquivalent(GameTestHelper helper, IBeeSpecies expected, IBeeSpecies actual) {
        ResourceLocation id = expected.id();
        helper.assertTrue(expected.getBody() == actual.getBody(), id + " body mismatch");
        helper.assertTrue(expected.getStripes() == actual.getStripes(), id + " stripes mismatch");
        helper.assertTrue(expected.getOutline() == actual.getOutline(), id + " outline mismatch");
        helper.assertTrue(expected.getEscritoireColor() == actual.getEscritoireColor(), id + " escritoire color mismatch");
        helper.assertTrue(expected.getTemperature() == actual.getTemperature(), id + " temperature mismatch");
        helper.assertTrue(expected.getHumidity() == actual.getHumidity(), id + " humidity mismatch");
        helper.assertTrue(expected.isDominant() == actual.isDominant(), id + " dominance mismatch");
        helper.assertTrue(expected.hasGlint() == actual.hasGlint(), id + " glint mismatch");
        helper.assertTrue(expected.isSecret() == actual.isSecret(), id + " secret mismatch");
        helper.assertTrue(expected.getAuthority().equals(actual.getAuthority()), id + " authority mismatch");
        helper.assertTrue(expected.getSpeciesName().equals(actual.getSpeciesName()), id + " binomial mismatch");
        helper.assertTrue(expected.getGenusName().equals(actual.getGenusName()), id + " genus mismatch");
        helper.assertTrue(canonicalProducts(expected.getProducts()).equals(canonicalProducts(actual.getProducts())),
                id + " products mismatch: expected " + canonicalProducts(expected.getProducts())
                        + ", got " + canonicalProducts(actual.getProducts()));
        if (!BeeSpeciesRuntimePatcher.hasRuntimeSpecialties(id.getPath())) {
            helper.assertTrue(canonicalProducts(expected.getSpecialties()).equals(canonicalProducts(actual.getSpecialties())),
                    id + " specialties mismatch: expected " + canonicalProducts(expected.getSpecialties())
                            + ", got " + canonicalProducts(actual.getSpecialties()));
        }
        helper.assertTrue(canonicalGenome(expected.getDefaultGenome()).equals(canonicalGenome(actual.getDefaultGenome())),
                id + " default genome mismatch");
    }

    private static List<String> canonicalProducts(List<forestry.api.core.IProduct> products) {
        List<String> result = new ArrayList<>();
        for (var product : products) {
            var stack = product.createStack();
            result.add(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem())
                    + "|" + stack.getCount()
                    + "|" + stack.getComponentsPatch()
                    + "|" + product.chance());
        }
        return result;
    }

    private static int countItem(Inventory inventory, ItemStack wanted) {
        int count = 0;
        for (ItemStack stack : inventory.items) {
            if (ItemStack.isSameItemSameComponents(stack, wanted)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static Set<String> recipeOutputItemIds(RecipeManager manager, net.minecraft.core.HolderLookup.Provider registries) {
        Set<String> outputs = new TreeSet<>();
        for (RecipeType<?> type : BuiltInRegistries.RECIPE_TYPE) {
            for (RecipeHolder<?> holder : recipesByType(manager, type)) {
                addRecipeOutput(outputs, holder.value().getResultItem(registries));
                if (holder.value() instanceof ICentrifugeRecipe centrifuge) {
                    for (IProduct product : centrifuge.getAllProducts()) {
                        addRecipeOutput(outputs, product.createStack());
                    }
                }
                if (holder.value() instanceof ICarpenterRecipe carpenter) {
                    addRecipeOutput(outputs, carpenter.getCraftingGridRecipe().getResultItem(registries));
                }
            }
        }
        return outputs;
    }

    private static Set<String> beeProductItemIds() {
        Set<String> outputs = new TreeSet<>();
        for (IBeeSpecies species : SpeciesUtil.getAllBeeSpecies()) {
            if (!MagicBees.MOD_ID.equals(species.id().getNamespace())) {
                continue;
            }
            for (IProduct product : species.getProducts()) {
                addRecipeOutput(outputs, product.createStack());
            }
            for (IProduct specialty : species.getSpecialties()) {
                addRecipeOutput(outputs, specialty.createStack());
            }
        }
        return outputs;
    }

    private static boolean isIgnoredHiveItem(Item item, ResourceLocation id) {
        return item instanceof net.minecraft.world.item.BlockItem && id.getPath().endsWith("_hive");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Collection<RecipeHolder<?>> recipesByType(RecipeManager manager, RecipeType<?> type) {
        return (Collection) manager.getAllRecipesFor((RecipeType) type);
    }

    private static void addRecipeOutput(Set<String> outputs, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (MagicBees.MOD_ID.equals(id.getNamespace())) {
            outputs.add(id.toString());
        }
    }

    private static String canonicalGenome(IGenome genome) {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<IChromosome<?>, AllelePair<?>> entry : genome.getChromosomes().entrySet()) {
            Allele<?> active = entry.getValue().active();
            Allele<?> inactive = entry.getValue().inactive();
            lines.add(entry.getKey().id() + "=" + active.value() + ":" + active.dominant()
                    + "|" + inactive.value() + ":" + inactive.dominant());
        }
        lines.sort(String::compareTo);
        return String.join("\n", lines);
    }

    private static boolean hasResource(GameTestHelper helper, String path) {
        return MagicBeesGameTests.class.getClassLoader().getResource(path) != null;
    }

    private static String readResource(GameTestHelper helper, String path) {
        InputStream resource = MagicBeesGameTests.class.getClassLoader().getResourceAsStream(path);
        if (resource == null) {
            throw new AssertionError("Missing resource " + path);
        }
        try (InputStream stream = resource) {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new AssertionError("Unable to read resource " + path, exception);
        }
    }

    private static void assertSpeciesAbsent(GameTestHelper helper, Set<ResourceLocation> speciesIds, String integration) {
        for (ResourceLocation id : speciesIds) {
            helper.assertTrue(findSpecies(id) == null,
                    integration + " species must be absent when its defining integration is not loaded: " + id);
        }
    }

    private static IBeeSpecies findSpecies(ResourceLocation id) {
        for (IBeeSpecies species : SpeciesUtil.getAllBeeSpecies()) {
            if (species.id().equals(id)) return species;
        }
        return null;
    }

    private static IBeeSpecies requireSpecies(GameTestHelper helper, String path) {
        IBeeSpecies species = SpeciesUtil.getBeeSpecies(MagicBees.id(path));
        helper.assertTrue(species != null, "Missing live bee species magicbees:" + path);
        return species;
    }

    private static <V> void assertAllele(GameTestHelper helper, IBeeSpecies species,
                                         IChromosome<V> chromosome, Allele<V> expected) {
        Allele<V> actual = species.getDefaultGenome().getActiveAllele(chromosome);
        helper.assertTrue(expected.equals(actual),
                species.id() + " has wrong " + chromosome.id() + ": expected " + expected + ", got " + actual);
    }

    private static void assertReference(GameTestHelper helper, IBeeSpecies species,
                                        IChromosome<ResourceLocation> chromosome, ResourceLocation expected) {
        ResourceLocation actual = species.getDefaultGenome().getActiveValue(chromosome);
        helper.assertTrue(expected.equals(actual),
                species.id() + " has wrong " + chromosome.id() + ": expected " + expected + ", got " + actual);
    }

    private static void assertOutputs(GameTestHelper helper, IBeeSpecies species,
                                      Map<String, Float> products, Map<String, Float> specialties) {
        helper.assertTrue(species.getProducts().size() == products.size(),
                species.id() + " product count mismatch: expected " + products.size() + ", got " + species.getProducts().size());
        helper.assertTrue(species.getSpecialties().size() == specialties.size(),
                species.id() + " specialty count mismatch: expected " + specialties.size() + ", got " + species.getSpecialties().size());

        for (var product : species.getProducts()) {
            String id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(product.createStack().getItem()).toString();
            Float expectedChance = products.get(id);
            helper.assertTrue(expectedChance != null, species.id() + " has unexpected product " + id);
            helper.assertTrue(Math.abs(product.chance() - expectedChance) < 0.00001f,
                    species.id() + " has wrong product chance for " + id + ": " + product.chance());
        }
        for (var specialty : species.getSpecialties()) {
            String id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(specialty.createStack().getItem()).toString();
            Float expectedChance = specialties.get(id);
            helper.assertTrue(expectedChance != null, species.id() + " has unexpected specialty " + id);
            helper.assertTrue(Math.abs(specialty.chance() - expectedChance) < 0.00001f,
                    species.id() + " has wrong specialty chance for " + id + ": " + specialty.chance());
        }
    }

    private static void assertProduct(GameTestHelper helper, ResourceLocation species, forestry.api.core.IProduct product) {
        helper.assertTrue(product.chance() > 0.0f && product.chance() <= 1.0f,
                "Product chance outside (0, 1]: " + species + " -> " + product.chance());
        helper.assertTrue(!product.createStack().isEmpty(), "Product creates an empty stack: " + species);
    }

    private static void assertSpecialty(GameTestHelper helper, IBeeSpecies species, String itemId, float chance) {
        for (var specialty : species.getSpecialties()) {
            String actual = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(specialty.createStack().getItem()).toString();
            if (actual.equals(itemId)) {
                helper.assertTrue(Math.abs(specialty.chance() - chance) < 0.00001f,
                        species.id() + " has wrong specialty chance for " + itemId + ": " + specialty.chance());
                return;
            }
        }
        helper.fail(species.id() + " is missing specialty " + itemId);
    }
}

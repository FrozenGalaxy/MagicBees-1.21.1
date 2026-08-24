package magicbees.forestry;

import forestry.api.apiculture.ForestryActivityTypes;
import forestry.api.apiculture.ForestryBeeEffects;
import forestry.api.apiculture.ForestryFlowerTypes;
import forestry.api.core.HumidityType;
import forestry.api.core.TemperatureType;
import forestry.api.core.genetics.alleles.Allele;
import forestry.api.core.genetics.alleles.BeeChromosomes;
import forestry.api.core.genetics.alleles.ForestryAlleles;
import forestry.api.plugin.IApicultureRegistration;
import forestry.api.plugin.IBeeSpeciesBuilder;
import magicbees.MagicBees;
import forestry.apiculture.bees.genetics.effects.PotionBeeEffect;
import magicbees.forestry.effect.SpawnMobBeeEffect;
import magicbees.forestry.effect.WorldTransformBeeEffect;
import magicbees.registry.MagicBeesItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

/**
 * Code registrations for the first complete Magic Bees breeding families.
 *
 * The old 1.12 primary species color maps to ForestryCE's outline tint. The
 * old branch/species secondary color maps to the body tint. ForestryCE added
 * a separately tintable stripe layer; leaving it at Forestry's black default
 * preserves a two-colour presentation instead of inventing a third MB colour.
 */
public final class MagicBeeSpecies {
    public static final ResourceLocation MYSTICAL = MagicBees.id("mystical");
    public static final ResourceLocation SORCEROUS = MagicBees.id("sorcerous");
    public static final ResourceLocation UNUSUAL = MagicBees.id("unusual");
    public static final ResourceLocation ATTUNED = MagicBees.id("attuned");
    public static final ResourceLocation ELDRITCH = MagicBees.id("eldritch");

    public static final ResourceLocation ESOTERIC = MagicBees.id("esoteric");
    public static final ResourceLocation MYSTERIOUS = MagicBees.id("mysterious");
    public static final ResourceLocation ARCANE = MagicBees.id("arcane");

    public static final ResourceLocation CHARMED = MagicBees.id("charmed");
    public static final ResourceLocation ENCHANTED = MagicBees.id("enchanted");
    public static final ResourceLocation SUPERNATURAL = MagicBees.id("supernatural");
        public static final ResourceLocation INFERNAL = MagicBees.id("infernal");
        public static final ResourceLocation OBLIVION = MagicBees.id("oblivion");
        public static final ResourceLocation ETHEREAL = MagicBees.id("ethereal");
        public static final ResourceLocation WATERY = MagicBees.id("watery");
        public static final ResourceLocation EARTHY = MagicBees.id("earthy");
        public static final ResourceLocation FIREY = MagicBees.id("firey");
        public static final ResourceLocation WINDY = MagicBees.id("windy");
        public static final ResourceLocation PUPIL = MagicBees.id("pupil");
        public static final ResourceLocation SCHOLARLY = MagicBees.id("scholarly");
        public static final ResourceLocation SAVANT = MagicBees.id("savant");
        public static final ResourceLocation GHASTLY = MagicBees.id("ghastly");
        public static final ResourceLocation IRON = MagicBees.id("iron");
        public static final ResourceLocation AWARE = MagicBees.id("aware");
        public static final ResourceLocation SPIRIT = MagicBees.id("spirit");
        public static final ResourceLocation SOUL = MagicBees.id("soul");
        public static final ResourceLocation SKULKING = MagicBees.id("skulking");
        public static final ResourceLocation SPIDERY = MagicBees.id("spidery");
        public static final ResourceLocation SMOULDERING = MagicBees.id("smouldering");
        public static final ResourceLocation BATTY = MagicBees.id("batty");
        public static final ResourceLocation HATEFUL = MagicBees.id("hateful");
        public static final ResourceLocation BRAINY = MagicBees.id("brainy");
        public static final ResourceLocation BIGBAD = MagicBees.id("bigbad");

        public static final ResourceLocation INVISIBLE = MagicBees.id("invisible");
        public static final ResourceLocation CHICKEN = MagicBees.id("chicken");
        public static final ResourceLocation BEEF = MagicBees.id("beef");
        public static final ResourceLocation PORK = MagicBees.id("pork");
        public static final ResourceLocation SHEEPISH = MagicBees.id("sheepish");
        public static final ResourceLocation HORSE = MagicBees.id("horse");
        public static final ResourceLocation CATTY = MagicBees.id("catty");
        public static final ResourceLocation TIMELY = MagicBees.id("timely");
        public static final ResourceLocation LORDLY = MagicBees.id("lordly");
        public static final ResourceLocation DOCTORAL = MagicBees.id("doctoral");
        public static final ResourceLocation SPITEFUL = MagicBees.id("spiteful");
        public static final ResourceLocation WITHERING = MagicBees.id("withering");
        public static final ResourceLocation NAMELESS = MagicBees.id("nameless");
        public static final ResourceLocation ABANDONED = MagicBees.id("abandoned");
        public static final ResourceLocation FORLORN = MagicBees.id("forlorn");
        public static final ResourceLocation DRACONIC = MagicBees.id("draconic");
        public static final ResourceLocation MUTABLE = MagicBees.id("mutable");
        public static final ResourceLocation TRANSMUTING = MagicBees.id("transmuting");
        public static final ResourceLocation CRUMBLING = MagicBees.id("crumbling");
        public static final ResourceLocation GOLD = MagicBees.id("gold");
        public static final ResourceLocation COPPER = MagicBees.id("copper");
        public static final ResourceLocation TIN = MagicBees.id("tin");
        public static final ResourceLocation SILVER = MagicBees.id("silver");
        public static final ResourceLocation LEAD = MagicBees.id("lead");
        public static final ResourceLocation ALUMINIUM = MagicBees.id("aluminium");
        public static final ResourceLocation ARDITE = MagicBees.id("ardite");
        public static final ResourceLocation COBALT = MagicBees.id("cobalt");
        public static final ResourceLocation MANYULLYN = MagicBees.id("manyullyn");
        public static final ResourceLocation OSMIUM = MagicBees.id("osmium");
        public static final ResourceLocation ELECTRUM = MagicBees.id("electrum");
        public static final ResourceLocation PLATINUM = MagicBees.id("platinum");
        public static final ResourceLocation NICKEL = MagicBees.id("nickel");
        public static final ResourceLocation INVAR = MagicBees.id("invar");
        public static final ResourceLocation BRONZE = MagicBees.id("bronze");
        public static final ResourceLocation DIAMOND = MagicBees.id("diamond");
        public static final ResourceLocation EMERALD = MagicBees.id("emerald");
        public static final ResourceLocation APATITE = MagicBees.id("apatite");
        public static final ResourceLocation SILICON = MagicBees.id("silicon");
        public static final ResourceLocation CERTUS = MagicBees.id("certus");
        public static final ResourceLocation FLUIX = MagicBees.id("fluix");
        public static final ResourceLocation AE_SKYSTONE = MagicBees.id("ae_skystone");

        public static final ResourceLocation TE_BLIZZY = MagicBees.id("te_blizzy");
        public static final ResourceLocation TE_GELID = MagicBees.id("te_gelid");
        public static final ResourceLocation TE_DANTE = MagicBees.id("te_dante");
        public static final ResourceLocation TE_PYRO = MagicBees.id("te_pyro");
        public static final ResourceLocation TE_SHOCKING = MagicBees.id("te_shocking");
        public static final ResourceLocation TE_AMPED = MagicBees.id("te_amped");
        public static final ResourceLocation TE_GROUNDED = MagicBees.id("te_grounded");
        public static final ResourceLocation TE_ROCKING = MagicBees.id("te_rocking");
        public static final ResourceLocation TE_COAL = MagicBees.id("te_coal");
        public static final ResourceLocation TE_DESTABILIZED = MagicBees.id("te_destabilized");
        public static final ResourceLocation TE_LUX = MagicBees.id("te_lux");

        public static final ResourceLocation BOT_ROOTED = MagicBees.id("bot_rooted");
        public static final ResourceLocation BOT_BOTANIC = MagicBees.id("bot_botanic");
        public static final ResourceLocation BOT_BLOSSOM = MagicBees.id("bot_blossom");
        public static final ResourceLocation BOT_FLORAL = MagicBees.id("bot_floral");
        public static final ResourceLocation BOT_VAZBEE = MagicBees.id("bot_vazbee");
        public static final ResourceLocation BOT_SOMNOLENT = MagicBees.id("bot_somnolent");
        public static final ResourceLocation BOT_DREAMING = MagicBees.id("bot_dreaming");
        public static final ResourceLocation BOT_ALFHEIM = MagicBees.id("bot_alfheim");

    private MagicBeeSpecies() {
    }

    public static void registerInitial(IApicultureRegistration apiculture) {
        apiculture.registerBeeEffect(MagicBees.id("spawn_bat"),
                new SpawnMobBeeEffect(net.minecraft.world.entity.EntityType.BAT, 150, 100, 5));
        apiculture.registerBeeEffect(MagicBees.id("spawn_spider"),
                new SpawnMobBeeEffect(net.minecraft.world.entity.EntityType.SPIDER, 400, 70, 4));
        apiculture.registerBeeEffect(MagicBees.id("spawn_blaze"),
                new SpawnMobBeeEffect(net.minecraft.world.entity.EntityType.BLAZE, 800, 60, 2));
        apiculture.registerBeeEffect(MagicBees.id("spawn_ghast"),
                new SpawnMobBeeEffect(net.minecraft.world.entity.EntityType.GHAST, 2060, 10, 1));
        apiculture.registerBeeEffect(MagicBees.id("spawn_zombie"),
                new SpawnMobBeeEffect(net.minecraft.world.entity.EntityType.ZOMBIE, 800, 100, 2, true));
        apiculture.registerBeeEffect(MagicBees.id("spawn_wolf"),
                new SpawnMobBeeEffect(net.minecraft.world.entity.EntityType.WOLF, 650, 40, 2));

        apiculture.registerBeeEffect(MagicBees.id("spawn_chicken"),
                new SpawnMobBeeEffect(net.minecraft.world.entity.EntityType.CHICKEN, 20, 100, 20));
        apiculture.registerBeeEffect(MagicBees.id("spawn_cow"),
                new SpawnMobBeeEffect(net.minecraft.world.entity.EntityType.COW, 640, 100, 3));
        apiculture.registerBeeEffect(MagicBees.id("spawn_pig"),
                new SpawnMobBeeEffect(net.minecraft.world.entity.EntityType.PIG, 350, 100, 4));
        apiculture.registerBeeEffect(MagicBees.id("spawn_sheep"),
                new SpawnMobBeeEffect(net.minecraft.world.entity.EntityType.SHEEP, 450, 100, 5));
        apiculture.registerBeeEffect(MagicBees.id("spawn_horse"),
                new SpawnMobBeeEffect(net.minecraft.world.entity.EntityType.HORSE, 450, 59, 2));
        apiculture.registerBeeEffect(MagicBees.id("spawn_cat"),
                new SpawnMobBeeEffect(net.minecraft.world.entity.EntityType.CAT, 702, 60, 2));
        // Legacy Thermal Foundation mob effects. Resolve entity IDs lazily so Magic Bees can still load without Thermal.
        apiculture.registerBeeEffect(MagicBees.id("spawn_blizz"),
                new SpawnMobBeeEffect(ResourceLocation.fromNamespaceAndPath("thermal", "blizz"), 100, 80, 5));
        apiculture.registerBeeEffect(MagicBees.id("spawn_blitz"),
                new SpawnMobBeeEffect(ResourceLocation.fromNamespaceAndPath("thermal", "blitz"), 100, 80, 5));
        apiculture.registerBeeEffect(MagicBees.id("spawn_basalz"),
                new SpawnMobBeeEffect(ResourceLocation.fromNamespaceAndPath("thermal", "basalz"), 100, 80, 5));
        apiculture.registerBeeEffect(MagicBees.id("effect_invisibility"),
                new PotionBeeEffect(false, MobEffects.INVISIBILITY, 10 * 20));
        apiculture.registerBeeEffect(MagicBees.id("effect_slow_speed"),
                new PotionBeeEffect(false, MobEffects.MOVEMENT_SLOWDOWN, 3 * 20));
        apiculture.registerBeeEffect(MagicBees.id("effect_withering"),
                new PotionBeeEffect(true, MobEffects.WITHER, 15 * 20));
        apiculture.registerBeeEffect(MagicBees.id("effect_transmuting"),
                new WorldTransformBeeEffect(WorldTransformBeeEffect.Mode.TRANSMUTING, 200));
        apiculture.registerBeeEffect(MagicBees.id("effect_crumbling"),
                new WorldTransformBeeEffect(WorldTransformBeeEffect.Mode.CRUMBLING, 600));
        apiculture.registerBeeEffect(MagicBees.id("effect_dreaming"),
                new WorldTransformBeeEffect(WorldTransformBeeEffect.Mode.DREAMING, 40));

        register(apiculture, MYSTICAL, MagicBeeTaxa.VEILED, "mysticum", true, 0xAFFFB7)
                .addProduct(stack(MagicBeesItems.comb("mundane")), 0.15f);

        register(apiculture, SORCEROUS, MagicBeeTaxa.VEILED, "fascinatio", true, 0xEA9A9A)
                .setTemperature(TemperatureType.HOT)
                .setHumidity(HumidityType.ARID)
                .setGenome(genome -> {
                    genome.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_3);
                    genome.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_DOWN_2);
                    genome.set(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_UP_1);
                })
                .addProduct(stack(MagicBeesItems.comb("mundane")), 0.15f);

        register(apiculture, UNUSUAL, MagicBeeTaxa.VEILED, "inusitatus", true, 0x72D361)
                .setGenome(genome -> genome.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_2))
                .addProduct(stack(MagicBeesItems.comb("mundane")), 0.15f);

        register(apiculture, ATTUNED, MagicBeeTaxa.VEILED, "similis", true, 0x0086A8)
                .setGenome(genome -> genome.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_3))
                .addProduct(stack(MagicBeesItems.comb("mundane")), 0.15f);

        register(apiculture, ELDRITCH, MagicBeeTaxa.VEILED, "prodigiosus", true, 0x8D75A0)
                .setGenome(genome -> genome.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOWER))
                .addProduct(stack(MagicBeesItems.comb("mundane")), 0.15f);

        registerSecret(apiculture, ESOTERIC, MagicBeeTaxa.ARCANE, "secretiore", true, 0x001099)
                .addProduct(stack(MagicBeesItems.comb("occult")), 0.18f);

        registerSecret(apiculture, MYSTERIOUS, MagicBeeTaxa.ARCANE, "mysticus", true, 0x762BC2)
                .setGenome(genome -> genome.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_2))
                .addProduct(stack(MagicBeesItems.comb("occult")), 0.20f);

        registerSecret(apiculture, ARCANE, MagicBeeTaxa.ARCANE, "arcanus", true, 0xD242DF)
                .setGenome(genome -> {
                    genome.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_2);
                    // 1.12 NORMAL fertility = 2 offspring.
                    genome.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_2);
                    genome.set(BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_AVERAGE);
                })
                .setGlint(true)
                .addProduct(stack(MagicBeesItems.comb("occult")), 0.25f)
                .addSpecialty(stack(MagicBeesItems.drop("enchanted")), 0.09f);

        registerSecret(apiculture, CHARMED, MagicBeeTaxa.SUPERNATURAL, "larvatus", true, 0x48EEEC)
                .addProduct(stack(MagicBeesItems.comb("otherworldly")), 0.18f);

        registerSecret(apiculture, ENCHANTED, MagicBeeTaxa.SUPERNATURAL, "cantatus", true, 0x18E726)
                .setGenome(genome -> genome.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1))
                .addProduct(stack(MagicBeesItems.comb("otherworldly")), 0.20f);

        registerSecret(apiculture, SUPERNATURAL, MagicBeeTaxa.SUPERNATURAL, "coeleste", true, 0x005614)
                .setGenome(genome -> {
                    genome.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_2);
                    genome.set(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
                })
                .setGlint(true)
                .addProduct(stack(MagicBeesItems.comb("otherworldly")), 0.25f)
                .addSpecialty(stack(MagicBeesItems.pollen("unusual")), 0.08f);

        registerAlwaysSecret(apiculture, GHASTLY, MagicBeeTaxa.SKULKING, "pallens", false, 0xCCCcee, 0xBF877C)
                .setGenome(genome -> {
                    genome.set(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
                    genome.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
                    genome.set(BeeChromosomes.TOLERATES_RAIN, ForestryAlleles.TRUE_RECESSIVE);
                    genome.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_1);
                                        genome.set(BeeChromosomes.EFFECT, MagicBees.id("spawn_ghast"));
                })
                .addProduct(stack(MagicBeesItems.comb("furtive")), 0.08f)
                .addSpecialty(new ItemStack(net.minecraft.world.item.Items.GHAST_TEAR), 0.099f);

        IBeeSpeciesBuilder iron = register(apiculture, IRON, MagicBeeTaxa.METALLIC, "ferrus", true, 0x686868, 0xE9E9E9);
        addOreProduct(iron, 0.18f, stack(MagicBeesItems.nugget("iron")));

        register(apiculture, AWARE, MagicBeeTaxa.SOUL, "sensibilis", false, 0x5E95B5)
                .addProduct(stack(MagicBeesItems.comb("intellect")), 0.18f);

        register(apiculture, SPIRIT, MagicBeeTaxa.SOUL, "larva", true, 0xB2964B)
                .setGenome(genome -> genome.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_SHORTENED))
                .addProduct(stack(MagicBeesItems.comb("intellect")), 0.22f)
                .addSpecialty(stack(MagicBeesItems.comb("soul")), 0.16f);

        register(apiculture, SOUL, MagicBeeTaxa.SOUL, "anima", false, 0x7D591B)
                .setGenome(genome -> {
                    genome.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_DOWN_2);
                    genome.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_NORMAL);
                })
                .setGlint(true)
                .addProduct(stack(MagicBeesItems.comb("intellect")), 0.28f)
                .addSpecialty(stack(MagicBeesItems.comb("soul")), 0.20f);

        registerAlwaysSecret(apiculture, SKULKING, MagicBeeTaxa.SKULKING, "malevolens", true, 0x524827)
                .setGenome(genome -> genome.set(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE))
                .addProduct(stack(MagicBeesItems.comb("furtive")), 0.10f);

        registerAlwaysSecret(apiculture, SPIDERY, MagicBeeTaxa.SKULKING, "araneolus", true, 0x888888, 0x222222)
                .setGenome(genome -> {
                    genome.set(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
                    genome.set(BeeChromosomes.TERRITORY, ForestryAlleles.TERRITORY_LARGER);
                                        genome.set(BeeChromosomes.EFFECT, MagicBees.id("spawn_spider"));
                })
                .addProduct(stack(MagicBeesItems.comb("furtive")), 0.13f)
                .addProduct(new ItemStack(net.minecraft.world.item.Items.STRING), 0.08f)
                .addSpecialty(new ItemStack(net.minecraft.world.item.Items.SPIDER_EYE), 0.08f);

        registerAlwaysSecret(apiculture, SMOULDERING, MagicBeeTaxa.SKULKING, "flagrantia", false, 0xFFC747, 0xEA8344)
                .setTemperature(TemperatureType.HELLISH)
                                .setGenome(genome -> {
                                        genome.set(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
                                        genome.set(BeeChromosomes.EFFECT, MagicBees.id("spawn_blaze"));
                                })
                .addProduct(stack(MagicBeesItems.comb("furtive")), 0.10f)
                .addProduct(stack(MagicBeesItems.comb("molten")), 0.10f)
                .addSpecialty(new ItemStack(net.minecraft.world.item.Items.BLAZE_ROD), 0.05f);

        register(apiculture, BATTY, MagicBeeTaxa.FLESHY, "chiroptera", true, 0x5B482B, 0x271B0F)
                .setGenome(genome -> {
                    genome.set(BeeChromosomes.TERRITORY, ForestryAlleles.TERRITORY_LARGE);
                    genome.set(BeeChromosomes.EFFECT, MagicBees.id("spawn_bat"));
                })
                .addProduct(stack(MagicBeesItems.comb("furtive")), 0.10f)
                .addSpecialty(new ItemStack(net.minecraft.world.item.Items.STRING), 0.00001f);

                register(apiculture, HATEFUL, MagicBeeTaxa.ABOMINABLE, "odibilis", false, 0xDB00DB)
                                .setGenome(genome -> {
                                        genome.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOW);
                                        genome.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_ELONGATED);
                                        genome.set(BeeChromosomes.TERRITORY, ForestryAlleles.TERRITORY_LARGER);
                                        genome.set(BeeChromosomes.EFFECT, forestry.api.apiculture.ForestryBeeEffects.MISANTHROPE);
                                })
                                .addProduct(stack(MagicBeesItems.comb("molten")), 0.18f);

                registerAlwaysSecret(apiculture, BRAINY, MagicBeeTaxa.SKULKING, "cerebrum", true, 0x83FF70)
                                .setGenome(genome -> {
                                        genome.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_1);
                                        genome.set(BeeChromosomes.EFFECT, MagicBees.id("spawn_zombie"));
                                })
                                .addProduct(stack(MagicBeesItems.comb("furtive")), 0.10f)
                                .addProduct(new ItemStack(net.minecraft.world.item.Items.ROTTEN_FLESH), 0.06f);

                register(apiculture, BIGBAD, MagicBeeTaxa.SKULKING, "magnumalum", true, 0xA9344B, 0x453536)
                                .setGenome(genome -> {
                                        genome.set(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
                                        genome.set(BeeChromosomes.EFFECT, MagicBees.id("spawn_wolf"));
                                        genome.set(BeeChromosomes.ACTIVITY, forestry.api.apiculture.ForestryActivityTypes.NOCTURNAL);
                                })
                                .addProduct(stack(MagicBeesItems.comb("furtive")), 0.18f)
                                .addProduct(new ItemStack(net.minecraft.world.item.Items.BEEF), 0.12f)
                                .addProduct(new ItemStack(net.minecraft.world.item.Items.CHICKEN), 0.12f);

        register(apiculture, INFERNAL, MagicBeeTaxa.ABOMINABLE, "infernales", true, 0xFF1C1C)
                .setGenome(genome -> genome.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOW))
                .addProduct(stack(MagicBeesItems.comb("molten")), 0.12f);

        register(apiculture, OBLIVION, MagicBeeTaxa.EXTRINSIC, "oblivioni", false, 0xD5C3E5)
                .setGenome(genome -> genome.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_4))
                .addProduct(stack(MagicBeesItems.comb("forgotten")), 0.14f);

        register(apiculture, ETHEREAL, MagicBeeTaxa.MAGICAL, "diaphanum", true, 0xBA3B3B, 0xEFF8FF)
                .setGenome(genome -> {
                    genome.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_SHORTENED);
                    genome.set(BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_AVERAGE);
                })
                .addProduct(stack(MagicBeesItems.comb("occult")), 0.10f)
                .addProduct(stack(MagicBeesItems.comb("otherworldly")), 0.10f);

        register(apiculture, WATERY, MagicBeeTaxa.MAGICAL, "aquatilis", true, 0x313C5E)
                .setGenome(genome -> {
                    genome.set(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_UP_1);
                    genome.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_DOWN_2);
                    genome.set(BeeChromosomes.FLOWER_TYPE, ForestryFlowerTypes.SNOW);
                })
                .addProduct(stack(MagicBeesItems.comb("watery")), 0.25f)
                .addSpecialty(new ItemStack(net.minecraft.world.level.block.Blocks.ICE), 0.025f);

        register(apiculture, EARTHY, MagicBeeTaxa.MAGICAL, "fictili", true, 0x78822D)
                .setGenome(genome -> {
                    genome.set(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1);
                    genome.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_LONG);
                })
                .addProduct(stack(MagicBeesItems.comb("earthy")), 0.25f);

        register(apiculture, FIREY, MagicBeeTaxa.MAGICAL, "ardens", true, 0xD35119)
                .setGenome(genome -> {
                    genome.set(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_DOWN_1);
                    genome.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_UP_2);
                    genome.set(BeeChromosomes.FLOWER_TYPE, ForestryFlowerTypes.CACTI);
                })
                .addProduct(stack(MagicBeesItems.comb("firey")), 0.25f);

        register(apiculture, WINDY, MagicBeeTaxa.MAGICAL, "ventosum", true, 0xFFFDBA)
                .setGenome(genome -> {
                    genome.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_2);
                    genome.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_FASTER);
                    genome.set(BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_FASTER);
                })
                .addProduct(stack(MagicBeesItems.comb("airy")), 0.25f);

        register(apiculture, PUPIL, MagicBeeTaxa.SCHOLARLY, "disciplina", true, 0xFFFF00)
                .setGenome(genome -> genome.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOWER))
                .addProduct(stack(MagicBeesItems.comb("papery")), 0.20f);

        IBeeSpeciesBuilder scholarly = register(apiculture, SCHOLARLY, MagicBeeTaxa.SCHOLARLY,
                "studiosis", false, 0x6E0000)
                .setGenome(genome -> genome.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_1))
                .addProduct(stack(MagicBeesItems.comb("papery")), 0.25f);
        // ForestryCE alpha7 cannot condition one species specialty on an optional mod; the runtime patcher restores this when safe.

        IBeeSpeciesBuilder savant = register(apiculture, SAVANT, MagicBeeTaxa.SCHOLARLY,
                "philologus", false, 0xFFA042)
                .setGenome(genome -> {
                    genome.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_NORMAL);
                    genome.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_1);
                })
                .setGlint(true)
                .addProduct(stack(MagicBeesItems.comb("papery")), 0.40f);
        // ForestryCE alpha7 cannot condition one species specialty on an optional mod; the runtime patcher restores this when safe.

        // #7 - Invisible and remaining Fleshy family.
        register(apiculture, INVISIBLE, MagicBeeTaxa.VEILED, "invisible", false, 0xFFCCFF, 0xFFCCFF)
                .setGenome(genome -> {
                    genome.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_SHORTEST);
                    genome.set(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_UP_1);
                    genome.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_DOWN_2);
                    genome.set(BeeChromosomes.EFFECT, MagicBees.id("effect_invisibility"));
                }).addProduct(stack(MagicBeesItems.comb("mundane")), 0.35f);
        register(apiculture, CHICKEN, MagicBeeTaxa.FLESHY, "pullus", true, 0xFF0000, 0xD3D3D3)
                .setGenome(g -> g.set(BeeChromosomes.EFFECT, MagicBees.id("spawn_chicken")))
                .addProduct(stack(MagicBeesItems.comb("furtive")), 0.23f)
                .addSpecialty(new ItemStack(net.minecraft.world.item.Items.FEATHER), 0.08f)
                .addProduct(new ItemStack(net.minecraft.world.item.Items.EGG), 0.08f);
        register(apiculture, BEEF, MagicBeeTaxa.FLESHY, "bubulae", true, 0xB7B7B7, 0x3F3024)
                .setGenome(g -> g.set(BeeChromosomes.EFFECT, MagicBees.id("spawn_cow")))
                .addProduct(stack(MagicBeesItems.comb("furtive")), 0.25f)
                .addSpecialty(new ItemStack(net.minecraft.world.item.Items.LEATHER), 0.165f);
        register(apiculture, PORK, MagicBeeTaxa.FLESHY, "porcina", true, 0xF1AEAC, 0xDF847B)
                .setGenome(g -> g.set(BeeChromosomes.EFFECT, MagicBees.id("spawn_pig")))
                .addProduct(stack(MagicBeesItems.comb("furtive")), 0.10f)
                .addSpecialty(new ItemStack(net.minecraft.world.item.Items.CARROT), 0.165f);
        register(apiculture, SHEEPISH, MagicBeeTaxa.FLESHY, "balans", true, 0xF7F7F7, 0xCACACA)
                .setGenome(g -> { g.set(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE); g.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_DOWN_2); g.set(BeeChromosomes.EFFECT, MagicBees.id("spawn_sheep")); })
                .addProduct(stack(MagicBeesItems.comb("furtive")), 0.25f)
                .addSpecialty(new ItemStack(net.minecraft.world.level.block.Blocks.WHITE_WOOL), 0.16f)
                .addSpecialty(new ItemStack(net.minecraft.world.item.Items.WHEAT), 0.24f);
        register(apiculture, HORSE, MagicBeeTaxa.FLESHY, "equus", true, 0x906330, 0x7B4E1B)
                .setGenome(g -> { g.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_FASTEST); g.set(BeeChromosomes.EFFECT, MagicBees.id("spawn_horse")); })
                .addProduct(stack(MagicBeesItems.comb("furtive")), 0.25f)
                .addSpecialty(new ItemStack(net.minecraft.world.item.Items.LEATHER), 0.24f)
                .addSpecialty(new ItemStack(net.minecraft.world.item.Items.APPLE), 0.38f);
        register(apiculture, CATTY, MagicBeeTaxa.FLESHY, "feline", true, 0xECE684, 0x563C24)
                .setTemperature(TemperatureType.HOT)
                .setGenome(g -> { g.set(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE); g.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_3); g.set(BeeChromosomes.EFFECT, MagicBees.id("spawn_cat")); })
                .addProduct(stack(MagicBeesItems.comb("furtive")), 0.25f)
                .addSpecialty(new ItemStack(net.minecraft.world.item.Items.COD), 0.24f);

        // #8 - Time family.
        register(apiculture, TIMELY, MagicBeeTaxa.TIME, "gallifreis", true, 0xC6AF86)
                .setGenome(g -> { g.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_ELONGATED); g.set(BeeChromosomes.EFFECT, MagicBees.id("effect_slow_speed")); })
                .addProduct(stack(MagicBeesItems.comb("temporal")), 0.16f);
        register(apiculture, LORDLY, MagicBeeTaxa.TIME, "rassilonis", false, 0xC6AF86, 0x8E0213)
                .setGenome(g -> { g.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_LONG); g.set(BeeChromosomes.ACTIVITY, MagicBeeActivityTypes.NEVER_SLEEPS); g.set(BeeChromosomes.EFFECT, ForestryBeeEffects.DRUNKARD); })
                .addProduct(stack(MagicBeesItems.comb("temporal")), 0.19f);
        register(apiculture, DOCTORAL, MagicBeeTaxa.TIME, "medicus qui", false, 0xDDE5FC, 0x4B6E8C)
                .setGenome(g -> { g.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_3); g.set(BeeChromosomes.TERRITORY, ForestryAlleles.TERRITORY_LARGE); g.set(BeeChromosomes.ACTIVITY, MagicBeeActivityTypes.NEVER_SLEEPS); g.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_LONGEST); g.set(BeeChromosomes.EFFECT, ForestryBeeEffects.HEROIC); })
                .addProduct(stack(MagicBeesItems.comb("temporal")), 0.24f)
                .addSpecialty(new ItemStack(MagicBeesItems.JELLY_BABIES.get()), 0.078f);

        // #9 - remaining core Nether/End progression.
        register(apiculture, SPITEFUL, MagicBeeTaxa.ABOMINABLE, "maligna", false, 0x5FCC00)
                .setGenome(g -> { g.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_LONG); g.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_NORMAL); g.set(BeeChromosomes.TERRITORY, ForestryAlleles.TERRITORY_LARGER); g.set(BeeChromosomes.EFFECT, ForestryBeeEffects.MISANTHROPE); }).setGlint(true)
                .addProduct(stack(MagicBeesItems.comb("molten")), 0.23f);
        register(apiculture, WITHERING, MagicBeeTaxa.ABOMINABLE, "vietus", false, 0x5B5B5B)
                .setGenome(g -> { g.set(BeeChromosomes.TERRITORY, ForestryAlleles.TERRITORY_LARGEST); g.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_1); g.set(BeeChromosomes.EFFECT, MagicBees.id("effect_withering")); }).setGlint(true)
                .addSpecialty(stack(MagicBeesItems.resource("skull_chip")), 0.15f);
        register(apiculture, NAMELESS, MagicBeeTaxa.EXTRINSIC, "sine nomine", true, 0x8CA7CB)
                .setGenome(g -> { g.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_3); g.set(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1); }).addProduct(stack(MagicBeesItems.comb("forgotten")), 0.19f);
        register(apiculture, ABANDONED, MagicBeeTaxa.EXTRINSIC, "reliquit", true, 0xC5CB8C)
                .setGenome(g -> { g.set(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1); g.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_ELONGATED); g.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_2); g.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOW); g.set(BeeChromosomes.EFFECT, ForestryBeeEffects.REPULSION); }).addProduct(stack(MagicBeesItems.comb("forgotten")), 0.24f);
        register(apiculture, FORLORN, MagicBeeTaxa.EXTRINSIC, "perditus", false, 0xCBA88C)
                .setGenome(g -> { g.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_1); g.set(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1); g.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_LONGEST); g.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOW); g.set(BeeChromosomes.EFFECT, ForestryBeeEffects.REPULSION); }).setGlint(true).addProduct(stack(MagicBeesItems.comb("forgotten")), 0.30f);
        register(apiculture, DRACONIC, MagicBeeTaxa.EXTRINSIC, "draconic", false, 0x9F56AD, 0x5A3B62)
                .setGenome(g -> { g.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_1); g.set(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_1); g.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_LONGEST); g.set(BeeChromosomes.EFFECT, ForestryBeeEffects.MISANTHROPE); }).setGlint(true)
                .addProduct(stack(MagicBeesItems.resource("dragon_dust")), 0.15f);

        // #10 - transmuting family.
        register(apiculture, MUTABLE, MagicBeeTaxa.TRANSMUTING, "mutable", false, 0xDBB24C, 0xE0D5A6)
                .addProduct(forestryComb("parched"), 0.30f).addProduct(stack(MagicBeesItems.comb("transmuted")), 0.10f);
        register(apiculture, TRANSMUTING, MagicBeeTaxa.TRANSMUTING, "transmuting", false, 0xDBB24C, 0xA2D2D8)
                .setGenome(g -> g.set(BeeChromosomes.EFFECT, MagicBees.id("effect_transmuting")))
                .addProduct(forestryComb("parched"), 0.10f).addProduct(stack(MagicBeesItems.comb("transmuted")), 0.30f).addProduct(forestryComb("silky"), 0.05f).addProduct(forestryComb("simmering"), 0.05f);
        register(apiculture, CRUMBLING, MagicBeeTaxa.TRANSMUTING, "crumbling", false, 0xDBB24C, 0xDBA4A4)
                .setTemperature(TemperatureType.HOT).setHumidity(HumidityType.ARID)
                .setGenome(g -> g.set(BeeChromosomes.EFFECT, MagicBees.id("effect_crumbling")))
                .addProduct(forestryComb("parched"), 0.10f).addProduct(stack(MagicBeesItems.comb("transmuted")), 0.30f).addProduct(forestryComb("powdery"), 0.10f).addProduct(forestryComb("cocoa"), 0.15f);

        // #11 - Metallic and Gem families. Legacy OreDictionary-only outputs are not invented.
        IBeeSpeciesBuilder gold = register(apiculture, GOLD, MagicBeeTaxa.METALLIC, "aurum", false, 0x684B01, 0xFFFF0B).setGlint(true); addOreProduct(gold, 0.16f, new ItemStack(net.minecraft.world.item.Items.GOLD_NUGGET));
        IBeeSpeciesBuilder copper = register(apiculture, COPPER, MagicBeeTaxa.METALLIC, "aercus", true, 0x684B01, 0xFFC81A); addOreProduct(copper, 0.20f, stack(MagicBeesItems.nugget("copper")));
        IBeeSpeciesBuilder tin = register(apiculture, TIN, MagicBeeTaxa.METALLIC, "stannum", true, 0x3E596D, 0xA6BACB); addOreProduct(tin, 0.20f, stack(MagicBeesItems.nugget("tin")));
        addOreProduct(register(apiculture, SILVER, MagicBeeTaxa.METALLIC, "argenteus", false, 0x747C81, 0x96BFC4), 0.16f, ItemStack.EMPTY);
        addOreProduct(register(apiculture, LEAD, MagicBeeTaxa.METALLIC, "plumbeus", true, 0x96BFC4, 0x91A9F3), 0.17f, ItemStack.EMPTY);
        addOreProduct(register(apiculture, ALUMINIUM, MagicBeeTaxa.METALLIC, "aluminium", true, 0xEDEDED, 0x767676), 0.20f, ItemStack.EMPTY);
        addOreProduct(register(apiculture, ARDITE, MagicBeeTaxa.METALLIC, "aurantiaco", false, 0x720000, 0xFF9E00).setTemperature(TemperatureType.HOT).setHumidity(HumidityType.ARID), 0.18f, ItemStack.EMPTY);
        addOreProduct(register(apiculture, COBALT, MagicBeeTaxa.METALLIC, "caeruleo", false, 0x03265F, 0x59AAEF).setTemperature(TemperatureType.HOT).setHumidity(HumidityType.ARID), 0.18f, ItemStack.EMPTY);
        addOreProduct(register(apiculture, MANYULLYN, MagicBeeTaxa.METALLIC, "manahmanah", false, 0x481D6D, 0xBD92F1).setTemperature(TemperatureType.HOT).setHumidity(HumidityType.ARID).setGlint(true), 0.16f, ItemStack.EMPTY);
        addOreProduct(register(apiculture, OSMIUM, MagicBeeTaxa.METALLIC, "hyacintho", false, 0x374B5B, 0x6C7B89).setGenome(g -> g.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_LONGER)), 0.16f, ItemStack.EMPTY);
        addOreProduct(register(apiculture, ELECTRUM, MagicBeeTaxa.METALLIC, "electrum", false, 0x0EAF79).setTemperature(TemperatureType.HOT).setHumidity(HumidityType.ARID), 0.18f, ItemStack.EMPTY);
        IBeeSpeciesBuilder platinum = register(apiculture, PLATINUM, MagicBeeTaxa.METALLIC, "platinum", false, 0x9EE7F7); applyTENether(platinum); addOreProduct(platinum, 0.18f, ItemStack.EMPTY);
        addOreProduct(register(apiculture, NICKEL, MagicBeeTaxa.METALLIC, "nickel", false, 0xB4C989), 0.18f, ItemStack.EMPTY);
        IBeeSpeciesBuilder invar = register(apiculture, INVAR, MagicBeeTaxa.METALLIC, "invar", false, 0xCDE3A1).setTemperature(TemperatureType.HOT).setHumidity(HumidityType.ARID); applyTENether(invar); addOreProduct(invar, 0.18f, ItemStack.EMPTY);
        addOreProduct(register(apiculture, BRONZE, MagicBeeTaxa.METALLIC, "bronze", false, 0xB56D07), 0.18f, stack(MagicBeesItems.nugget("bronze")));
        addOreProduct(register(apiculture, DIAMOND, MagicBeeTaxa.GEM, "diamond", false, 0x209581, 0x8DF5E3), 0.06f, stack(MagicBeesItems.nugget("diamond")));
        addOreProduct(register(apiculture, EMERALD, MagicBeeTaxa.GEM, "prasinus", false, 0x005300, 0x17DD62), 0.04f, stack(MagicBeesItems.nugget("emerald")));
        addOreProduct(register(apiculture, APATITE, MagicBeeTaxa.GEM, "apatite", false, 0x2EA7EC, 0x001D51).setGenome(g -> { g.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_NORMAL); g.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_NORMAL); }), 0.10f, stack(MagicBeesItems.nugget("apatite")));
        addOreProduct(register(apiculture, SILICON, MagicBeeTaxa.GEM, "siliconisque", false, 0xADA2A7, 0x736675).setGenome(g -> { g.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOW); g.set(BeeChromosomes.TERRITORY, ForestryAlleles.TERRITORY_LARGER); g.set(BeeChromosomes.TOLERATES_RAIN, ForestryAlleles.TRUE_RECESSIVE); }), 0.16f, ItemStack.EMPTY);
        addOreProduct(register(apiculture, CERTUS, MagicBeeTaxa.GEM, "alia cristallum", true, 0x93C7FF, 0xA6B8C7).setGenome(g -> g.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOWER)), 0.08f, ItemStack.EMPTY);
        addOreProduct(register(apiculture, FLUIX, MagicBeeTaxa.GEM, "alia cristallum", true, 0xFC639E, 0x534797).setGenome(g -> g.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOWEST)), 0.06f, ItemStack.EMPTY);

        if (modLoaded("ae2")) {
            // #14A - Applied Energistics 2 / Sky Stone progression.
            IBeeSpeciesBuilder skyStone = register(apiculture, AE_SKYSTONE, MagicBeeTaxa.TRANSMUTING, "terra astris", true, 0x4B8381, 0x252929)
                    .setTemperature(TemperatureType.HOT).setHumidity(HumidityType.ARID)
                    .setGenome(g -> {
                        g.set(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
                        g.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_2);
                        g.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_SHORTER);
                        g.set(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_NONE);
                        g.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_NONE);
                        g.set(BeeChromosomes.EFFECT, ForestryBeeEffects.IGNITION);
                    })
                    .addProduct(stack(MagicBeesItems.comb("earthy")), 0.19f);
            // Forestry's bee_species JSON cannot condition an individual specialty on AE2 being present.
            // Include the exact legacy Sky Stone specialty when AE2 is actually in the active registry; otherwise omit it safely.
            // Omitted here: ForestryCE alpha7 cannot condition an individual specialty; the runtime patcher restores it with AE2.
        }

        if (modLoaded("thermal_foundation")) {

            // #12 - Thermal integration. These definitions retain their old branch/binomial/colors and use current Thermal IDs.
        IBeeSpeciesBuilder blizzy = register(apiculture, TE_BLIZZY, MagicBeeTaxa.ABOMINABLE, "blizzard", false, 0x0073C4, 0xFF7C26)
                .setTemperature(TemperatureType.COLD).setHumidity(HumidityType.NORMAL);
        applyTEEnd(blizzy);
        blizzy.setGenome(g -> { g.set(BeeChromosomes.FLOWER_TYPE, ForestryFlowerTypes.SNOW); g.set(BeeChromosomes.EFFECT, ForestryBeeEffects.GLACIAL); })
                .addProduct(forestryComb("frozen"), 0.10f);
        // Omitted here: ForestryCE alpha7 cannot condition an individual specialty; the runtime patcher restores it with Thermal.

        IBeeSpeciesBuilder gelid = register(apiculture, TE_GELID, MagicBeeTaxa.ABOMINABLE, "cyro", true, 0x4AAFF7, 0xFF7C26)
                .setTemperature(TemperatureType.COLD).setHumidity(HumidityType.NORMAL).setGlint(true);
        applyTEEnd(gelid);
        gelid.setGenome(g -> { g.set(BeeChromosomes.FLOWER_TYPE, ForestryFlowerTypes.SNOW); g.set(BeeChromosomes.EFFECT, MagicBees.id("spawn_blizz")); })
                .addProduct(forestryComb("frozen"), 0.10f);
        // Gelid Cryotheum dust has no exact Thermal 11.0.x item counterpart; keep the legacy output omission explicit.

        IBeeSpeciesBuilder dante = register(apiculture, TE_DANTE, MagicBeeTaxa.ABOMINABLE, "inferno", false, 0xF7AC4A, 0xFF7C26)
                .setTemperature(TemperatureType.HELLISH).setHumidity(HumidityType.ARID);
        applyTENether(dante);
        dante.addProduct(stack(MagicBeesItems.comb("furtive")), 0.10f)
                .addProduct(stack(MagicBeesItems.comb("molten")), 0.10f)
                .addSpecialty(new ItemStack(net.minecraft.world.item.Items.BLAZE_POWDER), 0.05f);
        // Omitted here: ForestryCE alpha7 cannot condition an individual specialty; the runtime patcher restores it with Thermal.

        IBeeSpeciesBuilder pyro = register(apiculture, TE_PYRO, MagicBeeTaxa.ABOMINABLE, "pyromania", true, 0xFA930C, 0xFF7C26)
                .setTemperature(TemperatureType.HELLISH).setHumidity(HumidityType.ARID).setGlint(true);
        applyTEEnd(pyro);
        pyro.setGenome(g -> g.set(BeeChromosomes.EFFECT, ForestryBeeEffects.IGNITION))
                .addProduct(stack(MagicBeesItems.comb("furtive")), 0.10f)
                .addProduct(stack(MagicBeesItems.comb("molten")), 0.10f)
                .addSpecialty(new ItemStack(net.minecraft.world.item.Items.BLAZE_POWDER), 0.05f);
        // Blazing Pyrotheum dust was removed from the supplied Thermal 11.0.x target.

        IBeeSpeciesBuilder shocking = register(apiculture, TE_SHOCKING, MagicBeeTaxa.ABOMINABLE, "horrendum", false, 0xC5FF26, 0xF8EE00)
                .setTemperature(TemperatureType.NORMAL).setHumidity(HumidityType.NORMAL);
        applyTEEnd(shocking);
        shocking.setGenome(g -> g.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_FAST))
                .addProduct(stack(MagicBeesItems.comb("airy")), 0.16f);
        // Omitted here: ForestryCE alpha7 cannot condition an individual specialty; the runtime patcher restores it with Thermal.

        IBeeSpeciesBuilder amped = register(apiculture, TE_AMPED, MagicBeeTaxa.ABOMINABLE, "concitatus", false, 0x8AFFFF, 0xECE670)
                .setTemperature(TemperatureType.NORMAL).setHumidity(HumidityType.NORMAL).setGlint(true);
        applyTEEnd(amped);
        amped.setGenome(g -> { g.set(BeeChromosomes.SPEED, Allele.recessive(2.0f)); g.set(BeeChromosomes.EFFECT, MagicBees.id("spawn_blitz")); })
                .addProduct(stack(MagicBeesItems.comb("airy")), 0.29f);
        // Aerotheum dust was removed from the supplied Thermal 11.0.x target.

        IBeeSpeciesBuilder grounded = register(apiculture, TE_GROUNDED, MagicBeeTaxa.ABOMINABLE, "tellus", true, 0xCEC1C1, 0x826767)
                .setTemperature(TemperatureType.HOT).setHumidity(HumidityType.ARID);
        applyTEEnd(grounded);
        grounded.addProduct(stack(MagicBeesItems.comb("earthy")), 0.16f);
        // Omitted here: ForestryCE alpha7 cannot condition an individual specialty; the runtime patcher restores it with Thermal.

        IBeeSpeciesBuilder rocking = register(apiculture, TE_ROCKING, MagicBeeTaxa.ABOMINABLE, "saxsous", true, 0x980000, 0xAB9D9B)
                .setTemperature(TemperatureType.HOT).setHumidity(HumidityType.ARID).setGlint(true);
        applyTEEnd(rocking);
        rocking.setGenome(g -> { g.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_FASTER); g.set(BeeChromosomes.EFFECT, MagicBees.id("spawn_basalz")); })
                .addProduct(stack(MagicBeesItems.comb("earthy")), 0.29f);
        // Petrotheum dust was removed from the supplied Thermal 11.0.x target.

        register(apiculture, TE_COAL, MagicBeeTaxa.THERMAL, "carbonis", false, 0x2E2D2D)
                .addProduct(forestryComb("honey"), 0.10f)
                .addProduct(stack(MagicBeesItems.comb("te_carbon")), 0.50f)
                .addSpecialty(new ItemStack(net.minecraft.world.item.Items.COAL), 0.05f);
        register(apiculture, TE_DESTABILIZED, MagicBeeTaxa.THERMAL, "electric", false, 0x5E0203)
                .addProduct(stack(MagicBeesItems.comb("occult")), 0.10f)
                .addProduct(stack(MagicBeesItems.comb("te_destabilized")), 0.10f)
                .addSpecialty(new ItemStack(net.minecraft.world.item.Items.REDSTONE), 0.05f);
        IBeeSpeciesBuilder lux = register(apiculture, TE_LUX, MagicBeeTaxa.THERMAL, "lux", false, 0xF1FA89);
        applyTENether(lux);
        lux.addProduct(stack(MagicBeesItems.comb("occult")), 0.10f)
                .addProduct(stack(MagicBeesItems.comb("te_lux")), 0.10f)
                .addSpecialty(new ItemStack(net.minecraft.world.item.Items.GLOWSTONE_DUST), 0.05f);
            // TODO: Re-enable Winsome/Endearing when the modern Thermal line has a registry-time-safe platinum source.
            // Their legacy route depends on Platinum, whose current target availability is tag/datapack-driven, while the
            // Endearing comb/drop would need to be item-registered before those tags exist.
        }

        // #13 Botanical branch. Botania item specialties are intentionally not serialized here: ForestryCE alpha7's
        // bee_species loader has no per-product NeoForge condition support, so embedding optional Botania items would
        // make the entire species definition fail when Botania is absent. The runtime patcher restores them when safe.
        if (!modLoaded("botania")) {
            return;
        }
        registerSecret(apiculture, BOT_ROOTED, MagicBeeTaxa.BOTANICAL, "truncus", true, 0x00A800)
                .addProduct(stack(MagicBeesItems.comb("mundane")), 0.10f);

        registerSecret(apiculture, BOT_BOTANIC, MagicBeeTaxa.BOTANICAL, "botanica", true, 0x94C661)
                .setGenome(g -> g.set(BeeChromosomes.FLOWER_TYPE, MagicBeeTaxa.BOTANICAL_FLOWERS))
                .addProduct(stack(MagicBeesItems.comb("mundane")), 0.10f)
                .addProduct(stack(MagicBeesItems.comb("transmuted")), 0.05f);

        registerSecret(apiculture, BOT_BLOSSOM, MagicBeeTaxa.BOTANICAL, "viridis", false, 0xA4C193)
                .setGenome(g -> {
                    g.set(BeeChromosomes.FLOWER_TYPE, MagicBeeTaxa.BOTANICAL_FLOWERS);
                    g.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_4);
                    g.set(BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_FASTER);
                })
                .addProduct(stack(MagicBeesItems.comb("mundane")), 0.20f)
                .addProduct(stack(MagicBeesItems.comb("transmuted")), 0.05f);

        registerSecret(apiculture, BOT_FLORAL, MagicBeeTaxa.BOTANICAL, "florens", true, 0x29D81A)
                .setGenome(g -> {
                    g.set(BeeChromosomes.FLOWER_TYPE, MagicBeeTaxa.BOTANICAL_FLOWERS);
                    g.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_3);
                    g.set(BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_MAXIMUM);
                })
                .addProduct(stack(MagicBeesItems.comb("mundane")), 0.25f)
                .addProduct(stack(MagicBeesItems.comb("transmuted")), 0.05f);

        registerSecret(apiculture, BOT_VAZBEE, MagicBeeTaxa.BOTANICAL, "vazbii", false, 0xFF6B9C)
                .setGenome(g -> {
                    g.set(BeeChromosomes.FLOWER_TYPE, MagicBeeTaxa.BOTANICAL_FLOWERS);
                    g.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_1);
                    g.set(BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_MAXIMUM);
                })
                .addProduct(stack(MagicBeesItems.comb("soul")), 0.05f)
                .addProduct(new ItemStack(net.minecraft.world.item.Items.PINK_DYE), 0.20f)
                .addProduct(new ItemStack(net.minecraft.world.level.block.Blocks.PINK_WOOL), 0.02f)
                .addProduct(new ItemStack(net.minecraft.world.item.Items.POPPY), 0.06f)
                .addProduct(stack(MagicBeesItems.comb("transmuted")), 0.15f);

        register(apiculture, BOT_SOMNOLENT, MagicBeeTaxa.BOTANICAL, "soporatus", true, 0x2978C6)
                .setGenome(g -> {
                    g.set(BeeChromosomes.FLOWER_TYPE, MagicBeeTaxa.BOTANICAL_FLOWERS);
                    g.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_UP_2);
                    g.set(BeeChromosomes.ACTIVITY, ForestryActivityTypes.NOCTURNAL);
                    g.set(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
                    g.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOWEST);
                    g.set(BeeChromosomes.EFFECT, MagicBees.id("effect_slow_speed"));
                })
                .addProduct(stack(MagicBeesItems.comb("watery")), 0.08f)
                .addProduct(stack(MagicBeesItems.comb("soul")), 0.15f);

        register(apiculture, BOT_DREAMING, MagicBeeTaxa.BOTANICAL, "somnior", true, 0x123456)
                .setGenome(g -> {
                    g.set(BeeChromosomes.FLOWER_TYPE, MagicBeeTaxa.BOTANICAL_FLOWERS);
                    g.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_UP_2);
                    g.set(BeeChromosomes.ACTIVITY, ForestryActivityTypes.NOCTURNAL);
                    g.set(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
                    g.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_NORMAL);
                    g.set(BeeChromosomes.TERRITORY, ForestryAlleles.TERRITORY_LARGER);
                    g.set(BeeChromosomes.EFFECT, MagicBees.id("effect_slow_speed"));
                })
                .addProduct(stack(MagicBeesItems.comb("watery")), 0.16f)
                .addProduct(stack(MagicBeesItems.comb("soul")), 0.33f);

        registerSecret(apiculture, BOT_ALFHEIM, MagicBeeTaxa.BOTANICAL, "alfheimis", false, 0xFFFFFF, 0xFFFFFF)
                .setGenome(g -> {
                    g.set(BeeChromosomes.FLOWER_TYPE, MagicBeeTaxa.BOTANICAL_FLOWERS);
                    g.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_UP_2);
                    g.set(BeeChromosomes.ACTIVITY, ForestryActivityTypes.NOCTURNAL);
                    g.set(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
                    g.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_NORMAL);
                    g.set(BeeChromosomes.TERRITORY, ForestryAlleles.TERRITORY_LARGER);
                    g.set(BeeChromosomes.EFFECT, MagicBees.id("effect_dreaming"));
                })
                .addProduct(stack(MagicBeesItems.comb("otherworldly")), 0.28f);
    }

    private static boolean modLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    private static IBeeSpeciesBuilder register(IApicultureRegistration apiculture, ResourceLocation id, String genus,
                                               String species, boolean dominant, int primary) {
        return register(apiculture, id, genus, species, dominant, primary, MagicBeeTaxa.legacySecondaryColor(genus));
    }

    private static IBeeSpeciesBuilder register(IApicultureRegistration apiculture, ResourceLocation id, String genus,
                                               String species, boolean dominant, int primary, int secondary) {
        IBeeSpeciesBuilder builder = apiculture.registerSpecies(id, genus, species, dominant, TextColor.fromRgb(primary))
                .setBody(TextColor.fromRgb(secondary))
                .setAuthority("Elec332");
        MagicBeeTaxa.applySpeciesDefaults(genus, builder);
        return builder;
    }

    private static IBeeSpeciesBuilder registerSecret(IApicultureRegistration apiculture, ResourceLocation id, String genus,
                                                     String species, boolean dominant, int primary) {
        return register(apiculture, id, genus, species, dominant, primary)
                .setSecret(false);
    }

    private static IBeeSpeciesBuilder registerSecret(IApicultureRegistration apiculture, ResourceLocation id, String genus,
                                                     String species, boolean dominant, int primary, int secondary) {
        return register(apiculture, id, genus, species, dominant, primary, secondary)
                .setSecret(false);
    }

    /** Legacy species that called setIsSecret directly, independently of the showAllBees setting. */
    private static IBeeSpeciesBuilder registerAlwaysSecret(IApicultureRegistration apiculture, ResourceLocation id, String genus,
                                                           String species, boolean dominant, int primary) {
        return register(apiculture, id, genus, species, dominant, primary)
                .setSecret(true);
    }

    private static IBeeSpeciesBuilder registerAlwaysSecret(IApicultureRegistration apiculture, ResourceLocation id, String genus,
                                                           String species, boolean dominant, int primary, int secondary) {
        return register(apiculture, id, genus, species, dominant, primary, secondary)
                .setSecret(true);
    }

    private static ItemStack forestryComb(String name) {
        return new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("forestry", name + "_comb")));
    }

    private static void addOreProduct(IBeeSpeciesBuilder species, float specialtyChance, ItemStack material) {
        species.addProduct(forestryComb("honey"), 0.10f);
        if (!material.isEmpty()) {
            species.addSpecialty(material, specialtyChance);
        }
    }

    /** Reproduces the legacy BeeGenomeTemplate default reset performed by getTemplateTE(...). */
    private static void applyTEBase(IBeeSpeciesBuilder species) {
        species.setGenome(genome -> {
            genome.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_FAST);
            genome.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_LONGER);
            genome.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_3);
            genome.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_2);
            genome.set(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_BOTH_2);
            genome.set(BeeChromosomes.ACTIVITY, ForestryActivityTypes.DIURNAL);
            genome.set(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.FALSE_RECESSIVE);
            genome.set(BeeChromosomes.TOLERATES_RAIN, ForestryAlleles.TRUE_RECESSIVE);
            genome.set(BeeChromosomes.FLOWER_TYPE, ForestryFlowerTypes.VANILLA);
            genome.set(BeeChromosomes.POLLINATION, ForestryAlleles.POLLINATION_SLOWEST);
            genome.set(BeeChromosomes.TERRITORY, ForestryAlleles.TERRITORY_AVERAGE);
            genome.set(BeeChromosomes.EFFECT, ForestryBeeEffects.NONE);
        });
    }

    private static void applyTENether(IBeeSpeciesBuilder species) {
        applyTEBase(species);
        species.setGenome(genome -> {
            genome.set(BeeChromosomes.HUMIDITY_TOLERANCE, ForestryAlleles.TOLERANCE_NONE);
            genome.set(BeeChromosomes.TEMPERATURE_TOLERANCE, ForestryAlleles.TOLERANCE_NONE);
            genome.set(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
            genome.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_SLOW);
            genome.set(BeeChromosomes.FERTILITY, ForestryAlleles.FERTILITY_2);
            genome.set(BeeChromosomes.FLOWER_TYPE, ForestryFlowerTypes.NETHER);
            genome.set(BeeChromosomes.EFFECT, ForestryBeeEffects.IGNITION);
            genome.set(BeeChromosomes.ACTIVITY, MagicBeeActivityTypes.NEVER_SLEEPS);
        });
    }

    private static void applyTEEnd(IBeeSpeciesBuilder species) {
        applyTEBase(species);
        species.setGenome(genome -> {
            genome.set(BeeChromosomes.ACTIVITY, MagicBeeActivityTypes.NEVER_SLEEPS);
            genome.set(BeeChromosomes.CAVE_DWELLING, ForestryAlleles.TRUE_RECESSIVE);
            genome.set(BeeChromosomes.SPEED, ForestryAlleles.SPEED_FASTEST);
            genome.set(BeeChromosomes.LIFESPAN, ForestryAlleles.LIFESPAN_LONGEST);
        });
    }

    private static ItemStack stack(net.neoforged.neoforge.registries.DeferredItem<?> item) {
        return new ItemStack(item.get());
    }
}

package eu.dsix.magicbees.integration.thaumaturge;

import com.leclowndu93150.thaumaturge.api.aura.AuraHelper;
import com.leclowndu93150.thaumaturge.api.taint.TaintApi;
import com.leclowndu93150.thaumaturge.registry.TCItems;
import com.leclowndu93150.thaumaturge.registry.TCEntities;
import forestry.api.core.backpacks.EnumBackpackType;
import forestry.api.core.backpacks.IBackpackDefinition;
import forestry.api.apiculture.ForestryFlowerTypes;
import forestry.api.apiculture.genetics.IBeeEffect;
import forestry.apiculture.bees.genetics.effects.PotionBeeEffect;
import forestry.api.core.genetics.IEffectData;
import forestry.api.core.genetics.IGenome;
import forestry.api.plugin.IApicultureRegistration;
import forestry.api.plugin.IGeneticRegistration;
import forestry.core.engine.genetics.flowers.TagFlowerType;
import eu.dsix.magicbees.MagicBees;
import eu.dsix.magicbees.registry.MagicBeesItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.Predicate;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/** Thaumaturge-only species, flower types, and aura effects. Loaded through {@code ThaumaturgeHook}. */
public final class ThaumaturgeIntegration {
    private static final ResourceLocation THAUMIC_FLOWERS = MagicBees.id("thaumic_flowers");
    private static final ResourceLocation AURA_NODE_FLOWERS = MagicBees.id("aura_node_flowers");
    private static final ResourceLocation REJUVENATING = MagicBees.id("thaumaturge_rejuvenating");
    private static final ResourceLocation EMPOWERING = MagicBees.id("thaumaturge_empowering");
    private static final ResourceLocation PURIFYING = MagicBees.id("thaumaturge_purifying");
    private static final ResourceLocation NEXUS = MagicBees.id("thaumaturge_nexus");
    private static final ResourceLocation TAINT = MagicBees.id("thaumaturge_taint");
    private static final ResourceLocation HUNGRY = MagicBees.id("thaumaturge_hungry");
    private static final ResourceLocation WISPY = MagicBees.id("thaumaturge_wispy");
    private static final ResourceLocation AIR_SPEED = MagicBees.id("thaumaturge_air_speed");
    private static final ResourceLocation WATER_CLEANSING = MagicBees.id("thaumaturge_water_cleansing");
    private static final ResourceLocation EARTH_HASTE = MagicBees.id("thaumaturge_earth_haste");

    private ThaumaturgeIntegration() {
    }

    public static void registerItems(IEventBus ignored) {
        MagicBeesItems.ITEMS.register("backpack_thaumaturge_t1", () -> createBackpack(EnumBackpackType.NORMAL));
        MagicBeesItems.ITEMS.register("backpack_thaumaturge_t2", () -> createBackpack(EnumBackpackType.WOVEN));
    }

    private static Item createBackpack(EnumBackpackType type) {
        try {
            Class<?> moduleStorage = Class.forName("forestry.core.content.backpacks.ModuleStorage");
            Field field = moduleStorage.getField("BACKPACK_INTERFACE");
            Object backpackInterface = field.get(null);
            return (Item) backpackInterface.getClass()
                    .getMethod("createBackpack", IBackpackDefinition.class, EnumBackpackType.class)
                    .invoke(backpackInterface, thaumaturgeBackpackDefinition(), type);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to create the Thaumaturge backpack", exception);
        }
    }

    private static IBackpackDefinition thaumaturgeBackpackDefinition() {
        Predicate<ItemStack> filter = stack -> {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            return "thaumaturge".equals(id.getNamespace())
                    && ("celestial_notes".equals(id.getPath()) || id.getPath().startsWith("curio_"));
        };
        return (IBackpackDefinition) Proxy.newProxyInstance(
                ThaumaturgeIntegration.class.getClassLoader(),
                new Class<?>[]{IBackpackDefinition.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getName" -> Component.translatable("item.magicbees.backpack_thaumaturge");
                    case "getPrimaryColour" -> 0x8700C6;
                    case "getSecondaryColour" -> 0xFFFFFF;
                    case "getFilter" -> filter;
                    default -> throw new UnsupportedOperationException(method.getName());
                });
    }

    public static void registerGenetics(IGeneticRegistration genetics) {
        genetics.registerFlowerType(THAUMIC_FLOWERS, new TagFlowerType(blockTag(THAUMIC_FLOWERS), false));
        genetics.registerFlowerType(AURA_NODE_FLOWERS, new TagFlowerType(blockTag(AURA_NODE_FLOWERS), false));
    }

    private static TagKey<net.minecraft.world.level.block.Block> blockTag(ResourceLocation id) {
        return TagKey.create(Registries.BLOCK, id);
    }

    public static void registerApiculture(IApicultureRegistration apiculture) {
        apiculture.registerBeeEffect(REJUVENATING, new AuraEffect(AuraEffect.Mode.REJUVENATE, false, 15));
        apiculture.registerBeeEffect(EMPOWERING, new AuraEffect(AuraEffect.Mode.EMPOWER, true, 1200));
        apiculture.registerBeeEffect(PURIFYING, new AuraEffect(AuraEffect.Mode.PURIFY, false, 15));
        apiculture.registerBeeEffect(NEXUS, new AuraEffect(AuraEffect.Mode.NEXUS, false, 15));
        apiculture.registerBeeEffect(TAINT, new AuraEffect(AuraEffect.Mode.TAINT, false, 15));
        apiculture.registerBeeEffect(HUNGRY, new HungryEffect());
        apiculture.registerBeeEffect(WISPY, new WispEffect());
        apiculture.registerBeeEffect(AIR_SPEED,
            new PotionBeeEffect(false, net.minecraft.world.effect.MobEffects.MOVEMENT_SPEED, 300, 200, 1.0f));
        apiculture.registerBeeEffect(WATER_CLEANSING, new CleansingEffect());
        apiculture.registerBeeEffect(EARTH_HASTE,
            new PotionBeeEffect(true, net.minecraft.world.effect.MobEffects.DIG_SPEED, 300, 200, 1.0f));

        registerElementalSpecies(apiculture);
        registerAdvancedSpecies(apiculture);
        apiculture.modifySpecies(MagicBees.id("brainy"), species ->
            species.addSpecialty(new ItemStack(TCItems.BRAIN.get()), 0.20f));
    }

    private static void registerElementalSpecies(IApicultureRegistration apiculture) {
        register(apiculture, "tc_air", "aether", true, 0xD9D636, 0x999999)
                .setGenome(genome -> {
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.SPEED,
                            forestry.api.core.genetics.alleles.ForestryAlleles.SPEED_FASTEST);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.LIFESPAN,
                            forestry.api.core.genetics.alleles.ForestryAlleles.LIFESPAN_SHORTENED);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.TERRITORY,
                            forestry.api.core.genetics.alleles.ForestryAlleles.TERRITORY_LARGEST);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.POLLINATION,
                            forestry.api.core.genetics.alleles.ForestryAlleles.POLLINATION_MAXIMUM);
                        genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.CAVE_DWELLING,
                            forestry.api.core.genetics.alleles.ForestryAlleles.TRUE_RECESSIVE);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.FLOWER_TYPE, THAUMIC_FLOWERS);
                        genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.EFFECT, AIR_SPEED);
                })
                .setGlint(true)
                .addProduct(stack("tc_air"), 0.20f).addSpecialty(MagicBeesItems.propolis("air").get().getDefaultInstance(), 0.18f);

        register(apiculture, "tc_fire", "praefervidus", true, 0xE50B0B, 0x999999)
                .setTemperature(forestry.api.core.TemperatureType.HOT)
                .setHumidity(forestry.api.core.HumidityType.ARID)
                .setGenome(genome -> {
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.SPEED,
                            forestry.api.core.genetics.alleles.ForestryAlleles.SPEED_FASTER);
                        genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.LIFESPAN,
                            forestry.api.core.genetics.alleles.ForestryAlleles.LIFESPAN_NORMAL);
                        genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.TEMPERATURE_TOLERANCE,
                            forestry.api.core.genetics.alleles.ForestryAlleles.TOLERANCE_DOWN_3);
                        genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.HUMIDITY_TOLERANCE,
                            forestry.api.core.genetics.alleles.ForestryAlleles.TOLERANCE_UP_2);
                        genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.CAVE_DWELLING,
                            forestry.api.core.genetics.alleles.ForestryAlleles.TRUE_RECESSIVE);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.FLOWER_TYPE, THAUMIC_FLOWERS);
                        genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.EFFECT,
                            forestry.api.apiculture.ForestryBeeEffects.IGNITION);
                })
                .setGlint(true)
                .addProduct(stack("tc_fire"), 0.20f).addSpecialty(MagicBeesItems.propolis("fire").get().getDefaultInstance(), 0.18f);

        register(apiculture, "tc_water", "umidus", true, 0x36CFD9, 0x999999)
                .setHumidity(forestry.api.core.HumidityType.DAMP)
                .setGenome(genome -> {
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.SPEED,
                            forestry.api.core.genetics.alleles.ForestryAlleles.SPEED_FAST);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.LIFESPAN,
                            forestry.api.core.genetics.alleles.ForestryAlleles.LIFESPAN_LONG);
                        genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.HUMIDITY_TOLERANCE,
                            forestry.api.core.genetics.alleles.ForestryAlleles.TOLERANCE_DOWN_2);
                        genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.CAVE_DWELLING,
                            forestry.api.core.genetics.alleles.ForestryAlleles.TRUE_RECESSIVE);
                        genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.TERRITORY,
                            forestry.api.core.genetics.alleles.ForestryAlleles.TERRITORY_LARGE);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.FLOWER_TYPE, THAUMIC_FLOWERS);
                        genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.EFFECT, WATER_CLEANSING);
                })
                .setGlint(true)
                .addProduct(stack("tc_water"), 0.20f).addSpecialty(MagicBeesItems.propolis("water").get().getDefaultInstance(), 0.18f);

        register(apiculture, "tc_earth", "sordida", true, 0x005100, 0x999999)
                .setGenome(genome -> {
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.SPEED,
                        forestry.api.core.genetics.alleles.ForestryAlleles.SPEED_SLOW);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.LIFESPAN,
                        forestry.api.core.genetics.alleles.ForestryAlleles.LIFESPAN_LONGER);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.HUMIDITY_TOLERANCE,
                        forestry.api.core.genetics.alleles.ForestryAlleles.TOLERANCE_DOWN_1);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.TOLERATES_RAIN,
                        forestry.api.core.genetics.alleles.ForestryAlleles.TRUE_RECESSIVE);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.CAVE_DWELLING,
                        forestry.api.core.genetics.alleles.ForestryAlleles.TRUE_RECESSIVE);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.POLLINATION,
                        forestry.api.core.genetics.alleles.ForestryAlleles.POLLINATION_FASTEST);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.FLOWER_TYPE, THAUMIC_FLOWERS);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.EFFECT, EARTH_HASTE);
                })
                .setGlint(true)
                .addProduct(stack("tc_earth"), 0.20f).addSpecialty(MagicBeesItems.propolis("earth").get().getDefaultInstance(), 0.18f);

        register(apiculture, "tc_order", "ordinatus", true, 0xAA32FC, 0x999999)
                .setGenome(genome -> {
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.SPEED,
                            forestry.api.core.genetics.alleles.ForestryAlleles.SPEED_NORMAL);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.LIFESPAN,
                            forestry.api.core.genetics.alleles.ForestryAlleles.LIFESPAN_NORMAL);
                })
                .setGlint(true)
                .addProduct(stack("tc_order"), 0.20f).addSpecialty(MagicBeesItems.propolis("order").get().getDefaultInstance(), 0.18f);

        register(apiculture, "tc_entropy", "tenebrarum", false, 0xCCCCCC, 0x999999)
                .setGenome(genome -> genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.FERTILITY,
                        forestry.api.core.genetics.alleles.ForestryAlleles.FERTILITY_2))
                .setGlint(true)
                .addProduct(stack("tc_entropy"), 0.20f).addSpecialty(MagicBeesItems.propolis("entropy").get().getDefaultInstance(), 0.18f);
    }

    private static void registerAdvancedSpecies(IApicultureRegistration apiculture) {
        register(apiculture, "tc_vis", "arcanus saecula", false, 0x004C99, 0x675ED1)
                .setGenome(genome -> {
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.HUMIDITY_TOLERANCE,
                    forestry.api.core.genetics.alleles.ForestryAlleles.TOLERANCE_BOTH_1);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.TEMPERATURE_TOLERANCE,
                    forestry.api.core.genetics.alleles.ForestryAlleles.TOLERANCE_BOTH_1);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.FLOWER_TYPE, AURA_NODE_FLOWERS);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.SPEED,
                            forestry.api.core.genetics.alleles.ForestryAlleles.SPEED_SLOW);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.POLLINATION,
                            forestry.api.core.genetics.alleles.ForestryAlleles.POLLINATION_SLOWER);
                }).addProduct(stack("intellect"), 0.10f);
        register(apiculture, "tc_rejuvenating", "arcanus vitae", false, 0x91D0D9, 0x675ED1)
            .setGenome(genome -> {
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.TEMPERATURE_TOLERANCE,
                    forestry.api.core.genetics.alleles.ForestryAlleles.TOLERANCE_BOTH_1);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.FLOWER_TYPE, AURA_NODE_FLOWERS);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.EFFECT, REJUVENATING);
            })
                .addProduct(stack("intellect"), 0.18f);
        register(apiculture, "tc_empowering", "tractus", true, 0x96FFBC, 0x675ED1)
            .setGenome(genome -> {
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.HUMIDITY_TOLERANCE,
                    forestry.api.core.genetics.alleles.ForestryAlleles.TOLERANCE_BOTH_1);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.TEMPERATURE_TOLERANCE,
                    forestry.api.core.genetics.alleles.ForestryAlleles.TOLERANCE_BOTH_1);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.FLOWER_TYPE, AURA_NODE_FLOWERS);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.EFFECT, EMPOWERING);
            })
                .addProduct(stack("intellect"), 0.14f)
                .addProduct(externalItem("forestry:crystalline_pollen_cluster"), 0.20f);
        register(apiculture, "tc_nexus", "nexi", false, 0x15AFAF, 0x675ED1)
            .setGenome(genome -> {
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.HUMIDITY_TOLERANCE,
                    forestry.api.core.genetics.alleles.ForestryAlleles.TOLERANCE_BOTH_1);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.TEMPERATURE_TOLERANCE,
                            forestry.api.core.genetics.alleles.ForestryAlleles.TOLERANCE_BOTH_1);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.FLOWER_TYPE, AURA_NODE_FLOWERS);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.EFFECT, NEXUS);
            })
                .setGlint(true)
                .addProduct(stack("intellect"), 0.25f)
                .addProduct(externalItem("forestry:crystalline_pollen_cluster"), 0.20f)
                .addProduct(stack("temporal"), 0.12f);
        register(apiculture, "tc_taint", "arcanus labe", false, 0x91376A, 0x675ED1)
                .setGenome(genome -> {
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.FLOWER_TYPE, AURA_NODE_FLOWERS);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.EFFECT, TAINT);
                })
                .addProduct(stack("intellect"), 0.14f).addSpecialty(MagicBeesItems.propolis("unstable").get().getDefaultInstance(), 0.213f);
        register(apiculture, "tc_pure", "arcanus puritatem", false, 0xE23F65, 0x675ED1)
                .setGenome(genome -> {
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.FLOWER_TYPE, AURA_NODE_FLOWERS);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.POLLINATION,
                            forestry.api.core.genetics.alleles.ForestryAlleles.POLLINATION_AVERAGE);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.EFFECT, PURIFYING);
                })
                .addProduct(stack("intellect"), 0.16f).addProduct(stack("soul"), 0.19f);
        register(apiculture, "tc_hungry", "omnique", false, 0xDCA5E2, 0x675ED1)
            .setGenome(genome -> {
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.FLOWER_TYPE, AURA_NODE_FLOWERS);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.TERRITORY,
                        forestry.api.core.genetics.alleles.ForestryAlleles.TERRITORY_LARGEST);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.EFFECT, HUNGRY);
            })
            .addProduct(stack("intellect"), 0.28f).addProduct(stack("temporal"), 0.195f);

        apiculture.registerSpecies(MagicBees.id("tc_void"), "Metalliapis", "obscurus", false,
                net.minecraft.network.chat.TextColor.fromRgb(0x180A29))
            .setBody(net.minecraft.network.chat.TextColor.fromRgb(0x4B2A74)).setAuthority("Elec332")
            .setTemperature(forestry.api.core.TemperatureType.ICY)
            .setGenome(genome -> {
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.SPEED,
                    forestry.api.core.genetics.alleles.ForestryAlleles.SPEED_SLOWEST);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.ACTIVITY,
                        MagicBees.id("activity_never_sleeps"));
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.CAVE_DWELLING,
                        forestry.api.core.genetics.alleles.ForestryAlleles.TRUE_RECESSIVE);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.TEMPERATURE_TOLERANCE,
                    forestry.api.core.genetics.alleles.ForestryAlleles.TOLERANCE_UP_1);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.FLOWER_TYPE,
                        ForestryFlowerTypes.VANILLA);
                    genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.EFFECT,
                        forestry.api.apiculture.ForestryBeeEffects.GLACIAL);
            })
            .addProduct(externalItem("forestry:honey_comb"), 0.10f)
            .addSpecialty(new net.minecraft.world.item.ItemStack(TCItems.NUGGET_VOID.get()), 0.155f);

        register(apiculture, "tc_wispy", "umbrabilis", false, 0x9CB8D5, 0xE15236)
            .setGenome(genome -> {
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.SPEED,
                    forestry.api.core.genetics.alleles.ForestryAlleles.SPEED_FAST);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.POLLINATION,
                    forestry.api.core.genetics.alleles.ForestryAlleles.POLLINATION_FASTER);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.ACTIVITY,
                    MagicBees.id("activity_never_sleeps"));
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.FLOWER_TYPE, THAUMIC_FLOWERS);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.TOLERATES_RAIN,
                    forestry.api.core.genetics.alleles.ForestryAlleles.TRUE_RECESSIVE);
                genome.set(forestry.api.core.genetics.alleles.BeeChromosomes.EFFECT, WISPY);
            })
            .addProduct(externalItem("forestry:silky_comb"), 0.22f)
            .addSpecialty(externalItem("forestry:silk_wisp"), 0.40f);
    }

    private static forestry.api.plugin.IBeeSpeciesBuilder register(IApicultureRegistration apiculture, String id,
                                                                     String species, boolean dominant, int outline, int body) {
        return apiculture.registerSpecies(MagicBees.id(id), "Thaumiapis", species, dominant,
                        net.minecraft.network.chat.TextColor.fromRgb(outline))
                .setBody(net.minecraft.network.chat.TextColor.fromRgb(body)).setAuthority("Elec332");
    }

    private static net.minecraft.world.item.ItemStack stack(String comb) {
        return MagicBeesItems.comb(comb).get().getDefaultInstance();
    }

    private static ItemStack externalItem(String id) {
        return new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.parse(id)));
    }

    private static final class AuraEffect implements IBeeEffect {
        private enum Mode { REJUVENATE, EMPOWER, PURIFY, NEXUS, TAINT, HUNGRY }

        private final Mode mode;
        private final boolean dominant;
        private final int throttle;

        private AuraEffect(Mode mode, boolean dominant, int throttle) {
            this.mode = mode;
            this.dominant = dominant;
            this.throttle = throttle;
        }

        @Override
        public boolean isDominant() {
            return dominant;
        }

        @Override
        public IEffectData validateStorage(IEffectData storedData) {
            return storedData == null ? new forestry.core.engine.genetics.EffectData(1, 0) : storedData;
        }

        @Override
        public IEffectData doEffect(IGenome genome, IEffectData storedData, forestry.api.apiculture.IBeeHousing housing) {
            int ticks = storedData.getInteger(0) + 1;
            storedData.setInteger(0, ticks);
            if (ticks < throttle || housing.getErrorLogic().hasErrors()) {
                return storedData;
            }
            storedData.setInteger(0, 0);
            Level level = housing.getLevel();
            BlockPos pos = housing.getBlockPos();
            switch (mode) {
                case REJUVENATE -> {
                    float capacity = Math.max(0.0f, AuraHelper.getAuraBase(level, pos) * 2.0f - AuraHelper.getVis(level, pos));
                    AuraHelper.addVis(level, pos, Math.min(capacity, 0.18f * level.random.nextFloat()));
                }
                case EMPOWER -> AuraHelper.addVis(level, pos, 1.0f);
                case PURIFY -> {
                    float amount = Math.min(AuraHelper.getFlux(level, pos), 0.05f + 0.04f * level.random.nextFloat());
                    AuraHelper.drainFlux(level, pos, amount, false);
                    AuraHelper.addVis(level, pos, amount);
                }
                case NEXUS -> {
                    float cap = Math.min(500.0f, AuraHelper.getAuraBase(level, pos) * 1.2f);
                    float room = Math.max(0.0f, cap - AuraHelper.getVis(level, pos));
                    AuraHelper.addVis(level, pos, Math.min(room, 0.18f * level.random.nextFloat()));
                }
                case TAINT -> AuraHelper.polluteAura(level, pos, 0.2f + 0.1f * level.random.nextFloat(), true);
                case HUNGRY -> {
                    AuraHelper.drainVis(level, pos, 0.5f, false);
                    AuraHelper.polluteAura(level, pos, 0.2f, true);
                }
            }
            if ((mode == Mode.TAINT || mode == Mode.HUNGRY) && level instanceof ServerLevel serverLevel) {
                TaintApi.spreadFibres(serverLevel, pos, false);
            }
            return storedData;
        }

    }

    private static final class WispEffect implements IBeeEffect {
        private static final int THROTTLE = 100;
        private static final int SPAWN_CHANCE = 80;
        private static final int MAX_NEARBY_WISPS = 2;

        @Override
        public boolean isDominant() {
            return false;
        }

        @Override
        public IEffectData validateStorage(IEffectData storedData) {
            return storedData == null ? new forestry.core.engine.genetics.EffectData(1, 0) : storedData;
        }

        @Override
        public IEffectData doEffect(IGenome genome, IEffectData storedData, forestry.api.apiculture.IBeeHousing housing) {
            int ticks = storedData.getInteger(0) + 1;
            storedData.setInteger(0, ticks);
            if (ticks < THROTTLE || housing.getErrorLogic().hasErrors()) {
                return storedData;
            }
            storedData.setInteger(0, 0);
            Level level = housing.getLevel();
            if (level != null && !level.isClientSide && level.random.nextInt(100) < SPAWN_CHANCE) {
                var bounds = forestry.apiculture.bees.genetics.effects.ThrottledBeeEffect.getBounding(housing, genome);
                if (level.getEntitiesOfClass(com.leclowndu93150.thaumaturge.content.entity.WispEntity.class, bounds).size() >= MAX_NEARBY_WISPS) {
                    return storedData;
                }
                com.leclowndu93150.thaumaturge.content.entity.WispEntity wisp =
                        TCEntities.WISP.get().create(level);
                if (wisp != null) {
                    BlockPos pos = housing.getBlockPos();
                    wisp.moveTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                            level.random.nextFloat() * 360.0f, 0.0f);
                    if (level.noCollision(wisp)) {
                        level.addFreshEntity(wisp);
                    }
                }
            }
            return storedData;
        }
    }

    private static final class CleansingEffect implements IBeeEffect {
        @Override
        public boolean isDominant() {
            return false;
        }

        @Override
        public IEffectData validateStorage(IEffectData storedData) {
            return storedData == null ? new forestry.core.engine.genetics.EffectData(1, 0) : storedData;
        }

        @Override
        public IEffectData doEffect(IGenome genome, IEffectData storedData,
                                    forestry.api.apiculture.IBeeHousing housing) {
            int ticks = storedData.getInteger(0) + 1;
            storedData.setInteger(0, ticks);
            if (ticks < 200 || housing.getErrorLogic().hasErrors()) {
                return storedData;
            }
            storedData.setInteger(0, 0);
            Level level = housing.getLevel();
            if (level != null && !level.isClientSide) {
                var bounds = forestry.apiculture.bees.genetics.effects.ThrottledBeeEffect.getBounding(housing, genome);
                for (net.minecraft.world.entity.player.Player player : level.getEntitiesOfClass(
                        net.minecraft.world.entity.player.Player.class, bounds)) {
                    player.removeAllEffects();
                }
            }
            return storedData;
        }
    }

    private static final class HungryEffect implements IBeeEffect {
        private static final int VIS_REQUIRED = Short.MAX_VALUE / 2;

        @Override
        public boolean isDominant() {
            return false;
        }

        @Override
        public IEffectData validateStorage(IEffectData storedData) {
            return storedData == null ? new forestry.core.engine.genetics.EffectData(2, 0) : storedData;
        }

        @Override
        public IEffectData doEffect(IGenome genome, IEffectData storedData,
                                    forestry.api.apiculture.IBeeHousing housing) {
            int ticks = storedData.getInteger(0) + 1;
            storedData.setInteger(0, ticks);
            if (ticks < 1000 || housing.getErrorLogic().hasErrors()) {
                return storedData;
            }
            storedData.setInteger(0, 0);
            Level level = housing.getLevel();
            if (level == null || level.isClientSide) {
                return storedData;
            }

            BlockPos pos = housing.getBlockPos();
            float nom = level.random.nextFloat() * 100.0f;
            float vis = com.leclowndu93150.thaumaturge.api.aura.AuraHelper.getVis(level, pos);
            if (vis > nom + 1.0f) {
                com.leclowndu93150.thaumaturge.api.aura.AuraHelper.drainVis(level, pos, nom, false);
                storedData.setInteger(1, storedData.getInteger(1) + Math.round(nom * 100.0f));
            } else {
                com.leclowndu93150.thaumaturge.api.aura.AuraHelper.polluteAura(level, pos, nom, true);
                com.leclowndu93150.thaumaturge.api.taint.TaintApi.spreadFibres(
                        (ServerLevel) level, pos, false);
            }

            if (storedData.getInteger(1) >= VIS_REQUIRED * 100) {
                var bounds = forestry.apiculture.bees.genetics.effects.ThrottledBeeEffect.getBounding(housing, genome);
                var candidates = level.getEntitiesOfClass(net.minecraft.world.entity.LivingEntity.class, bounds,
                        entity -> !(entity instanceof net.minecraft.world.entity.monster.Monster));
                if (!candidates.isEmpty()) {
                    var victim = candidates.get(level.random.nextInt(candidates.size()));
                    victim.hurt(level.damageSources().magic(), 1_000_000.0f);
                    var type = level.random.nextFloat() > 0.3f
                            ? com.leclowndu93150.thaumaturge.registry.TCEntities.CULTIST_KNIGHT.get()
                            : com.leclowndu93150.thaumaturge.registry.TCEntities.CULTIST_CLERIC.get();
                    var cultist = type.create(level);
                    if (cultist != null) {
                        cultist.moveTo(victim.getX(), victim.getY(), victim.getZ(), 0.0f, 0.0f);
                        level.addFreshEntity(cultist);
                    }
                    storedData.setInteger(1, 0);
                }
            }
            return storedData;
        }
    }
}

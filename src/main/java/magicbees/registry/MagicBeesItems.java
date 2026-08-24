package magicbees.registry;

import magicbees.MagicBees;
import magicbees.item.MagicBeesFrameItem;
import magicbees.item.MagicBeesFrameType;
import magicbees.item.MoonDialItem;
import magicbees.item.MysteriousMagnetItem;
import magicbees.item.ManasteelScoopItem;
import magicbees.item.ManasteelGrafterItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MagicBeesItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MagicBees.MOD_ID);

    public static final Map<String, DeferredItem<Item>> COMBS = variants("bee_comb",
            "mundane", "molten", "occult", "otherworldly", "transmuted", "papery", "soul", "furtive",
            "intellect", "temporal", "forgotten", "airy", "firey", "watery", "earthy",
            "te_destabilized", "te_carbon", "te_lux", "te_endearing",
            "tc_air", "tc_fire", "tc_water", "tc_earth", "tc_order", "tc_entropy");

    public static final Map<String, DeferredItem<Item>> DROPS = variants("drop",
            "enchanted", "intellect", "destabilized", "carbon", "lux", "endearing");

    public static final Map<String, DeferredItem<Item>> POLLEN = variants("pollen", "unusual", "phased");

    public static final Map<String, DeferredItem<Item>> PROPOLIS = variants("propolis",
            "unstable", "air", "fire", "water", "earth", "order", "entropy");

    public static final Map<String, DeferredItem<Item>> WAX = variants("wax", "magic", "soul", "amnesic");

    public static final Map<String, DeferredItem<Item>> NUGGETS = variants("ore_part",
            "diamond", "emerald", "apatite", "copper", "tin", "bronze", "iron");

    public static final Map<String, DeferredItem<Item>> RESOURCES = variants("resource",
            "lore_fragment", "aromatic_lump", "extended_fertilizer", "skull_chip", "skull_fragment",
            "dragon_dust", "dragon_chunk", "essence_false_life", "essence_shallow_grave", "essence_lost_time",
            "essence_everlasting_durability", "essence_scornful_oblivion", "essence_fickle_permanence",
            "dimensional_singularity", "tc_dust_air", "tc_dust_fire", "tc_dust_water", "tc_dust_earth",
            "tc_dust_order", "tc_dust_entropy");

    public static final Map<MagicBeesFrameType, DeferredItem<MagicBeesFrameItem>> FRAMES;

    public static final DeferredItem<MoonDialItem> MOON_DIAL = ITEMS.registerItem("moondial", MoonDialItem::new);
    public static final DeferredItem<MysteriousMagnetItem> MYSTERIOUS_MAGNET = ITEMS.registerItem("mysteriousmagnet", MysteriousMagnetItem::new);

    public static final DeferredItem<ManasteelScoopItem> MANASTEEL_SCOOP =
            ITEMS.register("manasteelscoop", ManasteelScoopItem::new);
    public static final DeferredItem<ManasteelGrafterItem> MANASTEEL_GRAFTER =
            ITEMS.register("manasteelgrafter", ManasteelGrafterItem::new);

    public static final DeferredItem<Item> JELLY_BABIES = ITEMS.registerItem(
            "jelly_babies",
            Item::new,
            new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(1)
                    .saturationModifier(0.0f)
                    .alwaysEdible()
                    .effect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 5, 1), 1.0f)
                    .build()));

    public static final DeferredItem<BlockItem> ENCHANTED_EARTH = ITEMS.registerSimpleBlockItem(MagicBeesBlocks.ENCHANTED_EARTH);
    public static final DeferredItem<BlockItem> EFFECT_JAR = ITEMS.registerSimpleBlockItem(MagicBeesBlocks.EFFECT_JAR);

    public static final DeferredItem<BlockItem> CURIOUS_HIVE = ITEMS.registerSimpleBlockItem(MagicBeesBlocks.CURIOUS_HIVE);
    public static final DeferredItem<BlockItem> UNUSUAL_HIVE = ITEMS.registerSimpleBlockItem(MagicBeesBlocks.UNUSUAL_HIVE);
    public static final DeferredItem<BlockItem> RESONANT_HIVE = ITEMS.registerSimpleBlockItem(MagicBeesBlocks.RESONANT_HIVE);
    public static final DeferredItem<BlockItem> DEEP_HIVE = ITEMS.registerSimpleBlockItem(MagicBeesBlocks.DEEP_HIVE);
    public static final DeferredItem<BlockItem> INFERNAL_HIVE = ITEMS.registerSimpleBlockItem(MagicBeesBlocks.INFERNAL_HIVE);
    public static final DeferredItem<BlockItem> OBLIVION_HIVE = ITEMS.registerSimpleBlockItem(MagicBeesBlocks.OBLIVION_HIVE);

    static {
        EnumMap<MagicBeesFrameType, DeferredItem<MagicBeesFrameItem>> frames = new EnumMap<>(MagicBeesFrameType.class);
        for (MagicBeesFrameType type : MagicBeesFrameType.values()) {
            frames.put(type, ITEMS.register("frame_" + type.serializedName(), () -> new MagicBeesFrameItem(type)));
        }
        FRAMES = Collections.unmodifiableMap(frames);
    }

    private MagicBeesItems() {
    }

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }

    public static DeferredItem<Item> comb(String name) {
        return required(COMBS, name);
    }

    public static DeferredItem<Item> drop(String name) {
        return required(DROPS, name);
    }

    public static DeferredItem<Item> pollen(String name) {
        return required(POLLEN, name);
    }

    public static DeferredItem<Item> resource(String name) {
        return required(RESOURCES, name);
    }

    public static DeferredItem<Item> propolis(String name) {
        return required(PROPOLIS, name);
    }

    public static DeferredItem<Item> nugget(String name) {
        return required(NUGGETS, name);
    }

    private static Map<String, DeferredItem<Item>> variants(String prefix, String... names) {
        Map<String, DeferredItem<Item>> result = new LinkedHashMap<>();
        for (String name : names) {
            result.put(name, ITEMS.registerSimpleItem(prefix + "_" + name));
        }
        return Collections.unmodifiableMap(result);
    }

    private static DeferredItem<Item> required(Map<String, DeferredItem<Item>> map, String name) {
        DeferredItem<Item> item = map.get(name);
        if (item == null) {
            throw new IllegalArgumentException("Unknown Magic Bees item variant: " + name);
        }
        return item;
    }
}

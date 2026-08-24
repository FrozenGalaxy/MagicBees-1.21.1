package eu.dsix.magicbees.gametest;

import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** Independent executable ledger transcribed from the Magic Bees 1.12 acquisition and centrifuge registrations. */
public final class LegacyBeeParityData {
    public enum Acquisition {
        NATURAL_HIVE,
        BOTANIA_CONVERSION
    }

    public record Conversion(String inputSpecies, String recipePath) {
    }

    public record Product(String itemId, float chance) {
        public ResourceLocation item() {
            return ResourceLocation.parse(itemId);
        }
    }

    public record Centrifuge(String recipePath, String requiredMod, List<Product> products) {
    }

    public static final Map<String, Acquisition> NON_BREEDING_ACQUISITIONS = Map.ofEntries(
            Map.entry("mystical", Acquisition.NATURAL_HIVE),
            Map.entry("sorcerous", Acquisition.NATURAL_HIVE),
            Map.entry("unusual", Acquisition.NATURAL_HIVE),
            Map.entry("attuned", Acquisition.NATURAL_HIVE),
            Map.entry("infernal", Acquisition.NATURAL_HIVE),
            Map.entry("oblivion", Acquisition.NATURAL_HIVE),
            Map.entry("bot_botanic", Acquisition.BOTANIA_CONVERSION),
            Map.entry("bot_vazbee", Acquisition.BOTANIA_CONVERSION),
            Map.entry("bot_alfheim", Acquisition.BOTANIA_CONVERSION)
    );

    public static final Map<String, Conversion> BOTANIA_CONVERSIONS = Map.of(
            "bot_botanic", new Conversion("bot_rooted", "botania/botanic_mana"),
            "bot_vazbee", new Conversion("bot_floral", "botania/vazbee_mana"),
            "bot_alfheim", new Conversion("bot_dreaming", "botania/alfheim_trade")
    );

    /** Legacy primal Thaumic mutations intentionally breed a species with itself around the matching crystal. */
    public static final Set<String> LEGACY_SELF_PARENT_BREEDING = Set.of(
            "tc_air", "tc_fire", "tc_water", "tc_earth", "tc_order", "tc_entropy"
    );

    public static final Map<String, Centrifuge> CENTRIFUGE = Map.ofEntries(
            comb("mundane", products("forestry:beeswax", .90f, "forestry:honey_drop", .60f, "magicbees:wax_magic", .10f)),
            comb("molten", products("forestry:refractory_wax", .86f, "forestry:honey_drop", .087f)),
            comb("forgotten", products("magicbees:wax_amnesic", .50f, "forestry:pulsating_propolis", .50f, "forestry:honey_drop", .40f)),
            comb("occult", products("magicbees:wax_magic", 1f, "forestry:honey_drop", .60f)),
            comb("otherworldly", products("forestry:beeswax", .50f, "magicbees:wax_magic", .22f, "forestry:honey_drop", 1f)),
            comb("papery", products("forestry:beeswax", .80f, "magicbees:wax_magic", .20f, "minecraft:paper", .057f)),
            comb("intellect", products("magicbees:wax_magic", .90f, "forestry:honeydew", .40f, "magicbees:drop_intellect", .10f)),
            comb("furtive", products("forestry:beeswax", .90f, "forestry:propolis", .20f, "forestry:honeydew", .35f)),
            comb("soul", products("magicbees:wax_soul", .95f, "forestry:honeydew", .26f)),
            comb("temporal", products("magicbees:wax_magic", 1f, "magicbees:pollen_phased", .055f, "forestry:honeydew", .60f)),
            comb("transmuted", products("forestry:beeswax", .80f, "magicbees:wax_magic", .80f, "magicbees:propolis_unstable", .15f)),
            comb("airy", products("magicbees:wax_magic", 1f, "minecraft:feather", .60f)),
            comb("firey", products("magicbees:wax_magic", 1f, "minecraft:blaze_powder", .60f)),
            comb("watery", products("magicbees:wax_magic", 1f, "minecraft:ink_sac", .60f)),
            comb("earthy", products("magicbees:wax_magic", 1f, "minecraft:clay_ball", .60f)),
            comb("te_destabilized", products("magicbees:wax_magic", .55f, "magicbees:drop_destabilized", .22f)),
            comb("te_carbon", products("magicbees:wax_magic", .55f, "magicbees:drop_carbon", .22f)),
            comb("te_lux", products("magicbees:wax_magic", .55f, "magicbees:drop_lux", .22f)),
            thaumComb("tc_air", products("magicbees:wax_magic", 1f, "minecraft:feather", .60f, "magicbees:propolis_air", .80f)),
            thaumComb("tc_fire", products("magicbees:wax_magic", 1f, "minecraft:blaze_powder", .60f, "magicbees:propolis_fire", .80f)),
            thaumComb("tc_water", products("magicbees:wax_magic", 1f, "minecraft:light_blue_dye", .60f, "magicbees:propolis_water", .80f)),
            thaumComb("tc_earth", products("magicbees:wax_magic", 1f, "minecraft:clay_ball", .60f, "magicbees:propolis_earth", .80f)),
            thaumComb("tc_order", products("magicbees:wax_magic", 1f, "minecraft:redstone", .60f, "magicbees:propolis_order", .80f)),
            thaumComb("tc_entropy", products("magicbees:wax_magic", 1f, "minecraft:gunpowder", .60f, "magicbees:propolis_entropy", .80f))
    );

    private LegacyBeeParityData() {
    }

    private static Map.Entry<String, Centrifuge> comb(String name, List<Product> products) {
        return Map.entry(name, new Centrifuge("centrifuge/" + name + "_comb", null, products));
    }

    private static Map.Entry<String, Centrifuge> thaumComb(String name, List<Product> products) {
        return Map.entry(name, new Centrifuge("thaumaturge/centrifuge_" + name, "thaumaturge", products));
    }

    private static List<Product> products(Object... values) {
        if ((values.length & 1) != 0) {
            throw new IllegalArgumentException("Product ledger requires item/chance pairs");
        }
        java.util.ArrayList<Product> products = new java.util.ArrayList<>(values.length / 2);
        for (int index = 0; index < values.length; index += 2) {
            products.add(new Product((String) values[index], ((Number) values[index + 1]).floatValue()));
        }
        return List.copyOf(products);
    }
}

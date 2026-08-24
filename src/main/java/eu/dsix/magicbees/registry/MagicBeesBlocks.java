package eu.dsix.magicbees.registry;

import eu.dsix.magicbees.MagicBees;
import eu.dsix.magicbees.block.EnchantedEarthBlock;
import eu.dsix.magicbees.block.EffectJarBlock;
import eu.dsix.magicbees.block.MagicBeeHiveBlock;
import eu.dsix.magicbees.forestry.MagicBeeSpecies;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MagicBeesBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MagicBees.MOD_ID);

    public static final DeferredBlock<EnchantedEarthBlock> ENCHANTED_EARTH = BLOCKS.register(
            "enchanted_earth",
            () -> new EnchantedEarthBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT)));

    public static final DeferredBlock<EffectJarBlock> EFFECT_JAR = BLOCKS.register("effectjar", () ->
            new EffectJarBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).strength(0.1F, 1.5F)));

    // The original 1.12 block used metadata for six variants. Modern registries require stable distinct IDs.
    public static final DeferredBlock<MagicBeeHiveBlock> CURIOUS_HIVE = hive("curious_hive", MagicBeeSpecies.MYSTICAL, 12);
    public static final DeferredBlock<MagicBeeHiveBlock> UNUSUAL_HIVE = hive("unusual_hive", MagicBeeSpecies.UNUSUAL, 12);
    public static final DeferredBlock<MagicBeeHiveBlock> RESONANT_HIVE = hive("resonant_hive", MagicBeeSpecies.SORCEROUS, 12);
    public static final DeferredBlock<MagicBeeHiveBlock> DEEP_HIVE = hive("deep_hive", MagicBeeSpecies.ATTUNED, 4);
    public static final DeferredBlock<MagicBeeHiveBlock> INFERNAL_HIVE = hive("infernal_hive", MagicBees.id("infernal"), 15);
    public static final DeferredBlock<MagicBeeHiveBlock> OBLIVION_HIVE = hive("oblivion_hive", MagicBees.id("oblivion"), 7);

    private static DeferredBlock<MagicBeeHiveBlock> hive(String id, net.minecraft.resources.ResourceLocation speciesId, int light) {
        return BLOCKS.register(id, () -> new MagicBeeHiveBlock(speciesId, light));
    }

    private MagicBeesBlocks() {
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}

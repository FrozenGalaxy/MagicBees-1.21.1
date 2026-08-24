package magicbees.registry;

import magicbees.MagicBees;
import magicbees.block.entity.MagicBeeHiveBlockEntity;
import magicbees.block.entity.EffectJarBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MagicBeesBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MagicBees.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MagicBeeHiveBlockEntity>> WILD_HIVE =
            BLOCK_ENTITIES.register("wild_hive", () -> BlockEntityType.Builder.of(
                    MagicBeeHiveBlockEntity::new,
                    MagicBeesBlocks.CURIOUS_HIVE.get(),
                    MagicBeesBlocks.UNUSUAL_HIVE.get(),
                    MagicBeesBlocks.RESONANT_HIVE.get(),
                    MagicBeesBlocks.DEEP_HIVE.get(),
                    MagicBeesBlocks.INFERNAL_HIVE.get(),
                    MagicBeesBlocks.OBLIVION_HIVE.get()
            ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EffectJarBlockEntity>> EFFECT_JAR =
            BLOCK_ENTITIES.register("effectjar", () -> BlockEntityType.Builder.of(EffectJarBlockEntity::new, MagicBeesBlocks.EFFECT_JAR.get()).build(null));

    private MagicBeesBlockEntities() {
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}

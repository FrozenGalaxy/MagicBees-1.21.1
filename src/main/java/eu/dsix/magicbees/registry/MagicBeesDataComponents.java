package eu.dsix.magicbees.registry;

import eu.dsix.magicbees.MagicBees;
import eu.dsix.magicbees.item.MagnetState;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MagicBeesDataComponents {
    private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, MagicBees.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<MagnetState>> MAGNET_STATE = COMPONENTS.register(
            "magnet_state", () -> DataComponentType.<MagnetState>builder()
                    .persistent(MagnetState.CODEC)
                    .networkSynchronized(MagnetState.STREAM_CODEC)
                    .cacheEncoding()
                    .build());

    private MagicBeesDataComponents() {}

    public static void register(IEventBus bus) {
        COMPONENTS.register(bus);
    }
}

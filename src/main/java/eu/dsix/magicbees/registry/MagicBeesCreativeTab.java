package eu.dsix.magicbees.registry;

import eu.dsix.magicbees.MagicBees;
import eu.dsix.magicbees.item.MagicBeesFrameType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MagicBeesCreativeTab {
    private static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MagicBees.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN = TABS.register(
            "magicbees",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.magicbees"))
                    .icon(() -> new ItemStack(MagicBeesItems.FRAMES.get(MagicBeesFrameType.MAGIC).get()))
                    .displayItems((parameters, output) -> MagicBeesItems.ITEMS.getEntries()
                            .forEach(entry -> output.accept(entry.get())))
                    .build());

    private MagicBeesCreativeTab() {
    }

    public static void register(IEventBus bus) {
        TABS.register(bus);
    }
}

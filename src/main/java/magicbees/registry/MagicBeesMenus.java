package magicbees.registry;

import magicbees.MagicBees;
import magicbees.menu.EffectJarMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MagicBeesMenus {
    private static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, MagicBees.MOD_ID);
    public static final DeferredHolder<MenuType<?>, MenuType<EffectJarMenu>> EFFECT_JAR = MENUS.register(
            "effectjar", () -> new MenuType<>((IContainerFactory<EffectJarMenu>) EffectJarMenu::fromNetwork, FeatureFlags.DEFAULT_FLAGS));
    private MagicBeesMenus() {}
    public static void register(IEventBus bus) { MENUS.register(bus); }
}

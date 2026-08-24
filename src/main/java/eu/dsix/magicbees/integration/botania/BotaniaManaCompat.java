package eu.dsix.magicbees.integration.botania;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

import java.lang.reflect.Method;

/** Lazy bridge so the Manasteel tools stay loadable when Botania is absent. */
public final class BotaniaManaCompat {
    private static final ResourceLocation MANASTEEL = ResourceLocation.fromNamespaceAndPath("botania", "manasteel_ingot");
    private static volatile boolean resolved;
    private static volatile Method instanceMethod;
    private static volatile Method requestMethod;

    private BotaniaManaCompat() {}

    public static boolean isManasteel(ItemStack stack) {
        if (!BuiltInRegistries.ITEM.containsKey(MANASTEEL)) return false;
        Item item = BuiltInRegistries.ITEM.get(MANASTEEL);
        return !stack.isEmpty() && stack.is(item);
    }

    public static boolean repairOne(ItemStack tool, Player player, int cost) {
        if (player.level().isClientSide() || !ModList.get().isLoaded("botania")) return false;
        try {
            resolve();
            if (instanceMethod == null || requestMethod == null) return false;
            Object handler = instanceMethod.invoke(null);
            return Boolean.TRUE.equals(requestMethod.invoke(handler, tool, player, cost, true));
        } catch (ReflectiveOperationException | LinkageError ignored) {
            return false;
        }
    }

    private static void resolve() throws ReflectiveOperationException {
        if (resolved) return;
        synchronized (BotaniaManaCompat.class) {
            if (resolved) return;
            Class<?> api = Class.forName("vazkii.botania.api.mana.ManaItemHandler");
            instanceMethod = api.getMethod("instance");
            requestMethod = api.getMethod("requestManaExactForTool", ItemStack.class, Player.class, int.class, boolean.class);
            resolved = true;
        }
    }
}

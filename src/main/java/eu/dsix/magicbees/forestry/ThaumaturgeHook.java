package eu.dsix.magicbees.forestry;

import net.neoforged.fml.ModList;
import net.neoforged.bus.api.IEventBus;

import java.lang.reflect.Method;

/** Loads the Thaumaturge integration only when its optional mod is present. */
public final class ThaumaturgeHook {
    private static final String BOOTSTRAP = "eu.dsix.magicbees.integration.thaumaturge.ThaumaturgeIntegration";

    private ThaumaturgeHook() {
    }

    static void invoke(String methodName, Object argument) {
        if (!ModList.get().isLoaded("thaumaturge")) {
            return;
        }
        try {
            Class<?> integration = Class.forName(BOOTSTRAP, true, ThaumaturgeHook.class.getClassLoader());
            Method method = null;
            for (Method candidate : integration.getMethods()) {
                if (candidate.getName().equals(methodName)
                        && candidate.getParameterCount() == 1
                        && candidate.getParameterTypes()[0].isInstance(argument)) {
                    method = candidate;
                    break;
                }
            }
            if (method == null) {
                throw new NoSuchMethodException(methodName);
            }
            method.invoke(null, argument);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Unable to initialize optional Thaumaturge integration", exception);
        }
    }

    public static void registerItems(IEventBus modBus) {
        if (!ModList.get().isLoaded("thaumaturge")) {
            return;
        }
        invoke("registerItems", modBus);
    }
}
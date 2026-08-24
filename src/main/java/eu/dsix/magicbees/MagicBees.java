package eu.dsix.magicbees;

import com.mojang.logging.LogUtils;
import eu.dsix.magicbees.config.MagicBeesConfig;
import eu.dsix.magicbees.registry.MagicBeesBlocks;
import eu.dsix.magicbees.registry.MagicBeesBlockEntities;
import eu.dsix.magicbees.registry.MagicBeesCreativeTab;
import eu.dsix.magicbees.registry.MagicBeesItems;
import eu.dsix.magicbees.gametest.MagicBeesGameTests;
import eu.dsix.magicbees.data.MagicBeeSpeciesProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.slf4j.Logger;

@Mod(MagicBees.MOD_ID)
public final class MagicBees {
    public static final String MOD_ID = "magicbees";
    public static final String MOD_NAME = "Magic Bees";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MagicBees(IEventBus modBus, ModContainer container) {
        modBus.addListener(RegisterGameTestsEvent.class, event -> event.register(MagicBeesGameTests.class));
        modBus.addListener(MagicBees::gatherData);
        eu.dsix.magicbees.forestry.MagicBeesMutationConditionTypes.register(modBus);
        eu.dsix.magicbees.registry.MagicBeesDataComponents.register(modBus);
        eu.dsix.magicbees.registry.MagicBeesRecipeSerializers.register(modBus);
        MagicBeesBlocks.register(modBus);
        eu.dsix.magicbees.forestry.ThaumaturgeHook.registerItems(modBus);
        MagicBeesItems.register(modBus);
        MagicBeesBlockEntities.register(modBus);
        eu.dsix.magicbees.registry.MagicBeesMenus.register(modBus);
        registerBotaniaCompat(modBus);
        MagicBeesCreativeTab.register(modBus);
        MagicBeesConfig.register(container);
    }

    private static void registerBotaniaCompat(IEventBus modBus) {
        if (!ModList.get().isLoaded("botania")) {
            return;
        }
        try {
            Class<?> integration = Class.forName("eu.dsix.magicbees.integration.botania.BotaniaIntegration");
            integration.getMethod("register", IEventBus.class).invoke(null, modBus);
            LOGGER.info("Registered optional Botania integration");
        } catch (ReflectiveOperationException | LinkageError exception) {
            throw new IllegalStateException("Botania is loaded but Magic Bees could not initialize its Botania integration", exception);
        }
    }

    private static void gatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                new MagicBeeSpeciesProvider(event.getGenerator().getPackOutput(), event.getLookupProvider())
        );
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}

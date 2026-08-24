package eu.dsix.magicbees.client;

import eu.dsix.magicbees.MagicBees;
import eu.dsix.magicbees.registry.MagicBeesItems;
import eu.dsix.magicbees.client.renderer.EffectJarRenderer;
import eu.dsix.magicbees.client.screen.EffectJarScreen;
import eu.dsix.magicbees.item.MysteriousMagnetItem;
import eu.dsix.magicbees.registry.MagicBeesBlockEntities;
import eu.dsix.magicbees.registry.MagicBeesMenus;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.Map;

@EventBusSubscriber(modid = MagicBees.MOD_ID, value = Dist.CLIENT)
public final class MagicBeesClientEvents {
    private static final ModelResourceLocation OLD_EFFECT_JAR_MODEL =
            ModelResourceLocation.standalone(MagicBees.id("block/effectjar_old"));
    private static final Map<String, int[]> COMBS = Map.ofEntries(
            Map.entry("mundane", new int[]{0xFF9859, 0xFFC58E}),
            Map.entry("molten", new int[]{0xCC3333, 0x1E160E}),
            Map.entry("occult", new int[]{0x9872FF, 0x2D2D2D}),
            Map.entry("otherworldly", new int[]{0x3EE0D8, 0x3A3820}),
            Map.entry("transmuted", new int[]{0xE5425D, 0x323291}),
            Map.entry("papery", new int[]{0xBCA664, 0x35332E}),
            Map.entry("soul", new int[]{0x7F7171, 0x876D53}),
            Map.entry("furtive", new int[]{0xB7ACB7, 0x636363}),
            Map.entry("intellect", new int[]{0x0092E9, 0x618FFF}),
            Map.entry("temporal", new int[]{0x2F9381, 0x773C31}),
            Map.entry("forgotten", new int[]{0xB191D8, 0x35443B}),
            Map.entry("airy", new int[]{0xFFFF7E, 0x606308}),
            Map.entry("firey", new int[]{0xFF3C01, 0x5B0D10}),
            Map.entry("watery", new int[]{0x0090FF, 0x102F6B}),
            Map.entry("earthy", new int[]{0x00A000, 0x043004}),
            Map.entry("te_destabilized", new int[]{0xCC002C, 0x6B0118}),
            Map.entry("te_carbon", new int[]{0x454545, 0x0F0F0F}),
            Map.entry("te_lux", new int[]{0xF5F3A4, 0xC9C87D}),
            Map.entry("tc_air", new int[]{0xFFDF14, 0x707019}),
            Map.entry("tc_fire", new int[]{0xE21802, 0x3F0E12}),
            Map.entry("tc_water", new int[]{0x00B6FF, 0x0F373D}),
            Map.entry("tc_earth", new int[]{0x28D328, 0x333004}),
            Map.entry("tc_order", new int[]{0xDDDDFF, 0x9D9DB5}),
            Map.entry("tc_entropy", new int[]{0x555577, 0x2D2D56})
    );
    private static final Map<String, int[]> PROPOLIS = Map.ofEntries(
            Map.entry("unstable", new int[]{0xEFB492, 0xC2BEA7}),
            Map.entry("air", new int[]{0xA19E10, 0xC2BEA7}),
            Map.entry("fire", new int[]{0x95132F, 0xC2BEA7}),
            Map.entry("water", new int[]{0x1054A1, 0xC2BEA7}),
            Map.entry("earth", new int[]{0x00A000, 0xC2BEA7}),
            Map.entry("order", new int[]{0xDDDDFF, 0xC2BEA7}),
            Map.entry("entropy", new int[]{0x555577, 0xC2BEA7})
    );
    private static final Map<String, int[]> POLLEN = Map.of(
            "unusual", new int[]{0xD8417B, 0xA03059},
            "phased", new int[]{0x4974B4, 0x456BA5});

    private MagicBeesClientEvents() {
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(MagicBeesItems.MOON_DIAL.get(), MagicBees.id("moon_phase"),
                    (stack, level, entity, seed) -> {
                        var renderLevel = level != null ? level : net.minecraft.client.Minecraft.getInstance().level;
                        return renderLevel == null ? 0.0F : renderLevel.getMoonPhase();
                    });
            ItemProperties.register(MagicBeesItems.MYSTERIOUS_MAGNET.get(), MagicBees.id("active"),
                    (stack, level, entity, seed) -> MysteriousMagnetItem.active(stack) ? 1.0F : 0.0F);
        });
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MagicBeesMenus.EFFECT_JAR.get(), EffectJarScreen::new);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MagicBeesBlockEntities.EFFECT_JAR.get(), EffectJarRenderer::new);
    }

    @SubscribeEvent
    public static void registerAdditionalModels(ModelEvent.RegisterAdditional event) {
        event.register(OLD_EFFECT_JAR_MODEL);
    }

    @SubscribeEvent
    public static void modifyBakedModels(ModelEvent.ModifyBakingResult event) {
        if (!eu.dsix.magicbees.config.MagicBeesConfig.CLIENT.oldJarModel.get()) return;
        var oldJarModel = event.getModels().get(OLD_EFFECT_JAR_MODEL);
        if (oldJarModel == null) {
            MagicBees.LOGGER.error("Legacy Effect Jar model was not baked; keeping the default model");
            return;
        }

        ResourceLocation jarId = MagicBees.id("effectjar");
        event.getModels().put(new ModelResourceLocation(jarId, ""), oldJarModel);
        event.getModels().put(ModelResourceLocation.inventory(jarId), oldJarModel);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        register(event, MagicBeesItems.COMBS, COMBS);
        register(event, MagicBeesItems.PROPOLIS, PROPOLIS);
        register(event, MagicBeesItems.POLLEN, POLLEN);
    }

    private static void register(RegisterColorHandlersEvent.Item event,
                                 Map<String, net.neoforged.neoforge.registries.DeferredItem<Item>> items,
                                 Map<String, int[]> colors) {
        for (Map.Entry<String, net.neoforged.neoforge.registries.DeferredItem<Item>> entry : items.entrySet()) {
            int[] pair = colors.get(entry.getKey());
            if (pair != null) {
                event.register((stack, tintIndex) -> tint(pair, tintIndex), entry.getValue().get());
            }
        }
    }

    private static int tint(int[] pair, int tintIndex) {
        return tintIndex >= 0 && tintIndex < pair.length ? 0xFF000000 | pair[tintIndex] : 0xFFFFFFFF;
    }
}

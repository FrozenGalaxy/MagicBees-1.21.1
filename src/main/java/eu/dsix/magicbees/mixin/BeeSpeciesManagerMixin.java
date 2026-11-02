package eu.dsix.magicbees.mixin;

import com.google.gson.JsonElement;
import eu.dsix.magicbees.config.MagicBeesConfig;
import eu.dsix.magicbees.forestry.BeeSpeciesRuntimePatcher;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import forestry.apiculture.bees.genetics.BeeSpeciesManager;
import forestry.core.engine.genetics.SpeciesManager;

import java.util.Map;

/**
 * Restores the legacy default removeUnneededBees=true registration boundary for ForestryCE's datapack
 * species loader. Magic Bees 1.12 did not register species whose defining integration was unavailable. ForestryCE
 * now eagerly decodes every bee_species JSON before addon callbacks, so filter only those legacy optional species
 * at the raw decode boundary. The definitions remain shipped in the jar and become live automatically when their
 * defining mod is present.
 */
@Mixin(SpeciesManager.class)
public abstract class BeeSpeciesManagerMixin {
    @ModifyVariable(
            method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;"
                    + "Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Map<ResourceLocation, JsonElement> magicbees$filterInactiveOptionalSpecies(
            Map<ResourceLocation, JsonElement> definitions) {
        if (!((Object) this instanceof BeeSpeciesManager)) {
            return definitions;
        }

        ModList mods = ModList.get();
        boolean thaumaturge = mods.isLoaded("thaumaturge");
        boolean thermal = mods.isLoaded("thermal_foundation");
        boolean botania = mods.isLoaded("botania");
        boolean ae2 = mods.isLoaded("ae2");

        return BeeSpeciesRuntimePatcher.patch(definitions,
                new BeeSpeciesRuntimePatcher.Loadout(thaumaturge, thermal, botania, ae2,
                        MagicBeesConfig.COMMON.showAllBees.get()),
                id -> BuiltInRegistries.ITEM.containsKey(id),
                BeeSpeciesRuntimePatcher::firstItemInTag);
    }
}

package eu.dsix.magicbees.forestry;

import forestry.api.plugin.IApicultureRegistration;
import forestry.api.plugin.IForestryPlugin;
import forestry.api.plugin.IGeneticRegistration;
import eu.dsix.magicbees.MagicBees;
import net.minecraft.resources.ResourceLocation;

/** ForestryCE 3.x addon entry point loaded through Java ServiceLoader. */
public final class MagicBeesForestryPlugin implements IForestryPlugin {
    @Override
    public void registerGenetics(IGeneticRegistration genetics) {
        MagicBeeTaxa.registerInitial(genetics);
        ThaumaturgeHook.invoke("registerGenetics", genetics);
    }

    @Override
    public void registerApiculture(IApicultureRegistration apiculture) {
        MagicBeeActivityTypes.register(apiculture);
        MagicBeeSpecies.registerInitial(apiculture);
        MagicBeeHives.registerInitial(apiculture);
        ThaumaturgeHook.invoke("registerApiculture", apiculture);
    }

    @Override
    public ResourceLocation id() {
        return MagicBees.id("forestry");
    }
}

package magicbees.forestry;

import forestry.api.apiculture.IActivityType;
import forestry.api.apiculture.LightPreference;
import forestry.api.core.ForestryError;
import forestry.api.core.IError;
import forestry.api.plugin.IApicultureRegistration;
import magicbees.MagicBees;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

/** Activity compatibility values needed to preserve legacy Magic Bees chromosome semantics. */
final class MagicBeeActivityTypes {
    /**
     * Replacement for the removed 1.12 NEVER_SLEEPS=true recessive boolean allele.
     *
     * ForestryCE folds sleep behavior into the ACTIVITY reference chromosome. Its built-in metaturnal value has the
     * correct 24-hour behavior but is dominant, while Magic Bees explicitly used TRUE_RECESSIVE. Keeping a separate
     * always-active recessive value preserves both behavior and inheritance.
     */
    static final ResourceLocation NEVER_SLEEPS = MagicBees.id("activity_never_sleeps");

    private MagicBeeActivityTypes() {
    }

    static void register(IApicultureRegistration apiculture) {
        apiculture.registerActivityType(NEVER_SLEEPS, NeverSleepsActivityType.INSTANCE);
    }

    private enum NeverSleepsActivityType implements IActivityType {
        INSTANCE;

        @Override
        public boolean isDominant() {
            return false;
        }

        @Override
        public boolean isActive(long gameTime, long dayTime, BlockPos pos) {
            return true;
        }

        @Override
        public IError getInactiveError(long gameTime, long dayTime, BlockPos pos) {
            // isActive() is unconditional, so Forestry never displays this error.
            return ForestryError.INVALID;
        }

        @Override
        public LightPreference getLightPreference() {
            return LightPreference.ANY;
        }
    }
}

package eu.dsix.magicbees.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;

/**
 * Magic Bees' original eight-phase moon clock. The 1.12 utility deliberately
 * offset the phase boundary by 6000 ticks; keeping that offset preserves both
 * the Moon Dial and moon-gated breeding behavior.
 */
public enum LegacyMoonPhase {
    FULL,
    WANING_GIBBOUS,
    WANING_HALF,
    WANING_CRESCENT,
    NEW,
    WAXING_CRESCENT,
    WAXING_HALF,
    WAXING_GIBBOUS;

    public static LegacyMoonPhase fromTime(long time) {
        long legacyDay = (time - 6000L) / 24000L;
        return values()[Math.floorMod((int) legacyDay, values().length)];
    }

    public static LegacyMoonPhase fromLevel(Level level) {
        return fromTime(level.getDayTime());
    }

    public boolean isBetweenInclusive(LegacyMoonPhase start, LegacyMoonPhase end) {
        int value = ordinal();
        int first = start.ordinal();
        int last = end.ordinal();
        if (first <= last) {
            return value >= first && value <= last;
        }
        return value >= first || value <= last;
    }

    public Component displayName() {
        return Component.translatable("magicbees.moon_phase." + ordinal());
    }
}

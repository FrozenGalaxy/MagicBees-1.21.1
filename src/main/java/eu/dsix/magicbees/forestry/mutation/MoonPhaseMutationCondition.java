package eu.dsix.magicbees.forestry.mutation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.core.climate.IClimateProvider;
import forestry.api.core.genetics.IGenome;
import forestry.api.core.genetics.IMutation;
import forestry.api.core.genetics.IMutationCondition;
import forestry.api.core.genetics.MutationConditionType;
import eu.dsix.magicbees.util.LegacyMoonPhase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

/**
 * Generalized form of the old MoonPhaseMutationBonus and
 * MoonPhaseMutationRestriction conditions. A bonus uses outsideMultiplier=1;
 * a restriction uses outsideMultiplier=0.
 */
public record MoonPhaseMutationCondition(
        LegacyMoonPhase start,
        LegacyMoonPhase end,
        float insideMultiplier,
        float outsideMultiplier
) implements IMutationCondition {
    private static final Codec<LegacyMoonPhase> PHASE_CODEC = Codec.INT.xmap(
            value -> LegacyMoonPhase.values()[Math.floorMod(value, LegacyMoonPhase.values().length)],
            LegacyMoonPhase::ordinal);

    public static final MapCodec<MoonPhaseMutationCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            PHASE_CODEC.fieldOf("start").forGetter(MoonPhaseMutationCondition::start),
            PHASE_CODEC.fieldOf("end").forGetter(MoonPhaseMutationCondition::end),
            Codec.FLOAT.fieldOf("inside_multiplier").forGetter(MoonPhaseMutationCondition::insideMultiplier),
            Codec.FLOAT.fieldOf("outside_multiplier").forGetter(MoonPhaseMutationCondition::outsideMultiplier)
    ).apply(instance, MoonPhaseMutationCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MoonPhaseMutationCondition> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT.map(value -> LegacyMoonPhase.values()[Math.floorMod(value, LegacyMoonPhase.values().length)], LegacyMoonPhase::ordinal), MoonPhaseMutationCondition::start,
            ByteBufCodecs.VAR_INT.map(value -> LegacyMoonPhase.values()[Math.floorMod(value, LegacyMoonPhase.values().length)], LegacyMoonPhase::ordinal), MoonPhaseMutationCondition::end,
            ByteBufCodecs.FLOAT, MoonPhaseMutationCondition::insideMultiplier,
            ByteBufCodecs.FLOAT, MoonPhaseMutationCondition::outsideMultiplier,
            MoonPhaseMutationCondition::new
    );

    public static final MutationConditionType<MoonPhaseMutationCondition> TYPE = new MutationConditionType<>(CODEC, STREAM_CODEC);

    @Override
    public float modifyChance(Level level, BlockPos pos, IMutation<?> mutation, IGenome firstGenome, IGenome secondGenome,
                              IClimateProvider climate, float currentChance) {
        LegacyMoonPhase phase = LegacyMoonPhase.fromLevel(level);
        float multiplier = phase.isBetweenInclusive(start, end) ? insideMultiplier : outsideMultiplier;
        return currentChance * multiplier;
    }

    @Override
    public Component getDescription() {
        return Component.translatable("magicbees.mutation.condition.moon_phase",
                start.displayName(), end.displayName(), insideMultiplier, outsideMultiplier);
    }

    @Override
    public MutationConditionType<?> type() {
        return TYPE;
    }
}

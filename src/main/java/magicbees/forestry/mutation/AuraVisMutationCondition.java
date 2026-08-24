package magicbees.forestry.mutation;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.core.climate.IClimateProvider;
import forestry.api.core.genetics.IGenome;
import forestry.api.core.genetics.IMutation;
import forestry.api.core.genetics.IMutationCondition;
import forestry.api.core.genetics.MutationConditionType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

import java.lang.reflect.Method;

public record AuraVisMutationCondition(int minimum) implements IMutationCondition {
    public static final MapCodec<AuraVisMutationCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("minimum").forGetter(AuraVisMutationCondition::minimum)
    ).apply(instance, AuraVisMutationCondition::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AuraVisMutationCondition> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, AuraVisMutationCondition::minimum,
                    AuraVisMutationCondition::new);

    public static final MutationConditionType<AuraVisMutationCondition> TYPE =
            new MutationConditionType<>(CODEC, STREAM_CODEC);

    @Override
    public float modifyChance(Level level, BlockPos pos, IMutation<?> mutation, IGenome firstGenome,
                              IGenome secondGenome, IClimateProvider climate, float currentChance) {
        if (!net.neoforged.fml.ModList.get().isLoaded("thaumaturge")) {
            return 0.0f;
        }
        try {
            Class<?> auraHelper = Class.forName("com.leclowndu93150.thaumaturge.api.aura.AuraHelper");
            Method getVis = auraHelper.getMethod("getVis", Level.class, BlockPos.class);
            float vis = ((Number) getVis.invoke(null, level, pos)).floatValue();
            return vis >= minimum ? currentChance : 0.0f;
        } catch (ReflectiveOperationException exception) {
            return 0.0f;
        }
    }

    @Override
    public Component getDescription() {
        return Component.translatable("magicbees.mutation.condition.aura_vis", minimum);
    }

    @Override
    public MutationConditionType<?> type() {
        return TYPE;
    }
}

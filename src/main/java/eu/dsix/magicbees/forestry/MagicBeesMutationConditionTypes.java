package eu.dsix.magicbees.forestry;

import forestry.api.ForestryRegistries;
import forestry.api.core.genetics.MutationConditionType;
import eu.dsix.magicbees.MagicBees;
import eu.dsix.magicbees.forestry.mutation.AuraVisMutationCondition;
import eu.dsix.magicbees.forestry.mutation.MoonPhaseMutationCondition;
import eu.dsix.magicbees.forestry.mutation.RequiredBlockTagMutationCondition;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * ForestryCE 3.0.0-alpha7 exposes mutation condition types through a public NeoForge registry
 * ({@code forestry:mutation_condition_type}). Custom conditions now register the same way as
 * items/blocks, through a {@link DeferredRegister} on the mod event bus, so no internal bridge
 * into Forestry's implementation package is required anymore.
 */
public final class MagicBeesMutationConditionTypes {
    public static final DeferredRegister<MutationConditionType<?>> MUTATION_CONDITION_TYPES =
            DeferredRegister.create(ForestryRegistries.Keys.MUTATION_CONDITION_TYPE, MagicBees.MOD_ID);

    public static final DeferredHolder<MutationConditionType<?>, MutationConditionType<MoonPhaseMutationCondition>> MOON_PHASE =
            MUTATION_CONDITION_TYPES.register("moon_phase", () -> MoonPhaseMutationCondition.TYPE);

    public static final DeferredHolder<MutationConditionType<?>, MutationConditionType<AuraVisMutationCondition>> AURA_VIS =
            MUTATION_CONDITION_TYPES.register("aura_vis", () -> AuraVisMutationCondition.TYPE);

    public static final DeferredHolder<MutationConditionType<?>, MutationConditionType<RequiredBlockTagMutationCondition>> REQUIRED_BLOCK_TAG =
            MUTATION_CONDITION_TYPES.register("required_block_tag", () -> RequiredBlockTagMutationCondition.TYPE);

    private MagicBeesMutationConditionTypes() {
    }

    public static void register(IEventBus modBus) {
        MUTATION_CONDITION_TYPES.register(modBus);
    }
}

package eu.dsix.magicbees.item;

import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.genetics.IBeeSpecies;
import forestry.api.core.genetics.IGenome;
import forestry.api.core.genetics.IMutation;
import net.minecraft.core.Vec3i;

public enum MagicBeesFrameType implements IBeeModifier {
    MAGIC("magic", 240, 1.0f, 1.0f, 1.0f, 2.0f, 1.0f, 0.6f),
    RESILIENT("resilient", 800, 1.0f, 1.0f, 1.0f, 2.0f, 1.0f, 0.5f),
    GENTLE("gentle", 200, 1.0f, 0.7f, 1.5f, 1.4f, 1.0f, 0.01f),
    METABOLIC("metabolic", 130, 1.0f, 1.8f, 1.0f, 1.2f, 1.0f, 1.0f),
    NECROTIC("necrotic", 280, 1.0f, 1.0f, 0.3f, 0.75f, 1.0f, 1.2f),
    TEMPORAL("temporal", 300, 1.0f, 1.0f, 2.5f, 1.0f, 1.0f, 0.8f),
    OBLIVION("oblivion", 50, 1.0f, 1.0f, 0.0001f, 0.0f, 1.0f, 1.0f);

    private final String serializedName;
    private final int durability;
    private final float territory;
    private final float mutation;
    private final float lifespan;
    private final float production;
    private final float pollination;
    private final float geneticDecay;

    MagicBeesFrameType(String serializedName, int durability, float territory, float mutation,
                       float lifespan, float production, float pollination, float geneticDecay) {
        this.serializedName = serializedName;
        this.durability = durability;
        this.territory = territory;
        this.mutation = mutation;
        this.lifespan = lifespan;
        this.production = production;
        this.pollination = pollination;
        this.geneticDecay = geneticDecay;
    }

    public String serializedName() {
        return serializedName;
    }

    public int durability() {
        return durability;
    }

    public float territoryModifier() {
        return territory;
    }

    public float mutationModifier() {
        return mutation;
    }

    public float lifespanModifier() {
        return lifespan;
    }

    public float productionModifier() {
        return production;
    }

    public float pollinationModifier() {
        return pollination;
    }

    public float geneticDecayModifier() {
        return geneticDecay;
    }

    @Override
    public Vec3i modifyTerritory(IGenome genome, Vec3i current) {
        if (territory == 1.0f) {
            return current;
        }
        return new Vec3i(
                Math.max(1, Math.round(current.getX() * territory)),
                Math.max(1, Math.round(current.getY() * territory)),
                Math.max(1, Math.round(current.getZ() * territory)));
    }

    @Override
    public float modifyMutationChance(IGenome genome, IGenome mate, IMutation<IBeeSpecies> mutationRecipe,
                                      float currentChance) {
        return currentChance * mutation;
    }

    @Override
    public float modifyAging(IGenome genome, IGenome mate, float currentAging) {
        // Forestry 1.12 exposed a lifespan multiplier; ForestryCE exposes aging directly.
        // Dividing aging by the old lifespan multiplier preserves the old lifetime relationship.
        return currentAging / lifespan;
    }

    @Override
    public float modifyProductionSpeed(IGenome genome, float currentSpeed) {
        return currentSpeed * production;
    }

    @Override
    public float modifyPollination(IGenome genome, float currentPollination) {
        return currentPollination * pollination;
    }

    @Override
    public float modifyGeneticDecay(IGenome genome, float currentDecay) {
        return currentDecay * geneticDecay;
    }
}

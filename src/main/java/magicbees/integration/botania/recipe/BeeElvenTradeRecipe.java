package magicbees.integration.botania.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.core.genetics.capability.IIndividualHandlerItem;
import forestry.core.platform.util.SpeciesUtil;
import magicbees.integration.botania.BotaniaIntegration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import vazkii.botania.api.recipe.ElvenTradeRecipe;
import vazkii.botania.api.recipe.ProcessingRecipeInput;

import java.util.List;
import java.util.Optional;

/** Legacy Alfheim trade: Dreaming drone in, default-genome Alfheim drone out. */
public record BeeElvenTradeRecipe(ResourceLocation inputSpecies,
                                  ResourceLocation outputSpecies) implements ElvenTradeRecipe {
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, Ingredient.of(BeeLifeStage.DRONE.getItemForm()));
    }

    @Override
    public List<ItemStack> getOutputs() {
        return List.of(SpeciesUtil.getBeeSpecies(outputSpecies).createStack(BeeLifeStage.DRONE));
    }

    @Override
    public Optional<AssemblyResult> tryAssemble(ProcessingRecipeInput input, HolderLookup.Provider registries) {
        for (int slot = 0; slot < input.size(); slot++) {
            ItemStack stack = input.getItem(slot);
            if (IIndividualHandlerItem.getLifeStage(stack) == BeeLifeStage.DRONE
                    && IIndividualHandlerItem.getIndividual(stack) instanceof IBee bee
                    && bee.getSpecies().id().equals(inputSpecies)) {
                return Optional.of(new AssemblyResult(
                        SpeciesUtil.getBeeSpecies(outputSpecies).createStack(BeeLifeStage.DRONE), slot));
            }
        }
        return Optional.empty();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BotaniaIntegration.BEE_ELVEN_SERIALIZER.get();
    }

    @Override
    public ItemStack getToastSymbol() {
        return ItemStack.EMPTY;
    }

    public static final class Serializer implements RecipeSerializer<BeeElvenTradeRecipe> {
        private static final MapCodec<BeeElvenTradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("input_species").forGetter(BeeElvenTradeRecipe::inputSpecies),
                ResourceLocation.CODEC.fieldOf("output_species").forGetter(BeeElvenTradeRecipe::outputSpecies)
        ).apply(instance, BeeElvenTradeRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, BeeElvenTradeRecipe> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, BeeElvenTradeRecipe::inputSpecies,
                ResourceLocation.STREAM_CODEC, BeeElvenTradeRecipe::outputSpecies,
                BeeElvenTradeRecipe::new);

        @Override
        public MapCodec<BeeElvenTradeRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BeeElvenTradeRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

package eu.dsix.magicbees.integration.botania.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.core.genetics.alleles.BeeChromosomes;
import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.core.genetics.alleles.Allele;
import forestry.api.core.genetics.capability.IIndividualHandlerItem;
import forestry.core.platform.util.SpeciesUtil;
import eu.dsix.magicbees.integration.botania.BotaniaIntegration;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import vazkii.botania.api.recipe.ManaInfusionRecipe;
import vazkii.botania.api.recipe.StateIngredient;
import vazkii.botania.common.crafting.StateIngredients;

import java.util.Locale;
import java.util.Map;

/** Botania mana-pool bee conversion that preserves the incoming genome except for species. */
public record BeeManaInfusionRecipe(ResourceLocation inputSpecies,
                                     ResourceLocation outputSpecies,
                                     BeeLifeStage stage,
                                     int mana,
                                     boolean alchemy) implements ManaInfusionRecipe {
    @Override
    public boolean matches(ItemStack stack) {
        return IIndividualHandlerItem.getLifeStage(stack) == stage
                && IIndividualHandlerItem.getIndividual(stack) instanceof IBee bee
                && bee.getSpecies().id().equals(inputSpecies);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return SpeciesUtil.getBeeSpecies(outputSpecies).createStack(stage);
    }

    @Override
    public ItemStack getRecipeOutput(RegistryAccess registries, ItemStack input) {
        if (!(IIndividualHandlerItem.getIndividual(input) instanceof IBee bee)) {
            return ItemStack.EMPTY;
        }
        var genome = bee.getGenome().copyWith(Map.of(BeeChromosomes.SPECIES, Allele.reference(outputSpecies)));
        return bee.copyWithGenome(genome).createStack(stage);
    }

    @Override
    public StateIngredient getRecipeCatalyst() {
        if (!alchemy) {
            return StateIngredients.NONE;
        }
        Block catalyst = BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath("botania", "alchemy_catalyst"));
        return StateIngredients.of(catalyst);
    }

    @Override
    public int getManaToConsume() {
        return mana;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, Ingredient.of(stage.getItemForm()));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BotaniaIntegration.BEE_MANA_SERIALIZER.get();
    }

    @Override
    public ItemStack getToastSymbol() {
        return ItemStack.EMPTY;
    }

    public static final class Serializer implements RecipeSerializer<BeeManaInfusionRecipe> {
        private static final Codec<BeeLifeStage> STAGE_CODEC = Codec.STRING.xmap(
                value -> BeeLifeStage.valueOf(value.toUpperCase(Locale.ROOT)), BeeLifeStage::getSerializedName);
        private static final StreamCodec<RegistryFriendlyByteBuf, BeeLifeStage> STAGE_STREAM_CODEC = new StreamCodec<>() {
            @Override
            public BeeLifeStage decode(RegistryFriendlyByteBuf buffer) {
                return BeeLifeStage.valueOf(ByteBufCodecs.STRING_UTF8.decode(buffer).toUpperCase(Locale.ROOT));
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, BeeLifeStage value) {
                ByteBufCodecs.STRING_UTF8.encode(buffer, value.getSerializedName());
            }
        };

        private static final MapCodec<BeeManaInfusionRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("input_species").forGetter(BeeManaInfusionRecipe::inputSpecies),
                ResourceLocation.CODEC.fieldOf("output_species").forGetter(BeeManaInfusionRecipe::outputSpecies),
                STAGE_CODEC.fieldOf("stage").forGetter(BeeManaInfusionRecipe::stage),
                Codec.INT.fieldOf("mana").forGetter(BeeManaInfusionRecipe::mana),
                Codec.BOOL.optionalFieldOf("alchemy", false).forGetter(BeeManaInfusionRecipe::alchemy)
        ).apply(instance, BeeManaInfusionRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, BeeManaInfusionRecipe> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, BeeManaInfusionRecipe::inputSpecies,
                ResourceLocation.STREAM_CODEC, BeeManaInfusionRecipe::outputSpecies,
                STAGE_STREAM_CODEC, BeeManaInfusionRecipe::stage,
                ByteBufCodecs.VAR_INT, BeeManaInfusionRecipe::mana,
                ByteBufCodecs.BOOL, BeeManaInfusionRecipe::alchemy,
                BeeManaInfusionRecipe::new);

        @Override
        public MapCodec<BeeManaInfusionRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, BeeManaInfusionRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

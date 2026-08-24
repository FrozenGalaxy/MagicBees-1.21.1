package eu.dsix.magicbees.integration.jei;

import eu.dsix.magicbees.MagicBees;
import eu.dsix.magicbees.recipe.ScornfulOblivionRecipe;
import eu.dsix.magicbees.registry.MagicBeesBlocks;
import eu.dsix.magicbees.registry.MagicBeesItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

@JeiPlugin
public final class MagicBeesJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return MagicBees.id("jei"); }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addExtension(ScornfulOblivionRecipe.class, new ScornfulOblivionJeiExtension());
    }

    @Override public void registerRecipes(IRecipeRegistration registry) {
        add(registry, new ItemStack(MagicBeesBlocks.CURIOUS_HIVE.get()), "hive.curious");
        add(registry, new ItemStack(MagicBeesBlocks.UNUSUAL_HIVE.get()), "hive.unusual");
        add(registry, new ItemStack(MagicBeesBlocks.RESONANT_HIVE.get()), "hive.resonant");
        add(registry, new ItemStack(MagicBeesBlocks.DEEP_HIVE.get()), "hive.deep");
        add(registry, new ItemStack(MagicBeesBlocks.INFERNAL_HIVE.get()), "hive.infernal");
        add(registry, new ItemStack(MagicBeesBlocks.OBLIVION_HIVE.get()), "hive.oblivion");
        add(registry, new ItemStack(MagicBeesItems.resource("aromatic_lump").get()), "aromatic_lump");
    }
    private static void add(IRecipeRegistration registry, ItemStack stack, String key) {
        registry.addIngredientInfo(stack, VanillaTypes.ITEM_STACK, Component.translatable("magicbees.jei.description." + key));
    }

    private static final class ScornfulOblivionJeiExtension implements ICraftingCategoryExtension<ScornfulOblivionRecipe> {
        @Override
        public void setRecipe(RecipeHolder<ScornfulOblivionRecipe> recipeHolder, IRecipeLayoutBuilder builder,
                              ICraftingGridHelper craftingGridHelper, IFocusGroup focuses) {
            ItemStack shallowGrave = new ItemStack(MagicBeesItems.resource("essence_shallow_grave").get());
            ItemStack lostTime = new ItemStack(MagicBeesItems.resource("essence_lost_time").get());
            ItemStack skull = new ItemStack(Items.WITHER_SKELETON_SKULL);

            craftingGridHelper.createAndSetInputs(builder, List.of(
                    List.of(shallowGrave),
                    List.of(skull),
                    List.of(lostTime),
                    List.of(skull),
                    List.of(new ItemStack(Items.DRAGON_EGG)),
                    List.of(skull),
                    List.of(lostTime),
                    List.of(skull),
                    List.of(shallowGrave)
            ), 3, 3);
            craftingGridHelper.createAndSetOutputs(builder,
                    List.of(new ItemStack(MagicBeesItems.resource("essence_scornful_oblivion").get())));
        }

        @Override
        public int getWidth(RecipeHolder<ScornfulOblivionRecipe> recipeHolder) {
            return 3;
        }

        @Override
        public int getHeight(RecipeHolder<ScornfulOblivionRecipe> recipeHolder) {
            return 3;
        }
    }
}

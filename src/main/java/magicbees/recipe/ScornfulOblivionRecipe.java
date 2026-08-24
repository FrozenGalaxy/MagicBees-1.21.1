package magicbees.recipe;

import magicbees.registry.MagicBeesItems;
import magicbees.registry.MagicBeesRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public final class ScornfulOblivionRecipe extends CustomRecipe {
    public ScornfulOblivionRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 3 || input.height() != 3) return false;
        return matchesOrientation(input, "essence_shallow_grave", "essence_lost_time")
                || matchesOrientation(input, "essence_lost_time", "essence_shallow_grave");
    }

    private static boolean matchesOrientation(CraftingInput input, String leftCorner, String rightCorner) {
        return is(input, 0, leftCorner) && is(input, 1, Items.WITHER_SKELETON_SKULL) && is(input, 2, rightCorner)
                && is(input, 3, Items.WITHER_SKELETON_SKULL) && input.getItem(4).is(Items.DRAGON_EGG) && is(input, 5, Items.WITHER_SKELETON_SKULL)
                && is(input, 6, rightCorner) && is(input, 7, Items.WITHER_SKELETON_SKULL) && is(input, 8, leftCorner);
    }

    private static boolean is(CraftingInput input, int slot, String resource) {
        return input.getItem(slot).is(MagicBeesItems.resource(resource).get());
    }

    private static boolean is(CraftingInput input, int slot, net.minecraft.world.item.Item item) {
        return input.getItem(slot).is(item);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        return new ItemStack(MagicBeesItems.resource("essence_scornful_oblivion").get());
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> result = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        result.set(4, new ItemStack(Items.DRAGON_EGG));
        return result;
    }

    @Override public boolean canCraftInDimensions(int width, int height) { return width >= 3 && height >= 3; }
    @Override public RecipeSerializer<?> getSerializer() { return MagicBeesRecipeSerializers.SCORNFUL_OBLIVION.get(); }
}

package magicbees.recipe;

import magicbees.config.MagicBeesConfig;
import magicbees.item.MagnetState;
import magicbees.item.MysteriousMagnetItem;
import magicbees.registry.MagicBeesItems;
import magicbees.registry.MagicBeesRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public final class MagnetUpgradeRecipe extends CustomRecipe {
    public MagnetUpgradeRecipe(CraftingBookCategory category) { super(category); }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 3 || input.height() != 3) return false;
        if (!input.getItem(1).is(Items.DIAMOND)
                || !input.getItem(4).is(MagicBeesItems.resource("dimensional_singularity").get())
                || !input.getItem(7).is(Items.REDSTONE_BLOCK)) return false;
        for (int i : new int[]{0,2,3,5,6,8}) {
            if (i != 3 && i != 5 && !input.getItem(i).isEmpty()) return false;
        }
        ItemStack left = input.getItem(3), right = input.getItem(5);
        if (!left.is(MagicBeesItems.MYSTERIOUS_MAGNET.get()) || !right.is(MagicBeesItems.MYSTERIOUS_MAGNET.get())) return false;
        MagnetState a = MysteriousMagnetItem.state(left), b = MysteriousMagnetItem.state(right);
        return !a.active() && !b.active() && a.level() == b.level() && a.level() < MagicBeesConfig.COMMON.magnetMaxLevel.get();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack left = input.getItem(3);
        if (!left.is(MagicBeesItems.MYSTERIOUS_MAGNET.get())) return ItemStack.EMPTY;
        int next = MysteriousMagnetItem.level(left) + 1;
        if (next > MagicBeesConfig.COMMON.magnetMaxLevel.get()) return ItemStack.EMPTY;
        ItemStack out = new ItemStack(MagicBeesItems.MYSTERIOUS_MAGNET.get());
        MysteriousMagnetItem.setState(out, new MagnetState(next, false));
        return out;
    }

    @Override public boolean canCraftInDimensions(int width, int height) { return width >= 3 && height >= 3; }
    @Override public RecipeSerializer<?> getSerializer() { return MagicBeesRecipeSerializers.MAGNET_UPGRADE.get(); }
}

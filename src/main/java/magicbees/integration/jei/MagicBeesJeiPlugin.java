package magicbees.integration.jei;

import magicbees.MagicBees;
import magicbees.registry.MagicBeesBlocks;
import magicbees.registry.MagicBeesItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
public final class MagicBeesJeiPlugin implements IModPlugin {
    @Override public ResourceLocation getPluginUid() { return MagicBees.id("jei"); }
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
}

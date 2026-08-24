package magicbees.registry;

import magicbees.MagicBees;
import magicbees.recipe.MagnetUpgradeRecipe;
import magicbees.recipe.ScornfulOblivionRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class MagicBeesRecipeSerializers {
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MagicBees.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ScornfulOblivionRecipe>> SCORNFUL_OBLIVION =
            SERIALIZERS.register("scornful_oblivion", () -> new SimpleCraftingRecipeSerializer<>(ScornfulOblivionRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<MagnetUpgradeRecipe>> MAGNET_UPGRADE =
            SERIALIZERS.register("magnet_upgrade", () -> new SimpleCraftingRecipeSerializer<>(MagnetUpgradeRecipe::new));

    private MagicBeesRecipeSerializers() {}
    public static void register(IEventBus bus) { SERIALIZERS.register(bus); }
}

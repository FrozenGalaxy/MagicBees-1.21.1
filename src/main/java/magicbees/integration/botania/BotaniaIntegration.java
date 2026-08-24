package magicbees.integration.botania;

import magicbees.MagicBees;
import magicbees.integration.botania.blockentity.BeegoniaBlockEntity;
import magicbees.integration.botania.blockentity.HibeescusBlockEntity;
import magicbees.integration.botania.blockentity.HiveacynthBlockEntity;
import magicbees.integration.botania.block.HibeescusFlowerBlock;
import magicbees.integration.botania.recipe.BeeElvenTradeRecipe;
import magicbees.integration.botania.recipe.BeeManaInfusionRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import vazkii.botania.common.block.flower.PoweredSpecialFlowerBlock;
import vazkii.botania.common.block.flower.SpecialFlowerBlock;

public final class BotaniaIntegration {
    private static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MagicBees.MOD_ID);
    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MagicBees.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MagicBees.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MagicBees.MOD_ID);

    public static final DeferredBlock<SpecialFlowerBlock> BEEGONIA = BLOCKS.register("beegonia", () ->
            new SpecialFlowerBlock(MobEffects.LUCK, 1, BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY), BotaniaIntegration::beegoniaType));
    public static final DeferredBlock<PoweredSpecialFlowerBlock> HIVEACYNTH = BLOCKS.register("hiveacynth", () ->
            new PoweredSpecialFlowerBlock(MobEffects.LUCK, 1, BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY), BotaniaIntegration::hiveacynthType));
    public static final DeferredBlock<HibeescusFlowerBlock> HIBEESCUS = BLOCKS.register("hibeescus", () ->
            new HibeescusFlowerBlock(MobEffects.LUCK, 1, BlockBehaviour.Properties.ofFullCopy(Blocks.POPPY), BotaniaIntegration::hibeescusType));

    public static final DeferredItem<BlockItem> BEEGONIA_ITEM = ITEMS.register("beegonia", () -> new BlockItem(BEEGONIA.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> HIVEACYNTH_ITEM = ITEMS.register("hiveacynth", () -> new BlockItem(HIVEACYNTH.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> HIBEESCUS_ITEM = ITEMS.register("hibeescus", () -> new BlockItem(HIBEESCUS.get(), new Item.Properties()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BeegoniaBlockEntity>> BEEGONIA_BE = BLOCK_ENTITIES.register("beegonia", () ->
            BlockEntityType.Builder.of(BeegoniaBlockEntity::new, BEEGONIA.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HiveacynthBlockEntity>> HIVEACYNTH_BE = BLOCK_ENTITIES.register("hiveacynth", () ->
            BlockEntityType.Builder.of(HiveacynthBlockEntity::new, HIVEACYNTH.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HibeescusBlockEntity>> HIBEESCUS_BE = BLOCK_ENTITIES.register("hibeescus", () ->
            BlockEntityType.Builder.of(HibeescusBlockEntity::new, HIBEESCUS.get()).build(null));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BeeManaInfusionRecipe>> BEE_MANA_SERIALIZER =
            RECIPE_SERIALIZERS.register("botania_bee_mana_infusion", BeeManaInfusionRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BeeElvenTradeRecipe>> BEE_ELVEN_SERIALIZER =
            RECIPE_SERIALIZERS.register("botania_bee_elven_trade", BeeElvenTradeRecipe.Serializer::new);


    private static BlockEntityType<? extends vazkii.botania.api.block_entity.SpecialFlowerBlockEntity> beegoniaType() { return BEEGONIA_BE.get(); }
    private static BlockEntityType<? extends vazkii.botania.api.block_entity.SpecialFlowerBlockEntity> hiveacynthType() { return HIVEACYNTH_BE.get(); }
    private static BlockEntityType<? extends vazkii.botania.api.block_entity.SpecialFlowerBlockEntity> hibeescusType() { return HIBEESCUS_BE.get(); }
    private BotaniaIntegration() {}
    public static void register(IEventBus bus) {
        BLOCKS.register(bus); ITEMS.register(bus); BLOCK_ENTITIES.register(bus); RECIPE_SERIALIZERS.register(bus);
    }
}

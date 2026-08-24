package magicbees.integration.botania.blockentity;

import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.core.genetics.capability.IIndividualHandlerItem;
import magicbees.config.MagicBeesConfig;
import magicbees.integration.botania.BotaniaIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import vazkii.botania.api.block_entity.GeneratingFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;

public final class BeegoniaBlockEntity extends GeneratingFlowerBlockEntity {
    private static final int RANGE = 3, AGE = 60, BASE_BURN = 100;
    private int burnTimeLeft;
    public BeegoniaBlockEntity(BlockPos pos, BlockState state) { super(BotaniaIntegration.BEEGONIA_BE.get(), pos, state); }

    @Override public void tickFlower() {
        super.tickFlower();
        if (level.isClientSide()) return;
        if (burnTimeLeft > 0) { burnTimeLeft--; addMana(1); return; }
        if (findBoundTile() == null || getMana() >= getMaxMana()) return;
        AABB box = new AABB(getEffectivePos()).inflate(RANGE);
        for (ItemEntity entity : level.getEntitiesOfClass(ItemEntity.class, box)) {
            if (entity.getAge() < AGE || !entity.isAlive()) continue;
            ItemStack stack = entity.getItem();
            if (IIndividualHandlerItem.getLifeStage(stack) != BeeLifeStage.DRONE || !(IIndividualHandlerItem.getIndividual(stack) instanceof IBee bee)) continue;
            int complexity = Math.max(1, bee.getSpecies().getComplexity() / 2);
            burnTimeLeft = (int) (BASE_BURN * complexity * MagicBeesConfig.COMMON.beegoniaManaMultiplier.get());
            stack.shrink(1); if (stack.isEmpty()) entity.discard(); setChanged(); break;
        }
    }
    @Override public RadiusDescriptor getRadius() { return RadiusDescriptor.Rectangle.square(getEffectivePos(), RANGE); }
    @Override public int getMaxMana() { return 600; }
    @Override public int getColor() { return 0xFFFF96; }
    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) { super.loadAdditional(tag, registries); burnTimeLeft=tag.getInt("burnTimeLeft"); }
    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) { super.saveAdditional(tag, registries); tag.putInt("burnTimeLeft", burnTimeLeft); }
}

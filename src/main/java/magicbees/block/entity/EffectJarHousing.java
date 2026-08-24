package magicbees.block.entity;

import com.mojang.authlib.GameProfile;
import forestry.api.IForestryApi;
import forestry.api.apiculture.*;
import forestry.api.core.HumidityType;
import forestry.api.core.IErrorLogic;
import forestry.api.core.TemperatureType;
import forestry.api.core.genetics.IGenome;
import forestry.api.core.genetics.alleles.BeeChromosomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

final class EffectJarHousing implements IBeeHousing {
    private static final IBeeModifier MODIFIER = new IBeeModifier() {
        @Override public Vec3i modifyTerritory(IGenome genome, Vec3i current) {
            return new Vec3i(Math.max(1, (int)(current.getX()*0.9F)), Math.max(1, (int)(current.getY()*0.9F)), Math.max(1, (int)(current.getZ()*0.9F)));
        }
        @Override public float modifyMutationChance(IGenome genome, IGenome mate, forestry.api.core.genetics.IMutation<forestry.api.apiculture.genetics.IBeeSpecies> mutation, float currentChance) { return 0F; }
        @Override public float modifyAging(IGenome genome, IGenome mate, float currentAging) { return 0F; }
        @Override public float modifyProductionSpeed(IGenome genome, float currentSpeed) { return 0F; }
        @Override public float modifyPollination(IGenome genome, float currentPollination) { return 0F; }
        @Override public float modifyGeneticDecay(IGenome genome, float currentDecay) { return 0F; }
        @Override public boolean isSealed() { return true; }
        @Override public boolean isSunlightSimulated() { return true; }
        @Override public boolean isAlwaysActive(IGenome genome) {
            return ForestryActivityTypes.DIURNAL.equals(genome.getActiveValue(BeeChromosomes.ACTIVITY));
        }
    };

    private final EffectJarBlockEntity jar;
    private final IErrorLogic errors;
    private final IBeekeepingLogic logic;
    private final IBeeHousingInventory inventory = new IBeeHousingInventory() {
        @Override public ItemStack getQueen() { return jar.getQueenStack(); }
        @Override public ItemStack getDrone() { return ItemStack.EMPTY; }
        @Override public void setQueen(ItemStack stack) { jar.setQueenStack(stack); }
        @Override public void setDrone(ItemStack stack) {}
        @Override public boolean addProduct(ItemStack product, boolean all) { return true; }
    };

    EffectJarHousing(EffectJarBlockEntity jar) {
        this.jar = jar;
        this.errors = IForestryApi.INSTANCE.getErrorManager().createErrorLogic();
        this.logic = IForestryApi.INSTANCE.getHiveManager().createBeekeepingLogic(this);
    }

    boolean canWork() { return logic.canWork(); }
    void refreshWorkConditions() { logic.clearCachedValues(); }
    @Override public Iterable<IBeeModifier> getBeeModifiers() { return List.of(MODIFIER); }
    @Override public Iterable<IBeeListener> getBeeListeners() { return List.of(); }
    @Override public IBeeHousingInventory getBeeInventory() { return inventory; }
    @Override public IBeekeepingLogic getBeekeepingLogic() { return logic; }
    @Override public int getBlockLightValue() { return jar.getLevel().getMaxLocalRawBrightness(jar.getBlockPos().above()); }
    @Override public boolean canBlockSeeTheSky() { return jar.getLevel().getBrightness(LightLayer.SKY, jar.getBlockPos().above()) >= 10; }
    @Override public boolean isRaining() { return jar.getLevel().isRaining() && jar.getLevel().getBrightness(LightLayer.SKY, jar.getBlockPos().above()) > 7; }
    @Override public @Nullable GameProfile getOwner() { return jar.getOwner(); }
    @Override public Vec3 getBeeFXCoordinates() { BlockPos p=jar.getBlockPos(); return new Vec3(p.getX()+.5,p.getY()+.5,p.getZ()+.5); }
    @Override public IErrorLogic getErrorLogic() { return errors; }
    @Override public TemperatureType temperature() { return IForestryApi.INSTANCE.getClimateManager().getTemperature(getBiome()); }
    @Override public HumidityType humidity() { return IForestryApi.INSTANCE.getClimateManager().getHumidity(getBiome()); }
    @Override public Holder<Biome> getBiome() { return jar.getLevel().getBiome(jar.getBlockPos()); }
    @Override public BlockPos getBlockPos() { return jar.getBlockPos(); }
    @Override public Level getLevel() { return jar.getLevel(); }
}

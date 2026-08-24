package magicbees.integration.botania.blockentity;

import forestry.api.apiculture.ForestryBeeSpecies;
import forestry.api.core.genetics.alleles.BeeChromosomes;
import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.apiculture.genetics.IBeeSpecies;
import forestry.api.core.genetics.alleles.ForestryAlleles;
import forestry.core.platform.util.SpeciesUtil;
import magicbees.config.MagicBeesConfig;
import magicbees.forestry.MagicBeeSpecies;
import magicbees.integration.botania.BotaniaIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.botania.api.block_entity.FunctionalFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;

import java.util.List;
import java.util.Map;

public final class HiveacynthBlockEntity extends FunctionalFlowerBlockEntity {
    private static final int RANGE=3, BASE_COST=15000;
    private record Weighted(ResourceLocation id,double weight) {}
    private static final List<Weighted> SPECIES=List.of(
            new Weighted(ForestryBeeSpecies.FOREST,20),new Weighted(ForestryBeeSpecies.MEADOWS,20),new Weighted(ForestryBeeSpecies.TROPICAL,10),
            new Weighted(ForestryBeeSpecies.MODEST,16),new Weighted(ForestryBeeSpecies.WINTRY,10),new Weighted(ForestryBeeSpecies.ENDED,.5),
            new Weighted(MagicBeeSpecies.MYSTICAL,20),new Weighted(MagicBeeSpecies.UNUSUAL,20),new Weighted(MagicBeeSpecies.SORCEROUS,13),
            new Weighted(MagicBeeSpecies.ATTUNED,6),new Weighted(MagicBeeSpecies.INFERNAL,10),new Weighted(MagicBeeSpecies.OBLIVION,1));
    private static final double TOTAL=SPECIES.stream().mapToDouble(Weighted::weight).sum();
    public HiveacynthBlockEntity(BlockPos pos,BlockState state){super(BotaniaIntegration.HIVEACYNTH_BE.get(),pos,state);}

    @Override public void tickFlower(){
        super.tickFlower();
        if(level.isClientSide()||isPowered()||level.getGameTime()%200!=0||getMana()<finalCost())return;
        addMana(-finalCost());
        IBeeSpecies species=SpeciesUtil.getBeeSpecies(selectSpecies());
        IBee bee=level.random.nextDouble()<MagicBeesConfig.COMMON.hiveacynthRainResistRate.get()
                ? species.createIndividual(Map.of(BeeChromosomes.TOLERATES_RAIN,ForestryAlleles.TRUE_RECESSIVE)) : species.createIndividual();
        BeeLifeStage stage=BeeLifeStage.DRONE;
        if(level.random.nextDouble()<MagicBeesConfig.COMMON.hiveacynthPrincessSpawnRate.get()){
            stage=BeeLifeStage.PRINCESS;
            if(MagicBeesConfig.COMMON.hiveacynthPristineRate.get()<level.random.nextDouble())bee.setPristine(false);
        }
        ItemStack stack=bee.createStack(stage); BlockPos p=getEffectivePos();
        ItemEntity item=new ItemEntity(level,p.getX()-RANGE+level.random.nextInt(RANGE*2+1),p.getY()+1,p.getZ()-RANGE+level.random.nextInt(RANGE*2+1),stack);
        item.setDeltaMovement(0,0,0); level.addFreshEntity(item);
    }
    private ResourceLocation selectSpecies(){double v=level.random.nextDouble()*TOTAL;for(Weighted w:SPECIES){v-=w.weight;if(v<=0)return w.id;}return SPECIES.getFirst().id;}
    private int finalCost(){return(int)(BASE_COST*MagicBeesConfig.COMMON.hiveacynthManaMultiplier.get());}
    @Override public RadiusDescriptor getRadius(){return RadiusDescriptor.Rectangle.square(getEffectivePos(),RANGE);}
    @Override public int getMaxMana(){return finalCost();}
    @Override public int getColor(){return 0x0071C6;}
}

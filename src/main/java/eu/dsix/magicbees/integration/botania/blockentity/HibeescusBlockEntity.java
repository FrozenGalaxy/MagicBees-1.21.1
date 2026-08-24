package eu.dsix.magicbees.integration.botania.blockentity;

import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.core.genetics.ILifeStage;
import forestry.api.core.genetics.capability.IIndividualHandlerItem;
import eu.dsix.magicbees.config.MagicBeesConfig;
import eu.dsix.magicbees.integration.botania.BotaniaIntegration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import vazkii.botania.api.block_entity.FunctionalFlowerBlockEntity;
import vazkii.botania.api.block_entity.RadiusDescriptor;

public final class HibeescusBlockEntity extends FunctionalFlowerBlockEntity {
    private static final int BASE_MANA=10000, BASE_TICKS=20*60*15;
    private static final double RANGE=.75;
    private ItemStack beeSlot=ItemStack.EMPTY;
    private long operationTicksRemaining;
    private float manaCostRollover;

    public HibeescusBlockEntity(BlockPos pos,BlockState state){super(BotaniaIntegration.HIBEESCUS_BE.get(),pos,state);}

    @Override public void tickFlower(){
        super.tickFlower();
        if(level.isClientSide())return;
        if(!beeSlot.isEmpty())progressOperation();
        else if(!isPowered() && ((level.getGameTime() ^ getEffectivePos().getX() ^ getEffectivePos().getZ()) % 11 == 0))findBee();
    }
    private void progressOperation(){
        if(operationTicksRemaining>0 && getMana()>0){
            operationTicksRemaining--; manaCostRollover+=manaPerBaseTick();
            if(manaCostRollover>=1f){int amount=(int)manaCostRollover;addMana(-amount);manaCostRollover-=amount;}
        } else if(operationTicksRemaining<=0){
            if(!(IIndividualHandlerItem.getIndividual(beeSlot) instanceof IBee bee))return;
            bee.setPristine(true);
            BeeLifeStage stage=bee.getMate()==null?BeeLifeStage.PRINCESS:BeeLifeStage.QUEEN;
            drop(bee.createStack(stage)); beeSlot=ItemStack.EMPTY; setChanged();
        }
    }
    private void findBee(){
        BlockPos p=getEffectivePos();
        AABB box=new AABB(p.getX()-RANGE,p.getY(),p.getZ()-RANGE,p.getX()+RANGE+1,p.getY()+1,p.getZ()+RANGE+1);
        for(ItemEntity entity:level.getEntitiesOfClass(ItemEntity.class,box)){
            ItemStack stack=entity.getItem();
            if(!(IIndividualHandlerItem.getIndividual(stack) instanceof IBee bee)||bee.isPristine())continue;
            ILifeStage stage=IIndividualHandlerItem.getLifeStage(stack);
            if(stage!=BeeLifeStage.PRINCESS && stage!=BeeLifeStage.QUEEN)continue;
            beeSlot=stack.copyWithCount(1);
            operationTicksRemaining=(long)(BASE_TICKS*MagicBeesConfig.COMMON.hibeescusTicksMultiplier.get())+level.random.nextInt(200);
            manaCostRollover=0; stack.shrink(1); if(stack.isEmpty())entity.discard(); setChanged(); break;
        }
    }
    public ItemStack removeHeldBee(){ItemStack stack=beeSlot;beeSlot=ItemStack.EMPTY;setChanged();return stack;}
    private void drop(ItemStack stack){
        BlockPos p=getEffectivePos();
        ItemEntity e=new ItemEntity(level,p.getX()-RANGE+level.random.nextInt((int)(RANGE*2+1)),p.getY()+1,p.getZ()-RANGE+level.random.nextInt((int)(RANGE*2+1)),stack);
        e.setDeltaMovement(0,0,0);level.addFreshEntity(e);
    }
    private int finalCost(){return(int)(BASE_MANA*MagicBeesConfig.COMMON.hibeescusManaCostMultiplier.get());}
    private float manaPerBaseTick(){return(float)finalCost()/BASE_TICKS;}
    @Override public RadiusDescriptor getRadius(){return RadiusDescriptor.Rectangle.square(getEffectivePos(),1);}
    @Override public int getMaxMana(){return finalCost()/20;}
    @Override public int getColor(){return 0xF94F4F;}
    @Override protected void loadAdditional(CompoundTag tag,HolderLookup.Provider registries){super.loadAdditional(tag,registries);beeSlot=ItemStack.parseOptional(registries,tag.getCompound("slot"));operationTicksRemaining=tag.getLong("operationTicks");manaCostRollover=tag.getFloat("manaRollover");}
    @Override protected void saveAdditional(CompoundTag tag,HolderLookup.Provider registries){super.saveAdditional(tag,registries);if(!beeSlot.isEmpty())tag.put("slot",beeSlot.saveOptional(registries));tag.putLong("operationTicks",operationTicksRemaining);tag.putFloat("manaRollover",manaCostRollover);}
}

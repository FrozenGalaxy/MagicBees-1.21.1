package magicbees.integration.botania.block;
import magicbees.integration.botania.blockentity.HibeescusBlockEntity;
import net.minecraft.core.BlockPos; import net.minecraft.core.Holder; import net.minecraft.world.effect.MobEffect; import net.minecraft.world.entity.item.ItemEntity; import net.minecraft.world.level.Level; import net.minecraft.world.level.block.entity.BlockEntityType; import net.minecraft.world.level.block.state.BlockState; import vazkii.botania.api.block_entity.SpecialFlowerBlockEntity; import vazkii.botania.common.block.flower.PoweredSpecialFlowerBlock; import java.util.function.Supplier;
public final class HibeescusFlowerBlock extends PoweredSpecialFlowerBlock {
 public HibeescusFlowerBlock(Holder<MobEffect> e,int d,Properties p,Supplier<BlockEntityType<? extends SpecialFlowerBlockEntity>> t){super(e,d,p,t);}
 @Override protected void onRemove(BlockState state,Level level,BlockPos pos,BlockState next,boolean moving){
  if(!state.is(next.getBlock()) && level.getBlockEntity(pos) instanceof HibeescusBlockEntity be){var stack=be.removeHeldBee();if(!stack.isEmpty())level.addFreshEntity(new ItemEntity(level,pos.getX()+.5,pos.getY()+1,pos.getZ()+.5,stack));}
  super.onRemove(state,level,pos,next,moving);
 }
}

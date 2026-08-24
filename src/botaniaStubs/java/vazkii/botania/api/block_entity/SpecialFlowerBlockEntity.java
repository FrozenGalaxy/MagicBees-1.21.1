package vazkii.botania.api.block_entity;
import net.minecraft.core.BlockPos; import net.minecraft.world.level.Level; import net.minecraft.world.level.block.entity.BlockEntity; import net.minecraft.world.level.block.entity.BlockEntityType; import net.minecraft.world.level.block.state.BlockState;
public abstract class SpecialFlowerBlockEntity extends BlockEntity {
 public SpecialFlowerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state){super(type,pos,state);} public static void commonTick(Level l,BlockPos p,BlockState s,SpecialFlowerBlockEntity self){self.tickFlower();} protected void tickFlower(){} public BlockPos getEffectivePos(){return getBlockPos();} public boolean isPowered(){return false;} public abstract RadiusDescriptor getRadius();
}

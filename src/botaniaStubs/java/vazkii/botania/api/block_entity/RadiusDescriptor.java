package vazkii.botania.api.block_entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
public interface RadiusDescriptor {
 record Rectangle(BlockPos subtileCoords, AABB aabb) implements RadiusDescriptor {
  public static Rectangle square(BlockPos pos, int expand) { return new Rectangle(pos, new AABB(pos.getX()-expand,pos.getY(),pos.getZ()-expand,pos.getX()+expand+1,pos.getY(),pos.getZ()+expand+1)); }
 }
}

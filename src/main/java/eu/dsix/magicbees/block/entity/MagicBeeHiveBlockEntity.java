package eu.dsix.magicbees.block.entity;

import forestry.apiculture.hives.HiveBlockEntity;
import eu.dsix.magicbees.registry.MagicBeesBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/** Wild-hive tile using ForestryCE's explicit addon constructor. */
public final class MagicBeeHiveBlockEntity extends HiveBlockEntity {
    public MagicBeeHiveBlockEntity(BlockPos pos, BlockState state) {
        super(MagicBeesBlockEntities.WILD_HIVE.get(), pos, state);
    }
}

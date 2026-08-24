package eu.dsix.magicbees.block;

import forestry.apiculture.hives.BlockBeeHive;
import forestry.apiculture.hives.HiveBlockEntity;
import eu.dsix.magicbees.block.entity.MagicBeeHiveBlockEntity;
import eu.dsix.magicbees.registry.MagicBeesBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * A Magic Bees wild hive using ForestryCE's native hive interaction semantics.
 *
 * <p>Forestry's {@link HiveBlockEntity} exposes an addon constructor accepting an addon-owned block entity type.
 * Extending {@link BlockBeeHive} preserves scoop-only drops, smoker calming, anger and wild-bee activity while
 * keeping Magic Bees' blocks and block entity type under the {@code magicbees} namespace.</p>
 */
public final class MagicBeeHiveBlock extends BlockBeeHive {
    private final int lightLevel;

    public MagicBeeHiveBlock(ResourceLocation speciesId, int lightLevel) {
        super(speciesId);
        this.lightLevel = lightLevel;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return lightLevel;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MagicBeeHiveBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == MagicBeesBlockEntities.WILD_HIVE.get()
                ? (level1, pos, state1, tile) -> ((MagicBeeHiveBlockEntity) tile).tick(level1)
                : null;
    }
}

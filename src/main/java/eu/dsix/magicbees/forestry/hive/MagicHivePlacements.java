package eu.dsix.magicbees.forestry.hive;

import forestry.api.apiculture.hives.IHivePlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import org.jetbrains.annotations.Nullable;

/** Native 1.21 placements reproducing Magic Bees' old Forestry hive generators. */
public final class MagicHivePlacements {
    private MagicHivePlacements() {
    }

    public static final IHivePlacement TREE = new IHivePlacement() {
        @Nullable
        @Override
        public BlockPos getPosForHive(WorldGenLevel level, RandomSource random, int x, int z) {
            ChunkAccess chunk = level.getChunk(x >> 4, z >> 4);
            int height = chunk.getHeight(Heightmap.Types.WORLD_SURFACE, x & 15, z & 15) - 1;
            if (height <= chunk.getMinBuildHeight()) {
                return null;
            }
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, height, z);
            BlockState state = chunk.getBlockState(pos);
            if (!IHivePlacement.isTreeBlock(state)) {
                return null;
            }
            do {
                pos.move(Direction.DOWN);
                state = chunk.getBlockState(pos);
            } while (IHivePlacement.isTreeBlock(state));
            return pos.immutable();
        }

        @Override
        public boolean isValidLocation(WorldGenLevel level, BlockPos pos) {
            return IHivePlacement.isTreeBlock(level.getBlockState(pos.above()))
                    && canReplace(level.getBlockState(pos.below()), level, pos.below());
        }

        @Override
        public boolean canReplace(BlockState state, WorldGenLevel level, BlockPos pos) {
            return state.canBeReplaced();
        }
    };

    /** Legacy HiveManager.genHelper.ground(Blocks.DIRT, Blocks.GRASS). */
    public static final IHivePlacement GROUND_DIRT = ground(MagicHivePlacements::isLegacyDirtGround);

    /** Legacy HiveManager.genHelper.ground(Blocks.SAND, Blocks.SANDSTONE). */
    public static final IHivePlacement GROUND_SAND = ground(MagicHivePlacements::isLegacySandGround);

    private static IHivePlacement ground(java.util.function.Predicate<BlockState> validGround) {
        return new IHivePlacement() {
            @Nullable
            @Override
            public BlockPos getPosForHive(WorldGenLevel level, RandomSource random, int x, int z) {
                int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
                if (y <= level.getMinBuildHeight()) {
                    return null;
                }
                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x, y, z);
                while (canReplace(level.getBlockState(pos), level, pos)) {
                    pos.move(Direction.DOWN);
                    if (pos.getY() <= level.getMinBuildHeight()) {
                        return null;
                    }
                }
                return pos.above().immutable();
            }

            @Override
            public boolean isValidLocation(WorldGenLevel level, BlockPos pos) {
                return validGround.test(level.getBlockState(pos.below()));
            }

            @Override
            public boolean canReplace(BlockState state, WorldGenLevel level, BlockPos pos) {
                return state.canBeReplaced() || IHivePlacement.isTreeBlock(state);
            }
        };
    }

    /** Legacy HiveGenUnderground using the flattened variants of 1.12 Blocks.STONE. */
    public static IHivePlacement undergroundStone(int minLevel, int range, int surroundCount) {
        return new UndergroundPlacement(minLevel, range, surroundCount, MagicHivePlacements::isLegacyStone);
    }

    /** Legacy HiveGenNether, including its +/-4 Y search when the first random position is unsuitable. */
    public static IHivePlacement nether(int minLevel, int range, int surroundCount) {
        return new NetherPlacement(minLevel, range, surroundCount);
    }

    public static final IHivePlacement END = new IHivePlacement() {
        @Nullable
        @Override
        public BlockPos getPosForHive(WorldGenLevel level, RandomSource random, int x, int z) {
            int surface = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
            if (surface == 0) {
                return null;
            }
            int y = 10;
            while (y < surface && !level.getBlockState(new BlockPos(x, y - 1, z)).is(Blocks.END_STONE)) {
                y += 8;
            }
            while (y > -1 && !level.getBlockState(new BlockPos(x, y - 2, z)).isAir()) {
                y--;
            }
            return new BlockPos(x, y, z);
        }

        @Override
        public boolean isValidLocation(WorldGenLevel level, BlockPos pos) {
            for (Direction direction : Direction.values()) {
                if (!level.getBlockState(pos.relative(direction)).is(Blocks.END_STONE)) {
                    return false;
                }
            }
            return level.getBlockState(pos.below(2)).isAir()
                    && level.getBlockState(pos.below(3)).isAir();
        }

        @Override
        public boolean canReplace(BlockState state, WorldGenLevel level, BlockPos pos) {
            return state.is(Blocks.END_STONE);
        }
    };

    /**
     * 1.12 Blocks.STONE contained seven metadata variants. Flattening made them separate blocks in modern MC.
     * The old isReplaceableOreGen predicate compared only the Block object, so all seven variants qualified.
     */
    public static boolean isLegacyStone(BlockState state) {
        return state.is(Blocks.STONE)
                || state.is(Blocks.GRANITE)
                || state.is(Blocks.POLISHED_GRANITE)
                || state.is(Blocks.DIORITE)
                || state.is(Blocks.POLISHED_DIORITE)
                || state.is(Blocks.ANDESITE)
                || state.is(Blocks.POLISHED_ANDESITE);
    }

    /** 1.12 Blocks.DIRT metadata variants plus Blocks.GRASS. */
    public static boolean isLegacyDirtGround(BlockState state) {
        return state.is(Blocks.DIRT)
                || state.is(Blocks.COARSE_DIRT)
                || state.is(Blocks.PODZOL)
                || state.is(Blocks.GRASS_BLOCK);
    }

    /** 1.12 Blocks.SAND metadata variants plus Blocks.SANDSTONE metadata variants. */
    public static boolean isLegacySandGround(BlockState state) {
        return state.is(Blocks.SAND)
                || state.is(Blocks.RED_SAND)
                || state.is(Blocks.SANDSTONE)
                || state.is(Blocks.CHISELED_SANDSTONE)
                || state.is(Blocks.SMOOTH_SANDSTONE);
    }

    @FunctionalInterface
    private interface HostPredicate {
        boolean test(BlockState state);
    }

    private record UndergroundPlacement(int minLevel, int range, int surroundCount,
                                        HostPredicate host) implements IHivePlacement {
        @Nullable
        @Override
        public BlockPos getPosForHive(WorldGenLevel level, RandomSource random, int x, int z) {
            return new BlockPos(x, minLevel + random.nextInt(range), z);
        }

        @Override
        public boolean isValidLocation(WorldGenLevel level, BlockPos pos) {
            int remaining = surroundCount;
            for (Direction direction : Direction.values()) {
                if (host.test(level.getBlockState(pos.relative(direction)))) {
                    remaining--;
                }
                if (remaining <= 0) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canReplace(BlockState state, WorldGenLevel level, BlockPos pos) {
            return !state.isAir() && host.test(state);
        }
    }

    private record NetherPlacement(int minLevel, int range, int surroundCount) implements IHivePlacement {
        @Nullable
        @Override
        public BlockPos getPosForHive(WorldGenLevel level, RandomSource random, int x, int z) {
            BlockPos pos = new BlockPos(x, minLevel + random.nextInt(range), z);
            if (!isValidLocation(level, pos)) {
                int searchDirection = random.nextBoolean() ? 4 : -4;
                while (!level.getBlockState(pos).is(Blocks.NETHERRACK)) {
                    pos = pos.offset(0, searchDirection, 0);
                    if (pos.getY() < minLevel || pos.getY() > minLevel + range
                            || level.getBlockState(pos.below()).is(Blocks.BEDROCK)) {
                        return null;
                    }
                }
            }
            return pos;
        }

        @Override
        public boolean isValidLocation(WorldGenLevel level, BlockPos pos) {
            int remaining = surroundCount;
            for (Direction direction : Direction.values()) {
                if (level.getBlockState(pos.relative(direction)).is(Blocks.NETHERRACK)) {
                    remaining--;
                }
                if (remaining <= 0) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean canReplace(BlockState state, WorldGenLevel level, BlockPos pos) {
            return !state.isAir() && state.is(Blocks.NETHERRACK);
        }
    }
}

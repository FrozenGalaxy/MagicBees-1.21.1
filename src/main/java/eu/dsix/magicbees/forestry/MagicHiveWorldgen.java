package eu.dsix.magicbees.forestry;

import eu.dsix.magicbees.forestry.hive.MagicHivePlacements;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/** Small shared port of the old random-walk post-generation vein helper. */
final class MagicHiveWorldgen {
    private MagicHiveWorldgen() {
    }

    static void spawnLegacyStoneVein(WorldGenLevel level, RandomSource random, BlockPos start, int maxSpawnCount,
                                     BlockState replacement) {
        spawnVein(level, random, start, maxSpawnCount, replacement, MagicHivePlacements::isLegacyStone);
    }

    static void spawnNetherrackVein(WorldGenLevel level, RandomSource random, BlockPos start, int maxSpawnCount,
                                    BlockState replacement) {
        spawnVein(level, random, start, maxSpawnCount, replacement, state -> state.is(Blocks.NETHERRACK));
    }

    private static void spawnVein(WorldGenLevel level, RandomSource random, BlockPos start, int maxSpawnCount,
                                  BlockState replacement, java.util.function.Predicate<BlockState> replaceable) {
        int target = random.nextInt(maxSpawnCount + 1);
        int attempts = 0;
        int spawned = 0;
        BlockPos pos = start;
        // The 1.12 loop compared an int attempt counter to target * 1.2 (double).
        while (spawned < target && attempts < target * 1.2D) {
            attempts++;
            if (level.hasChunkAt(pos)) {
                BlockState state = level.getBlockState(pos);
                if (!state.isAir() && replaceable.test(state)) {
                    level.setBlock(pos, replacement, Block.UPDATE_CLIENTS);
                    spawned++;
                }
            }
            pos = pos.relative(Direction.getRandom(random));
        }
    }
}

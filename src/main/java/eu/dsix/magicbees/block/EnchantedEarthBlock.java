package eu.dsix.magicbees.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriState;

public final class EnchantedEarthBlock extends Block {
    public EnchantedEarthBlock(Properties properties) {
        super(properties.randomTicks().noOcclusion());
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        accelerate(level, pos.above(), 0);

        if (random.nextBoolean()) {
            BlockPos nearby = pos.offset(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
            accelerate(level, nearby, random.nextInt(5) + 1);
        }
    }

    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, BlockState plant) {
        // The 1.12 block returned true for every IPlantable. NeoForge's TriState.TRUE
        // is the direct modern override of the plant's normal soil decision.
        return TriState.TRUE;
    }

    @Override
    public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    private static void accelerate(ServerLevel level, BlockPos pos, int delay) {
        BlockState target = level.getBlockState(pos);
        if (target.isRandomlyTicking()) {
            level.scheduleTick(pos, target.getBlock(), delay);
        }
    }
}

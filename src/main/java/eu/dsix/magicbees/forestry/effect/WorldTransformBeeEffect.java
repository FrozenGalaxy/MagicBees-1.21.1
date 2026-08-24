package eu.dsix.magicbees.forestry.effect;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.genetics.IBeeEffect;
import forestry.api.core.genetics.IEffectData;
import forestry.api.core.genetics.IGenome;
import forestry.apiculture.bees.genetics.effects.ThrottledBeeEffect;
import forestry.core.engine.genetics.EffectData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.Tags;

public final class WorldTransformBeeEffect implements IBeeEffect {
    public enum Mode { TRANSMUTING, CRUMBLING, DREAMING }

    private final Mode mode;
    private final int throttle;

    public WorldTransformBeeEffect(Mode mode, int throttle) {
        this.mode = mode;
        this.throttle = throttle;
    }

    public Mode mode() { return mode; }
    public int throttle() { return throttle; }

    @Override
    public boolean isDominant() { return true; }

    @Override
    public IEffectData validateStorage(IEffectData storedData) {
        return storedData == null ? new EffectData(1, 0) : storedData;
    }

    @Override
    public IEffectData doEffect(IGenome genome, IEffectData storedData, IBeeHousing housing) {
        if (housing.getErrorLogic().hasErrors()) return storedData;
        int ticks = storedData.getInteger(0) + 1;
        storedData.setInteger(0, ticks);
        if (ticks < throttle) return storedData;
        storedData.setInteger(0, 0);
        Level level = housing.getLevel();
        if (level == null || level.isClientSide) return storedData;

        AABB bounds = ThrottledBeeEffect.getBounding(housing, genome);
        int minX = Mth.floor(bounds.minX), minY = Mth.floor(bounds.minY), minZ = Mth.floor(bounds.minZ);
        int maxX = Mth.floor(bounds.maxX), maxY = Mth.floor(bounds.maxY), maxZ = Mth.floor(bounds.maxZ);
        BlockPos pos = new BlockPos(
                minX + level.random.nextInt(Math.max(1, maxX - minX + 1)),
                minY + level.random.nextInt(Math.max(1, maxY - minY + 1)),
                minZ + level.random.nextInt(Math.max(1, maxZ - minZ + 1)));
        transformAt(level, pos, mode);
        return storedData;
    }

    public static boolean transformAt(Level level, BlockPos pos, Mode mode) {
        BlockState current = level.getBlockState(pos);
        BlockState replacement = replacement(level, pos, current, mode);
        if (replacement == current || replacement.equals(current)) return false;
        return level.setBlock(pos, replacement, 3);
    }

    public static BlockState replacement(Level level, BlockPos pos, BlockState state, Mode mode) {
        var biome = level.getBiome(pos);
        return replacementForState(
                state,
                mode,
                biome.is(Tags.Biomes.IS_SANDY),
                biome.is(BiomeTags.IS_FOREST) || biome.is(Tags.Biomes.IS_FOREST),
                biome.is(Tags.Biomes.IS_SNOWY),
                biome.is(BiomeTags.IS_OCEAN) || biome.is(Tags.Biomes.IS_OCEAN),
                biome.is(BiomeTags.IS_RIVER) || biome.is(Tags.Biomes.IS_RIVER));
    }

    public static BlockState replacementForState(BlockState state, Mode mode, boolean sandyBiome) {
        return replacementForState(state, mode, sandyBiome, false, false, false, false);
    }

    /**
     * Deterministic seam for the legacy transmutation controller. Railcraft 1.12 mapped plain stone/cobblestone
     * to the corresponding Quarried or Abyssal metadata variant; modern Railcraft exposes those as distinct blocks.
     */
    public static BlockState replacementForState(BlockState state, Mode mode, boolean sandyBiome,
                                                 boolean forestBiome, boolean snowyBiome,
                                                 boolean oceanBiome, boolean riverBiome) {
        if (mode == Mode.TRANSMUTING) {
            if (sandyBiome && state.is(Blocks.SAND)) return Blocks.SANDSTONE.defaultBlockState();
            ResourceLocation railcraftTarget = railcraftReplacementId(state, forestBiome, snowyBiome, oceanBiome, riverBiome);
            if (railcraftTarget != null) {
                Block replacement = optionalBlock(railcraftTarget.toString());
                if (replacement != null) return replacement.defaultBlockState();
            }
            return state;
        }
        if (mode == Mode.DREAMING) {
            Block livingwood = optionalBlock("botania:livingwood");
            Block dreamwood = optionalBlock("botania:dreamwood");
            Block livingrock = optionalBlock("botania:livingrock");
            if (livingwood != null && dreamwood != null && state.is(livingwood)) return dreamwood.defaultBlockState();
            if (livingwood != null && state.is(BlockTags.LOGS)) return livingwood.defaultBlockState();
            if (livingrock != null && state.is(Blocks.STONE)) return livingrock.defaultBlockState();
            return state;
        }
        if (state.is(Blocks.STONE)) return Blocks.COBBLESTONE.defaultBlockState();
        if (state.is(Blocks.COBBLESTONE)) return Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        if (state.is(Blocks.STONE_BRICKS)) return Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        if (state.is(Blocks.CRACKED_STONE_BRICKS)) return Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
        if (state.is(Blocks.COBBLESTONE_WALL)) return Blocks.MOSSY_COBBLESTONE_WALL.defaultBlockState();
        if (state.is(Blocks.GRAVEL)) return Blocks.SAND.defaultBlockState();
        return state;
    }

    public static ResourceLocation railcraftReplacementId(BlockState state,
                                                          boolean forestBiome, boolean snowyBiome,
                                                          boolean oceanBiome, boolean riverBiome) {
        if (!state.is(Blocks.STONE) && !state.is(Blocks.COBBLESTONE)) return null;
        String suffix = state.is(Blocks.STONE) ? "stone" : "cobblestone";
        if (forestBiome && !snowyBiome) {
            return ResourceLocation.fromNamespaceAndPath("railcraft", "quarried_" + suffix);
        }
        if (oceanBiome && !riverBiome) {
            return ResourceLocation.fromNamespaceAndPath("railcraft", "abyssal_" + suffix);
        }
        return null;
    }

    private static Block optionalBlock(String rawId) {
        ResourceLocation id = ResourceLocation.tryParse(rawId);
        if (id == null || !BuiltInRegistries.BLOCK.containsKey(id)) return null;
        Block block = BuiltInRegistries.BLOCK.get(id);
        return block == Blocks.AIR ? null : block;
    }
}

package magicbees.forestry.mutation;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.core.climate.IClimateProvider;
import forestry.api.core.genetics.IGenome;
import forestry.api.core.genetics.IMutation;
import forestry.api.core.genetics.IMutationCondition;
import forestry.api.core.genetics.MutationConditionType;
import forestry.core.platform.tile.TileUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

/** Resource-block requirement that preserves legacy ore-dictionary behavior through modern common block tags. */
public record RequiredBlockTagMutationCondition(TagKey<Block> tag) implements IMutationCondition {
    public static final MapCodec<RequiredBlockTagMutationCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(RequiredBlockTagMutationCondition::tag)
    ).apply(instance, RequiredBlockTagMutationCondition::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequiredBlockTagMutationCondition> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC.map(id -> TagKey.create(Registries.BLOCK, id), TagKey::location),
                    RequiredBlockTagMutationCondition::tag,
                    RequiredBlockTagMutationCondition::new);
    public static final MutationConditionType<RequiredBlockTagMutationCondition> TYPE =
            new MutationConditionType<>(CODEC, STREAM_CODEC);

    @Override
    public float modifyChance(Level level, BlockPos pos, IMutation<?> mutation, IGenome firstGenome,
                              IGenome secondGenome, IClimateProvider climate, float currentChance) {
        BlockEntity tile;
        do {
            pos = pos.below();
            tile = TileUtil.getTile(level, pos);
        } while (tile instanceof IBeeHousing);
        return level.getBlockState(pos).is(tag) ? currentChance : 0.0f;
    }

    @Override
    public Component getDescription() {
        return Component.translatable("magicbees.mutation.condition.required_block_tag", tag.location().toString());
    }

    @Override
    public MutationConditionType<?> type() {
        return TYPE;
    }
}

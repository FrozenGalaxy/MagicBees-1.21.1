package eu.dsix.magicbees.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record MagnetState(int level, boolean active) {
    public static final MagnetState DEFAULT = new MagnetState(0, false);
    public static final Codec<MagnetState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("level").forGetter(MagnetState::level),
            Codec.BOOL.fieldOf("active").forGetter(MagnetState::active)
    ).apply(instance, MagnetState::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, MagnetState> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, MagnetState::level,
            ByteBufCodecs.BOOL, MagnetState::active,
            MagnetState::new);

    public MagnetState {
        if (level < 0) level = 0;
    }
}

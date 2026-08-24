package eu.dsix.magicbees.item;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.apiculture.hives.IHiveFrame;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public final class MagicBeesFrameItem extends Item implements IHiveFrame {
    private final MagicBeesFrameType type;

    public MagicBeesFrameItem(MagicBeesFrameType type) {
        super(new Item.Properties().durability(type.durability()));
        this.type = type;
    }

    @Override
    public ItemStack frameUsed(IBeeHousing housing, ItemStack frame, IBee queen, int wear) {
        if (housing.getLevel() instanceof ServerLevel serverLevel) {
            frame.hurtAndBreak(wear, serverLevel, null, ignored -> {
            });
        }
        return frame.isEmpty() ? ItemStack.EMPTY : frame;
    }

    @Override
    public IBeeModifier getBeeModifier(ItemStack frame) {
        return type;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        addModifier(tooltip, "territory", type.territoryModifier(), 1);
        addModifier(tooltip, "mutation", type.mutationModifier(), 1);
        addModifier(tooltip, "lifespan", type.lifespanModifier(), type == MagicBeesFrameType.OBLIVION ? 4 : 1);
        addModifier(tooltip, "production", type.productionModifier(), 1);
        addModifier(tooltip, "flowering", type.pollinationModifier(), 1);
        addModifier(tooltip, "genetic_decay", type.geneticDecayModifier(), type == MagicBeesFrameType.GENTLE ? 2 : 1);
        if (!stack.isDamaged()) {
            tooltip.add(Component.translatable("item.forestry.durability", stack.getMaxDamage()).withStyle(ChatFormatting.GRAY));
        }
    }

    private static void addModifier(List<Component> tooltip, String key, float value, int decimals) {
        if (value == 1.0f) {
            return;
        }
        String formatted = String.format(java.util.Locale.ROOT, "%." + decimals + "fx", value);
        tooltip.add(Component.translatable("magicbees.frame.modifier." + key)
                .append(": ")
                .append(formatted)
                .withStyle(ChatFormatting.GRAY));
    }
}

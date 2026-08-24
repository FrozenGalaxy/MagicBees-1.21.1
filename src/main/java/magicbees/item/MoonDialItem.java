package magicbees.item;

import magicbees.config.MagicBeesConfig;
import magicbees.util.LegacyMoonPhase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public final class MoonDialItem extends Item {
    public MoonDialItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Level level = context.level();
        if (level != null && MagicBeesConfig.COMMON.moonDialShowsPhaseInText.get()) {
            tooltip.add(LegacyMoonPhase.fromLevel(level).displayName().copy().withStyle(ChatFormatting.GRAY));
        }
    }
}

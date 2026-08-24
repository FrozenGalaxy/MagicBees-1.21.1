package magicbees.item;

import forestry.core.content.tools.ItemScoop;
import magicbees.integration.botania.BotaniaManaCompat;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class ManasteelScoopItem extends ItemScoop {
    public static final int MANA_PER_DAMAGE = 30;
    public ManasteelScoopItem() { super(20); }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean selected) {
        super.inventoryTick(stack, level, entity, slotId, selected);
        if (stack.isDamaged() && entity instanceof Player player && BotaniaManaCompat.repairOne(stack, player, MANA_PER_DAMAGE)) {
            stack.setDamageValue(stack.getDamageValue() - 1);
        }
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return BotaniaManaCompat.isManasteel(repairCandidate) || super.isValidRepairItem(stack, repairCandidate);
    }
}

package eu.dsix.magicbees.menu;

import eu.dsix.magicbees.block.entity.EffectJarBlockEntity;
import eu.dsix.magicbees.registry.MagicBeesMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class EffectJarMenu extends AbstractContainerMenu {
    public static final int JAR_SLOT_X = 80, JAR_SLOT_Y = 32;
    private static final int JAR_SLOT = 0;
    private static final int PLAYER_INVENTORY_START = 1;
    private static final int PLAYER_INVENTORY_END = 28;
    private static final int HOTBAR_START = 28;
    private static final int HOTBAR_END = 37;
    private final EffectJarBlockEntity jar;
    private final ContainerData data;

    public static EffectJarMenu fromNetwork(int id, Inventory inventory, RegistryFriendlyByteBuf buf) {
        var pos = buf.readBlockPos();
        if (!(inventory.player.level().getBlockEntity(pos) instanceof EffectJarBlockEntity jar)) {
            throw new IllegalStateException("Effect Jar menu opened without Effect Jar at " + pos);
        }
        return new EffectJarMenu(id, inventory, jar, new SimpleContainerData(6));
    }

    public EffectJarMenu(int id, Inventory inventory, EffectJarBlockEntity jar) {
        this(id, inventory, jar, jar.menuData());
    }

    private EffectJarMenu(int id, Inventory inventory, EffectJarBlockEntity jar, ContainerData data) {
        super(MagicBeesMenus.EFFECT_JAR.get(), id);
        this.jar = jar;
        this.data = data;
        addSlot(new DroneSlot(jar, JAR_SLOT_X, JAR_SLOT_Y));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
        }
        for (int col = 0; col < 9; col++) addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        addDataSlots(data);
    }

    public EffectJarBlockEntity jar() { return jar; }
    public int beeHealth() { return data.get(0); }
    public int beeColour() { return (data.get(1) & 0xFFFF) | ((data.get(2) & 0xFF) << 16); }
    public int ageProgress() { return data.get(3); }
    public int ticksUntilDeath() { return data.get(4); }
    public boolean isActive() { return data.get(5) != 0; }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) {
            return ItemStack.EMPTY;
        }

        Slot slot = slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index == JAR_SLOT) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (EffectJarBlockEntity.isDrone(stack)) {
            if (!moveItemStackTo(stack, JAR_SLOT, JAR_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= PLAYER_INVENTORY_START && index < PLAYER_INVENTORY_END) {
            if (!moveItemStackTo(stack, HOTBAR_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index >= HOTBAR_START && index < HOTBAR_END) {
            if (!moveItemStackTo(stack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }

    @Override public boolean stillValid(Player player) { return jar.stillValid(player); }

    private static final class DroneSlot extends Slot {
        private DroneSlot(EffectJarBlockEntity jar, int x, int y) {
            super(jar, 0, x, y);
        }

        @Override public boolean mayPlace(ItemStack stack) {
            return EffectJarBlockEntity.isDrone(stack);
        }
    }
}

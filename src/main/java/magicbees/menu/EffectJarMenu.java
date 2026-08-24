package magicbees.menu;

import magicbees.block.entity.EffectJarBlockEntity;
import magicbees.registry.MagicBeesMenus;
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
    @Override public ItemStack quickMoveStack(Player player, int index) { return ItemStack.EMPTY; }
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

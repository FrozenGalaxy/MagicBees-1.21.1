package magicbees.block.entity;

import com.mojang.authlib.GameProfile;
import forestry.api.apiculture.genetics.BeeLifeStage;
import forestry.api.apiculture.genetics.IBee;
import forestry.api.core.HumidityType;
import forestry.api.core.IErrorLogic;
import forestry.api.core.IErrorLogicSource;
import forestry.api.core.TemperatureType;
import forestry.api.core.climate.IClimateProvider;
import forestry.api.core.genetics.IEffectData;
import forestry.api.core.genetics.capability.IIndividualHandlerItem;
import magicbees.registry.MagicBeesBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public final class EffectJarBlockEntity extends BlockEntity implements Container, MenuProvider, IErrorLogicSource, IClimateProvider {
    public static final int AGE_THROTTLE = 550;
    public static final float AGE_STEP = 0.26F;
    private static final int INACTIVE_WORK_REFRESH_INTERVAL = 10;

    private final EffectJarHousing housing = new EffectJarHousing(this);
    private ItemStack visibleStack = ItemStack.EMPTY;
    private ItemStack queenStack = ItemStack.EMPTY;
    private IEffectData[] effectData = new IEffectData[2];
    private int throttle;
    private int currentBeeHealth;
    private int currentBeeColour = 0x0FFFFFF;
    private int ageProgress;
    private int ticksUntilDeath;
    private int inactiveWorkRefreshTicks;
    private boolean active;
    private @Nullable GameProfile owner;

    private final ContainerData menuData = new ContainerData() {
        @Override public int get(int index) {
            return switch (index) {
                case 0 -> currentBeeHealth;
                case 1 -> currentBeeColour & 0xFFFF;
                case 2 -> (currentBeeColour >>> 16) & 0xFF;
                case 3 -> ageProgress;
                case 4 -> ticksUntilDeath;
                case 5 -> active ? 1 : 0;
                default -> 0;
            };
        }
        @Override public void set(int index, int value) {
            if (index == 0) currentBeeHealth = value;
            else if (index == 1) currentBeeColour = (currentBeeColour & 0xFF0000) | (value & 0xFFFF);
            else if (index == 2) currentBeeColour = (currentBeeColour & 0x00FFFF) | ((value & 0xFF) << 16);
            else if (index == 3) ageProgress = value;
            else if (index == 4) ticksUntilDeath = value;
            else if (index == 5) active = value != 0;
        }
        @Override public int getCount() { return 6; }
    };

    public EffectJarBlockEntity(BlockPos pos, BlockState state) { super(MagicBeesBlockEntities.EFFECT_JAR.get(), pos, state); }

    public static void serverTick(Level level, BlockPos pos, BlockState state, EffectJarBlockEntity jar) {
        if (!jar.queenStack.isEmpty()) jar.tickQueen(level);
        else if (!jar.visibleStack.isEmpty()) jar.createQueenFromDrone();
    }

    private void createQueenFromDrone() {
        if (!(IIndividualHandlerItem.getIndividual(visibleStack) instanceof IBee bee)
                || IIndividualHandlerItem.getLifeStage(visibleStack) != BeeLifeStage.DRONE) return;
        queenStack = bee.createStack(BeeLifeStage.QUEEN);
        visibleStack.shrink(1);
        if (visibleStack.isEmpty()) visibleStack = ItemStack.EMPTY;
        updatePresentation(bee);
        ageProgress = 0;
        // ForestryCE's modern flower cache probes one position per tick. Refresh once when the jar
        // receives a new Queen so its normal canWork() state is initialized immediately; subsequent
        // ticks continue to use Forestry's ordinary passive cache maintenance.
        housing.refreshWorkConditions();
        inactiveWorkRefreshTicks = 0;
        syncChanged();
    }

    private void tickQueen(Level level) {
        if (!(IIndividualHandlerItem.getIndividual(queenStack) instanceof IBee queen)) {
            queenStack = ItemStack.EMPTY;
            active = false;
            ticksUntilDeath = 0;
            syncChanged();
            return;
        }
        if (!active && ++inactiveWorkRefreshTicks >= INACTIVE_WORK_REFRESH_INTERVAL) {
            inactiveWorkRefreshTicks = 0;
            housing.refreshWorkConditions();
        }
        active = housing.canWork();
        if (!active) {
            syncChanged();
            return;
        }
        inactiveWorkRefreshTicks = 0;
        currentBeeColour = queen.getSpecies().getBody();
        updateAgeProgress();
        updateTicksUntilDeath(queen);
        effectData = queen.doEffect(effectData, housing);
        if (throttle > AGE_THROTTLE) {
            throttle = 0;
            ageProgress = 0;
            queen.age(level, AGE_STEP);
            if (!queen.isAlive() || queen.getHealth() == 0) {
                queenStack = ItemStack.EMPTY;
                currentBeeHealth = 0;
                currentBeeColour = 0x0FFFFFF;
                ageProgress = 0;
                ticksUntilDeath = 0;
                active = false;
            } else {
                queenStack = queen.createStack(BeeLifeStage.QUEEN);
                updatePresentation(queen);
            }
            syncChanged();
        } else {
            throttle++;
            updateAgeProgress();
            updateTicksUntilDeath(queen);
        }
    }

    private void updatePresentation(IBee bee) {
        int max = bee.getMaxHealth();
        currentBeeHealth = max <= 0 ? 0 : bee.getHealth() * 100 / max;
        currentBeeColour = bee.getSpecies().getBody();
        updateTicksUntilDeath(bee);
    }

    public ItemStack getVisibleStack() { return visibleStack; }
    public void setVisibleStack(ItemStack stack) {
        visibleStack = isDrone(stack) ? stack : ItemStack.EMPTY;
        updatePresentationFromVisibleStack();
        syncChanged();
    }
    public ItemStack getQueenStack() { return queenStack; }
    public void setQueenStack(ItemStack stack) {
        queenStack = stack == null ? ItemStack.EMPTY : stack;
        if (IIndividualHandlerItem.getIndividual(queenStack) instanceof IBee bee) updatePresentation(bee);
        else ticksUntilDeath = 0;
        syncChanged();
    }
    public int getThrottle() { return throttle; }
    public void setThrottleForTest(int throttle) { this.throttle = throttle; }
    public int getCurrentBeeHealth() { return currentBeeHealth; }
    public int getCurrentBeeColour() { return currentBeeColour; }
    public int getAgeProgress() { return ageProgress; }
    public int getTicksUntilDeath() { return ticksUntilDeath; }
    public boolean isActive() { return active; }
    public @Nullable GameProfile getOwner() { return owner; }
    public void setOwner(@Nullable Player player) { owner = player == null ? null : player.getGameProfile(); }
    @Override public IErrorLogic getErrorLogic() { return housing.getErrorLogic(); }
    @Override public TemperatureType temperature() { return housing.temperature(); }
    @Override public HumidityType humidity() { return housing.humidity(); }

    private void updatePresentationFromVisibleStack() {
        if (IIndividualHandlerItem.getIndividual(queenStack) instanceof IBee queen) {
            updatePresentation(queen);
            updateAgeProgress();
        } else if (IIndividualHandlerItem.getIndividual(visibleStack) instanceof IBee bee) {
            updatePresentation(bee);
            ageProgress = 0;
        } else if (queenStack.isEmpty()) {
            currentBeeHealth = 0;
            currentBeeColour = 0x0FFFFFF;
            ageProgress = 0;
            ticksUntilDeath = 0;
        }
    }

    private void updateAgeProgress() {
        ageProgress = Math.max(0, Math.min(100, throttle * 100 / (AGE_THROTTLE + 1)));
    }

    private void updateTicksUntilDeath(IBee bee) {
        if (!bee.isAlive() || bee.getHealth() <= 0) {
            ticksUntilDeath = 0;
            return;
        }
        int remainingAgeSteps = Math.max(1, (int) Math.ceil(bee.getHealth() / AGE_STEP));
        int ticksUntilNextAge = Math.max(1, AGE_THROTTLE + 1 - throttle);
        long totalTicks = (long) ticksUntilNextAge + (long) (remainingAgeSteps - 1) * (AGE_THROTTLE + 1);
        ticksUntilDeath = (int) Math.min(Integer.MAX_VALUE, totalTicks);
    }

    public static boolean isDrone(ItemStack stack) {
        return stack != null && !stack.isEmpty()
                && IIndividualHandlerItem.getIndividual(stack) instanceof IBee
                && IIndividualHandlerItem.getLifeStage(stack) == BeeLifeStage.DRONE;
    }

    public ContainerData menuData() { return menuData; }
    private void syncChanged() {
        setChanged();
        if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override public int getContainerSize() { return 1; }
    @Override public boolean isEmpty() { return visibleStack.isEmpty(); }
    @Override public ItemStack getItem(int slot) { return slot == 0 ? visibleStack : ItemStack.EMPTY; }
    @Override public ItemStack removeItem(int slot, int amount) {
        if (slot != 0 || visibleStack.isEmpty()) return ItemStack.EMPTY;
        ItemStack out = visibleStack.split(amount);
        updatePresentationFromVisibleStack();
        syncChanged();
        return out;
    }
    @Override public ItemStack removeItemNoUpdate(int slot) {
        if (slot != 0) return ItemStack.EMPTY;
        ItemStack out = visibleStack;
        visibleStack = ItemStack.EMPTY;
        updatePresentationFromVisibleStack();
        syncChanged();
        return out;
    }
    @Override public void setItem(int slot, ItemStack stack) { if (slot == 0) setVisibleStack(stack); }
    @Override public boolean canPlaceItem(int slot, ItemStack stack) { return slot == 0 && isDrone(stack); }
    @Override public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) return false;
        double dx = player.getX() - (worldPosition.getX() + 0.5D);
        double dy = player.getY() - (worldPosition.getY() + 0.5D);
        double dz = player.getZ() - (worldPosition.getZ() + 0.5D);
        return dx * dx + dy * dy + dz * dz < 64.0D;
    }
    @Override public void clearContent() { setVisibleStack(ItemStack.EMPTY); }
    @Override public Component getDisplayName() { return Component.translatable("block.magicbees.effectjar"); }
    @Override public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        if (level != null && !level.isClientSide) housing.refreshWorkConditions();
        return new magicbees.menu.EffectJarMenu(id, inventory, this);
    }
    @Override public ClientboundBlockEntityDataPacket getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }
    @Override public CompoundTag getUpdateTag(HolderLookup.Provider registries) { return saveWithoutMetadata(registries); }

    @Override protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        visibleStack = tag.contains("visibleStack") ? ItemStack.parseOptional(registries, tag.getCompound("visibleStack")) : ItemStack.EMPTY;
        queenStack = tag.contains("queenStack") ? ItemStack.parseOptional(registries, tag.getCompound("queenStack")) : ItemStack.EMPTY;
        currentBeeHealth = tag.getInt("currentBeeHealth");
        currentBeeColour = tag.contains("currentBeeColour") ? tag.getInt("currentBeeColour") : 0x0FFFFFF;
        ticksUntilDeath = tag.getInt("ticksUntilDeath");
        throttle = tag.getInt("throttle");
        loadErrors(tag);
        updateAgeProgress();
        effectData = new IEffectData[2];
        active = false;
    }

    @Override protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!visibleStack.isEmpty()) tag.put("visibleStack", visibleStack.saveOptional(registries));
        if (!queenStack.isEmpty()) tag.put("queenStack", queenStack.saveOptional(registries));
        tag.putInt("currentBeeHealth", currentBeeHealth);
        tag.putInt("currentBeeColour", currentBeeColour);
        tag.putInt("ticksUntilDeath", ticksUntilDeath);
        tag.putInt("throttle", throttle);
        saveErrors(tag);
    }

    private void saveErrors(CompoundTag tag) {
        short[] errors = housing.getErrorLogic().toArray();
        int[] encoded = new int[errors.length];
        for (int i = 0; i < errors.length; i++) encoded[i] = errors[i];
        tag.putIntArray("errors", encoded);
    }

    private void loadErrors(CompoundTag tag) {
        int[] encoded = tag.getIntArray("errors");
        short[] errors = new short[encoded.length];
        for (int i = 0; i < encoded.length; i++) errors[i] = (short) encoded[i];
        housing.getErrorLogic().fromArray(errors);
    }
}

package eu.dsix.magicbees.item;

import eu.dsix.magicbees.config.MagicBeesConfig;
import eu.dsix.magicbees.registry.MagicBeesDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class MysteriousMagnetItem extends Item {
    private static final double FUDGE_FACTOR = 0.2D;

    public MysteriousMagnetItem(Properties properties) {
        super(properties);
    }

    public static MagnetState state(ItemStack stack) {
        MagnetState value = stack.get(MagicBeesDataComponents.MAGNET_STATE.get());
        return value == null ? MagnetState.DEFAULT : value;
    }

    public static void setState(ItemStack stack, MagnetState state) {
        stack.set(MagicBeesDataComponents.MAGNET_STATE.get(), state);
    }

    public static int level(ItemStack stack) {
        return state(stack).level();
    }

    public static boolean active(ItemStack stack) {
        return state(stack).active();
    }

    public static double configuredRange(ItemStack stack) {
        return MagicBeesConfig.COMMON.magnetBaseRange.get() + level(stack) * MagicBeesConfig.COMMON.magnetLevelMultiplier.get();
    }

    public static double effectiveRange(ItemStack stack) {
        return configuredRange(stack) - FUDGE_FACTOR;
    }

    public static Vec3 attractionVector(Vec3 playerPosition, Vec3 playerVelocity, Vec3 itemPosition) {
        Vec3 delta = playerPosition.subtract(itemPosition);
        double length = delta.length() * 2.0D;
        if (length == 0.0D) return playerVelocity.scale(0.5D);
        return delta.scale(1.0D / length).add(playerVelocity.scale(0.5D));
    }

    public static boolean attractsItemAge(int age) {
        return age >= 10;
    }

    public static boolean processesArrowsAtLevel(int level) {
        return level >= 7;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean selected) {
        if (!(entity instanceof Player player) || !active(stack) || level.isClientSide()) return;

        double radius = effectiveRange(stack);
        AABB bounds = player.getBoundingBox().inflate(radius);

        if (processesArrowsAtLevel(level(stack))) {
            for (AbstractArrow arrow : level.getEntitiesOfClass(AbstractArrow.class, bounds)) {
                if ((arrow.pickup == AbstractArrow.Pickup.ALLOWED || level.random.nextFloat() < 0.3F)
                        && arrow.getOwner() != player) {
                    level.addFreshEntity(new ItemEntity(level, arrow.getX(), arrow.getY(), arrow.getZ(), new ItemStack(Items.ARROW)));
                }
                arrow.discard();
            }
        }

        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, bounds)) {
            if (!attractsItemAge(item.getAge())) continue;
            Vec3 motion = attractionVector(player.position(), player.getDeltaMovement(), item.position());
            if (item.horizontalCollision) motion = motion.add(0.0D, 1.0D, 0.0D);
            item.setDeltaMovement(motion);
            item.hasImpulse = true;

            if (level.random.nextFloat() < 0.2F && MagicBeesConfig.COMMON.magnetSound.get()) {
                float pitch = 0.85F - level.random.nextFloat() * 0.3F;
                level.playSound(null, item.getX(), item.getY(), item.getZ(), SoundEvents.ENDERMAN_TELEPORT,
                        SoundSource.MASTER, 0.6F, pitch);
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isShiftKeyDown()) {
            MagnetState old = state(stack);
            setState(stack, new MagnetState(old.level(), !old.active()));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return active(stack) || super.isFoil(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Component levelText = Component.translatable("misc.level", level(stack));
        tooltip.add(Component.translatable(active(stack) ? "misc.magnetActive" : "misc.magnetInactive", levelText));
    }
}

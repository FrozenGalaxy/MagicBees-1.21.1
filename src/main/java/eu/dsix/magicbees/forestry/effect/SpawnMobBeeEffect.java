package eu.dsix.magicbees.forestry.effect;

import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.genetics.IBeeEffect;
import forestry.api.core.genetics.IEffectData;
import forestry.api.core.genetics.IGenome;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;

public final class SpawnMobBeeEffect implements IBeeEffect {
    private final EntityType<? extends Mob> entityType;
    private final ResourceLocation entityTypeId;
    private final int chance;
    private final int maxMobs;
    private final int throttle;
    private final boolean angryOnPlayers;

    public SpawnMobBeeEffect(EntityType<? extends Mob> entityType, int throttle, int chance, int maxMobs) {
        this(entityType, throttle, chance, maxMobs, false);
    }

    public SpawnMobBeeEffect(EntityType<? extends Mob> entityType, int throttle, int chance, int maxMobs, boolean angryOnPlayers) {
        this.throttle = throttle;
        this.entityType = entityType;
        this.entityTypeId = null;
        this.chance = chance;
        this.maxMobs = maxMobs;
        this.angryOnPlayers = angryOnPlayers;
    }

    public SpawnMobBeeEffect(ResourceLocation entityTypeId, int throttle, int chance, int maxMobs) {
        this(entityTypeId, throttle, chance, maxMobs, false);
    }

    public SpawnMobBeeEffect(ResourceLocation entityTypeId, int throttle, int chance, int maxMobs, boolean angryOnPlayers) {
        this.throttle = throttle;
        this.entityType = null;
        this.entityTypeId = entityTypeId;
        this.chance = chance;
        this.maxMobs = maxMobs;
        this.angryOnPlayers = angryOnPlayers;
    }

    public EntityType<? extends Mob> entityType() { return resolveEntityType(); }
    public ResourceLocation entityTypeId() { return entityTypeId != null ? entityTypeId : BuiltInRegistries.ENTITY_TYPE.getKey(entityType); }
    public int throttle() { return throttle; }
    public int chance() { return chance; }
    public int maxMobs() { return maxMobs; }
    public boolean angryOnPlayers() { return angryOnPlayers; }

    @Override
    public boolean isDominant() {
        return false;
    }

    @Override
    public IEffectData validateStorage(IEffectData storedData) {
        return storedData == null ? new forestry.core.engine.genetics.EffectData(1, 0) : storedData;
    }

    @Override
    public IEffectData doEffect(IGenome genome, IEffectData storedData, IBeeHousing housing) {
        if (housing.getErrorLogic().hasErrors()) {
            return storedData;
        }
        int ticks = storedData.getInteger(0) + 1;
        storedData.setInteger(0, ticks);
        if (ticks < throttle) {
            return storedData;
        }
        storedData.setInteger(0, 0);
        Level level = housing.getLevel();
        if (level == null || level.isClientSide || (chance < 100 && level.random.nextInt(100) > chance)) {
            return storedData;
        }

        EntityType<? extends Mob> resolvedType = resolveEntityType();
        if (resolvedType == null) {
            return storedData;
        }
        Mob mob = resolvedType.create(level);
        if (mob == null) {
            return storedData;
        }
        AABB bounds = forestry.apiculture.bees.genetics.effects.ThrottledBeeEffect.getBounding(housing, genome);
        if (level.getEntitiesOfClass(mob.getClass(), bounds).size() > maxMobs) {
            return storedData;
        }
        RandomSource random = level.random;
        BlockPos pos = housing.getBlockPos();
        ResourceLocation typeId = BuiltInRegistries.ENTITY_TYPE.getKey(resolvedType);
        if (spawnsAboveHousing(resolvedType, typeId)) {
            mob.moveTo(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D,
                    random.nextFloat() * 360.0f, 0.0f);
            if (!level.noCollision(mob)) {
                return storedData;
            }
        } else {
            boolean foundSpace = false;
            for (int attempt = 0; attempt < 12; attempt++) {
                int dx;
                int dz;
                do {
                    dx = random.nextInt(3) - 1;
                    dz = random.nextInt(3) - 1;
                } while (dx == 0 && dz == 0);
                mob.moveTo(pos.getX() + dx + 0.5D, pos.getY() + 1.0D, pos.getZ() + dz + 0.5D,
                        random.nextFloat() * 360.0f, 0.0f);
                if (level.noCollision(mob)) {
                    foundSpace = true;
                    break;
                }
            }
            if (!foundSpace) {
                return storedData;
            }
        }
        if (level.addFreshEntity(mob) && angryOnPlayers) {
            level.getEntitiesOfClass(Player.class, bounds, player -> !player.isSpectator())
                    .stream()
                    .min(Comparator.comparingDouble(player -> player.distanceToSqr(housing.getBlockPos().getCenter())))
                    .ifPresent(mob::setTarget);
        }
        return storedData;
    }

    private static boolean spawnsAboveHousing(EntityType<? extends Mob> type, ResourceLocation typeId) {
        if (type == EntityType.BAT || type == EntityType.GHAST || type == EntityType.BLAZE) {
            return true;
        }
        String path = typeId.getPath();
        return path.contains("wisp") || path.contains("blizz") || path.contains("blitz");
    }

    @SuppressWarnings("unchecked")
    private EntityType<? extends Mob> resolveEntityType() {
        if (entityType != null) {
            return entityType;
        }
        if (entityTypeId == null) {
            return null;
        }
        EntityType<?> resolved = BuiltInRegistries.ENTITY_TYPE.getOptional(entityTypeId).orElse(null);
        if (resolved == null) {
            return null;
        }
        return (EntityType<? extends Mob>) resolved;
    }

}

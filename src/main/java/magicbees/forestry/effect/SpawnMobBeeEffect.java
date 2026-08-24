package magicbees.forestry.effect;

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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class SpawnMobBeeEffect implements IBeeEffect {
    private final EntityType<? extends Mob> entityType;
    private final ResourceLocation entityTypeId;
    private final int chance;
    private final int maxMobs;
    private final int throttle;

    public SpawnMobBeeEffect(EntityType<? extends Mob> entityType, int throttle, int chance, int maxMobs) {
        this.throttle = throttle;
        this.entityType = entityType;
        this.entityTypeId = null;
        this.chance = chance;
        this.maxMobs = maxMobs;
    }

    public SpawnMobBeeEffect(ResourceLocation entityTypeId, int throttle, int chance, int maxMobs) {
        this.throttle = throttle;
        this.entityType = null;
        this.entityTypeId = entityTypeId;
        this.chance = chance;
        this.maxMobs = maxMobs;
    }

    public EntityType<? extends Mob> entityType() { return resolveEntityType(); }
    public ResourceLocation entityTypeId() { return entityTypeId != null ? entityTypeId : BuiltInRegistries.ENTITY_TYPE.getKey(entityType); }
    public int throttle() { return throttle; }
    public int chance() { return chance; }
    public int maxMobs() { return maxMobs; }

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
        AABB bounds = forestry.apiculture.bees.genetics.effects.ThrottledBeeEffect.getBounding(housing, genome);
        if (level.getEntitiesOfClass(resolvedType.getBaseClass(), bounds).size() > maxMobs) {
            return storedData;
        }

        Mob mob = resolvedType.create(level);
        if (mob == null) {
            return storedData;
        }
        RandomSource random = level.random;
        BlockPos pos = housing.getBlockPos();
        mob.moveTo(
                bounds.minX + random.nextDouble() * Math.max(0.0, bounds.maxX - bounds.minX),
                bounds.minY + random.nextDouble() * Math.max(0.0, bounds.maxY - bounds.minY),
                bounds.minZ + random.nextDouble() * Math.max(0.0, bounds.maxZ - bounds.minZ),
                random.nextFloat() * 360.0f,
                0.0f);
        level.addFreshEntity(mob);
        return storedData;
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

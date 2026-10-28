package com.example.sculkecho.entity;

import com.example.sculkecho.init.ModAttachments;
import com.example.sculkecho.init.ModEntities;
import com.example.sculkecho.infection.SymbiosisEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SculkSoul extends ThrowableProjectile {
    private LivingEntity target;

    public SculkSoul(EntityType<? extends SculkSoul> type, Level level) {
        super(type, level);
    }

    public SculkSoul(Level level, LivingEntity shooter) {
        super(ModEntities.SCULK_SOUL.get(), shooter, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        // Сетевых данных у души нет
    }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        if (this.tickCount > 100) { discard(); return; }

        // Наводимся на цель, если она ещё жива и незаражена
        if (target == null || !target.isAlive() || ModEntities.isInfected(target)
                || target.distanceToSqr(this) > 400) {
            target = findTarget();
        }
        if (target != null) {
            Vec3 toTarget = target.position().add(0, target.getBbHeight() * 0.5, 0)
                    .subtract(position());
            double len = toTarget.length();
            if (len > 0) {
                Vec3 dir = toTarget.scale(0.6 / len);
                setDeltaMovement(dir);
            }
        }

        ServerLevel sl = (ServerLevel) level();
        sl.sendParticles(ParticleTypes.SCULK_SOUL, getX(), getY(), getZ(), 2, 0.1, 0.1, 0.1, 0.02);
    }

    private LivingEntity findTarget() {
        LivingEntity best = null;
        double bestDist = 256;
        for (LivingEntity e : level().getEntitiesOfClass(LivingEntity.class,
                this.getBoundingBox().inflate(16))) {
            if (ModEntities.isInfected(e) || e == getOwner()) continue;
            if (e instanceof ServerPlayer) continue;
            double d = e.distanceToSqr(this);
            if (d < bestDist) { bestDist = d; best = e; }
        }
        return best;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide || isRemoved()) return;
        ServerLevel sl = (ServerLevel) level();
        sl.sendParticles(ParticleTypes.SCULK_SOUL, getX(), getY(), getZ(), 24, 0.5, 0.5, 0.5, 0.05);
        sl.playSound(null, blockPosition(), SoundEvents.SCULK_CATALYST_BLOOM,
                SoundSource.HOSTILE, 1f, 1.3f);

        if (result instanceof EntityHitResult ehr && ehr.getEntity() instanceof LivingEntity living
                && !ModEntities.isInfected(living)) {
            ModEntities.infecTarget(sl, living);
            if (getOwner() instanceof ServerPlayer owner) {
                SymbiosisEvents.creditConversion(owner);
            }
        }
        discard();
    }

    @Override
    public boolean isPickable() { return false; }
}
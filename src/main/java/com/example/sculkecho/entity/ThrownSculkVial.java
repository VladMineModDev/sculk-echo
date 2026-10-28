package com.example.sculkecho.entity;

import com.example.sculkecho.init.ModEntities;
import com.example.sculkecho.infection.InfectionData;
import com.example.sculkecho.infection.InfectionHelper;
import com.example.sculkecho.network.InfectionSyncPayload;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.network.PacketDistributor;

public class ThrownSculkVial extends ThrowableItemProjectile {
    public ThrownSculkVial(EntityType<? extends ThrownSculkVial> type, Level level) {
        super(type, level);
    }

    public ThrownSculkVial(Level level, LivingEntity shooter) {
        super(ModEntities.THROWN_SCULK_VIAL.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SPLASH_POTION;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide || isRemoved()) return;
        ServerLevel sl = (ServerLevel) level();
        sl.sendParticles(ParticleTypes.SCULK_SOUL, getX(), getY(), getZ(), 16, 0.4, 0.3, 0.4, 0.04);
        sl.playSound(null, blockPosition(), SoundEvents.SCULK_SENSOR_BREAK, SoundSource.HOSTILE, 0.8f, 1.3f);

        if (result instanceof EntityHitResult ehr && ehr.getEntity() instanceof LivingEntity living) {
            if (living instanceof ServerPlayer sp) {
                InfectionHelper.addInfection(sp, 2);
                InfectionData sd = InfectionHelper.getData(sp);
                PacketDistributor.sendToPlayer(sp, new InfectionSyncPayload(sd.stage(), sd.infection()));
            } else if (!ModEntities.isInfected(living) && sl.getRandom().nextFloat() < 0.25f) {
                ModEntities.infecTarget(sl, living);
            } else {
                living.hurt(sl.damageSources().magic(), 2f);
            }
        }
        discard();
    }
}
package com.example.sculkecho.entity;

import com.example.sculkecho.init.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class InfectedEvoker extends Evoker {
    public InfectedEvoker(EntityType<? extends Evoker> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this, LivingEntity.class, 10, true, false,
                e -> !ModEntities.isInfected(e) && e != this && e.isAlive()));
    }

    // Смерть: облако заражения + тотем + осколки
    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (level().isClientSide) return;
        ServerLevel sl = (ServerLevel) level();
        this.spawnAtLocation(Items.TOTEM_OF_UNDYING);
        for (int i = 0; i < 3; i++) this.spawnAtLocation(Items.ECHO_SHARD);
        sl.sendParticles(ParticleTypes.SCULK_SOUL, getX(), getY() + 1, getZ(), 40, 1.0, 0.8, 1.0, 0.08);
        sl.playSound(null, blockPosition(), SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.HOSTILE, 2f, 0.9f);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class,
                new AABB(blockPosition()).inflate(3.0))) {
            if (ModEntities.isInfected(e) || e == this) continue;
            e.hurt(sl.damageSources().magic(), 4f);
            ModEntities.infecTarget(sl, e);
        }
    }

    @Override
    public boolean removeWhenFarAway(double distance) { return false; }
}
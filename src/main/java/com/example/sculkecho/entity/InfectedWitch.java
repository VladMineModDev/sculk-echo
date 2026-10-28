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
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class InfectedWitch extends Witch {
    private int drinkCooldown = 0;

    public InfectedWitch(EntityType<? extends Witch> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this, LivingEntity.class, 10, true, false,
                e -> !ModEntities.isInfected(e) && e != this && e.isAlive()));
    }

    // Склянки заражения вместо ванильных зелий
    @Override
    public void performRangedAttack(LivingEntity target, float velocity) {
        ThrownSculkVial vial = new ThrownSculkVial(this.level(), this);
        Vec3 vec = target.getEyePosition().subtract(this.getEyePosition());
        double h = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        vial.shoot(vec.x, vec.y + h * 0.2, vec.z, 0.75f, 2.0f);
        this.playSound(SoundEvents.WITCH_THROW, 1f, 1f);
        this.level().addFreshEntity(vial);
    }

    // Пьёт осколок при низком здоровье
    @Override
    public void aiStep() {
        super.aiStep();
        if (drinkCooldown > 0) drinkCooldown--;
        if (level().isClientSide || drinkCooldown > 0) return;
        if (this.getHealth() < this.getMaxHealth() * 0.4f && this.random.nextFloat() < 0.02f) {
            drinkCooldown = 600;
            this.heal(6f);
            ServerLevel sl = (ServerLevel) level();
            sl.sendParticles(ParticleTypes.SCULK_SOUL, getX(), getY() + 1, getZ(), 20, 0.4, 0.6, 0.4, 0.05);
            sl.playSound(null, blockPosition(), SoundEvents.WITCH_DRINK, SoundSource.HOSTILE, 1f, 0.7f);
            sl.playSound(null, blockPosition(), SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.HOSTILE, 0.8f, 1.2f);
        }
    }

    // Смерть: лужа-облако обращения + осколок эха
    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (level().isClientSide) return;
        ServerLevel sl = (ServerLevel) level();
        this.spawnAtLocation(Items.ECHO_SHARD);
        sl.sendParticles(ParticleTypes.SCULK_SOUL, getX(), getY() + 0.5, getZ(), 28, 0.7, 0.5, 0.7, 0.05);
        sl.playSound(null, blockPosition(), SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.HOSTILE, 1.2f, 1.0f);
        for (LivingEntity e : sl.getEntitiesOfClass(LivingEntity.class,
                new AABB(blockPosition()).inflate(2.0))) {
            if (ModEntities.isInfected(e) || e == this) continue;
            e.hurt(sl.damageSources().magic(), 2f);
            ModEntities.infecTarget(sl, e);
        }
    }

    @Override
    public boolean removeWhenFarAway(double distance) { return false; }
}
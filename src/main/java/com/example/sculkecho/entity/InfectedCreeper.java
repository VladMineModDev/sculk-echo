package com.example.sculkecho.entity;

import com.example.sculkecho.init.ModEntities;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

public class InfectedCreeper extends Creeper {
    private int prevSwellDir = 0;

    public InfectedCreeper(EntityType<? extends Creeper> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this, LivingEntity.class, 10, true, false,
                e -> !ModEntities.isInfected(e) && e != this && e.isAlive()));
    }

    // Запал: вместо обычного шипения — крик скалк-шрикера
    @Override
    public void aiStep() {
        super.aiStep();
        int s = getSwellDir();
        if (s > 0 && prevSwellDir <= 0 && !level().isClientSide) {
            level().playSound(null, blockPosition(),
                    SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.HOSTILE, 2.0f, 1.4f);
        }
        prevSwellDir = s;
    }

    @Override
    public boolean shouldDespawnInPeaceful() { return false; }

    @Override
    public boolean removeWhenFarAway(double distance) { return false; }
}
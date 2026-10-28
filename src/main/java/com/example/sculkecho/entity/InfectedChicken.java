package com.example.sculkecho.entity;

import com.example.sculkecho.init.ModEntities;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class InfectedChicken extends Chicken {
    private int eggTimer = 6000; // 5 минут между скалк-яйцами

    public InfectedChicken(EntityType<? extends Chicken> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(
                this, LivingEntity.class, 10, true, false,
                e -> !ModEntities.isInfected(e) && e != this && e.isAlive()));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide && --eggTimer <= 0) {
            eggTimer = 6000 + random.nextInt(1200);
            ItemStack egg = new ItemStack(com.example.sculkecho.init.ModItems.SCULK_EGG.get());
            this.spawnAtLocation(egg);
            level().playSound(null, blockPosition(), net.minecraft.sounds.SoundEvents.CHICKEN_EGG,
                    net.minecraft.sounds.SoundSource.NEUTRAL, 1f, 0.8f);
        }
    }

    // Вместо обычного квохтания — звук скалк-сенсора
    @Override
    protected net.minecraft.sounds.SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_SENSOR_PLACE;
    }

    @Override
    public boolean removeWhenFarAway(double distance) { return false; }
}

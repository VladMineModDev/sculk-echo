package com.example.sculkecho.entity;

import com.example.sculkecho.init.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class InfectedEnderman extends EnderMan {
    public InfectedEnderman(EntityType<? extends EnderMan> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(
                this, LivingEntity.class, 10, true, false,
                e -> !ModEntities.isInfected(e) && e != this && e.isAlive()));
    }

    // Дроп: 1–2 осколка эха
    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, source, recentlyHit);
        int n = 1 + level.getRandom().nextInt(2);
        for (int i = 0; i < n; i++) {
            this.spawnAtLocation(Items.ECHO_SHARD);
        }
    }

    @Override
    public boolean removeWhenFarAway(double distance) { return false; }
}
package com.example.sculkecho.entity;

import com.example.sculkecho.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.projectile.windcharge.BreezeWindCharge;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class InfectedBreeze extends Breeze {
    public InfectedBreeze(EntityType<? extends Breeze> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(
                this, LivingEntity.class, 10, true, false,
                e -> !ModEntities.isInfected(e) && e != this && e.isAlive()));
    }

    public void performRangedAttack(LivingEntity target, float velocity) {
        BreezeWindCharge charge = new BreezeWindCharge(this, this.level());
        Vec3 vec = target.getEyePosition().subtract(this.getEyePosition());
        double h = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
        charge.shoot(vec.x, vec.y + h * 0.15, vec.z, 0.8f, 1.0f);
        this.level().addFreshEntity(charge);
        this.playSound(SoundEvents.BREEZE_SHOOT, 1.5f, 1.2f);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!level().isClientSide && this.onGround() && random.nextInt(40) == 0) {
            ServerLevel sl = (ServerLevel) level();
            BlockPos bp = blockPosition();
            if (sl.getBlockState(bp.below()).isSolid() && sl.getBlockState(bp).isAir()) {
                sl.setBlock(bp, Blocks.SCULK.defaultBlockState(), 3);
                sl.sendParticles(ParticleTypes.SCULK_SOUL, bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5,
                        8, 0.3, 0.2, 0.3, 0.03);
            }
        }
    }

    @Override
    public boolean removeWhenFarAway(double distance) { return false; }
}
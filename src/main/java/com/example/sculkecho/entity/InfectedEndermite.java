package com.example.sculkecho.entity;

import com.example.sculkecho.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class InfectedEndermite extends Endermite {
    private int burrowTimer = 200;

    public InfectedEndermite(EntityType<? extends Endermite> type, Level level) {
        super(type, level);
        var attr = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            attr.addPermanentModifier(new AttributeModifier(
                    ResourceLocation.fromNamespaceAndPath("sculkecho", "sculk_mite_speed"),
                    0.18, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(
                this, LivingEntity.class, 10, true, false,
                e -> !ModEntities.isInfected(e) && e != this && e.isAlive()));
    }

    // Зарывается и выныривает за спиной ближайшего игрока
    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide) return;
        if (--burrowTimer > 0) return;
        burrowTimer = 200 + random.nextInt(200);
        ServerLevel sl = (ServerLevel) level();
        Player target = sl.getNearestPlayer(this, 12.0);
        if (target == null) return;

        Vec3 old = position();
        Vec3 look = target.getLookAngle();
        Vec3 behind = target.position().subtract(look.multiply(2.0, 0, 2.0));

        sl.sendParticles(ParticleTypes.SCULK_SOUL, old.x, old.y + 0.2, old.z, 12, 0.3, 0.2, 0.3, 0.05);
        moveTo(behind.x, target.getY(), behind.z, getYRot(), 0);
        sl.sendParticles(ParticleTypes.SCULK_SOUL, behind.x, target.getY() + 0.2, behind.z, 12, 0.3, 0.2, 0.3, 0.05);
        sl.playSound(null, BlockPos.containing(behind.x, target.getY(), behind.z),
                SoundEvents.SCULK_SENSOR_BREAK, SoundSource.HOSTILE, 0.8f, 1.5f);
    }

    // Смерть = мини-облако обращения
    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (level().isClientSide) return;
        ServerLevel sl = (ServerLevel) level();
        sl.sendParticles(ParticleTypes.SCULK_SOUL, getX(), getY() + 0.3, getZ(), 24, 0.6, 0.4, 0.6, 0.05);
        sl.playSound(null, blockPosition(), SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.HOSTILE, 1f, 1.4f);
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
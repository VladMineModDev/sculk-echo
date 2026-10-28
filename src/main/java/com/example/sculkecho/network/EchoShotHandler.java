package com.example.sculkecho.network;

import com.example.sculkecho.init.ModAttachments;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class EchoShotHandler {
    private static final double SHOT_RANGE = 8.0;
    private static final float DAMAGE = 10.0f; // как у Вардена

    public static void handle(EchoShotPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            if (!player.getData(ModAttachments.STIMULATED.get())) return;
            if (!(player.level() instanceof ServerLevel level)) return;

            Vec3 eye = player.getEyePosition();
            Vec3 look = player.getLookAngle();

            // Ближайшая цель в конусе ~30 градусов
            LivingEntity target = null;
            double closestDistSq = Double.MAX_VALUE;
            AABB searchBox = new AABB(eye, eye).inflate(SHOT_RANGE);
            for (LivingEntity entity : level.getEntitiesOfClass(
                    LivingEntity.class, searchBox, e -> e != player && e.isAlive())) {
                Vec3 toEntity = entity.position().subtract(eye);
                if (toEntity.lengthSqr() < 0.0001) continue;
                if (look.dot(toEntity.normalize()) < 0.866) continue;
                double distSq = entity.distanceToSqr(player);
                if (distSq < closestDistSq && distSq <= SHOT_RANGE * SHOT_RANGE) {
                    closestDistSq = distSq;
                    target = entity;
                }
            }
            if (target == null) return;

            Vec3 toTarget = target.position().subtract(player.position());
            double dist = toTarget.length();
            if (dist < 0.0001) return;
            Vec3 dir = toTarget.normalize();

            // 1. Урон sonicBoom: игнорирует броню и щиты (в 1.21.1 — через hurt)
            target.hurt(level.damageSources().sonicBoom(player), DAMAGE);

            // 2. Отталкивание по ванильной формуле
            double d1 = target.getX() - player.getX();
            double d2 = target.getZ() - player.getZ();
            double d3 = Math.sqrt(d1 * d1 + d2 * d2);
            if (d3 > 0.001) {
                target.push(d1 / d3 * 2.5, 0.5, d2 / d3 * 2.5);
            }

            // 3. Звук Вардена
            level.playSound(null, player.blockPosition(),
                    SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 3.0f, 1.0f);

            // 4. Те самые кольца с фото: цепочка sonic_boom вдоль линии
            Vec3 start = eye.add(dir.scale(0.5));
            int steps = (int) (dist / 0.3);
            for (int i = 0; i <= steps; i++) {
                Vec3 p = start.add(dir.scale(i * 0.3));
                level.sendParticles(ParticleTypes.SONIC_BOOM, p.x, p.y, p.z, 1, 0, 0, 0, 0);
            }
        });
    }
}
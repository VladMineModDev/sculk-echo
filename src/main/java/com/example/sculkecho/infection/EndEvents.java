package com.example.sculkecho.infection;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.entity.InfectedEnderman;
import com.example.sculkecho.entity.InfectedShulker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = SculkEcho.MODID)
public class EndEvents {
    private static final Map<UUID, Vec3> lastPos = new HashMap<>();

    // Шептун: отслеживаем телепорт по резкой смене позиции
    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof InfectedEnderman e)) return;
        UUID id = e.getUUID();
        Vec3 prev = lastPos.get(id);
        Vec3 now = e.position();
        if (prev != null && prev.distanceToSqr(now) > 16) { // телепорт > 4 блоков
            ServerLevel level = (ServerLevel) e.level();
            level.sendParticles(ParticleTypes.SCULK_SOUL, now.x, now.y + 1, now.z, 16, 0.4, 0.8, 0.4, 0.04);
            if (level.getRandom().nextInt(10) == 0) {
                BlockPos bp = BlockPos.containing(now);
                if (level.getBlockState(bp).isAir()) {
                    level.setBlock(bp, Blocks.SCULK.defaultBlockState(), 3);
                }
            }
        }
        lastPos.put(id, now);
    }

    // Пуля шалкера: 10% шанс вырастить скалк на поражённом блоке
    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (!(event.getProjectile() instanceof ShulkerBullet bullet)) return;
        if (!(bullet.getOwner() instanceof InfectedShulker)) return;
        if (bullet.level().isClientSide) return;
        if (event.getRayTraceResult().getType() != HitResult.Type.BLOCK) return;
        ServerLevel level = (ServerLevel) bullet.level();
        BlockHitResult bhr = (BlockHitResult) event.getRayTraceResult();
        if (level.getRandom().nextInt(10) != 0) return;
        BlockPos target = bhr.getBlockPos().relative(bhr.getDirection());
        if (level.getBlockState(target).isAir()) {
            level.setBlock(target, Blocks.SCULK.defaultBlockState(), 3);
        }
    }
}
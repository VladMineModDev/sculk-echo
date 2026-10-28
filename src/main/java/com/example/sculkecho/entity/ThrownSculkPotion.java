package com.example.sculkecho.entity;

import com.example.sculkecho.init.ModEntities;
import com.example.sculkecho.init.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class ThrownSculkPotion extends ThrowableItemProjectile {
    public ThrownSculkPotion(EntityType<? extends ThrownSculkPotion> type, Level level) {
        super(type, level);
    }

    public ThrownSculkPotion(Level level, LivingEntity shooter) {
        super(ModEntities.THROWN_SCULK_POTION.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SCULK_POTION.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide || isRemoved()) return;
        ServerLevel sl = (ServerLevel) level();

        // Прямое попадание по мобу: урон + шанс обращения
        if (result instanceof EntityHitResult ehr && ehr.getEntity() instanceof LivingEntity living) {
            living.hurt(sl.damageSources().magic(), 2f);
            if (!ModEntities.isInfected(living) && sl.getRandom().nextFloat() < 0.2f) {
                ModEntities.infecTarget(sl, living);
            }
        }

        BlockPos hit = result instanceof BlockHitResult bhr
                ? bhr.getBlockPos()
                : BlockPos.containing(position());
        bloom(sl, hit);
        discard();
    }

    // Мини-каталист: корка скалка по поверхности
    private void bloom(ServerLevel sl, BlockPos hit) {
        sl.playSound(null, hit, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.BLOCKS, 1.2f, 1.0f);
        sl.sendParticles(ParticleTypes.SCULK_SOUL, hit.getX() + 0.5, hit.getY() + 1, hit.getZ() + 0.5,
                40, 1.2, 0.6, 1.2, 0.06);

        RandomSource r = sl.getRandom();
        int placed = 0;
        for (BlockPos p : BlockPos.betweenClosed(hit.offset(-2, -1, -2), hit.offset(2, 1, 2))) {
            if (placed >= 12) break;
            if (r.nextInt(100) >= 30) continue;
            BlockState st = sl.getBlockState(p);
            if (st.isAir() || st.is(Blocks.BEDROCK)) continue;
            if (!sl.getBlockState(p.above()).isAir()) continue; // только поверхность
            sl.setBlock(p.immutable(), Blocks.SCULK.defaultBlockState(), 3);
            placed++;
        }
    }
}
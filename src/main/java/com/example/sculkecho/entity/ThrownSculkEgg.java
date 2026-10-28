package com.example.sculkecho.entity;

import com.example.sculkecho.init.ModEntities;
import com.example.sculkecho.init.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

public class ThrownSculkEgg extends ThrowableItemProjectile {

    public ThrownSculkEgg(EntityType<? extends ThrownSculkEgg> type, Level level) {
        super(type, level);
    }

    public ThrownSculkEgg(Level level, LivingEntity shooter) {
        super(ModEntities.THROWN_SCULK_EGG.get(), shooter, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.SCULK_EGG.get();
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (level().isClientSide || isRemoved()) return;

        ServerLevel sl = (ServerLevel) level();

        // Случайный заражённый моб
        EntityType<? extends LivingEntity>[] types = new EntityType[] {
                ModEntities.INFECTED_ZOMBIE.get(),
                ModEntities.INFECTED_SKELETON.get(),
                ModEntities.INFECTED_SPIDER.get(),
                ModEntities.INFECTED_COW.get(),
                ModEntities.INFECTED_SHEEP.get(),
                ModEntities.INFECTED_CHICKEN.get(),
                ModEntities.INFECTED_ENDERMITE.get(),
                ModEntities.INFECTED_WITCH.get(),
                ModEntities.INFECTED_POLAR_BEAR.get()
        };

        EntityType<? extends LivingEntity> chosen = types[sl.getRandom().nextInt(types.length)];
        LivingEntity mob = chosen.create(sl);
        if (mob != null) {
            mob.moveTo(getX(), getY(), getZ(), sl.getRandom().nextFloat() * 360f, 0);
            sl.addFreshEntity(mob);
        }

        sl.sendParticles(ParticleTypes.SCULK_SOUL, getX(), getY() + 0.5, getZ(), 20, 0.5, 0.5, 0.5, 0.05);
        sl.playSound(null, blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.NEUTRAL, 1f, 1.2f);

        discard();
    }
}
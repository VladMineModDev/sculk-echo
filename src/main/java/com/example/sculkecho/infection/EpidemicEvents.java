package com.example.sculkecho.infection;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.entity.InfectedCreeper;
import com.example.sculkecho.init.ModEntities;
import com.example.sculkecho.network.InfectionSyncPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.windcharge.BreezeWindCharge;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = SculkEcho.MODID)
public class EpidemicEvents {

    // Эпидемия через урон: ближний бой, стрелы, пули шалкера, заряды бриза
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        if (event.getEntity().level().isClientSide) return;
        LivingEntity target = event.getEntity();
        if (ModEntities.isInfected(target)) return;

        Entity attacker = event.getSource().getEntity();
        Entity direct = event.getSource().getDirectEntity();
        boolean infectedMelee = ModEntities.isInfected(attacker);
        boolean infectedArrow = direct instanceof AbstractArrow arrow
                && ModEntities.isInfected(arrow.getOwner());
        boolean shulkerShot = direct instanceof ShulkerBullet sb
                && ModEntities.isInfected(sb.getOwner());
        boolean breezeShot = direct instanceof BreezeWindCharge bc
                && ModEntities.isInfected(bc.getOwner());
        if (!infectedMelee && !infectedArrow && !shulkerShot && !breezeShot) return;

        if (target instanceof ServerPlayer sp) {
            if (shulkerShot) InfectionHelper.addInfection(sp, 3);
            if (breezeShot) InfectionHelper.addInfection(sp, 2);
            if (shulkerShot || breezeShot) {
                InfectionData sd = InfectionHelper.getData(sp);
                PacketDistributor.sendToPlayer(sp, new InfectionSyncPayload(sd.stage(), sd.infection()));
            }
        }

        if (target.level().getRandom().nextFloat() < 0.15f) {
            ModEntities.infecTarget((ServerLevel) target.level(), target);
            if (attacker instanceof ServerPlayer spAtt) {
                SymbiosisEvents.creditConversion(spAtt);
            }
        }
    }

    // Взрыв скалк-крипера: блоки целы, облако обращает всех задетых
    @SubscribeEvent
    public static void onExplosionStart(ExplosionEvent.Start event) {
        if (!(event.getExplosion().getIndirectSourceEntity() instanceof InfectedCreeper creeper)) return;
        if (creeper.level().isClientSide) return;
        event.setCanceled(true);
        sculkBurst((ServerLevel) creeper.level(), creeper.position(), creeper);
    }

    private static void sculkBurst(ServerLevel level, Vec3 center, InfectedCreeper source) {
        BlockPos bp = BlockPos.containing(center);
        level.playSound(null, bp, SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.HOSTILE, 4.0f, 0.8f);
        level.playSound(null, bp, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 1.5f, 1.2f);
        level.sendParticles(ParticleTypes.SCULK_SOUL, center.x, center.y + 0.5, center.z, 60, 1.5, 1.0, 1.5, 0.1);
        level.sendParticles(ParticleTypes.SONIC_BOOM, center.x, center.y + 0.5, center.z, 3, 0.5, 0.5, 0.5, 0);

        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class,
                new AABB(center, center).inflate(4.5))) {
            if (ModEntities.isInfected(e) || e == source) continue;
            e.hurt(level.damageSources().explosion(null, source), 6.0f);
            ModEntities.infecTarget(level, e);
        }
    }
}
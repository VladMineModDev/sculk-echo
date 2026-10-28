package com.example.sculkecho.entity;

import com.example.sculkecho.infection.InfectionData;
import com.example.sculkecho.infection.InfectionHelper;
import com.example.sculkecho.infection.PossessionData;
import com.example.sculkecho.init.ModAttachments;
import com.example.sculkecho.network.InfectionSyncPayload;
import com.example.sculkecho.network.PossessionPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

public class EchoWisp extends Entity {
    private int phase = 0;       // 0 = пролёт, 1 = наблюдение из углов, 2 = вселение
    private int age = 0;
    private int peekCount = 0;   // сколько раз уже выглянула
    private int lookTimer = 0;   // сколько игрок смотрит прямо на неё
    private int vanishTimer = 0; // пауза, пока скрыта
    private Vec3 flyDirection = Vec3.ZERO;

    public EchoWisp(EntityType<?> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noPhysics = true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {}

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        phase = tag.getInt("phase");
        age = tag.getInt("age");
        peekCount = tag.getInt("peek");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("phase", phase);
        tag.putInt("age", age);
        tag.putInt("peek", peekCount);
    }

    @Override
    public boolean isPushable() { return false; }

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide) return;
        ServerLevel level = (ServerLevel) level();
        Player target = level.getNearestPlayer(this, 64);
        if (target == null) { discard(); return; }
        age++;

        // Силуэт: дым + скалк-искры (видны на свету; в темноте тень выдаёт эхо-след)
        if (!isInvisible()) {
            if (age % 2 == 0) level.sendParticles(ParticleTypes.LARGE_SMOKE, getX(), getY() + 0.8, getZ(), 8, 0.25, 0.45, 0.25, 0.015);
            if (age % 4 == 0) level.sendParticles(ParticleTypes.SCULK_SOUL, getX(), getY() + 0.8, getZ(), 2, 0.15, 0.3, 0.15, 0.03);
        }

        if (phase == 0) {
            // ФАЗА 0: пролёт перед игроком
            if (age == 1) {
                Vec3 look = target.getLookAngle();
                Vec3 side = new Vec3(-look.z, 0, look.x);
                setPos(target.getEyePosition().add(look.scale(16)).add(side.scale(9)).add(0, 1.5, 0));
                flyDirection = side.scale(-1).add(look.scale(0.25)).normalize().scale(0.4);
            }
            setPos(position().add(flyDirection));
            if (age > 50) { phase = 1; age = 0; choosePeekSpot(level, target); }

        } else if (phase == 1) {
            // ФАЗА 1: наблюдение из углов
            if (vanishTimer > 0) {
                vanishTimer--;
                if (vanishTimer == 0) {
                    if (peekCount >= 3 && age > 120) { // минимум 6 секунд наблюдения перед вселением
                        phase = 2; age = 0;
                        Vec3 look = target.getLookAngle();
                        setPos(target.position().add(0, 1.4, 0).subtract(look.scale(3)));
                    } else {
                        choosePeekSpot(level, target);
                        setInvisible(false);
                        lookTimer = 0;
                    }
                }
                return; // скрыта
            }
            setPos(position().add(0, Math.sin(age * 0.1) * 0.02, 0));

            // Игрок смотрит прямо на неё?
            Vec3 toWisp = position().subtract(target.getEyePosition()).normalize();
            if (toWisp.dot(target.getLookAngle()) > 0.985) lookTimer++;
            else lookTimer = Math.max(0, lookTimer - 2);

            if (lookTimer > 15) { // ~0.7с прямого взгляда — тень сбегает
                peekCount++;
                setInvisible(true);
                vanishTimer = 30 + random.nextInt(30);
                setPos(target.position().add(0, -40, 0));
            }
            if (age > 400) {      // насмотрелась — идёт вселяться
                phase = 2; age = 0;
                Vec3 look = target.getLookAngle();
                setPos(target.position().add(0, 1.4, 0).subtract(look.scale(3)));
            }

        } else {
            // ФАЗА 2: вселение со спины
            Vec3 look = target.getLookAngle();
            Vec3 behind = target.position().add(0, 1.2, 0).subtract(look.scale(1.5));
            Vec3 to = behind.subtract(position());
            if (to.length() > 0.4 && age < 80) {
                setPos(position().add(to.normalize().scale(Math.min(0.45, to.length()))));
            } else {
                Vec3 into = target.position().add(0, 1.2, 0).subtract(position());
                setPos(position().add(into.normalize().scale(0.55)));
                if (distanceTo(target) < 1.2 && target instanceof ServerPlayer sp) {
                    possess(level, sp);
                    discard();
                    return;
                }
            }
            if (age > 200) discard();
        }
    }

    // Точка "подглядывания": в 35-65° от взгляда, в 8-13 блоках, не внутри блока
    private void choosePeekSpot(ServerLevel level, Player target) {
        Vec3 eye = target.getEyePosition();
        Vec3 look = target.getLookAngle();
        Vec3 horiz = new Vec3(look.x, 0, look.z);
        if (horiz.lengthSqr() < 0.001) horiz = new Vec3(1, 0, 0);
        horiz = horiz.normalize();
        Vec3 side = new Vec3(-horiz.z, 0, horiz.x);
        double sideSign = random.nextBoolean() ? 1 : -1;
        double offsetDeg = 15 + random.nextInt(20); // 15-35°: периферия зрения, видно боковым взглядом
        double t = Math.tan(Math.toRadians(offsetDeg)) * sideSign;
        Vec3 dir = horiz.add(side.scale(t)).normalize();
        double dist = 6 + random.nextInt(5); // ближе: 6-10 блоков
        Vec3 spot = eye.add(dir.scale(dist));
        spot = new Vec3(spot.x, eye.y - 1.0 + random.nextInt(3), spot.z);
        for (int i = 0; i < 4; i++) {
            BlockPos bp = BlockPos.containing(spot);
            if (level.getBlockState(bp).getCollisionShape(level, bp).isEmpty()) break;
            spot = spot.add(0, 1, 0);
        }
        setPos(spot);
        setInvisible(false);
    }

    private void possess(ServerLevel level, ServerPlayer player) {
        level.playSound(null, player.blockPosition(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.HOSTILE, 1.5f, 0.7f);
        level.playSound(null, player.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.HOSTILE, 0.7f, 1.5f);
        level.sendParticles(ParticleTypes.SCULK_SOUL, player.getX(), player.getY() + 1.2, player.getZ(), 40, 0.4, 0.8, 0.4, 0.06);
        level.sendParticles(ParticleTypes.LARGE_SMOKE, player.getX(), player.getY() + 1.2, player.getZ(), 20, 0.3, 0.6, 0.3, 0.02);
        InfectionHelper.addInfection(player, 10);
        InfectionData after = InfectionHelper.getData(player);
        if (after.infection() < 20) InfectionHelper.addInfection(player, 20 - after.infection());
        player.setData(ModAttachments.POSSESSION.get(), new PossessionData(true, 0));
        PacketDistributor.sendToPlayer(player, new PossessionPayload(true, InfectionHelper.getData(player).stage()));
    }
}
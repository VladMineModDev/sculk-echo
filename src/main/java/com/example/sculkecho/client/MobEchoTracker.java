package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.entity.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(value = Dist.CLIENT, modid = SculkEcho.MODID)
public class MobEchoTracker {
    private static final Map<Integer, Vec3> lastPositions = new HashMap<>();
    private static final double MIN_MOVE = 0.02;
    private static final int TRACK_RANGE = 32;
    private static final long TRAIL_LIFETIME_MS = 2000;
    private static final int TRAIL_COLOR = 0x80C8FF;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        Vec3 playerPos = mc.player.position();
        AABB range = new AABB(playerPos, playerPos).inflate(TRACK_RANGE);

        // Тень EchoWisp: видна эхо-зрением на ЛЮБОЙ стадии, кроме моментов, когда прячется
        for (Entity entity : mc.level.getEntities((Entity) null, range, e -> e instanceof EchoWisp && !e.isInvisible())) {
            ClientEchoState.addPassiveBox(entity.getBoundingBox().inflate(0.1), 0x1FB8B8, 500);
        }

        // Скалк-торговец: золотой маяк в эхо-зрении
        if (ClientEchoState.getInfection() > 0) {
            for (Entity entity : mc.level.getEntities((Entity) null, range, e -> e instanceof SculkVillager)) {
                ClientEchoState.addPassiveBox(entity.getBoundingBox(), 0xFFD24F, 700);
            }
        }

        // Скалк-ведьма: бирюзовое ядро + фиолетовая кромка (двойная аура)
        if (ClientEchoState.getInfection() > 0) {
            for (Entity entity : mc.level.getEntities((Entity) null, range, e -> e instanceof InfectedWitch)) {
                ClientEchoState.addPassiveBox(entity.getBoundingBox(), 0x0ED0D0, 500);
                ClientEchoState.addPassiveBox(entity.getBoundingBox().inflate(0.25), 0x8A4FE0, 250);
            }
        }

        // Шептун: глушение сонара вблизи + мерцающий силуэт
        for (Entity entity : mc.level.getEntities((Entity) null, range, e -> e instanceof InfectedEnderman)) {
            if (entity.distanceTo(mc.player) < 16) {
                ClientEchoState.addInterference(0.35f);
            }
            if ((mc.player.tickCount / 10) % 2 == 0 && ClientEchoState.getInfection() > 0) {
                ClientEchoState.addPassiveBox(entity.getBoundingBox(), 0x0ED0D0, 300);
            }
        }

        // Заражённые мобы звучат скалком постоянно: бирюзовый силуэт
        if (ClientEchoState.getInfection() > 0) {
            for (Entity entity : mc.level.getEntities((Entity) null, range, e -> e instanceof InfectedZombie)) {
                ClientEchoState.addPassiveBox(entity.getBoundingBox(), 0x0ED0D0, 600);
            }
        }

        // Мобы — только со стадии 3
        if (ClientEchoState.getStage() < 3) {
            lastPositions.clear();
            return;
        }

        for (Entity entity : mc.level.getEntities((Entity) null, range, e -> e instanceof LivingEntity && e != mc.player)) {
            int id = entity.getId();
            Vec3 current = entity.position();
            Vec3 prev = lastPositions.get(id);
            if (prev != null && current.distanceTo(prev) > MIN_MOVE) {
                ClientEchoState.addPassiveBox(entity.getBoundingBox(), TRAIL_COLOR, TRAIL_LIFETIME_MS);
            }
            lastPositions.put(id, current);
        }

        lastPositions.entrySet().removeIf(entry -> entry.getValue().distanceTo(playerPos) > TRACK_RANGE + 10);
    }
}
package com.example.sculkecho.service;

import com.example.sculkecho.infection.InfectionHelper;
import com.example.sculkecho.network.EchoPointsPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class EchoService {
    public record EchoProfile(double radius, int maxPoints, int cooldown) {}

    public static EchoProfile profileForStage(int stage) {
        return switch (stage) {
            case 0 -> new EchoProfile(6.0, 80, 30);
            case 1 -> new EchoProfile(8.0, 120, 24);
            case 2 -> new EchoProfile(12.0, 200, 18);
            case 3 -> new EchoProfile(16.0, 300, 14);
            case 4 -> new EchoProfile(20.0, 400, 10);
            default -> new EchoProfile(24.0, 500, 8);
        };
    }

    public static void pulse(ServerPlayer player, EchoProfile profile) {
        ServerLevel level = player.serverLevel();
        Vec3 feet = player.position().add(0, 0.5, 0);
        List<Vec3> points = new ArrayList<>();

        // Горизонтальная волна
        double[] heights = {0.3, 1.0, 1.8};
        int raysPerLayer = profile.maxPoints() / heights.length;
        for (double height : heights) {
            Vec3 origin = feet.add(0, height, 0);
            for (int i = 0; i < raysPerLayer; i++) {
                double angle = (i / (double) raysPerLayer) * Math.PI * 2.0;
                Vec3 direction = new Vec3(Math.cos(angle), 0, Math.sin(angle));
                Vec3 end = origin.add(direction.scale(profile.radius()));
                HitResult hit = level.clip(new ClipContext(
                        origin, end,
                        ClipContext.Block.OUTLINE,
                        ClipContext.Fluid.NONE,
                        player
                ));
                if (hit.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHit = (BlockHitResult) hit;
                    BlockState state = level.getBlockState(blockHit.getBlockPos());
                    if (!isPlant(state)) {
                        points.add(hit.getLocation());
                    }
                }
            }
        }

        // Пол и потолок
        int verticalRays = profile.maxPoints() / 4;
        for (int i = 0; i < verticalRays; i++) {
            double distance = profile.radius() * (i / (double) verticalRays);
            double angle = (i * 2.39996) % (Math.PI * 2.0);
            double x = Math.cos(angle) * distance;
            double z = Math.sin(angle) * distance;

            // Вниз
            Vec3 downStart = feet.add(x, 0.1, z);
            HitResult floor = level.clip(new ClipContext(
                    downStart, downStart.add(0, -3, 0),
                    ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player
            ));
            if (floor.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) floor;
                BlockState state = level.getBlockState(blockHit.getBlockPos());
                if (!isPlant(state)) {
                    points.add(floor.getLocation());
                }
            }

            // Вверх
            Vec3 upStart = feet.add(x, 1.8, z);
            HitResult ceiling = level.clip(new ClipContext(
                    upStart, upStart.add(0, 4, 0),
                    ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player
            ));
            if (ceiling.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHit = (BlockHitResult) ceiling;
                BlockState state = level.getBlockState(blockHit.getBlockPos());
                if (!isPlant(state)) {
                    points.add(ceiling.getLocation());
                }
            }
        }

        addLivingEntityPoints(player, feet, profile.radius(), points, profile.maxPoints());

        level.playSound(null, player.blockPosition(), SoundEvents.SCULK_CLICKING, SoundSource.PLAYERS, 0.6F, 1.2F);

        int stage = InfectionHelper.getStage(player);
        PacketDistributor.sendToPlayer(player, new EchoPointsPayload(stage, (float) profile.radius(), points));
    }

    /**
     * Проверяет, является ли блок растением, травой или листвой.
     * Если да, эхолокация его игнорирует.
     */
    private static boolean isPlant(BlockState state) {
        Block block = state.getBlock();
        if (block instanceof net.minecraft.world.level.block.BushBlock) return true;
        if (block instanceof net.minecraft.world.level.block.FlowerBlock) return true;
        if (block instanceof net.minecraft.world.level.block.TallGrassBlock) return true;
        if (block instanceof net.minecraft.world.level.block.DoublePlantBlock) return true;
        if (block instanceof net.minecraft.world.level.block.MushroomBlock) return true;
        if (block instanceof net.minecraft.world.level.block.VineBlock) return true;
        if (block instanceof net.minecraft.world.level.block.LeavesBlock) return true;
        if (block instanceof net.minecraft.world.level.block.CropBlock) return true;
        if (block instanceof net.minecraft.world.level.block.CactusBlock) return true;
        if (block instanceof net.minecraft.world.level.block.SugarCaneBlock) return true;
        if (block instanceof net.minecraft.world.level.block.BambooStalkBlock) return true;
        if (block instanceof net.minecraft.world.level.block.GlowLichenBlock) return true;
        if (block instanceof net.minecraft.world.level.block.MossBlock) return true;

        if (block == Blocks.SHORT_GRASS || block == Blocks.FERN || block == Blocks.LARGE_FERN ||
                block == Blocks.TALL_GRASS || block == Blocks.DEAD_BUSH || block == Blocks.SEAGRASS ||
                block == Blocks.TALL_SEAGRASS || block == Blocks.KELP || block == Blocks.KELP_PLANT) return true;

        return false;
    }

    private static void addLivingEntityPoints(ServerPlayer player, Vec3 origin, double radius, List<Vec3> points, int maxPoints) {
        Level level = player.level();
        AABB box = new AABB(origin, origin).inflate(radius);
        for (Entity entity : level.getEntities(player, box, Entity::isAlive)) {
            if (points.size() >= maxPoints) return;
            if (entity instanceof LivingEntity && entity != player) {
                points.add(entity.position().add(0, entity.getBbHeight() * 0.6, 0));
            }
        }
    }
}
package com.example.sculkecho.infection;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.entity.SculkVillager;
import com.example.sculkecho.init.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = SculkEcho.MODID)
public class SculkVillagerSpawnEvents {
    private static final Map<Long, UUID> spawned = new HashMap<>();

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        ServerLevel level = event.getServer().overworld();
        if (level.getGameTime() % 200 != 0) return; // раз в 10 секунд

        for (ServerPlayer player : level.players()) {
            BlockPos pos = player.blockPosition();
            long key = pos.asLong();

            // Один торговец на вход в Древний город
            if (!spawned.containsKey(key)) {
                StructureStart city = level.structureManager()
                        .getStructureAt(pos, level.registryAccess()
                                .registryOrThrow(net.minecraft.core.registries.Registries.STRUCTURE)
                                .get(BuiltinStructures.ANCIENT_CITY));
                if (city != null && city.isValid()) {
                    spawnMerchantNear(level, pos);
                    spawned.put(key, UUID.randomUUID());
                }
            }

            // Ванильный житель у каталиста принимает скалк
            for (Villager v : level.getEntitiesOfClass(Villager.class,
                    new AABB(pos.getX() - 24, pos.getY() - 24, pos.getZ() - 24,
                            pos.getX() + 24, pos.getY() + 24, pos.getZ() + 24))) {
                if (level.getRandom().nextInt(20) != 0) continue;
                if (nearCatalyst(level, v.blockPosition())) {
                    convert(level, v);
                }
            }
        }

        spawned.entrySet().removeIf(e -> {
            BlockPos p = BlockPos.of(e.getKey());
            return level.players().stream()
                    .noneMatch(pl -> pl.blockPosition().distSqr(p) < 64 * 64);
        });
    }

    private static void spawnMerchantNear(ServerLevel level, BlockPos pos) {
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos();
        for (int attempt = 0; attempt < 10; attempt++) {
            int dx = -8 + level.getRandom().nextInt(17);
            int dz = -8 + level.getRandom().nextInt(17);
            for (int y = pos.getY() - 3; y < pos.getY() + 10; y++) {
                mpos.set(pos.getX() + dx, y, pos.getZ() + dz);
                if (level.getBlockState(mpos).isAir() && !level.getBlockState(mpos.below()).isAir()) {
                    SculkVillager v = ModEntities.SCULK_VILLAGER.get().create(level);
                    if (v == null) return;
                    v.moveTo(mpos.getX() + 0.5, mpos.getY(), mpos.getZ() + 0.5,
                            level.getRandom().nextFloat() * 360f, 0);
                    level.addFreshEntity(v);
                    return;
                }
            }
        }
    }

    private static boolean nearCatalyst(ServerLevel level, BlockPos center) {
        for (BlockPos bp : BlockPos.betweenClosed(
                center.offset(-2, -1, -2), center.offset(2, 2, 2))) {
            if (level.getBlockState(bp).is(Blocks.SCULK_CATALYST)) return true;
        }
        return false;
    }

    private static void convert(ServerLevel level, Villager v) {
        SculkVillager sv = ModEntities.SCULK_VILLAGER.get().create(level);
        if (sv == null) return;
        sv.moveTo(v.position());
        sv.setYRot(v.getYRot());
        level.addFreshEntity(sv);
        level.sendParticles(ParticleTypes.SCULK_SOUL, v.getX(), v.getY() + 1, v.getZ(), 24, 0.5, 0.9, 0.5, 0.05);
        level.playSound(null, v.blockPosition(), SoundEvents.SCULK_SHRIEKER_SHRIEK, SoundSource.HOSTILE, 1f, 1.2f);
        v.discard();
    }
}
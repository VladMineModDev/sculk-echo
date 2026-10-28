package com.example.sculkecho.infection;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.entity.EchoWisp;
import com.example.sculkecho.init.ModAttachments;
import com.example.sculkecho.init.ModEntities;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import com.example.sculkecho.network.InfectionSyncPayload;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import com.example.sculkecho.init.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.minecraft.world.damagesource.DamageSource;
import com.example.sculkecho.entity.InfectedZombie;
import com.example.sculkecho.init.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = SculkEcho.MODID)
public class InfectionEvents {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;

        DamageSource source = event.getSource();
        if (source.getEntity() instanceof ServerPlayer killer) {
            if (killer.getData(ModAttachments.STIMULATED.get())) {
                // Регенерация 2-4 сердца за убийство
                float heal = 4.0f + killer.getRandom().nextFloat() * 4.0f;
                killer.heal(heal);
            }
        }
    }

    private static void convertNearbyZombie(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        for (Zombie z : level.getEntitiesOfClass(Zombie.class, player.getBoundingBox().inflate(20),
                z -> !(z instanceof InfectedZombie) && !z.isBaby())) {
            if (player.getRandom().nextInt(3) != 0) continue;
            InfectedZombie inf = ModEntities.INFECTED_ZOMBIE.get().create(level);
            if (inf == null) return;
            inf.moveTo(z.position());
            inf.setYRot(z.getYRot());
            inf.setHealth(z.getHealth());
            level.addFreshEntity(inf);
            z.discard();
            level.sendParticles(ParticleTypes.SCULK_SOUL, z.getX(), z.getY() + 1, z.getZ(), 12, 0.4, 0.8, 0.4, 0.03);
            return;
        }
    }

    // Варден роняет Echo Stimulator
    @SubscribeEvent
    public static void onWardenDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Warden warden && !warden.level().isClientSide) {
            warden.level().addFreshEntity(new ItemEntity(warden.level(),
                    warden.getX(), warden.getY(), warden.getZ(),
                    new ItemStack(ModItems.ECHO_STIMULATOR.get())));
        }
    }

    // ПКМ по стимулятору: установка навсегда
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        if (event.getLevel().isClientSide) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        ItemStack stack = event.getItemStack();
        if (!stack.is(ModItems.ECHO_STIMULATOR.get())) return;
        if (player.getData(ModAttachments.STIMULATED.get())) return;

        player.setData(ModAttachments.STIMULATED.get(), true);
        player.setData(ModAttachments.POSSESSION.get(), new PossessionData(true, 0)); // тени больше не трогают
        stack.shrink(1);
        InfectionData data = InfectionHelper.getData(player);
        InfectionHelper.addInfection(player, 100 - data.infection());
        InfectionData after = InfectionHelper.getData(player);
        PacketDistributor.sendToPlayer(player, new InfectionSyncPayload(after.stage(), after.infection()));
    }
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) syncStage(sp);
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) syncStage(sp);
    }


    // Заражённые мобы не выбирают носителя скалк-защиты целью
    @SubscribeEvent
    public static void onEntityTick(net.neoforged.neoforge.event.tick.EntityTickEvent.Post event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof net.minecraft.world.entity.Mob mob)) return;
        if (!ModEntities.isInfected(mob)) return;
        LivingEntity target = mob.getTarget();
        if (target != null && SculkWard.hasWard(target)) {
            mob.setTarget(null);
        }
    }

    private static void syncStage(ServerPlayer sp) {
        InfectionData data = InfectionHelper.getData(sp);
        PacketDistributor.sendToPlayer(sp,
                new InfectionSyncPayload(data.stage(), data.infection()));
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.level().isClientSide) return;

        InfectionData data = InfectionHelper.getData(player);
        if (data.cooldown() > 0) {
            InfectionHelper.setData(player, data.withCooldown(data.cooldown() - 1));
        }

        if (player.tickCount % 20 == 0) {
            boolean immune = player.getData(ModAttachments.IMMUNE.get());
            boolean inDeepDark = player.level().getBiome(player.blockPosition()).is(Biomes.DEEP_DARK);

            if (!immune) {
                int gain = 0;
                if (inDeepDark) gain += 1;
                if (isStandingOnSculk(player)) gain += 2;
                if (gain > 0) {
                    InfectionHelper.addInfection(player, gain);
                    InfectionData sd = InfectionHelper.getData(player);
                    PacketDistributor.sendToPlayer(player, new InfectionSyncPayload(sd.stage(), sd.infection()));
                }
                handleWisp(player, inDeepDark);
            }

            if (SculkWard.hasWard(player)) {
                for (net.minecraft.world.entity.Mob m : player.level().getEntitiesOfClass(
                        net.minecraft.world.entity.Mob.class, player.getBoundingBox().inflate(20),
                        mm -> ModEntities.isInfected(mm) && mm.getTarget() == player)) {
                    m.setTarget(null);
                }
            }

            if (player.getData(ModAttachments.STIMULATED.get())) {
                InfectionData cur = InfectionHelper.getData(player);
                if (cur.infection() < 100) InfectionHelper.addInfection(player, 100 - cur.infection());
            }

            if (inDeepDark && player.tickCount % 100 == 0) convertNearbyZombie(player);
        }
    }

    private static void handleWisp(ServerPlayer player, boolean inDeepDark) {
        PossessionData possession = player.getData(ModAttachments.POSSESSION.get());
        if (possession.possessed()) return;

        int delay = possession.wispDelay();
        if (delay > 0) {
            player.setData(ModAttachments.POSSESSION.get(), new PossessionData(false, delay - 20));
            return;
        }
        if (!inDeepDark) return;

        // Тень появляется ТОЛЬКО если:
        // 1. Игрок уже заражён (инфекция > 10), ИЛИ
        // 2. Игрок в Deep Dark больше 2 минут (120 секунд = 2400 тиков)
        InfectionData data = InfectionHelper.getData(player);
        boolean alreadyInfected = data.infection() > 10;
        boolean inDeepDarkLongEnough = (player.tickCount % 2400) < 20; // проверяем каждые 2 минуты

        if (!alreadyInfected && !inDeepDarkLongEnough) return;

        // Шанс 1/30 в секунду
        if (player.getRandom().nextInt(30) != 0) return;

        EchoWisp wisp = new EchoWisp(ModEntities.ECHO_WISP.get(), player.level());
        wisp.setPos(player.position().add(0, 2, 0));
        player.level().addFreshEntity(wisp);
        // Кулдаун 5 минут (6000 тиков)
        player.setData(ModAttachments.POSSESSION.get(), new PossessionData(false, 6000));
    }

    private static boolean isStandingOnSculk(ServerPlayer player) {
        Block below = player.level().getBlockState(player.blockPosition().below()).getBlock();
        return below == Blocks.SCULK
                || below == Blocks.SCULK_CATALYST
                || below == Blocks.SCULK_SENSOR
                || below == Blocks.SCULK_SHRIEKER
                || below == Blocks.SCULK_VEIN;
    }
}
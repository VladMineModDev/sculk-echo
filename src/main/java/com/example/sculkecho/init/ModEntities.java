package com.example.sculkecho.init;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.entity.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.example.sculkecho.entity.InfectedCow;
import com.example.sculkecho.entity.InfectedSheep;
import com.example.sculkecho.entity.InfectedSpider;
import com.example.sculkecho.entity.InfectedChicken;
import com.example.sculkecho.entity.ThrownSculkEgg;

import java.util.function.Supplier;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, SculkEcho.MODID);

    public static final Supplier<EntityType<EchoWisp>> ECHO_WISP =
            ENTITY_TYPES.register("echo_wisp", () -> EntityType.Builder.<EchoWisp>of(EchoWisp::new, MobCategory.MISC)
                    .sized(0.6f, 1.8f).clientTrackingRange(64).updateInterval(1).fireImmune()
                    .build("echo_wisp"));

    public static final Supplier<EntityType<InfectedZombie>> INFECTED_ZOMBIE =
            ENTITY_TYPES.register("infected_zombie", () -> EntityType.Builder.of(InfectedZombie::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).clientTrackingRange(8)
                    .build("infected_zombie"));

    public static final Supplier<EntityType<InfectedSkeleton>> INFECTED_SKELETON =
            ENTITY_TYPES.register("infected_skeleton", () -> EntityType.Builder.of(InfectedSkeleton::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.99f).clientTrackingRange(8)
                    .build("infected_skeleton"));

    public static final Supplier<EntityType<InfectedPig>> INFECTED_PIG =
            ENTITY_TYPES.register("infected_pig", () -> EntityType.Builder.of(InfectedPig::new, MobCategory.MONSTER)
                    .sized(0.9f, 0.9f).clientTrackingRange(8)
                    .build("infected_pig"));

    public static final Supplier<EntityType<InfectedCreeper>> INFECTED_CREEPER =
            ENTITY_TYPES.register("infected_creeper", () -> EntityType.Builder.of(InfectedCreeper::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.7f).clientTrackingRange(8)
                    .build("infected_creeper"));
    public static final Supplier<EntityType<SculkVillager>> SCULK_VILLAGER =
            ENTITY_TYPES.register("sculk_villager", () -> EntityType.Builder.of(SculkVillager::new, MobCategory.MISC)
                    .sized(0.6f, 1.95f).clientTrackingRange(8)
                    .build("sculk_villager"));

    public static final Supplier<EntityType<InfectedCow>> INFECTED_COW =
            ENTITY_TYPES.register("infected_cow", () -> EntityType.Builder.of(InfectedCow::new, MobCategory.MONSTER)
                    .sized(0.9f, 1.4f).clientTrackingRange(8)
                    .build("infected_cow"));

    public static final Supplier<EntityType<InfectedSheep>> INFECTED_SHEEP =
            ENTITY_TYPES.register("infected_sheep", () -> EntityType.Builder.of(InfectedSheep::new, MobCategory.MONSTER)
                    .sized(0.9f, 1.3f).clientTrackingRange(8)
                    .build("infected_sheep"));

    public static final Supplier<EntityType<InfectedSpider>> INFECTED_SPIDER =
            ENTITY_TYPES.register("infected_spider", () -> EntityType.Builder.of(InfectedSpider::new, MobCategory.MONSTER)
                    .sized(1.4f, 0.9f).clientTrackingRange(8)
                    .build("infected_spider"));

    public static final Supplier<EntityType<InfectedChicken>> INFECTED_CHICKEN =
            ENTITY_TYPES.register("infected_chicken", () -> EntityType.Builder.of(InfectedChicken::new, MobCategory.MONSTER)
                    .sized(0.4f, 0.7f).clientTrackingRange(8)
                    .build("infected_chicken"));
    public static final Supplier<EntityType<ThrownSculkEgg>> THROWN_SCULK_EGG =
            ENTITY_TYPES.register("thrown_sculk_egg", () -> EntityType.Builder.<ThrownSculkEgg>of(ThrownSculkEgg::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10)
                    .build("thrown_sculk_egg"));

    public static final Supplier<EntityType<InfectedShulker>> INFECTED_SHULKER =
            ENTITY_TYPES.register("infected_shulker", () -> EntityType.Builder.of(InfectedShulker::new, MobCategory.MONSTER)
                    .sized(1.0f, 1.0f).clientTrackingRange(8)
                    .build("infected_shulker"));

    public static final Supplier<EntityType<InfectedEnderman>> INFECTED_ENDERMAN =
            ENTITY_TYPES.register("infected_enderman", () -> EntityType.Builder.of(InfectedEnderman::new, MobCategory.MONSTER)
                    .sized(0.6f, 2.9f).clientTrackingRange(8)
                    .build("infected_enderman"));

    public static final Supplier<EntityType<InfectedEndermite>> INFECTED_ENDERMITE =
            ENTITY_TYPES.register("infected_endermite", () -> EntityType.Builder.of(InfectedEndermite::new, MobCategory.MONSTER)
                    .sized(0.4f, 0.3f).clientTrackingRange(8)
                    .build("infected_endermite"));

    public static final Supplier<EntityType<InfectedWitch>> INFECTED_WITCH =
            ENTITY_TYPES.register("infected_witch", () -> EntityType.Builder.of(InfectedWitch::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).clientTrackingRange(8)
                    .build("infected_witch"));

    public static final Supplier<EntityType<ThrownSculkVial>> THROWN_SCULK_VIAL =
            ENTITY_TYPES.register("thrown_sculk_vial", () -> EntityType.Builder.<ThrownSculkVial>of(ThrownSculkVial::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10)
                    .build("thrown_sculk_vial"));

    public static final Supplier<EntityType<ThrownSculkPotion>> THROWN_SCULK_POTION =
            ENTITY_TYPES.register("thrown_sculk_potion", () -> EntityType.Builder.<ThrownSculkPotion>of(ThrownSculkPotion::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f).clientTrackingRange(4).updateInterval(10)
                    .build("thrown_sculk_potion"));

    public static final Supplier<EntityType<InfectedBreeze>> INFECTED_BREEZE =
            ENTITY_TYPES.register("infected_breeze", () -> EntityType.Builder.of(InfectedBreeze::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.77f).clientTrackingRange(8)
                    .build("infected_breeze"));

    public static final Supplier<EntityType<InfectedEvoker>> INFECTED_EVOKER =
            ENTITY_TYPES.register("infected_evoker", () -> EntityType.Builder.of(InfectedEvoker::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).clientTrackingRange(8)
                    .build("infected_evoker"));

    public static final Supplier<EntityType<InfectedPolarBear>> INFECTED_POLAR_BEAR =
            ENTITY_TYPES.register("infected_polar_bear", () -> EntityType.Builder.of(InfectedPolarBear::new, MobCategory.MONSTER)
                    .sized(1.4f, 1.4f).clientTrackingRange(8)
                    .build("infected_polar_bear"));

    public static final Supplier<EntityType<InfectedPiglin>> INFECTED_PIGLIN =
            ENTITY_TYPES.register("infected_piglin", () -> EntityType.Builder.of(InfectedPiglin::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).clientTrackingRange(8)
                    .build("infected_piglin"));

    public static final Supplier<EntityType<InfectedPiglinBrute>> INFECTED_PIGLIN_BRUTE =
            ENTITY_TYPES.register("infected_piglin_brute", () -> EntityType.Builder.of(InfectedPiglinBrute::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.95f).clientTrackingRange(8)
                    .build("infected_piglin_brute"));
    public static final Supplier<EntityType<com.example.sculkecho.entity.SculkSoul>> SCULK_SOUL =
            ENTITY_TYPES.register("sculk_soul", () -> EntityType.Builder
                    .<com.example.sculkecho.entity.SculkSoul>of(
                            com.example.sculkecho.entity.SculkSoul::new, MobCategory.MISC)
                    .sized(0.4f, 0.4f).clientTrackingRange(8).updateInterval(10)
                    .build("sculk_soul"));

    public static void createAttributes(EntityAttributeCreationEvent event) {
        event.put(INFECTED_ZOMBIE.get(), Zombie.createAttributes().build());
        event.put(INFECTED_SKELETON.get(), Skeleton.createAttributes().build());
        event.put(INFECTED_PIG.get(), Pig.createAttributes()
                .add(Attributes.ATTACK_DAMAGE, 3.0)
                .build());
        event.put(INFECTED_CREEPER.get(), Creeper.createAttributes().build());
        event.put(SCULK_VILLAGER.get(), Mob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .build());
        event.put(INFECTED_COW.get(), Cow.createAttributes().build());
        event.put(INFECTED_SHEEP.get(), Sheep.createAttributes().build());
        event.put(INFECTED_SPIDER.get(), Spider.createAttributes().build());
        event.put(INFECTED_CHICKEN.get(), Chicken.createAttributes()
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .build());
        event.put(INFECTED_SHULKER.get(), Shulker.createAttributes().build());
        event.put(INFECTED_ENDERMAN.get(), EnderMan.createAttributes().build());
        event.put(INFECTED_ENDERMITE.get(), Endermite.createAttributes().build());
        event.put(INFECTED_WITCH.get(), Witch.createAttributes().build());
        event.put(INFECTED_BREEZE.get(), Breeze.createAttributes().build());
        event.put(INFECTED_EVOKER.get(), Evoker.createAttributes().build());
        event.put(INFECTED_POLAR_BEAR.get(), PolarBear.createAttributes().build());
        event.put(INFECTED_PIGLIN.get(), Piglin.createAttributes().build());
        event.put(INFECTED_PIGLIN_BRUTE.get(), PiglinBrute.createAttributes().build());
    }

    // Заражение сохраняет вид: нет заражённой формы — нет превращения
    public static EntityType<? extends LivingEntity> infectedTypeFor(Entity e) {
        if (e instanceof Zombie && !(e instanceof InfectedZombie)) return INFECTED_ZOMBIE.get();
        if (e instanceof Skeleton && !(e instanceof InfectedSkeleton)) return INFECTED_SKELETON.get();
        if (e instanceof Pig && !(e instanceof InfectedPig)) return INFECTED_PIG.get();
        if (e instanceof Creeper && !(e instanceof InfectedCreeper)) return INFECTED_CREEPER.get();
        if (e instanceof Cow && !(e instanceof InfectedCow)) return INFECTED_COW.get();
        if (e instanceof Sheep && !(e instanceof InfectedSheep)) return INFECTED_SHEEP.get();
        if (e instanceof Spider && !(e instanceof InfectedSpider)) return INFECTED_SPIDER.get();
        if (e instanceof Chicken && !(e instanceof InfectedChicken)) return INFECTED_CHICKEN.get();
        if (e instanceof Shulker && !(e instanceof InfectedShulker)) return INFECTED_SHULKER.get();
        if (e instanceof EnderMan && !(e instanceof InfectedEnderman)) return INFECTED_ENDERMAN.get();
        if (e instanceof Endermite && !(e instanceof InfectedEndermite)) return INFECTED_ENDERMITE.get();
        if (e instanceof Witch && !(e instanceof InfectedWitch)) return INFECTED_WITCH.get();
        if (e instanceof Breeze && !(e instanceof InfectedBreeze)) return INFECTED_BREEZE.get();
        if (e instanceof Evoker && !(e instanceof InfectedEvoker)) return INFECTED_EVOKER.get();
        if (e instanceof PolarBear && !(e instanceof InfectedPolarBear)) return INFECTED_POLAR_BEAR.get();
        if (e instanceof PiglinBrute && !(e instanceof InfectedPiglinBrute)) return INFECTED_PIGLIN_BRUTE.get();
        if (e instanceof Piglin && !(e instanceof InfectedPiglin)) return INFECTED_PIGLIN.get();
        return null;
    }

    public static boolean isInfected(Entity e) {
        return e instanceof InfectedZombie || e instanceof InfectedSkeleton
                || e instanceof InfectedPig || e instanceof InfectedCreeper
                        || e instanceof InfectedCow || e instanceof InfectedSheep
                        || e instanceof InfectedSpider || e instanceof InfectedChicken
                        || e instanceof InfectedShulker || e instanceof InfectedEnderman
                        || e instanceof InfectedEndermite
                        || e instanceof InfectedWitch
                        || e instanceof InfectedBreeze || e instanceof InfectedEvoker
                        || e instanceof InfectedPolarBear
                        || e instanceof InfectedPiglin || e instanceof InfectedPiglinBrute;
    }

    public static void infecTarget(ServerLevel level, LivingEntity target) {
        EntityType<? extends LivingEntity> type = infectedTypeFor(target);
        if (type == null) return;
        LivingEntity inf = type.create(level);
        if (inf == null) return;
        inf.moveTo(target.position());
        inf.setYRot(target.getYRot());
        inf.setHealth(target.getHealth());
        level.addFreshEntity(inf);
        level.sendParticles(ParticleTypes.SCULK_SOUL,
                target.getX(), target.getY() + 1, target.getZ(), 18, 0.4, 0.8, 0.4, 0.04);
        target.discard();
    }
}
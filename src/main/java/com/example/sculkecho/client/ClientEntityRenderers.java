package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.init.ModEntities;
import net.minecraft.client.renderer.entity.BreezeRenderer;
import net.minecraft.client.renderer.entity.EvokerRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = SculkEcho.MODID)
public class ClientEntityRenderers {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.ECHO_WISP.get(), EchoWispRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_ZOMBIE.get(), InfectedZombieRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_SKELETON.get(), InfectedSkeletonRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_PIG.get(), InfectedPigRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_CREEPER.get(), InfectedCreeperRenderer::new);
        event.registerEntityRenderer(ModEntities.SCULK_VILLAGER.get(), SculkVillagerRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_COW.get(), InfectedCowRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_SHEEP.get(), InfectedSheepRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_SPIDER.get(), InfectedSpiderRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_CHICKEN.get(), InfectedChickenRenderer::new);
        event.registerEntityRenderer(ModEntities.THROWN_SCULK_EGG.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_SHULKER.get(), InfectedShulkerRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_ENDERMAN.get(), InfectedEndermanRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_ENDERMITE.get(), InfectedEndermiteRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_WITCH.get(), InfectedWitchRenderer::new);
        event.registerEntityRenderer(ModEntities.THROWN_SCULK_VIAL.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.THROWN_SCULK_POTION.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_BREEZE.get(), InfectedBreezeRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_EVOKER.get(), InfectedEvokerRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_POLAR_BEAR.get(), InfectedPolarBearRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_PIGLIN.get(), InfectedPiglinRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_PIGLIN.get(), InfectedPiglinRenderer::new);
        event.registerEntityRenderer(ModEntities.INFECTED_PIGLIN_BRUTE.get(), InfectedPiglinBruteRenderer::new);
        event.registerEntityRenderer(ModEntities.SCULK_SOUL.get(), SculkSoulRenderer::new);
    }
}

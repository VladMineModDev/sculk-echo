package com.example.sculkecho.init;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.client.EchoParticleProvider;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, SculkEcho.MODID);

    public static final Supplier<SimpleParticleType> ECHO_POINT =
            PARTICLE_TYPES.register("echo_point", () -> new SimpleParticleType(true));

    @EventBusSubscriber(value = Dist.CLIENT, modid = SculkEcho.MODID)
    public static class ClientEvents {
        @SubscribeEvent
        public static void registerProviders(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ECHO_POINT.get(), EchoParticleProvider::new);
        }
    }
}

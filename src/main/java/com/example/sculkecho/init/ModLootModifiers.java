package com.example.sculkecho.init;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.loot.ReflectorBlueprintLootModifier;
import com.mojang.serialization.MapCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, SculkEcho.MODID);

    public static final Supplier<MapCodec<ReflectorBlueprintLootModifier>> BLUEPRINT =
            MODIFIERS.register("add_reflector_blueprint", () -> ReflectorBlueprintLootModifier.CODEC);

    public static void register(IEventBus bus) {
        MODIFIERS.register(bus);
    }
}
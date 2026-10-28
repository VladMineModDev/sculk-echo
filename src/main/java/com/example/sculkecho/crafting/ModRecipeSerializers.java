package com.example.sculkecho.crafting;

import com.example.sculkecho.SculkEcho;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, SculkEcho.MODID);

    public static final Supplier<RecipeSerializer<ReflectorRecipe>> REFLECTOR =
            SERIALIZERS.register("reflector", () -> new SimpleCraftingRecipeSerializer<>(ReflectorRecipe::new));

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }
}

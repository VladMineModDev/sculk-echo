package com.example.sculkecho.init;

import com.example.sculkecho.SculkEcho;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;

@EventBusSubscriber(modid = SculkEcho.MODID) // игровая шина по умолчанию — то, что нужно этому событию
public class ModBrewing {

    @SubscribeEvent
    public static void register(RegisterBrewingRecipesEvent event) {
        event.getBuilder().addRecipe(new IBrewingRecipe() {
            // Вход: любое взрывное зелье
            @Override
            public boolean isInput(ItemStack stack) {
                return stack.is(Items.SPLASH_POTION);
            }

            // Ингредиент: осколок эха
            @Override
            public boolean isIngredient(ItemStack stack) {
                return stack.is(Items.ECHO_SHARD);
            }

            // Выход: скалк-зелье
            @Override
            public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
                return new ItemStack(ModItems.SCULK_POTION.get());
            }
        });
    }
}
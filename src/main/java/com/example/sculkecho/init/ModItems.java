package com.example.sculkecho.init;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.item.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredRegister;
import com.example.sculkecho.item.SculkEggItem;

import java.util.function.Supplier;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.createItems(SculkEcho.MODID);

    public static final Supplier<Item> ECHO_STIMULATOR =
            ITEMS.register("echo_stimulator",
                    () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
    public static final Supplier<Item> SCULK_EGG =
            ITEMS.register("sculk_egg", () -> new SculkEggItem(new Item.Properties().stacksTo(16)));

    public static final Supplier<Item> SCULK_POTION =
            ITEMS.register("sculk_potion", () -> new SculkPotionItem(new Item.Properties().stacksTo(16)));

    public static final Supplier<Item> REFLECTOR_BLUEPRINT =
            ITEMS.register("reflector_blueprint", () -> new ReflectorBlueprintItem(new Item.Properties().stacksTo(1)));

    public static final Supplier<Item> REFLECTOR =
            ITEMS.register("reflector", () -> new ReflectorItem(new Item.Properties().stacksTo(1)));
}
package com.example.sculkecho.infection;

import com.example.sculkecho.SculkEcho;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

public final class SculkWard {

    public static Holder<Enchantment> holder(Level level) {
        Registry<Enchantment> reg = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        ResourceKey<Enchantment> key = ResourceKey.create(Registries.ENCHANTMENT,
                ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "sculk_ward"));
        return reg.getHolder(key).orElse(null);
    }

    // Надето ли зачарование на броне
    public static boolean hasWard(LivingEntity entity) {
        Holder<Enchantment> h = holder(entity.level());
        if (h == null) return false;
        return net.minecraft.world.item.enchantment.EnchantmentHelper.getEnchantmentLevel(h, entity) > 0;
    }

    // Зачарованная книга для сделки торговца
    public static ItemStack book(Level level) {
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
        Holder<Enchantment> h = holder(level);
        if (h == null) return stack;

        // В 1.21.1 создаём через Mutable
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutable.set(h, 1);
        stack.set(DataComponents.STORED_ENCHANTMENTS, mutable.toImmutable());
        return stack;
    }
}
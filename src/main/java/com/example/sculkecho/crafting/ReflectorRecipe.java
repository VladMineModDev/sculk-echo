package com.example.sculkecho.crafting;

import com.example.sculkecho.init.ModItems;
import com.example.sculkecho.item.ReflectorBlueprintItem;
import com.example.sculkecho.item.ReflectorItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ReflectorRecipe extends CustomRecipe {
    public ReflectorRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return !assemble(input, level.registryAccess()).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack stim = ItemStack.EMPTY;
        ItemStack bp = ItemStack.EMPTY;
        int redstone = 0, prismarine = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack s = input.getItem(i);
            if (s.isEmpty()) continue;
            if (s.is(ModItems.ECHO_STIMULATOR.get())) {
                if (!stim.isEmpty()) return ItemStack.EMPTY;
                stim = s;
            } else if (s.is(ModItems.REFLECTOR_BLUEPRINT.get())) {
                if (!bp.isEmpty()) return ItemStack.EMPTY;
                bp = s;
            } else if (s.is(Items.REDSTONE)) redstone++;
            else if (s.is(Items.PRISMARINE_SHARD)) prismarine++;
            else return ItemStack.EMPTY;
        }
        if (stim.isEmpty() || bp.isEmpty() || redstone != 2 || prismarine != 2) return ItemStack.EMPTY;
        int idx = ReflectorBlueprintItem.getColorIndex(bp);
        if (idx < 0) return ItemStack.EMPTY;
        return ReflectorItem.withColor(idx);
    }

    // Стимулятор переживает крафт, чертёж — нет
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        NonNullList<ItemStack> rem = NonNullList.withSize(input.size(), ItemStack.EMPTY);
        for (int i = 0; i < input.size(); i++) {
            ItemStack s = input.getItem(i);
            if (s.is(ModItems.ECHO_STIMULATOR.get())) rem.set(i, s.copy());
        }
        return rem;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 5;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.REFLECTOR.get();
    }
}
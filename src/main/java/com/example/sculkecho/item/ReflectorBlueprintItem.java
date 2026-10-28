package com.example.sculkecho.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ReflectorBlueprintItem extends Item {
    public ReflectorBlueprintItem(Properties p) { super(p); }

    public static ItemStack withColor(ItemStack base, int colorIndex) {
        ItemStack s = base.copy();
        s.set(DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY
                .update(t -> t.putInt("colorIndex", colorIndex)));
        s.set(DataComponents.CUSTOM_NAME, Component.translatable("item.sculkecho.reflector_blueprint.color",
                Component.translatable("color.sculkecho." + colorIndex)));
        return s;
    }

    public static int getColorIndex(ItemStack stack) {
        var cd = stack.get(DataComponents.CUSTOM_DATA);
        if (cd == null) return -1;
        return cd.copyTag().getInt("colorIndex");
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        int idx = getColorIndex(stack);
        if (idx >= 0) tip.add(Component.translatable("color.sculkecho." + idx));
    }
}
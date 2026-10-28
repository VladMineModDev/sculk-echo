package com.example.sculkecho.loot;

import com.example.sculkecho.init.ModItems;
import com.example.sculkecho.item.ReflectorBlueprintItem;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class ReflectorBlueprintLootModifier extends LootModifier {
    public static final MapCodec<ReflectorBlueprintLootModifier> CODEC =
            RecordCodecBuilder.mapCodec(i -> codecStart(i).apply(i, ReflectorBlueprintLootModifier::new));

    public ReflectorBlueprintLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextFloat() < 0.005f) { // 0.5%
            int idx = context.getRandom().nextInt(16);
            generatedLoot.add(ReflectorBlueprintItem.withColor(
                    new ItemStack(ModItems.REFLECTOR_BLUEPRINT.get()), idx));
        }
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}

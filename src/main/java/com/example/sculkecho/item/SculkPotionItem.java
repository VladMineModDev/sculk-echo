package com.example.sculkecho.item;

import com.example.sculkecho.entity.ThrownSculkPotion;
import com.example.sculkecho.init.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SculkPotionItem extends Item {
    public SculkPotionItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) return InteractionResultHolder.success(stack);

        level.playSound(null, player.blockPosition(), SoundEvents.SPLASH_POTION_THROW, SoundSource.PLAYERS, 0.5f, 0.8f);

        ThrownSculkPotion potion = new ThrownSculkPotion(level, player);
        potion.setItem(new ItemStack(ModItems.SCULK_POTION.get()));
        potion.shootFromRotation(player, player.getXRot(), player.getYRot(), -20f, 0.75f, 1f);
        level.addFreshEntity(potion);

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResultHolder.success(stack);
    }
}
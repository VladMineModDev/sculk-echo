package com.example.sculkecho.network;

import com.example.sculkecho.entity.SculkVillager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RerollTradesHandler {
    public static void handle(RerollTradesPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            var list = player.level().getEntitiesOfClass(SculkVillager.class,
                    player.getBoundingBox().inflate(8),
                    v -> v.getTradingPlayer() == player);
            if (list.isEmpty()) return;
            SculkVillager merchant = list.get(0);

            // Цена обновления — 1 осколок эха
            boolean paid = false;
            for (ItemStack s : player.getInventory().items) {
                if (s.is(Items.ECHO_SHARD)) { s.shrink(1); paid = true; break; }
            }
            if (!paid) {
                player.displayClientMessage(
                        Component.literal("Торговец хочет осколок эха за новый товар"), true);
                player.level().playSound(null, player.blockPosition(),
                        SoundEvents.VILLAGER_NO, SoundSource.NEUTRAL, 1f, 0.8f);
                return;
            }

            merchant.rerollTrades();
            player.level().playSound(null, merchant.blockPosition(),
                    SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.NEUTRAL, 1f, 1.2f);
            player.displayClientMessage(
                    Component.literal("Торговец шелестит скалком и показывает новый товар..."), true);
        });
    }
}
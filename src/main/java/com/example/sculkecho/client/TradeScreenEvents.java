package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.entity.SculkVillager;
import com.example.sculkecho.network.RerollTradesPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(value = Dist.CLIENT, modid = SculkEcho.MODID)
public class TradeScreenEvents {

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof MerchantScreen)) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        boolean nearMerchant = !mc.level.getEntitiesOfClass(SculkVillager.class,
                mc.player.getBoundingBox().inflate(8)).isEmpty();
        if (!nearMerchant) return;

        var screen = event.getScreen();
        event.addListener(Button.builder(
                        Component.translatable("gui.sculkecho.new_goods"),
                        btn -> PacketDistributor.sendToServer(new RerollTradesPayload()))
                .bounds(screen.width / 2 + 22, screen.height / 2 - 14, 108, 14)
                .build());
    }
}
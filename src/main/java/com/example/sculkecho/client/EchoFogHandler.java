package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ViewportEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = SculkEcho.MODID)
public class EchoFogHandler {

    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        // Ранний выход при отключённом тумане
        if (ClientEchoState.isFogDisabled()) return;
        if (ClientEchoState.isFogSuppressed()) return; // скалк ещё вгрызается — зрение обычное

        int stage = ClientEchoState.getStage();
        float i = ClientEchoState.getInterference();

        if (i > 0.01f) {
            float k = Math.min(0.85f, i * 0.85f);
            event.setRed(event.getRed() * (1 - k));
            event.setGreen(event.getGreen() * (1 - k) + 0.45f * k);
            event.setBlue(event.getBlue() * (1 - k) + 0.45f * k);
        }

        if (stage >= 2 && i < 0.05f) {
            event.setRed(0.0f);
            event.setGreen(0.0f);
            event.setBlue(0.0f);
        }
    }

    @SubscribeEvent
    public static void onRenderFog(ViewportEvent.RenderFog event) {
        // Ранний выход при отключённом тумане
        if (ClientEchoState.isFogDisabled()) return;
        if (ClientEchoState.isFogSuppressed()) return;

        // Убираем виньетку: не модифицируем ближнюю/дальнюю плоскости
        // Стандартный туман Minecraft рендерится нормально,
        // но цвет остаётся бирюзовым из onComputeFogColor
    }
}
package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

@EventBusSubscriber(modid = SculkEcho.MODID, value = Dist.CLIENT)
public class SymbiosisHudRenderer {

    // Рисуем только при активном "Принятии"
    @SubscribeEvent
    public static void onRenderGui(RenderGuiLayerEvent.Post event) {
        if (!ClientSymbiosisState.isEmbraced()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        GuiGraphics graphics = event.getGuiGraphics();
        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

        // Полоска Благоволия улья: справа от хотбара, под сердечками
        int barX = sw / 2 + 92;   // справа от хотбара
        int barY = sh - 55;
        int barW = 4;              // вертикальная полоска
        int barH = 40;

        int favor = ClientSymbiosisState.getFavor();

        // Фон
        graphics.fill(barX, barY, barX + barW, barY + barH, 0xAA000000);
        // Заполнение (снизу вверх)
        int filled = (int) (barH * favor / 100f);
        graphics.fill(barX, barY + barH - filled, barX + barW, barY + barH, 0xFF0ED0D0);
        // Бирюзовая кромка
        graphics.fill(barX - 1, barY - 1, barX + barW + 1, barY, 0xFF0ED0D0);
        graphics.fill(barX - 1, barY + barH, barX + barW + 1, barY + barH + 1, 0xFF0ED0D0);

        // Пипсы Симбиоза I–V рядом с полоской
        int pipX = barX + 8;
        for (int i = 0; i < 5; i++) {
            int py = barY + barH - 8 - i * 9;
            int color = i < ClientSymbiosisState.getLevel() ? 0xFF0ED0D0 : 0x6610222A;
            graphics.fill(pipX, py, pipX + 6, py + 6, color);
        }
    }
}
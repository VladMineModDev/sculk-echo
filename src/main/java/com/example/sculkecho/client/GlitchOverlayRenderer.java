package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;

import java.util.Random;

@EventBusSubscriber(modid = SculkEcho.MODID, value = Dist.CLIENT)
public class GlitchOverlayRenderer {

    @SubscribeEvent
    public static void onGuiRender(RenderGuiLayerEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        long now = mc.level.getGameTime();
        if (!ClientEchoState.hasGlitchingReflector(now)) return;

        GuiGraphics g = event.getGuiGraphics();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        long t = now - ClientEchoState.getReflectorInstallTick();
        float progress = Math.min(1f, t / 2400f);
        Random rnd = new Random(now / 2);

        // Смыкание зрения: тёмные края пульсируют и слабеют по мере стабилизации
        float pulse = 0.5f + 0.5f * (float) Math.sin(t * 0.15);
        int thick = (int) ((1f - progress) * pulse * Math.min(w, h) * 0.18f) + rnd.nextInt(3);
        int alpha = (int) (150 * (1f - progress)) << 24;
        if (thick > 0) {
            g.fill(0, 0, w, thick, alpha);
            g.fill(0, h - thick, w, h, alpha);
            g.fill(0, thick, thick, h - thick, alpha);
            g.fill(w - thick, thick, w, h - thick, alpha);
        }

        // Бирюзовая кромка скалка по внутренней границе смыкания
        if (thick > 2) {
            int tealEdge = (60 << 24) | 0x0ED0D0;
            g.fill(0, thick, w, thick + 1, tealEdge);
            g.fill(0, h - thick - 1, w, h - thick, tealEdge);
            g.fill(thick, thick, thick + 1, h - thick, tealEdge);
            g.fill(w - thick - 1, thick, w - thick, h - thick, tealEdge);
        }

        // Глитч-полосы в ЦВЕТАХ СКАЛКА: бирюза и тёмный скалк
        int teal = (90 << 24) | 0x0ED0D0;
        int dark = (90 << 24) | 0x0A5F63;
        for (int i = 0; i < 3; i++) {
            int y = rnd.nextInt(h);
            int bh = 1 + rnd.nextInt(3);
            int x = rnd.nextInt(Math.max(1, w / 2));
            int bw = rnd.nextInt(Math.max(1, w / 2));
            g.fill(x, y, x + bw, y + bh, (i % 2 == 0) ? teal : dark);
        }

        // Редкие белые вспышки-искры
        if (rnd.nextInt(8) == 0) {
            int x = rnd.nextInt(w), y = rnd.nextInt(h);
            g.fill(x, y, x + 2, y + 2, 0xCCFFFFFF);
        }
    }
}
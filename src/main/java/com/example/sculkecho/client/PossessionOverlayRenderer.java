package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = SculkEcho.MODID)
public class PossessionOverlayRenderer {

    private static final int SCULK_BLACK = 0xFF010809;
    private static final int VEIN_TEAL = 0xFF0E3A3E;
    private static final int SPARK_TEAL = 0xFF19C8C8;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (!ClientEchoState.isPossessionSequenceActive()) return;
        float p = Math.min(1f, ClientEchoState.getPossessionProgress());
        float pe = p * p * (3f - 2f * p); // плавный разгон и торможение
        GuiGraphics g = event.getGuiGraphics();
        Minecraft mc = Minecraft.getInstance();
        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        // секвенция завершена — полная чернота, её подхватит туман
        if (p >= 1f) {
            g.fill(0, 0, w, h, 0xFF000000);
            return;
        }

        // мягкое общее затемнение по мере роста скалка
        g.fill(0, 0, w, h, ((int) (pe * pe * 120)) << 24);

        double t = System.currentTimeMillis() / 1000.0;

        // Верх и низ: волнистые фронты скалка
        int cols = 96;
        for (int i = 0; i < cols; i++) {
            int x0 = i * w / cols, x1 = (i + 1) * w / cols + 1;
            int lenT = (int) (h * 0.66 * pe * shape(i, 1, t));
            int lenB = (int) (h * 0.66 * pe * shape(i, 2, t));
            if (lenT > 0) {
                g.fill(x0, 0, x1, lenT, SCULK_BLACK);
                g.fill(x0, lenT, x1, lenT + 1, VEIN_TEAL);
                if (hash(i, 3, t) < 0.10) g.fill(x0, lenT + 1, x1, lenT + 3, SPARK_TEAL);
            }
            if (lenB > 0) {
                g.fill(x0, h - lenB, x1, h, SCULK_BLACK);
                g.fill(x0, h - lenB - 1, x1, h - lenB, VEIN_TEAL);
                if (hash(i, 4, t) < 0.10) g.fill(x0, h - lenB - 3, x1, h - lenB - 1, SPARK_TEAL);
            }
        }

        // Лево и право
        int rows = 48;
        for (int i = 0; i < rows; i++) {
            int y0 = i * h / rows, y1 = (i + 1) * h / rows + 1;
            int lenL = (int) (w * 0.66 * pe * shape(i, 5, t));
            int lenR = (int) (w * 0.66 * pe * shape(i, 6, t));
            if (lenL > 0) {
                g.fill(0, y0, lenL, y1, SCULK_BLACK);
                g.fill(lenL, y0, lenL + 1, y1, VEIN_TEAL);
            }
            if (lenR > 0) {
                g.fill(w - lenR, y0, w, y1, SCULK_BLACK);
                g.fill(w - lenR - 1, y0, w - lenR, y1, VEIN_TEAL);
            }
        }
    }

    // Органическая кромка: сумма синусов вместо рваного рандома
    private static float shape(int i, int seed, double t) {
        double v = Math.sin(i * 0.31 + t * 1.9 + seed * 12.9898)
                + 0.6 * Math.sin(i * 0.17 - t * 1.3 + seed * 78.233)
                + 0.4 * Math.sin(i * 0.053 + t * 0.7 + seed * 37.719);
        v /= 2.0; // [-1..1]
        return (float) (0.8 + 0.4 * (v * 0.5 + 0.5)); // 0.8..1.2
    }

    // Редкие мерцающие искры вдоль кромки
    private static float hash(int i, int seed, double t) {
        long step = (long) (t * 8);
        long x = i * 374761393L + seed * 668265263L + step * 1442695040888963407L;
        x = (x ^ (x >> 13)) * 1274126177L;
        x = (x ^ (x >> 16)) & 0x7FFFFFFF;
        return x / (float) 0x7FFFFFFF;
    }
}
package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = SculkEcho.MODID)
public class EchoHudRenderer {

    private static final int TEAL = 0xFF0ED0D0;
    private static final int TEAL_BRIGHT = 0xFF4FE8E0;
    private static final int TEAL_DIM = 0xFF0A5F63;
    private static final int BAR_BG = 0xFF06222A;
    private static final int PANEL_BG = 0xAA010809;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (ClientEchoState.isPossessionSequenceActive()) return;
        int infection = ClientEchoState.getInfection();
        if (infection <= 0) return;

        GuiGraphics g = event.getGuiGraphics();
        int x = 6, y = 6;

        // Панель
        g.fill(x - 3, y - 3, x + 93, y + 43, PANEL_BG);

        // Надпись + полоса инфекции
        g.drawString(mc.font, "INFECTION " + infection, x, y, TEAL, false);
        y += 10;
        g.fill(x, y, x + 88, y + 5, BAR_BG);
        int w = Math.min(88, Math.max(0, (int) (88 * infection / 100f)));
        if (w > 0) {
            g.fill(x, y, x + w, y + 5, TEAL);
            g.fill(x + w - 1, y, x + w, y + 5, 0xFFE6FFFB); // яркая кромка
        }
        y += 8;

        // Стадии: 6 пипсов (0..5)
        int stage = ClientEchoState.getStage();
        for (int i = 0; i <= 5; i++) {
            int px = x + i * 10;
            boolean on = i <= stage;
            g.fill(px, y, px + 7, y + 7, on ? (i == 5 ? TEAL_BRIGHT : TEAL) : BAR_BG);
        }
        y += 11;

        // Echo Shot: только на стадии 5
        if (stage >= 5) {
            int cd = ClientEchoState.getEchoShotCooldown();
            if (cd > 0) {
                g.drawString(mc.font, "ECHO SHOT", x, y, TEAL_DIM, false);
                g.fill(x + 60, y - 1, x + 88, y + 6, BAR_BG);
                g.fill(x + 60, y - 1, x + 60 + (int) (28 * cd / 40f), y + 6, TEAL_DIM);
            } else {
                g.drawString(mc.font, "ECHO SHOT READY", x, y, TEAL, false);
            }
        }

    }
}
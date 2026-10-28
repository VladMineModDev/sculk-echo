package com.example.sculkecho.client;

import com.example.sculkecho.network.EchoPulsePayload;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class EchoPulseHandler {
    public static void handle(EchoPulsePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientEchoState.setStage(payload.stage());
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;

            System.out.println("[SCULK ECHO DEBUG] Client: Pulse received! Starting scan...");
            ClientEchoState.startPulse(mc.level, mc.player.position(), payload.radius(), payload.colorMask());
            System.out.println("[SCULK ECHO DEBUG] Client: Scan complete. Points found: " + ClientEchoState.getPoints().size());
        });
    }
}
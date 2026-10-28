package com.example.sculkecho.client;

import com.example.sculkecho.network.EchoPointsPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class EchoPointsHandler {
    public static void handle(EchoPointsPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientEchoState.setStage(payload.stage());
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.level == null) return;
            ClientEchoState.startPulse(mc.level, mc.player.position(), payload.radius(), 0);
        });
    }
}

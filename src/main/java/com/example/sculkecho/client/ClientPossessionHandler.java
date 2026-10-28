package com.example.sculkecho.client;

import com.example.sculkecho.network.PossessionPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientPossessionHandler {
    public static void handle(PossessionPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (payload.possessed()) {
                // 1. Сначала ставим стадию (чтобы рендер и туман знали)
                ClientEchoState.setStage(payload.stage());
                // 2. Запускаем секвенцию скалка (туман будет подавлен, пока она идёт)
                ClientEchoState.startPossessionSequence();
                // 3. Лёгкий бирюзовый глитч на момент удара
                ClientEchoState.addInterference(0.35f);
            }
        });
    }
}
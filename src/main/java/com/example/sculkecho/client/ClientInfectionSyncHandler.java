package com.example.sculkecho.client;

import com.example.sculkecho.network.InfectionSyncPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientInfectionSyncHandler {
    public static void handle(InfectionSyncPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientEchoState.setStage(payload.stage());
            ClientEchoState.setInfection(payload.infection());
        });
    }
}

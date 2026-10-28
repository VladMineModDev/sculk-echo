package com.example.sculkecho.network;

import com.example.sculkecho.client.ClientEchoState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ReflectorSyncHandler {
    public static void handle(ReflectorSyncPayload p, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientEchoState.setReflector(p.colorIndex(), p.installStartTick()));
    }
}
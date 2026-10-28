package com.example.sculkecho.network;

import com.example.sculkecho.client.ClientSymbiosisState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SymbiosisSyncHandler {
    public static void handle(SymbiosisSyncPayload p, IPayloadContext ctx) {
        ctx.enqueueWork(() -> ClientSymbiosisState.set(p.embraced(), p.favor(), p.level()));
    }
}
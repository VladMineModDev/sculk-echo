package com.example.sculkecho.client;

import com.example.sculkecho.infection.InfectionData;
import com.example.sculkecho.infection.InfectionHelper;
import com.example.sculkecho.init.ModAttachments;
import com.example.sculkecho.network.EchoPulsePayload;
import com.example.sculkecho.network.EchoRequestPayload;
import com.example.sculkecho.service.EchoService;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ServerEchoHandler {
    public static void handle(EchoRequestPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer player)) return;
            InfectionData data = InfectionHelper.getData(player);
            if (data.cooldown() > 0){
                if (data.infection() <= 0) return; // без заражения эхо не отвечает
                System.out.println("[SCULK ECHO DEBUG] Server: Request rejected (cooldown " + data.cooldown() + ")");
                return;
            }
            InfectionHelper.setData(player, data.withCooldown(10));

            int stage = data.stage();
            boolean stimulated = player.getData(ModAttachments.STIMULATED.get());
            float radius = stimulated ? 24f : switch (stage) {
                case 0 -> 3f; case 1 -> 5f; case 2 -> 8f;
                case 3 -> 11f; case 4 -> 15f; default -> 20f;
            };
            int mask = player.getData(ModAttachments.ECHO_COLORS.get());
            System.out.println("[SCULK ECHO DEBUG] Server: Sending pulse! Stage=" + stage + ", Radius=" + radius + ", Mask=" + mask);

            PacketDistributor.sendToPlayer(player, new EchoPulsePayload(stage, radius, mask));
        });
    }
}
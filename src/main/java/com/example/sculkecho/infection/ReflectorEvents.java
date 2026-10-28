package com.example.sculkecho.infection;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.init.ModAttachments;
import com.example.sculkecho.network.ReflectorSyncPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = SculkEcho.MODID)
public class ReflectorEvents {
    @SubscribeEvent
    public static void onPlayerTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        PlayerReflectorData d = sp.getData(ModAttachments.REFLECTOR.get());
        long now = sp.level().getGameTime();
        if (d.isGlitching(now) && d.isStabilized(now)) {
            PlayerReflectorData stable = d.stabilize();
            sp.setData(ModAttachments.REFLECTOR.get(), stable);
            PacketDistributor.sendToPlayer(sp, new ReflectorSyncPayload(stable.colorIndex(), stable.installStartTick()));
            sp.displayClientMessage(Component.translatable("message.sculkecho.stabilized"), false);
        }
    }
}
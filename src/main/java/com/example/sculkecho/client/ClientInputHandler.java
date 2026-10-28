package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.network.EchoRequestPayload;
import com.example.sculkecho.network.EchoShotPayload;
import com.example.sculkecho.network.SculkSoulPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.example.sculkecho.client.ModKeys.SCULK_SOUL;

@EventBusSubscriber(
        value = Dist.CLIENT,
        modid = SculkEcho.MODID
)
public class ClientInputHandler {
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        ClientEchoState.tick();
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null
                && ClientEchoState.hasGlitchingReflector(minecraft.level.getGameTime())
                && minecraft.level.getRandom().nextFloat() < 0.3f) {
            return; // сонар дал осечку во время глитча
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }

        // consumeClick() срабатывает ровно один раз на нажатие и не спамит, если кнопку держат
        while (ModKeys.ECHO_PULSE.consumeClick()) {
            if (ClientEchoState.getInfection() > 0 && ClientEchoState.tryPulse()) {
                PacketDistributor.sendToServer(new EchoRequestPayload());
            }
        }

        while (ModKeys.ECHO_SHOT.consumeClick()) {
            if (ClientEchoState.getStage() >= 5 && ClientEchoState.tryEchoShot()) {
                PacketDistributor.sendToServer(new EchoShotPayload());
            }
        }
        while (SCULK_SOUL.consumeClick()) {
            PacketDistributor.sendToServer(new SculkSoulPayload());
        }

    }
}
package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.init.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = SculkEcho.MODID, value = Dist.CLIENT)
public class ModItemColors {
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        // Layer 1 (glow) tinted with teal color
        event.register((stack, tintIndex) -> tintIndex == 1 ? 0x0ED0D0 : 0xFFFFFF,
                ModItems.ECHO_STIMULATOR.get());
    }
}
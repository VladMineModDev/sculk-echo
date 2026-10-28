package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import com.mojang.blaze3d.platform.InputConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(value = Dist.CLIENT, modid = SculkEcho.MODID)
public class ModKeys {
    public static final KeyMapping ECHO_PULSE = new KeyMapping("key.sculkecho.pulse", GLFW.GLFW_KEY_V, "key.categories.gameplay");
    public static final KeyMapping ECHO_SHOT = new KeyMapping(
            "key.sculkecho.echo_shot",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_R,
            "key.categories.sculkecho"
    );
    public static final KeyMapping SCULK_SOUL = new KeyMapping(
            "key.sculkecho.sculk_soul", GLFW.GLFW_KEY_G, "key.categories.sculkecho"
    );
    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(ECHO_PULSE);
        event.register(ECHO_SHOT);
    }
}
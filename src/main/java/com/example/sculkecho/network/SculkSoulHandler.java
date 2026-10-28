package com.example.sculkecho.network;

import com.example.sculkecho.entity.SculkSoul;
import com.example.sculkecho.infection.SymbiosisEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class SculkSoulHandler {
    public static void handle(SculkSoulPayload p, IPayloadContext ctx) {
        ServerPlayer sp = (ServerPlayer) ctx.player();
        if (sp == null) return;
        ctx.enqueueWork(() -> {
            if (!SymbiosisEvents.spendFavor(sp, 15)) {
                sp.displayClientMessage(Component.translatable("message.sculkecho.not_enough_favor"), true);
                return;
            }
            SculkSoul soul = new SculkSoul(sp.level(), sp);
            Vec3 eye = sp.getEyePosition();
            Vec3 look = sp.getLookAngle();
            soul.moveTo(eye.x, eye.y, eye.z, sp.getYRot(), sp.getXRot());
            soul.shoot(look.x, look.y, look.z, 0.6f, 0.5f);
            sp.level().addFreshEntity(soul);
        });
    }
}
package com.example.sculkecho.network;

import com.example.sculkecho.SculkEcho;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record EchoPulsePayload(int stage, float radius, int colorMask) implements CustomPacketPayload {
    public static final Type<EchoPulsePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "echo_pulse"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EchoPulsePayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, EchoPulsePayload::stage,
                    ByteBufCodecs.FLOAT, EchoPulsePayload::radius,
                    ByteBufCodecs.VAR_INT, EchoPulsePayload::colorMask,
                    EchoPulsePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package com.example.sculkecho.network;

import com.example.sculkecho.SculkEcho;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record EchoRequestPayload() implements CustomPacketPayload {
    public static final Type<EchoRequestPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "echo_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, EchoRequestPayload> STREAM_CODEC = StreamCodec.unit(new EchoRequestPayload());
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
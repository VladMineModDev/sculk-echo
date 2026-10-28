package com.example.sculkecho.network;

import com.example.sculkecho.SculkEcho;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record EchoShotPayload() implements CustomPacketPayload {
    public static final Type<EchoShotPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "echo_shot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EchoShotPayload> STREAM_CODEC =
            StreamCodec.unit(new EchoShotPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
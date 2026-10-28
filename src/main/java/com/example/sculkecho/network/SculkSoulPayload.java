package com.example.sculkecho.network;

import com.example.sculkecho.SculkEcho;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SculkSoulPayload() implements CustomPacketPayload {
    public static final Type<SculkSoulPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "sculk_soul"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SculkSoulPayload> STREAM_CODEC =
            StreamCodec.unit(new SculkSoulPayload());
    @Override public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
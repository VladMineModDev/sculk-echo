package com.example.sculkecho.network;

import com.example.sculkecho.SculkEcho;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PossessionPayload(boolean possessed, int stage) implements CustomPacketPayload {
    public static final Type<PossessionPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "possession"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PossessionPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, PossessionPayload::possessed,
                    ByteBufCodecs.VAR_INT, PossessionPayload::stage,
                    PossessionPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}

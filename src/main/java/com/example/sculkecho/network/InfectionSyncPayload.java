package com.example.sculkecho.network;

import com.example.sculkecho.SculkEcho;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record InfectionSyncPayload(int stage, int infection) implements CustomPacketPayload {
    public static final Type<InfectionSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "infection_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, InfectionSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, InfectionSyncPayload::stage,
                    ByteBufCodecs.VAR_INT, InfectionSyncPayload::infection,
                    InfectionSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
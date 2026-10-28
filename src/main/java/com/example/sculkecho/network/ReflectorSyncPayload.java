package com.example.sculkecho.network;

import com.example.sculkecho.SculkEcho;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ReflectorSyncPayload(int colorIndex, long installStartTick) implements CustomPacketPayload {
    public static final Type<ReflectorSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "reflector_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ReflectorSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, ReflectorSyncPayload::colorIndex,
                    ByteBufCodecs.VAR_LONG, ReflectorSyncPayload::installStartTick,
                    ReflectorSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
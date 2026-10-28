package com.example.sculkecho.network;

import com.example.sculkecho.SculkEcho;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SymbiosisSyncPayload(boolean embraced, int favor, int level) implements CustomPacketPayload {
    public static final Type<SymbiosisSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "symbiosis_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SymbiosisSyncPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL, SymbiosisSyncPayload::embraced,
                    ByteBufCodecs.INT, SymbiosisSyncPayload::favor,
                    ByteBufCodecs.INT, SymbiosisSyncPayload::level,
                    SymbiosisSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
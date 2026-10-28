package com.example.sculkecho.network;

import com.example.sculkecho.SculkEcho;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public record EchoPointsPayload(int stage, float radius, List<Vec3> points) implements CustomPacketPayload {

    public EchoPointsPayload {
        points = List.copyOf(points);
    }

    public static final Type<EchoPointsPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "echo_points"));

    public static final StreamCodec<RegistryFriendlyByteBuf, EchoPointsPayload> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public EchoPointsPayload decode(RegistryFriendlyByteBuf buf) {
                    int stage = buf.readVarInt();
                    float radius = buf.readFloat();
                    int count = buf.readVarInt();
                    List<Vec3> pts = new ArrayList<>(count);
                    for (int i = 0; i < count; i++) {
                        pts.add(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
                    }
                    return new EchoPointsPayload(stage, radius, pts);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, EchoPointsPayload payload) {
                    buf.writeVarInt(payload.stage());
                    buf.writeFloat(payload.radius());
                    buf.writeVarInt(payload.points().size());
                    for (Vec3 v : payload.points()) {
                        buf.writeDouble(v.x);
                        buf.writeDouble(v.y);
                        buf.writeDouble(v.z);
                    }
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
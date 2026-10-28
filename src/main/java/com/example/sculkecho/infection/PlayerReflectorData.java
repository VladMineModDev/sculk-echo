package com.example.sculkecho.infection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PlayerReflectorData(int colorIndex, long installStartTick) {
    public static final PlayerReflectorData EMPTY = new PlayerReflectorData(-1, -1L);

    public static final Codec<PlayerReflectorData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.INT.optionalFieldOf("colorIndex", -1).forGetter(PlayerReflectorData::colorIndex),
            Codec.LONG.optionalFieldOf("installStartTick", -1L).forGetter(PlayerReflectorData::installStartTick)
    ).apply(i, PlayerReflectorData::new));

    public boolean isInstalled() { return colorIndex >= 0; }

    public boolean isStabilized(long nowTick) {
        return installStartTick > 0 && (nowTick - installStartTick) >= 2400L; // 2 минуты
    }

    public boolean isGlitching(long nowTick) {
        return installStartTick > 0 && (nowTick - installStartTick) < 2400L;
    }

    public PlayerReflectorData install(int colorIdx, long now) {
        return new PlayerReflectorData(colorIdx, now);
    }

    public PlayerReflectorData stabilize() {
        return new PlayerReflectorData(colorIndex, -1L);
    }
}
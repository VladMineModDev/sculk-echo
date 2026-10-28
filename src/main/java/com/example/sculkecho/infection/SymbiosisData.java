package com.example.sculkecho.infection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SymbiosisData(boolean embraced, int favor, int conversions) {
    public static final SymbiosisData EMPTY = new SymbiosisData(false, 0, 0);

    public static final Codec<SymbiosisData> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.BOOL.optionalFieldOf("embraced", false).forGetter(SymbiosisData::embraced),
            Codec.INT.optionalFieldOf("favor", 0).forGetter(SymbiosisData::favor),
            Codec.INT.optionalFieldOf("conversions", 0).forGetter(SymbiosisData::conversions)
    ).apply(i, SymbiosisData::new));

    public static int levelFor(int conversions) {
        if (conversions >= 75) return 5;
        if (conversions >= 50) return 4;
        if (conversions >= 30) return 3;
        if (conversions >= 15) return 2;
        if (conversions >= 5) return 1;
        return 0;
    }

    public int level() { return levelFor(conversions); }

    public SymbiosisData withEmbrace() { return new SymbiosisData(true, favor, conversions); }

    public SymbiosisData addFavor(int v) {
        return new SymbiosisData(embraced, Math.max(0, Math.min(100, favor + v)), conversions);
    }

    public SymbiosisData addConversion(int favorReward) {
        return new SymbiosisData(embraced, Math.max(0, Math.min(100, favor + favorReward)), conversions + 1);
    }
}
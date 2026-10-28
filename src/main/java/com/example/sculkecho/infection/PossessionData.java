package com.example.sculkecho.infection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PossessionData(boolean possessed, int wispDelay) {
    public static final PossessionData DEFAULT = new PossessionData(false, 0);

    public static final Codec<PossessionData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.fieldOf("possessed").forGetter(PossessionData::possessed),
                    Codec.INT.fieldOf("wispDelay").forGetter(PossessionData::wispDelay)
            ).apply(instance, PossessionData::new)
    );
}
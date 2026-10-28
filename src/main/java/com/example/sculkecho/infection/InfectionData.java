package com.example.sculkecho.infection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record InfectionData(int infection, int cooldown, int stage) {
    public static final InfectionData DEFAULT = new InfectionData(0, 0, 0);
    public static final Codec<InfectionData> CODEC = RecordCodecBuilder.create(i -> i.group(Codec.INT.fieldOf("infection").forGetter(InfectionData::infection), Codec.INT.fieldOf("cooldown").forGetter(InfectionData::cooldown), Codec.INT.fieldOf("stage").forGetter(InfectionData::stage)).apply(i, InfectionData::new));
    public InfectionData withInfection(int v) { return new InfectionData(v, cooldown, stage); }
    public InfectionData withCooldown(int v) { return new InfectionData(infection, v, stage); }
    public InfectionData withStage(int v) { return new InfectionData(infection, cooldown, v); }
    public static int calculateStage(int infection) {
        if (infection >= 100) return 5;   // порог превращения / стимулятор
        if (infection >= 80)  return 4;   // глубокий резонанс
        if (infection >= 50)  return 3;   // эхо-зависимость
        if (infection >= 20)  return 2;   // затуманенность
        if (infection >= 5)   return 1;   // носитель с помехами
        return 0;
    }
}
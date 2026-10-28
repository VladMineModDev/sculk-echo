package com.example.sculkecho.infection;

import com.example.sculkecho.init.ModAttachments;
import com.example.sculkecho.network.InfectionSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import static com.example.sculkecho.client.ClientEchoState.setStage;

public final class InfectionHelper {
    public static InfectionData getData(ServerPlayer p) {
        return p.getData(ModAttachments.INFECTION.get());
    }
    
    public static void setData(ServerPlayer p, InfectionData d) {
        InfectionData old = getData(p);
        p.setData(ModAttachments.INFECTION.get(), d);
        
        // Синхронизируем с клиентом, если изменилась стадия или уровень инфекции
        if (old.stage() != d.stage() || old.infection() != d.infection()) {
            PacketDistributor.sendToPlayer(
                p, 
                new InfectionSyncPayload(d.infection(), d.stage())
            );
        }
    }
    
    public static int getInfection(ServerPlayer p) {
        return getData(p).infection();
    }
    
    public static int getStage(ServerPlayer p) {
        return getData(p).stage();
    }
    
    public static void setInfection(ServerPlayer p, int v) {
        int i = Math.max(0, Math.min(100, v));
        InfectionData d = getData(p);
        setData(p, new InfectionData(i, d.cooldown(), InfectionData.calculateStage(i)));
    }

    public static void addInfection(ServerPlayer player, int amount) {
        // Скалк-защита: заражение не растёт
        if (amount > 0 && SculkWard.hasWard(player)) return;

        InfectionData cur = getData(player);
        int newVal = Math.max(0, Math.min(100, cur.infection() + amount));
        if (newVal == cur.infection()) return;

        setInfection(player, newVal);

        InfectionSyncPayload payload = new InfectionSyncPayload(newVal / 20, newVal);
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, payload);
    }
}

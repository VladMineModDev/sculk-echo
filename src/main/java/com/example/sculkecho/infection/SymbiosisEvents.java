package com.example.sculkecho.infection;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.init.ModAttachments;
import com.example.sculkecho.network.SymbiosisSyncPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = SculkEcho.MODID)
public class SymbiosisEvents {

    @SubscribeEvent
    public static void onPlayerTick(EntityTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer sp)) return;
        SymbiosisData d = sp.getData(ModAttachments.SYMBIOSIS.get());
        boolean changed = false;

        // Болезнь стала частью игрока: Принятие при инфекции 100
        if (!d.embraced() && InfectionHelper.getData(sp).infection() >= 100) {
            d = d.withEmbrace();
            changed = true;
            sp.displayClientMessage(Component.translatable("message.sculkecho.embraced"), false);
            sp.level().playSound(null, sp.blockPosition(),
                    SoundEvents.SCULK_CATALYST_BLOOM, SoundSource.PLAYERS, 1.2f, 0.8f);
        }

        // Благоволение улья: +1/сек, пока стоишь на скалке
        if (d.embraced() && sp.tickCount % 20 == 0 && d.favor() < 100) {
            BlockState below = sp.level().getBlockState(sp.blockPosition().below());
            if (below.is(Blocks.SCULK) || below.is(Blocks.SCULK_VEIN) || below.is(Blocks.SCULK_CATALYST)) {
                d = d.addFavor(1);
                changed = true;
            }
        }

        if (changed) {
            sp.setData(ModAttachments.SYMBIOSIS.get(), d);
            PacketDistributor.sendToPlayer(sp,
                    new SymbiosisSyncPayload(d.embraced(), d.favor(), d.level()));
        }
    }

    // Начисление за обращение (вызывать из EpidemicEvents и Скалк-души)
    public static void creditConversion(ServerPlayer sp) {
        SymbiosisData d = sp.getData(ModAttachments.SYMBIOSIS.get());
        if (!d.embraced()) return;
        int before = d.level();
        d = d.addConversion(5);
        sp.setData(ModAttachments.SYMBIOSIS.get(), d);
        PacketDistributor.sendToPlayer(sp,
                new SymbiosisSyncPayload(d.embraced(), d.favor(), d.level()));
        if (d.level() > before) {
            sp.displayClientMessage(
                    Component.translatable("message.sculkecho.symbiosis_up", d.level()), false);
        }
    }

    // Трата Благоволия (вызывать из способностей); false = не хватает
    public static boolean spendFavor(ServerPlayer sp, int cost) {
        SymbiosisData d = sp.getData(ModAttachments.SYMBIOSIS.get());
        if (!d.embraced() || d.favor() < cost) return false;
        d = d.addFavor(-cost);
        sp.setData(ModAttachments.SYMBIOSIS.get(), d);
        PacketDistributor.sendToPlayer(sp,
                new SymbiosisSyncPayload(d.embraced(), d.favor(), d.level()));
        return true;
    }
}
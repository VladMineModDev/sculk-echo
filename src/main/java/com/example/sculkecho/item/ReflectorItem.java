package com.example.sculkecho.item;

import com.example.sculkecho.infection.PlayerReflectorData;
import com.example.sculkecho.init.ModAttachments;
import com.example.sculkecho.init.ModItems;
import com.example.sculkecho.network.ReflectorSyncPayload;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public class ReflectorItem extends Item {
    public ReflectorItem(Properties p) { super(p); }

    public static ItemStack withColor(int colorIndex) {
        ItemStack s = new ItemStack(ModItems.REFLECTOR.get());
        s.set(DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY
                .update(t -> t.putInt("colorIndex", colorIndex)));
        s.set(DataComponents.CUSTOM_NAME, Component.translatable("item.sculkecho.reflector.color",
                Component.translatable("color.sculkecho." + colorIndex)));
        return s;
    }

    public static int getColorIndex(ItemStack stack) {
        var cd = stack.get(DataComponents.CUSTOM_DATA);
        if (cd == null) return -1;
        return cd.copyTag().getInt("colorIndex");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack reflector = player.getItemInHand(hand);

        // Стимулятор достаточно просто иметь в инвентаре (или в оффхенде)
        boolean hasStim = player.getOffhandItem().is(ModItems.ECHO_STIMULATOR.get())
                || player.getInventory().items.stream().anyMatch(s -> s.is(ModItems.ECHO_STIMULATOR.get()));
        if (!hasStim) {
            if (!level.isClientSide) player.displayClientMessage(
                    Component.translatable("message.sculkecho.need_stimulator"), true);
            return InteractionResultHolder.fail(reflector);
        }

        int colorIdx = getColorIndex(reflector);
        if (colorIdx < 0 || level.isClientSide) return InteractionResultHolder.success(reflector);

        if (player instanceof ServerPlayer sp) {
            PlayerReflectorData cur = sp.getData(ModAttachments.REFLECTOR.get());
            long now = sp.level().getGameTime();
            sp.setData(ModAttachments.REFLECTOR.get(), cur.install(colorIdx, now));
            PacketDistributor.sendToPlayer(sp, new ReflectorSyncPayload(colorIdx, now));
            sp.displayClientMessage(Component.translatable("message.sculkecho.installing",
                    Component.translatable("color.sculkecho." + colorIdx)), true);
        }

        if (!player.getAbilities().instabuild) {
            reflector.shrink(1);
        }
        return InteractionResultHolder.success(reflector);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> tip, TooltipFlag flag) {
        int idx = getColorIndex(stack);
        if (idx >= 0) tip.add(Component.translatable("color.sculkecho." + idx));
        tip.add(Component.translatable("item.sculkecho.reflector.hint"));
    }
}
package com.example.sculkecho.entity;

import com.example.sculkecho.infection.SculkWard;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import com.example.sculkecho.item.ReflectorBlueprintItem;
import com.example.sculkecho.init.ModItems;
import java.util.ArrayList;
import java.util.List;

public class SculkVillager extends AbstractVillager {

    public SculkVillager(EntityType<? extends AbstractVillager> type, Level level) {
        super(type, level);
    }

    @Override
    public MerchantOffers getOffers() {
        if (this.offers == null) {
            this.offers = buildRandomOffers();
        }
        return this.offers;
    }

    // Ядро + случайный ассортимент
    private MerchantOffers buildRandomOffers() {
        MerchantOffers offers = new MerchantOffers();
        offers.add(new MerchantOffer(new ItemCost(Items.ECHO_SHARD, 3), new ItemStack(Items.EMERALD), 12, 5, 1f));
        offers.add(new MerchantOffer(new ItemCost(Items.EMERALD, 2), new ItemStack(Items.ECHO_SHARD), 8, 5, 1f));

        List<MerchantOffer> pool = new ArrayList<>();
        pool.add(new MerchantOffer(new ItemCost(Items.ECHO_SHARD, 6 + random.nextInt(4)), new ItemStack(Items.DIAMOND), 4, 10, 1f));
        pool.add(new MerchantOffer(new ItemCost(Items.ECHO_SHARD, 4 + random.nextInt(3)), new ItemStack(Items.POTION), 6, 3, 1f));
        pool.add(new MerchantOffer(new ItemCost(Items.ECHO_SHARD, 5 + random.nextInt(3)), new ItemStack(Items.GOLDEN_APPLE), 3, 8, 1f));
        pool.add(new MerchantOffer(new ItemCost(Items.ECHO_SHARD, 3 + random.nextInt(2)), new ItemStack(Blocks.SCULK_CATALYST.asItem()), 2, 12, 1f));
        pool.add(new MerchantOffer(new ItemCost(Items.ECHO_SHARD, 8 + random.nextInt(5)), new ItemStack(Items.EXPERIENCE_BOTTLE, 4), 5, 6, 1f));
        pool.add(new MerchantOffer(new ItemCost(Items.ECHO_SHARD, 5 + random.nextInt(4)), new ItemStack(Items.ENDER_PEARL, 2), 4, 6, 1f));
        pool.add(new MerchantOffer(new ItemCost(Items.EMERALD, 1), new ItemStack(Blocks.SCULK.asItem(), 2), 10, 2, 1f));
        pool.add(new MerchantOffer(new ItemCost(Items.ECHO_SHARD, 12 + random.nextInt(5)), new ItemStack(Items.NETHERITE_SCRAP), 1, 20, 1f));
        pool.add(new MerchantOffer(new ItemCost(Items.ECHO_SHARD, 14 + random.nextInt(5)), SculkWard.book(this.level()), 1, 20, 1f));
        if (random.nextFloat() < 0.15f) {
            int idx = random.nextInt(16);
            ItemStack bp = ReflectorBlueprintItem.withColor(
                    new ItemStack(ModItems.REFLECTOR_BLUEPRINT.get()), idx);
            pool.add(new MerchantOffer(new ItemCost(Items.ECHO_SHARD, 10 + random.nextInt(6)), bp, 1, 20, 1f));
        }

        // Ручное перемешивание через RandomSource (совместимо с Minecraft API)
        for (int i = pool.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            MerchantOffer temp = pool.get(i);
            pool.set(i, pool.get(j));
            pool.set(j, temp);
        }

        int n = 3 + random.nextInt(2); // 3–4 случайные сделки
        for (int i = 0; i < n && i < pool.size(); i++) {
            offers.add(pool.get(i));
        }
        return offers;
    }

    // Кнопка «новый товар»: перемешать ассортимент и переоткрыть GUI
    public void rerollTrades() {
        this.offers = buildRandomOffers();
        if (this.getTradingPlayer() instanceof ServerPlayer sp) {
            // Переоткрываем окно — трейды обновятся
            this.openTradingScreen(sp, Component.translatable("entity.sculkecho.sculk_villager"), 1);
        }
    }

    // Достижение «Вселенская дружба»: только за торговлю со скалк-торговцем
    @Override
    public void notifyTrade(MerchantOffer offer) {
        super.notifyTrade(offer);
        if (this.getTradingPlayer() instanceof ServerPlayer sp && sp.getServer() != null) {
            var holder = sp.getServer().getAdvancements()
                    .get(net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("sculkecho", "universal_friendship"));
            if (holder != null) {
                sp.getAdvancements().award(holder, "trade_with_sculk");
            }
        }
    }
    @Override
    public void updateTrades() {
    }

    @Override
    public void rewardTradeXp(MerchantOffer offer) {
        if (this.getTradingPlayer() instanceof ServerPlayer sp) {
            sp.giveExperiencePoints(offer.getXp());
        }
    }

    @Override
    public AgeableMob getBreedOffspring(net.minecraft.server.level.ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide) return InteractionResult.CONSUME;
        if (player instanceof ServerPlayer sp && this.getTradingPlayer() == null && this.isAlive()) {
            this.setTradingPlayer(sp);
            this.openTradingScreen(sp, Component.translatable("entity.sculkecho.sculk_villager"), 1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SCULK_SENSOR_BREAK;
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }
}
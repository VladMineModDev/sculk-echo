package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.AbstractVillager;

public class SculkVillagerRenderer extends MobRenderer<AbstractVillager, VillagerModel<AbstractVillager>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "textures/entity/sculk_villager.png");

    public SculkVillagerRenderer(EntityRendererProvider.Context context) {
        super(context, new VillagerModel<>(context.bakeLayer(ModelLayers.VILLAGER)), 0.5f);
        this.addLayer(new SculkVillagerCapeLayer(this)); // накидка навсегда, ванильных слоёв нет
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractVillager entity) {
        return TEXTURE;
    }
}
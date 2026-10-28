package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PiglinHeadModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;

public class InfectedPiglinRenderer extends HumanoidMobRenderer<AbstractPiglin, HumanoidModel<AbstractPiglin>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "textures/entity/infected_piglin.png");

    public InfectedPiglinRenderer(EntityRendererProvider.Context context) {
        super(context, new HumanoidModel<>(context.bakeLayer(ModelLayers.PIGLIN)), 0.5f);
        this.addLayer(new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PIGLIN_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PIGLIN_OUTER_ARMOR)),
                context.getModelManager()));
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractPiglin entity) {
        return TEXTURE;
    }
}
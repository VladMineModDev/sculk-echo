package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import net.minecraft.client.model.IllagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Evoker;

public class InfectedEvokerRenderer extends MobRenderer<Evoker, IllagerModel<Evoker>> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "textures/entity/infected_evoker.png");

    public InfectedEvokerRenderer(EntityRendererProvider.Context context) {
        super(context, new IllagerModel<>(context.bakeLayer(ModelLayers.EVOKER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(Evoker entity) {
        return TEXTURE;
    }
}
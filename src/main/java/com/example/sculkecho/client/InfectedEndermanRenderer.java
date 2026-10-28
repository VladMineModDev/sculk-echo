package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import net.minecraft.client.renderer.entity.EndermanRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.EnderMan;

public class InfectedEndermanRenderer extends EndermanRenderer {
    private static final ResourceLocation BODY =
            ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "textures/entity/infected_enderman.png");
    private static final ResourceLocation EYES =
            ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "textures/entity/infected_enderman_eyes.png");

    public InfectedEndermanRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.layers.clear();
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public net.minecraft.client.renderer.RenderType renderType() {
                return net.minecraft.client.renderer.RenderType.eyes(EYES);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(EnderMan entity) {
        return BODY;
    }
}
package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.WitchRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Witch;

public class InfectedWitchRenderer extends WitchRenderer {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "textures/entity/infected_witch.png");

    public InfectedWitchRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Witch entity) {
        return TEXTURE;
    }
}
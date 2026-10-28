package com.example.sculkecho.client;

import com.example.sculkecho.entity.EchoWisp;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;

public class EchoWispRenderer extends EntityRenderer<EchoWisp> {

    public EchoWispRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(EchoWisp entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
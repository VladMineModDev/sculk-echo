package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SheepRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;

public class InfectedSheepRenderer extends SheepRenderer {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "textures/entity/infected_sheep.png");

    public InfectedSheepRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.layers.clear();
        this.addLayer(new InfectedSheepWoolLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(Sheep entity) {
        return TEXTURE;
    }
}
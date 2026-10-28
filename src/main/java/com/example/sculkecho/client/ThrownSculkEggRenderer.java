package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.entity.ThrownSculkEgg;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;

public class ThrownSculkEggRenderer extends ThrownItemRenderer<ThrownSculkEgg> {
    public ThrownSculkEggRenderer(EntityRendererProvider.Context context) {
        super(context);
    }
}
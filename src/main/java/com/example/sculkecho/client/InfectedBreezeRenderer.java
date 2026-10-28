package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.BreezeModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.BreezeRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.breeze.Breeze;

public class InfectedBreezeRenderer extends BreezeRenderer {
    private static final ResourceLocation BODY =
            ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "textures/entity/infected_breeze.png");
    private static final ResourceLocation EYES =
            ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "textures/entity/infected_breeze_eyes.png");
    private static final ResourceLocation WIND =
            ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "textures/entity/infected_breeze_wind.png");

    public InfectedBreezeRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.layers.clear();

        // Светящиеся глаза (аддитивно, видны в темноте)
        this.addLayer(new EyesLayer<>(this) {
            @Override
            public RenderType renderType() {
                return RenderType.eyes(EYES);
            }
        });

        // Вихрь: энергетический свайр поверх тела (полупрозрачный, как у ванили)
        this.addLayer(new RenderLayer<Breeze, BreezeModel<Breeze>>(this) {
            @Override
            public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                               Breeze breeze, float limbSwing, float limbSwingAmount, float partialTick,
                               float ageInTicks, float netHeadYaw, float headPitch) {
                if (breeze.isInvisible()) return;
                VertexConsumer consumer = buffer.getBuffer(RenderType.energySwirl(WIND, 0, 0));
                this.getParentModel().renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
            }
        });
    }

    @Override
    public ResourceLocation getTextureLocation(Breeze entity) {
        return BODY;
    }
}

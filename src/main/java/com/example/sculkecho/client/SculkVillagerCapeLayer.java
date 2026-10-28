package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.AbstractVillager;

public class SculkVillagerCapeLayer extends RenderLayer<AbstractVillager, VillagerModel<AbstractVillager>> {
    private static final ResourceLocation CAPE =
            ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "textures/entity/sculk_villager_cape.png");

    public SculkVillagerCapeLayer(RenderLayerParent<AbstractVillager, VillagerModel<AbstractVillager>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       AbstractVillager entity, float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) return;
        VertexConsumer consumer = buffer.getBuffer(RenderType.eyes(CAPE));
        this.getParentModel().renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
    }
}
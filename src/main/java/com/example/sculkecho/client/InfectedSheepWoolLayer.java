package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.SheepFurModel;
import net.minecraft.client.model.SheepModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Sheep;

public class InfectedSheepWoolLayer extends RenderLayer<Sheep, SheepModel<Sheep>> {
    private static final ResourceLocation FUR =
            ResourceLocation.fromNamespaceAndPath(SculkEcho.MODID, "textures/entity/infected_sheep_fur.png");

    private final SheepFurModel<Sheep> furModel;

    public InfectedSheepWoolLayer(RenderLayerParent<Sheep, SheepModel<Sheep>> renderer, EntityModelSet models) {
        super(renderer);
        this.furModel = new SheepFurModel<>(models.bakeLayer(ModelLayers.SHEEP_FUR));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       Sheep sheep, float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        if (sheep.isInvisible()) return;
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(FUR));
        this.furModel.prepareMobModel(sheep, limbSwing, limbSwingAmount, partialTick);
        this.furModel.setupAnim(sheep, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.furModel.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
    }
}
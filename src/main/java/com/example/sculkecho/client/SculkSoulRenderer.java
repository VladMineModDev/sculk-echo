package com.example.sculkecho.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;

public class SculkSoulRenderer extends EntityRenderer<Entity> {
    public SculkSoulRenderer(EntityRendererProvider.Context ctx) { super(ctx); }

    @Override
    public void render(Entity entity, float yaw, float pt, PoseStack pose, MultiBufferSource src, int light) {
        pose.pushPose();
        pose.translate(0, 0.2, 0);
        float s = 0.5f + 0.1f * (float) Math.sin(entity.tickCount * 0.4);
        pose.scale(s, s, s);
        pose.mulPose(Axis.YP.rotationDegrees(entity.tickCount * 10f));

        VertexConsumer buf = src.getBuffer(RenderType.lines());
        Matrix4f mat = pose.last().pose();
        int r = 14, g = 208, b = 208, a = 220;

        // Трёхосная искра + диагонали = "звезда" души
        line(buf, mat, -0.5f, 0, 0, 0.5f, 0, 0, r, g, b, a);
        line(buf, mat, 0, -0.5f, 0, 0, 0.5f, 0, r, g, b, a);
        line(buf, mat, 0, 0, -0.5f, 0, 0, 0.5f, r, g, b, a);
        line(buf, mat, -0.3f, -0.3f, 0, 0.3f, 0.3f, 0, r, g, b, a);
        line(buf, mat, -0.3f, 0.3f, 0, 0.3f, -0.3f, 0, r, g, b, a);

        pose.popPose();
    }

    private static void line(VertexConsumer buf, Matrix4f mat,
                             float x1, float y1, float z1, float x2, float y2, float z2,
                             int r, int g, int b, int a) {
        float nx = x2 - x1, ny = y2 - y1, nz = z2 - z1;
        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (len > 0) { nx /= len; ny /= len; nz /= len; }
        buf.addVertex(mat, x1, y1, z1).setColor(r, g, b, a).setNormal(nx, ny, nz);
        buf.addVertex(mat, x2, y2, z2).setColor(r, g, b, a).setNormal(nx, ny, nz);
    }

    @Override
    public ResourceLocation getTextureLocation(Entity entity) {
        return null;
    }
}
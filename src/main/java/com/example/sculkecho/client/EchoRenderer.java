package com.example.sculkecho.client;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.entity.EchoWisp;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT, modid = SculkEcho.MODID)
public class EchoRenderer {
    private static final float POINT_SIZE = 0.035f;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;

        List<ClientEchoState.Point> points = ClientEchoState.getPoints();
        List<ClientEchoState.PassivePoint> passive = ClientEchoState.getPassivePoints();
        boolean pulseActive = !ClientEchoState.isExpired();

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        boolean hasWisps = false;
        for (Entity e : mc.level.getEntities((Entity) null,
                new AABB(mc.player != null ? mc.player.position() : Vec3.ZERO,
                        mc.player != null ? mc.player.position() : Vec3.ZERO).inflate(48),
                en -> en instanceof EchoWisp && !en.isInvisible())) {
            hasWisps = true;
            break;
        }

        if (points.isEmpty() && passive.isEmpty() && !hasWisps) return;

        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();

        Quaternionf rot = new Quaternionf(camera.rotation());
        rot.conjugate();
        Matrix4f view = new Matrix4f();
        view.rotate(rot);
        view.translate((float) -camPos.x, (float) -camPos.y, (float) -camPos.z);

        // Точный прямоугольный фруструм: границы экрана, а не конус
        float aspect = (float) mc.getWindow().getWidth() / Math.max(1, mc.getWindow().getHeight());
        double tanV = Math.tan(Math.toRadians(mc.options.fov().get()) / 2.0);
        double tanH = tanV * aspect;
        Vector4f tv = new Vector4f();

        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderFogStart(0f);
        RenderSystem.setShaderFogEnd(100000f);

        BufferBuilder buffer = Tesselator.getInstance().begin(
                VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        boolean hasVertices = false;
        float hs = POINT_SIZE / 2.0f;

        // 1. Импульсные точки
        if (pulseActive) {
            for (ClientEchoState.Point point : points) {
                int a = ClientEchoState.getPointAlpha(point);
                if (a <= 0) continue;

                float x = (float) point.pos.x;
                float y = (float) point.pos.y;
                float z = (float) point.pos.z;

                tv.set(x, y, z, 1f);
                tv.mul(view);
                double fwd = -tv.z();
                if (fwd < 0.25 || fwd > 48.0) continue;
                double hw = fwd * tanH + 0.15;
                double hh = fwd * tanV + 0.15;
                if (tv.x() < -hw || tv.x() > hw || tv.y() < -hh || tv.y() > hh) continue;

                buffer.addVertex(view, x - hs, y - hs, z).setColor(point.r, point.g, point.b, a);
                buffer.addVertex(view, x + hs, y - hs, z).setColor(point.r, point.g, point.b, a);
                buffer.addVertex(view, x + hs, y + hs, z).setColor(point.r, point.g, point.b, a);
                buffer.addVertex(view, x - hs, y + hs, z).setColor(point.r, point.g, point.b, a);

                buffer.addVertex(view, x, y - hs, z - hs).setColor(point.r, point.g, point.b, a);
                buffer.addVertex(view, x, y - hs, z + hs).setColor(point.r, point.g, point.b, a);
                buffer.addVertex(view, x, y + hs, z + hs).setColor(point.r, point.g, point.b, a);
                buffer.addVertex(view, x, y + hs, z - hs).setColor(point.r, point.g, point.b, a);

                buffer.addVertex(view, x - hs, y, z - hs).setColor(point.r, point.g, point.b, a);
                buffer.addVertex(view, x + hs, y, z - hs).setColor(point.r, point.g, point.b, a);
                buffer.addVertex(view, x + hs, y, z + hs).setColor(point.r, point.g, point.b, a);
                buffer.addVertex(view, x - hs, y, z + hs).setColor(point.r, point.g, point.b, a);

                hasVertices = true;
            }

            Vec3 origin = ClientEchoState.getPulseOrigin();
            float waveRadius = ClientEchoState.getWaveRadius();
            int waveAlpha = Math.min(255, (int) (160 * (1f - ClientEchoState.getWaveAge())));
            if (waveAlpha > 0 && waveRadius > 0.01f) {
                float ox = (float) origin.x;
                float oy = (float) origin.y + 0.1f;
                float oz = (float) origin.z;
                float inner = Math.max(0f, waveRadius - 0.25f);
                int segments = 64;
                for (int i = 0; i < segments; i++) {
                    float a1 = (float) (i / (double) segments * Math.PI * 2);
                    float a2 = (float) ((i + 1) / (double) segments * Math.PI * 2);
                    float c1 = (float) Math.cos(a1), s1 = (float) Math.sin(a1);
                    float c2 = (float) Math.cos(a2), s2 = (float) Math.sin(a2);

                    buffer.addVertex(view, ox + c1 * inner, oy, oz + s1 * inner).setColor(0, 220, 220, waveAlpha);
                    buffer.addVertex(view, ox + c1 * waveRadius, oy, oz + s1 * waveRadius).setColor(0, 220, 220, waveAlpha);
                    buffer.addVertex(view, ox + c2 * waveRadius, oy, oz + s2 * waveRadius).setColor(0, 220, 220, waveAlpha);
                    buffer.addVertex(view, ox + c2 * inner, oy, oz + s2 * inner).setColor(0, 220, 220, waveAlpha);
                }
                hasVertices = true;
            }
        }

        // 2. Пассивные точки
        for (ClientEchoState.PassivePoint p : passive) {
            float age = p.getAge();
            if (age < 0f) continue;
            int a = Math.max(0, Math.min(255, (int) (255 * (1f - age))));
            if (a <= 0) continue;

            float x = (float) p.pos.x;
            float y = (float) p.pos.y;
            float z = (float) p.pos.z;

            tv.set(x, y, z, 1f);
            tv.mul(view);
            double fwd = -tv.z();
            if (fwd < 0.25 || fwd > 48.0) continue;
            double hw = fwd * tanH + 0.15;
            double hh = fwd * tanV + 0.15;
            if (tv.x() < -hw || tv.x() > hw || tv.y() < -hh || tv.y() > hh) continue;

            buffer.addVertex(view, x - hs, y - hs, z).setColor(p.r, p.g, p.b, a);
            buffer.addVertex(view, x + hs, y - hs, z).setColor(p.r, p.g, p.b, a);
            buffer.addVertex(view, x + hs, y + hs, z).setColor(p.r, p.g, p.b, a);
            buffer.addVertex(view, x - hs, y + hs, z).setColor(p.r, p.g, p.b, a);

            buffer.addVertex(view, x, y - hs, z - hs).setColor(p.r, p.g, p.b, a);
            buffer.addVertex(view, x, y - hs, z + hs).setColor(p.r, p.g, p.b, a);
            buffer.addVertex(view, x, y + hs, z + hs).setColor(p.r, p.g, p.b, a);
            buffer.addVertex(view, x, y + hs, z - hs).setColor(p.r, p.g, p.b, a);

            buffer.addVertex(view, x - hs, y, z - hs).setColor(p.r, p.g, p.b, a);
            buffer.addVertex(view, x + hs, y, z - hs).setColor(p.r, p.g, p.b, a);
            buffer.addVertex(view, x + hs, y, z + hs).setColor(p.r, p.g, p.b, a);
            buffer.addVertex(view, x - hs, y, z + hs).setColor(p.r, p.g, p.b, a);

            hasVertices = true;
        }

        // 3. Силуэт тени
        if (hasWisps) {
            for (Entity entity : mc.level.getEntities((Entity) null,
                    new AABB(camPos, camPos).inflate(48), e -> e instanceof EchoWisp && !e.isInvisible())) {
                float wx = (float) entity.getX();
                float wy = (float) entity.getY();
                float wz = (float) entity.getZ();
                float pulse = 0.75f + 0.25f * (float) Math.sin(System.currentTimeMillis() / 150.0);
                int bodyA = (int) (200 * pulse);
                addWispQuad(buffer, view, wx, wy - 0.9f, wz, 0.55f, 1.8f, 8, 34, 38, bodyA);
                addWispQuadX(buffer, view, wx, wy - 0.9f, wz, 0.55f, 1.8f, 8, 34, 38, bodyA);
                addWispQuad(buffer, view, wx, wy + 0.75f, wz, 0.22f, 0.3f, 25, 200, 200, 255);
                addWispQuadX(buffer, view, wx, wy + 0.75f, wz, 0.22f, 0.3f, 25, 200, 200, 255);
                hasVertices = true;
            }
        }

        if (hasVertices) {
            BufferUploader.drawWithShader(buffer.buildOrThrow());
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void addWispQuad(BufferBuilder buffer, Matrix4f view, float x, float y, float z,
                                    float w, float h, int r, int g, int b, int a) {
        buffer.addVertex(view, x - w / 2, y, z).setColor(r, g, b, a);
        buffer.addVertex(view, x + w / 2, y, z).setColor(r, g, b, a);
        buffer.addVertex(view, x + w / 2, y + h, z).setColor(r, g, b, a);
        buffer.addVertex(view, x - w / 2, y + h, z).setColor(r, g, b, a);
    }

    private static void addWispQuadX(BufferBuilder buffer, Matrix4f view, float x, float y, float z,
                                     float w, float h, int r, int g, int b, int a) {
        buffer.addVertex(view, x, y, z - w / 2).setColor(r, g, b, a);
        buffer.addVertex(view, x, y, z + w / 2).setColor(r, g, b, a);
        buffer.addVertex(view, x, y + h, z + w / 2).setColor(r, g, b, a);
        buffer.addVertex(view, x, y + h, z - w / 2).setColor(r, g, b, a);
    }
}
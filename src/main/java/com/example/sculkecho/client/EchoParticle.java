package com.example.sculkecho.client;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EchoParticle extends Particle {
    private static final float SIZE = 0.04f;

    protected EchoParticle(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.lifetime = 40; // 2 секунды (40 тиков)
        this.gravity = 0f;
        this.hasPhysics = false;
        this.xd = 0; this.yd = 0; this.zd = 0;
    }

    @Override
    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        Vec3 camPos = camera.getPosition();
        float x = (float) (this.x - camPos.x);
        float y = (float) (this.y - camPos.y);
        float z = (float) (this.z - camPos.z);

        float age = (this.age + partialTick) / this.lifetime;
        int alpha = (int) (255 * Math.max(0, 1.0f - age));
        if (alpha <= 0) return;

        // Билборд: квадрат всегда повернут к камере
        Quaternionf quaternion = camera.rotation();
        Vector3f v1 = new Vector3f(-1.0f, -1.0f, 0.0f).rotate(quaternion);
        Vector3f v2 = new Vector3f(-1.0f, 1.0f, 0.0f).rotate(quaternion);
        Vector3f v3 = new Vector3f(1.0f, 1.0f, 0.0f).rotate(quaternion);
        Vector3f v4 = new Vector3f(1.0f, -1.0f, 0.0f).rotate(quaternion);

        buffer.addVertex(x + v1.x() * SIZE, y + v1.y() * SIZE, z + v1.z() * SIZE)
                .setColor(255, 255, 255, alpha).setUv(0, 1).setUv2(240, 240).setNormal(0, 1, 0);
        buffer.addVertex(x + v2.x() * SIZE, y + v2.y() * SIZE, z + v2.z() * SIZE)
                .setColor(255, 255, 255, alpha).setUv(0, 0).setUv2(240, 240).setNormal(0, 1, 0);
        buffer.addVertex(x + v3.x() * SIZE, y + v3.y() * SIZE, z + v3.z() * SIZE)
                .setColor(255, 255, 255, alpha).setUv(1, 0).setUv2(240, 240).setNormal(0, 1, 0);
        buffer.addVertex(x + v4.x() * SIZE, y + v4.y() * SIZE, z + v4.z() * SIZE)
                .setColor(255, 255, 255, alpha).setUv(1, 1).setUv2(240, 240).setNormal(0, 1, 0);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT; // Светится в темноте
    }
}
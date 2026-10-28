package com.example.sculkecho.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public final class FaceCull {


    public static boolean shouldDraw(Level level, BlockPos pos, Direction face) {
        // 1) Сосед сплошной — грань внутри геометрии
        BlockPos neighborPos = pos.relative(face);
        BlockState neighbor = level.getBlockState(neighborPos);
        if (neighbor.canOcclude()) return false;

        // 2) Backface: нормаль грани смотрит от камеры
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return true;
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        Vec3 center = Vec3.atCenterOf(pos);
        Vec3 normal = new Vec3(face.getStepX(), face.getStepY(), face.getStepZ());
        Vec3 toCamera = cameraPos.subtract(center);
        return toCamera.dot(normal) > 0.0;
    }
}
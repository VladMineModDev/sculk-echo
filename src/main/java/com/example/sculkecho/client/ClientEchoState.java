package com.example.sculkecho.client;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class ClientEchoState {
    private static float interference = 0f;
    private static long possessionStart = 0;
    public static final long CREEP_MS = 5200;
    public static final long HOLD_MS = 900;

    public static void addInterference(float amount) {
        interference = Math.min(1f, interference + amount);
    }

    public static void startPossessionSequence() {
        possessionStart = System.currentTimeMillis();
    }

    public static float getPossessionProgress() {
        if (possessionStart == 0) return 0f;
        return (System.currentTimeMillis() - possessionStart) / (float) CREEP_MS;
    }

    public static boolean isPossessionSequenceActive() {
        if (possessionStart == 0) return false;
        if (System.currentTimeMillis() - possessionStart > CREEP_MS + HOLD_MS) {
            possessionStart = 0;
            return false;
        }
        return true;
    }

    public static boolean isFogSuppressed() {
        return possessionStart != 0
                && System.currentTimeMillis() - possessionStart < CREEP_MS;
    }

    // Команда /sculkecho fog off/on
    private static boolean fogDisabled = false;
    public static void setFogDisabled(boolean v) { fogDisabled = v; }
    public static boolean isFogDisabled() { return fogDisabled; }

    public static float getInterference() {
        return interference;
    }

    private static final float OFFSET = 0.03f;
    private static final double[] GRID = {0.25, 0.5, 0.75};
    private static final Random RND = new Random();
    private static final int DOTS_PER_SIGNAL = 9;
    private static final int[] BRIGHT = {
            0xFFFFFF, 0xFF9020, 0xE060D0, 0x50C0FF,
            0xFFE040, 0x90E040, 0xFFA0C0, 0x707878,
            0xB8B8B0, 0x20D0D0, 0xA050E0, 0x5060FF,
            0xA07040, 0x70C030, 0xFF4040, 0x505050
    };

    private record Profile(int maxSignals, long waveMs, long fadeMs,
                           double noise, double falseRatio, double phantomRatio,
                           double delayChance, long delayMinMs, long delayMaxMs,
                           double ghostChance, double moverChance) {}

    private static Profile profile(int stage) {
        return switch (stage) {
            case 0  -> new Profile(40,    300, 400,  0.80, 0.50, 0.250, 0.60, 120, 260, 0.00, 0.00);
            case 1  -> new Profile(200,   400, 700,  0.40, 0.25, 0.120, 0.35, 100, 200, 0.15, 0.10);
            case 2  -> new Profile(500,   500, 1100, 0.20, 0.10, 0.060, 0.18, 80,  160, 0.15, 0.08);
            case 3  -> new Profile(900,   600, 1500, 0.10, 0.04, 0.020, 0.06, 60,  120, 0.05, 0.05);
            case 4  -> new Profile(1600,  700, 2000, 0.03, 0.01, 0.005, 0.01, 40,  80,  0.00, 0.00);
            default -> new Profile(0,     800, 2600, 0.00, 0.00, 0.000, 0.00, 0,   0,   0.00, 0.00);
        };
    }

    private static int cooldown = 0;
    private static int stage = 0;
    private static int echoShotCooldown = 0;

    private static int reflectorColorIndex = -1;
    private static long reflectorInstallTick = -1L;

    public static void setReflector(int colorIndex, long installTick) {
        reflectorColorIndex = colorIndex;
        reflectorInstallTick = installTick;
    }

    public static int getReflectorColorIndex() { return reflectorColorIndex; }
    public static long getReflectorInstallTick() { return reflectorInstallTick; }

    public static boolean hasStabilizedReflector(long now) {
        return reflectorColorIndex >= 0 && reflectorInstallTick > 0 && (now - reflectorInstallTick) >= 2400L;
    }

    public static boolean hasGlitchingReflector(long now) {
        return reflectorColorIndex >= 0 && reflectorInstallTick > 0 && (now - reflectorInstallTick) < 2400L;
    }

    public static boolean tryEchoShot() {
        if (echoShotCooldown > 0) return false;
        echoShotCooldown = 40;
        return true;
    }

    public static int getEchoShotCooldown() { return echoShotCooldown; }

    private static int infection = 0;
    public static void setInfection(int v) { infection = v; }
    public static int getInfection() { return infection; }

    private static final List<Point> points = new ArrayList<>();
    private static final List<PassivePoint> passivePoints = new ArrayList<>();
    private static final List<Mover> movers = new ArrayList<>();
    private static long pulseTime = 0;
    private static Vec3 pulseOrigin = Vec3.ZERO;
    private static double pulseRadius = 0;
    private static Profile pulseProfile = profile(0);

    public static class Point {
        public final Vec3 pos;
        public final int r, g, b;
        public int revealDelayMs;
        public Point(Vec3 pos, int r, int g, int b) {
            this.pos = pos; this.r = r; this.g = g; this.b = b;
        }
    }

    public static class PassivePoint {
        public final Vec3 pos;
        public final int r, g, b;
        public final long spawnTime;
        public final long lifetimeMs;
        public PassivePoint(Vec3 pos, int r, int g, int b, long spawnTime, long lifetimeMs) {
            this.pos = pos; this.r = r; this.g = g; this.b = b;
            this.spawnTime = spawnTime; this.lifetimeMs = lifetimeMs;
        }
        public float getAge() {
            return (System.currentTimeMillis() - spawnTime) / (float) lifetimeMs;
        }
        public boolean isExpired() { return getAge() >= 1f; }
    }

    private static final class Mover {
        Vec3 pos; final Vec3 dir; final double speed; final long dieAt;
        Mover(Vec3 p, Vec3 d, double s, long t) { pos = p; dir = d; speed = s; dieAt = t; }
    }

    // pixel = true: семплировать пиксель текстуры грани; col = запасной/приоритетный цвет
    private record FaceSig(int x, int y, int z, Direction dir, int col, boolean pixel) {}

    public static void setStage(int newStage) { stage = newStage; }
    public static int getStage() { return stage; }

    public static boolean tryPulse() {
        if (cooldown > 0) return false;
        cooldown = 12;
        return true;
    }

    public static void tick() {
        if (cooldown > 0) cooldown--;
        if (isExpired()) points.clear();
        passivePoints.removeIf(PassivePoint::isExpired);
        interference = Math.max(0f, interference - 0.02f);
        long now = System.currentTimeMillis();
        Iterator<Mover> it = movers.iterator();
        if (echoShotCooldown > 0) echoShotCooldown--;
        while (it.hasNext()) {
            Mover m = it.next();
            if (now >= m.dieAt) { it.remove(); continue; }
            m.pos = m.pos.add(m.dir.scale(m.speed));
            addPassivePoint(m.pos, 0xD0D0D0, 800);
        }
    }

    public static List<Point> getPoints() { return points; }
    public static List<PassivePoint> getPassivePoints() { return passivePoints; }
    public static Vec3 getPulseOrigin() { return pulseOrigin; }
    public static double getPulseRadius() { return pulseRadius; }

    public static boolean isExpired() {
        if (pulseTime == 0) return true;
        return System.currentTimeMillis() - pulseTime
                > pulseProfile.waveMs() + pulseProfile.fadeMs() + 400;
    }

    public static float getWaveAge() {
        if (pulseTime == 0) return 1f;
        return Math.min(1f, (System.currentTimeMillis() - pulseTime) / (float) pulseProfile.waveMs());
    }

    public static float getWaveRadius() {
        if (pulseTime == 0) return 0f;
        return (float) (pulseRadius * getWaveAge());
    }

    public static int getPointAlpha(Point p) {
        if (pulseTime == 0 || pulseRadius <= 0) return 0;
        long e = System.currentTimeMillis() - pulseTime - p.revealDelayMs;
        double dist = p.pos.distanceTo(pulseOrigin);
        long revealMs = (long) (pulseProfile.waveMs() * Math.min(1.0, dist / pulseRadius));
        if (e < revealMs) return 0;
        float fade = (e - revealMs) / (float) pulseProfile.fadeMs();
        if (fade >= 1f) return 0;
        return (int) (255 * (1f - fade));
    }

    public static void addPassivePoint(Vec3 pos, int color, long lifetimeMs) {
        passivePoints.add(new PassivePoint(pos,
                (color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF,
                System.currentTimeMillis(), lifetimeMs));
    }

    public static void addPassiveBox(AABB bb, int color, long lifetimeMs) {
        int r = (color >> 16) & 0xFF, g = (color >> 8) & 0xFF, b = color & 0xFF;
        long now = System.currentTimeMillis();
        double[] t = {0.2, 0.5, 0.8};
        for (double a : t) for (double c : t) {
            double x = bb.minX + (bb.maxX - bb.minX) * a;
            double y = bb.minY + (bb.maxY - bb.minY) * a;
            double z = bb.minZ + (bb.maxZ - bb.minZ) * a;
            passivePoints.add(new PassivePoint(new Vec3(x, y, z), r, g, b, now, lifetimeMs));
        }
    }

    public static void startPulse(ClientLevel level, Vec3 origin, double radius, int colorMask) {
        points.clear();
        pulseOrigin = origin;
        pulseRadius = radius;
        pulseProfile = profile(stage);
        Profile pr = pulseProfile;

        List<FaceSig> faces = new ArrayList<>();
        int r = (int) Math.ceil(radius);
        double r2 = radius * radius;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        BlockPos.MutableBlockPos nPos = new BlockPos.MutableBlockPos();
        int ox = (int) Math.floor(origin.x);
        int oy = (int) Math.floor(origin.y);
        int oz = (int) Math.floor(origin.z);

        for (int dx = -r; dx <= r; dx++) {
            for (int dy = -r; dy <= r; dy++) {
                for (int dz = -r; dz <= r; dz++) {
                    double cx = dx + 0.5, cy = dy + 0.5, cz = dz + 0.5;
                    if (cx * cx + cy * cy + cz * cz > r2) continue;
                    pos.set(ox + dx, oy + dy, oz + dz);
                    BlockState state = level.getBlockState(pos);
                    if (state.isAir()) continue;
                    if (state.getCollisionShape(level, pos).isEmpty()) continue;
                    if (!state.getFluidState().isEmpty()) continue;

                    int col = faceColor(level, pos, state, colorMask);
                    boolean pixel = wantsPixel(level, pos, state, colorMask);
                    for (Direction dir : Direction.values()) {
                        nPos.setWithOffset(pos, dir);
                        BlockState nState = level.getBlockState(nPos);
                        if (!nState.isAir() && !nState.getCollisionShape(level, nPos).isEmpty()) continue;
                        // Backface culling: грань, отвёрнутая от источника импульса, не рисуется
                        Vec3 faceCenter = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                        Vec3 normal = new Vec3(dir.getStepX(), dir.getStepY(), dir.getStepZ());
                        if (origin.subtract(faceCenter).dot(normal) <= 0.0) continue;
                        faces.add(new FaceSig(pos.getX(), pos.getY(), pos.getZ(), dir, col, pixel));
                    }
                }
            }
        }

        if (pr.maxSignals() > 0 && faces.size() > pr.maxSignals()) {
            Collections.shuffle(faces, RND);
            faces = new ArrayList<>(faces.subList(0, pr.maxSignals()));
        }

        double jitter = pr.noise() * 0.6;
        for (FaceSig f : faces) {
            addFaceDots(f, jitter, level);
        }

        int falseCount = (int) (pr.maxSignals() * DOTS_PER_SIGNAL * pr.falseRatio());
        for (int i = 0; i < falseCount; i++) {
            double dist = radius * RND.nextDouble();
            double y = -1 + RND.nextDouble() * 4;
            double ang = RND.nextDouble() * Math.PI * 2;
            double rad = Math.sqrt(Math.max(0, 1 - (y * y) / 16));
            points.add(new Point(origin.add(Math.cos(ang) * dist * rad, y, Math.sin(ang) * dist * rad), 255, 255, 255));
        }

        int phantomCount = (int) (points.size() * pr.phantomRatio());
        for (int i = 0; i < phantomCount && !points.isEmpty(); i++) {
            Vec3 src = points.get(RND.nextInt(points.size())).pos;
            Vec3 off = new Vec3(RND.nextDouble() * 2 - 1, RND.nextDouble() * 2 - 1, RND.nextDouble() * 2 - 1)
                    .normalize().scale(0.6 + RND.nextDouble() * 0.9);
            points.add(new Point(src.add(off), 255, 255, 255));
        }

        if (pr.delayChance() > 0) {
            for (Point p : points) {
                if (RND.nextDouble() < pr.delayChance()) {
                    p.revealDelayMs = (int) (pr.delayMinMs() + (long) (RND.nextDouble() * (pr.delayMaxMs() - pr.delayMinMs())));
                }
            }
        }

        if (RND.nextDouble() < pr.ghostChance()) {
            double ang = RND.nextDouble() * Math.PI * 2;
            double dist = radius * (0.35 + RND.nextDouble() * 0.5);
            Vec3 g = origin.add(Math.cos(ang) * dist, 0, Math.sin(ang) * dist);
            long appearAt = System.currentTimeMillis() + 500 + RND.nextInt(600);
            addGhostBox(g, appearAt);
        }

        if (RND.nextDouble() < pr.moverChance()) {
            double ang = RND.nextDouble() * Math.PI * 2;
            Vec3 m = origin.add(Math.cos(ang) * (3 + RND.nextDouble() * 4), 1.0, Math.sin(ang) * (3 + RND.nextDouble() * 4));
            double va = RND.nextDouble() * Math.PI * 2;
            movers.add(new Mover(m, new Vec3(Math.cos(va), 0, Math.sin(va)),
                    0.05 + RND.nextDouble() * 0.04,
                    System.currentTimeMillis() + 2200 + RND.nextInt(800)));
        }

        if (stage >= 3) {
            for (Entity entity : level.getEntities((Entity) null,
                    new AABB(origin, origin).inflate(radius),
                    e -> e instanceof LivingEntity
                            && !(e instanceof net.minecraft.client.player.LocalPlayer)
                            && !(e instanceof net.minecraft.world.entity.decoration.ArmorStand))) {
                int mobColor = (entity instanceof net.minecraft.world.entity.monster.Monster) ? 0xFF4545 : 0x45FF7A;
                addBoxDots(entity.getBoundingBox(), mobColor);
            }
        }

        pulseTime = System.currentTimeMillis();
    }

    private static void addGhostBox(Vec3 g, long appearAt) {
        AABB bb = new AABB(g.x - 0.3, g.y, g.z - 0.3, g.x + 0.3, g.y + 1.8, g.z + 0.3);
        double[] t = {0.2, 0.5, 0.8};
        for (double a : t) for (double c : t) {
            double x = bb.minX + (bb.maxX - bb.minX) * a;
            double y = bb.minY + (bb.maxY - bb.minY) * a;
            double z = bb.minZ + (bb.maxZ - bb.minZ) * a;
            ghostDot(x, bb.maxY + OFFSET, z, appearAt);
            ghostDot(x, bb.minY - OFFSET, z, appearAt);
            ghostDot(x, y, bb.minZ - OFFSET, appearAt);
            ghostDot(x, y, bb.maxZ + OFFSET, appearAt);
            ghostDot(bb.minX - OFFSET, y, z, appearAt);
            ghostDot(bb.maxX + OFFSET, y, z, appearAt);
        }
    }

    private static void ghostDot(double x, double y, double z, long appearAt) {
        passivePoints.add(new PassivePoint(new Vec3(x, y, z), 232, 232, 255, appearAt, 1300));
    }

    private static int mapColorBright(ClientLevel level, BlockPos pos, BlockState state) {
        try {
            int rgb = state.getMapColor(level, pos).calculateRGBColor(MapColor.Brightness.HIGH);
            int r = Math.min(255, ((rgb >> 16) & 0xFF) + 50);
            int g = Math.min(255, ((rgb >> 8) & 0xFF) + 50);
            int b = Math.min(255, (rgb & 0xFF) + 50);
            return (r << 16) | (g << 8) | b;
        } catch (Exception e) {
            return 0xB8B8B0;
        }
    }
    // Приоритет: маска (белый) > рефлектор > пиксель текстуры > цвет карты блока (никогда не белый)
    private static int faceColor(ClientLevel level, BlockPos pos, BlockState state, int mask) {
        long now = System.currentTimeMillis();
        int idx = dyeIndexOf(state.getMapColor(level, pos));
        if (idx >= 0 && (mask & (1 << idx)) == 0) return 0xFFFFFF;
        if (idx < 0) return mapColorBright(level, pos, state);
        if (idx >= 0) {
            if (hasStabilizedReflector(now) && reflectorColorIndex == idx) return BRIGHT[reflectorColorIndex];
            if (hasGlitchingReflector(now) && reflectorColorIndex == idx && RND.nextBoolean()) return BRIGHT[reflectorColorIndex];
            return BRIGHT[idx];
        }
        return mapColorFallback(level, pos, state);
    }

    // Семплируем пиксель, только если цвет не переопределён маской или рефлектором
    private static boolean wantsPixel(ClientLevel level, BlockPos pos, BlockState state, int mask) {
        int idx = dyeIndexOf(state.getMapColor(level, pos));
        if (idx >= 0 && (mask & (1 << idx)) == 0) return false;
        long now = System.currentTimeMillis();
        if (idx >= 0) {
            if (hasStabilizedReflector(now) && reflectorColorIndex == idx) return false;
            if (hasGlitchingReflector(now) && reflectorColorIndex == idx) return false;
        }
        return true;
    }

    public static int getReflectorRgb() {
        return reflectorColorIndex >= 0 && reflectorColorIndex < BRIGHT.length
                ? BRIGHT[reflectorColorIndex] : 0xFFFFFF;
    }

    public static int getHudDotColor(long now) {
        if (hasStabilizedReflector(now)) return getReflectorRgb();
        if (hasGlitchingReflector(now)) {
            return ((now / 6) % 2 == 0) ? getReflectorRgb() : 0xFFFFFF;
        }
        return -1;
    }

    private static int dyeIndexOf(MapColor mc) {
        for (net.minecraft.world.item.DyeColor dye : net.minecraft.world.item.DyeColor.values()) {
            if (dye.getMapColor() == mc) return dye.getId();
        }
        if (mc == MapColor.GRASS) return 13;
        if (mc == MapColor.PLANT) return 5;
        if (mc == MapColor.DIRT || mc == MapColor.PODZOL || mc == MapColor.WOOD) return 12;
        if (mc == MapColor.STONE) return 7;
        if (mc == MapColor.METAL || mc == MapColor.CLAY) return 8;
        if (mc == MapColor.SAND || mc == MapColor.GOLD) return 4;
        if (mc == MapColor.WATER || mc == MapColor.LAPIS) return 11;
        if (mc == MapColor.ICE) return 3;
        if (mc == MapColor.DIAMOND) return 9;
        if (mc == MapColor.EMERALD) return 13;
        if (mc == MapColor.NETHER) return 14;
        return -1;
    }

    // Запасной цвет для нераспознанных MapColor: яркая версия цвета карты блока. БЕЛЫМ больше не будет никогда
    private static int mapColorFallback(ClientLevel level, BlockPos pos, BlockState state) {
        try {
            int rgb = state.getMapColor(level, pos).calculateRGBColor(MapColor.Brightness.HIGH);
            int r = Math.min(255, ((rgb >> 16) & 0xFF) + 50);
            int g = Math.min(255, ((rgb >> 8) & 0xFF) + 50);
            int b = Math.min(255, (rgb & 0xFF) + 50);
            return (r << 16) | (g << 8) | b;
        } catch (Exception e) {
            return 0xB8B8B0;
        }
    }

    private static void addFaceDots(FaceSig f, double jitter, ClientLevel level) {
        double x0 = f.x(), y0 = f.y(), z0 = f.z();
        BlockPos bp = new BlockPos(f.x(), f.y(), f.z());
        BlockState state = f.pixel() ? level.getBlockState(bp) : null;
        for (double a : GRID) {
            for (double c : GRID) {
                double dx, dy, dz;
                switch (f.dir()) {
                    case UP    -> { dx = x0 + a; dy = y0 + 1 + OFFSET; dz = z0 + c; }
                    case DOWN  -> { dx = x0 + a; dy = y0 - OFFSET;     dz = z0 + c; }
                    case NORTH -> { dx = x0 + a; dy = y0 + c; dz = z0 - OFFSET; }
                    case SOUTH -> { dx = x0 + a; dy = y0 + c; dz = z0 + 1 + OFFSET; }
                    case WEST  -> { dx = x0 - OFFSET; dy = y0 + a; dz = z0 + c; }
                    default    -> { dx = x0 + 1 + OFFSET; dy = y0 + a; dz = z0 + c; }
                }
                int col = f.col();
                if (f.pixel()) {
                    int pc = pixelColorAt(state, f.dir(), dx, dy, dz);
                    if (pc == -1) continue; // прозрачный пиксель (листва!) — точку не рисуем
                    if (pc >= 0) col = pc;
                }
                addJittered(dx, dy, dz, jitter, (col >> 16) & 0xFF, (col >> 8) & 0xFF, col & 0xFF);
            }
        }
    }

    // ---------- Семплирование пикселей текстур ----------

    private static final Map<ResourceLocation, int[]> PIXEL_CACHE = new HashMap<>();
    private static final RandomSource QUAD_RND = RandomSource.create(4242);

    private static int[] loadPixels(ResourceLocation tex) {
        return PIXEL_CACHE.computeIfAbsent(tex, loc -> {
            try {
                ResourceLocation file = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(),
                        "textures/" + loc.getPath() + ".png");
                Optional<Resource> res = Minecraft.getInstance().getResourceManager().getResource(file);
                if (res.isEmpty()) return new int[0];
                NativeImage img = NativeImage.read(res.get().open());
                int w = img.getWidth(), h = img.getHeight();
                int[] px = new int[2 + w * h];
                px[0] = w; px[1] = h;
                for (int y = 0; y < h; y++)
                    for (int x = 0; x < w; x++)
                        px[2 + y * w + x] = img.getPixelRGBA(x, y);
                img.close();
                return px;
            } catch (Exception e) {
                return new int[0];
            }
        });
    }

    // Цвет пикселя текстуры ТОЙ грани, куда попала точка. -1 = прозрачный пиксель, -2 = нет данных
    private static int pixelColorAt(BlockState state, Direction dir, double x, double y, double z) {
        try {
            Minecraft mc = Minecraft.getInstance();
            var model = mc.getBlockRenderer().getBlockModel(state);
            TextureAtlasSprite sprite = null;
            List<BakedQuad> quads = model.getQuads(state, dir, QUAD_RND);
            if (!quads.isEmpty()) sprite = quads.get(0).getSprite();
            if (sprite == null) sprite = model.getParticleIcon();
            int[] px = loadPixels(sprite.contents().name());
            if (px.length < 3) return -2;
            int w = px[0], h = px[1];
            double fu, fv;
            switch (dir) {
                case UP, DOWN -> { fu = x - Math.floor(x); fv = z - Math.floor(z); }
                case NORTH, SOUTH -> { fu = x - Math.floor(x); fv = y - Math.floor(y); }
                default -> { fu = z - Math.floor(z); fv = y - Math.floor(y); }
            }
            int ix = Math.min(w - 1, (int) (fu * w));
            int iy = Math.min(h - 1, (int) (fv * h));
            int rgba = px[2 + iy * w + ix];
            if ((rgba >>> 24) < 128) return -1;
            int r = Math.min(255, (rgba & 0xFF) + 40);
            int g = Math.min(255, ((rgba >> 8) & 0xFF) + 40);
            int b = Math.min(255, ((rgba >> 16) & 0xFF) + 40);
            return (r << 16) | (g << 8) | b;
        } catch (Exception e) {
            return -2;
        }
    }

    private static void addBoxDots(AABB bb, int col) {
        int cr = (col >> 16) & 0xFF, cg = (col >> 8) & 0xFF, cb = col & 0xFF;
        double[] t = {0.2, 0.5, 0.8};
        for (double a : t) for (double c : t) {
            double x = bb.minX + (bb.maxX - bb.minX) * a;
            double y = bb.minY + (bb.maxY - bb.minY) * a;
            double z = bb.minZ + (bb.maxZ - bb.minZ) * a;
            points.add(new Point(new Vec3(x, bb.maxY + OFFSET, z), cr, cg, cb));
            points.add(new Point(new Vec3(x, bb.minY - OFFSET, z), cr, cg, cb));
            points.add(new Point(new Vec3(x, y, bb.minZ - OFFSET), cr, cg, cb));
            points.add(new Point(new Vec3(x, y, bb.maxZ + OFFSET), cr, cg, cb));
            points.add(new Point(new Vec3(bb.minX - OFFSET, y, z), cr, cg, cb));
            points.add(new Point(new Vec3(bb.maxX + OFFSET, y, z), cr, cg, cb));
        }
    }

    private static void addJittered(double x, double y, double z, double jitter, int cr, int cg, int cb) {
        if (jitter > 0 && RND.nextDouble() < jitter * 1.25) {
            x += (RND.nextDouble() * 2 - 1) * jitter;
            y += (RND.nextDouble() * 2 - 1) * jitter;
            z += (RND.nextDouble() * 2 - 1) * jitter;
        }
        points.add(new Point(new Vec3(x, y, z), cr, cg, cb));
    }

    public static void addEchoShotWave(Vec3 eye, Vec3 dir) {
        Vec3 up = Math.abs(dir.y) > 0.9 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        Vec3 side1 = dir.cross(up).normalize();
        Vec3 side2 = dir.cross(side1).normalize();
        long now = System.currentTimeMillis();
        for (int i = 0; i <= 26; i++) {
            Vec3 c = eye.add(dir.scale(0.5 + i * 0.3));
            double r = 0.35 + i * 0.03;
            for (int s = 0; s < 12; s++) {
                double a = (s / 12.0) * Math.PI * 2;
                Vec3 p = c.add(side1.scale(Math.cos(a) * r)).add(side2.scale(Math.sin(a) * r));
                passivePoints.add(new PassivePoint(p, 25, 200, 200, now + i * 15L, 700));
            }
        }
    }
}
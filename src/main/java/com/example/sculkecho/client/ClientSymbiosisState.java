package com.example.sculkecho.client;

public class ClientSymbiosisState {
    private static boolean embraced = false;
    private static int favor = 0;
    private static int level = 0;

    public static void set(boolean e, int f, int l) { embraced = e; favor = f; level = l; }
    public static boolean isEmbraced() { return embraced; }
    public static int getFavor() { return favor; }
    public static int getLevel() { return level; }
    public static int getMaxSummons() { return level; } // Симбиоз I-V = 1-5 слотов призыва
}
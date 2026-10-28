package com.example.sculkecho;

import com.example.sculkecho.client.ClientEchoState;
import com.example.sculkecho.infection.InfectionData;
import com.example.sculkecho.infection.InfectionHelper;
import com.example.sculkecho.init.ModAttachments;
import com.example.sculkecho.network.InfectionSyncPayload;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = SculkEcho.MODID)
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();
        d.register(Commands.literal("sculkecho")
                .requires(src -> src.hasPermission(2))
                .then(Commands.literal("infection")
                        .then(Commands.literal("set")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, 100))
                                        .executes(ctx -> setInfection(ctx.getSource(),
                                                IntegerArgumentType.getInteger(ctx, "value")))))
                        .then(Commands.literal("get")
                                .executes(ctx -> getInfection(ctx.getSource()))))
                .then(Commands.literal("colors")
                        .then(Commands.literal("add")
                                .then(Commands.argument("color", StringArgumentType.word())
                                        .executes(ctx -> addColor(ctx.getSource(),
                                                StringArgumentType.getString(ctx, "color")))))
                        .then(Commands.literal("all")
                                .executes(ctx -> setAllColors(ctx.getSource())))
                        .then(Commands.literal("clear")
                                .executes(ctx -> clearColors(ctx.getSource()))))
                .then(Commands.literal("immunity")
                        .executes(ctx -> toggleImmunity(ctx.getSource()))
                        .then(Commands.literal("on").executes(ctx -> setImmunity(ctx.getSource(), true)))
                        .then(Commands.literal("off").executes(ctx -> setImmunity(ctx.getSource(), false))))

                .then(Commands.literal("fog")
                        .then(Commands.literal("on")
                                .executes(ctx -> {
                                    ClientEchoState.setFogDisabled(false);
                                    ctx.getSource().sendSuccess(() -> Component.literal("§6Sculk fog: §aenabled"), false);
                                    return 1;
                                }))
                        .then(Commands.literal("off")
                                .executes(ctx -> {
                                    ClientEchoState.setFogDisabled(true);
                                    ctx.getSource().sendSuccess(() -> Component.literal("§6Sculk fog: §cdisabled"), false);
                                    return 1;
                                })))
        );
    }

    private static int setInfection(CommandSourceStack src, int value) {
        if (!(src.getEntity() instanceof ServerPlayer player)) return 0;
        InfectionData cur = InfectionHelper.getData(player);
        int delta = value - cur.infection();
        if (delta != 0) InfectionHelper.addInfection(player, delta);
        InfectionData after = InfectionHelper.getData(player);
        PacketDistributor.sendToPlayer(player, new InfectionSyncPayload(after.stage(), after.infection()));
        src.sendSuccess(() -> Component.literal("Infection set to " + value + " (stage " + after.stage() + ")"), false);
        return 1;
    }

    private static int getInfection(CommandSourceStack src) {
        if (!(src.getEntity() instanceof ServerPlayer player)) return 0;
        InfectionData data = InfectionHelper.getData(player);
        src.sendSuccess(() -> Component.literal("Infection: " + data.infection()
                + " (stage " + data.stage() + ", cooldown " + data.cooldown() + ")"), false);
        return 1;
    }

    private static int addColor(CommandSourceStack src, String colorName) {
        if (!(src.getEntity() instanceof ServerPlayer player)) return 0;
        DyeColor dye = DyeColor.byName(colorName.toLowerCase(), null);
        if (dye == null) {
            src.sendFailure(Component.literal("Unknown color: " + colorName + ". Use: white, orange, magenta, light_blue, yellow, lime, pink, gray, light_gray, cyan, purple, blue, brown, green, red, black"));
            return 0;
        }
        int mask = player.getData(ModAttachments.ECHO_COLORS.get()) | (1 << dye.getId());
        player.setData(ModAttachments.ECHO_COLORS.get(), mask);
        src.sendSuccess(() -> Component.literal("Color " + dye.getName() + " unlocked"), false);
        return 1;
    }

    private static int setAllColors(CommandSourceStack src) {
        if (!(src.getEntity() instanceof ServerPlayer player)) return 0;
        player.setData(ModAttachments.ECHO_COLORS.get(), 0xFFFF);
        src.sendSuccess(() -> Component.literal("All colors unlocked"), false);
        return 1;
    }

    private static int clearColors(CommandSourceStack src) {
        if (!(src.getEntity() instanceof ServerPlayer player)) return 0;
        player.setData(ModAttachments.ECHO_COLORS.get(), 0);
        src.sendSuccess(() -> Component.literal("All colors cleared"), false);
        return 1;
    }

    private static int toggleImmunity(CommandSourceStack src) {
        if (!(src.getEntity() instanceof ServerPlayer player)) return 0;
        boolean now = !player.getData(ModAttachments.IMMUNE.get());
        player.setData(ModAttachments.IMMUNE.get(), now);
        src.sendSuccess(() -> Component.literal("Sculk immunity: " + (now ? "ON" : "OFF")), false);
        return 1;
    }

    private static int setImmunity(CommandSourceStack src, boolean value) {
        if (!(src.getEntity() instanceof ServerPlayer player)) return 0;
        player.setData(ModAttachments.IMMUNE.get(), value);
        src.sendSuccess(() -> Component.literal("Sculk immunity: " + (value ? "ON" : "OFF")), false);
        return 1;
    }
}
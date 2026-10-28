package com.example.sculkecho.init;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.infection.InfectionData;
import com.example.sculkecho.infection.PlayerReflectorData;
import com.example.sculkecho.infection.PossessionData;
import com.example.sculkecho.infection.SymbiosisData;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class ModAttachments {
    // ВАЖНО: ATTACHMENT_TYPES должно быть ПЕРВЫМ
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SculkEcho.MODID);

    public static final Supplier<AttachmentType<InfectionData>> INFECTION =
            ATTACHMENT_TYPES.register("infection",
                    () -> AttachmentType.builder(() -> InfectionData.DEFAULT)
                            .serialize(InfectionData.CODEC)
                            .build());

    public static final Supplier<AttachmentType<PossessionData>> POSSESSION =
            ATTACHMENT_TYPES.register("possession",
                    () -> AttachmentType.builder(() -> PossessionData.DEFAULT)
                            .serialize(PossessionData.CODEC)
                            .build());

    public static final Supplier<AttachmentType<Boolean>> STIMULATED =
            ATTACHMENT_TYPES.register("stimulated",
                    () -> AttachmentType.builder(() -> false)
                            .serialize(Codec.BOOL)
                            .build());

    // Маска открытых цветов для эхо-рефлекторов
    public static final Supplier<AttachmentType<Integer>> ECHO_COLORS =
            ATTACHMENT_TYPES.register("echo_colors",
                    () -> AttachmentType.builder(() -> 0)
                            .serialize(Codec.INT)
                            .build());

    public static final Supplier<AttachmentType<Boolean>> IMMUNE =
            ATTACHMENT_TYPES.register("immune",
                    () -> AttachmentType.builder(() -> false)
                            .serialize(Codec.BOOL)
                            .build());

    public static final Supplier<AttachmentType<PlayerReflectorData>> REFLECTOR =
            ATTACHMENT_TYPES.register("reflector",
                    () -> AttachmentType.builder(() -> PlayerReflectorData.EMPTY)
                            .serialize(PlayerReflectorData.CODEC)
                            .build());

    public static final Supplier<AttachmentType<SymbiosisData>> SYMBIOSIS =
            ATTACHMENT_TYPES.register("symbiosis",
                    () -> AttachmentType.builder(() -> SymbiosisData.EMPTY)
                            .serialize(SymbiosisData.CODEC)
                            .build());

}
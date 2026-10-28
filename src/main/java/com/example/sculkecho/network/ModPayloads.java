package com.example.sculkecho.network;

import com.example.sculkecho.SculkEcho;
import com.example.sculkecho.client.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = SculkEcho.MODID)
public class ModPayloads {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToServer(
                EchoRequestPayload.TYPE,
                EchoRequestPayload.STREAM_CODEC,
                ServerEchoHandler::handle);

        registrar.playToClient(
                EchoPointsPayload.TYPE,
                EchoPointsPayload.STREAM_CODEC,
                EchoPointsHandler::handle
        );

        registrar.playToClient(
                EchoPulsePayload.TYPE,
                EchoPulsePayload.STREAM_CODEC,
                EchoPulseHandler::handle);

        registrar.playToClient(
                InfectionSyncPayload.TYPE,
                InfectionSyncPayload.STREAM_CODEC,
                ClientInfectionSyncHandler::handle
        );

        registrar.playToClient(
                PossessionPayload.TYPE,
                PossessionPayload.STREAM_CODEC,
                ClientPossessionHandler::handle);

        registrar.playToServer(
                EchoShotPayload.TYPE,
                EchoShotPayload.STREAM_CODEC,
                EchoShotHandler::handle);

        registrar.playToServer(
                RerollTradesPayload.TYPE,
                RerollTradesPayload.STREAM_CODEC,
                RerollTradesHandler::handle);
        registrar.playToClient(
                ReflectorSyncPayload.TYPE,
                ReflectorSyncPayload.STREAM_CODEC,
                ReflectorSyncHandler::handle);
        registrar.playToClient(
                SymbiosisSyncPayload.TYPE,
                SymbiosisSyncPayload.STREAM_CODEC,
                SymbiosisSyncHandler::handle);
        registrar.playToServer(
                SculkSoulPayload.TYPE,
                SculkSoulPayload.STREAM_CODEC,
                SculkSoulHandler::handle);
    }
}
package com.studiopulsar.feintha.originalfur.fabric;

import io.github.apace100.origins.Origins;
import io.github.apace100.origins.networking.ModPackets;
import io.github.apace100.origins.origin.Origin;
import mod.azure.azurelib.AzureLib;
import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.client.RenderProvider;
import mod.azure.azurelib.core.animatable.GeoAnimatable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animatable.instance.SingletonAnimatableInstanceCache;
import mod.azure.azurelib.core.animation.*;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.model.GeoModel;
import mod.azure.azurelib.renderer.GeoArmorRenderer;
import net.bettercombat.network.Packets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.client.rendering.ArmorRendererRegistryImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class OriginalFur implements ModInitializer {
    public static final String[] QUIVER_MODS = new String[]{
            "nyfsquiver"
    };
    public static final String[] BACKPACK_MODS = new String[]{
            "travelersbackpack", "umu_backpack", "packedup"
    };
    @Override
    public void onInitialize() {
        ServerPlayNetworking.registerGlobalReceiver(C2S_REQ_ORIGIN_UUID, (server, player, handler, buf, responseSender) -> {
            RequestOriginPacket packet = new RequestOriginPacket();
            packet.read(buf);
            var rqPlayer = server.getPlayerManager().getPlayer(packet.requestedPlayerUUID);
            if (rqPlayer != null){
                ArrayList<Identifier> ids = new ArrayList<>();
                ((IPlayerEntityMixins)rqPlayer).originalFur$currentOrigins().forEach(origin -> ids.add(origin.getIdentifier()));
                RequestOriginPacket response = new RequestOriginPacket();
                response.origins = ids;
                response.requestedPlayerUUID = packet.requestedPlayerUUID;
                response.requestedPlayerName = rqPlayer.getName().getString();
            } else {
                System.out.println("Player was null.. what the bingle!?");
            }
        });
    }
    @NotNull public static final Identifier S2C_REQ_ORIGIN_RESP = new Identifier("orif", "origin_response");
    @NotNull public static final Identifier C2S_REQ_ORIGIN_UUID = new Identifier("orif", "request_player_origin");
    public static class RequestOriginPacket {
        public UUID requestedPlayerUUID = new UUID(0,0);
        public String requestedPlayerName = "";
        public ArrayList<Identifier> origins = new ArrayList<>();
        public void writeSv(PacketByteBuf buf) {
            buf.writeUuid(requestedPlayerUUID);
            buf.writeString(requestedPlayerName);
            buf.writeInt(origins.size());
            origins.forEach(identifier -> {
                buf.writeString(identifier.toString());
            });
        }
        public void read(PacketByteBuf buf){
            requestedPlayerUUID = buf.readUuid();
            requestedPlayerName = buf.readString();
            var sz = buf.readInt();
            origins = new ArrayList<>();
            for (int i = 0; i < sz; i++){
                origins.add(Identifier.tryParse(buf.readString()));
            };
        }
    }
}

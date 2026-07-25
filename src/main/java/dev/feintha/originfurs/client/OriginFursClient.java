package dev.feintha.originfurs.client;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import dev.feintha.originfurs.OriginList;
import dev.feintha.originfurs.fur.FurDef;
import dev.feintha.originfurs.networking.PacketIDs;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.InvalidateRenderStateCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class OriginFursClient implements ClientModInitializer {
    public static final HashMap<UUID, OriginList> CACHED_ORIGINS = new HashMap<>();
    public static final HashSet<UUID> AWAITING_RESULT = new HashSet<>();
    public static final HashMap<Identifier, FurDef> CACHED_FURS = new HashMap<>();
    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> AWAITING_RESULT.clear());
        ClientPlayConnectionEvents.INIT.register((handler, client) -> AWAITING_RESULT.clear());
        InvalidateRenderStateCallback.EVENT.register(CACHED_FURS::clear);
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return Identifier.of("orif", "client_reload");
            }
            @Override
            public void reload(ResourceManager manager) {
                CACHED_FURS.clear();
                var resources = manager.findResources("furs", identifier -> identifier.getPath().endsWith(".json"));
                resources.forEach((identifier, resources1) -> {
                    Identifier origin_id = Identifier.of(identifier.getNamespace(), identifier.getPath().substring(0, identifier.getPath().lastIndexOf(".")).replaceFirst("furs/", ""));
                    System.out.println(origin_id);
                    try {
                        var f = FurDef.CODEC.decode(JsonOps.INSTANCE, JsonParser.parseReader(resources1.getReader())).result().orElseThrow().getFirst();
                        CACHED_FURS.put(origin_id, f);
                        System.out.println(f);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
    }
}

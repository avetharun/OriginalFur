package com.studiopulsar.feintha.originalfur.client;

import com.google.gson.JsonObject;
import mod.azure.azurelib.cache.object.*;
import mod.azure.azurelib.model.GeoModel;
import mod.azure.azurelib.renderer.GeoObjectRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;

import java.util.LinkedHashMap;

public class OriginalFurClient implements ClientModInitializer {
    public static class OriginFur extends GeoObjectRenderer<OriginFurAnimatable> {
        public void setPlayer(PlayerEntity e) {
            this.animatable.setPlayer(e);
        }

        public OriginFur(JsonObject json) {
            super(new OriginFurModel(json));
            this.animatable = new OriginFurAnimatable();
        }

    }

    public static LinkedHashMap<String, OriginFur> FUR_REGISTRY = new LinkedHashMap<>();
    public static LinkedHashMap<Identifier, Resource> FUR_RESOURCES = new LinkedHashMap<>();
    @Override
    public void onInitializeClient() {

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
            @Override
            public Identifier getFabricId() {
                return new Identifier("originalfur", "furs");
            }

            final String r_M = "\\/([A-Za-z0-9_.-]+)\\.json";
            @Override
            public void reload(ResourceManager manager) {
                var resources = manager.findResources("furs", identifier -> identifier.getPath().endsWith(".json"));
                for (var res : resources.keySet()) {
                    System.out.println(res);
                    String itemName = res.getPath().replaceAll(".*/(.*?)\\.json", "$1");
                    Identifier id = new Identifier("origin", itemName);
                    FUR_RESOURCES.put(id, resources.get(res));
                }
            }
        });
        BakedGeoModel m;
    }
}

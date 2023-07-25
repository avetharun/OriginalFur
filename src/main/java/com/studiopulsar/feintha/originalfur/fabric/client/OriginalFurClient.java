package com.studiopulsar.feintha.originalfur.fabric.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.studiopulsar.feintha.originalfur.OriginFurAnimatable;
import com.studiopulsar.feintha.originalfur.fabric.OriginFurModel;
import mod.azure.azurelib.cache.object.*;
import mod.azure.azurelib.renderer.GeoObjectRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.LinkedHashMap;

public class OriginalFurClient implements ClientModInitializer {
    public static class OriginFur extends GeoObjectRenderer<OriginFurAnimatable> {

        public void setPlayer(PlayerEntity e) {
            this.animatable.setPlayer(e);
        }

        public OriginFur(JsonObject json) {
            super(new OriginFurModel(json));
            this.animatable = new OriginFurAnimatable();
//            this.addRenderLayer(new AutoGlowingGeoLayer<>(this) {
//                @Override
//                public GeoModel<OriginFurAnimatable> getGeoModel() {
//                    return OriginFur.this.getGeoModel();
//                }
//
//                @Override
//                protected RenderLayer getRenderType(OriginFurAnimatable animatable) {
//                    return RenderLayer.getEntityCutout(getTextureResource(animatable));
//                }
//
//                @Override
//                protected Identifier getTextureResource(OriginFurAnimatable animatable) {
//                    OriginFurModel gM = (OriginFurModel) OriginFur.this.getGeoModel();
//                    return gM.getFullbrightTextureResource(animatable);
//                }
//            });
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
                    String itemName = res.getPath().replaceAll(".*/(.*?)\\.json", "$1");
                    Identifier id = new Identifier("origin", itemName);
                    if (FUR_REGISTRY.containsKey(id.getPath())) {
                        OriginFurModel m = (OriginFurModel) FUR_REGISTRY.get(id.getPath()).getGeoModel();
                        try {
                            m.recompile(JsonParser.parseString(new String(resources.get(res).getInputStream().readAllBytes())).getAsJsonObject());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        FUR_RESOURCES.put(id, resources.get(res));
                    }
                }
            }
        });
        BakedGeoModel m;
    }
}

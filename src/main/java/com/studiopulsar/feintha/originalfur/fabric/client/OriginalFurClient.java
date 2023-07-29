package com.studiopulsar.feintha.originalfur.fabric.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.studiopulsar.feintha.originalfur.OriginFurAnimatable;
import com.studiopulsar.feintha.originalfur.fabric.OriginFurModel;
import mod.azure.azurelib.cache.object.*;
import mod.azure.azurelib.renderer.GeoObjectRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.util.Arrays;
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
    public static boolean isRenderingInWorld = false;

    public static LinkedHashMap<Identifier, OriginFur> FUR_REGISTRY = new LinkedHashMap<>();
    public static LinkedHashMap<Identifier, Resource> FUR_RESOURCES = new LinkedHashMap<>();
    @Override
    public void onInitializeClient() {
        WorldRenderEvents.END.register(context -> isRenderingInWorld = false);
        WorldRenderEvents.START.register(context -> isRenderingInWorld = true);
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
                    String itemName = res.getPath().substring(res.getPath().indexOf('/')+1, res.getPath().lastIndexOf('.'));
                    Identifier id = new Identifier("origins", itemName);
                    var p = itemName.split("\\.");
                    if (p.length > 1) {
                        id = Identifier.of(p[0], p[1]);
                    }
                    if (FUR_REGISTRY.containsKey(id)) {
                        OriginFurModel m = (OriginFurModel) FUR_REGISTRY.get(id).getGeoModel();
                        try {
                            m.recompile(JsonParser.parseString(new String(resources.get(res).getInputStream().readAllBytes())).getAsJsonObject());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        FUR_RESOURCES.put(id, resources.get(res));
                    }
                    System.out.println(id);
                }
                System.out.println(FUR_RESOURCES.keySet());
            }
        });
    }
}

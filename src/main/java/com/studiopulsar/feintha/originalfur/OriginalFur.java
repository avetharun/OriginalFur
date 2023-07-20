package com.studiopulsar.feintha.originalfur;

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
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.impl.client.rendering.ArmorRendererRegistryImpl;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class OriginalFur implements ModInitializer {

//    public static final Registry<OriginalFurRenderer> ORIGIN_RENDERERS = FabricRegistryBuilder.createSimple(OriginalFurRenderer.class, Identifier.of("originalfur", "renderers")).buildAndRegister();
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        AzureLib.initialize();
    }
}

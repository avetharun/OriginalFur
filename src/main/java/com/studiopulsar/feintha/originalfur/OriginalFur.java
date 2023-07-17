package com.studiopulsar.feintha.originalfur;

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
import net.fabricmc.fabric.impl.client.rendering.ArmorRendererRegistryImpl;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class OriginalFur implements ModInitializer {
    public static class OriginalFurRenderer extends GeoArmorRenderer<OriginalFurArmorItem> {
        @Override
        public void applyBoneVisibilityByPart(EquipmentSlot currentSlot, ModelPart currentPart, BipedEntityModel<?> model) {}
        @Override
        protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {}

        private OriginalFurRenderer() {
            super(new GeoModel<OriginalFurArmorItem>() {
                @Override
                public Identifier getModelResource(OriginalFurArmorItem originalFurArmorItem) {
                    return new Identifier("originalfur", "geo/missing_model.geo.json");
                }

                @Override
                public Identifier getTextureResource(OriginalFurArmorItem originalFurArmorItem) {
                    return new Identifier("originalfur", "textures/no_origin_model_provided.png");
                }

                @Override
                public Identifier getAnimationResource(OriginalFurArmorItem originalFurArmorItem) {
                    return new Identifier("originalfur", "geo/missing_model.animation.json");
                }
            });
        }
        public OriginalFurRenderer(GeoModel<OriginalFurArmorItem> model) {
            super(model);
        }
        public Origin associated_origin;
    }

    @SuppressWarnings("rawtypes")
    private static class OriginalFurArmorItem extends ArmorItem implements GeoItem, GeoAnimatable {
        public final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
        public final Supplier<Object> rendererProvider = GeoItem.makeRenderer(this);
        public OriginalFurArmorItem(ArmorMaterial material, Type type, Settings settings) {
            super(material, type, settings);
        }

        @Override
        public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
            controllerRegistrar.add( new AnimationController<>(this, "controller", this::predicate));
        }
        private PlayState predicate(AnimationState s) {
            s.getController().setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }

        @Override
        public AnimatableInstanceCache getAnimatableInstanceCache() {
            return cache;
        }

        @Override
        public void createRenderer(Consumer<Object> consumer) {
            consumer.accept(new RenderProvider() {
                private OriginalFurRenderer renderer;
                @Override
                public BipedEntityModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, BipedEntityModel<LivingEntity> original) {
                    if (this.renderer == null) {
                        this.renderer = new OriginalFurRenderer();
                    }
                    this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                    return this.renderer;
                }
            });
        }

        @Override
        public Supplier<Object> getRenderProvider() {
            return this.rendererProvider;
        }
    }
    public static final ArmorItem ORIGINAL_FUR_ARMOR_HOLDER = new ArmorItem(ArmorMaterials.LEATHER, ArmorItem.Type.BOOTS, new FabricItemSettings());
//    public static final Registry<OriginalFurRenderer> ORIGIN_RENDERERS = FabricRegistryBuilder.createSimple(OriginalFurRenderer.class, Identifier.of("originalfur", "renderers")).buildAndRegister();
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        AzureLib.initialize();

    }
}

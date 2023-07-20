package com.studiopulsar.feintha.originalfur.mixin;


import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.studiopulsar.feintha.originalfur.IPlayerEntityMixins;
import com.studiopulsar.feintha.originalfur.ModelRootAccessor;
import com.studiopulsar.feintha.originalfur.alib;
import com.studiopulsar.feintha.originalfur.client.FurRenderFeature;
import com.studiopulsar.feintha.originalfur.client.OriginFurModel;
import com.studiopulsar.feintha.originalfur.client.OriginalFurClient;
import io.github.apace100.apoli.mixin.DamageSourceMixin;
import io.github.apace100.apoli.power.ModelColorPower;
import io.github.apace100.origins.component.PlayerOriginComponent;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {
    @Mixin(PlayerEntityModel.class)
    public static abstract class PlayerEntityModel$RootModel$Mixin implements ModelRootAccessor, IPlayerEntityMixins{
        @Shadow @Final private boolean thinArms;


        @Unique
        ModelPart root;
        @Unique
        float elytraPitch = 0;
        @Unique
        boolean justStartedFlying = false;

        @Override
        public boolean originalFur$justUsedElytra() {
            return justStartedFlying;
        }

        @Override
        public float originalFur$elytraPitch() {
            return elytraPitch;
        }

        @Override
        public void originalFur$setElytraPitch(float f) {
            elytraPitch = f;
        }

        @Override
        public void originalFur$setJustUsedElytra(boolean b) {
            justStartedFlying = b;
        }

        @Inject(method="<init>", at=@At("TAIL"))
        void initMixin(ModelPart root, boolean thinArms, CallbackInfo ci){
            this.root = root;

        }

        @Override
        public ModelPart originalFur$getRoot() {
            return root;
        }
        @Unique
        boolean proc_slim = false;
        @Override
        public boolean originalFur$hasProcessedSlim() {
            return proc_slim;
        }

        @Override
        public void originalFur$setProcessedSlim(boolean state) {
            proc_slim = state;
        }

        @Override
        public boolean originalFur$isSlim() {
            return thinArms;
        }
    }
//    @Inject(method="render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at=@At("TAIL"))
//    private void renderMixin(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
////        OriginalFurClient.render(abstractClientPlayerEntity,0,0,matrixStack,vertexConsumerProvider,i);
//    }
    @Inject(method="<init>", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/PlayerEntityRenderer;addFeature(Lnet/minecraft/client/render/entity/feature/FeatureRenderer;)Z", ordinal = 1, shift = At.Shift.BEFORE))
    void initMixin(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        PlayerEntityRenderer r = (PlayerEntityRenderer) (Object)this;
        var c = LivingEntityRenderer.class;
        var m = c.getDeclaredMethod("addFeature", FeatureRenderer.class);
        m.setAccessible(true);
        m.invoke(r, new FurRenderFeature<>(r));
    }
    @Mixin(LivingEntityRenderer.class)
    public static abstract class LivingEntityRendererMixin$HidePlayerModelIfNeeded <T extends LivingEntity, M extends EntityModel<T>> implements IPlayerEntityMixins {
        @Shadow @Final protected List<FeatureRenderer<T, M>> features;

        @Shadow public abstract M getModel();

        @Unique
        boolean isInvisible = false;
        @Override
        public boolean originalFur$isPlayerInvisible() {
            return isInvisible;
        }

        @Inject(method="render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
                at=@At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                shift = At.Shift.BEFORE))
        private void renderPreProcessMixin(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
            if (livingEntity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
                IPlayerEntityMixins m = this;
                isInvisible = false;
                PlayerOriginComponent c = (PlayerOriginComponent) ModComponents.ORIGIN.get(abstractClientPlayerEntity);
                var mod = (PlayerEntityModel)this.getModel();
//                Iterable<ModelPart> headParts = alib.runPrivateMixinMethod(mod, "getHeadParts");
//                Iterable<ModelPart> bodyParts = alib.runPrivateMixinMethod(mod, "getBodyParts");

                for (var layer : OriginLayers.getLayers()) {
                    var origin = c.getOrigin(layer);
                    MinecraftClient.getInstance().getProfiler().push("originalfurs:" + origin.getIdentifier().getPath());
                    Identifier id = origin.getIdentifier();
                    var opt = OriginalFurClient.FUR_REGISTRY.get(id.getPath());
                    if (opt == null) {return;}
                    if (((OriginFurModel) opt.getGeoModel()).isPlayerModelInvisible()) {
                        isInvisible = true;
                        matrixStack.translate(0,9999,0);
                        return;
                    } else {
                        isInvisible = false;
                    }
                }
            }
        }
        @Inject(method="render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
                at=@At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
                        shift = At.Shift.AFTER))
        private void renderPostProcessMixin(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
            if (isInvisible) {
                matrixStack.translate(0,-9999,0);
            }
        }

//        @Inject(method="render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
//        at=@At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/model/EntityModel;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;IIFFFF)V",
//        shift = At.Shift.AFTER))
//        void postRenderMixin(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
//            if (livingEntity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity && isInvisible) {
//            }
//        }
    }
}

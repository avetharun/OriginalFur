package com.studiopulsar.feintha.originalfur.fabric.client;

import com.studiopulsar.feintha.originalfur.alib;
import com.studiopulsar.feintha.originalfur.fabric.AbstractClientPlayerEntityCompatMixins;
import com.studiopulsar.feintha.originalfur.fabric.OriginFurModel;
import com.studiopulsar.feintha.originalfur.fabric.IPlayerEntityMixins;
import com.studiopulsar.feintha.originalfur.fabric.ModelRootAccessor;
import dev.kosmx.playerAnim.api.IPlayer;
import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.PlayerAnimationFrame;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.IAnimatedPlayer;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.origins.component.PlayerOriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.registry.ModComponents;
import io.github.apace100.originsclasses.OriginsClasses;
import io.github.kosmx.bendylib.impl.IBendable;
import mod.azure.azurelib.core.animatable.model.CoreGeoBone;
import net.bettercombat.BetterCombat;
import net.bettercombat.api.AttackHand;
import net.bettercombat.api.EntityPlayer_BetterCombat;
import net.bettercombat.api.MinecraftClient_BetterCombat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.ObjectUtils;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.transformer.meta.MixinMerged;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class FurRenderFeature <T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public FurRenderFeature(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Unique
    private int getOverlayMixin(LivingEntity entity, float whiteOverlayProgress) {
        return OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(entity.hurtTime > 0 || entity.deathTime > 0));
    }
    public static class ModelTransformation {
        public Vec3d position, rotation;
        public ModelTransformation(Vec3f pos, Vec3f rot) {
            this.position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
            this.rotation = new Vec3d(rot.getX(), rot.getY(), rot.getZ());
        }
        public ModelTransformation(IAnimation anim, String bone_name) {
            Vec3f pos = anim.get3DTransform(bone_name, TransformType.POSITION, MinecraftClient.getInstance().getTickDelta(), new Vec3f(0,0,0));
            Vec3f rot = anim.get3DTransform(bone_name, TransformType.ROTATION, MinecraftClient.getInstance().getTickDelta(), new Vec3f(0,0,0));
            this.position = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
            this.rotation = new Vec3d(rot.getX(), rot.getY(), rot.getZ());
        }
        public ModelTransformation invert(boolean x, boolean y, boolean z) {
            this.rotation = this.rotation.multiply(x ? -1 : 1, y ? -1 : 1, z ? -1 : 1);
            return this;
        }
        public ModelTransformation invert(boolean i) {
            this.rotation = this.rotation.multiply(i ? -1 : 1);
            return this;
        }
    }
    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
            if (abstractClientPlayerEntity.isInvisible() || abstractClientPlayerEntity.isSpectator()) {return;}
            PlayerOriginComponent c = (PlayerOriginComponent) ModComponents.ORIGIN.get(abstractClientPlayerEntity);
            var iPEM = (IPlayerEntityMixins) abstractClientPlayerEntity;
            OriginalFurClient.OriginFur fur = iPEM.originalFur$getCurrentFur();
            if (fur == null){return;}
            Origin o = fur.currentAssociatedOrigin;
            if (o == null) {return;}
            var eR = (PlayerEntityRenderer)MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(abstractClientPlayerEntity);
            var eRA = (IPlayerEntityMixins) eR;
            var acc = (ModelRootAccessor)eR.getModel();
            var a = fur.getAnimatable();
            OriginFurModel m = (OriginFurModel) fur.getGeoModel();
            Origin finalO = o;
            m.getAnimationProcessor().getRegisteredBones().forEach(coreGeoBone -> {
                if (((IGeoBone)coreGeoBone).originfurs$isHiddenByDefault()) {
                    return;
                }
                m.preprocess(finalO, eR, eRA, acc, abstractClientPlayerEntity);
            });
            fur.setPlayer(abstractClientPlayerEntity);
            var lAP = eR.getModel().leftArmPose;
            var rAP = eR.getModel().rightArmPose;
            for (int i = 0; i < 2; i++) {
                matrixStack.push();
                matrixStack.multiply(new Quaternionf().rotateX(180 * MathHelper.RADIANS_PER_DEGREE));
//                matrixStack.multiply(new Quaternionf().rotateY((-abstractClientPlayerEntity.getYaw(tickDelta)) * MathHelper.RADIANS_PER_DEGREE));
                matrixStack.translate(0, -1.51f, 0);
                MinecraftClient.getInstance().getProfiler().push("copy_mojmap");
//
                m.resetBone("bipedHead");
                m.resetBone("bipedBody");
                m.resetBone("bipedLeftArm");
                m.resetBone("bipedRightArm");
                m.resetBone("bipedLeftLeg");
                m.resetBone("bipedRightLeg");

                m.setRotationForBone("bipedHead", ((IMojModelPart)(Object)eR.getModel().head).originfurs$getRotation());
                m.translatePositionForBone("bipedHead", ((IMojModelPart)(Object)eR.getModel().head).originfurs$getPosition().multiply(-16f));
                m.translatePositionForBone("bipedBody", ((IMojModelPart)(Object)eR.getModel().body).originfurs$getPosition().multiply(-16f));
                m.translatePositionForBone("bipedLeftArm", ((IMojModelPart)(Object)eR.getModel().leftArm).originfurs$getPosition().multiply(-16f));
                m.translatePositionForBone("bipedRightArm", ((IMojModelPart)(Object)eR.getModel().rightArm).originfurs$getPosition().multiply(-16f));
                m.translatePositionForBone("bipedLeftLeg", ((IMojModelPart)(Object)eR.getModel().rightLeg).originfurs$getPosition().multiply(-16f));
                m.translatePositionForBone("bipedRightLeg", ((IMojModelPart)(Object)eR.getModel().leftLeg).originfurs$getPosition().multiply(-16f));
                m.translatePositionForBone("bipedLeftArm", new Vec3d(5,2,0));
                m.translatePositionForBone("bipedRightArm", new Vec3d(-5,2,0));
//                m.translatePositionForBone("bipedLeftLeg", new Vec3d(-1.9999,11.98,0.02));
                m.translatePositionForBone("bipedLeftLeg", new Vec3d(-2,12,0));
                m.translatePositionForBone("bipedRightLeg", new Vec3d(2,12,0));
//                PlayerEntityModel
//                System.out.println(m.getPositionForBone("bipedRightArm").scale(16));
//                MinecraftClient.getInstance().getProfiler().pop();
//                MinecraftClient.getInstance().getProfiler().push("transform_manual");
//                boolean allowSneakingPose = true, translateVanilla = true;
//
//                MinecraftClient.getInstance().getProfiler().pop();
                matrixStack.translate(-0.5, -0.5, -0.5);
                m.setRotationForBone("bipedBody", ((IMojModelPart)(Object)eR.getModel().body).originfurs$getRotation());
                m.invertRotForPart("bipedBody", false, true, false);
                m.setRotationForBone("bipedLeftArm", ((IMojModelPart)(Object)eR.getModel().leftArm).originfurs$getRotation());
                m.setRotationForBone("bipedRightArm", ((IMojModelPart)(Object)eR.getModel().rightArm).originfurs$getRotation());
                m.setRotationForBone("bipedLeftLeg", ((IMojModelPart)(Object)eR.getModel().rightLeg).originfurs$getRotation());
                m.setRotationForBone("bipedRightLeg", ((IMojModelPart)(Object)eR.getModel().leftLeg).originfurs$getRotation());
                m.invertRotForPart("bipedHead", false, true, true);
                m.invertRotForPart("bipedRightArm", false, true, true);
                m.invertRotForPart("bipedLeftArm", false, true, true);
                m.invertRotForPart("bipedRightLeg", false, true, true);
                m.invertRotForPart("bipedLeftLeg", false, true, true);
                MinecraftClient.getInstance().getProfiler().push("render");
                if (i == 0) {
                    fur.render(matrixStack, a, vertexConsumerProvider, RenderLayer.getEntityTranslucent(m.getTextureResource(a)), null, light);
                } else {
                    fur.render(matrixStack, a, vertexConsumerProvider, RenderLayer.getEntityTranslucentEmissive(m.getFullbrightTextureResource(a)), null, Integer.MAX_VALUE - 1);
                }

                MinecraftClient.getInstance().getProfiler().pop();
                MinecraftClient.getInstance().getProfiler().pop();
//                m.popScl("bipedLeftLeg");
//                m.popScl("bipedRightLeg");
                matrixStack.pop();
            }
        }
        MinecraftClient.getInstance().getProfiler().pop();
    }
}

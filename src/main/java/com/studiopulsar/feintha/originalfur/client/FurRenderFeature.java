package com.studiopulsar.feintha.originalfur.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.studiopulsar.feintha.originalfur.IPlayerEntityMixins;
import com.studiopulsar.feintha.originalfur.ModelRootAccessor;
import io.github.apace100.origins.component.PlayerOriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

public class FurRenderFeature <T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public FurRenderFeature(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        if (entity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {
            if (abstractClientPlayerEntity.isInvisible() || abstractClientPlayerEntity.isSpectator()) {return;}
            PlayerOriginComponent c = (PlayerOriginComponent) ModComponents.ORIGIN.get(abstractClientPlayerEntity);
            Origin o = null;
            OriginalFurClient.OriginFur fur = null;
            for (var layer : OriginLayers.getLayers()) {
                var origin = c.getOrigin(layer);
                Identifier id = origin.getIdentifier();
                var opt = OriginalFurClient.FUR_REGISTRY.get(id.getPath());
                if (opt == null) {
                    return;
                }
                if (c.hasOrigin(layer)) {
                    o = origin;
                    fur = opt;
                    break;
                }
            }
            if (o == null) {return;}
            var eR = (PlayerEntityRenderer)MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(abstractClientPlayerEntity);
            var eRA = (IPlayerEntityMixins) eR;

            var acc = (ModelRootAccessor)eR.getModel();
            var a = fur.getAnimatable();
            OriginFurModel m = (OriginFurModel) fur.getGeoModel();
            m.getAnimationProcessor().getRegisteredBones().forEach(coreGeoBone -> {
                if (coreGeoBone.getName().endsWith("thin_only") && !acc.originalFur$isSlim()) {
                    coreGeoBone.setHidden(true);
                } else coreGeoBone.setHidden(coreGeoBone.getName().endsWith("wide_only") && acc.originalFur$isSlim());
            });

            fur.setPlayer(abstractClientPlayerEntity);
            var lAP = eR.getModel().leftArmPose;
            var rAP = eR.getModel().rightArmPose;
            matrixStack.push();
            matrixStack.multiply(new Quaternionf().rotateX(180 * MathHelper.RADIANS_PER_DEGREE));
            matrixStack.translate(0,-1.51f,0);
            m.resetBone("bipedHead");
            m.resetBone("bipedBody");
            m.resetBone("bipedLeftArm");
            m.resetBone("bipedRightArm");
            m.resetBone("bipedLeftArm");
            m.resetBone("bipedRightArm");
            m.resetBone("bipedLeftLeg");
            m.resetBone("bipedRightLeg");
            MinecraftClient.getInstance().getProfiler().push("copy_mojmap");
            m.copyRotFromMojangModelPart("bipedHead", eR.getModel().head, true, false, false);
            m.copyRotFromMojangModelPart("bipedBody", eR.getModel().body);
            m.copyPosFromMojangModelPart("bipedLeftArm", eR.getModel().rightArm);
            m.copyPosFromMojangModelPart("bipedRightArm", eR.getModel().leftArm);
            m.copyRotFromMojangModelPart("bipedLeftArm", eR.getModel().leftArm, true, false, false);
            m.copyRotFromMojangModelPart("bipedRightArm", eR.getModel().rightArm, true, false, false);
            // For some reason, these two are inverted!
            m.copyRotFromMojangModelPart("bipedRightLeg", eR.getModel().rightLeg);
            m.copyRotFromMojangModelPart("bipedLeftLeg", eR.getModel().leftLeg);
            m.translatePositionForBone("bipedRightArm", new Vec3d(-5, -2, 0));
            m.translatePositionForBone("bipedLeftArm", new Vec3d(5, -2, 0));
            m.setScaleForBone("bipedRightLeg", new Vec3d(0.995, 0.99, 0.995));
//            m.translateRotationForBone("bipedRightLeg", new Vec3d(0,-.5 * MathHelper.RADIANS_PER_DEGREE, 0));
//            m.translatePositionForBone("bipedLeftLeg", new Vec3d(0, 0, -.05));
//            m.translatePositionForBone("bipedRightLeg", new Vec3d(0, 0, -.05));
            MinecraftClient.getInstance().getProfiler().pop();
            MinecraftClient.getInstance().getProfiler().push("transform_manual");
            if (abstractClientPlayerEntity.isInSneakingPose()) {
                m.copyRotFromMojangModelPart("bipedBody", eR.getModel().body, true, false, false);
                m.translatePositionForBone("bipedBody", new Vec3d(0,-3.2, 0));
                m.translatePositionForBone("bipedHead", new Vec3d(0,-4.2,0));
                m.translatePositionForBone("bipedLeftArm", new Vec3d(0,-6.4,0));
                m.translatePositionForBone("bipedRightArm", new Vec3d(0,-6.4,0));
                m.translatePositionForBone("bipedRightLeg", new Vec3d(0,-0.2,-4));
                m.translatePositionForBone("bipedLeftLeg", new Vec3d(0,-0.2,-4));
            }

            MinecraftClient.getInstance().getProfiler().pop();
            matrixStack.translate(-0.5,-0.5,-0.5);
            MinecraftClient.getInstance().getProfiler().push("render");
//            RenderSystem.setColor
            fur.render(matrixStack, a, vertexConsumerProvider, RenderLayer.getEntityCutout(fur.getTextureLocation(a)), null, light);
            MinecraftClient.getInstance().getProfiler().pop();
            MinecraftClient.getInstance().getProfiler().pop();
            matrixStack.pop();
        };
        MinecraftClient.getInstance().getProfiler().pop();
    }
}

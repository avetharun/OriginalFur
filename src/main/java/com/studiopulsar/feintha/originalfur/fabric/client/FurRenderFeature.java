package com.studiopulsar.feintha.originalfur.fabric.client;

import com.studiopulsar.feintha.originalfur.fabric.OriginFurModel;
import com.studiopulsar.feintha.originalfur.fabric.IPlayerEntityMixins;
import com.studiopulsar.feintha.originalfur.fabric.ModelRootAccessor;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.origins.component.PlayerOriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.registry.ModComponents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Unique;

public class FurRenderFeature <T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public FurRenderFeature(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Unique
    private int getOverlayMixin(LivingEntity entity, float whiteOverlayProgress) {
        return OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(entity.hurtTime > 0 || entity.deathTime > 0));
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
                if (origin == null) {continue;}
                Identifier id = origin.getIdentifier();
                var opt = OriginalFurClient.FUR_REGISTRY.get(id.getPath());
                if (opt == null) {
                    continue;
                }
                if (c.hasOrigin(layer)) {
                    o = origin;
                    fur = opt;
                    break;
                }
            }
            if (o == null) {return;}
//            if (fur == null) {return;}
            var eR = (PlayerEntityRenderer)MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(abstractClientPlayerEntity);
            var eRA = (IPlayerEntityMixins) eR;
            var acc = (ModelRootAccessor)eR.getModel();
            assert fur != null;
            var a = fur.getAnimatable();
            OriginFurModel m = (OriginFurModel) fur.getGeoModel();
            Origin finalO = o;
            m.getAnimationProcessor().getRegisteredBones().forEach(coreGeoBone -> {
                coreGeoBone.setHidden(false);
                coreGeoBone.setHidden(coreGeoBone.getName().endsWith("thin_only") && !acc.originalFur$isSlim());
                if (coreGeoBone.isHidden()) {return;}
                coreGeoBone.setHidden(coreGeoBone.getName().endsWith("wide_only") && acc.originalFur$isSlim());
                if (coreGeoBone.isHidden()) {return;}
                coreGeoBone.setHidden(coreGeoBone.getName().endsWith("elytra_hides") &&
                        (finalO.hasPowerType(PowerTypeRegistry.get(new Identifier("origins:elytra")))
                                || abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA)));
                if (coreGeoBone.isHidden()) {return;}
                coreGeoBone.setHidden(coreGeoBone.getName().endsWith("helmet_hides") && !abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.HEAD).isEmpty());
                if (coreGeoBone.isHidden()) {return;}
                coreGeoBone.setHidden(coreGeoBone.getName().endsWith("chestplate_hides") && !abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.CHEST).isEmpty());
                if (coreGeoBone.isHidden()) {return;}
                coreGeoBone.setHidden(coreGeoBone.getName().endsWith("leggings_hides") && !abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.LEGS).isEmpty());
                if (coreGeoBone.isHidden()) {return;}
                coreGeoBone.setHidden(coreGeoBone.getName().endsWith("boot_hides") && !abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.FEET).isEmpty());
            });
            fur.setPlayer(abstractClientPlayerEntity);
            var lAP = eR.getModel().leftArmPose;
            var rAP = eR.getModel().rightArmPose;
            for (int i = 0; i < 2; i++) {

                matrixStack.push();
                matrixStack.multiply(new Quaternionf().rotateX(180 * MathHelper.RADIANS_PER_DEGREE));
                matrixStack.translate(0, -1.51f, 0);
                m.resetBone("bipedHead");
                m.resetBone("bipedBody");
                m.resetBone("bipedLeftArm");
                m.resetBone("bipedRightArm");
                m.resetBone("bipedLeftArm");
                m.resetBone("bipedRightArm");
                m.resetBone("bipedLeftLeg");
                m.resetBone("bipedRightLeg");
                m.setScaleForBone("bipedLeftLeg", new Vec3d(1.075f, 1.01f, 1.075f));
                m.setScaleForBone("bipedRightLeg", new Vec3d(1.075f, 1.01f, 1.075f));
                MinecraftClient.getInstance().getProfiler().push("copy_mojmap");
                m.copyRotFromMojangModelPart("bipedHead", eR.getModel().head, true, false, false);
                m.copyRotFromMojangModelPart("bipedBody", eR.getModel().body);
                m.copyPosFromMojangModelPart("bipedLeftArm", eR.getModel().rightArm);
                m.copyPosFromMojangModelPart("bipedRightArm", eR.getModel().leftArm);
                m.copyRotFromMojangModelPart("bipedLeftArm", eR.getModel().leftArm, true, false, false);
                m.copyRotFromMojangModelPart("bipedRightArm", eR.getModel().rightArm, true, false, false);
                m.copyRotFromMojangModelPart("bipedRightLeg", eR.getModel().rightLeg);
                m.copyRotFromMojangModelPart("bipedLeftLeg", eR.getModel().leftLeg);
                m.translatePositionForBone("bipedRightArm", new Vec3d(-5, -2, 0));
                m.translatePositionForBone("bipedLeftArm", new Vec3d(5, -2, 0));
                m.copyPosFromMojangModelPart("bipedLeftLeg", eR.getModel().leftLeg);
                m.copyPosFromMojangModelPart("bipedRightLeg", eR.getModel().rightLeg);
                m.translatePositionForBone("bipedLeftLeg", new Vec3d(-2.3,-12,0));
                m.translatePositionForBone("bipedRightLeg", new Vec3d(2.3,-12,0));
                MinecraftClient.getInstance().getProfiler().pop();
                MinecraftClient.getInstance().getProfiler().push("transform_manual");
                if (abstractClientPlayerEntity.isInSneakingPose()) {
                    m.copyRotFromMojangModelPart("bipedBody", eR.getModel().body, true, false, false);
                    m.translatePositionForBone("bipedBody", new Vec3d(0, -3.2, 0));
                    m.translatePositionForBone("bipedHead", new Vec3d(0, -4.2, 0));
                    m.translatePositionForBone("bipedLeftArm", new Vec3d(0, -6.4, 0));
                    m.translatePositionForBone("bipedRightArm", new Vec3d(0, -6.4, 0));
                    m.translatePositionForBone("bipedRightLeg", new Vec3d(0, -0.3, -8));
                    m.translatePositionForBone("bipedLeftLeg", new Vec3d(0, -0.3, -8));
                }

                MinecraftClient.getInstance().getProfiler().pop();
                matrixStack.translate(-0.5, -0.5, -0.5);
                MinecraftClient.getInstance().getProfiler().push("render");
                if (i == 0) {
                    fur.render(matrixStack, a, vertexConsumerProvider, RenderLayer.getEntityCutout(fur.getTextureLocation(a)), null, light);
                } else {
                    fur.render(matrixStack, a, vertexConsumerProvider, RenderLayer.getEntityTranslucentEmissive(m.getFullbrightTextureResource(a)), null, Integer.MAX_VALUE - 1);
                }

                MinecraftClient.getInstance().getProfiler().pop();
                MinecraftClient.getInstance().getProfiler().pop();
                m.popScl("bipedLeftLeg");
                m.popScl("bipedRightLeg");
                matrixStack.pop();

            }
        };
        MinecraftClient.getInstance().getProfiler().pop();
    }
}

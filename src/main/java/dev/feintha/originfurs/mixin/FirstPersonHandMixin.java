package dev.feintha.originfurs.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import dev.feintha.originfurs.client.OriginFursClient;
import dev.feintha.originfurs.fur.FurDef;
import dev.feintha.originfurs.fur.FurPartTypes;
import dev.feintha.originfurs.fur.IMojModelPart;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(PlayerEntityRenderer.class)
public class FirstPersonHandMixin {
    @WrapWithCondition(method = "renderArm", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V", ordinal = 0),
    slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V", ordinal = 0), to = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V", ordinal = 1)))
    boolean renderArmMixin(ModelPart instance, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, @Local(argsOnly = true) AbstractClientPlayerEntity player, @Local(argsOnly = true) VertexConsumerProvider vertexConsumers) {
        var self = (PlayerEntityRenderer)(Object)this;
        boolean left = self.getModel().leftArm == instance || self.getModel().leftSleeve == instance;
        boolean isSleeve = self.getModel().leftSleeve == instance || self.getModel().rightSleeve == instance;
        List<FurDef> furs = new ArrayList<>();
        ModComponents.ORIGIN.get(player).getOrigins().values().forEach(origin -> {
            if (!isSleeve) {
                var model = OriginFursClient.CACHED_MODELS.getOrDefault(origin.getIdentifier(), null);
                if (model != null) {
                    final String boneName = left ? "bipedLeftArm" : "bipedRightArm";
                    model.getBone(boneName).ifPresent(b -> {
                        if (b == null) return;
                        model.resetBone(Optional.of(b));
                        var rl_main = RenderLayer.getEntityCutoutNoCull(model.getTextureResource(model));
                        if (model.getEmissiveTextureResource(model) != null) {
                            var rl_emissive = RenderLayer.getEntityTranslucentEmissive(model.getEmissiveTextureResource(model));
                        }
                        matrices.push();
                        model.setRotationForBone(boneName, ((IMojModelPart) (Object) instance).originfurs$getRotation());
                        model.translatePositionForBone(boneName, ((IMojModelPart) (Object) instance).originfurs$getPosition());
                        model.invertRotForPart(boneName, false, true, true);
                        model.translatePositionForBone(boneName, new Vec3d(5 * (left ? 1 : -1), 10, 0));
                        model.updateAnimatedTextureFrame(model);
                        model.getBakedModel(model.getModelResource(model));
                        matrices.multiply(new Quaternionf().rotateX(180 * MathHelper.RADIANS_PER_DEGREE));
                        matrices.translate(0,-2,0);
                        model.renderRecursively(matrices, model, b, rl_main, vertexConsumers, vertexConsumers.getBuffer(rl_main), false, 0, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
                        model.resetBone(Optional.of(b));
                        matrices.pop();
                    });
                }
            }
            var fur = OriginFursClient.CACHED_FURS.getOrDefault(origin.getIdentifier(), null);

            if (fur != null) {
                furs.add(fur);
                if (instance.visible) {

                    if (isSleeve) {
                        instance.visible = !(fur.hiddenParts().contains(left ? FurPartTypes.leftArm : FurPartTypes.rightArm));
                    } else {
                        instance.visible = !(fur.hiddenParts().contains(left ? FurPartTypes.leftSleeve : FurPartTypes.rightSleeve));
                    }
                }
            }
        });
        instance.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
        for (FurDef fur : furs) {
            if (instance.visible) {
                if (fur.overlay().isPresent()) {
                    instance.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(fur.overlay().get())), light, OverlayTexture.DEFAULT_UV);
                }
                if (fur.emissive_overlay().isPresent()) {
                    instance.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucentEmissive(fur.emissive_overlay().get())), light, OverlayTexture.DEFAULT_UV);
                }
            }
        }
        return false;
    }
}

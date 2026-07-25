package dev.feintha.originfurs.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import dev.feintha.originfurs.client.OriginFursClient;
import dev.feintha.originfurs.fur.*;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(PlayerEntityRenderer.class)
public class FirstPersonHandMixin {
    @WrapWithCondition(method = "renderArm", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"),
    slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V", ordinal = 0), to = @At(value = "TAIL")))
    boolean renderArmMixin(ModelPart instance, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, @Local(argsOnly = true) AbstractClientPlayerEntity player, @Local(argsOnly = true) VertexConsumerProvider vertexConsumers) {
        var self = (PlayerEntityRenderer)(Object)this;
        boolean left = self.getModel().leftArm == instance || self.getModel().leftSleeve == instance;
        boolean isSleeve = self.getModel().leftSleeve == instance || self.getModel().rightSleeve == instance;
        AtomicBoolean hasElytra = new AtomicBoolean(false);
        List<FurDef> furs = new ArrayList<>();
        List<FurModel> furModels = new ArrayList<>();
        ModComponents.ORIGIN.get(player).getOrigins().values().forEach(origin -> {
            hasElytra.set(hasElytra.get() || origin.hasPowerType(PowerTypeRegistry.get(new Identifier("origins:elytra"))));
            if (!isSleeve) {
                var model = OriginFursClient.CACHED_MODELS.getOrDefault(origin.getIdentifier(), null);
                if (model != null) {
                    furModels.add(model);
                }
            }
            var fur = OriginFursClient.CACHED_FURS.getOrDefault(origin.getIdentifier(), null);

            if (fur != null) {
                furs.add(fur);
                if (instance.visible) {

                    if (isSleeve) {
                        instance.visible = instance.visible && !(fur.hiddenParts().contains(left ? FurPartTypes.leftSleeve : FurPartTypes.rightSleeve));
                    } else {
                        instance.visible = instance.visible && !(fur.hiddenParts().contains(left ? FurPartTypes.leftArm : FurPartTypes.rightArm));
                    }
                }
            }
        });
        FurOffsets furOffsets = FurOffsets.pickHighest(ModComponents.ORIGIN.get(player).getOrigins().values().stream().map(o -> {
            var fur = OriginFursClient.CACHED_FURS.getOrDefault(o.getIdentifier(), null);
            if (fur != null) {
                return fur.offsets();
            }
            return FurOffsets.NONE;
        }).toList());
        Vec3d offset = left ? furOffsets.first_person_left() : furOffsets.first_person_right();
        matrices.push();

        matrices.translate(-offset.x / 16.0f, offset.y / 16.0f, offset.z / 16.0f);
        if (instance.visible) {
            instance.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);
        }

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
        matrices.pop();
        final String boneName = left ? "bipedLeftArm" : "bipedRightArm";
        for (FurModel model : furModels) {
            model.getBakedModel(model.getModelResource(model));
            var b1 = model.getBone(boneName);
            if (b1.isPresent() && b1.get() != null) {
                var b = b1.get();
                model.resetBone(Optional.of(b));
                model.preprocess(List.of(b), player, self.getModel(), hasElytra.get());
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
                matrices.multiply(new Quaternionf().rotateX(180 * MathHelper.RADIANS_PER_DEGREE));
                matrices.translate(0,-2,0);
                matrices.translate(offset.x / 16.0f, -offset.y / 16.0f, -offset.z / 16.0f);
                model.renderRecursively(matrices, model, b, rl_main, vertexConsumers, vertexConsumers.getBuffer(rl_main), false, 0, light, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
                model.resetBone(Optional.of(b));
                matrices.pop();
            }
        }
        return false;
    }
}

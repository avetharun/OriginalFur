package dev.feintha.originfurs.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.feintha.originfurs.client.OriginFursClient;
import dev.feintha.originfurs.fur.FurOffsets;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.PlayerHeldItemFeatureRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Pair;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HeldItemFeatureRenderer.class)
public class ItemInHandMixin {
    enum HandSide {
        LEFT,
        RIGHT, NO_OFFSET;
    }
    @WrapOperation(method="renderItem", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"))
    void renderItemMixin(HeldItemRenderer instance, LivingEntity entity, ItemStack stack, ModelTransformationMode transformationMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Operation<Void> original) {
        matrices.push();
        HandSide side = HandSide.NO_OFFSET;
        if (transformationMode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND || transformationMode == ModelTransformationMode.THIRD_PERSON_LEFT_HAND) {
            side = HandSide.LEFT;
        }
        if (transformationMode == ModelTransformationMode.FIRST_PERSON_RIGHT_HAND || transformationMode == ModelTransformationMode.THIRD_PERSON_RIGHT_HAND) {
            side = HandSide.RIGHT;
        }
        if (side != HandSide.NO_OFFSET) {
            FurOffsets furOffsets = FurOffsets.pickHighest(ModComponents.ORIGIN.get(entity).getOrigins().values().stream().map(o -> {
                var fur = OriginFursClient.CACHED_FURS.getOrDefault(o.getIdentifier(), null);
                if (fur != null) {
                    return fur.offsets();
                }
                return FurOffsets.NONE;
            }).toList());
            Vec3d offset = switch (side) {
                case LEFT -> furOffsets.left();
                case RIGHT -> furOffsets.right();
                default -> Vec3d.ZERO;
            };
            matrices.translate(offset.x, offset.y, offset.z);
        }
        original.call(instance, entity, stack, transformationMode, leftHanded, matrices, vertexConsumers, light);
        matrices.pop();
    }
}

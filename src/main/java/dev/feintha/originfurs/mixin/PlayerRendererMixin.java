package dev.feintha.originfurs.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.feintha.originfurs.client.OriginFursClient;
import dev.feintha.originfurs.fur.FurDef;
import dev.feintha.originfurs.fur.FurFeature;
import dev.feintha.originfurs.fur.FurPartTypes;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public PlayerRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }
    @Inject(method="<init>", at=@At("TAIL"))
    void initMixin(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        addFeature(new FurFeature<>(this));
    }
    @Shadow
    protected abstract void setModelPose(AbstractClientPlayerEntity player);
    @Unique
    void resetModelVisibility(PlayerEntityModel<AbstractClientPlayerEntity> model) {
        model.head.visible = true;
        model.body.visible = true;
        model.rightArm.visible = true;
        model.leftArm.visible = true;
        model.rightLeg.visible = true;
        model.leftLeg.visible = true;
        model.hat.visible = true;
        model.jacket.visible = true;
        model.rightSleeve.visible = true;
        model.leftSleeve.visible = true;
        model.rightPants.visible = true;
        model.leftPants.visible = true;
    }
    @Unique
    Identifier currentOverlayTexture = null;
    @ModifyReturnValue(method = "getTexture(Lnet/minecraft/client/network/AbstractClientPlayerEntity;)Lnet/minecraft/util/Identifier;", at=@At("RETURN"))
    Identifier getTextureMixin(Identifier original) {
        return currentOverlayTexture == null ? original : currentOverlayTexture;
    }
    @Inject(method="render(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at= @At(value = "TAIL"))
    <T extends LivingEntity> void renderMixin(AbstractClientPlayerEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci){
        var self = (PlayerEntityRenderer) (Object) this;
        var entityModel = self.getModel();
        resetModelVisibility(entityModel);
        setModelPose(livingEntity);
        EnumSet<FurPartTypes> HIDDEN_PARTS = EnumSet.noneOf(FurPartTypes.class);
        AtomicBoolean fullyHidePlayerModel = new AtomicBoolean(false);
        Set<Pair<Identifier, FurDef>> furs = new HashSet<>();
        ModComponents.ORIGIN.get(livingEntity).getOrigins().values().forEach(origin -> {
            var fur = OriginFursClient.CACHED_FURS.getOrDefault(origin.getIdentifier(), null);
            if (fur != null) {
                furs.add(new Pair<>(origin.getIdentifier(), fur));
                HIDDEN_PARTS.addAll(fur.hiddenParts());
                fullyHidePlayerModel.set(fullyHidePlayerModel.get() | fur.playerInvisible());
            }
        });
        applyHidden(HIDDEN_PARTS, entityModel);
        boolean fullyHidesPlayerModel = fullyHidePlayerModel.get() || HIDDEN_PARTS.size() == FurPartTypes.values().length;
        for (Pair<Identifier, FurDef> furIdPair : furs) {
            var fur = furIdPair.getRight();
            if (!fullyHidesPlayerModel) {
                if (fur.overlay().isPresent()) {
                    currentOverlayTexture = fur.overlay().get();
                    super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
                }
                if (fur.emissive_overlay().isPresent()) {
                    currentOverlayTexture = fur.emissive_overlay().get();
                    super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
                }
            }
        }
        currentOverlayTexture = null;
    }
    @Unique
    void applyHidden(EnumSet<FurPartTypes> parts, PlayerEntityModel<AbstractClientPlayerEntity> model) {
        model.head.visible = !parts.contains(FurPartTypes.head);
        model.body.visible = !parts.contains(FurPartTypes.body);
        model.rightArm.visible = !parts.contains(FurPartTypes.rightArm);
        model.leftArm.visible = !parts.contains(FurPartTypes.leftArm);
        model.rightLeg.visible = !parts.contains(FurPartTypes.rightLeg);
        model.leftLeg.visible = !parts.contains(FurPartTypes.leftLeg);
        model.hat.visible = !parts.contains(FurPartTypes.hat);
        model.jacket.visible = !parts.contains(FurPartTypes.jacket);
        model.rightSleeve.visible = !parts.contains(FurPartTypes.rightSleeve);
        model.leftSleeve.visible = !parts.contains(FurPartTypes.leftSleeve);
        model.rightPants.visible = !parts.contains(FurPartTypes.rightPants);
        model.leftPants.visible = !parts.contains(FurPartTypes.leftPants);
    }
}

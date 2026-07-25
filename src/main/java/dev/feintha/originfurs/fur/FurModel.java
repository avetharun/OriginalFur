package dev.feintha.originfurs.fur;

import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.origins.registry.ModComponents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class FurModel extends GeoModel<FurModel> implements GeoRenderer<FurModel>, GeoAnimatable {
    public static final FurModel EMPTY = null;
    FurDef furDef;
    public FurModel(FurDef fur) {
        this.furDef = fur;
    }
    @Override
    public Identifier getModelResource(FurModel furModel) {
        return furDef.model().orElse(null);
    }
    @Override
    public Identifier getTextureResource(FurModel furModel) {
        return furDef.texture().orElse(null);
    }
    public Identifier getEmissiveTextureResource(FurModel furModel) {
        return furDef.fullbrightTexture().orElse(null);
    }
    @Override
    public Identifier getAnimationResource(FurModel furModel) {
        return furDef.animation().orElse(null);
    }
    @Override
    public GeoModel<FurModel> getGeoModel() {
        return this;
    }
    @Override
    public FurModel getAnimatable() {
        return this;
    }
    @Override
    public void fireCompileRenderLayersEvent() {

    }
    @Override
    public boolean firePreRenderEvent(MatrixStack matrixStack, BakedGeoModel bakedGeoModel, VertexConsumerProvider vertexConsumerProvider, float v, int i) {
        return false;
    }
    @Override
    public void firePostRenderEvent(MatrixStack matrixStack, BakedGeoModel bakedGeoModel, VertexConsumerProvider vertexConsumerProvider, float v, int i) {

    }
    @Override
    public void updateAnimatedTextureFrame(FurModel furModel) {

    }
    AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "origin_fur", animationState -> {
            animationState.setAnimation(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
            return PlayState.CONTINUE;
        }));
    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
    @Override
    public double getTick(Object o) {
        return MinecraftClient.getInstance().getTickDelta();
    }
    public void cloneBone(Optional<GeoBone> o_bone, ModelPart part) {
        if (o_bone.isEmpty()) return;
        var bone = o_bone.get();
        var dmat = part.getDefaultTransform();

//        bone.updatePosition(part.pivotX, -part.pivotY, part.pivotZ);
        bone.updatePivot(part.pivotX, part.pivotY, part.pivotZ);
//        bone.updateRotation(part.pitch, part.yaw, part.roll);
//        bone.setScaleX(part.xScale);
//        bone.setScaleY(part.yScale);
//        bone.setScaleZ(part.zScale);
//        bone.updatePosition(bone.getPosX(), bone.getPosY() - 16.0f, bone.getPosZ());
    }
    public void resetBone(Optional<GeoBone> o_bone) {
        if (o_bone.isEmpty()) return;
        var bone = o_bone.get();
        bone.setPosX(0);
        bone.setPosY(0);
        bone.setPosZ(0);
        bone.setScaleX(1);
        bone.setScaleY(1);
        bone.setScaleZ(1);
        bone.setRotX(0);
        bone.setRotY(0);
        bone.setRotZ(0);
    }
    public void preprocess(Collection<? extends CoreGeoBone> coreGeoBoneList, AbstractClientPlayerEntity player, PlayerEntityModel<AbstractClientPlayerEntity> model, boolean hasElytra) {
        for (CoreGeoBone coreGeoBone : coreGeoBoneList) {
            preprocess(coreGeoBone.getChildBones(), player, model, hasElytra);
            coreGeoBone.setHidden(false);
            coreGeoBone.setHidden(coreGeoBone.getName().endsWith("thin_only") && !model.thinArms);
            if (coreGeoBone.isHidden()) {return;}
            coreGeoBone.setHidden(coreGeoBone.getName().endsWith("wide_only") && model.thinArms);
            if (coreGeoBone.isHidden()) {return;}
            coreGeoBone.setHidden(coreGeoBone.getName().contains("elytra_hides") && hasElytra || player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA));
            if (coreGeoBone.isHidden()) {return;}
            coreGeoBone.setHidden(coreGeoBone.getName().contains("helmet_hides") && !player.getEquippedStack(EquipmentSlot.HEAD).isEmpty());
            if (coreGeoBone.isHidden()) {return;}
            coreGeoBone.setHidden(coreGeoBone.getName().contains("chestplate_hides") && !player.getEquippedStack(EquipmentSlot.CHEST).isEmpty());
            if (coreGeoBone.isHidden()) {return;}
            coreGeoBone.setHidden(coreGeoBone.getName().contains("leggings_hides") && !player.getEquippedStack(EquipmentSlot.LEGS).isEmpty());
            if (coreGeoBone.isHidden()) {return;}
            coreGeoBone.setHidden(coreGeoBone.getName().contains("boots_hides") && !player.getEquippedStack(EquipmentSlot.FEET).isEmpty());
            if (coreGeoBone.isHidden()) {return;}
            for (var modid : FabricLoader.getInstance().getAllMods()){
                var id = modid.getMetadata().getId();
                if (coreGeoBone.getName().contains("mod_hides_"+id)) {
                    coreGeoBone.setHidden(true);
                    return;
                }
            }
        }
    }
    public void preprocess(AbstractClientPlayerEntity player, PlayerEntityModel<AbstractClientPlayerEntity> model, boolean hasElytra) {
        preprocess(getAnimationProcessor().getRegisteredBones(), player, model, hasElytra);
    }
    public void render(PlayerEntityModel<AbstractClientPlayerEntity> playerModel, AbstractClientPlayerEntity player, MatrixStack poseStack, VertexConsumerProvider bufferSource, float yaw, float partialTick, int packedLight) {
        poseStack.push();
        AtomicBoolean hasElytra = new AtomicBoolean(false);
        ModComponents.ORIGIN.get(player).getOrigins().values().forEach(origin -> {
            hasElytra.set(hasElytra.get() || origin.hasPowerType(PowerTypeRegistry.get(new Identifier("origins:elytra"))));
        });
        preprocess(player, playerModel, hasElytra.get());
        setRotationForBone("bipedHead", ((IMojModelPart) (Object) playerModel.head).originfurs$getRotation());
        setRotationForBone("bipedBody", ((IMojModelPart) (Object) playerModel.body).originfurs$getRotation());
        setRotationForBone("bipedLeftArm", ((IMojModelPart) (Object) playerModel.leftArm).originfurs$getRotation());
        setRotationForBone("bipedRightArm", ((IMojModelPart) (Object) playerModel.rightArm).originfurs$getRotation());
        setRotationForBone("bipedLeftLeg", ((IMojModelPart) (Object) playerModel.leftLeg).originfurs$getRotation());
        setRotationForBone("bipedRightLeg", ((IMojModelPart) (Object) playerModel.rightLeg).originfurs$getRotation());
        translatePositionForBone("bipedHead", ((IMojModelPart) (Object) playerModel.head).originfurs$getPosition());
        translatePositionForBone("bipedBody", ((IMojModelPart) (Object) playerModel.body).originfurs$getPosition());
        translatePositionForBone("bipedLeftArm", ((IMojModelPart) (Object) playerModel.leftArm).originfurs$getPosition());
        translatePositionForBone("bipedRightArm", ((IMojModelPart) (Object) playerModel.rightArm).originfurs$getPosition());
        translatePositionForBone("bipedLeftLeg", ((IMojModelPart) (Object) playerModel.rightLeg).originfurs$getPosition());
        translatePositionForBone("bipedRightLeg", ((IMojModelPart) (Object) playerModel.leftLeg).originfurs$getPosition());
        translatePositionForBone("bipedLeftArm", new Vec3d(5, 2, 0));
        translatePositionForBone("bipedRightArm", new Vec3d(-5, 2, 0));
        translatePositionForBone("bipedLeftLeg", new Vec3d(-2, 12, 0));
        translatePositionForBone("bipedRightLeg", new Vec3d(2, 12, 0));
        invertRotForPart("bipedBody", false, true, false);
        invertRotForPart("bipedHead", false, true, true);
        invertRotForPart("bipedRightArm", false, true, true);
        invertRotForPart("bipedLeftArm", false, true, true);
        invertRotForPart("bipedRightLeg", false, true, true);
        invertRotForPart("bipedLeftLeg", false, true, true);
        poseStack.multiply(new Quaternionf().rotateX(180 * MathHelper.RADIANS_PER_DEGREE));
        poseStack.translate(0, -1.5, 0);
        var rl_main = RenderLayer.getEntityCutoutNoCull(getTextureResource(this));
        actuallyRender(poseStack, this, getBakedModel(getModelResource(this)), rl_main, bufferSource, bufferSource.getBuffer(rl_main), false, partialTick, packedLight, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        if (getEmissiveTextureResource(this) != null) {
            var rl_emissive = RenderLayer.getEntityTranslucentEmissive(getEmissiveTextureResource(this));
            actuallyRender(poseStack, this, getBakedModel(getModelResource(this)), rl_main, bufferSource, bufferSource.getBuffer(rl_emissive), false, partialTick, packedLight, OverlayTexture.DEFAULT_UV, 1, 1, 1, 1);
        }
        poseStack.pop();
        resetBone(getBone("bipedHead"));
        resetBone(getBone("bipedHat"));
        resetBone(getBone("bipedBody"));
        resetBone(getBone("bipedJacket"));
        resetBone(getBone("bipedLeftArm"));
        resetBone(getBone("bipedLeftSleeve"));
        resetBone(getBone("bipedRightArm"));
        resetBone(getBone("bipedRightSleeve"));
        resetBone(getBone("bipedLeftLeg"));
        resetBone(getBone("bipedLeftPants"));
        resetBone(getBone("bipedRightLeg"));
        resetBone(getBone("bipedRightPants"));
    }

    public final void setRotationForBone(String bone_name, Vec3d rot) {
        getBone(bone_name).ifPresent(b->{
            b.setRotX((float)rot.x);
            b.setRotY((float)rot.y);
            b.setRotZ((float)rot.z);
        });
    }
    public final void setRotationForBone(String bone_name, Vec3d rot, boolean iX, boolean iY, boolean iZ) {
        getBone(bone_name).ifPresent(b->{
            b.setRotX((float)rot.x * (iX ? -1 : 1));
            b.setRotY((float)rot.y * (iY ? -1 : 1));
            b.setRotZ((float)rot.z * (iZ ? -1 : 1));
        });
    }
    public void translatePositionForBone(String name, Vec3d vec3d) {
        getBone(name).ifPresent(b->{
            b.setPosX((float)vec3d.x + b.getPosX());
            b.setPosY((float)vec3d.y + b.getPosY());
            b.setPosZ((float)vec3d.z + b.getPosZ());
        });
    }
    public final void invertRotForPart(String bone_name, boolean x, boolean y, boolean z) {
        getBone(bone_name).ifPresent(b->{
            var r =b.getRotationVector().mul(x ? -1 : 1, y ? -1 : 1, z ? -1 : 1);
            b.setRotX((float) r.x);
            b.setRotY((float) r.y);
            b.setRotZ((float) r.z);
        });
    }
}

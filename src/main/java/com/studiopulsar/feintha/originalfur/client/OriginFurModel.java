package com.studiopulsar.feintha.originalfur.client;

import com.google.gson.JsonObject;
import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.model.GeoModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.model.ElytraEntityModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;

public class OriginFurModel extends GeoModel<OriginFurAnimatable> {
    final JsonObject json;
    public OriginFurModel(JsonObject json) {
        this.json = json;
        System.out.println(json);
    }
    public final boolean isPlayerModelInvisible() {return JsonHelper.getBoolean(json, "playerInvisible", false);}
    public final GeoBone resetBone(String bone_name) {
        setPositionForBone(bone_name, new Vec3d(0,0,0));
        setRotationForBone(bone_name, new Vec3d(0,0,0));
        return setScaleForBone(bone_name, new Vec3d(1,1,1));
    }

    public final GeoBone setPositionForBone(String bone_name, Vec3d pos) {
        var b = this.getAnimationProcessor().getBone(bone_name);
        if (b == null) {return null;}
        b.setPosX((float)pos.x);
        b.setPosY((float)pos.y);
        b.setPosZ((float)pos.z);
        return (GeoBone) b;
    }
    public final GeoBone translatePositionForBone(String bone_name, Vec3d pos) {
        var b = this.getAnimationProcessor().getBone(bone_name);
        if (b == null) {return null;}
        b.setPosX((float)pos.x + b.getPosX());
        b.setPosY((float)pos.y + b.getPosY());
        b.setPosZ((float)pos.z + b.getPosZ());
        return (GeoBone) b;
    }
    public final GeoBone translateRotationForBone(String bone_name, Vec3d pos) {
        var b = this.getAnimationProcessor().getBone(bone_name);
        if (b == null) {return null;}
        b.setRotX((float)pos.x + b.getRotX());
        b.setRotY((float)pos.y + b.getRotY());
        b.setRotZ((float)pos.z + b.getRotZ());
        return (GeoBone) b;
    }
    public final GeoBone setRotationForBone(String bone_name, Vec3d rot) {
        var b = this.getAnimationProcessor().getBone(bone_name);
        if (b == null) {return null;}
        b.setRotX((float)rot.x);
        b.setRotY((float)rot.y);
        b.setRotZ((float)rot.z);
        return (GeoBone) b;
    }
    public final GeoBone setScaleForBone(String bone_name, Vec3d scale) {
        var b = this.getAnimationProcessor().getBone(bone_name);
        if (b == null) {return null;}
        b.setScaleX((float)scale.x);
        b.setScaleY((float)scale.y);
        b.setScaleZ((float)scale.z);
        return (GeoBone) b;
    }
    public final GeoBone setTransformationForBone(String bone_name, Vec3d pos, Vec3d scale, Vec3d eulers){
        setPositionForBone(bone_name, pos);
        setRotationForBone(bone_name, eulers);
        return setScaleForBone(bone_name,scale);

    }
    public final GeoBone copyFromMojangModelPart(String bone_name, ModelPart part) {
        Vec3d pos = new Vec3d(part.pivotX, part.pivotY, part.pivotZ);
        Vec3d scale = new Vec3d(part.xScale, part.yScale, part.zScale);
        Vec3d rott = new Vec3d(-part.pitch, -part.yaw, -part.roll);
        return setTransformationForBone(bone_name, pos, scale, rott);
    }
    public final GeoBone copyFromMojangModelPart(String bone_name, ModelPart part, boolean inverted) {
        Vec3d pos = new Vec3d(part.pivotX, part.pivotY, part.pivotZ);
        Vec3d scale = new Vec3d(part.xScale, part.yScale, part.zScale);
        Vec3d rott = new Vec3d(-part.pitch, -part.yaw, -part.roll);
        if (inverted) {
            rott = new Vec3d(part.pitch, part.yaw, part.roll);
        }
        return setTransformationForBone(bone_name, pos, scale, rott);
    }
    public final GeoBone copyRotFromMojangModelPart(String bone_name, ModelPart part, boolean inverted) {
        Vec3d rott = new Vec3d(-part.pitch, -part.yaw, -part.roll);
        if (inverted) {
            rott = new Vec3d(part.pitch, part.yaw, part.roll);
        }
        return setRotationForBone(bone_name,rott);
    }
    public final GeoBone copyRotFromMojangModelPart(String bone_name, ModelPart part, boolean invertedX, boolean invertedY, boolean invertedZ) {
        Vec3d rott = new Vec3d(-part.getTransform().pitch, -part.getTransform().yaw, -part.getTransform().roll);
        if (invertedX) rott = rott.multiply(new Vec3d(-1,1,1));
        if (invertedY) rott = rott.multiply(new Vec3d(1,-1,1));
        if (invertedZ) rott = rott.multiply(new Vec3d(1,-1,1));
        return setRotationForBone(bone_name,rott);
    }
    public final GeoBone copyRotFromMojangModelPart(String bone_name, ModelPart part) {
        Vec3d rott = new Vec3d(-part.pitch, -part.yaw, -part.roll);
        return setRotationForBone(bone_name,rott);
    }
    public final GeoBone copyPosFromMojangModelPart(String bone_name, ModelPart part) {
        var t = part.getTransform();
        Vec3d rott = new Vec3d(t.pivotX, t.pivotY, t.pivotZ);
        return setPositionForBone(bone_name,rott);

    }
    public final GeoBone copyScaleFromMojangModelPart(String bone_name, ModelPart part) {
        Vec3d rott = new Vec3d(part.xScale, part.yScale, part.zScale);
        return setPositionForBone(bone_name,rott);
    }
//                public GeoModel setTransformationForBone(String bone_name, Vec3d pos, Vec3d scale, Quaterniond quaternion){}

    @Override
    public Identifier getModelResource(OriginFurAnimatable geoAnimatable) {
        var id = Identifier.tryParse(JsonHelper.getString(json, "model", "originalfur:geo/missing.geo.json"));

        return id;
    }

    @Override
    public Identifier getTextureResource(OriginFurAnimatable geoAnimatable) {
        var id = Identifier.tryParse(JsonHelper.getString(json, "texture", "originalfur:textures/missing.png"));
        return id;
    }
    public Identifier getFullbrightTextureResource(OriginFurAnimatable geoAnimatable) {
        var id = Identifier.tryParse(JsonHelper.getString(json, "fullbrightTexture", "originalfur:textures/missing.png"));
        return id;
    }
    public boolean hasCustomElytraTexture() {
        return json.has("elytraTexture");
    }
    public Identifier getElytraTexture() {
        return Identifier.tryParse(JsonHelper.getString(json, "elytraTexture", "textures/entity/elytra.png"));
    }
    public Identifier getHurtSoundResource() {
        return Identifier.tryParse(JsonHelper.getString(json, "hurtSound", "null"));
    }
    @Override
    public Identifier getAnimationResource(OriginFurAnimatable geoAnimatable) {
        var id = Identifier.tryParse(JsonHelper.getString(json, "animation", "originalfur:animations/missing.animation.json"));
        return id;
    }
}

package com.studiopulsar.feintha.originalfur.fabric;

import com.google.gson.JsonObject;
import com.studiopulsar.feintha.originalfur.alib;
import com.studiopulsar.feintha.originalfur.OriginFurAnimatable;
import it.unimi.dsi.fastutil.longs.Long2ReferenceLinkedOpenHashMap;
import mod.azure.azurelib.cache.AzureLibCache;
import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.model.GeoModel;
import net.minecraft.client.model.ModelPart;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Stack;

public class OriginFurModel extends GeoModel<OriginFurAnimatable> {
    JsonObject json;
    public OriginFurModel(JsonObject json) {
        this.recompile(json);
    }
    public enum VMP {
        leftArm, rightArm, rightSleeve, leftSleeve, rightLeg, leftLeg, rightPants, leftPants, hat, head, body, jacket;

        public static final EnumSet<VMP> ALL_OPTS = EnumSet.allOf(VMP.class);
    }
    public EnumSet<VMP> hiddenParts = EnumSet.noneOf(VMP.class);
    public EnumSet<VMP> getHiddenParts() {
        return hiddenParts;
    }
    @SuppressWarnings("SpellCheckingInspection")
    public void parseHiddenParts() {
        var set = EnumSet.noneOf(VMP.class);
        if (json.has("hidden")) {
            var h = json.getAsJsonArray("hidden");
            h.forEach(jsonElement -> {
                switch (jsonElement.getAsString().toLowerCase()) {
                    case "rightsleeve"-> set.add(VMP.rightSleeve);
                    case "leftsleeve"-> set.add(VMP.leftSleeve);
                    case "rightarm"-> set.add(VMP.rightArm);
                    case "leftarm"-> set.add(VMP.leftArm);
                    case "leftpant", "leftpants" -> set.add(VMP.leftPants);
                    case "rightpant", "rightpants" -> set.add(VMP.rightPants);
                    case "rightleg" -> set.add(VMP.rightLeg);
                    case "leftleg" -> set.add(VMP.leftLeg);
                    case "hat", "hair" -> set.add(VMP.hat);
                    case "head" -> set.add(VMP.head);
                    case "body", "torso" -> set.add(VMP.body);
                    case "jacket" -> set.add(VMP.jacket);
                }
            });
        }
        hiddenParts.clear();
        hiddenParts.addAll(set);
    }
    public void recompile(JsonObject json) {
        this.json = json;
        hiddenParts.clear();
        parseHiddenParts();
        var id = getModelResource(null);
        // Force cache this model! This is so getCachedGeoModel will not throw an exception unless the bone doesn't exist!
        AzureLibCache.getBakedModels().put(id, this.getBakedModel(id));
        boneCache.clear();
        for (var bone : this.getAnimationProcessor().getRegisteredBones()) {
            boneCache.put(alib.getHash64(bone.getName()), (GeoBone) bone);
        }
    }
    public boolean hasRenderingOffsets() {
        return json.has("rendering_offsets");
    }
    public boolean hasSubRenderingOffset(String id) {
        return hasRenderingOffsets() && json.getAsJsonObject("rendering_offsets").has(id);
    }
    public final Vec3d getRightOffset() {
        if (!hasSubRenderingOffset("right")) {
            return Vec3d.ZERO;
        }
        return alib.VectorFromJson(json.getAsJsonObject("rendering_offsets").get("right"));
    }
    public final Identifier getOverlayTexture(boolean slim) {
        if (!slim || !json.has("overlay_slim")) {
            if (json.has("overlay")) {
                return Identifier.tryParse(JsonHelper.getString(json, "overlay"));
            }
        } else {
            if (json.has("overlay_slim")) {
                return Identifier.tryParse(JsonHelper.getString(json, "overlay_slim"));
            }
        }
        return null;
    }
    public final Identifier getEmissiveTexture(boolean slim) {
        if (!slim || !json.has("emissive_overlay_slim")) {
            if (json.has("emissive_overlay")) {
                return Identifier.tryParse(JsonHelper.getString(json, "emissive_overlay"));
            }
        } else {

            if (json.has("emissive_overlay_slim")) {
                return Identifier.tryParse(JsonHelper.getString(json, "emissive_overlay_slim"));
            }
        }
        return null;
    }
    public final Vec3d getLeftOffset() {
        if (!hasSubRenderingOffset("left")) {
            return Vec3d.ZERO;
        }
        return alib.VectorFromJson(json.getAsJsonObject("rendering_offsets").get("left"));
    }
    public final boolean isPlayerModelInvisible() {return JsonHelper.getBoolean(json, "playerInvisible", false);}
    protected final LinkedHashMap<String,Stack<Vec3d>> posStack = new LinkedHashMap<>();
    protected final LinkedHashMap<String,Stack<Vec3d>> rotStack = new LinkedHashMap<>();
    protected final LinkedHashMap<String,Stack<Vec3d>> sclStack = new LinkedHashMap<>();
    public final void pushPos(String bone_name, Vec3d pos) {
        var bone = this.getCachedGeoBone(bone_name);
        if (bone == null) {return;}
        getStackFor(bone_name, posStack).push(new Vec3d(bone.getPosX(), bone.getPosY(), bone.getPosZ()));
        bone.setPosX((float) pos.x);
        bone.setPosY((float) pos.y);
        bone.setPosZ((float) pos.z);
    }
    public final void pushRot(String bone_name, Vec3d rot) {
        var bone = this.getCachedGeoBone(bone_name);
        if (bone == null) {return;}
        getStackFor(bone_name, rotStack).push(new Vec3d(bone.getRotX(), bone.getRotY(), bone.getRotZ()));
        bone.setRotX((float) rot.x);
        bone.setRotY((float) rot.y);
        bone.setRotZ((float) rot.z);
    }
    public final void pushScl(String bone_name, Vec3d scale) {
        var bone = this.getCachedGeoBone(bone_name);
        if (bone == null) {return;}
        getStackFor(bone_name, sclStack).push(new Vec3d(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ()));
        bone.setScaleX((float) scale.x);
        bone.setScaleY((float) scale.y);
        bone.setScaleZ((float) scale.z);
    }
    public final void popPos(String bone_name) {
        var bone = this.getCachedGeoBone(bone_name);
        if (bone == null) {return;}
        var stk = getStackFor(bone_name, posStack);
        if (stk.isEmpty()) {return;}
        var pos = stk.pop();
        bone.setModelPosition(new Vector3d(pos.x, pos.y, pos.z));

    }
    public final void popRot(String bone_name) {
        var bone = this.getCachedGeoBone(bone_name);
        if (bone == null) {return;}
        var stk = getStackFor(bone_name, rotStack);
        if (stk.isEmpty()) {return;}
        var pos = stk.pop();
        bone.setRotX((float) pos.x);
        bone.setRotY((float) pos.y);
        bone.setRotZ((float) pos.z);
    }
    public final void popScl(String bone_name) {
        var bone = this.getCachedGeoBone(bone_name);
        if (bone == null) {return;}
        var stk = getStackFor(bone_name, sclStack);
        if (stk.isEmpty()) {return;}
        var pos = stk.pop();
        bone.setScaleX((float) pos.x);
        bone.setScaleY((float) pos.y);
        bone.setScaleZ((float) pos.z);
    }
    public final GeoBone getCachedGeoBone(String bone_name) {
        long hash = alib.getHash64(bone_name);
        if (boneCache.containsKey(hash)) {
            return boneCache.get(hash);
        }
        GeoBone b = this.getBone(bone_name).orElse(null);
        if (b == null) {
//            System.err.println("Bone " + bone_name + " was null when fetching via string (hash: " + hash + ")");
            return null;
        }
        boneCache.putAndMoveToFirst(hash, b);
        return b;
    }
    private Stack<Vec3d> getStackFor(String bone_name, LinkedHashMap<String, Stack<Vec3d>> stackMap) {
        if (!stackMap.containsKey(bone_name)) {
            posStack.put(bone_name, new Stack<>());
        }
        return posStack.get(bone_name);
    }
    public Long2ReferenceLinkedOpenHashMap<GeoBone> boneCache =  new Long2ReferenceLinkedOpenHashMap<>();
    public final GeoBone resetBone(String bone_name) {
        setPositionForBone(bone_name, new Vec3d(0,0,0));
        setRotationForBone(bone_name, new Vec3d(0,0,0));
        return setScaleForBone(bone_name, new Vec3d(1,1,1));
    }

    public final GeoBone setPositionForBone(String bone_name, Vec3d pos) {
        var b = this.getCachedGeoBone(bone_name);
        if (b == null) {return null;}
        b.setPosX((float)pos.x);
        b.setPosY((float)pos.y);
        b.setPosZ((float)pos.z);
        return (GeoBone) b;
    }
    public final GeoBone translatePositionForBone(String bone_name, Vec3d pos) {
        var b = this.getCachedGeoBone(bone_name);
        if (b == null) {return null;}
        b.setPosX((float)pos.x + b.getPosX());
        b.setPosY((float)pos.y + b.getPosY());
        b.setPosZ((float)pos.z + b.getPosZ());
        return (GeoBone) b;
    }
    public final GeoBone translateRotationForBone(String bone_name, Vec3d pos) {
        var b = this.getCachedGeoBone(bone_name);
        if (b == null) {return null;}
        b.setRotX((float)pos.x + b.getRotX());
        b.setRotY((float)pos.y + b.getRotY());
        b.setRotZ((float)pos.z + b.getRotZ());
        return (GeoBone) b;
    }
    public final GeoBone setRotationForBone(String bone_name, Vec3d rot) {
        var b = this.getCachedGeoBone(bone_name);
        if (b == null) {return null;}
        b.setRotX((float)rot.x);
        b.setRotY((float)rot.y);
        b.setRotZ((float)rot.z);
        return (GeoBone) b;
    }
    public final GeoBone setScaleForBone(String bone_name, Vec3d scale) {
        var b = this.getCachedGeoBone(bone_name);
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

package dev.feintha.originfurs.fur;


import net.minecraft.client.model.ModelPart;
import net.minecraft.util.math.Vec3d;

public interface IMojModelPart {
    default Vec3d originfurs$getPosition() {return Vec3d.ZERO;}
    default Vec3d originfurs$getRotation() {return Vec3d.ZERO;}
    default Vec3d originfurs$getScale() {return Vec3d.ZERO;}
    default ModelPart originfurs$getHolderPart() {return null;}
}
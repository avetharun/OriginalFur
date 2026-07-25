package dev.feintha.originfurs.mixin;

import dev.feintha.originfurs.fur.IMojModelPart;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ModelPart.class)
public abstract class ModelPartMixin implements IMojModelPart {

    @Shadow public float pivotX;

    @Shadow public float pivotY;

    @Shadow public float pivotZ;

    @Shadow public abstract ModelTransform getTransform();

    @Shadow public float xScale;

    @Shadow public float yScale;

    @Shadow public float zScale;

    @Shadow public abstract ModelPart getChild(String name);

    @Override
    public ModelPart originfurs$getHolderPart() {
        return this.getChild("holder");
    }

    @Override
    public Vec3d originfurs$getPosition() {
        var t = getTransform();
        return new Vec3d(t.pivotX, t.pivotY, t.pivotZ).negate();
    }
    @Override
    public Vec3d originfurs$getScale() {
        return new Vec3d(xScale, yScale, zScale);
    }

    @Override
    public Vec3d originfurs$getRotation() {
        var t = getTransform();
        return new Vec3d(t.pitch, t.yaw, t.roll);
    }
}

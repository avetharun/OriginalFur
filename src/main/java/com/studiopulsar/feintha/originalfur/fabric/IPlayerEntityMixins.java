package com.studiopulsar.feintha.originalfur.fabric;

import io.github.apace100.origins.origin.Origin;
import net.minecraft.util.math.Vec3d;

public interface IPlayerEntityMixins {
    public default boolean originalFur$isPlayerInvisible() {return false;};
    public default Origin[] originalFur$currentOrigins() {return new Origin[]{Origin.EMPTY};}
    public default Vec3d itemRenderingOffset () {return Vec3d.ZERO;}
    public default OriginFurModel originalFur$getCurrentModel() {
        return null;
    }
}

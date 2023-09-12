package com.studiopulsar.feintha.originalfur.fabric;

import com.studiopulsar.feintha.originalfur.fabric.client.OriginalFurClient;
import io.github.apace100.origins.origin.Origin;
import net.minecraft.util.math.Vec3d;

public interface IPlayerEntityMixins {
    public default boolean betterCombat$isSwinging() {return false;}
    public default void betterCombat$setSwinging(boolean value) {}
    public default boolean originalFur$isPlayerInvisible() {return false;};
    public default Origin[] originalFur$currentOrigins() {return new Origin[]{Origin.EMPTY};}
    public default OriginFurModel originalFur$getCurrentModel() {
        return (OriginFurModel) originalFur$getCurrentFur().getGeoModel();
    }
    public default OriginalFurClient.OriginFur originalFur$getCurrentFur() {return OriginalFurClient.OriginFur.NULL_OR_DEFAULT_FUR;}
}

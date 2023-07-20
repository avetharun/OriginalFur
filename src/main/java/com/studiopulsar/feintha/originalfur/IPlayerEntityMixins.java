package com.studiopulsar.feintha.originalfur;

import io.github.apace100.origins.origin.Origin;

public interface IPlayerEntityMixins {
//    public GeoArmorRenderer<OriginalFur.OriginalFurArmorItem> renderer;
    public default boolean originalFur$isPlayerInvisible() {return false;};
    public default Origin[] originalFur$currentOrigins() {return new Origin[]{Origin.EMPTY};}
}

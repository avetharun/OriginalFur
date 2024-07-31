package com.studiopulsar.feintha.originalfur.fabric;

import com.studiopulsar.feintha.originalfur.fabric.client.OriginalFurClient;
import io.github.apace100.origins.origin.Origin;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public interface IPlayerEntityMixins {
    public default boolean betterCombat$isSwinging() {return false;}
    public default void betterCombat$setSwinging(boolean value) {}
    public default boolean originalFur$isPlayerInvisible() {return false;};
    public default ArrayList<Origin> originalFur$currentOrigins() {
        var a = new ArrayList<Origin>();
        a.add(Origin.EMPTY);
        return a;
    }
    public default ArrayList<OriginFurModel> originalFur$getCurrentModels() {
        ArrayList<OriginFurModel> mdls = new ArrayList<>();
        for (var fur : originalFur$getCurrentFurs()){
            mdls.add((OriginFurModel) fur.getGeoModel());
        }
        return mdls;
    }
    public default ArrayList<OriginalFurClient.OriginFur> originalFur$getCurrentFurs() {return new ArrayList<>();}
}

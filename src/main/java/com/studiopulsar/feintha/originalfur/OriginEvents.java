package com.studiopulsar.feintha.originalfur;

import io.github.apace100.origins.origin.Origin;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Unique;

public interface OriginRegistryEvents {

    @Unique
    public static final Event<OriginRegistryEvents.OriginRegistryAdded> ORIGIN_REGISTRY_ADDED_EVENT = EventFactory.createArrayBacked(OriginRegistryEvents.OriginRegistryAdded.class, callbacks -> (origin, id) -> {
        for (OriginRegistryEvents.OriginRegistryAdded callback : callbacks) {
            callback.onOriginAdded(origin,id);
        }
    });

    @FunctionalInterface
    public interface OriginRegistryAdded {
        void onOriginAdded(Origin origin, Identifier id);
    }
}

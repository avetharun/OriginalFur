package com.studiopulsar.feintha.originalfur.fabric;

import io.github.apace100.origins.origin.Origin;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Unique;

public interface OriginEvents {


    @Unique
    Event<OriginEvents.OriginRegistryAdded> ORIGIN_REGISTRY_ADDED_EVENT = EventFactory.createArrayBacked(OriginEvents.OriginRegistryAdded.class, callbacks -> (origin, id) -> {
        for (OriginEvents.OriginRegistryAdded callback : callbacks) {
            callback.onOriginAddedToRegistry(origin,id);
        }
    });

    @Unique
    Event<OriginEvents.OriginGivenToPlayer> ORIGIN_GIVEN_TO_PLAYER = EventFactory.createArrayBacked(OriginEvents.OriginGivenToPlayer.class, callbacks -> (origin, id) -> {
        for (OriginEvents.OriginGivenToPlayer callback : callbacks) {
            callback.onOriginGivenToPlayer(origin,id);
        }
    });

    @FunctionalInterface
    public interface OriginRegistryAdded {
        void onOriginAddedToRegistry(Origin origin, Identifier id);
    }
    @FunctionalInterface
    public interface OriginGivenToPlayer {
        void onOriginGivenToPlayer(Origin origin, Identifier id);
    }
}

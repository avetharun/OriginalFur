package com.studiopulsar.feintha.originalfur.fabric;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.UUID;
import java.util.function.Function;

public class FurPredicate {
    @FunctionalInterface
    public interface Predicate_T  {
        boolean test(OriginFurModel model, Identifier thisid, UUID ent_uuid);
    }
    Predicate_T predicate;
    public FurPredicate(Predicate_T predicate) {
        this.predicate = predicate;
    }
    public final boolean test(OriginFurModel model, Identifier thidid, UUID ent_uuid) {
        return predicate.test(model, thidid, ent_uuid);
    }
}

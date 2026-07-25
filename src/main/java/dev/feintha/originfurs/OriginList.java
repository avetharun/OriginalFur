package dev.feintha.originfurs;

import com.google.common.collect.ImmutableSet;
import io.github.apace100.origins.origin.Origin;

import java.util.Set;

public record OriginList(Set<Origin> origins) {
    public static final OriginList EMPTY = new OriginList(ImmutableSet.of());
    public boolean isEmpty() {
        return origins.isEmpty();
    }
}

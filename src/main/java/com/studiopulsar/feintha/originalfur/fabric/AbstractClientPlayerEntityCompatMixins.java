package com.studiopulsar.feintha.originalfur.fabric;

public interface AbstractClientPlayerEntityCompatMixins {
    default boolean betterCombat$isAttacking() {return false;}
}

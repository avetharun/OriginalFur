package com.studiopulsar.feintha.originalfur.fabric.mixin;

import com.studiopulsar.feintha.originalfur.fabric.client.IPlayerAnimatorAnimApplier;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;

public class PseudoPlayerAnimatorMixins{
    @Pseudo
    @Mixin(AnimationProcessor.class)
    public static class AnimationProcessorMixin implements IPlayerAnimatorAnimApplier {
        @Shadow private float tickDelta;

        @Override
        public float getDelta() {
            return tickDelta;
        }
    }
}

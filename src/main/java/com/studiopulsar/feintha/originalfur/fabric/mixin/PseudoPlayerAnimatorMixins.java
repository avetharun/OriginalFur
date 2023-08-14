package com.studiopulsar.feintha.originalfur.fabric.mixin;

import com.studiopulsar.feintha.originalfur.fabric.client.IPlayerAnimatorAnimApplier;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.*;
import dev.kosmx.playerAnim.core.impl.AnimationProcessor;
import dev.kosmx.playerAnim.core.util.Pair;
import dev.kosmx.playerAnim.core.util.Vec3f;
import dev.kosmx.playerAnim.impl.animation.AnimationApplier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PseudoPlayerAnimatorMixins{
    @Pseudo
    @Mixin(AnimationApplier.class)
    public static class AnimationApplierMixin   implements IPlayerAnimatorAnimApplier{

    }
    @Pseudo
    @Mixin(PlayerAnimationFrame.class)
    public static class AnimationMixin implements  IPlayerAnimatorAnimApplier{
        @Shadow HashMap<String, PlayerAnimationFrame.PlayerPart> parts;
        @Override
        public Vec3d getPositionForBone(String name) {
            var p = parts.get(name).pos;
            return new Vec3d(p.getX(), p.getY(), p.getZ());
        }
    }
    @Pseudo
    @Mixin(KeyframeAnimationPlayer.class)
    public static class KeyframeAnimationMixin implements  IPlayerAnimatorAnimApplier{
        @Shadow @Final public HashMap<String, KeyframeAnimationPlayer.BodyPart> bodyParts;

        @Override
        public Vec3d getPositionForBone(String name) {
            var p = bodyParts.get(name).getBodyOffset(Vec3f.ZERO);
            return new Vec3d(p.getX(), p.getY(), p.getZ());
        }
    }
    @Pseudo
    @Mixin(AnimationStack.class)
    public static class AnimationStackMixin implements IPlayerAnimatorAnimApplier {
        @Shadow @Final private ArrayList<Pair<Integer, IAnimation>> layers;

        @Override
        public @Nullable IAnimation getTop() {
            if (layers.size() == 0) {return null;}
            return layers.get(layers.size()-1).getRight();
        }

        @Override
        public Vec3d getPositionForBone(String name) {
            Vec3d pos = Vec3d.ZERO;
            Iterator var5 = this.layers.iterator();
            while(true) {
                Pair layer;
                do {
                    do {
                        if (!var5.hasNext()) {
                            return pos;
                        }
                        layer = (Pair)var5.next();
                    } while(!((IAnimation)layer.getRight()).isActive());
                } while(FirstPersonMode.isFirstPersonPass() && !((IAnimation)layer.getRight()).getFirstPersonMode(getDelta()).isEnabled());

                pos = pos.add(((IPlayerAnimatorAnimApplier)((IAnimation)layer.getRight())).getPositionForBone(name));
            }
        }
    }
    @Pseudo
    @Mixin(ModifierLayer.class)
    public static abstract class ModifierLayerMixin<T extends IAnimation> implements IPlayerAnimatorAnimApplier{
        @Shadow public abstract @Nullable T getAnimation();

        @Override
        public Vec3d getPositionForBone(String name) {
            return ((IPlayerAnimatorAnimApplier)this.getAnimation()).getPositionForBone(name);
        }
    }
    @Pseudo
    @Mixin(AnimationContainer.class)
    public static abstract class AnimationControllerMixin<T extends IAnimation> implements IPlayerAnimatorAnimApplier {
        @Shadow public abstract @Nullable T getAnim();

        @Override
        public Vec3d getPositionForBone(String name) {
            return ((IPlayerAnimatorAnimApplier)this.getAnim()).getPositionForBone(name);
        }
    }
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

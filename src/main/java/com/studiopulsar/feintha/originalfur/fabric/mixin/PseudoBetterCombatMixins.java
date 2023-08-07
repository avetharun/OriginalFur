package com.studiopulsar.feintha.originalfur.fabric.mixin;

import com.studiopulsar.feintha.originalfur.fabric.AbstractClientPlayerEntityCompatMixins;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.AnimatedHand;
import net.bettercombat.mixin.client.AbstractClientPlayerEntityMixin;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("MixinAnnotationTarget")
@Pseudo
@Mixin(AbstractClientPlayerEntity.class)
public class PseudoBetterCombatMixins implements AbstractClientPlayerEntityCompatMixins {}

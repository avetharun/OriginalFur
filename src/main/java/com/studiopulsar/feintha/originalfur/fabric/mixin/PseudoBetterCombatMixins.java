package com.studiopulsar.feintha.originalfur.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.studiopulsar.feintha.originalfur.fabric.AbstractClientPlayerEntityCompatMixins;
import com.studiopulsar.feintha.originalfur.fabric.IPlayerEntityMixins;
import net.bettercombat.BetterCombat;
import net.bettercombat.api.client.BetterCombatClientEvents;
import net.bettercombat.client.BetterCombatClient;
import net.bettercombat.client.ClientNetwork;
import net.bettercombat.client.animation.PlayerAttackAnimatable;
import net.bettercombat.logic.AnimatedHand;
import net.bettercombat.mixin.client.AbstractClientPlayerEntityMixin;
import net.bettercombat.network.Packets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("MixinAnnotationTarget")
@Pseudo
@Mixin(ClientNetwork.class)
public class PseudoBetterCombatMixins {
    @Inject(method="lambda$initializeHandlers$0", at=@At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getEntityById(I)Lnet/minecraft/entity/Entity;", shift = At.Shift.AFTER))
    private static void onAnimationState(MinecraftClient client, Packets.AttackAnimation packet, CallbackInfo ci) {
        assert client.world != null;
        Entity entity = client.world.getEntityById(packet.playerId());
        if (entity instanceof PlayerEntity pE) {
            IPlayerEntityMixins ipe = (IPlayerEntityMixins)pE;
            ipe.betterCombat$setSwinging(!packet.animationName().equals(Packets.AttackAnimation.StopSymbol));
        }
    }
}

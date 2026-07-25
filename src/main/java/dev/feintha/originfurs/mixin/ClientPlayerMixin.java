package dev.feintha.originfurs.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.feintha.originfurs.networking.PacketIDs;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.registry.ModComponents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class ClientPlayerMixin {
    @Inject(method="<init>", at=@At("TAIL"))
    void initMixin(CallbackInfo ci, @Local(argsOnly = true, name = "world") World world) {
        if (world.isClient()) {
            var self = ((PlayerEntity)(Object)this);
        }
    }
}

package com.studiopulsar.feintha.originalfur.fabric.mixin;

import com.google.gson.JsonParser;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.studiopulsar.feintha.originalfur.fabric.OriginEvents;
import com.studiopulsar.feintha.originalfur.fabric.client.OriginalFurClient;
import io.github.apace100.calio.data.SerializableData;
import io.github.apace100.origins.networking.ModPacketsS2C;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;

@Pseudo
@Mixin(OriginRegistry.class)
public class OriginsRegistryMixin {

    @Mixin(value = ModPacketsS2C.class, remap = false)
    public static class OriginListMixin$ORIF{
        @ModifyReturnValue(method="lambda$receiveOriginList$2", at=@At(value="RETURN"))
        private static Origin onRecievedOriginsDefineMissingMixin(Origin original) throws IOException {
            var manager = MinecraftClient.getInstance().getResourceManager();
            String path = "furs";
            Identifier id = original.getIdentifier();
            var fur = OriginalFurClient.FUR_RESOURCES.getOrDefault(id, null);
            if (fur == null) {
                OriginalFurClient.FUR_REGISTRY.put(id, new OriginalFurClient.OriginFur(JsonParser.parseString("{}").getAsJsonObject()));
            } else {
                OriginalFurClient.FUR_REGISTRY.put(id, new  OriginalFurClient.OriginFur(JsonParser.parseString(new String(fur.getInputStream().readAllBytes())).getAsJsonObject()));
            }
            return original;
        }
    }
    @Inject(method="register(Lnet/minecraft/util/Identifier;Lio/github/apace100/origins/origin/Origin;)Lio/github/apace100/origins/origin/Origin;", at=@At("RETURN"))
    private static void registerMixin(Identifier id, Origin origin, CallbackInfoReturnable<Origin> cir){
        OriginEvents.ORIGIN_REGISTRY_ADDED_EVENT.invoker().onOriginAddedToRegistry(origin,id);
    }
}

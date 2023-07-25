package com.studiopulsar.feintha.originalfur.fabric.mixin;

import com.google.gson.JsonParser;
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

    @Mixin(ModPacketsS2C.class)
    public static class OriginListMixin$ORIF{
        @Inject(locals= LocalCapture.CAPTURE_FAILHARD,
                method="lambda$receiveOriginList$4", at=@At(value="INVOKE", target = "Lio/github/apace100/origins/origin/OriginRegistry;register(Lnet/minecraft/util/Identifier;Lio/github/apace100/origins/origin/Origin;)Lio/github/apace100/origins/origin/Origin;", shift = At.Shift.AFTER))
        private static void onRecievedOriginsMixin(Identifier[] ids, SerializableData.Instance[] origins, CallbackInfo ci, int i) throws IOException {
            var manager = MinecraftClient.getInstance().getResourceManager();
            String path = "furs";
            Identifier id = new Identifier("origin", ids[i].getPath());
            System.out.println(id);
            var fur = OriginalFurClient.FUR_RESOURCES.getOrDefault(id, null);
            if (fur == null) {
                OriginalFurClient.FUR_REGISTRY.put(id.getPath(), new OriginalFurClient.OriginFur(JsonParser.parseString("{}").getAsJsonObject()));
            } else {
                OriginalFurClient.FUR_REGISTRY.put(id.getPath(), new  OriginalFurClient.OriginFur(JsonParser.parseString(new String(fur.getInputStream().readAllBytes())).getAsJsonObject()));
            }
        }
    }
    @Inject(method="register(Lnet/minecraft/util/Identifier;Lio/github/apace100/origins/origin/Origin;)Lio/github/apace100/origins/origin/Origin;", at=@At("RETURN"))
    private static void registerMixin(Identifier id, Origin origin, CallbackInfoReturnable<Origin> cir){
        OriginEvents.ORIGIN_REGISTRY_ADDED_EVENT.invoker().onOriginAddedToRegistry(origin,id);
    }
}

package com.studiopulsar.feintha.originalfur.mixin;

import com.studiopulsar.feintha.originalfur.OriginEvents;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginRegistry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(OriginRegistry.class)
public class OriginsRegistryMixin {
    @Inject(method="register(Lnet/minecraft/util/Identifier;Lio/github/apace100/origins/origin/Origin;)Lio/github/apace100/origins/origin/Origin;", at=@At("RETURN"))
    private static void registerMixin(Identifier id, Origin origin, CallbackInfoReturnable<Origin> cir){
        OriginEvents.ORIGIN_REGISTRY_ADDED_EVENT.invoker().onOriginAddedToRegistry(origin,id);
    }
}

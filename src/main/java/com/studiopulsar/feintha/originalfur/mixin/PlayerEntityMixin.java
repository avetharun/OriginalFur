package com.studiopulsar.feintha.originalfur.mixin;

import com.studiopulsar.feintha.originalfur.IPlayerEntityMixins;
import com.studiopulsar.feintha.originalfur.client.OriginFurModel;
import com.studiopulsar.feintha.originalfur.client.OriginalFurClient;
import io.github.apace100.apoli.power.PowerType;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.component.PlayerOriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginLayers;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;

@Mixin(ClientPlayerEntity.class)
public class PlayerEntityMixin implements IPlayerEntityMixins {
    @Inject(method="applyDamage", at=@At("TAIL"))
    void applyDamageMixin(DamageSource source, float amount, CallbackInfo ci){
        PlayerEntity e = (PlayerEntity)(Object)this;
        Origin origin = originalFur$currentOrigins()[0];
//        if (origin == null) {return;}
            Identifier id = origin.getIdentifier();
            var fur = OriginalFurClient.FUR_REGISTRY.get(id.getPath());
            if (fur == null) {return;}
            OriginFurModel m = (OriginFurModel) fur.getGeoModel();
            var r = m.getHurtSoundResource();
            if (r.equals(new Identifier("null"))) {return;}
            var sE = Registries.SOUND_EVENT.get(r);
            if (sE == null) {return;}
            System.out.println(sE);
            e.getWorld().playSound(null, e.getBlockPos(), sE, SoundCategory.PLAYERS);

    }
    @Override
    public Origin[] originalFur$currentOrigins() {
        PlayerOriginComponent c = (PlayerOriginComponent) ModComponents.ORIGIN.get(this);
        var v = c.getOrigins().values();
        return v.toArray(new Origin[0]);
    }
}

package com.studiopulsar.feintha.originalfur.fabric.mixin;

import com.studiopulsar.feintha.originalfur.fabric.IPlayerEntityMixins;
import com.studiopulsar.feintha.originalfur.fabric.OriginFurModel;
import com.studiopulsar.feintha.originalfur.fabric.client.OriginalFurClient;
import io.github.apace100.origins.component.PlayerOriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(ClientPlayerEntity.class)
public class PlayerEntityMixin implements IPlayerEntityMixins {
    @Mixin(OtherClientPlayerEntity.class)
    public static class OtherClientPlayerEntityMixin implements IPlayerEntityMixins{

        @Unique
        boolean betterCombat$isSwinging = false;
        @Override
        public void betterCombat$setSwinging(boolean value) {
            betterCombat$isSwinging = value;
        }
        @Override
        public boolean betterCombat$isSwinging() {
            return betterCombat$isSwinging;
        }

    }

    @Pseudo
    @Mixin(AbstractClientPlayerEntity.class)
    public static class ChangeElytraTextureMixin implements IPlayerEntityMixins{
        @Inject(method="getElytraTexture", at=@At("JUMP"), cancellable = true)
        void getElytraTextureMixin(CallbackInfoReturnable<Identifier> cir) {
            OriginFurModel m = originalFur$getCurrentModel();
            if (m == null) {return;}
            if (!m.hasCustomElytraTexture()) {
                return;
            }
            var eT = m.getElytraTexture();
            cir.setReturnValue(eT);
            cir.cancel();
        }
    }

    @Override
    public OriginFurModel originalFur$getCurrentModel() {
        var origin = originalFur$currentOrigins()[0];
        Identifier id = origin.getIdentifier();
        var opt = OriginalFurClient.FUR_REGISTRY.get(id);
        if (opt == null) {
            opt = OriginalFurClient.FUR_REGISTRY.get(Identifier.of("origins", id.getPath()));
            if (opt ==null) {
                System.out.println("[Origin Furs] Fur was null in entity mixin: " + id + ". This should NEVER happen! Report this to the devs!");
                System.out.println(OriginalFurClient.FUR_REGISTRY.keySet());
                System.out.println("[Origin Furs] Listed all registered furs. Please include the previous line!");
                System.out.println("[Origin Furs] Please copy all mods, and this log file and create an issue:");
                System.out.println("[Origin Furs] https://github.com/avetharun/OriginalFur/issues/new");
            }
            return null;
        }
        return (OriginFurModel) opt.getGeoModel();
    }

    @Inject(method="applyDamage", at=@At("TAIL"))
    void applyDamageMixin(DamageSource source, float amount, CallbackInfo ci){
        PlayerEntity e = (PlayerEntity)(Object)this;
        OriginFurModel m = originalFur$getCurrentModel();
        if (m == null) {return;}
        var r = m.getHurtSoundResource();
        if (r.equals(new Identifier("null"))) {return;}
        var sE = Registries.SOUND_EVENT.get(r);
        if (sE == null) {return;}
        e.getWorld().playSound(null, e.getBlockPos(), sE, SoundCategory.PLAYERS);

    }
    @Override
    public Origin[] originalFur$currentOrigins() {
        PlayerOriginComponent c = (PlayerOriginComponent) ModComponents.ORIGIN.get(this);
        var v = c.getOrigins().values();
        return v.toArray(new Origin[0]);
    }
}

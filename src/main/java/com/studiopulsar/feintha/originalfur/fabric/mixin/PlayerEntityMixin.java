package com.studiopulsar.feintha.originalfur.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.studiopulsar.feintha.originalfur.fabric.AbstractClientPlayerEntityCompatMixins;
import com.studiopulsar.feintha.originalfur.fabric.IPlayerEntityMixins;
import com.studiopulsar.feintha.originalfur.fabric.OriginFurModel;
import com.studiopulsar.feintha.originalfur.fabric.client.OriginalFurClient;
import io.github.apace100.origins.component.PlayerOriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Pseudo
@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements IPlayerEntityMixins {
    @Mixin(ServerPlayerEntity.class)
    public static class ServerPlayerEntityMixin implements IPlayerEntityMixins{

        @Override
        public ArrayList<OriginalFurClient.OriginFur> originalFur$getCurrentFurs() {
            // Not supported on server, this shouldn't ever be run!
            return new ArrayList<>();
        }
        @Override
        public ArrayList<Origin> originalFur$currentOrigins() {
            PlayerOriginComponent c = (PlayerOriginComponent) ModComponents.ORIGIN.get(this);
            var v = c.getOrigins().values();
            return new ArrayList<>(v);
        }
    }
    @Pseudo
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

    @Mixin(AbstractClientPlayerEntity.class)
    public static class ChangeElytraTextureMixin implements IPlayerEntityMixins, AbstractClientPlayerEntityCompatMixins {
        @Unique
        Origin[] orif$origins = new Origin[]{};

        @Override
        public ArrayList<Origin> originfurs$getOrigins() {
            return new ArrayList<>(List.of(orif$origins));
        }

        @Override
        public void originfurs$setOrigins(ArrayList<Identifier> origins) {
            AbstractClientPlayerEntityCompatMixins.super.originfurs$setOrigins(origins);
            ArrayList<Origin> oA = new ArrayList<>();
            for (Identifier oID : origins) {
                var orig = OriginRegistry.get(oID);
                if (orig != null){
                    oA.add(orig);
                }
            }
        }
        @Inject(method="getElytraTexture", at=@At("HEAD"), cancellable = true)
        void getElytraTextureMixin(CallbackInfoReturnable<Identifier> cir) {
            boolean hE = false;
            for (var fur : originalFur$getCurrentModels()) {
                if (fur == null) {
                    continue;
                }
                if (!fur.hasCustomElytraTexture()) {
                    continue;
                }
                var eT = fur.getElytraTexture();
                cir.setReturnValue(eT);
                cir.cancel();
            }
        }
    }
    @Override
    public ArrayList<OriginalFurClient.OriginFur> originalFur$getCurrentFurs() {
        var cO = originalFur$currentOrigins();
        ArrayList<OriginalFurClient.OriginFur> furs = new ArrayList<>();
        if (cO.size() == 0) {return furs;}
        for (var origin : cO){
            try {
                Identifier id = origin.getIdentifier();
                id = Identifier.of(id.getNamespace(), id.getPath().replace('/', '.').replace('\\', '.'));
                var opt = OriginalFurClient.FUR_REGISTRY.get(id);
                if (opt == null) {
                    opt = OriginalFurClient.FUR_REGISTRY.get(Identifier.of("origins", id.getPath()));
                    if (opt == null) {
                        System.out.println("[Origin Furs] Fur was null in entity mixin: " + id + ". This should NEVER happen! Report this to the devs!");
                        System.out.println(OriginalFurClient.FUR_REGISTRY.keySet());
                        System.out.println("[Origin Furs] Listed all registered furs. Please include the previous line!");
                        System.out.println("[Origin Furs] Please copy all mods, and this log file and create an issue:");
                        System.out.println("[Origin Furs] https://github.com/avetharun/OriginalFur/issues/new");
                        continue;
                    }
                }
                opt.currentAssociatedOrigin = origin;
                furs.add(opt);
            } catch (IndexOutOfBoundsException IOBE) {
                System.err.println("[Origin Furs] Something very wrong happened!");
                System.err.println(OriginalFurClient.FUR_REGISTRY.keySet());
                cO.forEach(System.out::println);
                System.err.println("[Origin Furs] Listed all registered furs. Please include the previous line!");
                System.err.println("[Origin Furs] Please copy all mods, and this log file and create an issue:");
                System.err.println("[Origin Furs] https://github.com/avetharun/OriginalFur/issues/new");
                throw new RuntimeException(IOBE.fillInStackTrace().toString());
            }
        }
        return furs;
    }
    @Override
    public ArrayList<Origin> originalFur$currentOrigins() {
        PlayerOriginComponent c = (PlayerOriginComponent) ModComponents.ORIGIN.get(this);
        var v = c.getOrigins().values();
        return new ArrayList<>(v);
    }
}

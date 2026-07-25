package dev.feintha.originfurs.mixin;

import io.github.apace100.apoli.component.PowerHolderComponent;
import io.github.apace100.apoli.component.PowerHolderComponentImpl;
import io.github.apace100.apoli.power.Power;
import io.github.apace100.apoli.power.PowerTypeRegistry;
import io.github.apace100.apoli.power.PowerTypes;
import io.github.apace100.origins.command.OriginCommand;
import io.github.apace100.origins.component.OriginComponent;
import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.origin.OriginLayer;
import io.github.apace100.origins.origin.OriginRegistry;
import io.github.apace100.origins.registry.ModComponents;
import io.github.apace100.origins.screen.OriginDisplayScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OriginDisplayScreen.class)
public class OriginPickScreenMixin {
    @Shadow
    protected int guiLeft;
    @Shadow
    protected int guiTop;
    @Shadow
    private OriginLayer layer;
    @Shadow
    private Origin origin;
    @Shadow
    private boolean isOriginRandom;
    @Nullable
    private PlayerEntity player;
    @Inject(method="init", at=@At("TAIL"))
    void initPlayerMixin(CallbackInfo ci) {
        player = new AbstractClientPlayerEntity(MinecraftClient.getInstance().world, MinecraftClient.getInstance().player.getGameProfile()) {
            @Override
            public boolean isSpectator() {
                return super.isSpectator();
            }

            @Override
            public boolean shouldRenderName() {
                return false;
            }
        };


    }
    @Unique
    float dRandomOrigin = 0;
    Origin currentOrigin = null;
    int index = 0;
    @Inject(method="render", at= @At(value = "INVOKE", target = "Lio/github/apace100/origins/screen/OriginDisplayScreen;renderBadgeTooltip(Lnet/minecraft/client/gui/DrawContext;II)V"))
    void renderRagdollMixin(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        var self = (OriginDisplayScreen)(Object)this;

        int x = this.guiLeft;
        int y = this.guiTop;

        assert player != null;
        if (isOriginRandom) {
            if (dRandomOrigin <= 0f) {
                var available = layer.getRandomOrigins(MinecraftClient.getInstance().player);
                index += 1;
                index %= available.size();
                currentOrigin = OriginRegistry.get(available.get(index));
                dRandomOrigin = 60f;
            }
            dRandomOrigin -= MinecraftClient.getInstance().getTickDelta();
        } else {
            dRandomOrigin = 60f;
            currentOrigin = origin;
        }
        OriginComponent originComponent = (OriginComponent)ModComponents.ORIGIN.get(player);
        originComponent.setOrigin(layer, currentOrigin);
        PowerHolderComponent component = (PowerHolderComponent)PowerHolderComponent.KEY.get(player);
        var power = PowerTypeRegistry.get(Identifier.of("origins", "invisibility"));
        component.removePower(power, Identifier.of("origins", "phantom"));
        originComponent.sync();
        InventoryScreen.drawEntity(context, x - 30, y + 75, 30, (float)(x - 30) - mouseX, (float)(y + 75 - 50) - mouseY, player);
    }
}

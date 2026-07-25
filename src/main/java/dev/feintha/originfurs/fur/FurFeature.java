package dev.feintha.originfurs.fur;

import dev.feintha.originfurs.client.OriginFursClient;
import io.github.apace100.origins.registry.ModComponents;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Pair;
import net.minecraft.util.math.MathHelper;

public class FurFeature <T extends LivingEntity, M extends BipedEntityModel<T>, A extends BipedEntityModel<T>> extends FeatureRenderer<T, M> {
    public FurFeature(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {

        float yaw = MathHelper.lerp(tickDelta, entity.prevYaw, entity.getYaw()) - MathHelper.lerp(tickDelta, entity.prevBodyYaw, entity.bodyYaw);
        ModComponents.ORIGIN.get(entity).getOrigins().values().forEach(origin -> {
            var model = OriginFursClient.CACHED_MODELS.getOrDefault(origin.getIdentifier(), null);
            if (model != null) {
                model.render((PlayerEntityModel<AbstractClientPlayerEntity>)this.getContextModel(), (AbstractClientPlayerEntity) entity, matrices, vertexConsumers, headYaw, tickDelta, light);
            }
        });
    }
}

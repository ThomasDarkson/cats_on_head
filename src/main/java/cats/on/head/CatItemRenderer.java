package cats.on.head;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class CatItemRenderer implements TrinketRenderer {
    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntityRenderState> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntityRenderState state, float limbAngle, float limbDistance) {
        if (contextModel instanceof BipedEntityModel bipedEntityModel) {
            BipedEntityRenderState bipedState = (BipedEntityRenderState) state;
            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            matrices.push();

            matrices.multiply(RotationAxis.POSITIVE_Z.rotation(bipedEntityModel.head.roll));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotation(bipedEntityModel.head.yaw));
            matrices.multiply(RotationAxis.POSITIVE_X.rotation(bipedEntityModel.head.pitch));

            if (bipedState.isInSneakingPose || bipedState.isInPose(EntityPose.SITTING)) {
                matrices.translate(0.0F, 0.25F, 0.0F);
            }

            matrices.translate(-0.0F, -0.8F, -0.0F);
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
            matrices.scale(0.625F, -0.625F, -0.625F);

            itemRenderer.renderItem(stack, ItemDisplayContext.HEAD, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, null, 0);

            matrices.pop();
        }
    }
}

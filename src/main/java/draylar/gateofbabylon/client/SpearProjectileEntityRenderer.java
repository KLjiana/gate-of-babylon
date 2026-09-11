package draylar.gateofbabylon.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import draylar.gateofbabylon.entity.SpearProjectileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;

public class SpearProjectileEntityRenderer extends EntityRenderer<SpearProjectileEntity> {

    private final ItemRenderer itemRenderer;
    private final boolean lit;

    public SpearProjectileEntityRenderer(EntityRendererProvider.Context context, ItemRenderer itemRenderer, float scale, boolean lit) {
        super(context);
        this.itemRenderer = itemRenderer;
        this.lit = lit;
    }

    public SpearProjectileEntityRenderer(EntityRendererProvider.Context context, ItemRenderer itemRenderer) {
        this(context, itemRenderer, 1.0F, false);
    }

    @Override
    protected int getBlockLightLevel(SpearProjectileEntity entity, BlockPos blockPos) {
        return this.lit ? 15 : super.getBlockLightLevel(entity, blockPos);
    }

    @Override
    public void render(SpearProjectileEntity entity, float yaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.scale(1.5F, 1.5F, 1.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) + 45.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        this.itemRenderer.renderStatic(entity.getStack(), ItemDisplayContext.FIXED, packedLight,
                OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), 0);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(SpearProjectileEntity entity) {
        return net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS;
    }
}

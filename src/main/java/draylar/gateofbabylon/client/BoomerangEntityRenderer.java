package draylar.gateofbabylon.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import draylar.gateofbabylon.entity.BoomerangEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;

public class BoomerangEntityRenderer extends EntityRenderer<BoomerangEntity> {

    private final ItemRenderer itemRenderer;

    public BoomerangEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(BoomerangEntity boomerang, float yaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float lerpedAge = Mth.lerp(partialTick, boomerang.tickCount - 1, boomerang.tickCount);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(lerpedAge));
        this.itemRenderer.renderStatic(boomerang.getStack(), ItemDisplayContext.FIXED, packedLight,
                OverlayTexture.NO_OVERLAY, poseStack, buffer, boomerang.level(), 0);
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(BoomerangEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}

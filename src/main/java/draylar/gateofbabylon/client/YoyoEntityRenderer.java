package draylar.gateofbabylon.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import draylar.gateofbabylon.GateOfBabylonClient;
import draylar.gateofbabylon.entity.YoyoEntity;
import draylar.gateofbabylon.registry.GOBItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LightLayer;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class YoyoEntityRenderer extends EntityRenderer<YoyoEntity> {

    private static final Map<Item, ModelResourceLocation> ITEM_TO_MODEL = new HashMap<>();

    static {
        ITEM_TO_MODEL.put(GOBItems.WOODEN_YOYO.get(), GateOfBabylonClient.WOODEN_YOYO_MODEL);
        ITEM_TO_MODEL.put(GOBItems.STONE_YOYO.get(), GateOfBabylonClient.STONE_YOYO_MODEL);
        ITEM_TO_MODEL.put(GOBItems.IRON_YOYO.get(), GateOfBabylonClient.IRON_YOYO_MODEL);
        ITEM_TO_MODEL.put(GOBItems.GOLDEN_YOYO.get(), GateOfBabylonClient.GOLDEN_YOYO_MODEL);
        ITEM_TO_MODEL.put(GOBItems.DIAMOND_YOYO.get(), GateOfBabylonClient.DIAMOND_YOYO_MODEL);
        ITEM_TO_MODEL.put(GOBItems.NETHERITE_YOYO.get(), GateOfBabylonClient.NETHERITE_YOYO_MODEL);
    }

    public YoyoEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(YoyoEntity yoyo, float yaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float lerpedAge = Mth.lerp(partialTick, yoyo.tickCount - 1, yoyo.tickCount);

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.15D, 0.0D);
        poseStack.mulPose(entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.XP.rotation(lerpedAge));

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(ITEM_TO_MODEL.get(yoyo.getStack().getItem()));
        if (model != null) {
            PoseStack.Pose pose = poseStack.last();
            VertexConsumer consumer = buffer.getBuffer(RenderType.solid());
            model.getQuads(null, null, yoyo.level().random).forEach(quad ->
                    consumer.putBulkData(pose, quad, 1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.NO_OVERLAY));
        }
        poseStack.popPose();

        Optional<UUID> owner = yoyo.getOwner();
        if (owner.isPresent()) {
            Player player = yoyo.level().getPlayerByUUID(owner.get());
            if (player != null) {
                renderString(yoyo, partialTick, poseStack, buffer, player);
            }
        }

        poseStack.popPose();
    }

    private void renderString(Entity yoyo, float partialTick, PoseStack poseStack,
                              MultiBufferSource buffer, Entity player) {
        poseStack.pushPose();
        Vec3Accessor positions = new Vec3Accessor(yoyo.getPosition(partialTick), player.getPosition(partialTick));
        double e = positions.player().z;
        double g = -positions.player().x;
        double h = Mth.lerp(partialTick, player.xOld, player.getX()) + e;
        double i = Mth.lerp(partialTick, player.yOld, player.getY()) + positions.player().y;
        double j = Mth.lerp(partialTick, player.zOld, player.getZ()) + g;
        poseStack.translate(e, positions.player().y, g);
        float k = (float) (positions.yoyo().x - h);
        float l = (float) (positions.yoyo().y - i);
        float m = (float) (positions.yoyo().z - j);
        VertexConsumer consumer = buffer.getBuffer(RenderType.leash());
        Matrix4f matrix = poseStack.last().pose();
        double o = Mth.fastInvSqrt(k * k + m * m) * 0.025F / 2.0F;
        double p = m * o;
        double q = k * o;
        BlockPos playerPos = BlockPos.containing(player.getEyePosition(partialTick));
        BlockPos yoyoPos = BlockPos.containing(yoyo.getEyePosition(partialTick));
        int r = getYoyoBlockLight(player, playerPos);
        int s = getYoyoBlockLight(yoyo, yoyoPos);
        int t = player.level().getBrightness(LightLayer.SKY, playerPos);
        int u = yoyo.level().getBrightness(LightLayer.SKY, yoyoPos);
        renderSide(consumer, matrix, k, l, m, r, s, t, u, 0.025F, 0.025F, p, q);
        renderSide(consumer, matrix, k, l, m, r, s, t, u, 0.025F, 0.0F, p, q);
        poseStack.popPose();
    }

    public static void renderSide(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z,
                                  int blockLightStart, int blockLightEnd, int skyLightStart, int skyLightEnd,
                                  float width, float offset, double sideX, double sideZ) {
        for (int index = 0; index < 24; ++index) {
            float progress = (float) index / 23.0F;
            int blockLight = (int) Mth.lerp(progress, (float) blockLightStart, (float) blockLightEnd);
            int skyLight = (int) Mth.lerp(progress, (float) skyLightStart, (float) skyLightEnd);
            int light = LightTexture.pack(blockLight, skyLight);
            addVertexPair(consumer, matrix, light, x, y, z, width, offset, 24, index, false, sideX, sideZ);
            addVertexPair(consumer, matrix, light, x, y, z, width, offset, 24, index + 1, true, sideX, sideZ);
        }
    }

    public static void addVertexPair(VertexConsumer consumer, Matrix4f matrix, int light,
                                     float x, float y, float z, float width, float offset,
                                     int segmentCount, int segment, boolean second,
                                     double sideX, double sideZ) {
        float red = 0.5F;
        float green = 0.4F;
        float blue = 0.3F;
        if (segment % 2 == 0) {
            red *= 0.7F;
            green *= 0.7F;
            blue *= 0.7F;
        }

        float progress = (float) segment / segmentCount;
        float px = x * progress;
        float py = y > 0.0F ? y * progress * progress : y - y * (1.0F - progress) * (1.0F - progress);
        float pz = z * progress;
        if (!second) {
            vertex(consumer, matrix, light, px + (float) sideX, py + offset - (float) sideZ, pz - (float) sideZ, red, green, blue);
        }
        vertex(consumer, matrix, light, px - (float) sideX, py + (float) sideZ, pz + (float) sideZ, red, green, blue);
        if (second) {
            vertex(consumer, matrix, light, px + (float) sideX, py + offset - (float) sideZ, pz - (float) sideZ, red, green, blue);
        }
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, int light,
                               float x, float y, float z, float red, float green, float blue) {
        consumer.vertex(matrix, x, y, z).color(red, green, blue, 1.0F).uv2(light).endVertex();
    }

    private static int getYoyoBlockLight(Entity entity, BlockPos blockPos) {
        return entity.isOnFire() ? 15 : entity.level().getBrightness(LightLayer.BLOCK, blockPos);
    }

    @Override
    public ResourceLocation getTextureLocation(YoyoEntity entity) {
        return net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS;
    }

    private record Vec3Accessor(net.minecraft.world.phys.Vec3 yoyo, net.minecraft.world.phys.Vec3 player) {
    }
}

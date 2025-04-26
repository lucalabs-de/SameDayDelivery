package de.lucalabs.delivery.renderer;

import de.lucalabs.delivery.SameDayDelivery;
import de.lucalabs.delivery.entities.PlacedShippingLabel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;

public class PlacedShippingLabelRenderer extends EntityRenderer<PlacedShippingLabel> {

    private static final Identifier SHIPPING_LABEL = //new ModelIdentifier(SameDayDelivery.MOD_ID, "entity/shipping_label_placed", "inventory");
            new Identifier(SameDayDelivery.MOD_ID, "entity/shipping_label_placed");

    public PlacedShippingLabelRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    public void render(
            final PlacedShippingLabel entity,
            final float yaw,
            final float delta,
            final MatrixStack stack,
            final VertexConsumerProvider buf,
            final int light) {
        super.render(entity, yaw, delta, stack, buf, light);
        stack.push();
        Direction direction = entity.getHorizontalFacing();
        Vec3d posOffset = getPositionOffset(entity, delta);
        stack.translate(-posOffset.getX(), -posOffset.getY(), -posOffset.getZ());
        stack.translate((double) direction.getOffsetX() * 0.46875, (double) direction.getOffsetY() * 0.46875, (double) direction.getOffsetZ() * 0.46875);
        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getPitch()));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - entity.getYaw()));

        final BakedModel model = MinecraftClient
                .getInstance()
                .getBakedModelManager()
                .getModel(SHIPPING_LABEL);


        stack.push();
//        stack.translate(-0.5F, -0.5F, -0.5F);

        renderBakedModel(
                model,
                stack,
                buf.getBuffer(TexturedRenderLayers.getEntityCutout()),
                1.0F,
                1.0F,
                1.0F,
                light,
                OverlayTexture.DEFAULT_UV);

        stack.pop();

        stack.pop();
    }

    public static void renderBakedModel(
            final BakedModel model,
            final MatrixStack matrix,
            final VertexConsumer buf,
            final float r,
            final float g,
            final float b,
            final int packedLight,
            final int packedOverlay) {

//        model.getTransformation().getTransformation(ModelTransformationMode.FIXED)
//                .apply(false, matrix);

        MatrixStack.Entry lastStack = matrix.peek();

        Random randSource = Random.create();
        for (final Direction side : Direction.values()) {
            randSource.setSeed(42L);
            for (final BakedQuad quad : model.getQuads(null, side, randSource)) {
                buf.quad(lastStack, quad, r, g, b, packedLight, packedOverlay);
            }
        }

        randSource.setSeed(42L);
        for (final BakedQuad quad : model.getQuads(null, null, randSource)) {
            buf.quad(lastStack, quad, r, g, b, packedLight, packedOverlay);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public Identifier getTexture(PlacedShippingLabel entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    @Override
    public Vec3d getPositionOffset(PlacedShippingLabel entity, float f) {
        return new Vec3d(
                (float) entity.getHorizontalFacing().getOffsetX() * 0.3F,
                -0.25,
                (float) entity.getHorizontalFacing().getOffsetZ() * 0.3F);
    }
}

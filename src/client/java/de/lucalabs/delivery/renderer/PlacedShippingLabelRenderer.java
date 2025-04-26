package de.lucalabs.delivery.renderer;

import de.lucalabs.delivery.SameDayDelivery;
import de.lucalabs.delivery.entities.PlacedShippingLabel;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PlacedShippingLabelRenderer extends EntityRenderer<PlacedShippingLabel> {

    public static final Identifier MODEL = //new ModelIdentifier(SameDayDelivery.MOD_ID, "entity/shipping_label_placed", "inventory");
            new Identifier(SameDayDelivery.MOD_ID, "entity/shipping_label_placed");

    private final BlockRenderManager blockRenderManager;

    public PlacedShippingLabelRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.blockRenderManager = context.getBlockRenderManager();
    }

    @Override
    public void render(
            final PlacedShippingLabel entity,
            final float yaw,
            final float delta,
            final MatrixStack stack,
            final VertexConsumerProvider buf,
            final int light) {
//        super.render(entity, yaw, delta, stack, buf, light);
        stack.push();

        // taken from ItemFrame renderer
        Direction direction = entity.getHorizontalFacing();
//        Vec3d posOffset = getPositionOffset(entity, delta);
//        stack.translate(-posOffset.getX(), -posOffset.getY(), -posOffset.getZ());
//
////        stack.translate(direction.getOffsetX() * 0.46875, direction.getOffsetY() * 0.46875, direction.getOffsetZ() * 0.46875);
//        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getPitch()));


        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - entity.getYaw()));
//        // ^
//

//        stack.push();
//
        Direction facingRight = getRightOfNormal(direction);
        Direction facingDown = getDownOfNormal(direction);


//
//        double downOffset = .25;
//        stack.translate(facingDown.getOffsetX() * downOffset, facingDown.getOffsetY() * downOffset, facingDown.getOffsetZ() * downOffset);
//
//        double rightOffset = .4;
//        stack.translate(facingRight.getOffsetX() * rightOffset, facingRight.getOffsetY() * rightOffset, facingRight.getOffsetZ() * rightOffset);
//
//        stack.multiply(getRotation(direction,25));

        stack.multiply(getRotation(facingDown, 180));
        stack.scale(.5f, .5f, .5f);

        stack.translate(0, 0, -1);


        double facingOffset = .3;
        stack.translate(direction.getOffsetX() * facingOffset, direction.getOffsetY() * facingOffset, direction.getOffsetZ() * facingOffset);

        final BakedModel model = MinecraftClient
                .getInstance()
                .getBakedModelManager()
                .getModel(MODEL);

        this.blockRenderManager.getModelRenderer().render(stack.peek(), buf.getBuffer(TexturedRenderLayers.getEntityCutout()), null, model, 1.0F, 1.0F, 1.0F, light, OverlayTexture.DEFAULT_UV);

//        renderBakedModel(
//                model,
//                stack,
//                buf.getBuffer(TexturedRenderLayers.getEntityCutout()),
//                1.0F,
//                1.0F,
//                1.0F,
//                light,
//                OverlayTexture.DEFAULT_UV);

//        stack.pop();
//        stack.pop();
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
                0,
                -0.25,
                0);
    }

    private Direction getRightOfNormal(Direction normal) {
        return switch (normal) {
            case DOWN, SOUTH -> Direction.WEST;
            case UP, NORTH -> Direction.EAST;
            case EAST -> Direction.NORTH;
            case WEST -> Direction.SOUTH;
        };
    }

    private Direction getDownOfNormal(Direction normal) {
        return switch (normal) {
            case EAST, WEST, SOUTH, NORTH -> Direction.DOWN;
            case UP -> Direction.NORTH;
            case DOWN ->  Direction.SOUTH;
        };
    }

    private Quaternionf getRotation(Direction axis, float degrees) {
       return RotationAxis
               .of(new Vector3f((float) axis.getOffsetX(), (float) axis.getOffsetY(), (float) axis.getOffsetZ()))
               .rotationDegrees(degrees);
    }
}

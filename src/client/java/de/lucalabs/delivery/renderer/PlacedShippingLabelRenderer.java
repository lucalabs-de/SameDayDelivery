package de.lucalabs.delivery.renderer;

import de.lucalabs.delivery.SameDayDelivery;
import de.lucalabs.delivery.entities.PlacedShippingLabel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
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

        float scale = .5F;
        float invScale = 1;

        stack.scale(scale, scale, scale);

        Direction facing = entity.getHorizontalFacing();
        Direction facingDown = getDownOfNormal(facing);

        stack.multiply(getRotation(facingDown, 180.0F));

        Vec3d initialOffset = this.getPositionOffset(entity, delta).multiply(-invScale);
        stack.translate(-initialOffset.getX(), -initialOffset.getY(), -initialOffset.getZ());

        double d = 0.46875 * invScale;
        stack.translate(facing.getOffsetX() * d, facing.getOffsetY() * d, facing.getOffsetZ() * d);

        stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getPitch()));
        stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - entity.getYaw()));
        stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-20.0F));

        stack.translate(-0.5F * invScale, -0.5 * invScale, -.35 * invScale);

        final BakedModel model = MinecraftClient
                .getInstance()
                .getBakedModelManager()
                .getModel(MODEL);

        this.blockRenderManager.getModelRenderer().render(stack.peek(), buf.getBuffer(TexturedRenderLayers.getEntityCutout()), null, model, 1.0F, 1.0F, 1.0F, light, OverlayTexture.DEFAULT_UV);

        stack.pop();
    }

    @SuppressWarnings("deprecation")
    @Override
    public Identifier getTexture(PlacedShippingLabel entity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }

    public Vec3d getPositionOffset(PlacedShippingLabel entity, float f) {
        return new Vec3d(entity.getHorizontalFacing().getOffsetX() * 0.3F, 0.25 * MathHelper.clamp(entity.getHorizontalFacing().getOffsetY(), -1, 1), entity.getHorizontalFacing().getOffsetZ() * 0.3F);
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
            case DOWN -> Direction.SOUTH;
        };
    }

    private Quaternionf getRotation(Direction axis, float degrees) {
        return RotationAxis
                .of(new Vector3f((float) axis.getOffsetX(), (float) axis.getOffsetY(), (float) axis.getOffsetZ()))
                .rotationDegrees(degrees);
    }
}

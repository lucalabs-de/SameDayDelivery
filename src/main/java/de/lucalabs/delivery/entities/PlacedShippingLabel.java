package de.lucalabs.delivery.entities;

import de.lucalabs.delivery.tags.Tags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PlacedShippingLabel extends AbstractDecorationEntity {
    protected PlacedShippingLabel(EntityType<? extends AbstractDecorationEntity> type, World world) {
        super(type, world);
    }

    protected PlacedShippingLabel(final World world) {
        this(SddEntities.SHIPPING_LABEL, world);
    }

    protected PlacedShippingLabel(final World world, final BlockPos pos) {
        this(world);
        this.setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    public static PlacedShippingLabel create(final World world, final BlockPos barrel, final Direction facing) {
        final PlacedShippingLabel label = new PlacedShippingLabel(world, barrel);
        label.setFacing(facing);
        world.spawnEntity(label);
        label.onPlace();
        return label;
    }

    @Override
    public int getWidthPixels() {
        return 10;
    }

    @Override
    public int getHeightPixels() {
        return 10;
    }

    @Override
    public void onBreak(@Nullable Entity entity) {
        // stub
    }

    @Override
    public boolean canStayAttached() {
        return !this.getWorld().canSetBlock(this.attachmentPos) || isOnBarrel(this.getWorld().getBlockState(this.attachmentPos));
    }

    @Override
    public void setPosition(final double x, final double y, final double z) {
        super.setPosition(MathHelper.floor(x) + 0.5, MathHelper.floor(y) + 0.5, MathHelper.floor(z) + 0.5);
    }

    @Override
    public void onPlace() {
        // start saving process
    }

    private boolean isOnBarrel(final BlockState state) {
        return state.isSolid() && state.isIn(Tags.BARRELS);
    }
}

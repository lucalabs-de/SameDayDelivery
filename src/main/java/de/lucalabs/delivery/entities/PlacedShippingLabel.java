package de.lucalabs.delivery.entities;

import de.lucalabs.delivery.SameDayDelivery;
import de.lucalabs.delivery.tags.Tags;
import de.lucalabs.delivery.util.TransferUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.Nullable;

public class PlacedShippingLabel extends AbstractDecorationEntity {

    BlockPos attachedBarrel;

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
        final PlacedShippingLabel label = new PlacedShippingLabel(world, barrel.offset(facing, 1));
        label.setFacing(facing);
        label.setBarrel(barrel);
        world.spawnEntity(label);
        label.onPlace();
        return label;
    }

    protected void updateAttachmentPosition() {
        if (this.facing != null) {
            double d = 0.46875;
            double e = (double)this.attachmentPos.getX() + 0.5 - (double)this.facing.getOffsetX() * 0.46875;
            double f = (double)this.attachmentPos.getY() + 0.5 - (double)this.facing.getOffsetY() * 0.46875;
            double g = (double)this.attachmentPos.getZ() + 0.5 - (double)this.facing.getOffsetZ() * 0.46875;
            this.setPos(e, f, g);
            double h = this.getWidthPixels();
            double i = this.getHeightPixels();
            double j = this.getWidthPixels();
            Direction.Axis axis = this.facing.getAxis();
            switch (axis) {
                case X -> h = 1.0;
                case Y -> i = 1.0;
                case Z -> j = 1.0;
            }

            h /= 32.0;
            i /= 32.0;
            j /= 32.0;
            this.setBoundingBox(new Box(e - h, f - i, g - j, e + h, f + i, g + j));
        }
    }

    @Override
    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.0F;
    }

    public void setBarrel(BlockPos pos) {
        this.attachedBarrel = pos;
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
    public SoundCategory getSoundCategory() {
        return SoundCategory.BLOCKS;
    }

    @Override
    public void onBreak(@Nullable Entity entity) {
        // stub
        SameDayDelivery.LOGGER.error("broken");
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean canStayAttached() {
        return !this.getWorld().canSetBlock(this.attachmentPos) || isOnBarrel(this.getWorld().getBlockState(attachedBarrel));
    }

    @Override
    protected void setFacing(Direction facing) {
        Validate.notNull(facing);
        this.facing = facing;
        if (facing.getAxis().isHorizontal()) {
            this.setPitch(0.0F);
            this.setYaw(this.facing.getHorizontal() * 90);
        } else {
            this.setPitch((float) (-90 * facing.getDirection().offset()));
            this.setYaw(0.0F);
        }

        this.prevPitch = this.getPitch();
        this.prevYaw = this.getYaw();
        this.updateAttachmentPosition();
    }

    @Override
    public Box getBoundingBox() {
        return new Box(getBlockPos()).expand(-.05).stretch(Vec3d.of(getHorizontalFacing().getVector()).multiply(-.4));
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        BlockPos barrelPos = getBlockPos();
        BlockEntity blockEntity = getWorld().getBlockEntity(barrelPos);
        if (blockEntity instanceof BarrelBlockEntity barrel) {
            int size = barrel.size(); // Total number of slots (should be 27)
            ItemStack[] barrelStacks = new ItemStack[size];

            for (int i = 0; i < size; i++) {
                ItemStack stack = barrel.getStack(i); // Get the stack at this slot
                if (!stack.isEmpty()) {
                    barrelStacks[i] = stack;
                }
            }

            boolean success = TransferUtils.storeStacksInFile(barrelStacks);

            if (success) {
                // TODO spawn enchanting particles and despawn barrel with items
            } else {
                // TODO spawn smoke particles
            }
        }
        return false;
    }

    @Override
    public void onPlace() {
        this.playSound(SoundEvents.BLOCK_WOOD_PLACE, 1, 0.9F);
    }

    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this, this.facing.getId(), this.getDecorationBlockPos());
    }

    public void onSpawnPacket(EntitySpawnS2CPacket packet) {
        super.onSpawnPacket(packet);
        this.setFacing(Direction.byId(packet.getEntityData()));
    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putByte("Facing", (byte)this.facing.getId());
        nbt.put("Barrel", NbtHelper.fromBlockPos(attachedBarrel));
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setFacing(Direction.byId(nbt.getByte("Facing")));
        this.setBarrel(NbtHelper.toBlockPos(nbt.getCompound("Barrel")));
    }

    @Override
    public boolean shouldRender(double distance) {
        double d = 16.0;
        d *= 64.0 * getRenderDistanceMultiplier();
        return distance < d * d;
    }

    private boolean isOnBarrel(final BlockState state) {
        return state.isSolid() && state.isIn(Tags.BARRELS);
    }
}

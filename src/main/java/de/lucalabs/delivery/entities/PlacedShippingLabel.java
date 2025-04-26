package de.lucalabs.delivery.entities;

import de.lucalabs.delivery.SameDayDelivery;
import de.lucalabs.delivery.tags.Tags;
import de.lucalabs.delivery.util.TransferUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;
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
        label.setPos(barrel.getX(), barrel.getY(), barrel.getZ());
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
        return !this.getWorld().canSetBlock(this.attachmentPos) || isOnBarrel(this.getWorld().getBlockState(this.attachmentPos));
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
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setFacing(Direction.byId(nbt.getByte("Facing")));
        this.setInvisible(nbt.getBoolean("Invisible"));
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

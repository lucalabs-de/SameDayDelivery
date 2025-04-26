package de.lucalabs.delivery.util;

import de.lucalabs.delivery.SameDayDelivery;
import de.lucalabs.delivery.entities.PlacedShippingLabel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.io.File;
import java.io.IOException;

public final class TransferUtils {
    private TransferUtils() {}

    public static boolean isTransferInProgress(World world, BlockPos pos) {
        return !world.getEntitiesByClass(PlacedShippingLabel.class, new Box(pos).expand(.1), e -> true).isEmpty();
    }

    public static boolean isTransferPending() {
        try {
            return FileUtils.getItemFile().exists();
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean storeStacksInFile(ItemStack[] items) {
        try {
            File f = FileUtils.getItemFile();
            NbtList l = new NbtList();

            for (ItemStack i : items) {
                if (i != null) {
                    l.add(i.writeNbt(new NbtCompound()));
                }
            }

            NbtCompound itemsNbt = new NbtCompound();
            itemsNbt.put("items", l);

            NbtIo.write(itemsNbt, f);

            return true;
        } catch (IOException e) {
            SameDayDelivery.LOGGER.error(e.getMessage());
            return false;
        }
    }
}

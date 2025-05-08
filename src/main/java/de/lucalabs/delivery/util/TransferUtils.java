package de.lucalabs.delivery.util;

import de.lucalabs.delivery.SameDayDelivery;
import de.lucalabs.delivery.entities.PlacedShippingLabel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.io.File;
import java.io.IOException;
import java.util.*;

public final class TransferUtils {
    private TransferUtils() {
    }

    public static Optional<ItemStack[]> getNextItemsFromFile() {
        Queue<ItemStack[]> deliveries = getDeliveries();
        if (deliveries.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(deliveries.element());
    }

    public static BlockPos chooseDeliveryPos(World w, BlockPos reference) {
        int radius = 5;
        Random r = w.getRandom();

        int x = reference.getX() + r.nextInt(radius * 2 + 1) - radius;
        int z = reference.getZ() + r.nextInt(radius * 2 + 1) - radius;

        BlockPos choice = new BlockPos(x, reference.getY(), z);

        while (w.getBlockState(choice).isAir()) {
            choice = choice.down();
        }

        while (!w.getBlockState(choice).isAir()) {
            choice = choice.up();
        }

        return choice;
    }

    public static Optional<PlacedShippingLabel> getShippingLabelAtPos(World world, BlockPos pos) {
        List<PlacedShippingLabel> labels = world.getEntitiesByClass(PlacedShippingLabel.class, new Box(pos).expand(.1), e -> true);
        if (labels.isEmpty()) {
            return Optional.empty();
        }

        return labels.stream().filter(l -> pos.equals(l.getBarrel())).findFirst();
    }

    public static boolean isMarkedForDelivery(World world, BlockPos pos) {
        return getShippingLabelAtPos(world, pos).isPresent();
    }

    public static boolean isTransferPending() {
        return !getRawDeliveries().isEmpty();
    }

    public static boolean storeStacksInFile(ItemStack[] items) {
        List<NbtElement> deliveries = getRawDeliveries();
        NbtList itemsNbt = new NbtList();

        for (ItemStack i : items) {
            if (i != null) {
                itemsNbt.add(i.writeNbt(new NbtCompound()));
            }
        }

        deliveries.add(itemsNbt);

        return writeRawDeliveries(deliveries);
    }

    public static void removeLastDelivery() {
        List<NbtElement> deliveries = getRawDeliveries();
        deliveries.remove(0);
        writeRawDeliveries(deliveries);
    }

    private static ArrayList<NbtElement> getRawDeliveries() {
        try {
            File f = FileUtils.getItemFile();
            NbtCompound nbt = NbtIo.read(f);

            if (nbt == null) {
                return new ArrayList<>();
            }

            NbtList deliveriesNbt = nbt.getList("deliveries", NbtElement.LIST_TYPE);

            return new ArrayList<>(deliveriesNbt);
        } catch (IOException e) {
            SameDayDelivery.LOGGER.error(e.getMessage());
        }

        return new ArrayList<>();
    }

    private static boolean writeRawDeliveries(List<NbtElement> deliveries) {
        NbtList deliveriesNbt = new NbtList(deliveries, NbtElement.LIST_TYPE);
        NbtCompound nbt = new NbtCompound();
        nbt.put("deliveries", deliveriesNbt);

        try {
            File f = FileUtils.getItemFile();
            NbtIo.write(nbt, f);
        } catch (IOException e) {
            SameDayDelivery.LOGGER.error(e.getMessage());
            return false;
        }

        return true;
    }

    public static Queue<ItemStack[]> getDeliveries() {
        Queue<ItemStack[]> result = new ArrayDeque<>();

        for (NbtElement delivery : getRawDeliveries()) {
            NbtList itemsNbt = (NbtList) delivery;

            ItemStack[] items = new ItemStack[itemsNbt.size()];

            for (int i = 0; i < items.length; i++) {
                try {
                    items[i] = ItemStack.fromNbt(itemsNbt.getCompound(i));
                } catch (RuntimeException e) {
                    // TODO add broken item
                }
            }

            result.add(items);
        }

        return result;
    }
}

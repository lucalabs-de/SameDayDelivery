package de.lucalabs.delivery;

import de.lucalabs.delivery.entities.SddEntities;
import de.lucalabs.delivery.events.ServerEventHandler;
import de.lucalabs.delivery.items.SddItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class SameDayDelivery implements ModInitializer {

    public static final String MOD_ID = "samedaydelivery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final List<Pair<RegistryKey<World>, BlockPos>> pendingTransfers = new ArrayList<>();

    @Override
    public void onInitialize() {
        SddItems.initialize();
        SddEntities.initialize();

        ServerEventHandler.initialize();

        LOGGER.info("Loaded Same-Day Delivery!");
    }

    public static int addPendingTransfer(World w, BlockPos p) {
        pendingTransfers.add(new Pair<>(w.getRegistryKey(), p));
        return pendingTransfers.size() - 1;
    }
}
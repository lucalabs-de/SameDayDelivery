package de.lucalabs.delivery;

import de.lucalabs.delivery.entities.SddEntities;
import de.lucalabs.delivery.events.ServerEventHandler;
import de.lucalabs.delivery.items.SddItems;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SameDayDelivery implements ModInitializer {

    public static final String MOD_ID = "samedaydelivery";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        SddItems.initialize();
        SddEntities.initialize();

        ServerEventHandler.initialize();

        LOGGER.info("Loaded Same-Day Delivery!");
    }
}
package de.lucalabs.delivery;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SameDayDelivery implements ModInitializer {

	public static final String MOD_ID = "samedaydelivery";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Loaded Same-Day Delivery!");
	}
}
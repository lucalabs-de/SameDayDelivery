package de.lucalabs.delivery.items;

import de.lucalabs.delivery.SameDayDelivery;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

public final class SddItems {
    private SddItems() {}

    public static void initialize() {
        SameDayDelivery.LOGGER.info("initializing items");

        Identifier shippingLabelId = Identifier.of(SameDayDelivery.MOD_ID, ShippingLabel.ID);
        ShippingLabel item = ShippingLabel.getInstance();
    }

}

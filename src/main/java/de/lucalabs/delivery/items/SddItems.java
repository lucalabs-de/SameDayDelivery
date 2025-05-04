package de.lucalabs.delivery.items;

import de.lucalabs.delivery.SameDayDelivery;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class SddItems {

    public static final Item SHIPPING_LABEL = register(ShippingLabel.ID, ShippingLabel.getInstance());
    public static final Item DELIVERY_REQUEST_FORM = register(DeliveryRequestForm.ID, DeliveryRequestForm.getInstance());

    private SddItems() {}

    private static Item register(Identifier id, Item item) {
        return Registry.register(Registries.ITEM, id, item);
    }

    public static void initialize() {
        SameDayDelivery.LOGGER.info("initializing items");
    }

}

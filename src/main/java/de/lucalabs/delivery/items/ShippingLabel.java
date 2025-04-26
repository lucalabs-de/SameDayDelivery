package de.lucalabs.delivery.items;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class ShippingLabel extends Item {

    public static final String ID = "shipping_label";

    protected ShippingLabel(Settings settings) {
        super(settings);
    }

    public static ShippingLabel getInstance() {
        return new ShippingLabel(new FabricItemSettings().rarity(Rarity.RARE));
    }
}

package de.lucalabs.delivery.entities;

import de.lucalabs.delivery.SameDayDelivery;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.function.Supplier;

public final class SddEntities {

    public static final EntityType<PlacedShippingLabel> SHIPPING_LABEL = register("fastener", () ->
            EntityType.Builder.<PlacedShippingLabel>create(PlacedShippingLabel::new, SpawnGroup.MISC)
                    .setDimensions(1.15F, 2.8F)
                    .maxTrackingRange(10)
                    .trackingTickInterval(Integer.MAX_VALUE)
                    .build(SameDayDelivery.MOD_ID + ":label")
    );

    private SddEntities() {}

    private static <T extends Entity> EntityType<T> register(final String name, Supplier<EntityType<T>> supplier) {
        Identifier identifier = Identifier.of(SameDayDelivery.MOD_ID, name);
        return Registry.register(Registries.ENTITY_TYPE, identifier, supplier.get());
    }

    public static void initialize() {
        SameDayDelivery.LOGGER.info("Registering entities");
    }

}

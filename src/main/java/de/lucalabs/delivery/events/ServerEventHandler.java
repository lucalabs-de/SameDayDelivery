package de.lucalabs.delivery.events;

import de.lucalabs.delivery.SameDayDelivery;
import de.lucalabs.delivery.entities.PlacedShippingLabel;
import de.lucalabs.delivery.tags.Tags;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;

public final class ServerEventHandler {
    private ServerEventHandler() {}

    public static void initialize() {
        SameDayDelivery.LOGGER.info("Initializing event handlers");

        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (state.isIn(Tags.BARRELS)) {
                for (Entity entity : world.getEntitiesByClass(PlacedShippingLabel.class, new Box(pos).expand(1.0), e -> true)) {
//                    entity.discard(); TODO add back
                }
            }
        });
    }
}

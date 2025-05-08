package de.lucalabs.delivery.events;

import de.lucalabs.delivery.SameDayDelivery;
import de.lucalabs.delivery.entities.PlacedShippingLabel;
import de.lucalabs.delivery.tags.Tags;
import de.lucalabs.delivery.util.TransferUtils;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public final class ServerEventHandler {
    private ServerEventHandler() {}

    public static void initialize() {
        SameDayDelivery.LOGGER.info("Initializing event handlers");

        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (state.isIn(Tags.BARRELS)) {
                for (Entity entity : world.getEntitiesByClass(PlacedShippingLabel.class, new Box(pos).expand(1.0), e -> true)) {
                    entity.discard();
                }
            }
        });

        UseBlockCallback.EVENT.register((playerEntity, world, hand, blockHitResult) -> {
            if (world.getBlockState(blockHitResult.getBlockPos()).isIn(Tags.BARRELS)) {
                if (TransferUtils.isMarkedForDelivery(world, blockHitResult.getBlockPos())) {
                    return ActionResult.FAIL;
                }
            }

            return ActionResult.PASS;
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            for (Pair<RegistryKey<World>, BlockPos> p : SameDayDelivery.pendingTransfers) {
                World w = server.getWorld(p.getLeft());
                if (w != null) {
                    w.removeBlock(p.getRight(), false);
                }
            }
        });
    }
}

package de.lucalabs.delivery.items;

import de.lucalabs.delivery.SameDayDelivery;
import de.lucalabs.delivery.entities.PlacedShippingLabel;
import de.lucalabs.delivery.tags.Tags;
import de.lucalabs.delivery.util.TransferUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ShippingLabel extends Item {

    public static final Identifier ID = Identifier.of(SameDayDelivery.MOD_ID, "shipping_label");

    protected ShippingLabel(Settings settings) {
        super(settings);
    }

    public static ShippingLabel getInstance() {
        return new ShippingLabel(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1));
    }

    @Override
    public ActionResult useOnBlock(final ItemUsageContext context) {
        final World world = context.getWorld();
        final PlayerEntity player = context.getPlayer();

        if (world.isClient() || player == null) {
            return super.useOnBlock(context);
        }

        final Direction side = context.getSide();
        final BlockPos clickPos = context.getBlockPos();

        if (world.getBlockState(clickPos).isIn(Tags.BARRELS)) {
            if (!TransferUtils.isTransferInProgress(world, clickPos)) {
                player.getMainHandStack().decrement(1);
                PlacedShippingLabel.create(world, clickPos, side);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }
}

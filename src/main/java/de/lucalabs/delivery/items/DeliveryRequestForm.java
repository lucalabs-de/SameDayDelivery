package de.lucalabs.delivery.items;

import de.lucalabs.delivery.SameDayDelivery;
import de.lucalabs.delivery.util.AnimationUtils;
import de.lucalabs.delivery.util.TransferUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class DeliveryRequestForm extends Item {

    public static final Identifier ID = Identifier.of(SameDayDelivery.MOD_ID, "delivery_request");

    public DeliveryRequestForm(Settings settings) {
        super(settings);
    }

    public static DeliveryRequestForm getInstance() {
        return new DeliveryRequestForm(new FabricItemSettings().rarity(Rarity.UNCOMMON).maxCount(1));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("lore.samedaydelivery.delivery_request").formatted(Formatting.DARK_GRAY).formatted(Formatting.ITALIC));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient) {
            boolean success = attemptDelivery((ServerWorld) world, user);
            if (success) {
                user.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1F);
                user.getMainHandStack().decrement(1);
                return TypedActionResult.consume(user.getStackInHand(hand));
            }
        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    private boolean attemptDelivery(ServerWorld w, PlayerEntity p) {
        if (!TransferUtils.isTransferPending()) {
            AnimationUtils.sendNoDeliveryMessage(p);
            return false;
        }

        Optional<ItemStack[]> items = TransferUtils.getNextItemsFromFile();
        if (items.isEmpty()) {
            return false;
        }

        BlockPos deliveryPos = TransferUtils.chooseDeliveryPos(w, p.getBlockPos());
        AnimationUtils.playDeliveryAnimation(w, deliveryPos);

        AnimationUtils.runWithDelay(() -> w.getServer().execute(() -> {
            w.setBlockState(deliveryPos, Blocks.BARREL.getDefaultState());

            if (w.getBlockEntity(deliveryPos) instanceof BarrelBlockEntity barrel) {
                for (int i = 0; i < barrel.size(); i++) {
                    barrel.setStack(i, items.get()[i]);
                }
            }

            TransferUtils.removeLastDelivery();
        }), AnimationUtils.DEFAULT_ANIMATION_DURATION, TimeUnit.SECONDS);

        return true;
    }
}

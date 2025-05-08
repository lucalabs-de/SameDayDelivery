package de.lucalabs.delivery.util;

import me.emafire003.dev.particleanimationlib.Effect;
import me.emafire003.dev.particleanimationlib.effects.AnimatedBallEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class AnimationUtils {
    public static final long DEFAULT_ANIMATION_DURATION = 2;

    private AnimationUtils() {
    }

    public static void playDeliveryAnimation(ServerWorld w, BlockPos p) {
        Vec3d pos = p.toCenterPos();

        Effect e = AnimatedBallEffect.builder(w, ParticleTypes.ENCHANT, pos)
                .originOffset(new Vec3d(0, 0, 0))
                .particles(250)
                .particlesPerIteration(50)
                .build();
        e.runFor(DEFAULT_ANIMATION_DURATION + .5);

        w.playSound(
                null,
                p,
                SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT,
                SoundCategory.BLOCKS,
                .8F,
                .5F);

        runWithDelay(() -> w.getServer().execute(() -> w.playSound(
                null,
                p,
                SoundEvents.BLOCK_WOOD_PLACE,
                SoundCategory.BLOCKS,
                1,
                .5F)), DEFAULT_ANIMATION_DURATION, TimeUnit.SECONDS);
    }

    public static void playTransferAnimation(ServerWorld w, BlockPos p) {
        Vec3d pos = p.toCenterPos();

        Effect e = AnimatedBallEffect.builder(w, ParticleTypes.ENCHANT, pos)
                .originOffset(new Vec3d(0, 0, 0))
                .particles(250)
                .particlesPerIteration(50)
                .build();
        e.runFor(DEFAULT_ANIMATION_DURATION + .5);

        runWithDelay(() -> w.getServer().execute(() -> w.playSound(
                null,
                p,
                SoundEvents.ENTITY_ENDERMAN_TELEPORT,
                SoundCategory.BLOCKS,
                .8F,
                .9F)), DEFAULT_ANIMATION_DURATION, TimeUnit.SECONDS);
    }

    public static void playFailureAnimation(ServerWorld w, BlockPos p) {
        Vec3d d = p.toCenterPos();
        w.spawnParticles(ParticleTypes.SMOKE, d.getX(), d.getY(), d.getZ(), 50, 0, 0, 0, 0);
    }

    public static void sendNoDeliveryMessage(PlayerEntity e) {
        e.sendMessage(Text.translatable("message.samedaydelivery.nodelivery").formatted(Formatting.GRAY), true);
    }

    public static void runWithDelay(Runnable r, long delay, TimeUnit unit) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(r, delay, unit);
        scheduler.shutdown();
    }
}

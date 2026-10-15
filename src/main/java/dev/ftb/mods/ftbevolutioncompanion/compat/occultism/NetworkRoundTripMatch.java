package dev.ftb.mods.ftbevolutioncompanion.compat.occultism;

import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public final class NetworkRoundTripMatch {
    private static final ThreadLocal<RegistryAccess> ACTIVE = new ThreadLocal<>();

    private NetworkRoundTripMatch() {}

    public static void begin(RegistryAccess registries) {
        ACTIVE.set(registries);
    }

    public static void end() {
        ACTIVE.remove();
    }

    public static boolean matchesAfterRoundTrip(ItemStack requested, ItemStack stored) {
        RegistryAccess registries = ACTIVE.get();
        if (registries == null || requested.isEmpty() || stored.isEmpty() || !requested.is(stored.getItem())) {
            return false;
        }
        RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), registries);
        try {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, stored);
            ItemStack synced = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            return ItemStack.isSameItemSameComponents(requested, synced);
        } catch (RuntimeException e) {
            return false;
        } finally {
            buf.release();
        }
    }
}

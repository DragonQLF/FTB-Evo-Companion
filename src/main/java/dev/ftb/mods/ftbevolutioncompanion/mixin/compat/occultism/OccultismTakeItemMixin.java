package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.occultism;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.ftb.mods.ftbevolutioncompanion.compat.occultism.NetworkRoundTripMatch;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(targets = "com.klikli_dev.occultism.network.messages.MessageTakeItem", remap = false)
public abstract class OccultismTakeItemMixin {
    @WrapMethod(method = "onServerReceived")
    private void ftbevo$matchSyncedStacks(MinecraftServer server, ServerPlayer player, Operation<Void> original) {
        NetworkRoundTripMatch.begin(player.registryAccess());
        try {
            original.call(server, player);
        } finally {
            NetworkRoundTripMatch.end();
        }
    }
}

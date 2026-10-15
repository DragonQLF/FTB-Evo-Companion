package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.occultism;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.ftb.mods.ftbevolutioncompanion.compat.occultism.NetworkRoundTripMatch;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "com.klikli_dev.occultism.common.misc.ItemStackComparator", remap = false)
public abstract class OccultismComparatorRoundTripMixin {
    @WrapOperation(
            method = "matches",
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lnet/minecraft/world/item/ItemStack;isSameItemSameComponents(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    private boolean ftbevo$matchSyncedCopy(ItemStack requested, ItemStack stored, Operation<Boolean> original) {
        return original.call(requested, stored) || NetworkRoundTripMatch.matchesAfterRoundTrip(requested, stored);
    }
}

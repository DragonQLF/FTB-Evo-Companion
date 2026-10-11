package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.colossalreactors;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.unfamily.colossal_reactors.menu.ResourcePortMenu", remap = false)
public abstract class ColossalReactorsTurbinePortQuickMoveMixin {
    @Shadow
    public abstract boolean showItemSlot();

    @Inject(
            method = "quickMoveStack(Lnet/minecraft/world/entity/player/Player;I)Lnet/minecraft/world/item/ItemStack;",
            at = @At("HEAD"),
            cancellable = true)
    private void ftbevo$noQuickMoveWithoutPortSlot(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        if (!showItemSlot()) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}

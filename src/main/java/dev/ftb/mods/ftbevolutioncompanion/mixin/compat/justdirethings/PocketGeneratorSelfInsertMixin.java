package dev.ftb.mods.ftbevolutioncompanion.mixin.compat.justdirethings;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.direwolf20.justdirethings.common.containers.PocketGeneratorContainer", remap = false)
public abstract class PocketGeneratorSelfInsertMixin {
    @Shadow
    public ItemStack pocketGeneratorItemStack;

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void ftbevo$keepOpenGenerator(Player player, int index, CallbackInfoReturnable<ItemStack> cir) {
        AbstractContainerMenu menu = (AbstractContainerMenu) (Object) this;
        if (index >= 0 && index < menu.slots.size() && menu.slots.get(index).getItem() == pocketGeneratorItemStack) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
}
